package com.foreveross.vds.service.fms.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.fms.FmsSendcarFeeRelate;
import com.foreveross.vds.dto.fms.ReceiveAddressChangeRecord;
import com.foreveross.vds.dto.fms.ReceiveAddressChangeV;
import com.foreveross.vds.dto.fms.SendcarHeaderChangeV;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettled;
import com.foreveross.vds.dto.pms.PmsDcsCrmDealerInter;
import com.foreveross.vds.dto.pms.TransportRuleHeader;
import com.foreveross.vds.dto.tms.TmsSendcarHeader;
import com.foreveross.vds.service.fms.mapper.ReceiveAddressChangeRecordMapper;
import com.foreveross.vds.service.fms.service.ReceiveAddressChangeRecordService;
import com.foreveross.vds.vo.fms.FmsTransportContractLine;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class ReceiveAddressChangeRecordServiceImpl implements ReceiveAddressChangeRecordService {

	@Autowired
	private ReceiveAddressChangeRecordMapper receiveAddressChangeRecordMapper;

	@Override
	public Page<SendcarHeaderChangeV> querySendcarHeaderChangeV(SendcarHeaderChangeV sendcarHeaderChangeV) {
		// TODO Auto-generated method stub
		PageHelper.startPage(sendcarHeaderChangeV.getPage(), sendcarHeaderChangeV.getRows());
		Page<SendcarHeaderChangeV> page = (Page<SendcarHeaderChangeV>) receiveAddressChangeRecordMapper.querySendcarHeaderChangeV(sendcarHeaderChangeV);
		return page;
	}

	@Override
	public Page<ReceiveAddressChangeV> queryReceiveAddressChangeV(ReceiveAddressChangeV receiveAddressChangeV) {
		// TODO Auto-generated method stub
		PageHelper.startPage(receiveAddressChangeV.getPage(), receiveAddressChangeV.getRows());
		Page<ReceiveAddressChangeV> page = (Page<ReceiveAddressChangeV>) receiveAddressChangeRecordMapper.queryReceiveAddressChangeV(receiveAddressChangeV);
		return page;
	}

	@Override
	public List<PmsDcsCrmDealerInter> queryPmsDcsCrmDealerInter(PmsDcsCrmDealerInter pmsDcsCrmDealerInter) {
		// TODO Auto-generated method stub
		return receiveAddressChangeRecordMapper.queryPmsDcsCrmDealerInter(pmsDcsCrmDealerInter);
	}

	@Override
	@Transactional
	public void changeAdress(ReceiveAddressChangeV receiveAddressChangeV) {
		//添加送车交接单收车地址变更记录表
		ReceiveAddressChangeRecord receiveAddressChangeRecord = new ReceiveAddressChangeRecord();
		receiveAddressChangeRecord.setSendcarHeaderId(receiveAddressChangeV.getSendcarHeaderId());
		receiveAddressChangeRecord.setLastSoldToOrgId(receiveAddressChangeV.getLastSoldToOrgId());
		receiveAddressChangeRecord.setLastProvinceId(receiveAddressChangeV.getLastProvinceId());
		receiveAddressChangeRecord.setLastCityId(receiveAddressChangeV.getLastCityId());
		receiveAddressChangeRecord.setLastCountyId(receiveAddressChangeV.getLastCountyId());
		receiveAddressChangeRecord.setLastReceiveAddress(receiveAddressChangeV.getLastReceiveAddress());
		receiveAddressChangeRecord.setNowSoldToOrgId(receiveAddressChangeV.getNowSoldToOrgId());
		receiveAddressChangeRecord.setNowProvinceId(receiveAddressChangeV.getNowProvinceId());
		receiveAddressChangeRecord.setNowCityId(receiveAddressChangeV.getNowCityId());
		receiveAddressChangeRecord.setNowCountyId(receiveAddressChangeV.getNowCountyId());
		receiveAddressChangeRecord.setNowReceiveAddress(receiveAddressChangeV.getNowReceiveAddress());
		receiveAddressChangeRecord.setChageReason(receiveAddressChangeV.getChageReason());
		receiveAddressChangeRecord.setCreatedBy(receiveAddressChangeV.getUserId());
		receiveAddressChangeRecord.setCreationDate(new Date());
		receiveAddressChangeRecord.setLastUpdatedBy(receiveAddressChangeV.getUserId());
		receiveAddressChangeRecord.setLastUpdateDate(new Date());
		receiveAddressChangeRecord.setLastUpdateLogin(receiveAddressChangeV.getSessionId());
		
		receiveAddressChangeRecordMapper.addReceiveAddressChangeRecord(receiveAddressChangeRecord);
		
		//根据ID获取送车交接单头表
		TmsSendcarHeader tmsSendcarHeader = receiveAddressChangeRecordMapper.queryTmsSendcarHeaderById(receiveAddressChangeV.getSendcarHeaderId());
		
		if(tmsSendcarHeader != null) {
			//根据库存组织终点获取默认发运规则头
			TransportRuleHeader transportRuleHeader = new TransportRuleHeader();
			transportRuleHeader.setProvinceId(tmsSendcarHeader.getStartProvinceId());
			transportRuleHeader.setCityId(tmsSendcarHeader.getStartCityId());
			transportRuleHeader.setCountyId(tmsSendcarHeader.getStartCountyId());
			transportRuleHeader.setOrganizationId(tmsSendcarHeader.getOrganizationId());
			
			TransportRuleHeader transportRuleHeader1 = receiveAddressChangeRecordMapper.queryTransportRuleHeader(transportRuleHeader);
			
			//修改送车交接单头表
			if(transportRuleHeader1 != null) {
				tmsSendcarHeader.setTransportRuleHeaderId(transportRuleHeader1.getTransportRuleHeaderId());
				tmsSendcarHeader.setTransportMethodId(transportRuleHeader1.getTransportMethod());
				tmsSendcarHeader.setLogisticsId(transportRuleHeader1.getLogisticsId());
				receiveAddressChangeRecordMapper.updateTmsSendcarHeader(tmsSendcarHeader);
			}
		}
		//根据库存组织、终点省市县和发运方式获取合同头和行ID
		FmsTransportContractLine fmsTransportContractLine = new FmsTransportContractLine();
		fmsTransportContractLine.setTransportMethod(tmsSendcarHeader.getTransportMethodId());
		fmsTransportContractLine.setStartProvinceId(tmsSendcarHeader.getStartProvinceId());
		fmsTransportContractLine.setStartCityId(tmsSendcarHeader.getStartCityId());
		fmsTransportContractLine.setStartCountyId(tmsSendcarHeader.getStartCountyId());
		fmsTransportContractLine.setOrganizationId(tmsSendcarHeader.getOrganizationId());
		FmsTransportContractLine fmsTransportContractLine1 = receiveAddressChangeRecordMapper.queryFmsTransportContractLine(fmsTransportContractLine);
		//根据送车交接单头ID修改送车交接单费用关联表中合同头和行ID
		if (fmsTransportContractLine1 != null) {
			FmsSendcarFeeRelate fmsSendcarFeeRelate = new FmsSendcarFeeRelate();
			fmsSendcarFeeRelate.setTransportContractHeaderId(fmsTransportContractLine1.getTransportContractHeaderId());
			fmsSendcarFeeRelate.setTransportContractLineId(fmsTransportContractLine1.getTransportContractLineId());
			fmsSendcarFeeRelate.setSendcarHeaderId(tmsSendcarHeader.getSendcarHeaderId());
			receiveAddressChangeRecordMapper.updateFmsSendcarFeeRelateContract(fmsSendcarFeeRelate);
		}
		
	}
}
