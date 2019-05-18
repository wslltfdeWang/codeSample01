package com.foreveross.vds.service.fms.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.Pagination;
import com.foreveross.vds.dto.fms.FmsProviderRequest;
import com.foreveross.vds.service.fms.service.FmsProviderService;
import com.foreveross.vds.vo.fms.FmsProvider;
import com.foreveross.vds.vo.tms.TmsLogisticsProviders;

@Controller
public class FmsProviderController {

	private static Logger logger = LoggerFactory.getLogger(FmsProviderController.class);
	
	@Autowired
	private FmsProviderService fmsProviderService;
	
	@ResponseBody
    @RequestMapping(value = "fms_provider_query.do")
    public Pagination fmsProviderQuery(@RequestBody Map<String, Object> params) {
        Pagination pagination = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            FmsProviderRequest fmsProviderRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("whereClause")), FmsProviderRequest.class);
            
            pagination = fmsProviderService.pageRealation(page, rows, fmsProviderRequest);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return pagination;
    }
	
	@ResponseBody
    @RequestMapping(value = "add_fms_provider.do")
    public BaseResponse addFmsProvider(@RequestBody Map<String, Object> params) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = (String) params.get("sessionId");
            FmsProvider fmsProvider = JSONObject.parseObject(JSONObject.toJSONString(params.get("fmsProvider")), FmsProvider.class);
            fmsProviderService.addRealationNew(userId, fmsProvider, sessionId);
        } catch (Exception e) {
        	if(e.getMessage().equals("供应商已存在")) {
        		baseResponse.setStatus(100);
        	}else {
        		baseResponse.setStatus(0);
        	}
            logger.error(e.getMessage(), e);
        }
        return baseResponse;
    }
	
	@ResponseBody
    @RequestMapping(value = "update_fms_provider.do")
    public BaseResponse updateFmsProvider(@RequestBody Map<String, Object> params) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = (String) params.get("sessionId");
            FmsProvider fmsProvider = JSONObject.parseObject(JSONObject.toJSONString(params.get("fmsProvider")), FmsProvider.class);
            fmsProviderService.updateRealationNew(userId, fmsProvider, sessionId);
        } catch (Exception e) {
            baseResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return baseResponse;
    }
	
	@ResponseBody
    @RequestMapping("delete_fms_provider.do")
    public DetailResponse deleteFmsProvider(@RequestBody Map<String, Object> params){
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            Long providerId = Long.parseLong(String.valueOf(params.get("providerId")));
            System.out.println(providerId);
            int i = fmsProviderService.delete(providerId);
            detailResponse.setDetail(i);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }
	
    @ResponseBody
    @RequestMapping(value = "get_new_providers_all.do")
    public Object getLogisticsProviders(){
        DetailResponse detailResponse = new DetailResponse();
        try {
            List<FmsProvider> fmsProvider = fmsProviderService.getAllProviders(null);
            detailResponse.setDetail(fmsProvider);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }
}
