package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsExpenseSettledRelate;

public interface FmsExpenseSettledRelateService {
	/**
	 * 批量添加报销单号结算批次关联表
	 * @param list
	 */
	void addFmsExpenseSettledRelateByList(List<FmsExpenseSettledRelate> list);
	List<FmsExpenseSettledRelate> queryFmsExpenseSettledRelateList(FmsExpenseSettledRelate fmsExpenseSettledRelate);
}
