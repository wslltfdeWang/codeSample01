package com.foreveross.vds.service.fms.service.impl.storagecontract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeaderV;
import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledV;
import com.foreveross.vds.service.fms.mapper.storagecontract.FmsStorageSettledHeaderVMapper;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledHeaderVService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class FmsStorageSettledHeaderVServiceImpl implements FmsStorageSettledHeaderVService {

	@Autowired
	private FmsStorageSettledHeaderVMapper fmsStorageSettledHeaderVMapper;
	
	@Override
	public Page<FmsStorageSettledHeaderV> queryFmsStorageSettledHeaderVByPage(
			FmsStorageSettledHeaderV fmsStorageSettledHeaderV) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsStorageSettledHeaderV.getPage(), fmsStorageSettledHeaderV.getRows());
		Page<FmsStorageSettledHeaderV> page = (Page<FmsStorageSettledHeaderV>) fmsStorageSettledHeaderVMapper.queryFmsStorageSettledHeaderV(fmsStorageSettledHeaderV);
		return page;
	}

}
