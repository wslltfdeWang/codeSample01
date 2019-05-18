package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.fms.SendcarExportDto;
import com.foreveross.vds.dto.fms.SendcarGenerateRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.SendcarGenerateVService;
import com.foreveross.vds.util.tools.ExcelExportUtil;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class SendcarExportImportController {

    private static Logger logger = LoggerFactory.getLogger(SendcarExportImportController.class);

    @Autowired
    private SendcarGenerateVService sendcarGenerateVService;

    @ResponseBody
    @RequestMapping("/export_sendcar.do")
    public Object exportSendcar(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            SendcarGenerateRequest sendcarGenerateRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("sendcarGenerateRequest")), SendcarGenerateRequest.class);
            List<SendcarExportDto> list = sendcarGenerateVService.queryExport(sendcarGenerateRequest);
            sendcarGenerateVService.translate(list);
            detailResponse.setDetail(ExcelExportUtil.exportToString(list));
        }catch (Exception e){
            detailResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping(value = "import_sendcar.do")
    public BaseResponse importSendcar(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));
            List<Long> organizationIdList = JSONObject.parseArray((String.valueOf(params.get("organizationIdList"))), Long.class);

            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            sendcarGenerateVService.checkWorkBook(workbook);
            List<Long> sendcarHeaderIdList = sendcarGenerateVService.importPreparecarPlan(userId, sessionId, organizationIdList,  workbook);
            sendcarGenerateVService.sendInter(sendcarHeaderIdList);
        }catch (BusinessServiceException e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(1);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setStatus(0);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

}
