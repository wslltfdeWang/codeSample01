package com.foreveross.vds.service.fms.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.fms.FmsStorageSettledArray;
import com.foreveross.vds.dto.fms.FmsStorageSettledDto;
import com.foreveross.vds.service.common.service.MaterialTransactionsService;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsStorageSettledMapper;
import com.foreveross.vds.service.fms.service.FmsStorageContractCategoryService;
import com.foreveross.vds.service.fms.service.FmsStorageContractService;
import com.foreveross.vds.service.fms.service.FmsStorageSettledService;
import com.foreveross.vds.util.exception.ServiceException;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.foreveross.vds.util.tools.DateUtil;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.foreveross.vds.vo.fms.FmsStorageContractCategory;
import com.foreveross.vds.vo.fms.FmsStorageSettled;

@Service
public class FmsStorageSettledServiceImpl extends BaseServiceImpl<FmsStorageSettled, Long> implements FmsStorageSettledService {

	@Autowired
	private FmsStorageContractService fmsStorageContractService;
	@Autowired
	private FmsStorageContractCategoryService fmsStorageContractCategoryService;
	@Autowired
	private MaterialTransactionsService materialTransactionsService;
	@Autowired
	private FmsStorageSettledMapper fmsStorageSettledMapper;
	
	@Override
	public List<FmsStorageSettledDto> calculationResult(FmsStorageSettledDto fmsStorageSettledDto) {
		
		List<FmsStorageSettledDto> result = new ArrayList<>();
		Long[] storageContractIds = fmsStorageSettledDto.getStorageContractIds();
		for (Long storageContractId : storageContractIds) {
			
			FmsStorageSettled params = new FmsStorageSettled();
			params.setContractId(storageContractId);
			params.setStartDate(fmsStorageSettledDto.getStartDate());
			List<FmsStorageSettled> queryList = this.queryList(params);
			if (AnalyzerTool.isEmpty(queryList)) {
				params.setStartDate(null);
				params.setEndDate(fmsStorageSettledDto.getEndDate());
				queryList = this.queryList(params);
			}
			if (AnalyzerTool.isNotEmpty(queryList)) {
				throw new ServiceException("0", String.format("当前合同时间段：%s -- %s 已生成结算数据，时间段不能重复", 
						DateUtil.format(queryList.get(0).getStartDate()),
						DateUtil.format(queryList.get(0).getEndDate())));
			}
			
			FmsStorageContract fmsStorageContract = fmsStorageContractService.selectByPrimaryKey(storageContractId);
			FmsStorageContractCategory fmsStorageContractCategory = fmsStorageContractCategoryService.selectByPrimaryKey(fmsStorageContract.getContractCategoryId());
			
			Date startDate = fmsStorageSettledDto.getStartDate();
			Date endDate = null;
			do {
				endDate = DateUtil.add(startDate, Calendar.MONTH, 1);
				endDate = DateUtil.addDate(endDate, -1);
				
				if (endDate.compareTo(fmsStorageSettledDto.getEndDate()) >= 0) {
					endDate = fmsStorageSettledDto.getEndDate();
				}
				
				FmsStorageSettledDto dto = AnalyzerTool.toObject(fmsStorageContract, FmsStorageSettledDto.class);
				if ("库房租赁费".equals(fmsStorageContractCategory.getCategoryName())) {
					dto.setSettledPrice(fmsStorageContract.getUnitPrice()
							.multiply(fmsStorageContract.getQuantity().multiply(
									monthDifference(startDate, endDate))));
				} else if ("挪车费".equals(fmsStorageContractCategory.getCategoryName())) {
					Integer scheduledReceipt = materialTransactionsService.queryScheduledReceipt(
							fmsStorageContract.getStorageRoomId(), startDate, endDate);
					dto.setSettledPrice(fmsStorageContract.getUnitPrice().multiply(new BigDecimal(scheduledReceipt)));
				} else if ("保安保洁".equals(fmsStorageContractCategory.getCategoryName())) {
					dto.setSettledPrice(fmsStorageContract.getUnitPrice()
							.multiply(monthDifference(startDate, endDate)));
				} else if ("仓储操作管理".equals(fmsStorageContractCategory.getCategoryName())) {
					Integer scheduledReceipt = materialTransactionsService.queryScheduledReceipt(
							fmsStorageContract.getStorageRoomId(), startDate, endDate);
					dto.setSettledPrice(fmsStorageContract.getUnitPrice().multiply(new BigDecimal(scheduledReceipt)));
				}
				
				dto.setStartDate(startDate);
				dto.setEndDate(endDate);
				result.add(dto);
				
				startDate = DateUtil.addDate(endDate, 1);
			} while (endDate.compareTo(fmsStorageSettledDto.getEndDate()) < 0);
		}
		
		return result;
	}

	protected BigDecimal monthDifference(Date startDate, Date endDate) {
		int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
		return days <= 15 ? new BigDecimal("0.5") : new BigDecimal(1);
	}
	
	@Override
	@Transactional
	public int save(FmsStorageSettledArray fmsStorageSettledArray) {
		Date date = new Date();
		for (FmsStorageSettled fmsStorageSettled : fmsStorageSettledArray.getArray()) {
			Calendar c = Calendar.getInstance();
			c.setTime(fmsStorageSettled.getStartDate());
			Long month = c.get(Calendar.MONTH) + 1L;
			FmsStorageContractCategory fmsStorageContractCategory = fmsStorageContractCategoryService.selectByPrimaryKey(fmsStorageSettled.getContractCategoryId());
			if ("库房租赁费".equals(fmsStorageContractCategory.getCategoryName())) {
				FmsStorageContract fmsStorageContract = fmsStorageContractService.selectByPrimaryKey(fmsStorageSettled.getContractId());
				FmsStorageSettledDto params = new FmsStorageSettledDto();
				params.setContractNumber(fmsStorageContract.getContractNumber());
				params.setContractCategoryId(fmsStorageContract.getContractCategoryId());
				params.setStartDate(fmsStorageContract.getStartDate());
				params.setEndDate(fmsStorageContract.getEndDate());
				params.setLogisticsId(fmsStorageContract.getLogisticsId());
				params.setStartPointId(fmsStorageContract.getStartPointId());
				params.setStorageRoomId(fmsStorageContract.getStorageRoomId());
				params.setMonth(month);
				
				List<FmsStorageSettled> queryList = this.queryListByContract(params);
				BigDecimal monthQuantity = new BigDecimal(0);
				if (AnalyzerTool.isNotEmpty(queryList)) {
					for (FmsStorageSettled item : queryList) {
						monthQuantity = monthQuantity.add(item.getEditQuantity());
					}
				}
				
				if (monthQuantity.add(fmsStorageSettled.getEditQuantity()).compareTo(fmsStorageContract.getQuantity()) > 0) {
					throw new ServiceException("0", "当月已超过合同数量");
				}
			}
			fmsStorageSettled.setMonth(month);
			fmsStorageSettled.setCreatedBy(fmsStorageSettledArray.getUserId());
			fmsStorageSettled.setCreationDate(date);
			fmsStorageSettled.setLastUpdateDate(date);
			fmsStorageSettled.setLastUpdatedBy(fmsStorageSettledArray.getUserId());
			fmsStorageSettled.setLastUpdateLogin(fmsStorageSettledArray.getSessionId());
			super.insertSelective(fmsStorageSettled);
		}
		return fmsStorageSettledArray.getArray().length;
	}
	
	@Override
	public <P> List<FmsStorageSettled> queryListByContract(P params) {
		return fmsStorageSettledMapper.queryListByContract(params);
	}
}
