package com.foreveross.vds.service.fms.service.impl.manualclearing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.manualclearing.ContractAllV;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettled;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledHeader;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLine;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledV;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.common.util.ConstantUtil;
import com.foreveross.vds.service.fms.mapper.manualclearing.ManualSettledMapper;
import com.foreveross.vds.service.fms.service.manualclearing.ManualSettledService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class ManualSettledServiceImpl implements ManualSettledService {

	@Autowired
	private ManualSettledMapper manualSettledMapper;

    @Autowired
    private LookupService lookupService;
	
	@Override
	public Page<FmsManualSettledV> queryFmsManualSettledVByPage(FmsManualSettledV fmsManualSettledV) {
		
		PageHelper.startPage(fmsManualSettledV.getPage(), fmsManualSettledV.getRows());
		Page<FmsManualSettledV> page = (Page<FmsManualSettledV>) manualSettledMapper.queryFmsManualSettledV(fmsManualSettledV);
		return page;
	}

	@Override
	public void addFmsManualSettled(FmsManualSettled fmsManualSettled) {
		
		manualSettledMapper.addFmsManualSettled(fmsManualSettled);
	}

	@Override
	public void updateFmsManualSettled(FmsManualSettled fmsManualSettled) {
		
		manualSettledMapper.updateFmsManualSettled(fmsManualSettled);
	}

	@Override
	public void delFmsManualSettled(Long manualSettledId) {
		
		manualSettledMapper.delFmsManualSettled(manualSettledId);
	}

	@Override
	public boolean checkSettledName(FmsManualSettled fmsManualSettled) {
		
		List<FmsManualSettled> list = manualSettledMapper.checkSettledName(fmsManualSettled);
		
		if(list != null && list.size() > 0) {
			return true;
		}
		
		return false;
	}

	@Override
	public Page<ContractAllV> queryContractAllVPage(ContractAllV contractAllV) {
		
		PageHelper.startPage(contractAllV.getPage(), contractAllV.getRows());
		Page<ContractAllV> page = (Page<ContractAllV>) manualSettledMapper.queryContractAllV(contractAllV);
		return page;
	}

	@Override
	@Transactional
	public RecRequest createBatchCode(List<FmsManualSettledV> fmsManualSettledVList) {
		
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		List<FmsManualSettledLine> lineList = new ArrayList<FmsManualSettledLine>();
		Long amount = 0L;
		for(int i=0;i<fmsManualSettledVList.size();i++) {
			FmsManualSettledV fmsManualSettledV = fmsManualSettledVList.get(i);
			
			for(int j=i;j<fmsManualSettledVList.size();j++) {
				FmsManualSettledV fmsManualSettledV1 = fmsManualSettledVList.get(j);
				
				if(fmsManualSettledV.getContractId().longValue() != fmsManualSettledV1.getContractId().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同合同数据，生成结算批次失败");
					return recRequest;
				}
				
				if(fmsManualSettledV.getStartPointId().longValue() != fmsManualSettledV1.getStartPointId().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同所属基地数据，生成结算批次失败");
					return recRequest;
				}
				
				if(fmsManualSettledV.getProvinceId().longValue() != fmsManualSettledV1.getProvinceId().longValue()
						|| fmsManualSettledV.getCityId().longValue() != fmsManualSettledV1.getCityId().longValue()
						|| fmsManualSettledV.getCountyId().longValue() != fmsManualSettledV1.getCountyId().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同终点数据，生成结算批次失败");
					return recRequest;
				}
				
			}
			
			amount = amount + fmsManualSettledV.getAmount();
			
			FmsManualSettledLine fmsManualSettledLine = new FmsManualSettledLine();
			
			fmsManualSettledLine.setOrderNumber(Long.valueOf(i+1));
			fmsManualSettledLine.setManualSettledId(fmsManualSettledV.getManualSettledId());
			fmsManualSettledLine.setCreatedBy(fmsManualSettledV.getCreatedBy());
			fmsManualSettledLine.setCreationDate(fmsManualSettledV.getCreationDate());
			fmsManualSettledLine.setLastUpdatedBy(fmsManualSettledV.getLastUpdatedBy());
			fmsManualSettledLine.setLastUpdateDate(fmsManualSettledV.getLastUpdateDate());
			fmsManualSettledLine.setLastUpdateLogin(fmsManualSettledV.getLastUpdateLogin());
			lineList.add(fmsManualSettledLine);
			
			FmsManualSettled fmsManualSettled = this.getFmsManualSettledById(fmsManualSettledV.getManualSettledId());
			fmsManualSettled.setSettledFlag("Y");
			
			this.updateFmsManualSettled(fmsManualSettled);
		}
		
		if(fmsManualSettledVList.size() > 0) {
			FmsManualSettledV fmsManualSettledV = fmsManualSettledVList.get(0);

			String batchCode = this.getBatchCode(fmsManualSettledV.getManualSettledId());
			
			FmsManualSettledHeader fmsManualSettledHeader = new FmsManualSettledHeader();
			
			fmsManualSettledHeader.setBatchCode(batchCode);
			fmsManualSettledHeader.setBatchFee(amount);
//			fmsManualSettledHeader.setSettledStatus("N");
			fmsManualSettledHeader.setCreatedBy(fmsManualSettledV.getCreatedBy());
			fmsManualSettledHeader.setCreationDate(fmsManualSettledV.getCreationDate());
			fmsManualSettledHeader.setLastUpdatedBy(fmsManualSettledV.getLastUpdatedBy());
			fmsManualSettledHeader.setLastUpdateDate(fmsManualSettledV.getLastUpdateDate());
			fmsManualSettledHeader.setLastUpdateLogin(fmsManualSettledV.getLastUpdateLogin());
			fmsManualSettledHeader = this.addFmsManualSettledHeader(fmsManualSettledHeader);
			
			for(int i=0;i<lineList.size();i++) {
				FmsManualSettledLine fmsManualSettledLine = lineList.get(i);
				
				fmsManualSettledLine.setManualSettledHeaderId(fmsManualSettledHeader.getManualSettledHeaderId());
				
				this.addFmsFmsManualSettledLine(fmsManualSettledLine);
			}
			
			
		}
		
		return recRequest;
	}
	@Override
	@Transactional
	public RecRequest createStorageBatchCode(List<FmsManualSettledV> fmsManualSettledVList) {
		RecRequest recRequest = new RecRequest();
		recRequest.setReCode(0);
		
		List<FmsManualSettledLine> lineList = new ArrayList<FmsManualSettledLine>();
		Long amount = 0L;
		for(int i=0;i<fmsManualSettledVList.size();i++) {
			FmsManualSettledV fmsManualSettledV = fmsManualSettledVList.get(i);
			
			for(int j=i;j<fmsManualSettledVList.size();j++) {
				FmsManualSettledV fmsManualSettledV1 = fmsManualSettledVList.get(j);
				/*
				if(fmsManualSettledV.getStorageContactType().longValue() != fmsManualSettledV1.getStorageContactType().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同仓储合同类别，生成结算批次失败");
					return recRequest;
				}
				if(fmsManualSettledV.getContractId().longValue() != fmsManualSettledV1.getContractId().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同合同数据，生成结算批次失败");
					return recRequest;
				}
				
				if(fmsManualSettledV.getStartPointId().longValue() != fmsManualSettledV1.getStartPointId().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同所属基地数据，生成结算批次失败");
					return recRequest;
				}*/
				if(fmsManualSettledV.getLogisticsId().longValue() != fmsManualSettledV1.getLogisticsId().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同供应商，生成结算批次失败");
					return recRequest;
				}
				if(fmsManualSettledV.getOrganizationId().longValue() != fmsManualSettledV1.getOrganizationId().longValue()) {
					recRequest.setReCode(1);
					recRequest.setMessage("存在不同库存组织，生成结算批次失败");
					return recRequest;
				}
			}
			
			amount = amount + fmsManualSettledV.getAmount();
			
			FmsManualSettledLine fmsManualSettledLine = new FmsManualSettledLine();
//			fmsManualSettledLine.setManualSettledHeaderId(manualSettledHeaderId);
			fmsManualSettledLine.setOrderNumber(Long.valueOf(i+1));
			fmsManualSettledLine.setManualSettledId(fmsManualSettledV.getManualSettledId());
			fmsManualSettledLine.setCreatedBy(fmsManualSettledV.getCreatedBy());
			fmsManualSettledLine.setCreationDate(fmsManualSettledV.getCreationDate());
			fmsManualSettledLine.setLastUpdatedBy(fmsManualSettledV.getLastUpdatedBy());
			fmsManualSettledLine.setLastUpdateDate(fmsManualSettledV.getLastUpdateDate());
			fmsManualSettledLine.setLastUpdateLogin(fmsManualSettledV.getLastUpdateLogin());
			lineList.add(fmsManualSettledLine);
			
			FmsManualSettled fmsManualSettled = this.getFmsManualSettledById(fmsManualSettledV.getManualSettledId());
			fmsManualSettled.setSettledStatus(lookupService.getLookupId(ConstantUtil.LOOKUP_TYPE_SETTLE_STATUS, ConstantUtil.LOOKUP_CODE_SETTLE_STATUS_SUBMITED));
			fmsManualSettled.setSettledFlag("Y");
			manualSettledMapper.updateFmsManualSettledSelective(fmsManualSettled);
		}
		
		if(fmsManualSettledVList.size() > 0) {
			FmsManualSettledV fmsManualSettledV = fmsManualSettledVList.get(0);
//			TODO
			String logisticsCode=fmsManualSettledV.getLogisticsCode();
			String departureCode=fmsManualSettledV.getDepartureCode();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
			String dateCode=sdf.format(new Date());
			System.out.println(dateCode);
			String feeTypeCode=getFeeTypeCode(fmsManualSettledV.getStorageContactTypeName());
			String batchCode = logisticsCode+departureCode+dateCode+feeTypeCode;
			String lineCode=manualSettledMapper.getLineNumber();
			batchCode+=lineCode;
			FmsManualSettledHeader fmsManualSettledHeader = new FmsManualSettledHeader();
			fmsManualSettledHeader.setContractCategory(fmsManualSettledV.getContractCategory());
			fmsManualSettledHeader.setBatchCode(batchCode);
			fmsManualSettledHeader.setBatchFee(amount);
			fmsManualSettledHeader.setSettledStatus(lookupService.getLookupId(ConstantUtil.LOOKUP_TYPE_SETTLE_STATUS, ConstantUtil.LOOKUP_CODE_SETTLE_STATUS_UN_SUBMIT));
			fmsManualSettledHeader.setCreatedBy(fmsManualSettledV.getCreatedBy());
			fmsManualSettledHeader.setCreationDate(fmsManualSettledV.getCreationDate());
			fmsManualSettledHeader.setLastUpdatedBy(fmsManualSettledV.getLastUpdatedBy());
			fmsManualSettledHeader.setLastUpdateDate(fmsManualSettledV.getLastUpdateDate());
			fmsManualSettledHeader.setLastUpdateLogin(fmsManualSettledV.getLastUpdateLogin());
			fmsManualSettledHeader = this.addFmsManualSettledHeader(fmsManualSettledHeader);
			
			for(int i=0;i<lineList.size();i++) {
				FmsManualSettledLine fmsManualSettledLine = lineList.get(i);
				
				fmsManualSettledLine.setManualSettledHeaderId(fmsManualSettledHeader.getManualSettledHeaderId());
				
				this.addFmsFmsManualSettledLine(fmsManualSettledLine);
			}
			
			
		}
		
		return recRequest;
	}

	@Override
	public String getBatchCode(Long manualSettledId) {
		
		return manualSettledMapper.getBatchCode(manualSettledId);
	}

	@Override
	public FmsManualSettledHeader addFmsManualSettledHeader(FmsManualSettledHeader fmsManualSettledHeader) {
		
		manualSettledMapper.addFmsManualSettledHeader(fmsManualSettledHeader);
		return fmsManualSettledHeader;
	}

	@Override
	public void addFmsFmsManualSettledLine(FmsManualSettledLine fmsManualSettledLine) {
		
		manualSettledMapper.addFmsFmsManualSettledLine(fmsManualSettledLine);
	}

	@Override
	public FmsManualSettled getFmsManualSettledById(Long manualSettledId) {
		
		return manualSettledMapper.getFmsManualSettledById(manualSettledId);
	}

	private String getFeeTypeCode(String storageContactTypeName ) {
		String feeType="";
		switch (storageContactTypeName) {
		case ConstantUtil.STORAGE_CONTACT_TYPE_NAME_LEASEHOLD:
			feeType= ConstantUtil.FEE_TYPE_CODE_LEASEHOLD;
			break;
		case ConstantUtil.STORAGE_CONTACT_TYPE_NAME_MOVECAR:
			feeType= ConstantUtil.FEE_TYPE_CODE_MOVECAR;
			break;
		case ConstantUtil.STORAGE_CONTACT_TYPE_NAME_CLENING:
			feeType= ConstantUtil.FEE_TYPE_CODE_CLENING;
			break;
		case ConstantUtil.STORAGE_CONTACT_TYPE_NAME_WAREHOUSE_MANAGE:
			feeType= ConstantUtil.FEE_TYPE_CODE_WAREHOUSE_MANAGE;
			break;
		}
		return feeType;
	}
}
