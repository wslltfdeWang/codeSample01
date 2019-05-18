package com.foreveross.vds.service.fms.controller.manualclearing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettled;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledHeader;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLine;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLineV;
import com.foreveross.vds.service.fms.service.manualclearing.AuditManualSettledServce;
import com.github.pagehelper.Page;
/**
 * 手工结算批次审核
 * @author foreveross
 *
 */
@Controller
public class AuditManualSettledController {

	@Autowired
	private AuditManualSettledServce auditManualSettledServce;
	
	/**
	 * 分页查询手工结算头数据
	 * @param fmsManualSettledV
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_audit_manual_settled_header_page")
	public BasePageResponse<FmsManualSettledHeader> queryAuditManualSettledHeaderPage(@RequestBody FmsManualSettledHeader fmsManualSettledHeader) {
		
		BasePageResponse<FmsManualSettledHeader> basePageResponse = new BasePageResponse<FmsManualSettledHeader>();
		
		Page<FmsManualSettledHeader> page = auditManualSettledServce.queryFmsManualSettledHeaderPage(fmsManualSettledHeader);
		
		
		
		basePageResponse.setRows(page.getResult());
		basePageResponse.setTotal(page.getTotal());
		return basePageResponse;
	}
	
	/**
	 * 查询手工结算行数据
	 * @param fmsManualSettledV
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_audit_manual_settled_line")
	public BasePageResponse<FmsManualSettledLine> queryAuditManualSettledLine(@RequestBody FmsManualSettledLine fmsManualSettledLine) {
		
		BasePageResponse<FmsManualSettledLine> basePageResponse = new BasePageResponse<FmsManualSettledLine>();
		
		List<FmsManualSettledLine> list = auditManualSettledServce.queryFmsManualSettledLine(fmsManualSettledLine);
		
		
		
		basePageResponse.setRows(list);
		basePageResponse.setTotal(Long.valueOf(list.size()));
		return basePageResponse;
	}
	
	/**
	 * 查询手工结算行数据视图
	 * @param fmsManualSettledV
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "query_audit_manual_settled_line_v")
	public BasePageResponse<FmsManualSettledLineV> queryAuditManualSettledLine(@RequestBody FmsManualSettledLineV fmsManualSettledLineV) {
		
		BasePageResponse<FmsManualSettledLineV> basePageResponse = new BasePageResponse<FmsManualSettledLineV>();
		
		List<FmsManualSettledLineV> list = auditManualSettledServce.queryFmsManualSettledLineV(fmsManualSettledLineV);
		
		
		
		basePageResponse.setRows(list);
		basePageResponse.setTotal(Long.valueOf(list.size()));
		return basePageResponse;
	}
	
	/**
	 * 审核手工结算头数据
	 * @param fmsManualSettledHeader
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "audit_manual_settled_header")
	public RecRequest auditManualSettledHeader(@RequestBody FmsManualSettledHeader fmsManualSettledHeader){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			
			Page<FmsManualSettledHeader> page = auditManualSettledServce.queryFmsManualSettledHeaderPage(fmsManualSettledHeader);
			
			if(page.getResult().size() > 0) {
				FmsManualSettledHeader auditFmsManualSettledHeader = page.getResult().get(0);
				
				if("reject".equals(fmsManualSettledHeader.getAuditType())) {//驳回
					auditFmsManualSettledHeader.setRejectFlag("Y");
					auditFmsManualSettledHeader.setRejectReason(fmsManualSettledHeader.getRejectFlag());
				}else if("adopt".equals(fmsManualSettledHeader.getAuditType())) {//通过
					auditFmsManualSettledHeader.setRejectFlag("N");
				}
				
				auditManualSettledServce.updateFmsManualSettledHeader(auditFmsManualSettledHeader);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("审核失败，请联系管理员");
		}
		
		return recRequest;
	}
	
	/**
	 * 审核手工结算行数据
	 * @param fmsManualSettledLine
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "audit_manual_settled_line")
	public RecRequest auditManualSettledLine(@RequestBody FmsManualSettledLine fmsManualSettledLine){
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		try {
			List<FmsManualSettledLine> list =auditManualSettledServce.queryFmsManualSettledLine(fmsManualSettledLine);
			if(list.size() > 0) {
				FmsManualSettledLine auditFmsManualSettledLine = list.get(0);
				auditFmsManualSettledLine.setRejectFlag("Y");
				auditFmsManualSettledLine.setRejectReason(fmsManualSettledLine.getRejectReason());
				auditManualSettledServce.updateFmsManualSettledLine(auditFmsManualSettledLine);
				//查询当前结算头ID下，未驳回的行数据
				FmsManualSettledLine chkFmsManualSettledLine = new FmsManualSettledLine();
				chkFmsManualSettledLine.setRejectFlag("N");
				chkFmsManualSettledLine.setManualSettledHeaderId(auditFmsManualSettledLine.getManualSettledHeaderId());
				
				List<FmsManualSettledLine> chkList =auditManualSettledServce.queryFmsManualSettledLine(chkFmsManualSettledLine);
				//如果当前结算头ID下，不存在未驳回的行数据，将对应头数据改为驳回状态
				if(chkList.size() == 0) {
					FmsManualSettledHeader fmsManualSettledHeader = new FmsManualSettledHeader();
					fmsManualSettledHeader.setManualSettledHeaderId(chkFmsManualSettledLine.getManualSettledHeaderId());
					Page<FmsManualSettledHeader> page = auditManualSettledServce.queryFmsManualSettledHeaderPage(fmsManualSettledHeader);
					
					if(page.getResult().size() > 0) {
						fmsManualSettledHeader = page.getResult().get(0);
						fmsManualSettledHeader.setRejectFlag("Y");
						fmsManualSettledHeader.setRejectReason("所有行数据被驳回，自动驳回头数据");
						auditManualSettledServce.updateFmsManualSettledHeader(fmsManualSettledHeader);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recRequest.setReCode(1);
			recRequest.setMessage("数据添加失败，请联系管理员");
		}
		
		return recRequest;
	}
}
