package com.foreveross.vds.service.fms.service.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeader;

public interface FmsStorageSettledHeaderService {
	/**
	 * 添加仓储结算数据头表
	 * @param fmsStorageSettledHeader
	 */
	FmsStorageSettledHeader addFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader) throws Exception;
	/**
	 * 获取生成批次编码
	 * @param storageContractId
	 * @return
	 */
	String getCode(Long storageContractId);
	
	/**
	 * 驳回仓储结算数据头数据
	 * @param fmsStorageSettledHeader
	 */
	void rejectedFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader) throws Exception;
	/**
	 * 通过仓储结算数据头数据
	 * @param fmsStorageSettledHeader
	 */
	void allowFmsStorageSettledHeader(FmsStorageSettledHeader fmsStorageSettledHeader) throws Exception;
}
