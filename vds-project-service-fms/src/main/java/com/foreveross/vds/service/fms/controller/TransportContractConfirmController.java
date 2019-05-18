package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.TransportContractRequest;
import com.foreveross.vds.service.fms.service.FmsTransportContractHeaderService;
import com.foreveross.vds.service.fms.service.FmsTransportContractLineService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;

/**
 * 运输合同复核
 */
@Controller
public class TransportContractConfirmController {

    private static Logger logger = LoggerFactory.getLogger(TransportContractConfirmController.class);

    @Autowired
    private FmsTransportContractHeaderService fmsTransportContractHeaderService;
    

    /**
     * 分页查询运输合同
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/page_transport_contract_confirm.do")
    public Object pageTransportContractConfirm(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            TransportContractRequest transportContractRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("transportContractRequest")), TransportContractRequest.class);
            transportContractRequest.setEnabledFlag("Y");
            basePageResponse = fmsTransportContractHeaderService.pageTransportContract(page, rows, transportContractRequest);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;

    }

    /**
     * 复核合同
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/confirm_transport_contract.do")
    public DetailResponse confirmTransportContract(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String jsonString = JSONObject.toJSONString(params.get("transportContractHeaderIds"));
            String sessionId = String.valueOf(params.get("sessionId"));
            List<Long> transportContractHeaderIds = JSONObject.parseArray(jsonString, Long.class);
            fmsTransportContractHeaderService.doConfirmTransportContract(transportContractHeaderIds, userId, sessionId);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    /**
     * 复核合同明细
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/confirm_transport_contract_line.do")
    public DetailResponse confirmTransportContractLine(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String jsonString = JSONObject.toJSONString(params.get("transportContractLineIds"));
            String sessionId = String.valueOf(params.get("sessionId"));
            List<Long> transportContractLineIds = JSONObject.parseArray(jsonString, Long.class);
        	fmsTransportContractHeaderService.doConfirmTransportContractLine(transportContractLineIds, userId, sessionId);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }
}
