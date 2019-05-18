package com.foreveross.vds.service.fms.mapper.differenceCost;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.vo.fms.FmsTransportContractHeader;

public interface DifferenceCostMapper {
	/**
	 * 获取待生成差价的运输费用结算数据
	 * @param fmsTransportSettledHeader
	 * @return
	 */
	List<FmsTransportSettledHeader> queryFmsTransportSettledHeaderToDifference(FmsTransportSettledHeader fmsTransportSettledHeader);
	/**
	 * 查询合同数据
	 * @param fmsTransportContractHeader
	 * @return
	 */
	List<FmsTransportContractHeader> queryFmsTransportContractHeader(FmsTransportContractHeader fmsTransportContractHeader);
}
