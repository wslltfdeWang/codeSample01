package com.foreveross.vds.service.fms.controller.storagecontract;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeader;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeaderV;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLine;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLineV;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledV;
import com.foreveross.vds.dto.pms.TmsLogisticsMode;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledHeaderService;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledHeaderVService;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledLineService;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledLineVService;
import com.github.pagehelper.Page;

import net.sf.json.JSONObject;
/**
 * 审核仓储结算数据批次
 * @author foreveross
 *
 */
@Controller
public class AuditStorageContractController{
	
	@Autowired
	private FmsStorageSettledHeaderVService fmsStorageSettledHeaderVService;
	@Autowired
	private FmsStorageSettledLineVService fmsStorageSettledLineVService;
	@Autowired
	private FmsStorageSettledHeaderService fmsStorageSettledHeaderService;
	@Autowired
	private FmsStorageSettledLineService fmsStorageSettledLineService;
	
	/**
	 * 分页查询仓储合同结算头数据
	 * @param tmsLogisticsProviders
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value = "query_aduti_storage_contract_page")
	public BasePageResponse queryAuditStorageContractPage(@RequestBody FmsStorageSettledHeaderV fmsStorageSettledHeaderV) {
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
	@RequestMapping(value = "query_audit_storage_settled_line_page")
	public BasePageResponse queryAuditFmsStorageSettledLineV(@RequestBody FmsStorageSettledLineV fmsStorageSettledLineV) {
		
		BasePageResponse basePageResponse = new BasePageResponse();
		
		Page<FmsStorageSettledLineV> page = fmsStorageSettledLineVService.queryFmsStorageSettledLineVByPage(fmsStorageSettledLineV);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	/**
	 * 驳回仓储结算头数据
	 * @param fmsStorageSettledHeader
	 * @return
	 */
	@Transactional
	@ResponseBody
	@RequestMapping(value = "rejected_aduti_storage_contract")
	public RecRequest rejectedSdutiStorageContract(@RequestBody FmsStorageSettledHeader fmsStorageSettledHeader){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			String torageSettledHeaderIdStr = fmsStorageSettledHeader.getStorageSettledHeaderIdStr();
			
			String[] torageSettledHeaderIdStrArray = torageSettledHeaderIdStr.split(",");
			
			List<Long> idList = new ArrayList<Long>();
			
			for(int i=0;i<torageSettledHeaderIdStrArray.length;i++) {
				if(torageSettledHeaderIdStrArray[i] != null 
						&& !"".equals(torageSettledHeaderIdStrArray[i])) {
					idList.add(Long.parseLong(torageSettledHeaderIdStrArray[i]));
				}
			}
			fmsStorageSettledHeader.setStorageSettledHeaderIdList(idList);
			fmsStorageSettledHeaderService.rejectedFmsStorageSettledHeader(fmsStorageSettledHeader);
			
			
			List<FmsStorageSettledLine> list = fmsStorageSettledLineService.getFmsStorageSettledLineByStorageSettledHeaderId(idList.get(0));
			List<Long> idList1 = new ArrayList<Long>();
			
			for(int i=0;i<list.size();i++) {
				FmsStorageSettledLine fmsStorageSettledLine = list.get(i);
				
				if(!"Y".equals(fmsStorageSettledLine.getRejectFlag())) {
					idList1.add(fmsStorageSettledLine.getStorageSettledLineId());
				}
			}
			
			FmsStorageSettledLine fmsStorageSettledLine = new FmsStorageSettledLine();
			fmsStorageSettledLine.setStorageSettledLineIdList(idList1);
			fmsStorageSettledLine.setRejectReason("头数据驳回，行数据自动驳回");
			fmsStorageSettledLineService.rejectedFmsStorageSettledLine(fmsStorageSettledLine);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("驳回失败");
		}
		
