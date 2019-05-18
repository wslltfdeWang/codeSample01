package com.foreveross.vds.service.fms.service.impl;

import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsManualSettledMapper;
import com.foreveross.vds.service.fms.service.FmsManualSettledService;
import com.foreveross.vds.service.fms.service.FmsTransportContractHeaderService;
import com.foreveross.vds.vo.fms.FmsManualSettled;
import com.foreveross.vds.vo.fms.FmsTransportContractHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
public class FmsManualSettledServiceImpl extends BaseServiceImpl<FmsManualSettled, Long> implements FmsManualSettledService {

    @Autowired
    private FmsManualSettledMapper fmsManualSettledMapper;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private FmsTransportContractHeaderService fmsTransportContractHeaderService;

    @Override
    public void insertOrUpdateFmsManualSettled(FmsManualSettled fmsManualSettled, Long userId, String sessionId) {
        Long manualSettledId = fmsManualSettled.getManualSettledId();
        if(manualSettledId == null){
            doSaveFmsManualSettled(fmsManualSettled, userId, sessionId);
        }else{
            doUpdateFmsManualSettled(fmsManualSettled, userId, sessionId);
        }
    }

    @Override
    public void deleteFmsManualSettled(List<Long> manualSettledIds) {
        for(Long manualSettledId : manualSettledIds){
            fmsManualSettledMapper.deleteByPrimaryKey(manualSettledId);
        }
    }

    private void doUpdateFmsManualSettled(FmsManualSettled fmsManualSettled, Long userId, String sessionId) {
        fmsManualSettled.setLastUpdatedBy(userId);
        fmsManualSettled.setLastUpdateDate(new Date());
        fmsManualSettled.setLastUpdateLogin(sessionId);
        fmsManualSettledMapper.updateByPrimaryKeySelective(fmsManualSettled);
    }

    private void doSaveFmsManualSettled(FmsManualSettled fmsManualSettled, Long userId, String sessionId) {
        Date now = new Date();
        fmsManualSettled.setContractName(getContractName(fmsManualSettled));
        fmsManualSettled.setContractCategory(lookupService.getLookupId("CONTRACT_CATEGORY", "TRANSPORT_CONTRACT"));
        fmsManualSettled.setSettledStatus(lookupService.getLookupId("SETTLE_STATUS", "UN_SUBMIT"));
        fmsManualSettled.setContractCategory(lookupService.getLookupId("CONTRACT_CATEGORY", "TRANSPORT_CONTRACT"));
        fmsManualSettled.setSettledFlag("N");

        fmsManualSettled.setCreatedBy(userId);
        fmsManualSettled.setCreationDate(now);
        fmsManualSettled.setLastUpdateDate(now);
        fmsManualSettled.setLastUpdatedBy(userId);
        fmsManualSettled.setLastUpdateLogin(sessionId);
        fmsManualSettledMapper.insertSelective(fmsManualSettled);
    }

    private String getContractName(FmsManualSettled fmsManualSettled) {
        FmsTransportContractHeader fmsTransportContractHeader = fmsTransportContractHeaderService.selectByPrimaryKey(fmsManualSettled.getContractId());
        return fmsTransportContractHeader.getContractName();
    }
}
