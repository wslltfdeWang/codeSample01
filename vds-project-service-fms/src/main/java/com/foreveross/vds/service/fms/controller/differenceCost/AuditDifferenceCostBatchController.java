package com.foreveross.vds.service.fms.controller.differenceCost;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledLine;
import com.foreveross.vds.service.fms.service.differenceCost.AuditDifferenceCostBatchService;
import com.github.pagehelper.Page;

@Controller
public class AuditDifferenceCostBatchController {
	@Autowired
	private AuditDifferenceCostBatchService auditDifferenceCostBatchService;
	/**
	 * 分页查询差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_audit_difference_page")
	public BasePageResponse<FmsRefundSettledHeader> queryAuditDifferencePage(@RequestBody FmsRefundSettledHeader fmsRefundSettledHeader) {
		
		BasePageResponse<FmsRefundSettledHeader> basePageResponse = new BasePageResponse<FmsRefundSettledHeader>();
		
		Page<FmsRefundSettledHeader> page = auditDifferenceCostBatchService.queryFmsRefundSettledHeaderByPage(fmsRefundSettledHeader);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 根据差价结算批次头ID查询差价结算批次行数据
	 * @param refundSettledHeaderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_difference_line")
	public BasePageResponse<FmsRefundSettledLine> queryFmsRefundSettledLine(@RequestBody Long refundSettledHeaderId) {
		
		BasePageResponse<FmsRefundSettledLine> basePageResponse = new BasePageResponse<FmsRefundSettledLine>();
		FmsRefundSettledLine fmsRefundSettledLine = new FmsRefundSettledLine();
		fmsRefundSettledLine.setRefundSettledHeaderId(refundSettledHeaderId);
		List<FmsRefundSettledLine> list = auditDifferenceCostBatchService.queryFmsRefundSettledLine(fmsRefundSettledLine);
		
		
		
		basePageResponse.setRows(list);
		basePageResponse.setTotal(Long.valueOf(list.size()));
		return basePageResponse;
	}
	/**
	 * 审核差价结算批次行数据
	 * @param fmsRefundSettledLine
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "audit_difference_line")
	public RecRequest auditFmsRefundSettledLine(@RequestBody FmsRefundSettledLine fmsRefundSettledLine) {
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		try {
			auditDifferenceCostBatchService.auditFmsRefundSettledLine(fmsRefundSettledLine);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("差价结算批次行审核失败，请联系管理员");
		}
		return recRequest;
	}
	
	/**
	 * 审核差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "audit_difference_header")
	public RecRequest auditFmsRefundSettledHeader(@RequestBody FmsRefundSettledHeader fmsRefundSettledHeader) {
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		try {
			auditDifferenceCostBatchService.auditFmsRefundSettledHeader(fmsRefundSettledHeader);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("差价结算批次头审核失败，请联系管理员");
		}
		return recRequest;
	}
}
