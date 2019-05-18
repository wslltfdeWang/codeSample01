package com.foreveross.vds.service.fms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.DetailPageRequest;
import com.foreveross.vds.dto.fms.SendcarPrintRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.SendcarPrintQueryVService;
import com.foreveross.vds.service.fms.service.SendcarPrintVService;
import com.foreveross.vds.vo.fms.SendcarPrintVNew;

/**
 * 送车交接单打印
 */
@RestController
public class SendcarPrintController {

    private static Logger logger = LoggerFactory.getLogger(SendcarPrintController.class);

    @Autowired
    private SendcarPrintQueryVService sendcarPrintQueryVService;

    @Autowired
    private SendcarPrintVService sendcarPrintVService;

    @RequestMapping("/page_sendcar_print.do")
    public Object pageSendcarPrint(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = new BasePageResponse(0L, new ArrayList());
        try {
            DetailPageRequest param = JSONObject.parseObject(JSONObject.toJSONString(params.get("detailPageRequest")), DetailPageRequest.class);
            basePageResponse = sendcarPrintQueryVService.queryPage(param, true);
            sendcarPrintQueryVService.selectOutDate(basePageResponse.getRows());
            sendcarPrintQueryVService.setFooter(basePageResponse);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;
    }

    @RequestMapping("/get_sendcar_print_data.do")
    public Object getSendcarPrintData(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            List<Long> sendcarHeaderId = JSONArray.parseArray(String.valueOf(params.get("sendcarHeaderId")), Long.class);
            SendcarPrintRequest sendcarPrintRequest = new SendcarPrintRequest();
            sendcarPrintRequest.setSendcarHeaderId(sendcarHeaderId);


            List<SendcarPrintVNew> sendcarPrintVList = sendcarPrintVService.queryList(sendcarPrintRequest);
            
            for (SendcarPrintVNew entity : sendcarPrintVList) {
            	entity.setSendcarLines(sendcarPrintVService.queryLineList(entity));
            }
            
            detailResponse.setDetail(sendcarPrintVList);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping("/sendcar_print_callback.do")
    public Object sendCarPrintCallback(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            List<Long> sendcarHeaderIds = JSONArray.parseArray(String.valueOf(params.get("sendcarHeaderIds")), Long.class);

            sendcarPrintVService.sendCarPrintCallback(sendcarHeaderIds, userId, sessionId);
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
    @RequestMapping("/sendcar_overtime_callback.do")
    public Object sendCarOvertimeCallback(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            Long sendcarHeaderId = Long.valueOf(params.get("sendcarHeaderId").toString());
//            List<Long> sendcarHeaderIds = JSONArray.parseArray(String.valueOf(params.get("sendcarHeaderIds")), Long.class);

            sendcarPrintVService.sendCarOvertimeCallback(sendcarHeaderId, userId, sessionId);
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

}
