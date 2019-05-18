package com.foreveross.vds.service.fms.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.foreveross.vds.vo.fms.FmsStorageContractV;

public interface FmsStorageContractService extends BaseService<FmsStorageContract, Long> {

	DetailResponse<Date> customSave(FmsStorageContract fmsStorageContract);
	
	<P> List<FmsStorageContractV> export(P params);

	int importStorageContract(Workbook workbook, Long userId, String sessionId);
}
