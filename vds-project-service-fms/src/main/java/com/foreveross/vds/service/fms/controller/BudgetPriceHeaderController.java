package com.foreveross.vds.service.fms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageRequest;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.BudgetPriceHeaderRequest;
import com.foreveross.vds.service.fms.service.BudgetPriceHeaderService;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.foreveross.vds.vo.fms.FmsBudgetPriceHeader;

/**
 * 预算-单价头表接口
 */
@Controller
@RequestMapping("/fms/budget_price/header")
public class BudgetPriceHeaderController {

	@Autowired
	private BudgetPriceHeaderService budgetPriceHeaderService;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse save(@RequestBody BudgetPriceHeaderRequest BudgetPriceHeaderRequest){
		
		boolean flag = true;
		String error = "";
		if (flag) {
			if (AnalyzerTool.isEmpty(
					BudgetPriceHeaderRequest.getVersionCode(),
					BudgetPriceHeaderRequest.getVersionName()
					)) {
				flag = false;
				error = "必传参数不能为空";
			}
		}
		
		if (flag) {
			FmsBudgetPriceHeader params = new FmsBudgetPriceHeader();
			params.setVersionCode(BudgetPriceHeaderRequest.getVersionCode());
			List<FmsBudgetPriceHeader> queryList = budgetPriceHeaderService.queryList(params);
			if (AnalyzerTool.isNotEmpty(queryList)) {
				flag = false;
				error = "新版本代码不能重复";
			}
		}
		
		if (flag) {
			FmsBudgetPriceHeader params = new FmsBudgetPriceHeader();
			params.setVersionName(BudgetPriceHeaderRequest.getVersionName());
			List<FmsBudgetPriceHeader> queryList = budgetPriceHeaderService.queryList(params);
			if (AnalyzerTool.isNotEmpty(queryList)) {
				flag = false;
				error = "新版本名称不能重复";
			}
		}
		
		int save = 0;
		if (flag) {
			save = budgetPriceHeaderService.save(BudgetPriceHeaderRequest);
		}
		
		if (flag) {
			return new DetailResponse<>(save);
		} else {
			return new DetailResponse<>(0, error);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse delete(@RequestBody FmsBudgetPriceHeader fmsBudgetPriceHeader) {
		int delete = budgetPriceHeaderService.deleteByPrimaryKey(fmsBudgetPriceHeader.getBudgetPriceHeaderId());
		return new DetailResponse<>(delete);
	}
	
	@RequestMapping(value = "/query_list", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse queryList(@RequestBody BasePageRequest basePageRequest) {
		BasePageResponse<FmsBudgetPriceHeader> queryPage = budgetPriceHeaderService.queryPage(basePageRequest);
		return new DetailResponse<>(queryPage);
	}
}
