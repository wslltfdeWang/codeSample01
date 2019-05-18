package com.foreveross.vds.service.fms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.client.client.VdsClient;
import com.foreveross.vds.client.client.VdsDefaultClient;
import com.foreveross.vds.dto.inter.expensereport.ExpenseReportHeadersVO;
import com.foreveross.vds.dto.inter.ordersettledetails.CavdsOrderSettleDetailsI;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.util.ConstantUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsSettleTransportChargeVRequest;
import com.foreveross.vds.dto.fms.FmsTransportSettledLine;
import com.foreveross.vds.dto.fms.SettleTransportChargeV;
import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.service.fms.service.FmsCreateSettleTransportChargeService;
import com.foreveross.vds.service.fms.service.FmsSettleTransportChargeVService;
import com.foreveross.vds.util.tools.FileEncodeUtil;
import com.foreveross.vds.vo.fms.FmsSettleTransportChargeV;

/**
 * 生成运费结算批次
 *
 * @author foreveross
 */
@Controller
public class FmsCreateSettleTransportChargeController {
    @Autowired
    private FmsCreateSettleTransportChargeService fmsCreateSettleTransportChargeService;

    @Autowired
    private FmsSettleTransportChargeVService fmsSettleTransportChargeVService;


    @ResponseBody
    @RequestMapping("/create_settle_transport_charge")
    public RecRequest createSettleTransportCharge(@RequestBody SettleTransportChargeV settleTransportChargeV) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);

        try {
            recRequest = fmsSettleTransportChargeVService.createSettleTransportCharge(settleTransportChargeV);
        } catch (BusinessServiceException be) {
            be.printStackTrace();
            recRequest.setReCode(1);
            recRequest.setMessage(be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            recRequest.setReCode(1);
            recRequest.setMessage("生成失败");
        }

        return recRequest;
    }

    @ResponseBody
    @RequestMapping("/regen_settle_transport")
    public RecRequest regenSettleTransport(@RequestBody SettleTransportChargeV settleTransportChargeV) {
        RecRequest recRequest = new RecRequest();
        recRequest.setReCode(0);
        String[] sendcarHeaderIdsStr = settleTransportChargeV.getSendcarheaderidStr().split(",");
        List<Long> sendcarHeaderIds = new ArrayList<>();
        for (String id : sendcarHeaderIdsStr) {
            sendcarHeaderIds.add(Long.valueOf(id));
        }

        if (sendcarHeaderIds.size() == 0) {
            FmsSettleTransportChargeVRequest request = new FmsSettleTransportChargeVRequest();
            List<FmsSettleTransportChargeV> list = fmsSettleTransportChargeVService.listFmsSettleTransportChargeTree(request);
            for (FmsSettleTransportChargeV entity : list) {
                FmsTransportSettledLine line = new FmsTransportSettledLine();
                line.setSendcarHeaderId(Long.valueOf(entity.getSendcarHeaderId()));
                line.setCreatedBy(settleTransportChargeV.getUserId());
                line.setLastUpdatedBy(settleTransportChargeV.getUserId());
                line.setLastUpdateLogin(settleTransportChargeV.getSessionId());
                try {
                    fmsCreateSettleTransportChargeService.relateData(line);
                    //调用民生接口
                    List<CavdsOrderSettleDetailsI> cavdsOrderSettleDetailsIList = new ArrayList<>();
                    CavdsOrderSettleDetailsI headerVO = new CavdsOrderSettleDetailsI();
                    headerVO.setSendcarHeaderId(line.getSendcarHeaderId());
                    cavdsOrderSettleDetailsIList.add(headerVO);

                    String url = "/cavds_order_settle_details_i_inter";
                    VdsClient vdsClient = new VdsDefaultClient(
                            ConstantUtil.INTER_URL + url, JSONObject.toJSONString(cavdsOrderSettleDetailsIList), "", "");
                    vdsClient.invokeWithSign(String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    recRequest.setReCode(1);
                    recRequest.setMessage("匹配数据失败，请联系系统管理员");
                    return recRequest;
                }

            }
        }

        for (Long headerId : sendcarHeaderIds) {
            FmsTransportSettledLine fmsTransportSettledLine = new FmsTransportSettledLine();
            fmsTransportSettledLine.setCreatedBy(settleTransportChargeV.getUserId());
            fmsTransportSettledLine.setLastUpdatedBy(settleTransportChargeV.getUserId());
            fmsTransportSettledLine.setLastUpdateLogin(settleTransportChargeV.getSessionId());
            fmsTransportSettledLine.setSendcarHeaderId(headerId);
            try {
                fmsCreateSettleTransportChargeService.relateData(fmsTransportSettledLine);
                //调用民生接口
                List<CavdsOrderSettleDetailsI> cavdsOrderSettleDetailsIList = new ArrayList<>();
                CavdsOrderSettleDetailsI headerVO = new CavdsOrderSettleDetailsI();
                headerVO.setSendcarHeaderId(headerId);
                cavdsOrderSettleDetailsIList.add(headerVO);

                String url = "/cavds_order_settle_details_i_inter";
                VdsClient vdsClient = new VdsDefaultClient(
                        ConstantUtil.INTER_URL + url, JSONObject.toJSONString(cavdsOrderSettleDetailsIList), "", "");
                vdsClient.invokeWithSign(String.class);
            } catch (Exception e) {
                e.printStackTrace();
                recRequest.setReCode(1);
                recRequest.setMessage("匹配数据失败，请联系系统管理员");
                return recRequest;
            }
        }
        return recRequest;
    }

    @RequestMapping("/export_settle_transport")
    @ResponseBody
    public Object exportSettleTransport(
            @RequestBody FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest
    ) {
        List<TmsSendcarHeaderFmsE> tmsSendcarHeaderEList = fmsSettleTransportChargeVService.export(fmsSettleTransportChargeVRequest);
        return new DetailResponse<>(tmsSendcarHeaderEList);
    }

    /**
     * 导入
     */
    @RequestMapping("import_settle_transport")
    @ResponseBody
    public BaseResponse importAllotCarrier(@RequestBody Map<String, Object> params) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Long userId = Long.valueOf(String.valueOf(params.get("userId")));
//			Long logisticsId = Long.valueOf(String.valueOf(params.get("logisticsId")));
            String sessionId = String.valueOf(params.get("sessionId"));
            String fileName = String.valueOf(params.get("fileName"));
            String fileString = String.valueOf(params.get("file"));
            Workbook workbook = FileEncodeUtil.parseWorkBoook(fileString, fileName);
            String result = fmsSettleTransportChargeVService.importSettleTransport(workbook, userId, sessionId, null);
            baseResponse = new DetailResponse<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            baseResponse.setError("0");
            baseResponse.setStatus(0);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }
}
