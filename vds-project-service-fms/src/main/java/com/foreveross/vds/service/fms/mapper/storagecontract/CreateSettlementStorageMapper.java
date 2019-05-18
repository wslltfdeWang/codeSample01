package com.foreveross.vds.service.fms.mapper.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettled;
import org.springframework.stereotype.Repository;

@Repository
public interface CreateSettlementStorageMapper {
	/**
	 * 查询仓储结算数据
	 * @param fmsStorageSettled
	 * @return
	 */
	List<FmsStorageSettled> queryFmsStorageSettled(FmsStorageSettled fmsStorageSettled);
	/**
	 * 添加仓储结算数据
	 * @param fmsStorageSettled
	 */
	void addFmsStorageSettled(FmsStorageSettled fmsStorageSettled);
}
