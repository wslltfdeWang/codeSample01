package com.foreveross.vds.service.fms.service.impl;

import com.foreveross.vds.client.client.VdsDefaultClient;
import com.foreveross.vds.dto.fms.FmsTransportSettledLine;
import com.foreveross.vds.dto.fms.SendcarExportDto;
import com.foreveross.vds.dto.fms.SendcarGenerateRequest;
import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.fms.SendcarGenerateSubmit;
import com.foreveross.vds.dto.inter.ResponsInter;
import com.foreveross.vds.dto.inter.sendcar.CavdsSendcarsI;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.mapper.pms.PmsAllotCarrierExtendMapper;
import com.foreveross.vds.service.common.service.*;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.common.util.ConstantUtil;
import com.foreveross.vds.service.fms.mapper.SendcarGenerateVMapper;
import com.foreveross.vds.service.fms.service.FmsCreateSettleTransportChargeService;
import com.foreveross.vds.service.fms.service.FmsSendcarFeeRelateService;
import com.foreveross.vds.service.fms.service.SendcarGenerateVService;
import com.foreveross.vds.util.tools.StringHelper;
import com.foreveross.vds.vo.common.*;
import com.foreveross.vds.vo.fms.FmsSendcarFeeRelate;
import com.foreveross.vds.vo.fms.SendcarGenerateV;
import com.foreveross.vds.vo.inv.InvDriverInfo;
import com.foreveross.vds.vo.inv.InvVinInfo;
import com.foreveross.vds.vo.invload.InvPreparecarPlan;
import com.foreveross.vds.vo.pms.PmsAllotCarrier;
import com.foreveross.vds.vo.tms.TmsTransTool;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SendcarGenerateVServiceImpl extends BaseServiceImpl<SendcarGenerateV, Long> implements SendcarGenerateVService {

    @Autowired
    private SendcarGenerateVMapper sendcarGenerateVMapper;

    @Autowired
    private PmsAllotCarrierExtendMapper pmsAllotCarrierExtendMapper;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private SendcarHeaderService sendcarHeaderService;

    @Autowired
    private SendcarLineService sendcarLineService;

    @Autowired
    private CodeService codeService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private InvVinInfoService invVinInfoService;

    @Autowired
    private DriverInfoService driverInfoService;

    @Autowired
    private FmsSendcarFeeRelateService fmsSendcarFeeRelateService;

    @Autowired
    private TransToolService transToolService;

    @Autowired
    private ExportTranslatorService exportTranslatorService;

    @Autowired
    private FmsCreateSettleTransportChargeService fmsCreateSettleTransportChargeService;

    @Override
    public List<SendcarGenerateV> getSameLoadListDetail(Long vinId, List<Long> organizationIds) throws BusinessServiceException {
        List<SendcarGenerateRequest> sendcarGenerateRequestList = sendcarGenerateVMapper.selectSameLoadListDetail(vinId, organizationIds);
        if(sendcarGenerateRequestList.size() == 0){
            throw new BusinessServiceException("没有找到所选车辆的装车单，请检查数据！");
        }
        List<SendcarGenerateV> sendcarGenerateVList = new ArrayList<>();
        for(SendcarGenerateRequest sendcarGenerateRequest : sendcarGenerateRequestList){
            sendcarGenerateRequest.setStartOrganizationId(organizationIds);//设置查询当前库存组织
            List<SendcarGenerateV> sendcarGenerateVS = sendcarGenerateVMapper.queryList(sendcarGenerateRequest);
            sendcarGenerateVList.addAll(sendcarGenerateVS);
        }
        return sendcarGenerateVList;
    }



    @Override
    @CustTx
    public List<Long> doGenerateSendcar(SendcarGenerateSubmit sendcarGenerateSubmit, Long userId, String sessionId) throws BusinessServiceException {
        //检查参数
        checkParam(sendcarGenerateSubmit);

        List<SendcarGenerateV> sendcarGenerateVList = this.getData(sendcarGenerateSubmit.getAllotCarrierIds());

        //分组：
        //  1、同一个订单组合为一组,一组则为一个送车交接单,同一组最多30条数据
        //  2、如果为联运第二段，则联运第一段在同一个送车交接单的为一组
        Map<String, List<SendcarGenerateV>> SendcarGenerateVGroups = this.groupData(sendcarGenerateVList);

        //验证：如果当前为联运第二段，则不能将第一段不在一个送车交接单的数据组合到一个新的送车交接单上
        //this.validDataGroups(SendcarGenerateVGroups);

        //保存：保存送车交接单头表和行表
        List<Long> sendcarHeaderIdList = this.saveData(SendcarGenerateVGroups, sendcarGenerateSubmit, userId, sessionId);

        //更新：更新分配承运商信息表的送车交接单生成状态
        this.updateGenSendcarFlag(sendcarGenerateSubmit.getAllotCarrierIds(), userId, sessionId);

        //修改订单活动 = “生成送车交接单”
        this.updateOrderAction(sendcarGenerateVList, userId, sessionId);

        //修改商品车活动 = “生成送车交接单”
        this.updateVinInfoAction(sendcarGenerateVList, userId, sessionId);

        return sendcarHeaderIdList;
    }

    @CustTx
    public List<Long> doGenerateSendcar(List<SendcarGenerateSubmit> sendcarGenerateSubmitList, Long userId, String sessionId) throws BusinessServiceException {
        List<Long> sendcarHeaderIdList = new ArrayList<>();
        for(SendcarGenerateSubmit sendcarGenerateSubmit : sendcarGenerateSubmitList){
            List<Long> sendcarHeaderIds = doGenerateSendcar(sendcarGenerateSubmit, userId, sessionId);
            sendcarHeaderIdList.addAll(sendcarHeaderIds);
        }
        return sendcarHeaderIdList;
    }

    @Override
    public void sendInter(List<Long> sendcarHeaderIdList) {
        new Thread(() -> {
            try {
                String url = ConstantUtil.INTER_URL + "/cavds_sendcar_inter";

                List<CavdsSendcarsI> cavdsSendcarsIList = getParams(sendcarHeaderIdList);
                VdsDefaultClient vdsDefaultClient = new VdsDefaultClient(url, cavdsSendcarsIList);
                vdsDefaultClient.invokeWithSign(ResponsInter.class);
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }).start();
    }

    @Override
    public InvDriverInfo getDriverInfo(Long transToolId) {
        return sendcarGenerateVMapper.getDriverInfo(transToolId);
    }

    @Override
    public List<SendcarExportDto> queryExport(SendcarGenerateRequest sendcarGenerateRequest) {
        return sendcarGenerateVMapper.queryExport(sendcarGenerateRequest);
    }

    @Override
    public void translate(List<SendcarExportDto> list) {
        for(SendcarExportDto sendcarExportDto : list){
            //省
            sendcarExportDto.setEndProvinceIdDesc(exportTranslatorService.transReginInfo(sendcarExportDto.getEndProvinceId()));
            //市
            sendcarExportDto.setEndCityIdDesc(exportTranslatorService.transReginInfo(sendcarExportDto.getEndCityId()));
            //县
            sendcarExportDto.setEndCountyIdDesc(exportTranslatorService.transReginInfo(sendcarExportDto.getEndCountyId()));
        }
    }

    @Override
    @CustTx
    public List<Long> importPreparecarPlan(Long userId, String sessionId, List<Long> organizationIdList, Workbook workbook) throws BusinessServiceException {
        List<SendcarGenerateSubmit> sendcarGenerateSubmitList = parseWorkbook(workbook, organizationIdList);
        List<Long> sendcarHeaderIdList = doGenerateSendcar(sendcarGenerateSubmitList, userId, sessionId);
        return sendcarHeaderIdList;
    }

    @Override
    public void checkParam(SendcarGenerateSubmit sendcarGenerateSubmit) throws BusinessServiceException {
        checkAllotCarrierIds(sendcarGenerateSubmit);
    }

    @Override
    public void relateData(List<Long> sendcarHeaderIdList, Long userId, String sessionId) {
        for(Long sendcarHeaderId : sendcarHeaderIdList){
            relateData(sendcarHeaderId, userId, sessionId);
        }
    }

    public void relateData(Long sendcarHeaderId, Long userId, String sessionId){
        try{
            FmsTransportSettledLine fmsTransportSettledLine = new FmsTransportSettledLine();
            fmsTransportSettledLine.setSendcarHeaderId(sendcarHeaderId);
            fmsTransportSettledLine.setCreatedBy(userId);
            fmsTransportSettledLine.setLastUpdatedBy(userId);
            fmsTransportSettledLine.setLastUpdateLogin(sessionId);
            fmsCreateSettleTransportChargeService.relateData(fmsTransportSettledLine);
        }catch (Exception e){

        }
    }

    private void checkAllotCarrierIds(SendcarGenerateSubmit sendcarGenerateSubmit) throws BusinessServiceException {
        List<Long> allotCarrierIds = sendcarGenerateSubmit.getAllotCarrierIds();

        if(allotCarrierIds == null || allotCarrierIds.size() == 0){
            throw new BusinessServiceException("数据异常！请联系管理员");
        }
    }

    public void checkWorkBook(Workbook workbook) throws BusinessServiceException {
        Map<String, Integer> orderNumberMap = new HashMap<>();
        Map<String, Integer> transToolCodeMap = new HashMap<>();

        String preOrderNumber = "";
        String preTransToolCode = "";
        String preTel = "";
        Sheet sheet = workbook.getSheetAt(0);
        for(int i = 1; i < sheet.getPhysicalNumberOfRows(); i++){
            Row row = sheet.getRow(i);

            if (row == null) {
            	break;
            }

            Cell cell = row.getCell(0);
            if(cell == null){
                break;
            }

            cell.setCellType(CellType.STRING);
            if(StringHelper.isEmpty(cell.getStringCellValue())){
                break;
            }



            //同一订单内条码不能间隔排列
            cell = row.getCell(1);
            if(cell == null){
                throw new BusinessServiceException("行["+i+"]：订单号不可为空！");
            }
            cell.setCellType(CellType.STRING);
            String orderNumber = cell.getStringCellValue();
            if(StringHelper.isEmpty(orderNumber)){
                throw new BusinessServiceException("行["+i+"]：订单号不可为空！");
            }
            if(orderNumberMap.containsKey(orderNumber) && orderNumber.equals(preOrderNumber)){
                Integer a = orderNumberMap.get(orderNumber);
                orderNumberMap.put(orderNumber, ++a);
            }else if(orderNumberMap.containsKey(orderNumber) && !orderNumber.equals(preOrderNumber)){
                throw new BusinessServiceException("行["+i+"]：同一订单内条码不能间隔排列！");
            }else if(!orderNumberMap.containsKey(orderNumber)){
                orderNumberMap.put(orderNumber, 1);
            }





            //同一车厢号不能间隔排列
            cell = row.getCell(10);
            if(cell == null){
                throw new BusinessServiceException("行["+i+"]：车厢号不可为空！");
            }
            cell.setCellType(CellType.STRING);
            String transToolCode = cell.getStringCellValue();
            if(StringHelper.isEmpty(transToolCode)){
                throw new BusinessServiceException("行["+i+"]：车厢号不可为空！");
            }
            if(transToolCodeMap.containsKey(transToolCode) && transToolCode.equals(preTransToolCode)){
                Integer a = transToolCodeMap.get(transToolCode);
                transToolCodeMap.put(transToolCode, ++a);
            }else if(transToolCodeMap.containsKey(transToolCode) && !transToolCode.equals(preTransToolCode)){
                throw new BusinessServiceException("行["+i+"]：同一车厢号不能间隔排列！");
            }else if(!transToolCodeMap.containsKey(transToolCode)){
                transToolCodeMap.put(transToolCode, 1);
            }





            //驾驶员电话
            cell = row.getCell(9);
            if(cell == null){
                throw new BusinessServiceException("行["+i+"]：驾驶员电话不可为空！");
            }
            cell.setCellType(CellType.STRING);
            String tel = cell.getStringCellValue();
            if(StringHelper.isEmpty(tel)){
                throw new BusinessServiceException("行["+i+"]：驾驶员电话不可为空！");
            }
            if(i != 1 && transToolCode.equals(preTransToolCode) && !tel.equals(preTel)) {
                throw new BusinessServiceException("行[" + i + "]：同一车厢号只能使用同一驾驶员电话！");
            }


            preOrderNumber = orderNumber;
            preTransToolCode = transToolCode;
            preTel = tel;
        }

    }

    private List<SendcarGenerateSubmit> parseWorkbook(Workbook workbook, List<Long> organizationIdList) throws BusinessServiceException {
        Map<Long, SendcarGenerateSubmit> sendcarGenerateSubmitMap = new HashMap<>();

        Sheet sheet = workbook.getSheetAt(0);
        for(int i = 1; i < sheet.getPhysicalNumberOfRows(); i++){
            Row row = sheet.getRow(i);

            if (row == null) {
            	break;
            }
            
            Cell cell = row.getCell(0);
            if(cell == null || StringHelper.isEmpty(cell.getStringCellValue())){
                break;
            }
            cell.setCellType(CellType.STRING);

            //VIN码
            cell = row.getCell(2);
            if(cell == null){
                throw new BusinessServiceException("行[" + i + "]：VIN码不可为空！");
            }
            cell.setCellType(CellType.STRING);
            String vinCode = cell.getStringCellValue();
            if(StringHelper.isEmpty(vinCode)){
                throw new BusinessServiceException("行[" + i + "]：VIN码不可为空！");
            }
            InvVinInfo invVinInfo = invVinInfoService.queryByVinCode(vinCode);
            if(invVinInfo == null){
                throw new BusinessServiceException("行[" + i + "]：VIN码["+vinCode+"]不正确！");
            }

            checkLoadScanOrOutScan(vinCode, organizationIdList, i);//检查是否装车或是否出库

            PmsAllotCarrier pmsAllotCarrier = pmsAllotCarrierExtendMapper.getBy(vinCode, organizationIdList);
            if(pmsAllotCarrier == null){
                throw new BusinessServiceException("行[" + i + "]：VIN码["+vinCode+"]没有分配承运商！");
            }


            //驾驶员电话
            cell = row.getCell(9);
            cell.setCellType(CellType.STRING);
            String tel = cell.getStringCellValue();
            DriverInfo driverInfo = driverInfoService.getBy(tel);
            if(driverInfo == null){
                throw new BusinessServiceException("行[" + i + "]：驾驶员电话["+tel+"]不正确！");
            }

            //车厢号
            cell = row.getCell(10);
            cell.setCellType(CellType.STRING);
            String transToolCode = cell.getStringCellValue();
            TransTool transTool = transToolService.selectBy(transToolCode);
            if(transTool == null){
                throw new BusinessServiceException("行[" + i + "]：车厢号["+transToolCode+"]不正确！");
            }


            //合并数据
            if(sendcarGenerateSubmitMap.containsKey(transTool.getTransToolId())){
                SendcarGenerateSubmit sendcarGenerateSubmit = sendcarGenerateSubmitMap.get(transTool.getTransToolId());
                sendcarGenerateSubmit.getAllotCarrierIds().add(pmsAllotCarrier.getAllotCarrierId());
            }else{
                SendcarGenerateSubmit sendcarGenerateSubmit = new SendcarGenerateSubmit();
                List<Long> allotCarrierIds = new ArrayList<>();
                allotCarrierIds.add(pmsAllotCarrier.getAllotCarrierId());
                sendcarGenerateSubmit.setAllotCarrierIds(allotCarrierIds);
                sendcarGenerateSubmit.setDriverId(driverInfo.getDriverId());
                sendcarGenerateSubmit.setTransToolId(transTool.getTransToolId());
                sendcarGenerateSubmitMap.put(transTool.getTransToolId(), sendcarGenerateSubmit);
            }
        }

        //检测运输工具状态、最大装载量
        checkTransTool(sendcarGenerateSubmitMap);

        List<SendcarGenerateSubmit> sendcarGenerateSubmitList = new ArrayList<>();
        for(SendcarGenerateSubmit sendcarGenerateSubmit : sendcarGenerateSubmitMap.values()){
            sendcarGenerateSubmitList.add(sendcarGenerateSubmit);
        }
        return sendcarGenerateSubmitList;
    }

    private void checkTransTool(Map<Long, SendcarGenerateSubmit> sendcarGenerateSubmitMap) throws BusinessServiceException {
        for(Map.Entry<Long, SendcarGenerateSubmit> entry : sendcarGenerateSubmitMap.entrySet()){
            checkTransTool(entry);
        }
    }

    private void checkTransTool(Map.Entry<Long, SendcarGenerateSubmit> entry) throws BusinessServiceException {
        TransTool transTool = transToolService.selectByPrimaryKey(entry.getKey());
        List<Long> transportMethods = pmsAllotCarrierExtendMapper.getTransportMethodBy(entry.getValue().getAllotCarrierIds().get(0));
        if(transportMethods.size() == 0){
            return;
        }
        Long transportMethod = transportMethods.get(0);

        checkTransToolStatus(transTool, transportMethod);
        checkTransportCapacity(transTool, transportMethod, entry.getValue().getAllotCarrierIds().size());
    }

    private void checkTransToolStatus(TransTool transTool,Long transportMethod) throws BusinessServiceException {
        Long waterway = lookupService.getLookupId("TRANSPORT_METHOD","WATERWAY");
        Long railway = lookupService.getLookupId("TRANSPORT_METHOD","RAILWAY");


        if (transportMethod.longValue() == waterway.longValue() || transportMethod.longValue() == railway.longValue()) {
            return;
        }

        Long empty = lookupService.getLookupId("TRANS_TOOL_STATUS", "empty");
        if(transTool.getTransToolStatus().longValue() != empty.longValue()){
            throw new BusinessServiceException("车厢号["+transTool.getTransToolCode()+"]不是空闲状态！");
        }
    }

    private void checkTransportCapacity(TransTool transTool, Long transportMethod, int actualTransportCapacity) throws BusinessServiceException {
        Long railway = lookupService.getLookupId("TRANSPORT_METHOD","RAILWAY");

        if(transportMethod.longValue() == railway.longValue()){
            return;
        }

        Long transportCapacity = transTool.getTransportCapacity();
        if(actualTransportCapacity > transportCapacity.longValue()){
            throw new BusinessServiceException("实际装载量["+actualTransportCapacity+"]超过车厢号["+transTool.getTransToolCode()+"]最大装载量["+transportCapacity+"]！");
        }
    }

    private void checkLoadScanOrOutScan(String vinCode, List<Long> organizationIdList, int i) throws BusinessServiceException {
        InvPreparecarPlan invPreparecarPlan = sendcarGenerateVMapper.queryPreparecarPlanBy(vinCode, organizationIdList);
        Long prepareLocationId = invPreparecarPlan.getPrepareLocationId();
        Long storageLocationType = sendcarGenerateVMapper.getStorageLocationType(prepareLocationId);

        Long preparearea = lookupService.getLookupId("STORAGE_ROOM_TYPE", "PREPARE_AREA");
        Long manualWaitingArea = lookupService.getLookupId("STORAGE_ROOM_TYPE", "MANUAL_WAITING_AREA");

        if(Objects.equals(storageLocationType, preparearea)){
            int count = sendcarGenerateVMapper.countLoadScan(vinCode, organizationIdList);
            if(count == 0){
                throw new BusinessServiceException("行[" + i + "]：VIN码["+vinCode+"]未装车！");
            }
        }else if(Objects.equals(storageLocationType, manualWaitingArea) && !"Y".equals(invPreparecarPlan.getOutScanFlag())){
            throw new BusinessServiceException("行[" + i + "]：VIN码["+vinCode+"]未出库！");
        }

    }

    private List<CavdsSendcarsI> getParams(List<Long> sendcarHeaderIdList) {
        List<CavdsSendcarsI> cavdsSendcarsIList = new ArrayList<>(sendcarHeaderIdList.size());
        for(Long sendcarHeaderId : sendcarHeaderIdList){
            CavdsSendcarsI cavdsSendcarsI = new CavdsSendcarsI();
            cavdsSendcarsI.setSendcarHeaderId(sendcarHeaderId);
            cavdsSendcarsIList.add(cavdsSendcarsI);
        }
        return cavdsSendcarsIList;
    }

    private void updateVinInfoAction(List<SendcarGenerateV> sendcarGenerateVList, Long userId, String sessionId) {
        InvVinInfo invVinInfo = new InvVinInfo();
        invVinInfo.setLastUpdatedBy(userId);
        invVinInfo.setLastUpdateDate(new Date());
        invVinInfo.setLastUpdateLogin(sessionId);

        for(SendcarGenerateV sendcarGenerateV : sendcarGenerateVList){
            invVinInfo.setVinId(sendcarGenerateV.getVinId());
            invVinInfoService.updateVinInfoBy(invVinInfo, "send_car_gen");
        }

    }

    private void updateOrderAction(List<SendcarGenerateV> sendcarGenerateVList, Long userId, String sessionId) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setLastUpdatedBy(userId);
        orderDetail.setLastUpdateDate(new Date());
        orderDetail.setLastUpdateLogin(sessionId);
        for(SendcarGenerateV sendcarGenerateV : sendcarGenerateVList){
            orderDetail.setOrderDetailId(sendcarGenerateV.getOrderDetailId());
            orderDetailService.updateStatusAndActionCode(orderDetail, OrderDetailActionRecord.StatusResourceSys.FMS.name(),"sendcar_create");
        }
    }

    private Map<String, List<SendcarGenerateV>> groupData(List<SendcarGenerateV> sendcarGenerateVList) {
        //按照订单号分组，每个组最多30条数据，每组是一个送车交接单
        Map<String, List<SendcarGenerateV>> dataGroups1 = groupByOrdernumber(sendcarGenerateVList);

        //如果是联运第二段，将联运第一段在同一个送车交接单的分为一组，每组是一个送车交接单
        Map<String, List<SendcarGenerateV>> dataGroups2 = groupByFirstSectionOrder(dataGroups1);

        return dataGroups2;
    }

    private Map<String, List<SendcarGenerateV>> groupByFirstSectionOrder(Map<String, List<SendcarGenerateV>> dataGroups) {
        Map<String, List<SendcarGenerateV>> result = new HashMap<>();
        for(Map.Entry entry : dataGroups.entrySet()){
            String key = (String) entry.getKey();
            List<SendcarGenerateV> value = (List<SendcarGenerateV>) entry.getValue();
            Map<String, List<SendcarGenerateV>> rel = groupByFirstSectionOrder(key, value);
            result.putAll(rel);
        }

        return result;
    }

    private Map<String, List<SendcarGenerateV>> groupByFirstSectionOrder(String key, List<SendcarGenerateV> sendcarGenerateVList) {
        Map<String, List<SendcarGenerateV>> result = new HashMap<>();

        //如果只有一条数据，则不需要分组
        if(sendcarGenerateVList.size() == 1){
            result.put(key, sendcarGenerateVList);
            return result;
        }

        Long curSectionOrder = sendcarGenerateVList.get(0).getSectionOrder();//获取当前组的段次
        if(curSectionOrder == 1L){//如果当前是第一段，则不需要分组
            result.put(key, sendcarGenerateVList);
            return result;
        }


        for(SendcarGenerateV sendcarGenerateV : sendcarGenerateVList){
            Long preSectionOrder = sendcarGenerateV.getSectionOrder() - 1;
            Long sendcarHeaderId = sendcarGenerateVMapper.getSendcarHeaderIdBy(preSectionOrder, sendcarGenerateV.getVinCode());
            String k = key + "-" + sendcarHeaderId;

            if(result.containsKey(k)){
                List<SendcarGenerateV> v = result.get(k);
                v.add(sendcarGenerateV);
            }else{
                List<SendcarGenerateV> v = new ArrayList<>();
                v.add(sendcarGenerateV);
                result.put(k, v);
            }

        }

        return result;
    }

    private Map<String, List<SendcarGenerateV>> groupByOrdernumberAndMax30EachGroup(List<SendcarGenerateV> sendcarGenerateVList) {
        Map<String, List<SendcarGenerateV>> dataGroups = new HashMap<>();

        for(SendcarGenerateV sendcarGenerateV : sendcarGenerateVList){
            String orderNumber = sendcarGenerateV.getOrderNumber();

            int groupNum = 1;
            while (true){
                String orderNumberKey = orderNumber + "-" + groupNum;
                if(!dataGroups.containsKey(orderNumberKey)){
                    List<SendcarGenerateV> sendcarGenerateGroup = new ArrayList<>();
                    sendcarGenerateGroup.add(sendcarGenerateV);
                    dataGroups.put(orderNumberKey, sendcarGenerateGroup);
                    break;
                }else if(dataGroups.get(orderNumberKey).size() < 30){
                    List<SendcarGenerateV> sendcarGenerateGroup = dataGroups.get(orderNumberKey);
                    sendcarGenerateGroup.add(sendcarGenerateV);
                    break;
                }else if(dataGroups.get(orderNumberKey).size() >= 30) {
                    groupNum++;
                }
            }
        }
        return dataGroups;
    }

    private Map<String, List<SendcarGenerateV>> groupByOrdernumber(List<SendcarGenerateV> sendcarGenerateVList) {
        Map<String, List<SendcarGenerateV>> dataGroups = new HashMap<>();

        for(SendcarGenerateV sendcarGenerateV : sendcarGenerateVList){
            String orderNumber = sendcarGenerateV.getOrderNumber();

            if(!dataGroups.containsKey(orderNumber)){
                List<SendcarGenerateV> sendcarGenerateGroup = new ArrayList<>();
                sendcarGenerateGroup.add(sendcarGenerateV);
                dataGroups.put(orderNumber, sendcarGenerateGroup);
            }else{
                List<SendcarGenerateV> sendcarGenerateGroup = dataGroups.get(orderNumber);
                sendcarGenerateGroup.add(sendcarGenerateV);
            }
        }
        return dataGroups;
    }

    private void validDataGroups(Map<String, List<SendcarGenerateV>> sendcarGenerateVGroups) throws BusinessServiceException {
        for(List<SendcarGenerateV> sendcarGenerateVGroup : sendcarGenerateVGroups.values()){
            this.validDataGroup(sendcarGenerateVGroup);
        }
    }

    private void validDataGroup(List<SendcarGenerateV> sendcarGenerateVGroup) throws BusinessServiceException {
        if(sendcarGenerateVGroup.size() == 1){//如果只有一条数据，则不需要验证
            return;
        }

        Long curSectionOrder = sendcarGenerateVGroup.get(0).getSectionOrder();//获取当前组的段次
        if(curSectionOrder == 1L){//如果当前是第一段，则不需要此验证
            return;
        }
        Long preSectionOrder = curSectionOrder - 1;
        List<Long> vinIdList = new ArrayList<>();
        for(SendcarGenerateV sendcarGenerateV : sendcarGenerateVGroup){
            vinIdList.add(sendcarGenerateV.getVinId());
        }

        if(sendcarGenerateVMapper.countSendcarHeaderId(preSectionOrder, vinIdList) > 1){
            throw new BusinessServiceException("存在车辆的前一段数据不在同一个送车交接单中！请重新选择数据！");
        }

    }

    private List<Long> saveData(Map<String, List<SendcarGenerateV>> sendcarGenerateVGroups, SendcarGenerateSubmit sendcarGenerateSubmit, Long userId, String sessionId) throws BusinessServiceException {
        List<Long> sendcarHeaderIdList = new ArrayList<>();
        for(List<SendcarGenerateV> sendcarGenerateVList : sendcarGenerateVGroups.values()){
            Long sendcarHeaderId = saveSendcar(sendcarGenerateVList, sendcarGenerateSubmit, userId, sessionId);
            sendcarHeaderIdList.add(sendcarHeaderId);
        }
        return sendcarHeaderIdList;
    }

    private Long saveSendcar(List<SendcarGenerateV> sendcarGenerateVList, SendcarGenerateSubmit sendcarGenerateSubmit, Long userId, String sessionId) throws BusinessServiceException {
        Long sendcarHeaderId = saveSendcarHeader(sendcarGenerateVList, sendcarGenerateSubmit, userId, sessionId);
        List<Long> sencarLineIds = saveSendcarLine(sendcarGenerateVList, userId, sessionId, sendcarHeaderId);
        saveFmsSendcarFeeRelate(sencarLineIds, userId, sessionId);
        return sendcarHeaderId;
    }

    private void saveFmsSendcarFeeRelate(List<Long> sencarLineIds, Long userId, String sessionId) {
        for(Long sencarLineId : sencarLineIds){
            saveFmsSendcarFeeRelate(sencarLineId, userId, sessionId);
        }
    }


    private void saveFmsSendcarFeeRelate(Long sencarLineId, Long userId, String sessionId){
        Date now = new Date();

        FmsSendcarFeeRelate fmsSendcarFeeRelate = new FmsSendcarFeeRelate();
        fmsSendcarFeeRelate.setSendcarLineId(sencarLineId);

        fmsSendcarFeeRelate.setCreatedBy(userId);
        fmsSendcarFeeRelate.setCreationDate(now);
        fmsSendcarFeeRelate.setLastUpdatedBy(userId);
        fmsSendcarFeeRelate.setLastUpdateDate(now);
        fmsSendcarFeeRelate.setLastUpdateLogin(sessionId);

        fmsSendcarFeeRelateService.insertSelective(fmsSendcarFeeRelate);
    }

    private void updateTransToolStatus(TmsTransTool tmsTransTool, Long userId, String sessionId) throws BusinessServiceException {
        Long transToolId = tmsTransTool.getTransToolId();
        Long loadingMode = tmsTransTool.getLoadingMode();
        if(loadingMode == lookupService.getLookupId("LOADING_MODE", "INDIRECT")){//倒板
            //改空闲
            transToolService.updateTransToolStatus(transToolId, "empty", userId, sessionId);
        }else if(loadingMode == lookupService.getLookupId("LOADING_MODE", "DIRECT")){//直运
            //改在途
            transToolService.updateTransToolStatus(transToolId, "transporting", userId, sessionId);
        }else {
            throw new BusinessServiceException("装车模式不正确，修改板车状态失败！请检查数据");
        }
    }

    private Long saveSendcarHeader(List<SendcarGenerateV> sendcarGenerateVList, SendcarGenerateSubmit sendcarGenerateSubmit, Long userId, String sessionId) throws BusinessServiceException {
        //获取运输工具
        TmsTransTool tmsTransTool = new TmsTransTool();
        if(sendcarGenerateSubmit.getTransToolId() != null){
            tmsTransTool = sendcarGenerateVMapper.getTransTool(sendcarGenerateSubmit.getTransToolId());
            //修改运输工具状态
            updateTransToolStatus(tmsTransTool, userId, sessionId);
        }

        //获取司机信息
        DriverInfo driverInfo = new DriverInfo();
        if(sendcarGenerateSubmit.getDriverId() != null){
            driverInfo = driverInfoService.selectByPrimaryKey(sendcarGenerateSubmit.getDriverId());
        }


        SendcarGenerateV sendcarGenerateV = sendcarGenerateVList.get(0);//默认取第一条数据，当订单相同、段次相同，默认以下这些数据都相同
        Date now = new Date();
        String sendcarOrderNumber = codeService.getSendcarOrderNumber(
                sendcarGenerateV.getOrgId(),
                sendcarGenerateV.getOrderType(),
                sendcarGenerateV.getTransportModel(),
                sendcarGenerateV.getSectionOrder()
        );


        SendcarHeader sendcarHeader = new SendcarHeader();
        sendcarHeader.setOrgId(sendcarGenerateV.getOrgId());
        sendcarHeader.setOrganizationId(sendcarGenerateV.getStartOrganizationId());
        sendcarHeader.setOrderHeaderId(sendcarGenerateV.getOrderHeaderId());
        sendcarHeader.setTransportRuleLineId(sendcarGenerateV.getTransportRuleLineId());
        sendcarHeader.setTransportRuleHeaderId(sendcarGenerateV.getTransportRuleHeaderId());
        sendcarHeader.setTransportMethodId(sendcarGenerateV.getTransportMethod());
        sendcarHeader.setSendcarOrderNumber(sendcarOrderNumber);
        sendcarHeader.setLogisticsId(sendcarGenerateV.getLogisticsId());
        sendcarHeader.setCarrierId(sendcarGenerateV.getCarrierId());
        sendcarHeader.setOrderType(sendcarGenerateV.getOrderType());
        sendcarHeader.setTransportModel(sendcarGenerateV.getTransportModel());
        sendcarHeader.setSectionTotal(sendcarGenerateV.getSectionTotal());
        sendcarHeader.setMidOrganizationId(getMidOrganizationId(sendcarGenerateV));
        sendcarHeader.setSectionOrder(sendcarGenerateV.getSectionOrder());
        sendcarHeader.setTransToolId(tmsTransTool.getTransToolId());
        sendcarHeader.setDriverId(driverInfo.getDriverId());
        sendcarHeader.setMotorman(driverInfo.getDriverName());
        sendcarHeader.setMotormanPhone(String.valueOf(driverInfo.getTel()));
        sendcarHeader.setStartPointId(sendcarGenerateV.getStartPointId());
        sendcarHeader.setStartProvinceId(sendcarGenerateV.getStartProvinceId());
        sendcarHeader.setStartCityId(sendcarGenerateV.getStartCityId());
        sendcarHeader.setStartCountyId(sendcarGenerateV.getStartCountyId());
        sendcarHeader.setEndProvinceId(sendcarGenerateV.getEndProvinceId());
        sendcarHeader.setEndCityId(sendcarGenerateV.getEndCityId());
        sendcarHeader.setEndCountyId(sendcarGenerateV.getEndCountyId());
        sendcarHeader.setCarQuantity(Long.valueOf(String.valueOf(sendcarGenerateVList.size())));
        sendcarHeader.setArrivedStatus(lookupService.getLookupId("ARRIVED_STATUS", "UN_ARRIVE"));
        sendcarHeader.setReceiveStatus(lookupService.getLookupId("RECEIVE_STATUS", "UN_RECEIVED"));
        sendcarHeader.setPrintFlag("N");
        sendcarHeader.setSettledFlag("N");
        sendcarHeader.setEnabledFlag("Y");
        sendcarHeader.setShipTo(sendcarGenerateV.getSoldTo());
        sendcarHeader.setShipToAddress(sendcarGenerateV.getReceiveAddress());
        sendcarHeader.setJjdPrintTime(0L);
        sendcarHeader.setSendcarDate(now);
        sendcarHeader.setBillTo(sendcarGenerateV.getInvoiceTo());
        sendcarHeader.setSoldToCompanyId(sendcarGenerateV.getSoldToOrgId());
        sendcarHeader.setManager(sendcarGenerateSubmit.getManager());
        sendcarHeader.setFlatcarId(tmsTransTool.getTransToolCode());
        sendcarHeader.setShipToAddressId(sendcarGenerateV.getShipToAddressId());
        sendcarHeader.setSettledQuantity(Long.valueOf(String.valueOf(sendcarGenerateVList.size())));

        sendcarHeader.setCreatedBy(userId);
        sendcarHeader.setCreationDate(now);
        sendcarHeader.setLastUpdatedBy(userId);
        sendcarHeader.setLastUpdateDate(now);
        sendcarHeader.setLastUpdateLogin(sessionId);

        sendcarHeaderService.insertSelective(sendcarHeader);
        return sendcarHeader.getSendcarHeaderId();
    }

    /**
     * 获取中转站库存组织ID
     * @desc 如果是移库订单 或 联运第一段 返回 当前段的终点库存组织，其他情况返回null
     * @param sendcarGenerateV
     * @return
     */
    private Long getMidOrganizationId(SendcarGenerateV sendcarGenerateV) {
        if(sendcarGenerateV.getOrderType() == lookupService.getLookupId("ORDER_TYPE","MOVE")){//如果是移库订单
            return sendcarGenerateV.getMidOrganizationId();//当前段的终点库存组织
        }

        if(sendcarGenerateV.getSectionTotal() != 1 && sendcarGenerateV.getSectionOrder() == 1){//如果是联运第一段
            return sendcarGenerateV.getMidOrganizationId();//当前段的终点库存组织
        }

        return null;
    }

    private List<Long> saveSendcarLine(List<SendcarGenerateV> sendcarGenerateVList, Long userId, String sessionId, Long sendcarHeaderId) throws BusinessServiceException {
        Date now = new Date();
        List<Long> sencarLineIds = new ArrayList();
        for(SendcarGenerateV sendcarGenerateV : sendcarGenerateVList){
            SendcarLine sendcarLine = new SendcarLine();
            sendcarLine.setParentId(getParentSendcarLineId(sendcarGenerateV.getVinId(), sendcarGenerateV.getSectionOrder()));
            sendcarLine.setSendcarHeaderId(sendcarHeaderId);
            sendcarLine.setOrderDetailId(sendcarGenerateV.getOrderDetailId());
            sendcarLine.setItemId(sendcarGenerateV.getItemId());
            sendcarLine.setItemCode(sendcarGenerateV.getItemCode());
            sendcarLine.setVinCode(sendcarGenerateV.getVinCode());
            sendcarLine.setCarCategoryId(sendcarGenerateV.getCarCategoryId());
            sendcarLine.setLotNumber(sendcarGenerateV.getLotNumber());
            sendcarLine.setArrivedFlag("N");
            sendcarLine.setReceiveFlag("N");
            sendcarLine.setCloseFlag("N");
            sendcarLine.setBarcode17(String.valueOf(sendcarGenerateV.getVinId()));
            sendcarLine.setCarType(sendcarGenerateV.getVehicleCode());
            sendcarLine.setColor(sendcarGenerateV.getColourCode());
            sendcarLine.setStatus(sendcarGenerateV.getStatusCode());

            sendcarLine.setCreatedBy(userId);
            sendcarLine.setCreationDate(now);
            sendcarLine.setLastUpdatedBy(userId);
            sendcarLine.setLastUpdateDate(now);
            sendcarLine.setLastUpdateLogin(sessionId);
            int i = sendcarLineService.insertSelective(sendcarLine);

            if(i != 1 || sendcarLine.getSencarLineId() == null){
                throw new BusinessServiceException("送车交接单行保存失败！");
            }

            sencarLineIds.add(sendcarLine.getSencarLineId());
        }

        return sencarLineIds;
    }

    private Long getParentSendcarLineId(Long vinId, Long sectionOrder) {
        SendcarLine parentSendcarLine = this.getParentSendcarLine(vinId, sectionOrder);
        return parentSendcarLine == null ? null : parentSendcarLine.getSencarLineId();
    }

    /**
     * 根据车辆vinId和当前段次找到该车辆前一段次的送车交接单行数据
     * @param vinId
     * @param sectionOrder
     * @return
     */
    private SendcarLine getParentSendcarLine(Long vinId, Long sectionOrder) {
        if(sectionOrder.longValue() == 1L){
            return null;
        }
        Long preSectionOrder  = sectionOrder - 1;
        return sendcarLineService.selectParentSendcarLine(vinId, preSectionOrder);
    }

    private void updateGenSendcarFlag(List<Long> allotCarrierIds, Long userId, String sessionId) throws BusinessServiceException {
        Date now = new Date();
        for(Long allotCarrierId : allotCarrierIds){
            PmsAllotCarrier pmsAllotCarrier = new PmsAllotCarrier();
            pmsAllotCarrier.setAllotCarrierId(allotCarrierId);
            pmsAllotCarrier.setGenSendcarFlag("Y");
            pmsAllotCarrier.setLastUpdatedBy(userId);
            pmsAllotCarrier.setLastUpdateDate(now);
            pmsAllotCarrier.setLastUpdateLogin(sessionId);
            int i = pmsAllotCarrierExtendMapper.updateGenSendcarFlag(pmsAllotCarrier);
            if(i == 0){
                throw new BusinessServiceException("allotCarrierId=["+allotCarrierId+"]已生成送车叫交接单！请检查数据");
            }
        }
    }

    private List<SendcarGenerateV> getData(List<Long> allotCarrierIds) {
        SendcarGenerateRequest sendcarGenerateRequest = new SendcarGenerateRequest();
        sendcarGenerateRequest.setAllotCarrierIds(allotCarrierIds);
        List<SendcarGenerateV> sendcarGenerateVList = sendcarGenerateVMapper.queryList(sendcarGenerateRequest);
        return sendcarGenerateVList;
    }
}
