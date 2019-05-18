package com.foreveross.vds.service.fms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.FmsExpenseSettledRelate;
import com.foreveross.vds.service.fms.mapper.FmsExpenseSettledRelateMapper;
import com.foreveross.vds.service.fms.service.FmsExpenseSettledRelateService;
@Service
public class FmsExpenseSettledRelateServiceImpl implements FmsExpenseSettledRelateService {

	@Autowired
	private FmsExpenseSettledRelateMapper fmsExpenseSettledRelateMapper;

	@Override
	public void addFmsExpenseSettledRelateByList(List<FmsExpenseSettledRelate> list) {
		// TODO Auto-generated method stub
		fmsExpenseSettledRelateMapper.addFmsExpenseSettledRelateByList(list);
	}

	@Override
	public List<FmsExpenseSettledRelate> queryFmsExpenseSettledRelateList(
			FmsExpenseSettledRelate fmsExpenseSettledRelate) {
		// TODO Auto-generated method stub
		return fmsExpenseSettledRelateMapper.queryFmsExpenseSettledRelateList(fmsExpenseSettledRelate);
	}
}
