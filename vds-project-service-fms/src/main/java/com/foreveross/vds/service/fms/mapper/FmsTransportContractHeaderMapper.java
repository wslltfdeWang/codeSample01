package com.foreveross.vds.service.fms.mapper;


import java.util.List;

import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.dto.fms.TransportContractRequest;
import com.foreveross.vds.dto.fms.TransportContractResponse;
import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.FmsTransportContractHeader;
import com.foreveross.vds.vo.tms.TmsDeparture;
import org.springframework.stereotype.Repository;

@Repository
public interface FmsTransportContractHeaderMapper extends BaseMapper<FmsTransportContractHeader, Long> {

    /**
     * 运输合同维护查询
     *
     * @param transportContractRequest
     * @return
     */
    List<TransportContractResponse> selectTransportContract(TransportContractRequest transportContractRequest);

    /**
     * 物流商、所属基地、发运方式、车型大类、起点、终点 查询合同数据
     *
     * @param transportContract
     * @return
     */
    List<TransportContract> selectOldTransportContract(TransportContract transportContract);

    /**
     * 判断合同号是否重复
     *
     * @param transportContract
     * @return
     */
    int isContractNumberRepeat(TransportContract transportContract);

    /**
     * 判断合同号、合同名称是否同时重复
     *
     * @param transportContract
     * @return
     */
    Integer isContractNumberAndNameRepeat(TransportContract transportContract);

    /**
     * 判断合同名称是否重复
     *
     * @param transportContract
     * @return
     */
    int isContractNameRepeat(TransportContract transportContract);

    /**
     * 判断该合同明细数据是否存在
     *
     * @param transportContract
     * @return
     */
    int isContractExists(TransportContract transportContract);

    int isContractLineRepeat(TransportContract transportContract);

    /**
     * 获取相同合同行
     *
     * @param transportContract
     * @return
     */
    List<TransportContract> selectContractLineSame(TransportContract transportContract);

    /**
     * 判断该正式合同日期是否满足
     *
     * @param transportContract
     * @return
     */
    int isContractDateMatch(TransportContract transportContract);

    void doSetContractPriority(TransportContract transportContract);

    List<TransportContractResponse> selectTransportContractLine(TransportContractRequest transportContractRequest);

    List<TmsDeparture> getReginInfoByDeparture(TmsDeparture tmsDeparture);

    void deleteTransportContractHeader(TransportContract transportContract);

    void deleteTransportContractLine(TransportContract transportContract);
}