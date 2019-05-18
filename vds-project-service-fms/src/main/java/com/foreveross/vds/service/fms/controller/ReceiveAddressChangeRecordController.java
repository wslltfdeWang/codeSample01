package com.foreveross.vds.service.fms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.ReceiveAddressChangeV;
import com.foreveross.vds.dto.fms.SendcarHeaderChangeV;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeaderV;
import com.foreveross.vds.dto.pms.PmsDcsCrmDealerInter;
import com.foreveross.vds.service.fms.service.ReceiveAddressChangeRecordService;
import com.github.pagehelper.Page;
/**
 * 收车地址变更
 * @author foreveross
 *
 */
@Controller
public class ReceiveAddressChangeRecordController {

	@Autowired
	private ReceiveAddressChangeRecordService receiveAddressChangeRecordService;

	/**
	 * 分页查询待变更送车交接单数据
	 * @param sendcarHeaderChangeV
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value = "query_sendcar_changev_page")
	public BasePageResponse<SendcarHeaderChangeV> querySendcarHeaderChangeVPage(@RequestBody SendcarHeaderChangeV sendcarHeaderChangeV) {
		BasePageResponse<SendcarHeaderChangeV> basePageResponse = new BasePageResponse<SendcarHeaderChangeV>();
		
		Page<SendcarHeaderChangeV> page = receiveAddressChangeRecordService.querySendcarHeaderChangeV(sendcarHeaderChangeV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 分页查询变更记录
	 * @param receiveAddressChangeV
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value = "query_receive_address_changev_page")
	public BasePageResponse<ReceiveAddressChangeV> queryReceiveAddressChangeV(@RequestBody ReceiveAddressChangeV receiveAddressChangeV) {
		BasePageResponse<ReceiveAddressChangeV> basePageResponse = new BasePageResponse<ReceiveAddressChangeV>();
		
		Page<ReceiveAddressChangeV> page = receiveAddressChangeRecordService.queryReceiveAddressChangeV(receiveAddressChangeV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 查询收车单位
	 * @param pmsDcsCrmDealerInter
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value = "query_dccs_crm_dealer_inter")
	public List<PmsDcsCrmDealerInter> queryPmsDcsCrmDealerInter(@RequestBody PmsDcsCrmDealerInter pmsDcsCrmDealerInter) {
		
		List<PmsDcsCrmDealerInter> list = receiveAddressChangeRecordService.queryPmsDcsCrmDealerInter(pmsDcsCrmDealerInter);
		return list;
	}
	
	/**
	 * 修改收车地址
	 * @param pmsDcsCrmDealerInter
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value = "change_adress")
	public RecRequest changeAdress(@RequestBody ReceiveAddressChangeV receiveAddressChangeV) {
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		try {
			receiveAddressChangeRecordService.changeAdress(receiveAddressChangeV);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("收车地址变更失败，请联系管理员");
		}
		return recRequest;
	}
}
