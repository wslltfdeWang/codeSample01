package com.foreveross.vds.service.fms.service.impl.storagecontract;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeader;
import com.foreveross.vds.service.fms.mapper.storagecontract.FmsStorageSettledHeaderMapper;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledHeaderService;
@Service
public class FmsStorageSettledHeaderServiceImpl implements FmsStorageSettledHeaderService {
	@Autowired
	private FmsStorageSettledHeaderMapper fmsStorageSettledHeaderMapper;
	
	@Override
	public FmsStorageSettledHeader addFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader) throws Exception {
		// TODO Auto-generated method stub
		fmsStorageSettledHeaderMapper.addFmsStorageSettledHeader(fmsStorageSettledHeader);
		return fmsStorageSettledHeader;
	}

	@Override
	public String getCode(Long storageContractId) {
		// TODO Auto-generated method stub
		Calendar nowtime = new GregorianCalendar(); 
		String batchCode = fmsStorageSettledHeaderMapper.getCode(storageContractId);
		batchCode = batchCode + nowtime.get(Calendar.YEAR) + nowtime.get(Calendar.MONTH)
			+ nowtime.get(Calendar.DATE) + nowtime.get(Calendar.HOUR) + nowtime.get(Calendar.MINUTE)
			+ nowtime.get(Calendar.SECOND) + nowtime.get(Calendar.MILLISECOND);
		return batchCode;
	}

	@Override
	public void rejectedFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader) throws Exception {
		// TODO Auto-generated method stub
		fmsStorageSettledHeaderMapper.rejectedFmsStorageSettledHeader(fmsStorageSettledHeader);
	}

	@Override
	public void allowFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader)  throws Exception{
		// TODO Auto-generated method stub
		fmsStorageSettledHeaderMapper.allowFmsStorageSettledHeader(fmsStorageSettledHeader);
	}


}
