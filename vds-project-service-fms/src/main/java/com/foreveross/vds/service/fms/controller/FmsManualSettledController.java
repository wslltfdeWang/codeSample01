package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.DetailPageRequest;
import com.foreveross.vds.service.fms.service.FmsManualSettledService;
import com.foreveross.vds.service.fms.service.FmsTransportContractHeaderService;
import com.foreveross.vds.vo.fms.FmsManualSettled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
public class FmsManualSettledController {

    private static Logger logger = LoggerFactory.getLogger(FmsManualSettledController.class);

    @Autowired
    private FmsManualSettledService fmsManualSettledService;

    @RequestMapping("/page_fms_manual_settled.do")
    public Object pageFmsManualSettled(@RequestBody Map<String, Object> params) {
        BasePageResponse basePageResponse = new BasePageResponse(0L, new ArrayList());
        try {
            DetailPageRequest detailPageRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("detailPageRequest")), DetailPageRequest.class);
            basePageResponse = fmsManualSettledService.queryPage(detailPageRequest, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;
    }

    @RequestMapping("/insert_or_update_fms_manual_settled.do")
    public Object insertOrUpdateFmsManualSettled(@RequestBody Map<String, Object> params) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            FmsManualSettled fmsManualSettled = JSONObject.parseObject(JSONObject.toJSONString(params.get("fmsManualSettled")), FmsManualSettled.class);
            fmsManualSettledService.insertOrUpdateFmsManualSettled(fmsManualSettled, userId, sessionId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            baseResponse.setStatus(0);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @RequestMapping("/delete_fms_manual_settled.do")
    public Object deleteFmsManualSettled(@RequestBody Map<String, Object> params) {
        BaseResponse detailResponse = new DetailResponse();
        try {
            List<Long> manualSettledIds = JSONObject.parseArray(String.valueOf(params.get("manualSettledIds")), Long.class);
            fmsManualSettledService.deleteFmsManualSettled(manualSettledIds);
        } catch (Exception e) {
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;

    }

    @ResponseBody
    @RequestMapping("/get_fms_manual_settled.do")
    public DetailResponse getFmsManualSettled(@RequestBody Map<String, Object> params) {
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long manualSettledId = Long.valueOf(String.valueOf(params.get("manualSettledId")));
            FmsManualSettled fmsManualSettled = fmsManualSettledService.selectByPrimaryKey(manualSettledId);
            detailResponse.setDetail(fmsManualSettled);
        } catch (Exception e) {
            e.printStackTrace();
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

}
