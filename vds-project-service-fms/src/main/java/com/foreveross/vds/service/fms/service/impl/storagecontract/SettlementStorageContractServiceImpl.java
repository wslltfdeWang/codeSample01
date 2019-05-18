package com.foreveross.vds.service.fms.service.impl.storagecontract;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeader;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLine;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledV;
import com.foreveross.vds.service.fms.mapper.storagecontract.SettlementStorageContractMapper;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledHeaderService;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledLineService;
import com.foreveross.vds.service.fms.service.storagecontract.SettlementStorageContractService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class SettlementStorageContractServiceImpl implements SettlementStorageContractService {

	@Autowired
	private SettlementStorageContractMapper settlementStorageContractMapper;
	@Autowired
	private FmsStorageSettledHeaderService fmsStorageSettledHeaderService;
	@Autowired
	private FmsStorageSettledLineService fmsStorageSettledLineService;
	
	@Override
	public Page<FmsStorageSettledV> querySettlementStorageContractByPage(FmsStorageSettledV fmsStorageSettledV)
			throws Exception {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsStorageSettledV.getPage(), fmsStorageSettledV.getRows());
		Page<FmsStorageSettledV> page = (Page<FmsStorageSettledV>) settlementStorageContractMapper.querySettlementStorageContract(fmsStorageSettledV);
		return page;
	}

	@Override
	public List<FmsStorageSettledV> querySettlementStorageContract(FmsStorageSettledV fmsStorageSettledV)
			throws Exception {
		// TODO Auto-generated method stub
		return settlementStorageContractMapper.querySettlementStorageContract(fmsStorageSettledV);
	}

	@Override
	public void create(List<FmsStorageSettledV> FmsStorageSettledVList) throws Exception {
		// TODO Auto-generated method stub
		FmsStorageSettledHeader fmsStorageSettledHeader = new FmsStorageSettledHeader();
//		fmsStorageSettledHeader.setSettledStatus("N");
		
		Long batchFee = 0L;
		
		for(int i=0;i<FmsStorageSettledVList.size();i++) {
			FmsStorageSettledV fmsStorageSettledV = FmsStorageSettledVList.get(i);
			batchFee = batchFee + fmsStorageSettledV.getAmount();
		}
		
		fmsStorageSettledHeader.setBatchFee(batchFee);
		
		//判断是否正式合同（不清楚怎么判断）
		if(this.isFormalContract(FmsStorageSettledVList.get(0).getContractId())) {
			fmsStorageSettledHeader.setFormalFlag("Y");
			fmsStorageSettledHeader.setRefundFlag("Y");
		}else {
			fmsStorageSettledHeader.setFormalFlag("N");
			fmsStorageSettledHeader.setRefundFlag("N");
		}
		String batchCode = fmsStorageSettledHeaderService.getCode(FmsStorageSettledVList.get(0).getContractId());
		fmsStorageSettledHeader.setCreationDate(new Date());
		fmsStorageSettledHeader.setBatchCode(batchCode);
		
		fmsStorageSettledHeader = fmsStorageSettledHeaderService.addFmsStorageSettledHeader(fmsStorageSettledHeader);
		
		for(int i=0;i<FmsStorageSettledVList.size();i++) {
			FmsStorageSettledV fmsStorageSettledV = FmsStorageSettledVList.get(i);
			
			FmsStorageSettledLine fmsStorageSettledLine = new FmsStorageSettledLine();
			
			fmsStorageSettledLine.setStorageSettledHeaderId(fmsStorageSettledHeader.getStorageSettledHeaderId());
			fmsStorageSettledLine.setOrderNumber(Long.valueOf(i+1));
			fmsStorageSettledLine.setStorageSettledId(fmsStorageSettledV.getStoragesettledid());
			fmsStorageSettledLine.setEnabledFlag("Y");
			fmsStorageSettledLine.setCreatedBy(1L);
			fmsStorageSettledLine.setCreationDate(new Date());
			
			fmsStorageSettledLineService.addFmsStorageSettledLine(fmsStorageSettledLine);
		}
	}

	@Override
	public Boolean isFormalContract(Long storageContractId) {
		// TODO Auto-generated method stub
		List<String> list = settlementStorageContractMapper.isFormalContract(storageContractId);
		
		if(list != null && list.size() > 0) {
			return true;
		}
		
		return false;
	}

}
