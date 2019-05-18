package com.foreveross.vds.service.fms.controller.storagecontract;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledV;
import com.foreveross.vds.service.fms.service.storagecontract.SettlementStorageContractService;
import com.github.pagehelper.Page;

import net.sf.json.JSONArray;
/**
 * 根据仓储结算数据生成批次
 * @author foreveross
 *
 */
@Controller
public class SettlementStorageContractController{
	
	@Autowired
	private SettlementStorageContractService settlementStorageContractService;
	
	/**
	 * 分页查询仓储合同结算数据
	 * @param page
	 * @param rows
	 * @param tmsLogisticsProviders
	 * @return
	 * @throws Exception 
	 * @throws DAOException 
	 */
	@ResponseBody
	@RequestMapping(value = "query_settlement_storage_contract_page")
	public BasePageResponse<?> querySettlementStorageContractPage(@RequestBody FmsStorageSettledV fmsStorageSettledV) throws Exception {
		BasePageResponse basePageResponse = new BasePageResponse();
		
		Page<FmsStorageSettledV> page = settlementStorageContractService.querySettlementStorageContractByPage(fmsStorageSettledV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 查询仓储合同结算数据
	 * @param page
	 * @param rows
	 * @param tmsLogisticsProviders
	 * @return
	 * @throws Exception 
	 * @throws DAOException 
	 */
	@ResponseBody
	@RequestMapping(value = "query_settlement_storage_contract")
	public List<FmsStorageSettledV> querySettlementStorageContract(@RequestBody FmsStorageSettledV fmsStorageSettledV) throws Exception {
		
		return settlementStorageContractService.querySettlementStorageContract(fmsStorageSettledV);
	}
	
	/**
	 * 生成仓储合同结算数据批次
	 * @param page
	 * @param rows
	 * @param tmsLogisticsProviders
	 * @return
	 * @throws Exception 
	 * @throws DAOException 
	 */
	@ResponseBody
	@RequestMapping(value = "create_settlement_storage_contract")
	public RecRequest createSettlementStorageContract(@RequestBody String fmsStorageSettledVStr){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		JSONArray jsonArray = JSONArray.fromObject(fmsStorageSettledVStr);
		List<FmsStorageSettledV> list = (List<FmsStorageSettledV>) JSONArray.toCollection(jsonArray,FmsStorageSettledV.class);
		
		if(list != null && list.size() > 0) {
			FmsStorageSettledV fmsStorageSettledV = list.get(0);
			
			for(int i=0;i<list.size();i++) {
				FmsStorageSettledV ChkFmsStorageSettledV = list.get(i);
				//判断勾选的所有仓储合同结算数据合同号
				if(fmsStorageSettledV.getContractnumber() != null
						&& !fmsStorageSettledV.getContractnumber().equals(ChkFmsStorageSettledV.getContractnumber())) {
					recRequest.setReCode(1);
					recRequest.setMessage("生成失败：勾选的数据存在不同合同数据");
					return recRequest;
				}
				//判断勾选的所有仓储合同结算数据所属基地是否相同
				if(fmsStorageSettledV.getStartpointid().longValue() != ChkFmsStorageSettledV.getStartpointid().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("生成失败：勾选的数据存在不同所属基地数据");
					return recRequest;
				}
			}
		}
		
		try {
			settlementStorageContractService.create(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("生成失败");
		}
		
		return recRequest;
	}
}
