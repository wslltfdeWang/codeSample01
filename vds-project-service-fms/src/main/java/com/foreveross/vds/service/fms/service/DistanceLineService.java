package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsDistanceLine;

public interface DistanceLineService extends BaseService<FmsDistanceLine, Long> {

    /**
     * 新增运输合同时查找运距
     * @param transportContract
     * @return
     */
    FmsDistanceLine findDistanceLine(TransportContract transportContract);

}
