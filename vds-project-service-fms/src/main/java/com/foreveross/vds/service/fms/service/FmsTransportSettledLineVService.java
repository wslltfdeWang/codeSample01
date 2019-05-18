package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;
import com.foreveross.vds.vo.fms.FmsTransportSettledLine;
import com.foreveross.vds.vo.fms.FmsTransportSettledLineV;

/**
 * @author luochong
 * @date 2018年8月8日 下午2:14:38
 * 
 * 
 */
public interface FmsTransportSettledLineVService extends BaseService<FmsTransportSettledLineV, Long> {

    String queryTransportSettledLineTotalfee(Long[] transportSettledHeaderIds);

	List<TmsSendcarHeaderFmsE> print(FmsTransportSettledHeader fmsTransportSettledHeader);

	void delete(FmsTransportSettledLine fmsTransportSettledLine);
}
