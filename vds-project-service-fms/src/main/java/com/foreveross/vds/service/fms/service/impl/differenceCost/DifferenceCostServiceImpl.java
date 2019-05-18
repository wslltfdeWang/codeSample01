package com.foreveross.vds.service.fms.service.impl.differenceCost;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.CreateDifferenceCostRequest;
import com.foreveross.vds.dto.fms.differencecost.DifferenceCostRequest;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettled;
import com.foreveross.vds.service.fms.mapper.differenceCost.DifferenceCostMapper;
import com.foreveross.vds.service.fms.mapper.differenceCost.FmsRefundSettledMapper;
import com.foreveross.vds.service.fms.service.differenceCost.DifferenceCostService;
import com.foreveross.vds.vo.fms.FmsTransportContractHeader;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class DifferenceCostServiceImpl implements DifferenceCostService {
	@Autowired
	private DifferenceCostMapper differenceCostMapper;
	@Autowired
	private FmsRefundSettledMapper fmsRefundSettledMapper;

	@Override
	public Page<FmsTransportSettledHeader> queryFmsTransportSettledHeaderToDifferenceByPage(
			FmsTransportSettledHeader fmsTransportSettledHeader) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsTransportSettledHeader.getPage(), fmsTransportSettledHeader.getRows());
		Page<FmsTransportSettledHeader> page = (Page<FmsTransportSettledHeader>) differenceCostMapper.queryFmsTransportSettledHeaderToDifference(fmsTransportSettledHeader);
		return page;
	}

	@Override
	public List<FmsTransportContractHeader> queryFmsTransportContractHeader(
			FmsTransportContractHeader fmsTransportContractHeader) {
		// TODO Auto-generated method stub
		return differenceCostMapper.queryFmsTransportContractHeader(fmsTransportContractHeader);
	}

	@Override
	public void addFmsRefundSettledByList(CreateDifferenceCostRequest createDifferenceCostRequest) {
		// TODO Auto-generated method stub
		List<FmsTransportSettledHeader> list = createDifferenceCostRequest.getList();
		List<FmsRefundSettled> addList = new ArrayList<FmsRefundSettled>();
		for(int i=0;i<list.size();i++) {
			FmsTransportSettledHeader fmsTransportSettledHeader = list.get(i);
			
			FmsRefundSettled fmsRefundSettled = new FmsRefundSettled();
			fmsRefundSettled.setContractCategory(fmsTransportSettledHeader.getContractCategoryId());
			fmsRefundSettled.setTempContractId(fmsTransportSettledHeader.getContractHeaderId());
			fmsRefundSettled.setTempAmount(fmsTransportSettledHeader.getBatchFee());
			fmsRefundSettled.setFormalContractId(fmsTransportSettledHeader.getNewContractHeaderId());
			fmsRefundSettled.setFormalAmount(fmsTransportSettledHeader.getUnitFee());
			BigDecimal refundAmount = fmsTransportSettledHeader.getUnitFee().subtract(fmsTransportSettledHeader.getBatchFee());
			fmsRefundSettled.setRefundAmount(refundAmount);
			fmsRefundSettled.setSettledFlag("N");
			fmsRefundSettled.setCreatedBy(createDifferenceCostRequest.getUserId());
			fmsRefundSettled.setCreationDate(new Date());
			fmsRefundSettled.setLastUpdatedBy(createDifferenceCostRequest.getUserId());
			fmsRefundSettled.setLastUpdateDate(new Date());
			fmsRefundSettled.setLastUpdateLogin(createDifferenceCostRequest.getSessionId());
			addList.add(fmsRefundSettled);
		}
		List<FmsRefundSettled> addList1 = new ArrayList<FmsRefundSettled>();
		for(int i=0;i<addList.size();i++) {
			addList1.add(addList.get(i));
			if(i % 100 == 0 || i == addList.size() - 1) {
				fmsRefundSettledMapper.addFmsRefundSettledByList(addList1);
				addList.clear();
			}
		}
	}

	@Override
	public void addFmsRefundSettled(FmsRefundSettled fmsRefundSettled) {
		// TODO Auto-generated method stub
		fmsRefundSettledMapper.addFmsRefundSettled(fmsRefundSettled);
	}
}
