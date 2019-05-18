package com.foreveross.vds.service.fms.service;

import java.util.List;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.dto.fms.TransportContractRequest;
import com.foreveross.vds.dto.fms.TransportContractResponse;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsTransportContractHeader;

/**
 * 运输合同头
 */
public interface FmsTransportContractHeaderService extends BaseService<FmsTransportContractHeader,Long>{

    /**
     * 运输合同分页查询
     * @param page
     * @param rows
     * @param transportContractRequest
     * @return
     */
    BasePageResponse pageTransportContract(Integer page, Integer rows, TransportContractRequest transportContractRequest);

    BasePageResponse pageTransportContractLine(Integer page, Integer rows, TransportContractRequest transportContractRequest);

    /**
     * 运输合同查询
     * @param transportContractRequest
     * @return
     */
    List<TransportContractResponse> selectTransportContract(TransportContractRequest transportContractRequest);


    List<TransportContractResponse> selectTransportContractLine(TransportContractRequest transportContractRequest);

    /**
     * 保存运输合同
     * 正式合同临时合同通用
     * @param transportContract
     * @param userId
     * @param sessionId
     */
    void saveTransportContract(TransportContract transportContract, Long userId, String sessionId) throws BusinessServiceException;

    @CustTx
    void saveTransportContractLine(TransportContract transportContract, Long userId, String sessionId) throws BusinessServiceException;

    /**
     * 保存运输合同
     * 正式合同临时合同通用
     * @param transportContractList
     * @param userId
     * @param sessionId
     */
    void saveTransportContract(List<TransportContract> transportContractList, Long userId, String sessionId) throws BusinessServiceException;

    /**
     * 根据物流商、所属基地、发运方式、车型大类、起点、终点 查找合同
     * @param transportContract
     * @return
     */
    List<TransportContract> findOldContract(TransportContract transportContract);

    /**
     * 修改合同
     * @param transportContract
     * @param userId
     */
    void updateTransportContract(TransportContract transportContract, Long userId);

    @CustTx
    void deleteTransportContractHeader(TransportContract transportContract)throws BusinessServiceException;

    @CustTx
    void deleteTransportContractLine(TransportContract transportContract) throws BusinessServiceException;

    /**
     *  @param transportContractHeaderIds
     * @param userId
     * @param sessionId
     */
    void doConfirmTransportContract(List<Long> transportContractHeaderIds, Long userId, String sessionId);
    
    void doConfirmTransportContractLine(List<Long> transportContractLineIds, Long userId, String sessionId);

    /**
     * 参数检查
     * @param transportContract
     */
    void checkParams(TransportContract transportContract) throws BusinessServiceException;

    int isContractHeaderExists(TransportContract transportContract);

    void checkContractLine(TransportContract transportContract) throws BusinessServiceException;
}
