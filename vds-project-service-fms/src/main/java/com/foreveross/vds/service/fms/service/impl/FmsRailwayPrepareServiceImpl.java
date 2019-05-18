package com.foreveross.vds.service.fms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.FmsRailwayPrepare;
import com.foreveross.vds.dto.fms.FmsRailwayPrepareV;
import com.foreveross.vds.service.fms.mapper.FmsRailwayPrepareMapper;
import com.foreveross.vds.service.fms.service.FmsRailwayPrepareService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class FmsRailwayPrepareServiceImpl implements FmsRailwayPrepareService {
	@Autowired
	private FmsRailwayPrepareMapper fmsRailwayPrepareMapper;

	@Override
	public Page<FmsRailwayPrepareV> queryFmsRailwayPrepareVByPage(FmsRailwayPrepareV fmsRailwayPrepareV) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsRailwayPrepareV.getPage(), fmsRailwayPrepareV.getRows());
		Page<FmsRailwayPrepareV> page = (Page<FmsRailwayPrepareV>) fmsRailwayPrepareMapper.queryFmsRailwayPrepareV(fmsRailwayPrepareV);
		return page;
	}

	@Override
	public void addFmsRailwayPrepare(FmsRailwayPrepare fmsRailwayPrepare) {
		// TODO Auto-generated method stub
		fmsRailwayPrepareMapper.addFmsRailwayPrepare(fmsRailwayPrepare);
	}

	@Override
	public void updateFmsRailwayPrepare(FmsRailwayPrepare fmsRailwayPrepare) {
		// TODO Auto-generated method stub
		fmsRailwayPrepareMapper.updateFmsRailwayPrepare(fmsRailwayPrepare);
	}

	@Override
	public void delFmsRailwayPrepare(FmsRailwayPrepare fmsRailwayPrepare) {
		// TODO Auto-generated method stub
		fmsRailwayPrepareMapper.delFmsRailwayPrepare(fmsRailwayPrepare);
	}
}
