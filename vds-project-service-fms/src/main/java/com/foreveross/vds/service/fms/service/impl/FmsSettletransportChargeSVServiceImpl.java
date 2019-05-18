package com.foreveross.vds.service.fms.service.impl;

import java.util.List;

import com.foreveross.vds.util.tools.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.FmsSettletransportChargeLVRequest;
import com.foreveross.vds.service.fms.mapper.FmsSettletransportChargeSVMapper;
import com.foreveross.vds.service.fms.service.FmsSettletransportChargeSVService;
import com.foreveross.vds.vo.fms.FmsSettletransportChargeSV;

@Service
public class FmsSettletransportChargeSVServiceImpl implements FmsSettletransportChargeSVService {

    @Autowired
    private FmsSettletransportChargeSVMapper fmsSettletransportChargeSVMapper;

    @Override
    public BasePageResponse getFmsSettleTransportChargeChildren(FmsSettletransportChargeLVRequest fmsSettletransportChargeLVRequest) {
        List<FmsSettletransportChargeSV> fmsSettletransportChargeSVList = fmsSettletransportChargeSVMapper.selectPmsSendcarLineStatistics(fmsSettletransportChargeLVRequest);

        for(FmsSettletransportChargeSV fmsSettletransportChargeSV : fmsSettletransportChargeSVList){
            fmsSettletransportChargeSV.set_parentId(fmsSettletransportChargeSV.getSendcarHeaderId());
            fmsSettletransportChargeSV.setLeaf();
            fmsSettletransportChargeSV.setSendcarHeaderId(fmsSettletransportChargeSV.getSendcarHeaderId());
            //以下操作为了使eazyui treegrid fieldId不出重复
            fmsSettletransportChargeSV.setEazyuiTreegridId(fmsSettletransportChargeSV.getSendcarHeaderId()+"-"+fmsSettletransportChargeSV.getSencarLineId());
        }

        BasePageResponse basePageResponse = new BasePageResponse();
        basePageResponse.setTotal(Long.valueOf(String.valueOf(fmsSettletransportChargeSVList.size())));
        basePageResponse.setRows(fmsSettletransportChargeSVList);
        return basePageResponse;
    }

    @Override
    public String getFmsSettletransportChargeSTotalFee(Long[] sendcarHeaderIds) {
        String totalFee = fmsSettletransportChargeSVMapper.getFmsSettletransportChargeSTotalFee(sendcarHeaderIds);
        if(StringHelper.isEmpty(totalFee)){
            return "0";
        }
        return totalFee;
    }
}
