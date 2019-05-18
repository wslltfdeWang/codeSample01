package com.foreveross.vds.service.fms.mapper.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledV;

public interface SettlementStorageContractMapper {

	public List<FmsStorageSettledV> querySettlementStorageContract(FmsStorageSettledV fmsStorageSettledV);
	/**
	 * 根据仓储合同ID判断是否正式合同
	 * @param storageContractId
	 * @return
	 */
	public List<String> isFormalContract(Long storageContractId);
}
