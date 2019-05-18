package com.foreveross.vds.service.fms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.DetailPageRequest;
import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.service.fms.service.FmsTransportSettledLineVService;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;
import com.foreveross.vds.vo.fms.FmsTransportSettledLine;
import com.foreveross.vds.vo.fms.FmsTransportSettledLineV;

/**
 * @author luochong
 * @date 2018年8月8日 下午2:16:18
 * 
 * 
 */
@Controller
@RequestMapping("/fms/transport_settled_line_v")
public class FmsTransportSettledLineVController {

	@Autowired
	private FmsTransportSettledLineVService fmsTransportSettledLineVService;
	
	@RequestMapping("/query_page")
	@ResponseBody
	public BaseResponse queryPage(
			@RequestBody DetailPageRequest<FmsTransportSettledHeader> detailPageRequest
			) {
		BasePageResponse<FmsTransportSettledLineV> queryPage = fmsTransportSettledLineVService.queryPage(detailPageRequest);
		return new DetailResponse<>(queryPage);
	}

	@RequestMapping("/query_transport_settled_line_totalfee")
	@ResponseBody
	public BaseResponse queryTransportSettledLineTotalfee(
			@RequestBody Long[] transportSettledHeaderIds
	) {
		if(transportSettledHeaderIds == null || transportSettledHeaderIds.length ==0){
			return new DetailResponse<>("0");
		}
		String totalFee = fmsTransportSettledLineVService.queryTransportSettledLineTotalfee(transportSettledHeaderIds);
		return new DetailResponse<>(totalFee);
	}
	
	@RequestMapping("/export")
	@ResponseBody
	public BaseResponse export(
			@RequestBody FmsTransportSettledHeader fmsTransportSettledHeader
			) {
		List<FmsTransportSettledLineV> queryList = fmsTransportSettledLineVService.queryList(fmsTransportSettledHeader);
		return new DetailResponse<>(queryList);
	}
	
	@RequestMapping("/print")
	@ResponseBody
	public BaseResponse print(
			@RequestBody FmsTransportSettledHeader fmsTransportSettledHeader
			) {
		List<TmsSendcarHeaderFmsE> list = fmsTransportSettledLineVService.print(fmsTransportSettledHeader);
		return new DetailResponse<>(list);
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public BaseResponse delete(
			@RequestBody FmsTransportSettledLine FmsTransportSettledLine
			) {
		fmsTransportSettledLineVService.delete(FmsTransportSettledLine);
		return new BaseResponse();
	}
}