		return recRequest;
	}
	
	/**
	 * 通过仓储结算头数据
	 * @param fmsStorageSettledHeader
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "allow_aduti_storage_contract")
	public RecRequest allowSdutiStorageContract(@RequestBody FmsStorageSettledHeader fmsStorageSettledHeader){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			String torageSettledHeaderIdStr = fmsStorageSettledHeader.getStorageSettledHeaderIdStr();
			
			String[] torageSettledHeaderIdStrArray = torageSettledHeaderIdStr.split(",");
			
			List<Long> idList = new ArrayList<Long>();
			
			for(int i=0;i<torageSettledHeaderIdStrArray.length;i++) {
				if(torageSettledHeaderIdStrArray[i] != null 
						&& !"".equals(torageSettledHeaderIdStrArray[i])) {
					idList.add(Long.parseLong(torageSettledHeaderIdStrArray[i]));
				}
			}
			fmsStorageSettledHeader.setStorageSettledHeaderIdList(idList);
			fmsStorageSettledHeaderService.allowFmsStorageSettledHeader(fmsStorageSettledHeader);
		
			List<FmsStorageSettledLine> list = fmsStorageSettledLineService.getFmsStorageSettledLineByStorageSettledHeaderId(idList.get(0));
			List<Long> idList1 = new ArrayList<Long>();
			
			for(int i=0;i<list.size();i++) {
				FmsStorageSettledLine fmsStorageSettledLine = list.get(i);
				
				if(!"Y".equals(fmsStorageSettledLine.getRejectFlag())) {
					idList1.add(fmsStorageSettledLine.getStorageSettledLineId());
				}
			}
			fmsStorageSettledLineService.allowFmsStorageSettledLine(idList1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("审核通过失败");
		}
		
		return recRequest;
	}
	
	/**
	 * 驳回仓储结算行数据
	 * @param fmsStorageSettledHeader
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "rejected_aduti_storage_contract_line")
	public RecRequest rejectedSdutiStorageContractLine(@RequestBody FmsStorageSettledLine fmsStorageSettledLine){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			
			String storageSettledLineIdStr = fmsStorageSettledLine.getStorageSettledLineIdStr();
			
			String[] tstorageSettledLineIdStrArray = storageSettledLineIdStr.split(",");
			
			List<Long> idList = new ArrayList<Long>();
			
			for(int i=0;i<tstorageSettledLineIdStrArray.length;i++) {
				if(tstorageSettledLineIdStrArray[i] != null 
						&& !"".equals(tstorageSettledLineIdStrArray[i])) {
					idList.add(Long.parseLong(tstorageSettledLineIdStrArray[i]));
				}
			}
			fmsStorageSettledLine.setStorageSettledLineIdList(idList);
			
			fmsStorageSettledLineService.rejectedFmsStorageSettledLine(fmsStorageSettledLine);
			
			List<FmsStorageSettledLine> list = fmsStorageSettledLineService.getFmsStorageSettledLineByStorageSettledHeaderId(fmsStorageSettledLine.getStorageSettledHeaderId());
			
			boolean isNotEditHeader = false;//是否更新头数据驳回状态
			
			for(int i=0;i<list.size();i++) {
				FmsStorageSettledLine bean = list.get(i);
				
				if(bean.getRejectFlag() != null && !"Y".equals(bean.getRejectFlag())) {
					isNotEditHeader = true;
				}
			}
			
			if(!isNotEditHeader) {//如果所有行数据都被驳回则修改头数据驳回状态为驳回
				FmsStorageSettledHeader fmsStorageSettledHeader = new FmsStorageSettledHeader();
				List<Long> idList1 = new ArrayList<Long>();
				
				idList1.add(fmsStorageSettledLine.getStorageSettledHeaderId());
				
				
				fmsStorageSettledHeader.setRejectFlag("Y");
				fmsStorageSettledHeader.setRejectReason(fmsStorageSettledLine.getRejectReason());
				fmsStorageSettledHeader.setStorageSettledHeaderIdList(idList1);
				fmsStorageSettledHeaderService.rejectedFmsStorageSettledHeader(fmsStorageSettledHeader);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("驳回失败");
		}
		
		return recRequest;
	}
}
