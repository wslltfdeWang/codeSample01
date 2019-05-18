package com.foreveross.vds.service.fms.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.Cursor;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsSettleTransportChargeVRequest;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.FmsTransportSettledLine;
import com.foreveross.vds.dto.fms.SettleTransportChargeV;
import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.FmsStatusService;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.fms.mapper.FmsSettleTransportChargeVMapper;
import com.foreveross.vds.service.fms.service.FmsCreateSettleTransportChargeService;
import com.foreveross.vds.service.fms.service.FmsSettleTransportChargeVService;
import com.foreveross.vds.util.exception.ServiceException;
import com.foreveross.vds.util.tools.AnalysisExcelUtils;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.foreveross.vds.vo.fms.FmsSettleTransportChargeV;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class FmsSettleTransportChargeVServiceImpl implements FmsSettleTransportChargeVService {

    @Autowired
    private FmsSettleTransportChargeVMapper fmsSettleTransportChargeVMapper;
    @Autowired
    private FmsCreateSettleTransportChargeService fmsCreateSettleTransportChargeService;
    @Autowired
    private FmsStatusService fmsStatusService;
    @Autowired
    private LookupService lookupService;

    @Override
    public BasePageResponse pageFmsSettleTransportChargeTree(Integer pageIndex, Integer pageSize, FmsSettleTransportChargeVRequest whereCause) {
        PageHelper.startPage(pageIndex, pageSize, whereCause.getOrderBy());
        Page<FmsSettleTransportChargeV> page = (Page<FmsSettleTransportChargeV>) this.listFmsSettleTransportChargeTree(whereCause);

        BasePageResponse basePageResponse = new BasePageResponse(page.getTotal(), page.getResult());
        return basePageResponse;
    }

    @Override
    public List<FmsSettleTransportChargeV> listFmsSettleTransportChargeTree(FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest) {
        return fmsSettleTransportChargeVMapper.listFmsSettleTransportChargeTree(fmsSettleTransportChargeVRequest);
    }

    @Override
    public void setEazyuiTreegridId(BasePageResponse basePageResponse) {
        List rows = basePageResponse.getRows();
        for (Object row : rows) {
            FmsSettleTransportChargeV fmsSettleTransportChargeV = (FmsSettleTransportChargeV) row;
            fmsSettleTransportChargeV.setEazyuiTreegridId(fmsSettleTransportChargeV.getSendcarHeaderId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecRequest createSettleTransportCharge(SettleTransportChargeV settleTransportChargeV) throws BusinessServiceException {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        //将数据转换为list
        convertStrToList(settleTransportChargeV);

        //将结算数据转换为list并判断是否有未匹配数据
        List<FmsTransportSettledLine> insertList = new ArrayList<>();
        List<SettleTransportChargeV> settleTransportChargeVList = isUnmatched(settleTransportChargeV);

        //记录批次总价
        BigDecimal batchFee = BigDecimal.valueOf(0);

        //循环拼装值
        for (int i = 0; i < settleTransportChargeVList.size(); i++) {
            SettleTransportChargeV entity = settleTransportChargeVList.get(i);

            //判断数据是否重复
            isDuplicate(settleTransportChargeVList, i);

            //获取总价
            BigDecimal amount = fmsCreateSettleTransportChargeService.getPrice(entity.getSendcarheaderid());

            //将送车交接单总价存入
            batchFee = batchFee.add(amount);

            FmsTransportSettledLine fmsTransportSettledLine = new FmsTransportSettledLine();
            fmsTransportSettledLine.setTransportContractHeaderId(entity.getTransportcontractheaderid());
            fmsTransportSettledLine.setOrderNumber(fmsSettleTransportChargeVMapper.getSettleTransportChargeVForTop(settleTransportChargeV.getSendcarheaderidList().get(i)));
            fmsTransportSettledLine.setDistanceLineId(entity.getDistanceheaderid());
            fmsTransportSettledLine.setAmount(amount);
            fmsTransportSettledLine.setSendcarHeaderId(entity.getSendcarheaderid());
            fmsTransportSettledLine.setCreatedBy(settleTransportChargeV.getUserId());
            fmsTransportSettledLine.setCreationDate(new Date());
            fmsTransportSettledLine.setLastUpdatedBy(settleTransportChargeV.getUserId());
            fmsTransportSettledLine.setLastUpdateDate(new Date());
            insertList.add(fmsTransportSettledLine);
        }

        SettleTransportChargeV settleTransportChargeV1 = settleTransportChargeVList.get(0);

        FmsTransportSettledHeader fmsTransportSettledHeader = new FmsTransportSettledHeader();
        fmsTransportSettledHeader.setOrgId(settleTransportChargeV1.getOrgid());
        fmsTransportSettledHeader.setLogisticsId(settleTransportChargeV1.getLogisticsid());
        fmsTransportSettledHeader.setStartPointId(settleTransportChargeV1.getStartpointid());
        fmsTransportSettledHeader.setBatchFee(batchFee);
        fmsTransportSettledHeader.setFormalFlag(settleTransportChargeV1.getContracttype());
        fmsTransportSettledHeader.setContractHeaderId(settleTransportChargeV1.getTransportcontractheaderid());
        fmsTransportSettledHeader.setRefundFlag("N");
        fmsTransportSettledHeader.setDistanceHeaderId(settleTransportChargeV1.getDistanceheaderid());
        fmsTransportSettledHeader.setSettledStatus(lookupService.getLookupId("SETTLE_STATUS", "SUBMITED"));
        fmsTransportSettledHeader.setTransportMethod(settleTransportChargeV1.getTransportMethod());
        fmsTransportSettledHeader.setCreatedBy(settleTransportChargeV.getUserId());
        fmsTransportSettledHeader.setCreationDate(new Date());
        fmsTransportSettledHeader.setLastUpdatedBy(settleTransportChargeV.getUserId());
        fmsTransportSettledHeader.setLastUpdateDate(new Date());

        String batchCode = fmsCreateSettleTransportChargeService.getBatchCode(fmsTransportSettledHeader);
        fmsTransportSettledHeader.setBatchCode(batchCode);
        recRequest.setMessage(batchCode);
        fmsTransportSettledHeader = fmsCreateSettleTransportChargeService.addFmsTransportSettledHeader(fmsTransportSettledHeader);

        for (int i = 0; i < insertList.size(); i++) {
            FmsTransportSettledLine fmsTransportSettledLine = insertList.get(i);

            fmsTransportSettledLine.setTransportSettledHeaderId(fmsTransportSettledHeader.getTransportSettledHeaderId());
            fmsCreateSettleTransportChargeService.addFmsTransportSettledLine(fmsTransportSettledLine);

            settleTransportChargeV1 = settleTransportChargeVList.get(i);
            fmsCreateSettleTransportChargeService.updateSendCarHeader(settleTransportChargeV1.getSendcarheaderid());
            fmsStatusService.updateSendcarSettleStatus(settleTransportChargeV1.getSendcarheaderid(), "SUBMITED", -1, "-1");
        }

        return recRequest;
    }

    @Override
    public List<TmsSendcarHeaderFmsE> export(FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest) {
        return fmsSettleTransportChargeVMapper.export(fmsSettleTransportChargeVRequest);
    }

    @Override
    public String importSettleTransport(Workbook workbook, Long userId, String sessionId, Long logisticsId) {
        String result = "";
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Cursor cursor = new Cursor(sheet, sheetIndex);
            result += sheetWork(cursor, userId, sessionId, logisticsId);
        }
        return result;
    }

    private String sheetWork(Cursor cursor, Long userId, String sessionId, Long logisticsId) {

        boolean flag = true;
        String message = "";
        StringBuffer result = new StringBuffer();

        Sheet sheet = null;
        if (flag) {
            sheet = cursor.getSheet();
        }

        List<String> list = null;
        if (flag) {
            list = new ArrayList<String>();
            list.add("");
            list.add("sendcarOrderNumber");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("contractNumber");
            list.add("");
            list.add("");
        }

        // 解析数据
        List<TmsSendcarHeaderFmsE> tmsSendcarHeaderFmsEList = null;
        if (flag) {
            tmsSendcarHeaderFmsEList = new ArrayList<>();
            for (int rowNum = 3; rowNum <= sheet.getLastRowNum() - 1; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    TmsSendcarHeaderFmsE tmsSendcarHeaderFmsE = null;
                    try {
                        tmsSendcarHeaderFmsE = AnalysisExcelUtils.getValue1(row, list.size(), TmsSendcarHeaderFmsE.class, list);
                    } catch (Exception e) {
                        flag = false;
                        message = String.format("Sheet:%s 行:%s 解析数据失败", cursor.getSheetIndex(), rowNum);
                        e.printStackTrace();
                        break;
                    }
                    tmsSendcarHeaderFmsEList.add(tmsSendcarHeaderFmsE);
                }
            }

            if (tmsSendcarHeaderFmsEList.size() == 0) {
                flag = false;
                message = "未解析到数据，请检查导入文件";
            }
        }

        List<String> errorBatchList = new ArrayList<>();
        Map<String, String> sendcarOrderNumberMap = new HashMap<>();
        Map<String, SettleTransportChargeV> groupList = new HashMap<>();
        tmsSendcarHeaderFmsEList.forEach(item -> {
            boolean forFlag = true;
            String errorMsg = "";

            String batchCode = item.getContractNumber();
            if (AnalyzerTool.isEmpty(batchCode)) {
                batchCode = "空";
            }

            for (String errorBatch : errorBatchList) {
                if (batchCode.equals(errorBatch)) {
                    return;
                }
            }

            String sendcarOrderNumber = item.getSendcarOrderNumber();
            if (AnalyzerTool.isEmpty(sendcarOrderNumber)) {
                forFlag = false;
                errorMsg = "送车交接单号不能为空";
//				throw new ServiceException("0", "送车交接单号不能为空");
            }
            
            if (forFlag) {
            	if (sendcarOrderNumberMap.containsKey(sendcarOrderNumber)) {
            		String sendcarBatchCode = sendcarOrderNumberMap.get(sendcarOrderNumber);
            		if (!sendcarBatchCode.equals(batchCode)) {
            			forFlag = false;
            			errorMsg = String.format("同一送车交接单号: %s 不能分为多个结算批次", sendcarOrderNumber);
            			errorBatchList.add(sendcarBatchCode);
                        groupList.remove(sendcarBatchCode);
                        result.append("原批次号：").append(sendcarBatchCode);
                        result.append("  生成结果： 失败");
                        result.append("  失败原因：").append(errorMsg);
                        result.append(" \n ");
            		} else {
            			return;
            		}
            	} else {
            		sendcarOrderNumberMap.put(sendcarOrderNumber, batchCode);
            	}
            }

            List<FmsSettleTransportChargeV> listFmsSettleTransportChargeTree = null;
            if (forFlag) {
                FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest = new FmsSettleTransportChargeVRequest();
                fmsSettleTransportChargeVRequest.setSendcarOrderNumber(sendcarOrderNumber);
                fmsSettleTransportChargeVRequest.setReceiveStatus("ALL_RECEIVED");
                fmsSettleTransportChargeVRequest.setSettledFlag("N");
                listFmsSettleTransportChargeTree = this.listFmsSettleTransportChargeTree(fmsSettleTransportChargeVRequest);
                if (AnalyzerTool.isEmpty(listFmsSettleTransportChargeTree)) {
                    forFlag = false;
                    errorMsg = "未查询到或已生成批次送车交接单号：" + sendcarOrderNumber;
//					throw new ServiceException("0", "未查询到或已生成批次送车交接单号：" + sendcarOrderNumber);
                } else if (listFmsSettleTransportChargeTree.size() > 1) {
                    forFlag = false;
                    errorMsg = "查询到多条送车交接单号：" + sendcarOrderNumber;
//					throw new ServiceException("0", "查询到多条送车交接单号：" + sendcarOrderNumber);
                }
            }

            if (!forFlag) {
                errorBatchList.add(batchCode);
                groupList.remove(batchCode);
                result.append("原批次号：").append(batchCode);
                result.append("  生成结果： 失败");
                result.append("  失败原因：").append(errorMsg);
                result.append(" \n ");
                return;
            }

            SettleTransportChargeV settleTransportChargeV = groupList.get(batchCode);
            if (settleTransportChargeV == null) {
                settleTransportChargeV = new SettleTransportChargeV();
                settleTransportChargeV.setSendcarheaderidStr("");
                settleTransportChargeV.setUserId(userId);
                settleTransportChargeV.setSessionId(sessionId);
            }
            String sendcarheaderidStr = settleTransportChargeV.getSendcarheaderidStr();
            sendcarheaderidStr += listFmsSettleTransportChargeTree.get(0).getSendcarHeaderId() + ",";
            settleTransportChargeV.setSendcarheaderidStr(sendcarheaderidStr);

            groupList.put(batchCode, settleTransportChargeV);
        });

        groupList.forEach((key, value) -> {
            result.append("原批次号：").append(key);
            try {
                String sendcarheaderidStr = value.getSendcarheaderidStr();
                sendcarheaderidStr = sendcarheaderidStr.substring(0, sendcarheaderidStr.length() - 1);
                value.setSendcarheaderidStr(sendcarheaderidStr);
                RecRequest recRequest = this.createSettleTransportCharge(value);
                if (recRequest.getReCode() == 0) {
                    result.append("  生成结果： 成功");
                    result.append("  生成批次号： ").append(recRequest.getMessage());
                    result.append(" \n ");
                } else {
                    result.append("  生成结果： 失败");
                    result.append("  失败原因：").append(recRequest.getMessage());
                    result.append(" \n ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.append("  生成结果： 失败");
                result.append("  失败原因：").append(e.getMessage());
                result.append(" \n ");
            }
        });

        if (!flag) {
            throw new ServiceException("0", message);
        }

        return result.toString();
    }


    /**
     * 将SettleTransportChargeV中的送车交接单头id转换为list
     *
     * @param settleTransportChargeV 结算数据
     * @throws BusinessServiceException 业务异常
     */
    private void convertStrToList(SettleTransportChargeV settleTransportChargeV) throws BusinessServiceException {
        if (settleTransportChargeV.getSendcarheaderidStr() == null || "".equals(settleTransportChargeV.getSendcarheaderidStr())) {
            throw new BusinessServiceException("未选择正确的结算数据！");
        }

        String[] sendcarheaderIdStrArray = settleTransportChargeV.getSendcarheaderidStr().split(",");

        List<Long> sendcarheaderIdList = new ArrayList<>();

        for (String aSendcarheaderIdStrArray : sendcarheaderIdStrArray) {

            if (aSendcarheaderIdStrArray != null && !"".equals(aSendcarheaderIdStrArray)) {
                sendcarheaderIdList.add(Long.parseLong(aSendcarheaderIdStrArray));
            }
        }

        settleTransportChargeV.setSendcarheaderidList(sendcarheaderIdList);
    }

    /**
     * 判断结算数据是否完全匹配
     *
     * @param settleTransportChargeV 结算数据
     * @return List<SettleTransportChargeV> 结算数据
     * @throws BusinessServiceException 业务异常
     */
    private List<SettleTransportChargeV> isUnmatched(SettleTransportChargeV settleTransportChargeV) throws BusinessServiceException {
        List<SettleTransportChargeV> settletransportchargevlist = fmsCreateSettleTransportChargeService.getSettleTransportChargeV(settleTransportChargeV);

        for (SettleTransportChargeV settleTransportChargeV1 : settletransportchargevlist) {
            if (settleTransportChargeV1.getTransportcontractheaderid() == null) {
                throw new BusinessServiceException("存在未匹配合同的数据，不能生成结算批次");
            }
        }

        return settletransportchargevlist;
    }

    private void isDuplicate(List<SettleTransportChargeV> list, int i) throws BusinessServiceException {
        for (int j = i + 1; j < list.size(); j++) {
            SettleTransportChargeV entity = list.get(i);
            SettleTransportChargeV loopEntity = list.get(j);

            if (!entity.getStartpointid().equals(loopEntity.getStartpointid())) {
                throw new BusinessServiceException("批次生成失败，存在不同出发地数据");
            }

            if (!entity.getTransportMethod().equals(loopEntity.getTransportMethod())) {
                throw new BusinessServiceException("批次生成失败，存在不同发运方式数据");
            }

            if (!entity.getTransportcontractheaderid().equals(loopEntity.getTransportcontractheaderid())) {
                throw new BusinessServiceException("批次生成失败，存在不同合同数据");
            }

            if (!entity.getLogisticsid().equals(loopEntity.getLogisticsid())) {
                throw new BusinessServiceException("批次生成失败，存在不同物流商数据");
            }

            if (entity.getSendCarDate() != null
                    && !entity.getSendCarDate().equals(loopEntity.getSendCarDate())) {
                throw new BusinessServiceException("批次生成失败，存在不同自然年数据");
            }
        }
    }
}
