package com.foreveross.vds.service.fms.controller;

import com.alibaba.fastjson.JSONObject;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.FmsSettleTransportChargeVRequest;
import com.foreveross.vds.dto.fms.FmsSettletransportChargeLVRequest;
import com.foreveross.vds.service.fms.service.FmsSettleTransportChargeVService;
import com.foreveross.vds.service.fms.service.FmsSettletransportChargeSVService;
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
 * 待结算运费维护
 */
@Controller
public class FmsSettleTransportChargeController {

    private static Logger logger = LoggerFactory.getLogger(FmsSettleTransportChargeController.class);

    @Autowired
    private FmsSettleTransportChargeVService fmsSettleTransportChargeVService;

    @Autowired
    private FmsSettletransportChargeSVService fmsSettletransportChargeSVService;

    /**
     * 分页查询送车交接单头数据作为树的第一层
     * 返回满足eazyui树格式的数据
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/page_fms_settle_transport_charge.do")
    public Object pageFmsSettleTransportCharge(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = null;
        try {
            Integer page = (Integer) params.get("page");
            Integer rows = (Integer) params.get("rows");
            FmsSettleTransportChargeVRequest whereClause = JSONObject.parseObject(
                    JSONObject.toJSONString(params.get("whereClause")),
                    FmsSettleTransportChargeVRequest.class);


            //库存组织没有查询条件默认查询当前用户下的库存组织
            if(whereClause.getOrganizationIds() == null || whereClause.getOrganizationIds().size() == 0){
                String organizationIdStr = String.valueOf(params.get("organizationIds"));
                List<Long> organizationIdArray = JSONObject.parseArray(organizationIdStr, Long.class);
                if(organizationIdArray.size() == 0){
                    //该用户下没有库存组织则查询不到数据
//                    return new BasePageResponse(0L, new ArrayList());
                }
                whereClause.setOrganizationIds(organizationIdArray);
            }

            basePageResponse = fmsSettleTransportChargeVService.pageFmsSettleTransportChargeTree(page, rows, whereClause);
            fmsSettleTransportChargeVService.setEazyuiTreegridId(basePageResponse);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;

    }

    /**
     * 根据sendcarHeaderId 查询送车交接单行统计数据作为树的第二层
     * 返回满足eazyui树格式的数据
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/list_fms_settle_transport_charge_s.do")
    public Object listFmsSettletransportChargeS(@RequestBody Map<String, Object> params){
        BasePageResponse basePageResponse = null;
        try {
            FmsSettletransportChargeLVRequest fmsSettletransportChargeLVRequest = JSONObject.parseObject(
                    String.valueOf(params.get("whereClause")),
                    FmsSettletransportChargeLVRequest.class);
            basePageResponse = fmsSettletransportChargeSVService.getFmsSettleTransportChargeChildren(fmsSettletransportChargeLVRequest);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return basePageResponse;
    }

    /**
     * 根据sendcarHeaderId 查询送车交接单行统计数据的费用合计
     * @param sendcarHeaderIds
     * @return
     */
    @ResponseBody
    @RequestMapping("/get_fms_settle_transport_charge_s_totalfee.do")
    public Object getFmsSettletransportChargeSTotalFee(@RequestBody Long[] sendcarHeaderIds){
        DetailResponse detailResponse = new DetailResponse();
        try {
          String totalFee = "0";
            if(sendcarHeaderIds == null || sendcarHeaderIds.length == 0){
                detailResponse.setDetail(totalFee);
            }else{
                totalFee = fmsSettletransportChargeSVService.getFmsSettletransportChargeSTotalFee(sendcarHeaderIds);
                detailResponse.setDetail(totalFee);
            }
        }catch (Exception e){
            detailResponse.setStatus(0);
            logger.error(e.getMessage(), e);
        }
        return detailResponse;
    }




}
