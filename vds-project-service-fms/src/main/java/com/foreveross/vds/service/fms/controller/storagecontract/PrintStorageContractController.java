package com.foreveross.vds.service.fms.controller.storagecontract;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeaderV;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLineV;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledV;
import com.foreveross.vds.dto.pms.TmsLogisticsMode;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledHeaderVService;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledLineVService;
import com.github.pagehelper.Page;

import net.sf.json.JSONObject;
/**
 * 打印仓储结算数据批次
 * @author foreveross
 *
 */
@Controller
public class PrintStorageContractController{
	
	@Autowired
	private FmsStorageSettledHeaderVService fmsStorageSettledHeaderVService;
	@Autowired
	private FmsStorageSettledLineVService fmsStorageSettledLineVService;
	
	/**
	 * 分页查询仓储合同结算头数据
	 * @param tmsLogisticsProviders
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_print_storage_contract_page")
	public BasePageResponse queryPrintStorageContractPage(@RequestBody FmsStorageSettledHeaderV fmsStorageSettledHeaderV) {
		BasePageResponse basePageResponse = new BasePageResponse();
		
		Page<FmsStorageSettledHeaderV> page = fmsStorageSettledHeaderVService.queryFmsStorageSettledHeaderVByPage(fmsStorageSettledHeaderV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 查询仓储合同结行视图
	 * @param tmsLogisticsProviders
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_storage_settled_line_page")
	public BasePageResponse queryFmsStorageSettledLineV(@RequestBody FmsStorageSettledLineV fmsStorageSettledLineV) {
		
		BasePageResponse basePageResponse = new BasePageResponse();
		
		Page<FmsStorageSettledLineV> page = fmsStorageSettledLineVService.queryFmsStorageSettledLineVByPage(fmsStorageSettledLineV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
}
