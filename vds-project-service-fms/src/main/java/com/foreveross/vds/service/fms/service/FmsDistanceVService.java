package com.foreveross.vds.service.fms.service;

import org.apache.poi.ss.usermodel.Workbook;

import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsDistanceV;

public interface FmsDistanceVService extends BaseService<FmsDistanceV, Long> {

	int importDistance(Workbook workbook, Long userId, String sessionId);

}
