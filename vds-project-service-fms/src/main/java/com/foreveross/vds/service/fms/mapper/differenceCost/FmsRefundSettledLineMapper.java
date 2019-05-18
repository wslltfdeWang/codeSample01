package com.foreveross.vds.service.fms.mapper.differenceCost;

import java.util.List;

import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledLine;

public interface FmsRefundSettledLineMapper {
	/**
	 * 添加差价结算批次行数据
	 * @param fmsRefundSettledLine
	 */
	void addFmsRefundSettledLine(FmsRefundSettledLine fmsRefundSettledLine);
	/**
	 * 批量添加差价结算批次行数据
	 * @param list
	 */
	void addFmsRefundSettledLineByList(List<FmsRefundSettledLine> list);
	/**
	 * 查询差价结算批次行数据
	 * @param refundSettledHeaderId
	 * @return
	 */
	List<FmsRefundSettledLine> queryFmsRefundSettledLine(FmsRefundSettledLine fmsRefundSettledLine);
	/**
	 * 审核差价结算批次行数据
	 * @param fmsRefundSettledLine
	 */
	void auditFmsRefundSettledLine(FmsRefundSettledLine fmsRefundSettledLine);
	/**
	 * 根据头ID审核行数据
	 * @param fmsRefundSettledLine
	 */
	void auditFmsRefundSettledLineByefundSettledHeaderId(FmsRefundSettledLine fmsRefundSettledLine);
}
