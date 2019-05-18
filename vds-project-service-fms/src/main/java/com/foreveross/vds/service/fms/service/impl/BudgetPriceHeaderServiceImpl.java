package com.foreveross.vds.service.fms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.fms.BudgetPriceHeaderRequest;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.service.BudgetPriceHeaderService;
import com.foreveross.vds.service.fms.service.BudgetPriceLineService;
import com.foreveross.vds.util.exception.ServiceException;
import com.foreveross.vds.vo.fms.FmsBudgetPriceHeader;
import com.foreveross.vds.vo.fms.FmsBudgetPriceLine;

@Service
public class BudgetPriceHeaderServiceImpl extends BaseServiceImpl<FmsBudgetPriceHeader, Long> implements BudgetPriceHeaderService {

	@Autowired
	private BudgetPriceLineService budgetPriceLineService;
	
	public BudgetPriceHeaderServiceImpl() {
		super("budgetPriceHeaderId");
	}
	
	@Override
	@Transactional
	public int save(BudgetPriceHeaderRequest budgetPriceHeaderRequest) {
		int save = super.quickSave(budgetPriceHeaderRequest);
		Long[] budgetPriceLines = budgetPriceHeaderRequest.getBudgetPriceLines();
		if (budgetPriceLines != null && budgetPriceLines.length > 0) {
			for (Long budgetPriceLine : budgetPriceLines) {
				FmsBudgetPriceLine fmsBudgetPriceLine = budgetPriceLineService.selectByPrimaryKey(budgetPriceLine);
				if (fmsBudgetPriceLine == null) {
					log.info(String.format("budgetPriceLine:%s 未查询到数据", budgetPriceLine));
					throw new ServiceException("0", "未查询到预算单价行表");
				}
				fmsBudgetPriceLine.setBudgetPriceLineId(null);
				fmsBudgetPriceLine.setBudgetPriceHeaderId(budgetPriceHeaderRequest.getBudgetPriceHeaderId());
				save += budgetPriceLineService.insertSelective(fmsBudgetPriceLine);
			}
		}
		return save;
	}
}
