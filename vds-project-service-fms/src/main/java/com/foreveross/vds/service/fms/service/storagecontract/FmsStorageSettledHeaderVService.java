package com.foreveross.vds.service.fms.service.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledHeaderV;
import com.github.pagehelper.Page;

public interface FmsStorageSettledHeaderVService {
	/**
	 * 分页查询仓储结算数据头视图
	 * @param fmsStorageSettledHeaderV
	 * @return
	 */
	Page<FmsStorageSettledHeaderV> queryFmsStorageSettledHeaderVByPage(FmsStorageSettledHeaderV fmsStorageSettledHeaderV);
}
