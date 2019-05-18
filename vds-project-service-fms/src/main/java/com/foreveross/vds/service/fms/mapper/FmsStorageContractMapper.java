package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.foreveross.vds.vo.fms.FmsStorageContractV;
import org.springframework.stereotype.Repository;

@Repository
public interface FmsStorageContractMapper extends BaseMapper<FmsStorageContract, Long> {
	
	<P> List<FmsStorageContractV> export(P params);
}