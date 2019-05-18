package com.foreveross.vds.service.fms.service.impl;

import com.foreveross.vds.service.fms.mapper.FmsTransportSettledLineVMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.service.common.service.SendcarHeaderService;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.service.FmsTransportSettledLineService;
import com.foreveross.vds.service.fms.service.FmsTransportSettledLineVService;
import com.foreveross.vds.vo.common.SendcarHeader;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;
import com.foreveross.vds.vo.fms.FmsTransportSettledLine;
import com.foreveross.vds.vo.fms.FmsTransportSettledLineV;

/**
 * @author luochong
 * @date 2018年8月8日 下午2:15:39
 * 
 * 
 */
@Service
public class FmsTransportSettledLineVServiceImpl extends BaseServiceImpl<FmsTransportSettledLineV, Long> implements FmsTransportSettledLineVService {

	@Autowired
	private SendcarHeaderService sendcarHeaderService;
	@Autowired
	private FmsTransportSettledLineService fmsTransportSettledLineService;
	
    @Autowired
    private FmsTransportSettledLineVMapper fmsTransportSettledLineVMapper;

    @Override
    public String queryTransportSettledLineTotalfee(Long[] transportSettledHeaderIds) {
        return fmsTransportSettledLineVMapper.queryTransportSettledLineTotalfee(transportSettledHeaderIds);
    }

	@Override
	public List<TmsSendcarHeaderFmsE> print(FmsTransportSettledHeader fmsTransportSettledHeader) {
		return fmsTransportSettledLineVMapper.print(fmsTransportSettledHeader);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(FmsTransportSettledLine fmsTransportSettledLine) {
		FmsTransportSettledLine fmsTransportSettledLineOld = fmsTransportSettledLineService.selectByPrimaryKey(fmsTransportSettledLine.getTransportSettledLineId());
		fmsTransportSettledLineOld.setEnableFlag("N");
		fmsTransportSettledLineOld.setLastUpdateDate(fmsTransportSettledLine.getLastUpdateDate());
		fmsTransportSettledLineOld.setLastUpdatedBy(fmsTransportSettledLine.getLastUpdatedBy());
		fmsTransportSettledLineOld.setLastUpdateLogin(fmsTransportSettledLine.getLastUpdateLogin());
		fmsTransportSettledLineService.updateByPrimaryKeySelective(fmsTransportSettledLineOld);
		
		SendcarHeader sendcarHeader = sendcarHeaderService.selectByPrimaryKey(fmsTransportSettledLineOld.getSendcarHeaderId());
		sendcarHeader.setSettledFlag("N");
		sendcarHeader.setLastUpdateDate(fmsTransportSettledLine.getLastUpdateDate());
		sendcarHeader.setLastUpdatedBy(fmsTransportSettledLine.getLastUpdatedBy());
		sendcarHeader.setLastUpdateLogin(fmsTransportSettledLine.getLastUpdateLogin());
		sendcarHeaderService.updateByPrimaryKeySelective(sendcarHeader);
	}
}
