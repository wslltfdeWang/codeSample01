package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsBmpV;
import com.foreveross.vds.dto.fms.FmsExpenseAccount;
import com.foreveross.vds.dto.fms.FmsExpenseSettledRelateV;
import com.foreveross.vds.dto.fms.FmsProApprovalRecord;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.github.pagehelper.Page;
import org.apache.poi.ss.usermodel.Workbook;

public interface FmsBmpService {
	/**
	 * 分页查询费用批次审批流程发起视图
	 * @param fmsBmpV
	 * @return
	 */
	Page<FmsBmpV> queryFmsBmpVByPage(FmsBmpV fmsBmpV);

	List<FmsBmpV> queryFmsBmpV(FmsBmpV fmsBmpV);

    @CustTx
    RecRequest updateSettledStatus(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException;

    RecRequest startFmsFlow(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException;
	/**
	 * 查询费用流程审核时报销单号结算批次关联表视图
	 * @param proInstId
	 * @return
	 */
	List<FmsExpenseSettledRelateV> queryFmsExpenseSettledRelateVList(String proInstId);
	/**
	 * 驳回或通过费用审批流程
	 * @param proStatus 流程状态
	 * @param rejectReason 驳回原因
	 * @param proInstId 流程ID
	 */
	void rejOrAdoptFmsFlow(String proStatus,FmsProApprovalRecord fmsProApprovalRecord) throws BusinessServiceException;
	/**
	 * 根据流程ID获取审批记录
	 * @param proInstId
	 * @return
	 */
	List<FmsProApprovalRecord> queryFmsProApprovalRecordByProInstId(String proInstId);

    FmsExpenseAccount stopFmsFlow(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException;

    /**
	 * 添加审批记录
	 * @param fmsProApprovalRecord
	 */
	void addFmsProApprovalRecord(FmsProApprovalRecord fmsProApprovalRecord);
	/**
	 * 获取报销单号
	 * @return
	 */
	String getExpenseAccountNumber();

    String isRelateConfirm(FmsExpenseAccount fmsExpenseAccount) throws BusinessServiceException;

    /**
	 * 根据报销单ID获取审批记录
	 * @param expenseAccountId
	 * @return
	 */
	List<FmsProApprovalRecord> queryFmsProApprovalRecordByExpenseAccountId(Long expenseAccountId);

	/**
	 * 导出时翻译
	 * @param list
	 */
	void translate(List<FmsBmpV> list);

	/**
	 * 导入时获取数据
	 * @param userId
	 * @param sessionId
	 * @param workbook
	 * @return
	 */
	List<FmsBmpV> getImportFlowData(Long userId, String sessionId, Workbook workbook) throws BusinessServiceException;
}
