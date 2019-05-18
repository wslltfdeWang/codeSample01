package com.foreveross.vds.service.fms.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsExpenseAccount;
import com.foreveross.vds.dto.fms.InvMidVatMain;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.github.pagehelper.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ExpenseInvviceRelateService {
	/**
	 * 根据报销单ID获取发票信息
	 * @param expenseAccountId
	 * @return
	 */
	List<InvMidVatMain> queryInvMidVatMainByExpenseAccountId(Long expenseAccountId);
	/**
	 * 添加票号关联关系
	 * @param fmsExpenseAccount
	 */
	RecRequest addFmsExpenseInvoiceRelate(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException;

	List<InvMidVatMain> queryInvMidVatMain(InvMidVatMain invMidVatMain);

	Page<InvMidVatMain> queryInvMidVatMainPage(InvMidVatMain invMidVatMain);
	/**
	 * 票号关联提交
	 * @param fmsExpenseAccount
	 */
	RecRequest submitFmsExpenseInvoiceRelate(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException;
	/**
	 * 票号关联复核
	 * @param fmsExpenseAccount
	 */
	RecRequest auditFmsExpenseInvoiceRelate(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException;
	
	List<InvMidVatMain> importInvmidVatmain(Workbook workbook);

    @Transactional(rollbackFor = Exception.class)
    void delRelatedInvoice(List<InvMidVatMain> list);
}
