package com.foreveross.vds.service.fms.service.differenceCost;

import java.util.List;

import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.CreateDifferenceCostRequest;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettled;
import com.foreveross.vds.vo.fms.FmsTransportContractHeader;
import com.github.pagehelper.Page;

public interface DifferenceCostService {
	/**
	 * 分页获取待生成差价的运输费用结算数据
	 * @param fmsTransportSettledHeader
	 * @return
	 */
	Page<FmsTransportSettledHeader> queryFmsTransportSettledHeaderToDifferenceByPage(FmsTransportSettledHeader fmsTransportSettledHeader);
	/**
	 * 查询运输合同头数据
	 * @param fmsTransportContractHeader
	 * @return
	 */
	List<FmsTransportContractHeader> queryFmsTransportContractHeader(FmsTransportContractHeader fmsTransportContractHeader);
	/**
	 * 批量添加差价结算数据
	 * @param createDifferenceCostRequest
	 */
	void addFmsRefundSettledByList(CreateDifferenceCostRequest createDifferenceCostRequest);
	/**
	 * 生成差价结算数据
	 * @param list
	 */
	void addFmsRefundSettled(FmsRefundSettled fmsRefundSettled);
}
