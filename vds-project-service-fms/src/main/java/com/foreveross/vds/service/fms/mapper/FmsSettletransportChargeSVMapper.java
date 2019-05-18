package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foreveross.vds.dto.fms.FmsSettletransportChargeLVRequest;
import com.foreveross.vds.vo.fms.FmsSettletransportChargeSV;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/***
 *待结算运费查询--行数据--统计
 */
@Mapper
@Repository
public interface FmsSettletransportChargeSVMapper {

    /**
     * 查询结算运距行统计数据
     * @param fmsSettletransportChargeLVRequest
     * @return
     */
    List<FmsSettletransportChargeSV> selectPmsSendcarLineStatistics(FmsSettletransportChargeLVRequest fmsSettletransportChargeLVRequest);

    /**
     * 据sendcarHeaderId 查询送车交接单行统计数据的费用合计
     * @param sendcarHeaderIds
     * @return
     */
    String getFmsSettletransportChargeSTotalFee(@Param("sendcarHeaderIds") Long[] sendcarHeaderIds);
}
