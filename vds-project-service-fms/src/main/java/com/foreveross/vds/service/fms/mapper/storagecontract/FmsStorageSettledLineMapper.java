package com.foreveross.vds.service.fms.mapper.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLine;

public interface FmsStorageSettledLineMapper {
	/**
	 * 添加仓储结算数据行表
	 * @param fmsStorageSettledLine
	 */
	void addFmsStorageSettledLine(FmsStorageSettledLine fmsStorageSettledLine);
	/**
	 * 驳回仓储结算行数据
	 * @param fmsStorageSettledLine
	 */
	void rejectedFmsStorageSettledLine(FmsStorageSettledLine fmsStorageSettledLine);
	/**
	 * 根据头ID获取行数据
	 * @param fmsStorageSettledLine
	 * @return
	 */
	List<FmsStorageSettledLine> getFmsStorageSettledLineByStorageSettledHeaderId(Long storageSettledHeaderId);
	/**
	 * 批量通过仓储结算行数据
	 * @param list
	 */
	void allowFmsStorageSettledLine(List<Long> list);
}
