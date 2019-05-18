package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;
import org.springframework.stereotype.Repository;

@Repository
public interface FmsTransportSettledHeaderMapper extends BaseMapper<FmsTransportSettledHeader, Long> {
	
	List<FmsTransportSettledHeader> queryConfrimData(FmsTransportSettledHeader header);

	void updateTransportSettledLineByTransportSettledHeader(FmsTransportSettledHeader fmsTransportSettledHeader);
}