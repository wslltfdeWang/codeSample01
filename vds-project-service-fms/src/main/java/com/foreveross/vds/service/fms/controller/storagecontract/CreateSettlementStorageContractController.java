package com.foreveross.vds.service.fms.controller.storagecontract;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettled;
import com.foreveross.vds.service.fms.service.FmsStorageContractService;
import com.foreveross.vds.service.fms.service.storagecontract.CreateSettlementStorageService;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.github.pagehelper.Page;

import net.sf.json.JSONArray;

/**
 * 生成仓储结算数据控制类
 * @author foreveross
 *
 */
@Controller
public class CreateSettlementStorageContractController {
	@Autowired
	private CreateSettlementStorageService createSettlementStorageService;
	@Autowired
	private FmsStorageContractService fmsStorageContractService;
	/**
	 * 分页查询仓储合同结算数据
	 * @param fmsStorageSettled
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value = "query_create_storage_contract_page")
	public BasePageResponse<?> queryAuditStorageContractPage(@RequestBody FmsStorageSettled fmsStorageSettled) {
		BasePageResponse<FmsStorageSettled> basePageResponse = new BasePageResponse<FmsStorageSettled>();
		
		Page<FmsStorageSettled> page = createSettlementStorageService.queryFmsStorageSettledByPage(fmsStorageSettled);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	/**
	 * 生成仓储合同结算数
	 * @param fmsStorageSettledStr
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value = "createing_settlement_storage_contract")
	public RecRequest createingSettlementStorageContract(@RequestBody String fmsStorageSettledStr){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		JSONArray jsonArray = JSONArray.fromObject(fmsStorageSettledStr);
//		List<FmsStorageSettled> list = (List) JSONArray.toCollection(jsonArray,FmsStorageSettled.class);
		List<FmsStorageSettled> list = JSON.parseArray(fmsStorageSettledStr,FmsStorageSettled.class);
		if(list != null && list.size() > 0) {
			List<FmsStorageSettled> insertList = new ArrayList<FmsStorageSettled>();
			for(int i=0;i<list.size();i++) {
				FmsStorageSettled fmsStorageSettled = list.get(i);
				
				FmsStorageContract fmsStorageContract = fmsStorageContractService.selectByPrimaryKey(fmsStorageSettled.getContractId());
				//如果结算时间段大于合同有效时间段，生成数据失败
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				if(fmsStorageContract != null) {
					
					if(Integer.parseInt(sdf.format(fmsStorageContract.getStartDate())) 
								> Integer.parseInt(sdf.format(fmsStorageSettled.getStartDate()))) {
						recRequest.setReCode(1);
						recRequest.setMessage("生成失败：结算开始时早于合同开始时间");
						return recRequest;
					}
					
					if(Integer.parseInt(sdf.format(fmsStorageContract.getEndDate()))
								< Integer.parseInt(sdf.format(fmsStorageSettled.getEndDate()))) {
						recRequest.setReCode(1);
						recRequest.setMessage("生成失败：结算结束时间晚于合同结束时间");
						return recRequest;
					}
				}
				
				List<FmsStorageSettled> chkList = createSettlementStorageService.queryFmsStorageSettled(fmsStorageSettled);
				
				if(chkList != null && chkList.size() > 0) {
					recRequest.setReCode(1);
					recRequest.setMessage("生成失败：结算时间段内已存在结算数据");
					return recRequest;
				}
				
				BigDecimal amount = createSettlementStorageService.getAmount(fmsStorageSettled, fmsStorageContract);
				
				fmsStorageSettled.setAmount(amount);
				fmsStorageSettled.setSettledFlag("N");
				fmsStorageSettled.setCreatedBy(1L);
				fmsStorageSettled.setCreationDate(new Date());
				
				insertList.add(fmsStorageSettled);
				
			}
			
			for(int i=0;i<insertList.size();i++) {
				try {
					createSettlementStorageService.addFmsStorageSettled(insertList.get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					recRequest.setReCode(1);
					recRequest.setMessage("生成失败");
					return recRequest;
				}
			}
		}
		
		return recRequest;
	}
}
