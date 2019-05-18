package com.foreveross.vds.service.fms.service.differenceCost;

import java.util.List;

import com.foreveross.vds.dto.fms.differencecost.DifferenceCostRequest;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettled;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledHeader;
import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettledLine;
import com.github.pagehelper.Page;

public interface CreateDifferenceCostBatchService {
	/**
	 * 分页查询差价结算数据
	 * @param fmsRefundSettled
	 * @return
	 */
	Page<FmsRefundSettled> queryFmsRefundSettledByPage(FmsRefundSettled fmsRefundSettled);
	/**
	 * 添加差价结算批次头数据
	 * @param fmsRefundSettledHeader
	 */
	void addFmsRefundSettledHeader(FmsRefundSettledHeader fmsRefundSettledHeader);
	/**
	 * 添加差价结算批次行数据
	 * @param fmsRefundSettledLine
	 */
	void addFmsRefundSettledLine(FmsRefundSettledLine fmsRefundSettledLine);
	/**
	 * 获取结算批次号
	 * @param transportContractHeaderId
	 * @return
	 */
	String getCode(Long transportContractHeaderId);
	/**
	 * 生成差价结算批次
	 * @param differenceCostRequest
	 */
	void createDifferenceCostBatch(DifferenceCostRequest differenceCostRequest);
}
