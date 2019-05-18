package com.foreveross.vds.service.fms.service.impl.storagecontract;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLine;
import com.foreveross.vds.service.fms.mapper.storagecontract.FmsStorageSettledLineMapper;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledLineService;
@Service
public class FmsStorageSettledLineServiceImpl implements FmsStorageSettledLineService {

	@Autowired
	private FmsStorageSettledLineMapper fmsStorageSettledLineMapper;

	@Override
	public FmsStorageSettledLine addFmsStorageSettledLine(FmsStorageSettledLine fmsStorageSettledLine)
			throws Exception {
		// TODO Auto-generated method stub
		fmsStorageSettledLineMapper.addFmsStorageSettledLine(fmsStorageSettledLine);
		return fmsStorageSettledLine;
	}

	@Override
	public void rejectedFmsStorageSettledLine(FmsStorageSettledLine fmsStorageSettledLine) throws Exception {
		// TODO Auto-generated method stub
		fmsStorageSettledLineMapper.rejectedFmsStorageSettledLine(fmsStorageSettledLine);
	}

	@Override
	public List<FmsStorageSettledLine> getFmsStorageSettledLineByStorageSettledHeaderId(Long storageSettledHeaderId) {
		// TODO Auto-generated method stub
		
		return fmsStorageSettledLineMapper.getFmsStorageSettledLineByStorageSettledHeaderId(storageSettledHeaderId);
	}

	@Override
	public void allowFmsStorageSettledLine(List<Long> list) {
		// TODO Auto-generated method stub
		fmsStorageSettledLineMapper.allowFmsStorageSettledLine(list);
	}
}
