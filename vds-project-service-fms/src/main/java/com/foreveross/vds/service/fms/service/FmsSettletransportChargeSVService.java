package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.FmsSettletransportChargeLVRequest;

public interface FmsSettletransportChargeSVService {

    /**
     * 根据sendcarHeaderId 查询送车交接单行统计数据作为树的第二层
     * 返回满足eazyui树格式的数据
     * @param fmsSettletransportChargeLVRequest
     * @return
     */
    BasePageResponse getFmsSettleTransportChargeChildren(FmsSettletransportChargeLVRequest fmsSettletransportChargeLVRequest);

    /**
     * 据sendcarHeaderId 查询送车交接单行统计数据的费用合计
     * @param sendcarHeaderIds
     * @return
     */
    String getFmsSettletransportChargeSTotalFee(Long[] sendcarHeaderIds);
}
