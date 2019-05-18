package com.foreveross.vds.service.fms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.client.client.VdsClient;
import com.foreveross.vds.client.client.VdsDefaultClient;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.*;
import com.foreveross.vds.dto.inter.expensereport.ExpenseReportHeadersVO;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.ExportTranslatorService;
import com.foreveross.vds.service.common.service.FmsStatusService;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.common.util.ConstantUtil;
import com.foreveross.vds.service.fms.mapper.FmsBmpMapper;
import com.foreveross.vds.service.fms.service.FmsBmpService;
import com.foreveross.vds.service.fms.service.FmsExpenseAccountService;
import com.foreveross.vds.service.fms.service.FmsExpenseSettledRelateService;
import com.foreveross.vds.util.tools.StringHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FmsBmpServiceImpl implements FmsBmpService {

    @Autowired
    private FmsBmpMapper fmsBmpMapper;
    @Autowired
    private FmsExpenseAccountService fmsExpenseAccountService;
    @Autowired
    private FmsExpenseSettledRelateService fmsExpenseSettledRelateService;
    @Autowired
    private LookupService lookupService;
    @Autowired
    private FmsStatusService fmsStatusService;
    @Autowired
    private ExportTranslatorService exportTranslatorService;

    @Override
    public Page<FmsBmpV> queryFmsBmpVByPage(FmsBmpV fmsBmpV) {
        PageHelper.startPage(fmsBmpV.getPage(), fmsBmpV.getRows());
        Page<FmsBmpV> page = (Page<FmsBmpV>) fmsBmpMapper.queryFmsBmpVList(fmsBmpV);
        return page;
    }

    @Override
    public List<FmsBmpV> queryFmsBmpV(FmsBmpV fmsBmpV) {
        return fmsBmpMapper.queryFmsBmpVList(fmsBmpV);
    }

    @Override
    @CustTx
    public RecRequest updateSettledStatus(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);

        //修改结算标识
        try {
            String feeTypeStatus;
            switch (fmsExpenseAccount.getBatchType()) {
                case "TRANSPORT_FEE":
                    feeTypeStatus = "TRANSPORT";
                    break;
                case "STORAGE_FEE":
                    feeTypeStatus = "STORAGE";
                    break;
                case "MANUAL_FEE":
                    feeTypeStatus = "MANUAL";
                    break;
                case "REFUND_FEE":
                    feeTypeStatus = "REFUND";
                    break;

                default:
                    throw new BusinessServiceException("发起流程失败，费用类型错误");
            }
            fmsStatusService.updateExpenseSettleStatus(fmsExpenseAccount.getExpenseAccountId(), "APPROVALING", feeTypeStatus, fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
        } catch (BusinessServiceException e) {
            throw new BusinessServiceException("结算状态变更失败");
        }

        return recRequest;
    }

    @Override
    @CustTx
    public RecRequest startFmsFlow(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);

        if (fmsExpenseAccount == null) {
            throw new BusinessServiceException("发起流程失败");
        }
        List<FmsExpenseDetail> detailList = fmsExpenseAccount.getDeatilList();

        if (detailList == null || detailList.size() == 0) {
            throw new BusinessServiceException("发起流程失败,至少有一条费用明细");
        }
        //添加报销单数据
        Long feeType = null;
        String lookupType = "FEE_TYPE";
        String meaning = null;
        switch (fmsExpenseAccount.getBatchType()) {
            case "TRANSPORT_FEE":
                meaning = "TRANSPORT_FEE";
                break;
            case "STORAGE_FEE":
                meaning = "STORAGE_FEE";
                break;
            case "MANUAL_FEE":
                meaning = "MANUAL_FEE";
                break;
            case "REFUND_FEE":
                meaning = "REFUND_FEE";
                break;

            default:
                throw new BusinessServiceException("发起流程失败，费用类型错误");
        }
        feeType = lookupService.getLookupId(lookupType, meaning);
        fmsExpenseAccount.setFeeType(feeType);
        fmsExpenseAccount.setCreatedBy(fmsExpenseAccount.getUserId());
        fmsExpenseAccount.setCreationDate(new Date());
        fmsExpenseAccount.setLastUpdatedBy(fmsExpenseAccount.getUserId());
        fmsExpenseAccount.setLastUpdateDate(new Date());
        fmsExpenseAccount.setLastUpdateLogin(fmsExpenseAccount.getSessionId());
        Long settledStatus = lookupService.getLookupIdByDesc("SETTLE_STATUS", "审批中");

        if (settledStatus == null) {
            throw new BusinessServiceException("结算状态枚举中未维护审核中状态");
        }
        fmsExpenseAccount.setSettledStatus(settledStatus);
        fmsExpenseAccount = fmsExpenseAccountService.addFmsExpenseAccount(fmsExpenseAccount);

        List<FmsExpenseSettledRelate> fmsExpenseSettledRelateList = new ArrayList<FmsExpenseSettledRelate>();
        //添加报销单结算批次关联数据
        String dataIdStr = fmsExpenseAccount.getDataIdStr();

        if (!StringUtils.isEmpty(dataIdStr)) {
            String[] dataIdArray = dataIdStr.split(",");

            for (int i = 0; i < dataIdArray.length; i++) {

                if (StringUtils.isEmpty(dataIdArray[i])) {
                    continue;
                }

                FmsExpenseSettledRelate fmsExpenseSettledRelate = new FmsExpenseSettledRelate();
                fmsExpenseSettledRelate.setSettledHeaderId(Long.valueOf(dataIdArray[i]));
                fmsExpenseSettledRelate.setCreatedBy(fmsExpenseAccount.getCreatedBy());
                fmsExpenseSettledRelate.setCreationDate(fmsExpenseAccount.getCreationDate());
                fmsExpenseSettledRelate.setLastUpdatedBy(fmsExpenseAccount.getLastUpdatedBy());
                fmsExpenseSettledRelate.setLastUpdateDate(fmsExpenseAccount.getLastUpdateDate());
                fmsExpenseSettledRelate.setLastUpdateLogin(fmsExpenseAccount.getLastUpdateLogin());
                fmsExpenseSettledRelateList.add(fmsExpenseSettledRelate);
            }
        }

        List<FmsExpenseSettledRelate> addList = new ArrayList<FmsExpenseSettledRelate>();
        for (FmsExpenseSettledRelate fmsExpenseSettledRelate : fmsExpenseSettledRelateList) {
            fmsExpenseSettledRelate.setExpenseAccountId(fmsExpenseAccount.getExpenseAccountId());
            addList.add(fmsExpenseSettledRelate);
        }

        fmsExpenseSettledRelateService.addFmsExpenseSettledRelateByList(addList);

        for (FmsExpenseDetail fmsExpenseDetail : detailList) {
            fmsExpenseDetail.setExpenseAccountId(fmsExpenseAccount.getExpenseAccountId());
            fmsExpenseDetail.setCreatedBy(fmsExpenseAccount.getUserId());
            fmsExpenseDetail.setCreationDate(new Date());
            fmsExpenseDetail.setLastUpdatedBy(fmsExpenseAccount.getUserId());
            fmsExpenseDetail.setLastUpdateDate(new Date());
            fmsExpenseDetail.setLastUpdateLogin(fmsExpenseAccount.getSessionId());
            fmsExpenseAccountService.addFmsExpenseDetail(fmsExpenseDetail);
        }

        //如果有合同明细则添加内容
        if (fmsExpenseAccount.getContractDetailList().size() > 0) {
            for (FmsExpenseContractDetail fmsExpenseContractDetail : fmsExpenseAccount.getContractDetailList()) {
                fmsExpenseContractDetail.setExpenseAccountId(fmsExpenseAccount.getExpenseAccountId());
                fmsExpenseContractDetail.setCreatedBy(fmsExpenseAccount.getUserId());
                fmsExpenseContractDetail.setCreationDate(new Date());
                fmsExpenseContractDetail.setLastUpdatedBy(fmsExpenseAccount.getUserId());
                fmsExpenseContractDetail.setLastUpdateDate(new Date());
                fmsExpenseContractDetail.setLastUpdateLogin(fmsExpenseAccount.getSessionId());
                fmsExpenseAccountService.addFmsExpenseContractDetail(fmsExpenseContractDetail);
            }
        }

        FmsProApprovalRecord fmsProApprovalRecord1 = new FmsProApprovalRecord();
        fmsProApprovalRecord1.setLinkName("发起流程");
        fmsProApprovalRecord1.setApprovalResult("发起流程");
        fmsProApprovalRecord1.setApprovalBy(fmsExpenseAccount.getAttribute2());
        fmsProApprovalRecord1.setApprovalDate(new Date());
        fmsProApprovalRecord1.setCreatedBy(fmsExpenseAccount.getUserId());
        fmsProApprovalRecord1.setCreationDate(new Date());
        fmsProApprovalRecord1.setLastUpdatedBy(fmsExpenseAccount.getUserId());
        fmsProApprovalRecord1.setLastUpdateDate(new Date());
        fmsProApprovalRecord1.setLastUpdateLogin(fmsExpenseAccount.getSessionId());
        fmsProApprovalRecord1.setProInstId(fmsExpenseAccount.getProcId());
        fmsProApprovalRecord1.setExpenseAccountId(fmsExpenseAccount.getExpenseAccountId());
        this.addFmsProApprovalRecord(fmsProApprovalRecord1);
        return recRequest;
    }

    @Override
    public List<FmsExpenseSettledRelateV> queryFmsExpenseSettledRelateVList(String proInstId) {
        return fmsBmpMapper.queryFmsExpenseSettledRelateVList(proInstId);
    }

    /**
     * @param proStatus            流程状态
     * @param fmsProApprovalRecord 流程实例对象
     * @throws BusinessServiceException 业务异常
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void rejOrAdoptFmsFlow(String proStatus, FmsProApprovalRecord fmsProApprovalRecord) throws BusinessServiceException {
        //新增流程流转信息
        this.addFmsProApprovalRecord(fmsProApprovalRecord);

        String reject = "驳回";
        if (reject.equals(proStatus)) {
            return;
        }

        //判断流程是否为部长审批
        String mi = "MI";
        String end = "END";
        if (fmsProApprovalRecord.getType().equals(mi)) {
            String approved = "审核通过";

            //根据报销单ID获取报销单号结算批次关联数据
            String feeType = this.fmsBmpMapper.getFeeType(fmsProApprovalRecord);
            String tranFeeStr = "TRANSPORT_FEE";
            String stoFeeStr = "STORAGE_FEE";
            String manFeeStr = "MANUAL_FEE";
            String refFeeStr = "REFUND_FEE";

            if (tranFeeStr.equals(feeType)) {
                //报销为运输费用
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "APPROVALED", "TRANSPORT", -1L, "-1");
            } else if (stoFeeStr.equals(feeType)) {
                //报销为仓储费用
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "APPROVALED", "STORAGE", -1L, "-1");
            } else if (manFeeStr.equals(feeType)) {
                //报销为手工结算
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "APPROVALED", "REFUND", -1L, "-1");
            } else if (refFeeStr.equals(feeType)) {
                //报销为差价费用
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "APPROVALED", "MANUAL", -1L, "-1");
            }
        } else if (fmsProApprovalRecord.getType().equals(end)) {
            FmsProApprovalRecord fmsProApprovalRecord1 = new FmsProApprovalRecord();
            fmsProApprovalRecord1.setLinkName("财务通过");
            fmsProApprovalRecord1.setApprovalResult("财务通过");
            fmsProApprovalRecord1.setApprovalBy(fmsProApprovalRecord.getApprovalBy());
            fmsProApprovalRecord1.setApprovalDate(new Date());
            fmsProApprovalRecord1.setCreatedBy(-1L);
            fmsProApprovalRecord1.setCreationDate(new Date());
            fmsProApprovalRecord1.setLastUpdatedBy(-1L);
            fmsProApprovalRecord1.setLastUpdateDate(new Date());
            fmsProApprovalRecord1.setLastUpdateLogin("bmp");
            fmsProApprovalRecord1.setExpenseAccountId(fmsProApprovalRecord.getExpenseAccountId());
            fmsProApprovalRecord1.setProInstId(fmsProApprovalRecord.getProInstId());
            this.addFmsProApprovalRecord(fmsProApprovalRecord1);

            //调用ERP接口
            List<ExpenseReportHeadersVO> expenseReportHeadersVOList = new ArrayList<>();
            ExpenseReportHeadersVO headerVO = new ExpenseReportHeadersVO();
            headerVO.setExpenseReportHeaderId(fmsProApprovalRecord.getExpenseAccountId());
            expenseReportHeadersVOList.add(headerVO);

            String url = "/cavds_expense_report_inter";
            try {
                VdsClient vdsClient = new VdsDefaultClient(
                        ConstantUtil.INTER_URL + url, JSONObject.toJSONString(expenseReportHeadersVOList), "", "");
                vdsClient.invokeWithSign(String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public List<FmsProApprovalRecord> queryFmsProApprovalRecordByProInstId(String proInstId) {
        return fmsBmpMapper.queryFmsProApprovalRecordByProInstId(proInstId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FmsExpenseAccount stopFmsFlow(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException {
        //检查数据是否为已审批
        Long settledStatus = lookupService.getLookupIdByCode("SETTLE_STATUS", "APPROVALING");
        List<FmsExpenseAccount> entities = fmsBmpMapper.getStatusByExpenseNumber(fmsExpenseAccount);

        if (entities == null || entities.size() == 0 || entities.size() > 1) {
            throw new BusinessServiceException("结算数据异常，请稍候再试!");
        } else if (entities.get(0).getSettledStatus() == null) {
            throw new BusinessServiceException("结算数据异常，请稍候再试!");
        } else if (!settledStatus.equals(entities.get(0).getSettledStatus())) {
            throw new BusinessServiceException("该数据已审批，不能撤销！");
        }
        fmsExpenseAccount.setExpenseAccountId(entities.get(0).getExpenseAccountId());
        fmsExpenseAccount.setSettledStatus(entities.get(0).getSettledStatus());
        fmsExpenseAccount.setProInstId(entities.get(0).getProInstId());
        fmsExpenseAccount.setFeeType(entities.get(0).getFeeType());
        //更新结算状态为已提交
        FmsProApprovalRecord fmsProApprovalRecord = new FmsProApprovalRecord();
        fmsProApprovalRecord.setExpenseAccountId(fmsExpenseAccount.getExpenseAccountId());
        String feeType = this.fmsBmpMapper.getFeeType(fmsProApprovalRecord);
        String tranFeeStr = "TRANSPORT_FEE";
        String stoFeeStr = "STORAGE_FEE";
        String manFeeStr = "MANUAL_FEE";
        String refFeeStr = "REFUND_FEE";

        if (tranFeeStr.equals(feeType)) {
            //报销为运输费用
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "AUDITED", "TRANSPORT", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
        } else if (stoFeeStr.equals(feeType)) {
            //报销为仓储费用
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "AUDITED", "STORAGE", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
        } else if (manFeeStr.equals(feeType)) {
            //报销为手工结算
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "AUDITED", "REFUND", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
        } else if (refFeeStr.equals(feeType)) {
            //报销为差价费用
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "AUDITED", "MANUAL", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
        }

        //释放关联发票数据
        fmsBmpMapper.unBindInvoice(fmsExpenseAccount);

        //删除已关联发票数据
        fmsBmpMapper.deleteInvoiceRelate(fmsExpenseAccount);

        //失效关联数据
        fmsBmpMapper.disableExpenseAccountLine(fmsExpenseAccount);

        //失效结算批次头数据
        fmsBmpMapper.disableExpenseAccount(fmsExpenseAccount);

        return fmsExpenseAccount;
    }

    @Override
    public void addFmsProApprovalRecord(FmsProApprovalRecord fmsProApprovalRecord) {
        if (fmsProApprovalRecord.getCreatedBy() == null) {
            fmsProApprovalRecord.setCreatedBy(-1L);
            fmsProApprovalRecord.setCreationDate(new Date());
            fmsProApprovalRecord.setLastUpdatedBy(-1L);
            fmsProApprovalRecord.setLastUpdateDate(new Date());
            fmsProApprovalRecord.setLastUpdateLogin("bmp");
            fmsProApprovalRecord.setApprovalDate(new Date());
        }
        fmsBmpMapper.addFmsProApprovalRecord(fmsProApprovalRecord);
    }

    @Override
    public String getExpenseAccountNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String expenseAccountNumber = fmsBmpMapper.getExpenseAccountNumber();

        expenseAccountNumber = "B" + sdf.format(new Date()).substring(2) + expenseAccountNumber;

        return expenseAccountNumber;
    }

    /**
     * @param fmsExpenseAccount 包含报销单号ID
     * @return 返回是否挂账
     * @throws BusinessServiceException 业务异常
     */
    @Override
    public String isRelateConfirm(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException {
        if (fmsExpenseAccount.getExpenseAccountId() == null) {
            throw new BusinessServiceException("数据异常，请联系系统管理员！");
        }
        return fmsBmpMapper.isRelateConfirm(fmsExpenseAccount);
    }

    @Override
    public List<FmsProApprovalRecord> queryFmsProApprovalRecordByExpenseAccountId(Long expenseAccountId) {
        return fmsBmpMapper.queryFmsProApprovalRecordByExpenseAccountId(expenseAccountId);
    }

    @Override
    public void translate(List<FmsBmpV> list) {
        for (FmsBmpV fmsBmpV : list) {
            //费用类型
            fmsBmpV.setBatchTypeDesc(exportTranslatorService.transEnum("FEE_TYPE", fmsBmpV.getBatchType()));
        }
    }

    @Override
    public List<FmsBmpV> getImportFlowData(Long userId, String sessionId, Workbook workbook) throws BusinessServiceException {
        List<FmsBmpV> fmsBmpVList = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        Row row;
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }

            cell.setCellType(CellType.STRING);
            String stringCellValue = cell.getStringCellValue();
            if (StringHelper.isEmpty(stringCellValue)) {
                throw new BusinessServiceException("行[" + i + "]列[1]：结算批次不可为空！");
            }

            FmsBmpV fmsBmpV = getFmsBmpByBatchCode(stringCellValue);
            if (fmsBmpV == null) {
                throw new BusinessServiceException("行[" + i + "]列[1]：结算批次[" + stringCellValue + "]不正确！");
            }
            fmsBmpVList.add(fmsBmpV);
        }

        return fmsBmpVList;
    }

    private FmsBmpV getFmsBmpByBatchCode(String batchCode) {
        FmsBmpV fmsBmpV = new FmsBmpV();
        fmsBmpV.setBatchCode(batchCode);
        List<FmsBmpV> fmsBmpVList = fmsBmpMapper.queryFmsBmpVList(fmsBmpV);
        if (fmsBmpVList == null || fmsBmpVList.size() != 1) {
            return null;
        }
        return fmsBmpVList.get(0);
    }


}
