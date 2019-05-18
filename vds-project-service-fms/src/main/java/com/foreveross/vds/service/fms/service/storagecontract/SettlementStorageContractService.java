package com.foreveross.vds.service.fms.service.storagecontract;

import java.util.List;

import com.foreveross.vds.dto.fms.storagecontract.FmsStorageSettledV;
import com.github.pagehelper.Page;

/**
 * 根据仓储结算数据生成批次
 * @author foreveross
 *
 */
public interface SettlementStorageContractService {
	/**
	 * 分页查询仓储合同结算数据
	 * @param fmsStorageSettledV
	 * @return
	 * @throws Exception
	 */
	public Page<FmsStorageSettledV> querySettlementStorageContractByPage(FmsStorageSettledV fmsStorageSettledV) throws Exception;
	/**
	 * 查询仓储合同结算数据
	 * @param fmsStorageSettledV
	 * @return
	 * @throws Exception
	 */
	public List<FmsStorageSettledV> querySettlementStorageContract(FmsStorageSettledV fmsStorageSettledV) throws Exception;
	/**
	 * 生成仓储结算数据批次
	 * @param FmsStorageSettledVList
	 */
	public void create(List<FmsStorageSettledV> FmsStorageSettledVList)  throws Exception ;
	/**
	 * 根据仓储合同ID判断是否正式合同
	 * @param storageContractId
	 * @return
	 */
	public Boolean isFormalContract(Long storageContractId);
}
