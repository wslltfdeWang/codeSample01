package com.foreveross.vds.service.fms.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.common.service.SendcarHeaderService;
import com.foreveross.vds.service.common.service.SendcarLineService;
import com.foreveross.vds.service.common.service.TransToolService;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.SendcarPrintVMapper;
import com.foreveross.vds.service.fms.mapper.TmsSendcarPrintRecordMapper;
import com.foreveross.vds.service.fms.service.SendcarPrintVService;
import com.foreveross.vds.vo.common.SendcarHeader;
import com.foreveross.vds.vo.fms.SendcarPrintVNew;
import com.foreveross.vds.vo.fms.TmsSendcarPrintRecord;

@Service
public class SendcarPrintVServiceImpl extends BaseServiceImpl<SendcarPrintVNew, Long> implements SendcarPrintVService {
	@Autowired
	SendcarPrintVMapper mapper;

	@Autowired
	private TransToolService transToolService;

	@Autowired
	private SendcarHeaderService sendcarHeaderService;

	@Autowired
	private SendcarLineService sendcarLineService;

	@Autowired
	private LookupService lookupService;
	
	@Autowired
	private TmsSendcarPrintRecordMapper sendcarPrintRecordMapper;
	
	@Override
	public List<TmsSendcarLine> queryLineList(SendcarPrintVNew entity) {
		return this.mapper.queryLineList(entity);
	}

	@Override
	@CustTx
	public void sendCarPrintCallback(List<Long> sendcarHeaderIds, Long userId, String sessionId) throws BusinessServiceException {
		for(Long sendcarHeaderId : sendcarHeaderIds){
			sendCarPrintCallback(sendcarHeaderId, userId, sessionId);
		}
	}

	public void sendCarPrintCallback(Long sendcarHeaderId, Long userId, String sessionId) throws BusinessServiceException {
		//如果已经打印，则跳过不修改
		SendcarHeader sendcarHeader = sendcarHeaderService.selectByPrimaryKey(sendcarHeaderId);
		String printFlag = sendcarHeader.getPrintFlag();
		Date jjdPrintDate = sendcarHeader.getJjdPrintDate();
		
		//修改打印标识
		updatePrintFlag(sendcarHeaderId, userId, sessionId);
		//交接单补打印记录表
		addSendcarPrintRecord(userId, sessionId, sendcarHeaderId,printFlag);
		
		if("Y".equals(printFlag) && jjdPrintDate != null){
			return;
		}
		//修改板车状态为在途
//		updateTransToolStatus(sendcarHeaderId, userId, sessionId);

		//如果是自提，将头表和行表的到达状态修改已到达
		updateArrivedStatus(sendcarHeaderId, userId, sessionId);
	}
	
	public void sendCarOvertimeCallback(Long sendcarHeaderId, Long userId, String sessionId) throws BusinessServiceException {
		SendcarHeader sendcarHeader = sendcarHeaderService.selectByPrimaryKey(sendcarHeaderId);
		sendcarHeader.setPrintFlag("Y");
		sendcarHeader.setLastUpdatedBy(userId);
		sendcarHeader.setLastUpdateDate(new Date());
		sendcarHeader.setLastUpdateLogin(sessionId);
		sendcarHeaderService.updateByPrimaryKeySelective(sendcarHeader);
	}

	private void addSendcarPrintRecord(Long userId, String sessionId,Long sendcarHeaderId,String printFlag) {
		if("Y".equals(printFlag)){
			TmsSendcarPrintRecord record = new TmsSendcarPrintRecord();
			Date printDate = new Date();
			record.setSendcarHeaderId(sendcarHeaderId);
			record.setPrintMan(userId);
			record.setPrintDate(printDate);
			record.setCreatedBy(userId);
			record.setLastUpdatedBy(userId);
			record.setCreationDate(printDate);
			record.setLastUpdateDate(printDate);
			record.setLastUpdateLogin(sessionId);
			sendcarPrintRecordMapper.insertSelective(record);
		}
	}

	private void updateArrivedStatus(Long sendcarHeaderId, Long userId, String sessionId) {
		SendcarHeader sendcarHeader = sendcarHeaderService.selectByPrimaryKey(sendcarHeaderId);
        Long transportMethodId = sendcarHeader.getTransportMethodId();
        //应刘主任和张处20180921下午的要求取更新人工发运方式的交接状态为到达
//        Long manual = lookupService.findLookUpId("TRANSPORT_METHOD", "MANUAL");
		Long self = lookupService.findLookUpId("TRANSPORT_METHOD", "SELF");

		if(transportMethodId != self){
			return;
		}

		updateSendcarHeaderArrivedStatus(sendcarHeaderId, userId, sessionId);
        updateSendcarLineArrivedStatus(sendcarHeaderId, userId, sessionId);

	}

    private void updateSendcarLineArrivedStatus(Long sendcarHeaderId, Long userId, String sessionId) {
        Long arrivedStatus = lookupService.getLookupId("ARRIVED_STATUS", "ARRIVE");
        sendcarLineService.updateSendcarLineArrivedStatus(sendcarHeaderId, arrivedStatus, userId, sessionId);
    }

    private void updateSendcarHeaderArrivedStatus(Long sendcarHeaderId, Long userId, String sessionId) {
	    Date now = new Date();
        SendcarHeader sendcarHeader = sendcarHeaderService.selectByPrimaryKey(sendcarHeaderId);
        sendcarHeader.setArrivedStatus(lookupService.getLookupId("ARRIVED_STATUS", "ARRIVE"));
        sendcarHeader.setArrivedDate(now);
        sendcarHeader.setArrivedQuantity(sendcarHeader.getCarQuantity());
        sendcarHeader.setLastUpdatedBy(userId);
        sendcarHeader.setLastUpdateDate(now);
        sendcarHeader.setLastUpdateLogin(sessionId);

        sendcarHeaderService.updateByPrimaryKeySelective(sendcarHeader);
    }

    private void updatePrintFlag(Long sendcarHeaderId, Long userId, String sessionId) {
		SendcarHeader sendcarHeader = sendcarHeaderService.selectByPrimaryKey(sendcarHeaderId);
		sendcarHeader.setPrintFlag("Y");
		sendcarHeader.setJjdPrintTime((sendcarHeader.getJjdPrintTime() == null ? 0 : sendcarHeader.getJjdPrintTime()) + 1);
		sendcarHeader.setJjdPrintDate(new Date());

		sendcarHeader.setLastUpdatedBy(userId);
		sendcarHeader.setLastUpdateDate(new Date());
		sendcarHeader.setLastUpdateLogin(sessionId);
		sendcarHeaderService.updateByPrimaryKeySelective(sendcarHeader);
	}

	@SuppressWarnings("unused")
	private void updateTransToolStatus(Long sendcarHeaderId, Long userId, String sessionId) throws BusinessServiceException {
		SendcarHeader sendcarHeader = sendcarHeaderService.selectByPrimaryKey(sendcarHeaderId);

		Long transToolId = sendcarHeader.getTransToolId();
		if(transToolId == null){
//			throw new BusinessServiceException("sendcarHeaderId = ["+sendcarHeaderId+"]修改板车状态失败，板车ID为NULL！");
		}else{
			transToolService.updateTransToolStatus(transToolId, "transporting", userId, sessionId);
		}

	}
}
