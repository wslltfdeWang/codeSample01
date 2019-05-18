package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.DetailPageRequest;
import com.foreveross.vds.dto.fms.SendcarGenerateRequest;
import com.foreveross.vds.dto.fms.SendcarGenerateSubmit;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.SendcarGenerateVService;
import com.foreveross.vds.vo.fms.SendcarGenerateV;
import com.foreveross.vds.vo.inv.InvDriverInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SendcarGenerateController {

    private static Logger logger = LoggerFactory.getLogger(SendcarGenerateController.class);

    @Autowired
    private SendcarGenerateVService sendcarGenerateVService;

    @ResponseBody
    @RequestMapping("/page_sendcar_generate.do")
    public Object pageSendcarGenerate(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = new BasePageResponse(0L, new ArrayList());
        try {
            DetailPageRequest param = JSONObject.parseObject(JSONObject.toJSONString(params.get("detailPageRequest")), DetailPageRequest.class);
            SendcarGenerateRequest sendcarGenerateRequest = JSONObject.parseObject(JSONObject.toJSONString(param.getDetail()), SendcarGenerateRequest.class);
            param.setDetail(sendcarGenerateRequest);
            basePageResponse = sendcarGenerateVService.queryPage(param, true);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;
    }

    @ResponseBody
    @RequestMapping("/get_same_load_list_detail.do")
    public Object getSameLoadListDetail(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long vinId = Long.valueOf(String.valueOf(params.get("vinId")));
            List<Long> organizationIds = JSONObject.parseArray(String.valueOf(params.get("organizationIds")), Long.class);
            List<SendcarGenerateV> sameLoadListDetail = sendcarGenerateVService.getSameLoadListDetail(vinId, organizationIds);
            detailResponse.setDetail(sameLoadListDetail);
        }catch (BusinessServiceException e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(1);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/do_generate_sendcar.do")
    public Object doGenerateSendcar(@RequestBody Map<String, Object> params){
        BaseResponse baseResponse = new BaseResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            SendcarGenerateSubmit sendcarGenerateSubmit = JSONObject.parseObject(String.valueOf(params.get("SendcarGenerateSubmit")), SendcarGenerateSubmit.class);

            List<Long> sendcarHeaderIdList = sendcarGenerateVService.doGenerateSendcar(sendcarGenerateSubmit, userId, sessionId);
            sendcarGenerateVService.sendInter(sendcarHeaderIdList);
            sendcarGenerateVService.relateData(sendcarHeaderIdList, userId, sessionId);
        }catch (BusinessServiceException e){
            logger.error(e.getMessage(), e);
            baseResponse.setStatus(1);
            baseResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @ResponseBody
    @RequestMapping("/get_driver_info.do")
    public Object getDriverInfo(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long transToolId = Long.valueOf(String.valueOf(params.get("transToolId")));
            InvDriverInfo invDriverInfo = sendcarGenerateVService.getDriverInfo(transToolId);
            detailResponse.setDetail(invDriverInfo);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

}
