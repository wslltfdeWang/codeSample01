package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.dto.fms.FmsBmpV;
import com.foreveross.vds.dto.fms.FmsExpenseAccount;
import com.foreveross.vds.dto.fms.FmsExpenseSettledRelateV;
import com.foreveross.vds.dto.fms.FmsProApprovalRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Villy
 */
@Repository
public interface FmsBmpMapper {
    /**
     * 查询费用批次审批流程发起视图
     *
     * @param fmsBmpV
     * @return
     */
    List<FmsBmpV> queryFmsBmpVList(FmsBmpV fmsBmpV);

    /**
     * 查询费用流程审核时报销单号结算批次关联表视图
     *
     * @param proInstId
     * @return
     */
    List<FmsExpenseSettledRelateV> queryFmsExpenseSettledRelateVList(String proInstId);

    /**
     * 驳回或通过费用审批流程
     *
     * @param map
     */
    void rejOrAdoptFmsFlow(Map<String, String> map);

    /**
     * 根据流程ID获取审批记录
     *
     * @param proInstId
     * @return
     */
    List<FmsProApprovalRecord> queryFmsProApprovalRecordByProInstId(String proInstId);

    /**
     * 根据报销单号查询结算状态
     *
     * @param fmsExpenseAccount
     * @return
     */
    List<FmsExpenseAccount> getStatusByExpenseNumber(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 添加审批记录
     *
     * @param fmsProApprovalRecord
     */
    void addFmsProApprovalRecord(FmsProApprovalRecord fmsProApprovalRecord);

    /**
     * 获取当天报销单序号
     *
     * @return
     */
    String getExpenseAccountNumber();

    /**
     * 根据报销单号ID获取是否挂账
     * @param fmsExpenseAccount
     * @return
     */
    String isRelateConfirm(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 根据报销单ID获取审批记录
     *
     * @param expenseAccountId
     * @return
     */
    List<FmsProApprovalRecord> queryFmsProApprovalRecordByExpenseAccountId(Long expenseAccountId);

    /**
     * 根据报销单ID获取费用类型
     *
     * @param fmsProApprovalRecord
     * @return
     */
    String getFeeType(FmsProApprovalRecord fmsProApprovalRecord);

    /**
     * 失效报销单号数据
     *
     * @param fmsExpenseAccount
     * @return
     */
    int disableExpenseAccount(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 失效报销单号数据
     *
     * @param fmsExpenseAccount
     * @return
     */
    int disableExpenseAccountLine(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 失效报销单号数据
     *
     * @param fmsExpenseAccount
     * @return
     */
    int unBindInvoice(FmsExpenseAccount fmsExpenseAccount);

    /**
     * 失效报销单号数据
     *
     * @param fmsExpenseAccount
     * @return
     */
    int deleteInvoiceRelate(FmsExpenseAccount fmsExpenseAccount);
} 
