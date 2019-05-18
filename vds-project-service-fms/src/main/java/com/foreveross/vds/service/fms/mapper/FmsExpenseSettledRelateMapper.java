package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsExpenseSettledRelate;

public interface FmsExpenseSettledRelateMapper {
	/**
	 * 批量添加报销单号结算批次关联表
	 * @param list
	 */
	void addFmsExpenseSettledRelateByList(List<FmsExpenseSettledRelate> list);
	
	List<FmsExpenseSettledRelate> queryFmsExpenseSettledRelateList(FmsExpenseSettledRelate fmsExpenseSettledRelate);
}
