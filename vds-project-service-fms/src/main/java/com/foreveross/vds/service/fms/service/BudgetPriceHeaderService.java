package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.fms.BudgetPriceHeaderRequest;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsBudgetPriceHeader;

public interface BudgetPriceHeaderService extends BaseService<FmsBudgetPriceHeader, Long> {

	/**
	 * 根据历史数据保存
	 */
	int save(BudgetPriceHeaderRequest budgetPriceHeaderRequest);
	
}
