package com.foreveross.vds.service.fms.controller.manualclearing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.manualclearing.ContractAllV;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettled;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledV;
import com.foreveross.vds.service.fms.service.manualclearing.ManualSettledService;
import com.github.pagehelper.Page;
/**
 * 手工结算控制类
 * @author foreveross
 *
 */
@Controller
public class ManualSettledController {

	@Autowired
	private ManualSettledService manualSettledService;
	
	/**
	 * 分页查询手工结算数据视图
	 * @param fmsManualSettledV
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_manual_settled_page")
	public BasePageResponse<FmsManualSettledV> queryManualSettledPage(@RequestBody FmsManualSettledV fmsManualSettledV) {
		
		BasePageResponse<FmsManualSettledV> basePageResponse = new BasePageResponse<FmsManualSettledV>();
		
		Page<FmsManualSettledV> page = manualSettledService.queryFmsManualSettledVByPage(fmsManualSettledV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 分页查询仓储合同和运输合同视图
	 * @param fmsManualSettledV
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_contract_allv_page")
	public BasePageResponse<ContractAllV> queryContractAllVPage(@RequestBody ContractAllV contractAllV) {
		
		BasePageResponse<ContractAllV> basePageResponse = new BasePageResponse<ContractAllV>();
		
		Page<ContractAllV> page = manualSettledService.queryContractAllVPage(contractAllV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	/**
	 * 添加手工结算数据
	 * @param fmsManualSettled
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "add_manual_settled")
	public RecRequest addManualSettled(@RequestBody FmsManualSettled fmsManualSettled){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			
			if(manualSettledService.checkSettledName(fmsManualSettled)) {
				recRequest.setReCode(1);
				recRequest.setMessage("数据添加失败，结算名称已存在");
			}
			
			manualSettledService.addFmsManualSettled(fmsManualSettled);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("数据添加失败，请联系管理员");
		}
		
		return recRequest;
	}
	
	/**
	 * 修改手工结算数据
	 * @param fmsManualSettled
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "edit_manual_settled")
	public RecRequest editManualSettled(@RequestBody FmsManualSettled fmsManualSettled){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			
			if(manualSettledService.checkSettledName(fmsManualSettled)) {
				recRequest.setReCode(1);
				recRequest.setMessage("数据添加失败，结算名称已存在");
			}
			
			manualSettledService.updateFmsManualSettled(fmsManualSettled);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("数据修改失败，请联系管理员");
		}
		
		return recRequest;
	}
	
	/**
	 * 删除手工结算数据
	 * @param fmsManualSettled
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "del_manual_settled")
	public RecRequest delManualSettled(@RequestBody Long manualSettledId){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			manualSettledService.delFmsManualSettled(manualSettledId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("数据删除失败，请联系管理员");
		}
		
		return recRequest;
	}
	
	/**
	 * 生成结算批次
	 * @param fmsManualSettled
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "create_batch_code")
	public RecRequest createBatchCode(@RequestBody List<FmsManualSettledV> fmsManualSettledVList){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			recRequest = manualSettledService.createBatchCode(fmsManualSettledVList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("结算批次生成失败，请联系管理员");
		}
		return recRequest;
	}
	/**
	 * 生成结算批次
	 * @param fmsManualSettled
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "create_storage_batch_code")
	public RecRequest createStorageBatchCode(@RequestBody List<FmsManualSettledV> fmsManualSettledVList){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			recRequest = manualSettledService.createStorageBatchCode(fmsManualSettledVList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("结算批次生成失败，请联系管理员");
		}
		return recRequest;
	}
}
