package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.SendcarPrintVNew;

public interface SendcarPrintVService extends BaseService<SendcarPrintVNew, Long> {

	List<TmsSendcarLine> queryLineList(SendcarPrintVNew entity);

    void sendCarPrintCallback(List<Long> sendcarHeaderIds, Long userId, String sessionId) throws BusinessServiceException;

	void sendCarOvertimeCallback(Long sendcarHeaderId, Long userId, String sessionId) throws BusinessServiceException;
}
