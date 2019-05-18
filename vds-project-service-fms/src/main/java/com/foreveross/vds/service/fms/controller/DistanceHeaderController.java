package com.foreveross.vds.service.fms.controller;

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
import com.foreveross.vds.dto.fms.DistanceHeaderRequest;
import com.foreveross.vds.service.fms.service.DistanceHeaderService;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.foreveross.vds.vo.fms.FmsDistanceHeader;

/**
 * 预算-运距头表接口
 */
@Controller
@RequestMapping("/fms/distance/header")
public class DistanceHeaderController {

	@Autowired
	private DistanceHeaderService distanceHeaderService;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse save(@RequestBody DistanceHeaderRequest distanceHeaderRequest){
		
		boolean flag = true;
		String error = "";
		if (flag) {
			if (AnalyzerTool.isEmpty(
					distanceHeaderRequest.getDistanceCode(),
					distanceHeaderRequest.getDistanceName()
					)) {
				flag = false;
				error = "必传参数不能为空";
			}
		}
		
		int save = 0;
		if (flag) {
			save = distanceHeaderService.save(distanceHeaderRequest);
		}
		
		if (flag) {
			return new DetailResponse<>(save);
		} else {
			return new DetailResponse<>(0, error);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse delete(@RequestBody FmsDistanceHeader fmsDistanceHeader) {
		int delete = distanceHeaderService.deleteByPrimaryKey(fmsDistanceHeader.getDistanceHeaderId());
		return new DetailResponse<>(delete);
	}
	
	@RequestMapping(value = "/query_list", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse queryList(@RequestBody BasePageRequest basePageRequest) {
		BasePageResponse<FmsDistanceHeader> queryPage = distanceHeaderService.queryPage(basePageRequest);
		return new DetailResponse<>(queryPage);
	}
}
