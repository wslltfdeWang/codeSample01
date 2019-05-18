package com.foreveross.vds.service.fms.controller.differenceCost;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.CreateDifferenceCostRequest;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledHeader;
import com.foreveross.vds.service.fms.service.differenceCost.DifferenceCostService;
import com.foreveross.vds.vo.fms.FmsTransportContractHeader;
import com.github.pagehelper.Page;

@Controller
public class DifferenceCostController {
	@Autowired
	private DifferenceCostService differenceCostService;
	/**
	 * 分页获取待生成差价的运输费用结算数据
	 * @param fmsTransportSettledHeader
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_to_difference_page")
	public BasePageResponse<FmsTransportSettledHeader> queryToDifferencePage(@RequestBody FmsTransportSettledHeader fmsTransportSettledHeader) {
		
		BasePageResponse<FmsTransportSettledHeader> basePageResponse = new BasePageResponse<FmsTransportSettledHeader>();
		
		Page<FmsTransportSettledHeader> page = differenceCostService.queryFmsTransportSettledHeaderToDifferenceByPage(fmsTransportSettledHeader);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 查询运输合同头列表
	 * @param fmsTransportContractHeader
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_transport_contract_header")
	public List<FmsTransportContractHeader> queryFmsTransportContractHeader(@RequestBody FmsTransportContractHeader fmsTransportContractHeader) {
		
		
		List<FmsTransportContractHeader> list = differenceCostService.queryFmsTransportContractHeader(fmsTransportContractHeader);
		
		return list;
	}
	
	/**
	 * 生成差价结算数据
	 * @param createDifferenceCostRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "create_difference")
	public RecRequest createDifference(@RequestBody CreateDifferenceCostRequest createDifferenceCostRequest) {
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
//			differenceCostService.addFmsRefundSettledByList(createDifferenceCostRequest);
			differenceCostService.addFmsRefundSettled(createDifferenceCostRequest.getFmsRefundSettled());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("生成差价结算数据失败，请联系管理员");
		}
		
		return recRequest;
	}
}
