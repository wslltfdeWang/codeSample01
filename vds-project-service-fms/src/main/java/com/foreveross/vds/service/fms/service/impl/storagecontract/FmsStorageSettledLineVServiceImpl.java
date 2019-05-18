package com.foreveross.vds.service.fms.service.impl.storagecontract;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLineV;
import com.foreveross.vds.service.fms.mapper.storagecontract.FmsStorageSettledLineVMapper;
import com.foreveross.vds.service.fms.service.storagecontract.FmsStorageSettledLineVService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class FmsStorageSettledLineVServiceImpl implements FmsStorageSettledLineVService {

	@Autowired
	private FmsStorageSettledLineVMapper fmsStorageSettledLineVMapper;
	
	@Override
	public Page<FmsStorageSettledLineV> queryFmsStorageSettledLineVByPage(FmsStorageSettledLineV fmsStorageSettledLineV) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsStorageSettledLineV.getPage(), fmsStorageSettledLineV.getRows());
		Page<FmsStorageSettledLineV> page = (Page<FmsStorageSettledLineV>)fmsStorageSettledLineVMapper.queryFmsStorageSettledLineV(fmsStorageSettledLineV);
		return page;
	}
	
}
