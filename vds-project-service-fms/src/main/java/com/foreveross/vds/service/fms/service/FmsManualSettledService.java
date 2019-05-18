package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsManualSettled;

import java.util.List;

/**
 *
 */
public interface FmsManualSettledService extends BaseService<FmsManualSettled, Long> {

    void insertOrUpdateFmsManualSettled(FmsManualSettled fmsManualSettled, Long userId, String sessionId);

    void deleteFmsManualSettled(List<Long> manualSettledIds);
}
