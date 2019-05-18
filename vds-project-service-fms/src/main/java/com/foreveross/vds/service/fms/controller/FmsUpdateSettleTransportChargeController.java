package com.foreveross.vds.service.fms.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.DealerRecieveAddressVRequest;
import com.foreveross.vds.dto.fms.FmsChangeAddressRecordRequest;
import com.foreveross.vds.dto.fms.FmsExportChangeAddressAttachments;
import com.foreveross.vds.dto.fms.FmsSettleTransportChargeVRequest;
import com.foreveross.vds.dto.tms.TmsReginInfoRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.FmsChangeAddressRecordService;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.foreveross.vds.vo.tms.TmsRegionInfo;

@Controller
public class FmsUpdateSettleTransportChargeController {
    
	private static Logger logger = LoggerFactory.getLogger(FmsUpdateSettleTransportChargeController.class);
	
	@Autowired
	FmsChangeAddressRecordService fmsChangeAddressRecordService;
	
    /**
     * 分页查询送车交接单头数据作为树的第一层
     * 返回满足eazyui树格式的数据
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/page_fms_change_settle_transport_charge.do")
    public Object pageFmsSettleTransportCharge(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            FmsChangeAddressRecordRequest whereClause = JSONObject.parseObject(
                    JSONObject.toJSONString(params.get("whereClause")),
                    FmsChangeAddressRecordRequest.class);


            basePageResponse =fmsChangeAddressRecordService.pageFmsSettleTransportChargeTree(page, rows, whereClause);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;

    }
	
    @ResponseBody
    @RequestMapping("/update_settle_transport_charge_address.do")
    public DetailResponse insertFmsCarCategory(@RequestBody FmsChangeAddressRecordRequest fmsChangeAddressRecordRequest) throws BusinessServiceException{
        DetailResponse<Integer> detailResponse = new DetailResponse();
        detailResponse.setStatus(200);
        try {
        	int j = fmsChangeAddressRecordService.updateSettleFlag(fmsChangeAddressRecordRequest);
        	fmsChangeAddressRecordRequest.setLastAddressId(
        			fmsChangeAddressRecordService.selectIdByName(fmsChangeAddressRecordRequest.getLastAddress()).getDealerRecieveAddressId());
        	fmsChangeAddressRecordRequest.setNowAddressId(
        			fmsChangeAddressRecordService.selectIdByName(fmsChangeAddressRecordRequest.getNowAddress()).getDealerRecieveAddressId());

        	fmsChangeAddressRecordRequest.setApproveStatus(new Long(1));
        	int i = fmsChangeAddressRecordService.insertApply(fmsChangeAddressRecordRequest);
            detailResponse.setDetail(i);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }
    
	/**
	 * 根据名称查询
	 * @param tmsReginInfoRequest
	 * @return
	 */
	@RequestMapping("/select_new_address.do")
	@ResponseBody
	public BaseResponse selectByNames(@RequestBody DealerRecieveAddressVRequest dealerRecieveAddressVRequest) {
		List<DealerRecieveAddressVRequest> queryList = fmsChangeAddressRecordService.selectByNames(dealerRecieveAddressVRequest);
		if(queryList.size()<= 0) {
			return new DetailResponse<>(100,"结果为空");
		}
		return new DetailResponse<>(queryList);
	}
	
    /**
	 * 导入
	 */
	@RequestMapping("import_change_settle_transport_address_dir")
	@ResponseBody
	public BaseResponse importAllotCarrier(@RequestBody Map<String, Object> params,HttpServletRequest request) {
		BaseResponse baseResponse = new BaseResponse();
		try {
			Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));
            Long changeAddressId = Long.valueOf(String.valueOf(params.get("changeAddressId")));
            String uploadUrl = String.valueOf(params.get("uploadUrl"));
            InputStream inputStream = FileEncodeUtil.decodeToStream(fileString);
            String result = fmsChangeAddressRecordService.importChangeSettleTransportDir(inputStream, fileName,userId, sessionId, changeAddressId,uploadUrl);
            baseResponse = new DetailResponse<>(result);
		} catch (Exception e) {
			e.printStackTrace();
			baseResponse.setError("0");
			baseResponse.setStatus(0);
			baseResponse.setMessage(e.getMessage());
		}
		return baseResponse;
	}
	
    /**
     * 分页查询送车交接单头数据作为树的第一层
     * 返回满足eazyui树格式的数据
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/approve_change_settle_transport_charge_address.do")
    public Object approveChangeSettleTransportChargeAddress(@RequestBody Map<String, Object> params){
    	BaseResponse baseResponse = new BaseResponse();
        try {
        	Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String sendcarHeaderId = String.valueOf(params.get("sendcarHeaderId"));
            List<Long> sendcarHeaderIdArray = JSONObject.parseArray(sendcarHeaderId, Long.class);
            String changeAddressId = String.valueOf(params.get("changeAddressId"));
            List<Long> changeAddressIdArray = JSONObject.parseArray(changeAddressId, Long.class);
            String result = fmsChangeAddressRecordService.approveChangeAddress(sendcarHeaderIdArray, changeAddressIdArray,userId, sessionId);
            baseResponse = new DetailResponse<>(result);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return baseResponse;

    }
    
    /**
     * 分页查询送车交接单头数据作为树的第一层
     * 返回满足eazyui树格式的数据
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/page_fms_change_attachments.do")
    public Object pageFmsChangeAttachments(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            FmsExportChangeAddressAttachments whereClause = JSONObject.parseObject(
                    JSONObject.toJSONString(params.get("whereClause")),
                    FmsExportChangeAddressAttachments.class);


            basePageResponse =fmsChangeAddressRecordService.pageFmsChangeAttachmentsExport(page, rows, whereClause);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;

    }
}
