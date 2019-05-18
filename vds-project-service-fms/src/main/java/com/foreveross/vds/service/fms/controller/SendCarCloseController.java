package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.dto.tms.TmsSendcarLineV;
import com.foreveross.vds.service.fms.service.SendCarCloseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author Villy
 */
@Controller
public class SendCarCloseController {

    @Autowired
    private SendCarCloseService sendCarCloseService;

    private static Logger logger = LoggerFactory.getLogger(SendCarCloseController.class);

    /**
     * 分页查询送车交接单视图
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query_sendcar_line_detail_page.do")
    public BasePageResponse<TmsSendcarLineV> querySendcarLineDetailPage(@RequestBody Map<String, Object> params) {
        BasePageResponse basePageResponse = null;
        try {
            TmsSendcarLineV whereClause = JSONObject.parseObject(
                    JSONObject.toJSONString(params.get("whereClause")),
                    TmsSendcarLineV.class);

            basePageResponse = sendCarCloseService.queryTmsSendcarLineV(whereClause);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;
    }

    /**
     * 分页查询关闭送车交接单视图
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query_sendcar_line_close_page.do")
    public BasePageResponse<TmsSendcarLineV> querySendcarLineCloselPage(@RequestBody Map<String, Object> params) {
        BasePageResponse basePageResponse = null;
        try {
            TmsSendcarLineV whereClause = JSONObject.parseObject(
                    JSONObject.toJSONString(params.get("whereClause")),
                    TmsSendcarLineV.class);

            basePageResponse = sendCarCloseService.queryCloseTmsSendcarLineV(whereClause);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;
    }

    @ResponseBody
    @RequestMapping(value = "close_car_line")
    public RecRequest closeCarLine(@RequestBody TmsSendcarLineV tmsSendcarLineV) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);

        TmsSendcarLine tmsSendcarLine = new TmsSendcarLine();
        tmsSendcarLine.setUserId(tmsSendcarLineV.getUserId());
        tmsSendcarLine.setLastUpdateLogin(tmsSendcarLineV.getSessionId());
        tmsSendcarLine.setCloseReason(tmsSendcarLineV.getCloseReason());

        for (String s : tmsSendcarLineV.getSendcarHeaderIds().split(",")) {
            tmsSendcarLine.setSencarLineId(Long.valueOf(s));
            try {
                sendCarCloseService.updateTmsSendcarLine(tmsSendcarLine);
            } catch (Exception e) {
                e.printStackTrace();
                recRequest.setReCode(1);
                recRequest.setMessage("结算条码关闭失败");
                return recRequest;
            }
        }

        return recRequest;
    }
}
