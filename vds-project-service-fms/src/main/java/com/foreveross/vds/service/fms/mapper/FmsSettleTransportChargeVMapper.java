package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foreveross.vds.dto.fms.FmsSettleTransportChargeVRequest;
import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.vo.fms.FmsSettleTransportChargeV;
import org.springframework.stereotype.Repository;

/***
 * 待结算运费查询视图
 */
@Mapper
@Repository
public interface FmsSettleTransportChargeVMapper {

    /**
     * 待结算运费树查询
     * @param fmsSettleTransportChargeVRequest
     * @return
     */
    List<FmsSettleTransportChargeV> listFmsSettleTransportChargeTree(FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest);

    String getSettleTransportChargeVForTop(Long sendcarHeaderId);

	List<TmsSendcarHeaderFmsE> export(FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest);
}
