package com.foreveross.vds.service.fms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsTransportContractLineMapper;
import com.foreveross.vds.service.fms.service.FmsTransportContractLineService;
import com.foreveross.vds.vo.fms.FmsTransportContractLine;

@Service
public class FmsTransportContractLineServiceImpl extends BaseServiceImpl<FmsTransportContractLine,Long> implements FmsTransportContractLineService {

    @Autowired
    private FmsTransportContractLineMapper fmsTransportContractLineMapper;

}
