package com.foreveross.vds.service.fms.service.impl.storagecontract;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettled;
import com.foreveross.vds.service.fms.mapper.storagecontract.CreateSettlementStorageMapper;
import com.foreveross.vds.service.fms.service.storagecontract.CreateSettlementStorageService;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
@Service
public class CreateSettlementStorageServiceImpl implements CreateSettlementStorageService {

	@Autowired
	private CreateSettlementStorageMapper createSettlementStorageMapper;

	@Override
	public List<FmsStorageSettled> queryFmsStorageSettled(FmsStorageSettled fmsStorageSettled) {
		// TODO Auto-generated method stub
		return createSettlementStorageMapper.queryFmsStorageSettled(fmsStorageSettled);
	}

	@Override
	public void addFmsStorageSettled(FmsStorageSettled fmsStorageSettled)  throws Exception{
		// TODO Auto-generated method stub
		createSettlementStorageMapper.addFmsStorageSettled(fmsStorageSettled);
	}

	@Override
	public BigDecimal getAmount(FmsStorageSettled fmsStorageSettled, FmsStorageContract fmsStorageContract) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		
		String startDateStr = sdf.format(fmsStorageSettled.getStartDate());
		String endDateStr = sdf.format(fmsStorageSettled.getEndDate());
		
		Long monthNum = Long.parseLong(endDateStr) - Long.parseLong(startDateStr) + 1;
		
		BigDecimal amount = fmsStorageContract.getMonthPrice().multiply(BigDecimal.valueOf(monthNum));

		return amount;
	}

	@Override
	public Page<FmsStorageSettled> queryFmsStorageSettledByPage(FmsStorageSettled fmsStorageSettled) {
		// TODO Auto-generated method stub
		PageHelper.startPage(fmsStorageSettled.getPage(), fmsStorageSettled.getRows());
		Page<FmsStorageSettled> page = (Page<FmsStorageSettled>) createSettlementStorageMapper.queryFmsStorageSettled(fmsStorageSettled);
		return page;
	}
}
