package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.constants.CommonErrorCode;
import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.dto.fms.TransportContractRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.fms.service.FmsTransportContractHeaderService;
import com.foreveross.vds.service.fms.service.transportContractImport.TransportContractImportService;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 运输合同维护
 */
@Controller
public class TransportContractController {

    private static Logger logger = LoggerFactory.getLogger(TransportContractController.class);

    @Autowired
    private FmsTransportContractHeaderService fmsTransportContractHeaderService;

    @Autowired
    private TransportContractImportService transportContractImportService;

    /**
     * 分页查询运输合同
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/page_transport_contract.do")
    public Object pageTransportContract(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            TransportContractRequest transportContractRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("transportContractRequest")), TransportContractRequest.class);
            basePageResponse = fmsTransportContractHeaderService.pageTransportContract(page, rows, transportContractRequest);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;

    }

    /**
     * 分页查询合同行
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/page_transport_contract_line.do")
    public Object pageTransportContractLine(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            TransportContractRequest transportContractRequest = JSONObject.parseObject(JSONObject.toJSONString(params.get("transportContractRequest")), TransportContractRequest.class);
            if(transportContractRequest.getConfirmFlag() == null) {
            	transportContractRequest.setConfirmFlag("N");
            }
            basePageResponse = fmsTransportContractHeaderService.pageTransportContractLine(page, rows, transportContractRequest);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;

    }

    /**
     * 保存合同
     * 正式合同临时合同通用
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/save_transport_contract.do")
    public DetailResponse saveTransportContract(@RequestBody Map<String, Object> params){
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            TransportContract transportContract = JSONObject.parseObject(String.valueOf(params.get("transportContract")), TransportContract.class);
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            fmsTransportContractHeaderService.checkParams(transportContract);
            fmsTransportContractHeaderService.saveTransportContract(transportContract, userId, sessionId);
        }catch (BusinessServiceException e){
            logger.warn(e.getMessage());
            detailResponse.setStatus(1);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    /**
     * 保存合同明细
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/save_transport_contract_line.do")
    public DetailResponse saveTransportContractLine(@RequestBody Map<String, Object> params){
        DetailResponse<Integer> detailResponse = new DetailResponse();
        try {
            TransportContract transportContract = JSONObject.parseObject(String.valueOf(params.get("transportContract")), TransportContract.class);
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            fmsTransportContractHeaderService.checkContractLine(transportContract);
            fmsTransportContractHeaderService.saveTransportContractLine(transportContract, userId, sessionId);
        }catch (BusinessServiceException e){
            logger.warn(e.getMessage());
            detailResponse.setStatus(1);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    /**
     * 根据物流商、所属基地、发运方式、车型大类、起点、终点判断是否已存在合同，
     * 如果存在则找到结束时间最晚的一条合同的结束时间 + 1天作为合同起始时间填入，并且合同起始时间不可编辑。
     * @return DetailResponse
     */
    @ResponseBody
    @RequestMapping("/get_transport_contract_start_date.do")
    public DetailResponse getTransportContractStartDate(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            TransportContract transportContract = JSONObject.parseObject(String.valueOf(params.get("transportContract")), TransportContract.class);
            transportContract.setOrderBy("t1.END_DATE DESC");
            List<TransportContract> oldContract = fmsTransportContractHeaderService.findOldContract(transportContract);
            if(oldContract.size() != 0){
                Date endDate = oldContract.get(0).getFmsTransportContractHeader().getEndDate();
                Calendar c = Calendar.getInstance();
                c.setTime(endDate);
                c.add(Calendar.DAY_OF_MONTH, 1);
                detailResponse.setDetail(c.getTime());
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    /**
     * 修改合同
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/update_transport_contract.do")
    public DetailResponse updateTransportContract(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            TransportContract transportContract = JSONObject.parseObject(String.valueOf(params.get("transportContract")), TransportContract.class);
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            fmsTransportContractHeaderService.checkParams(transportContract);
            fmsTransportContractHeaderService.updateTransportContract(transportContract, userId);
        }catch (BusinessServiceException e){
            logger.warn(e.getMessage());
            detailResponse.setStatus(1);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    /**
     * 删除合同头
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/delete_transport_contract_header.do")
    public DetailResponse deleteTransportContractHeader(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            TransportContract transportContract = JSONObject.parseObject(String.valueOf(params.get("transportContract")), TransportContract.class);
            fmsTransportContractHeaderService.deleteTransportContractHeader(transportContract);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    /**
     * 删除合同行
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/delete_transport_contract_line.do")
    public DetailResponse deleteTransportContractLine(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            TransportContract transportContract = JSONObject.parseObject(String.valueOf(params.get("transportContract")), TransportContract.class);
            fmsTransportContractHeaderService.deleteTransportContractLine(transportContract);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            detailResponse.setStatus(0);
            detailResponse.setError(CommonErrorCode.SYSTEM_EXCEPTION);
            detailResponse.setMessage(e.getMessage());
        }
        return detailResponse;
    }

    @ResponseBody
    @RequestMapping(value = "import_transport_contract.do")
    public BaseResponse importTransportContract(@RequestBody Map<String, Object> params){
        DetailResponse detailResponse = new DetailResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));

            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            transportContractImportService.importTransportContract(workbook, userId, sessionId);
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
