package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.fms.DistanceHeaderRequest;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsDistanceHeader;

public interface DistanceHeaderService extends BaseService<FmsDistanceHeader, Long> {

	public int save(DistanceHeaderRequest distanceHeaderRequest);
	
}
