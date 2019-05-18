package com.foreveross.vds.service.fms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsDistanceLineMapper;
import com.foreveross.vds.service.fms.service.DistanceLineService;
import com.foreveross.vds.vo.fms.FmsDistanceLine;

@Service
public class DistanceLineServiceImpl extends BaseServiceImpl<FmsDistanceLine, Long> implements DistanceLineService {

	@Autowired
	private FmsDistanceLineMapper fmsDistanceLineMapper;

	public DistanceLineServiceImpl() {
		super("distanceLineId");
	}

	@Override
	public FmsDistanceLine findDistanceLine(TransportContract transportContract) {
		List<FmsDistanceLine> fmsDistanceLineList = fmsDistanceLineMapper.selectDistanceLine(transportContract);
		if(fmsDistanceLineList.size() != 0){
			return fmsDistanceLineList.get(0);
		}
		return null;
	}
	
}
