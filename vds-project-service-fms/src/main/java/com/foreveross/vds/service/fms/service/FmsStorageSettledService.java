package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsStorageSettledArray;
import com.foreveross.vds.dto.fms.FmsStorageSettledDto;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsStorageSettled;

public interface FmsStorageSettledService extends BaseService<FmsStorageSettled, Long> {

	List<FmsStorageSettledDto> calculationResult(FmsStorageSettledDto fmsStorageSettledDto);

	int save(FmsStorageSettledArray fmsStorageSettledArray);

	<P> List<FmsStorageSettled> queryListByContract(P params);

}
