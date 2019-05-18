package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.FmsStorageSettled;

public interface FmsStorageSettledMapper extends BaseMapper<FmsStorageSettled, Long> {

	<P> List<FmsStorageSettled> queryListByContract(P params);

}
