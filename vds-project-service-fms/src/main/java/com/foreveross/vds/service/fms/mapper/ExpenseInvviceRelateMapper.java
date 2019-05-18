package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsExpenseAccount;
import com.foreveross.vds.dto.fms.FmsExpenseInvoiceRelate;
import com.foreveross.vds.dto.fms.InvMidVatMain;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseInvviceRelateMapper {
    /**
     * 根据报销单ID获取发票信息
     *
     * @param expenseAccountId
     * @return
     */
    List<InvMidVatMain> queryInvMidVatMainByExpenseAccountId(Long expenseAccountId);

    /**
     * 添加票号关联关系
     *
     * @param fmsExpenseInvoiceRelate
     */
    void addFmsExpenseInvoiceRelate(FmsExpenseInvoiceRelate fmsExpenseInvoiceRelate);

    /**
     * 获取发票信息
     *
     * @return
     */
    List<InvMidVatMain> queryInvMidVatMain(InvMidVatMain invMidVatMain);

    void updateInvoiceBindFlag(InvMidVatMain invMidVatMain);

    List<FmsExpenseAccount> selectFee(FmsExpenseInvoiceRelate fmsExpenseInvoiceRelate);
}
