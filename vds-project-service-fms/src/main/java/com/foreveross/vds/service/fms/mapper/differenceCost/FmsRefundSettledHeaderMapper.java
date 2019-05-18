package com.foreveross.vds.service.fms.mapper.differenceCost;

import java.util.List;

import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledHeader;

public interface FmsRefundSettledHeaderMapper {
	/**
	 * 添加差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 */
	FmsRefundSettledHeader addFmsRefundSettledHeader(FmsRefundSettledHeader fmsRefundSettledHeader);
	String getCode(Long transportContractHeaderId);
	/**
	 * 查询差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 * @return
	 */
	List<FmsRefundSettledHeader> queryFmsRefundSettledHeader(FmsRefundSettledHeader fmsRefundSettledHeader);
	/**
	 * 审核差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 */
	void auditFmsRefundSettledHeader(FmsRefundSettledHeader fmsRefundSettledHeader);
}
