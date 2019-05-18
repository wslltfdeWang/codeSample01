package com.foreveross.vds.service.fms.service.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLine;

public interface FmsStorageSettledLineService {
	/**
	 * 添加仓储结算数据行表
	 * @param fmsStorageSettledLine
	 */
	FmsStorageSettledLine addFmsStorageSettledLine(FmsStorageSettledLine fmsStorageSettledLine) throws Exception;
	/**
	 * 驳回仓储结算行数据
	 * @param fmsStorageSettledLine
	 */
	void rejectedFmsStorageSettledLine(FmsStorageSettledLine fmsStorageSettledLine) throws Exception;
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
