package com.foreveross.vds.service.fms.service.impl.manualclearing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledHeader;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLine;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledLineV;
import com.foreveross.vds.dto.fms.manualclearing.FmsManualSettledV;
import com.foreveross.vds.service.fms.mapper.manualclearing.AuditManualSettledMapper;
import com.foreveross.vds.service.fms.service.manualclearing.AuditManualSettledServce;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class AuditManualSettledServceImpl implements AuditManualSettledServce {
	@Autowired
	private AuditManualSettledMapper auditManualSettledMapper;

	@Override
	public Page<FmsManualSettledHeader> queryFmsManualSettledHeaderPage(FmsManualSettledHeader fmsManualSettledHeader) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsManualSettledHeader.getPage(), fmsManualSettledHeader.getRows());
		Page<FmsManualSettledHeader> page = (Page<FmsManualSettledHeader>) auditManualSettledMapper.queryFmsManualSettledHeader(fmsManualSettledHeader);
		return page;
	}

	@Override
	public void updateFmsManualSettledHeader(FmsManualSettledHeader fmsManualSettledHeader) {
		// TODO Auto-generated method stub
		auditManualSettledMapper.updateFmsManualSettledHeader(fmsManualSettledHeader);
	}

	@Override
	public List<FmsManualSettledLine> queryFmsManualSettledLine(FmsManualSettledLine fmsManualSettledLine) {
		// TODO Auto-generated method stub
		return auditManualSettledMapper.queryFmsManualSettledLine(fmsManualSettledLine);
	}

	@Override
	public void updateFmsManualSettledLine(FmsManualSettledLine fmsManualSettledLine) {
		// TODO Auto-generated method stub
		auditManualSettledMapper.updateFmsManualSettledLine(fmsManualSettledLine);
	}

	@Override
	public List<FmsManualSettledLineV> queryFmsManualSettledLineV(FmsManualSettledLineV fmsManualSettledLineV) {
		// TODO Auto-generated method stub
		return auditManualSettledMapper.queryFmsManualSettledLineV(fmsManualSettledLineV);
	}
}
