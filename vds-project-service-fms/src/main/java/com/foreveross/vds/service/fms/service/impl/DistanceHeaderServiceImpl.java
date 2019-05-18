package com.foreveross.vds.service.fms.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreveross.vds.dto.fms.DistanceHeaderRequest;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.service.DistanceHeaderService;
import com.foreveross.vds.service.fms.service.DistanceLineService;
import com.foreveross.vds.util.exception.ServiceException;
import com.foreveross.vds.util.tools.AnalyzerTool;
import com.foreveross.vds.vo.fms.FmsDistanceHeader;
import com.foreveross.vds.vo.fms.FmsDistanceLine;

@Service
public class DistanceHeaderServiceImpl extends BaseServiceImpl<FmsDistanceHeader, Long> implements DistanceHeaderService {

	private Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private DistanceLineService distanceLineService;
	
	public DistanceHeaderServiceImpl() {
		super("distanceHeaderId");
	}

	@Override
	@Transactional
	public int save(DistanceHeaderRequest distanceHeaderRequest) {
		int quickSave = this.quickSave(distanceHeaderRequest);
		Long[] distanceLineIds = distanceHeaderRequest.getDistanceLineIds();
		if (distanceLineIds != null && distanceLineIds.length > 0) {
			for (Long distanceLineId : distanceLineIds) {
				FmsDistanceLine fmsDistanceLine = distanceLineService.selectByPrimaryKey(distanceLineId);
				if (fmsDistanceLine == null) {
					log.info(String.format("distanceLineId:%s 未查询到数据", distanceLineId));
					throw new ServiceException("0", "未查询到运距行表");
				}
				fmsDistanceLine.setDistanceHeaderId(distanceHeaderRequest.getDistanceHeaderId());
				fmsDistanceLine.setDistanceLineId(null);
				quickSave += distanceLineService.insertSelective(fmsDistanceLine);
			}
		}
		return quickSave;
	}
	
	@Override
	public int quickSave(FmsDistanceHeader record) {
		
		boolean flag = true;
		String error = "";
		
		if (flag) {
			FmsDistanceHeader params = new FmsDistanceHeader();
			params.setDistanceCode(record.getDistanceCode());
			List<FmsDistanceHeader> queryList = this.queryList(params);
			if (AnalyzerTool.isNotEmpty(queryList)) {
				flag = false;
				error = "新版本代码不能重复";
			}
		}
		
		if (flag) {
			FmsDistanceHeader params = new FmsDistanceHeader();
			params.setDistanceName(record.getDistanceName());
			List<FmsDistanceHeader> queryList = this.queryList(params);
			if (AnalyzerTool.isNotEmpty(queryList)) {
				flag = false;
				error = "新版本名称不能重复";
			}
		}
		
		if (!flag) {
			throw new ServiceException("0", error);
		}
		
		return super.quickSave(record);
	}
}
