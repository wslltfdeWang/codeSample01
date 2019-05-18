package com.foreveross.vds.service.fms.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.common.Cursor;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsExpenseAccount;
import com.foreveross.vds.dto.fms.FmsExpenseInvoiceRelate;
import com.foreveross.vds.dto.fms.FmsProApprovalRecord;
import com.foreveross.vds.dto.fms.InvMidVatMain;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.FmsStatusService;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.fms.mapper.ExpenseInvviceRelateMapper;
import com.foreveross.vds.service.fms.mapper.FmsBmpMapper;
import com.foreveross.vds.service.fms.mapper.FmsExpenseAccountMapper;
import com.foreveross.vds.service.fms.service.ExpenseInvviceRelateService;
import com.foreveross.vds.util.exception.ServiceException;
import com.foreveross.vds.util.tools.AnalysisExcelUtils;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class ExpenseInvviceRelateServiceImpl implements ExpenseInvviceRelateService {
    @Autowired
    private ExpenseInvviceRelateMapper expenseInvviceRelateMapper;
    @Autowired
    private FmsStatusService fmsStatusService;
    @Autowired
    private LookupService lookupService;
    @Autowired
    private FmsExpenseAccountMapper fmsExpenseAccountMapper;
    @Autowired
    private FmsBmpMapper fmsBmpMapper;

    @Override
    public List<InvMidVatMain> queryInvMidVatMainByExpenseAccountId(Long expenseAccountId) {
        return expenseInvviceRelateMapper.queryInvMidVatMainByExpenseAccountId(expenseAccountId);
    }

    @Override
    @Transactional
    public RecRequest addFmsExpenseInvoiceRelate(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException {
        // TODO Auto-generated method stub
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);

        if (StringUtils.isEmpty(fmsExpenseAccount.getInvoiceIdArrayStr())) {
            recRequest.setReCode(1);
            recRequest.setMessage("请选择要关联的票号");
            return recRequest;
        }

        String[] invoiceIdArray = fmsExpenseAccount.getInvoiceIdArrayStr().split(",");

        for (String invoiceId : invoiceIdArray) {
            FmsExpenseInvoiceRelate fmsExpenseInvoiceRelate = new FmsExpenseInvoiceRelate();
            fmsExpenseInvoiceRelate.setExpenseAccountId(fmsExpenseAccount.getExpenseAccountId());
            fmsExpenseInvoiceRelate.setInvoiceId(Long.valueOf(invoiceId));
            fmsExpenseInvoiceRelate.setCreatedBy(fmsExpenseAccount.getUserId());
            fmsExpenseInvoiceRelate.setCreationDate(new Date());
            fmsExpenseInvoiceRelate.setLastUpdatedBy(fmsExpenseAccount.getUserId());
            fmsExpenseInvoiceRelate.setLastUpdateDate(new Date());
            fmsExpenseInvoiceRelate.setLastUpdateLogin(fmsExpenseAccount.getSessionId());

            List<FmsExpenseAccount> list = expenseInvviceRelateMapper.selectFee(fmsExpenseInvoiceRelate);
            if (list.get(0).getTotalFee() == null) {
                throw new BusinessServiceException("报销单号金额有误，请联系系统管理员！");
            }
            BigDecimal invoiceFee = list.get(0).getInvoiceFee() == null ? BigDecimal.valueOf(0) : list.get(0).getInvoiceFee();
            BigDecimal associatedFee = list.get(0).getAssociatedFee() == null ? BigDecimal.valueOf(0) : list.get(0).getAssociatedFee();

            if (list.get(0).getTotalFee().compareTo(invoiceFee.add(associatedFee)) == -1) {
                throw new BusinessServiceException("关联的发票金额超过报销单号总金额!");
            }


            expenseInvviceRelateMapper.addFmsExpenseInvoiceRelate(fmsExpenseInvoiceRelate);

            InvMidVatMain invMidVatMain = new InvMidVatMain();
            invMidVatMain.setId(Long.valueOf(invoiceId));
            invMidVatMain.setBindFlag("Y");
            invMidVatMain.setUpdateBy(fmsExpenseAccount.getUserId().toString());
            expenseInvviceRelateMapper.updateInvoiceBindFlag(invMidVatMain);
        }

        return recRequest;
    }

    @Override
    public Page<InvMidVatMain> queryInvMidVatMainPage(InvMidVatMain invMidVatMain) {
        PageHelper.startPage(invMidVatMain.getPage(), invMidVatMain.getRows());
        return (Page<InvMidVatMain>) expenseInvviceRelateMapper.queryInvMidVatMain(invMidVatMain);
    }

    @Override
    public List<InvMidVatMain> queryInvMidVatMain(InvMidVatMain invMidVatMain) {
        return expenseInvviceRelateMapper.queryInvMidVatMain(invMidVatMain);
    }

    @Override
    public RecRequest submitFmsExpenseInvoiceRelate(FmsExpenseAccount fmsExpenseAccount)
            throws BusinessServiceException {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);

        Long approvaled = lookupService.getLookupIdByCode("SETTLE_STATUS", "APPROVALED");
        Long relateRejact = lookupService.getLookupIdByCode("SETTLE_STATUS", "RELATE_REJECT");
        fmsExpenseAccount = fmsExpenseAccountMapper.getFmsExpenseAccountById(fmsExpenseAccount.getExpenseAccountId());

        if (!fmsExpenseAccount.getSettledStatus().equals(approvaled) && !fmsExpenseAccount.getSettledStatus().equals(relateRejact)) {
            recRequest.setReCode(1);
            recRequest.setMessage("不能提交，流程未审批完");
            return recRequest;
        }
        //验证金额
        FmsExpenseAccount entity = this.fmsExpenseAccountMapper.getFeeInfo(fmsExpenseAccount);

        if (!entity.getTotalFee().equals(entity.getInvoiceFee())) {
            recRequest.setReCode(1);
            recRequest.setMessage("发票金额与报销单号金额不相等，提交失败");
            return recRequest;
        }


        //根据报销单ID获取报销单号结算批次关联数据
        FmsProApprovalRecord fmsProApprovalRecord = new FmsProApprovalRecord();
        fmsProApprovalRecord.setExpenseAccountId(fmsExpenseAccount.getExpenseAccountId());
        String feeType = this.fmsBmpMapper.getFeeType(fmsProApprovalRecord);

        if ("TRANSPORT_FEE".equals(feeType)) {
            //报销为运输费用
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "RELATED", "TRANSPORT", -1L, "-1");
        } else if ("STORAGE_FEE".equals(feeType)) {
            //报销为仓储费用
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "RELATED", "STORAGE", -1L, "-1");
        } else if ("MANUAL_FEE".equals(feeType)) {
            //报销为手工结算
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "RELATED", "REFUND", -1L, "-1");
        } else if ("REFUND_FEE".equals(feeType)) {
            //报销为差价费用
            fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), "RELATED", "MANUAL", -1L, "-1");
        }

        return recRequest;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecRequest auditFmsExpenseInvoiceRelate(FmsExpenseAccount fmsExpenseAccount)
            throws BusinessServiceException {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);

        String[] ids = fmsExpenseAccount.getIdsStr().split(",");

        //根据报销单ID获取报销单号结算批次关联数据
        String status;

        for (String id : ids) {
            if ("reject".equals(fmsExpenseAccount.getOper())) {
                status = "RELATE_REJECT";
            } else {
                status = "POSTED";
            }
            FmsProApprovalRecord fmsProApprovalRecord = new FmsProApprovalRecord();
            fmsProApprovalRecord.setExpenseAccountId(Long.valueOf(id));
            String feeType = this.fmsBmpMapper.getFeeType(fmsProApprovalRecord);
            if ("TRANSPORT_FEE".equals(feeType)) {
                //报销为运输费用
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), status, "TRANSPORT", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
            } else if ("STORAGE_FEE".equals(feeType)) {
                //报销为仓储费用
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), status, "STORAGE", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
            } else if ("MANUAL_FEE".equals(feeType)) {
                //报销为手工结算
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), status, "REFUND", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
            } else if ("REFUND_FEE".equals(feeType)) {
                //报销为差价费用
                fmsStatusService.updateExpenseSettleStatus(fmsProApprovalRecord.getExpenseAccountId(), status, "MANUAL", fmsExpenseAccount.getUserId(), fmsExpenseAccount.getSessionId());
            }

            FmsExpenseAccount editFmsExpenseAccount = new FmsExpenseAccount();
            editFmsExpenseAccount.setApprovalResult(fmsExpenseAccount.getApprovalResult());
            if ("reject".equals(fmsExpenseAccount.getOper())) {
                editFmsExpenseAccount.setRejectReason(fmsExpenseAccount.getApprovalResult());
            } else {
                editFmsExpenseAccount.setRejectReason("通过");
            }
            editFmsExpenseAccount.setRejectReason(fmsExpenseAccount.getApprovalResult());
            editFmsExpenseAccount.setExpenseAccountId(Long.valueOf(id));
            editFmsExpenseAccount.setLastUpdatedBy(fmsExpenseAccount.getUserId());
            editFmsExpenseAccount.setLastUpdateLogin(fmsExpenseAccount.getSessionId());
            fmsExpenseAccountMapper.updateFmsExpenseAccountRejectReason(editFmsExpenseAccount);
        }

        return recRequest;
    }

	@Override
	public List<InvMidVatMain> importInvmidVatmain(Workbook workbook) {
		
		boolean flag = true;
		String message = "";
		Sheet sheet = workbook.getSheetAt(0);
        Cursor cursor = new Cursor(sheet, 0);
        
        List<String> list = null;
		if (flag) {
			list = new ArrayList<String>();
			list.add("");
			list.add("");
			list.add("");
			list.add("invNum");
		}
		
		// 解析数据
		List<InvMidVatMain> invMidVatMainList = null;
		if (flag) {
			invMidVatMainList = new ArrayList<>();
			for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				if (row != null) {
					InvMidVatMain invMidVatMain = null;
					try {
						invMidVatMain = AnalysisExcelUtils.getValue1(row, list.size(), InvMidVatMain.class, list);
					} catch (Exception e) {
						flag = false;
						message = String.format("Sheet:%s 行:%s 解析数据失败", cursor.getSheetIndex(), rowNum);
						e.printStackTrace();
						break;
					}
					invMidVatMainList.add(invMidVatMain);
				}
			}
			
			if (invMidVatMainList.size() == 0) {
				flag = false;
				message = "未解析到数据，请检查导入文件";
			}
		}
		
		List<InvMidVatMain> result = null;
		if (flag) {
			result = new ArrayList<>();
			Map<String, InvMidVatMain> map = new HashMap<>();
			for (InvMidVatMain item : invMidVatMainList) {
				if (map.containsKey(item.getInvNum())) {
					continue;
				} else {
					map.put(item.getInvNum(), item);
				}
				List<InvMidVatMain> queryInvMidVatMain = this.queryInvMidVatMain(item);
				if (AnalyzerTool.isEmpty(queryInvMidVatMain)) {
					throw new ServiceException("0", String.format("发票号：%s，没有查询到数据", item.getInvNum()));
				}
				result.addAll(queryInvMidVatMain);
			}
		}
		
		if (!flag) {
			throw new ServiceException("0", message);
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delRelatedInvoice(List<InvMidVatMain> list) {
        for (InvMidVatMain invMidVatMain : list) {
            fmsExpenseAccountMapper.delRelateInvoice(invMidVatMain);
            fmsExpenseAccountMapper.unBindInvoice(invMidVatMain);
        }
    }
}
