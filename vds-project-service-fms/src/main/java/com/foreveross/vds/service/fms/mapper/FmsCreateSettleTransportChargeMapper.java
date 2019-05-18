package com.foreveross.vds.service.fms.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.foreveross.vds.dto.fms.FmsSendcarFeeRelate;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.FmsTransportSettledLine;
import com.foreveross.vds.dto.fms.SettleTransportChargeV;
import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.vo.fms.FmsTransportContractLine;
import org.springframework.stereotype.Repository;

@Repository
public interface FmsCreateSettleTransportChargeMapper {
    /**
     * 获取待生成运费结算批次的数据
     *
     * @param settleTransportChargeV
     * @return
     */
    List<SettleTransportChargeV> getSettleTransportChargeV(SettleTransportChargeV settleTransportChargeV);

    /**
     * 添加运费结算批次行数据
     *
     * @param fmsTransportSettledLine
     */
    void addFmsTransportSettledLine(FmsTransportSettledLine fmsTransportSettledLine);

    /**
     * 添加运费结算批次头数据
     *
     * @param fmsTransportSettledHeader
     */
    void addFmsTransportSettledHeader(FmsTransportSettledHeader fmsTransportSettledHeader);

    /**
     * 根据送车交接单头ID获取金额
     *
     * @param sendcarHeaderId
     * @return
     */
    BigDecimal getPrice(Long sendcarHeaderId);

    /**
     * 修改送车交接单结算标识
     *
     * @param sendcarHeaderId
     */
    void updateSendCarHeader(Long sendcarHeaderId);

    /**
     * 根据规则获取结算批次码
     *
     * @param fmsTransportSettledHeader
     * @return
     */
    String getBatchCode(FmsTransportSettledHeader fmsTransportSettledHeader);

    /**
     * 匹配送车交接单行表车型大类ID
     *
     * @param tmsSendcarLine
     * @return
     */
    void matchSendcarLineCarCategory(TmsSendcarLine tmsSendcarLine);

    List<TmsSendcarLine> querySendcarLineByHeaderId(FmsTransportSettledLine fmsTransportSettledLine);

    List<Integer> queryFeeRelateByLineId(TmsSendcarLine tmsSendcarLine);

    void saveFeeRelate(TmsSendcarLine tmsSendcarLine);

    List<TmsSendcarLine> queryReSendcarLine(TmsSendcarLine tmsSendcarLine);

    List<Integer> queryCarCategoryId(TmsSendcarLine tmsSendcarLine);

    void updateFeeRelateBySendcarLineId(FmsTransportContractLine fmsTransportContractLine);

    List<FmsTransportContractLine> selectContractDataBySendcarLineId(TmsSendcarLine tmsSendcarLine);
}
