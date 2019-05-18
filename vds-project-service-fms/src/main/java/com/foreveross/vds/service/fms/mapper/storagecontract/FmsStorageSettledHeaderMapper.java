package com.foreveross.vds.service.fms.mapper.storagecontract;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeader;

public interface FmsStorageSettledHeaderMapper {
	/**
	 * 添加仓储结算数据头表
	 * @param fmsStorageSettledHeader
	 */
	void addFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader);
	/**
	 * 获取生成批次编码的code
	 * @param storageContractId
	 * @return
	 */
	String getCode(Long storageContractId);
	/**
	 * 驳回仓储结算数据头数据
	 * @param fmsStorageSettledHeader
	 */
	void rejectedFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader);
	/**
	 * 通过仓储结算数据头数据
	 * @param fmsStorageSettledHeader
	 */
	void allowFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader);
}
