package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.dto.fms.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface FmsExpenseAccountMapper {
    /**
     * 添加报销单号表
     *
     * @param fmsExpenseAccount
     * @return
     */
    void addFmsExpenseAccount(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 查询报销单号表
     *
     * @param fmsExpenseAccount
     * @return
     */
    List<FmsExpenseAccount> queryFmsExpenseAccountList(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 根据流程ID查询报销单号
     *
     * @param proInstId
     * @return
     */
    FmsExpenseAccount getFmsExpenseAccountByProInstId(String proInstId);

    /**
     * 添加报销单费用明细
     *
     * @param fmsExpenseDetail
     */
    void addFmsExpenseDetail(FmsExpenseDetail fmsExpenseDetail);

    /**
     * 添加报销单合同明细
     *
     * @param fmsExpenseContractDetail
     */
    void addFmsExpenseContractDetail(FmsExpenseContractDetail fmsExpenseContractDetail);

    /**
     * 根据报销单ID获取报销单费用明细
     *
     * @param expenseAccountId
     * @return
     */
    List<FmsExpenseDetail> queryFmsExpenseDetailByExpenseAccountId(Long expenseAccountId);

    /**
     * 根据报销单ID获取报销单费用明细
     *
     * @param expenseAccountId
     * @return
     */
    List<FmsExpenseContractDetail> queryFmsExpenseContractDetailByExpenseAccountId(Long expenseAccountId);

    /**
     * 根据合同ID和合同类型获取合同信息
     *
     * @param map
     * @return
     */
    Contarct getContByIdAndLookupId(Map<String, Long> map);

    /**
     * 根据ID获取报销单号
     *
     * @param expenseAccountId
     * @return
     */
    FmsExpenseAccount getFmsExpenseAccountById(Long expenseAccountId);

    /**
     * 修改报销单审批意见
     *
     * @param fmsExpenseAccount
     */
    void updateFmsExpenseAccountRejectReason(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 验证报销单金额
     *
     * @param fmsExpenseAccount
     */
    FmsExpenseAccount getFeeInfo(FmsExpenseAccount fmsExpenseAccount);


    /**
     * 删除关联发票数据
     *
     * @param invMidVatMain
     */
    void delRelateInvoice(InvMidVatMain invMidVatMain);

    /**
     * 取消发票关联
     *
     * @param invMidVatMain
     */
    void unBindInvoice(InvMidVatMain invMidVatMain);
}
