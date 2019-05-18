package com.foreveross.vds.service.fms.service.impl;

import org.springframework.stereotype.Service;

import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.service.BudgetPriceLineService;
import com.foreveross.vds.vo.fms.FmsBudgetPriceLine;

@Service
public class BudgetPriceLineServiceImpl extends BaseServiceImpl<FmsBudgetPriceLine, Long> implements BudgetPriceLineService {

	public BudgetPriceLineServiceImpl() {
		super("budgetPriceLineId");
	}
	
}
