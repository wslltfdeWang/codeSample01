package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.FmsDistanceLine;

public interface FmsDistanceLineMapper extends BaseMapper<FmsDistanceLine, Long> {

    /**
     *新增运输合同时查找运距
     * @param transportContract
     * @return
     */
    List<FmsDistanceLine> selectDistanceLine(TransportContract transportContract);
}