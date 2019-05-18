package com.foreveross.vds.service.fms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.BudgetPriceLineRequest;
import com.foreveross.vds.service.fms.service.BudgetPriceLineService;
import com.foreveross.vds.vo.fms.FmsBudgetPriceLine;

/**
 * 预算-单价行表接口
 */
@Controller
@RequestMapping("/fms/budget_price/line")
public class BudgetPriceLineController {
	
	@Autowired
	private BudgetPriceLineService budgetPriceLineServices;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse save(@RequestBody FmsBudgetPriceLine fmsBudgetPriceLine){
		int quickSave = budgetPriceLineServices.quickSave(fmsBudgetPriceLine);
		return new DetailResponse<>(quickSave);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse delete(@RequestBody FmsBudgetPriceLine fmsBudgetPriceLine) {
		int delete = budgetPriceLineServices.deleteByPrimaryKey(fmsBudgetPriceLine.getBudgetPriceLineId());
		return new DetailResponse<>(delete);
	}
	
	@RequestMapping(value = "/query_list", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse queryList(@RequestBody BudgetPriceLineRequest BudgetPriceLineRequest) {
		BasePageResponse<FmsBudgetPriceLine> queryPage = budgetPriceLineServices.queryPage(BudgetPriceLineRequest);
		return new DetailResponse<>(queryPage);
	}
}
