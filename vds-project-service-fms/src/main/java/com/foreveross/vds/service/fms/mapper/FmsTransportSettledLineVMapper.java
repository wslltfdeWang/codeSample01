package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;
import com.foreveross.vds.vo.fms.FmsTransportSettledLineV;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface FmsTransportSettledLineVMapper extends BaseMapper<FmsTransportSettledLineV, Long> {

    String queryTransportSettledLineTotalfee(@Param("transportSettledHeaderIds") Long[] transportSettledHeaderIds);

	List<TmsSendcarHeaderFmsE> print(FmsTransportSettledHeader fmsTransportSettledHeader);
}