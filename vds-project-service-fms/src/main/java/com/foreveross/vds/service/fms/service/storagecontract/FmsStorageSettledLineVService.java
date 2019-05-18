package com.foreveross.vds.service.fms.service.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledLineV;
import com.github.pagehelper.Page;

public interface FmsStorageSettledLineVService {
	/**
	 * 分页查询仓储结算数据行视图
	 * @param fmsStorageSettledLineV
	 * @return
	 */
	Page<FmsStorageSettledLineV> queryFmsStorageSettledLineVByPage(FmsStorageSettledLineV fmsStorageSettledLineV);
}
