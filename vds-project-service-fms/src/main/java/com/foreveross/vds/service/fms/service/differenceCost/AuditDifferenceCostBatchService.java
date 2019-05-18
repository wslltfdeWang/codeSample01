package com.foreveross.vds.service.fms.service.differenceCost;

import java.util.List;

import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledLine;
import com.github.pagehelper.Page;

public interface AuditDifferenceCostBatchService {
	/**
	 * 查询差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 * @return
	 */
	Page<FmsRefundSettledHeader> queryFmsRefundSettledHeaderByPage(FmsRefundSettledHeader fmsRefundSettledHeader);
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
	 * 审核差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 */
	void auditFmsRefundSettledHeader(FmsRefundSettledHeader fmsRefundSettledHeader);
}
