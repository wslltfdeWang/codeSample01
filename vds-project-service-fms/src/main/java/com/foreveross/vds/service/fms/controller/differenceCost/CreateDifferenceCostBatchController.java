package com.foreveross.vds.service.fms.controller.differenceCost;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.DifferenceCostRequest;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettled;
import com.foreveross.vds.service.fms.service.differenceCost.CreateDifferenceCostBatchService;
import com.github.pagehelper.Page;

/**
 * 生成差价结算批次控制类
 * @author foreveross
 *
 */
@Controller
public class CreateDifferenceCostBatchController {
	@Autowired
	private CreateDifferenceCostBatchService createDifferenceCostBatchService;
	
	/**
	 * 分页查询差价结算数据
	 * @param fmsRefundSettled
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_refund_settled_page")
	public BasePageResponse<FmsRefundSettled> queryRefundSettledPage(@RequestBody FmsRefundSettled fmsRefundSettled) {
		
		BasePageResponse<FmsRefundSettled> basePageResponse = new BasePageResponse<FmsRefundSettled>();
		
		Page<FmsRefundSettled> page = createDifferenceCostBatchService.queryFmsRefundSettledByPage(fmsRefundSettled);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	/**
	 * 生成差价结算批次
	 * @param differenceCostRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "create_refund_settled")
	public RecRequest createRefundSettled(@RequestBody DifferenceCostRequest differenceCostRequest) {
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			createDifferenceCostBatchService.createDifferenceCostBatch(differenceCostRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("生成结算批次失败，请联系管理员");
		}
		
		return recRequest;
	}
}
