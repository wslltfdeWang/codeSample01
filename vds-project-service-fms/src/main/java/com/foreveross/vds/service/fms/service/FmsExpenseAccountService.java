package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.fms.Contarct;
import com.foreveross.vds.dto.fms.FmsExpenseAccount;
import com.foreveross.vds.dto.fms.FmsExpenseContractDetail;
import com.foreveross.vds.dto.fms.FmsExpenseDetail;
import com.github.pagehelper.Page;

public interface FmsExpenseAccountService {
	/**
	 * 添加报销单号表
	 * @param fmsExpenseAccount
	 * @return
	 */
	FmsExpenseAccount addFmsExpenseAccount(FmsExpenseAccount fmsExpenseAccount);
	/**
	 * 分页查询报销单号表
	 * @param fmsExpenseAccount
	 * @return
	 */
	Page<FmsExpenseAccount> queryFmsExpenseAccountByPage(FmsExpenseAccount fmsExpenseAccount);
	/**
	 * 根据流程ID查询报销单号
	 * @param proInstId
	 * @return
	 */
	FmsExpenseAccount getFmsExpenseAccountByProInstId(String proInstId);
	/**
	 * 添加报销单费用明细
	 * @param fmsExpenseDetail
	 */
	void addFmsExpenseDetail(FmsExpenseDetail fmsExpenseDetail);

    void addFmsExpenseContractDetail(FmsExpenseContractDetail fmsExpenseContractDetail);

    /**
	 * 根据报销单ID获取报销单费用明细
	 * @param expenseAccountId
	 * @return
	 */
	List<FmsExpenseDetail> queryFmsExpenseDetailByExpenseAccountId(Long expenseAccountId);

    List<FmsExpenseContractDetail> queryFmsContractExpenseDetailByExpenseAccountId(Long expenseAccountId);

    /**
	 * 根据合同ID和合同类型获取合同信息
	 * @param map
	 * @return
	 */
	Contarct getContByIdAndLookupId(Long contId,Long lookupId);
	/**
	 * 根据ID获取报销单号
	 * @param expenseAccountId
	 * @return
	 */
	FmsExpenseAccount getFmsExpenseAccountById(Long expenseAccountId);
}
