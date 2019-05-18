package com.foreveross.vds.service.fms.service.impl.transportContractImport;

import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.dto.fms.transportContractImport.CostType;
import com.foreveross.vds.dto.fms.transportContractImport.Cursor;
import com.foreveross.vds.dto.fms.transportContractImport.TransportMethod;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.fms.mapper.TransportContractImportVerifierMapper;
import com.foreveross.vds.service.fms.service.DistanceLineService;
import com.foreveross.vds.service.fms.service.FmsCarCategoryService;
import com.foreveross.vds.service.fms.service.FmsDistanceHeaderService;
import com.foreveross.vds.service.fms.service.transportContractImport.TransportContractImportVerifierService;
import com.foreveross.vds.util.tools.ListHelper;
import com.foreveross.vds.util.tools.StringHelper;
import com.foreveross.vds.vo.fms.FmsCarCategory;
import com.foreveross.vds.vo.fms.FmsDistanceHeader;
import com.foreveross.vds.vo.fms.FmsDistanceLine;
import com.foreveross.vds.vo.tms.TmsDeparture;
import com.foreveross.vds.vo.tms.TmsLogisticsProviders;
import com.foreveross.vds.vo.tms.TmsRegionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TransportContractImportVerifierServiceImpl implements TransportContractImportVerifierService {

    @Autowired
    private TransportContractImportVerifierMapper transportContractImportVerifierMapper;

    @Autowired
    private DistanceLineService distanceLineService;

    @Autowired
    private FmsCarCategoryService fmsCarCategoryService;

    @Autowired
    private FmsDistanceHeaderService fmsDistanceHeaderService;

    @Autowired
    private LookupService lookupService;

    @Override
    public TransportMethod verifyTransMethodNoException(String stringCellValue) {
        if (StringHelper.isEmpty(stringCellValue)) {
            return null;
        }

        return getTransportMethod(stringCellValue);
    }

    @Override
    public FmsCarCategory verifyCarCategory(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "车型大类不可为空！"));
        }

        List<FmsCarCategory> fmsCarCategorys = getCarCategory(stringCellValue);
        if (ListHelper.isEmpty(fmsCarCategorys)) {
            throw new BusinessServiceException(buildMsg(cursor, "车型大类不存在！"));
        }

        return fmsCarCategorys.get(0);
    }

    @Override
    public CostType verifyCostType(String stringCellValue) {
        if (StringHelper.isEmpty(stringCellValue)) {
            return null;
        }

        return getCostType(stringCellValue);
    }

    @Override
    public TransportMethod verifyTransMethod(String stringCellValue, Cursor cursor) {
        if (StringHelper.isEmpty(stringCellValue)) {
            return null;
        }

        return getTransportMethod(stringCellValue);
    }

    @Override
    public Long verifyContractType(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "合同类型不可为空"));
        }

        if (stringCellValue.equals("正式合同")) {
            return lookupService.getLookupId("CONTRACT_TYPE", "FORMAL");
        } else if (stringCellValue.equals("临时合同")) {
            return lookupService.getLookupId("CONTRACT_TYPE", "TEMPORARY");
        } else {
            throw new BusinessServiceException(buildMsg(cursor, "合同类型[" + stringCellValue + "]填写错误，只能为临时合同/正式合同！"));
        }
    }

    @Override
    public void verifyContractNumber(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "合同号不可为空"));
        }

        /*if (transportContractImportVerifierMapper.contractNumberRepeat(stringCellValue) != 0) {
            throw new BusinessServiceException(buildMsg(cursor, "合同号[" + stringCellValue + "]已经被使用！"));
        }*/
    }

    @Override
    public void verifyContractName(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "合同名称不可为空"));
        }
        /*if(transportContractImportVerifierMapper.contractNameRepeat(stringCellValue) != 0){
            throw new BusinessServiceException(buildMsg(cursor,"合同名称["+stringCellValue+"]已经被使用！"));
        }*/
    }

    @Override
    public TmsLogisticsProviders verifyLogistics(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "物流商不可为空"));
        }

        List<TmsLogisticsProviders> tmsLogisticsProviders = transportContractImportVerifierMapper.getLogistics(stringCellValue);
        if (ListHelper.isEmpty(tmsLogisticsProviders)) {
            throw new BusinessServiceException(buildMsg(cursor, "物流商[" + stringCellValue + "]不存在！"));
        }
        return tmsLogisticsProviders.get(0);
    }

    @Override
    public TmsDeparture verifyTmsStartPoint(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "所属基地不可为空"));
        }

        List<TmsDeparture> tmsStartPoints = transportContractImportVerifierMapper.getTmsStartPoint(stringCellValue);
        if (ListHelper.isEmpty(tmsStartPoints)) {
            throw new BusinessServiceException(buildMsg(cursor, "所属基地[" + stringCellValue + "]不存在"));
        }
        return tmsStartPoints.get(0);
    }

    @Override
    public Date verifyStartDate(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "合同开始时间不可为空"));
        }
        try {
            return new SimpleDateFormat("yyyy.MM.dd").parse(stringCellValue);
        } catch (ParseException e) {
            throw new BusinessServiceException(buildMsg(cursor, "日期[" + stringCellValue + "]格式不正确！应该为yyyy.MM.dd"));
        }
    }

    @Override
    public Date verifyEndDate(String stringCellValue, Cursor cursor, Date startDate) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "合同结束时间不可为空"));
        }

        Date endDate;
        try {
            endDate = new SimpleDateFormat("yyyy.MM.dd").parse(stringCellValue);
        } catch (ParseException e) {
            throw new BusinessServiceException(buildMsg(cursor, "日期[" + stringCellValue + "]格式不正确！应该为yyyy.MM.dd"));
        }

        if (endDate.compareTo(startDate) <= 0) {
            throw new BusinessServiceException(buildMsg(cursor, "结束时间必须晚于开始时间"));
        }

        return endDate;
    }

    @Override
    public Long verifyStartProvince(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        return verifyProvince(stringCellValue, cursor, "起点省");
    }

    @Override
    public Long verifyStartCity(String stringCellValue, Cursor cursor, Long startProvinceId) throws BusinessServiceException {
        return verifyCity(stringCellValue, cursor, startProvinceId, "起点市");
    }

    @Override
    public Long verifyStartCounty(String stringCellValue, Cursor cursor, Long startCityId) throws BusinessServiceException {
        return verifyCounty(stringCellValue, cursor, startCityId, "起点区");
    }

    @Override
    public Long verifyEndProvince(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        return verifyProvince(stringCellValue, cursor, "终点省");
    }

    @Override
    public Long verifyEndCity(String stringCellValue, Cursor cursor, Long endProvinceId) throws BusinessServiceException {
        return verifyCity(stringCellValue, cursor, endProvinceId, "终点市");
    }

    @Override
    public Long verifyEndCounty(String stringCellValue, Cursor cursor, Long endCityId) throws BusinessServiceException {
        return verifyCounty(stringCellValue, cursor, endCityId, "终点区");
    }

    @Override
    public FmsDistanceHeader verifyDistanceName(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "运距名称不可为空"));
        }

        List<FmsDistanceHeader> fmsDistanceHeaders = getDistanceHeader(stringCellValue);
        if (ListHelper.isEmpty(fmsDistanceHeaders)) {
            throw new BusinessServiceException(buildMsg(cursor, "运距名称[" + stringCellValue + "]不正确！"));
        }
        return fmsDistanceHeaders.get(0);
    }

    @Override
    public Integer verifyDistance(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "运距不可为空"));
        }
        try {
            return Integer.valueOf(stringCellValue);
        } catch (Exception e) {
            throw new BusinessServiceException(buildMsg(cursor, "运距[" + stringCellValue + "]无法解析，必须为数字"));
        }
    }

    @Override
    public BigDecimal verifyUnitPrice(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "单价不可为空！"));
        }

        try {
            return new BigDecimal(stringCellValue);
        } catch (Exception e) {
            throw new BusinessServiceException(buildMsg(cursor, "单价[" + stringCellValue + "]不正确！"));
        }

    }

    @Override
    public FmsDistanceLine verifyDistanceLine(TransportContract contract, Cursor cursor) throws BusinessServiceException {
        FmsDistanceLine fmsDistanceLine = distanceLineService.findDistanceLine(contract);
        if (fmsDistanceLine == null) {
            throw new BusinessServiceException(buildMsg(cursor, "未找到对应运距！"));
        }
        return fmsDistanceLine;
    }

    @Override
    public BigDecimal verifyOtherPrice(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            return null;
        }

        try {
            return new BigDecimal(stringCellValue);
        } catch (Exception e) {
            throw new BusinessServiceException(buildMsg(cursor, "价格格式不正确！无法解析！"));
        }
    }

    private Long verifyProvince(String stringCellValue, Cursor cursor, String alertName) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, alertName + "不可为空"));
        }

        Long startProvinceId = validReginInfo(stringCellValue, 1000000000L);
        if (startProvinceId == null) {
            throw new BusinessServiceException(buildMsg(cursor, alertName + "[" + stringCellValue + "]不存在！"));
        }
        return startProvinceId;
    }

    private Long verifyCity(String stringCellValue, Cursor cursor, Long startPointId, String alertName) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, alertName + "不可为空"));
        }

        Long startCityId = validReginInfo(stringCellValue, startPointId);
        if (startCityId == null) {
            throw new BusinessServiceException(buildMsg(cursor, alertName + "[" + stringCellValue + "]不存在！"));
        }
        return startCityId;
    }

    private Long verifyCounty(String stringCellValue, Cursor cursor, Long startCityId, String alertName) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, alertName + "不可为空"));
        }

        Long startCountyId = validReginInfo(stringCellValue, startCityId);
        if (startCountyId == null) {
            throw new BusinessServiceException(buildMsg(cursor, alertName + "[" + stringCellValue + "]不存在！"));
        }
        return startCountyId;
    }

    private TransportMethod getTransportMethod(String stringCellValue) {
        return transportContractImportVerifierMapper.getTransportMethod(stringCellValue);
    }

    private List<FmsCarCategory> getCarCategory(String stringCellValue) {
        FmsCarCategory fmsCarCategory = new FmsCarCategory();
        fmsCarCategory.setCarCategoryName(stringCellValue);

        return fmsCarCategoryService.listByWhereCause(fmsCarCategory);
    }

    private String buildMsg(Cursor cursor, String msg) {
        Integer sheetIndex = cursor.getSheetIndex() + 1;//实际标号=下标号+1
        Integer rowIndex = cursor.getRowIndex() + 1;//实际标号=下标号+1
        Integer cellIndex = cursor.getCellIndex() + 1;//实际标号=下标号+1

        StringBuilder sb = new StringBuilder();
        sb.append("[Sheet:").append(sheetIndex).append(" 行:").append(rowIndex).append(" 列:").append(cellIndex).append("]").append(msg);
        return sb.toString();
    }

    private CostType getCostType(String stringCellValue) {
        if (CostType.over_sea_fee.val.equals(stringCellValue)) {
            return CostType.over_sea_fee;
        }

        if (CostType.railway_Prepare_Fee.val.equals(stringCellValue)) {
            return CostType.railway_Prepare_Fee;
        }

        return null;
    }

    private Long validReginInfo(String reginName, Long parentReginId) {
        TmsRegionInfo param = new TmsRegionInfo();
        param.setRegionName(reginName);
        if (parentReginId != -1L) {
            param.setParentId(parentReginId);
        }
        List<TmsRegionInfo> tmsRegionInfos = transportContractImportVerifierMapper.getReginInfo(param);
        if (tmsRegionInfos.size() == 0) {
            return null;
        } else {
            return tmsRegionInfos.get(0).getRegionId();
        }
    }

    private List<FmsDistanceHeader> getDistanceHeader(String stringCellValue) {
        FmsDistanceHeader fmsDistanceHeader = new FmsDistanceHeader();
        fmsDistanceHeader.setDistanceName(stringCellValue);
        return fmsDistanceHeaderService.queryList(fmsDistanceHeader);
    }

    @Override
    public void verifyReceivingUnit(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "收款单位不可为空"));
        }
    }

    @Override
    public void verifyAccountBank(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "开户行不可为空"));
        }
    }

    @Override
    public void verifyReceivingAcount(String stringCellValue, Cursor cursor) throws BusinessServiceException {
        if (StringHelper.isEmpty(stringCellValue)) {
            throw new BusinessServiceException(buildMsg(cursor, "收款账户不可为空"));
        }
    }

}
