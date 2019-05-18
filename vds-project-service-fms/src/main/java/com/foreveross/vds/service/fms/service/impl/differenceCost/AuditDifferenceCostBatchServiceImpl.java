package com.foreveross.vds.service.fms.service.impl.differenceCost;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledLine;
import com.foreveross.vds.service.fms.mapper.differenceCost.FmsRefundSettledHeaderMapper;
import com.foreveross.vds.service.fms.mapper.differenceCost.FmsRefundSettledLineMapper;
import com.foreveross.vds.service.fms.service.differenceCost.AuditDifferenceCostBatchService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class AuditDifferenceCostBatchServiceImpl implements AuditDifferenceCostBatchService {
	@Autowired
	private FmsRefundSettledHeaderMapper fmsRefundSettledHeaderMapper;
	@Autowired
	private FmsRefundSettledLineMapper fmsRefundSettledLineMapper;
	
	@Override
	public Page<FmsRefundSettledHeader> queryFmsRefundSettledHeaderByPage(
			FmsRefundSettledHeader fmsRefundSettledHeader) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsRefundSettledHeader.getPage(), fmsRefundSettledHeader.getRows());
		Page<FmsRefundSettledHeader> page = (Page<FmsRefundSettledHeader>) fmsRefundSettledHeaderMapper.queryFmsRefundSettledHeader(fmsRefundSettledHeader);
		return page;
	}

	@Override
	@Transactional
	public void auditFmsRefundSettledLine(FmsRefundSettledLine fmsRefundSettledLine) {
		// TODO Auto-generated method stub
		fmsRefundSettledLineMapper.auditFmsRefundSettledLine(fmsRefundSettledLine);
		
		if("Y".equals(fmsRefundSettledLine.getRejectFlag())) {//如果为驳回操作
			FmsRefundSettledLine queryFmsRefundSettledLine = new FmsRefundSettledLine();
			queryFmsRefundSettledLine.setRefundSettledHeaderId(fmsRefundSettledLine.getRefundSettledHeaderId());
			List<FmsRefundSettledLine> list = fmsRefundSettledLineMapper.queryFmsRefundSettledLine(fmsRefundSettledLine);
			
			boolean toAuditHeader = true;
			for(FmsRefundSettledLine line : list) {//判断头ID对应的所有行数据是否被驳回
				
				if(!"Y".equals(line.getRejectFlag())) {
					toAuditHeader = false;
					break;
				}
			}
			
			if(toAuditHeader) {
				FmsRefundSettledHeader fmsRefundSettledHeader = new FmsRefundSettledHeader();
				fmsRefundSettledHeader.setRefundSettledHeaderId(fmsRefundSettledLine.getRefundSettledHeaderId());
				fmsRefundSettledLine.setRejectFlag("Y");
				fmsRefundSettledLine.setRejectReason("所有行数据被驳回，自动驳回头数据");
				fmsRefundSettledHeaderMapper.auditFmsRefundSettledHeader(fmsRefundSettledHeader);
			}
		}
	}

	@Override
	@Transactional
	public List<FmsRefundSettledLine> queryFmsRefundSettledLine(FmsRefundSettledLine fmsRefundSettledLine) {
		// TODO Auto-generated method stub
		return fmsRefundSettledLineMapper.queryFmsRefundSettledLine(fmsRefundSettledLine);
	}

	@Override
	@Transactional
	public void auditFmsRefundSettledHeader(FmsRefundSettledHeader fmsRefundSettledHeader) {
		// TODO Auto-generated method stub
		fmsRefundSettledHeaderMapper.auditFmsRefundSettledHeader(fmsRefundSettledHeader);
		
		if("Y".equals(fmsRefundSettledHeader.getRejectFlag())) {//驳回操作
			FmsRefundSettledLine fmsRefundSettledLine = new FmsRefundSettledLine();
			fmsRefundSettledLine.setRejectFlag("Y");
			fmsRefundSettledLine.setRejectReason("头数据被驳回，行数据自动驳回");
			fmsRefundSettledLine.setRefundSettledHeaderId(fmsRefundSettledLine.getRefundSettledHeaderId());
			fmsRefundSettledLineMapper.auditFmsRefundSettledLineByefundSettledHeaderId(fmsRefundSettledLine);
		}else {
			FmsRefundSettledLine fmsRefundSettledLine = new FmsRefundSettledLine();
			fmsRefundSettledLine.setRejectFlag("N");
			fmsRefundSettledLineMapper.auditFmsRefundSettledLineByefundSettledHeaderId(fmsRefundSettledLine);
		}
	}
}
