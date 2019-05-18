package com.foreveross.vds.service.fms.service.storagecontract;

import java.math.BigDecimal;
import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettled;
import com.foreveross.vds.vo.fms.FmsStorageContract;
import com.github.pagehelper.Page;

public interface CreateSettlementStorageService {
	/**
	 * 查询仓储结算数据
	 * @param fmsStorageSettled
	 * @return
	 */
	List<FmsStorageSettled> queryFmsStorageSettled(FmsStorageSettled fmsStorageSettled);
	/**
	 * 分页查询仓储结算数据
	 * @param fmsStorageSettled
	 * @return
	 */
	Page<FmsStorageSettled> queryFmsStorageSettledByPage(FmsStorageSettled fmsStorageSettled);
	/**
	 * 添加仓储结算数据
	 * @param fmsStorageSettled
	 */
	void addFmsStorageSettled(FmsStorageSettled fmsStorageSettled) throws Exception;
	/**
	 * 根据结算时间段获取结算总金额
	 * @param fmsStorageSettled
	 * @return
	 */
	BigDecimal getAmount(FmsStorageSettled fmsStorageSettled, FmsStorageContract fmsStorageContract);
}
