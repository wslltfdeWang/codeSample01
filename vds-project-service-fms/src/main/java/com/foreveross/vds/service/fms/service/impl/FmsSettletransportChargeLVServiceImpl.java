package com.foreveross.vds.service.fms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.FmsSettletransportChargeLVRequest;
import com.foreveross.vds.service.fms.mapper.FmsSettletransportChargeLVMapper;
import com.foreveross.vds.service.fms.service.FmsSettletransportChargeLVService;
import com.foreveross.vds.vo.fms.FmsSettletransportChargeLV;

@Service
public class FmsSettletransportChargeLVServiceImpl implements FmsSettletransportChargeLVService {

    @Autowired
    private FmsSettletransportChargeLVMapper fmsSettletransportChargeLVMapper;

    @Override
    public BasePageResponse getFmsSettleTransportChargeChildren(FmsSettletransportChargeLVRequest fmsSettletransportChargeLVRequest) {
        List<FmsSettletransportChargeLV> fmsSettletransportChargeLVS = fmsSettletransportChargeLVMapper.selectPmsSendcarLine(fmsSettletransportChargeLVRequest);

        for(FmsSettletransportChargeLV fmsSettletransportChargeLV : fmsSettletransportChargeLVS){
            fmsSettletransportChargeLV.set_parentId(fmsSettletransportChargeLVRequest.getSendcarHeaderId());
            fmsSettletransportChargeLV.setLeaf();
            fmsSettletransportChargeLV.setSendcarHeaderId(-fmsSettletransportChargeLVRequest.getSendcarHeaderId());//转换为负数避免ID重复
        }

        BasePageResponse basePageResponse = new BasePageResponse();
        basePageResponse.setTotal(Long.valueOf(String.valueOf(fmsSettletransportChargeLVS.size())));
        basePageResponse.setRows(fmsSettletransportChargeLVS);
        return basePageResponse;
    }

}
