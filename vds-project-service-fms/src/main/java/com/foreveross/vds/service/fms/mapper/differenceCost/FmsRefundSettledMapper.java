package com.foreveross.vds.service.fms.mapper.differenceCost;

import java.util.List;

import com.foreveross.vds.dto.fms.differencecost.FmsRefundSettled;

public interface FmsRefundSettledMapper {
	/**
	 * 查询差价结算数据
	 * @param fmsRefundSettled
	 * @return
	 */
	List<FmsRefundSettled> queryFmsRefundSettled(FmsRefundSettled fmsRefundSettled);
	/**
	 * 批量添加差价结算数据
	 * @param list
	 */
	void addFmsRefundSettledByList(List<FmsRefundSettled> list);
	/**
	 * 生成差价结算数据
	 * @param list
	 */
	void addFmsRefundSettled(FmsRefundSettled fmsRefundSettled);
}
