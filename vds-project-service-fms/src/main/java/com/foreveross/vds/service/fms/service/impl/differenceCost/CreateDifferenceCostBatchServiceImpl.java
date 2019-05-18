package com.foreveross.vds.service.fms.service.impl.differenceCost;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.fms.differencecost.DifferenceCostRequest;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettled;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledLine;
import com.foreveross.vds.service.fms.mapper.differenceCost.FmsRefundSettledHeaderMapper;
import com.foreveross.vds.service.fms.mapper.differenceCost.FmsRefundSettledLineMapper;
import com.foreveross.vds.service.fms.mapper.differenceCost.FmsRefundSettledMapper;
import com.foreveross.vds.service.fms.service.differenceCost.CreateDifferenceCostBatchService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class CreateDifferenceCostBatchServiceImpl implements CreateDifferenceCostBatchService {
	@Autowired
	private FmsRefundSettledMapper fmsRefundSettledMapper;
	@Autowired
	private FmsRefundSettledHeaderMapper fmsRefundSettledHeaderMapper;
	@Autowired
	private FmsRefundSettledLineMapper fmsRefundSettledLineMapper;

	@Override
	public Page<FmsRefundSettled> queryFmsRefundSettledByPage(FmsRefundSettled fmsRefundSettled) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsRefundSettled.getPage(), fmsRefundSettled.getRows());
		Page<FmsRefundSettled> page = (Page<FmsRefundSettled>) fmsRefundSettledMapper.queryFmsRefundSettled(fmsRefundSettled);
		return page;
	}

	@Override
	public void addFmsRefundSettledHeader(FmsRefundSettledHeader fmsRefundSettledHeader) {
		// TODO Auto-generated method stub
		fmsRefundSettledHeaderMapper.addFmsRefundSettledHeader(fmsRefundSettledHeader);
	}

	@Override
	public void addFmsRefundSettledLine(FmsRefundSettledLine fmsRefundSettledLine) {
		// TODO Auto-generated method stub
		fmsRefundSettledLineMapper.addFmsRefundSettledLine(fmsRefundSettledLine);
	}

	@Override
	public String getCode(Long transportContractHeaderId) {
		// TODO Auto-generated method stub
		Calendar nowtime = new GregorianCalendar(); 
		String batchCode = fmsRefundSettledHeaderMapper.getCode(transportContractHeaderId);
		batchCode = batchCode + nowtime.get(Calendar.YEAR) + nowtime.get(Calendar.MONTH)
		+ nowtime.get(Calendar.DATE) + nowtime.get(Calendar.HOUR) + nowtime.get(Calendar.MINUTE)
		+ nowtime.get(Calendar.SECOND) + nowtime.get(Calendar.MILLISECOND);
		return batchCode;
	}

	@Override
	@Transactional
	public void createDifferenceCostBatch(DifferenceCostRequest differenceCostRequest) {
		// TODO Auto-generated method stub
		List<FmsRefundSettled> list = differenceCostRequest.getList();
		FmsRefundSettledHeader fmsRefundSettledHeader = new FmsRefundSettledHeader();
		String batchCode = this.getCode(list.get(0).getFormalContractId());
		fmsRefundSettledHeader.setBatchCode(batchCode);
		
		BigDecimal batchFee = BigDecimal.valueOf(0);
		
		for(int i=0;i<list.size();i++) {
			FmsRefundSettled fmsRefundSettled = list.get(i);
			batchFee = batchFee.add(fmsRefundSettled.getRefundAmount());
		}
		
		fmsRefundSettledHeader.setBatchFee(batchFee);
//		fmsRefundSettledHeader.setSettledStatus("N");
		fmsRefundSettledHeader.setCreatedBy(differenceCostRequest.getUserId());
		fmsRefundSettledHeader.setCreationDate(new Date());
		fmsRefundSettledHeader.setLastUpdatedBy(differenceCostRequest.getUserId());
		fmsRefundSettledHeader.setLastUpdateDate(new Date());
		fmsRefundSettledHeader.setLastUpdateLogin(differenceCostRequest.getSessionId());
	
		FmsRefundSettledHeader recFmsRefundSettledHeader = fmsRefundSettledHeaderMapper.addFmsRefundSettledHeader(fmsRefundSettledHeader);
	
		List<FmsRefundSettledLine> addList = new ArrayList<FmsRefundSettledLine>();
		for(int i=0;i<list.size();i++) {
			FmsRefundSettled fmsRefundSettled = list.get(i);
			
			FmsRefundSettledLine fmsRefundSettledLine = new FmsRefundSettledLine();

			fmsRefundSettledLine.setRefundSettledHeaderId(recFmsRefundSettledHeader.getRefundSettledHeaderId());
			fmsRefundSettledLine.setRefundSettledId(fmsRefundSettled.getRefundSettledId());
			fmsRefundSettledLine.setCreatedBy(differenceCostRequest.getUserId());
			fmsRefundSettledLine.setCreationDate(new Date());
			fmsRefundSettledLine.setLastUpdatedBy(differenceCostRequest.getUserId());
			fmsRefundSettledLine.setLastUpdateDate(new Date());
			fmsRefundSettledLine.setLastUpdateLogin(differenceCostRequest.getSessionId());
			fmsRefundSettledLine.setOrderNumber(Long.valueOf(i));
			addList.add(fmsRefundSettledLine);
		}
		
		List<FmsRefundSettledLine> addList1 = new ArrayList<FmsRefundSettledLine>();
		for(int i=0;i<addList.size();i++) {
			addList1.add(addList.get(i));
			
			if(i % 100 == 0 || i == addList.size() -1) {
				fmsRefundSettledLineMapper.addFmsRefundSettledLineByList(addList1);
				addList1.clear();
			}
		}
	}
}
