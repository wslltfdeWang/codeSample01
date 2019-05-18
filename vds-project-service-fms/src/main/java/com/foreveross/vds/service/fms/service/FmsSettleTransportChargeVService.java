package com.foreveross.vds.service.fms.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.RecRequest;
import com.foreveross.vds.dto.fms.FmsSettleTransportChargeVRequest;
import com.foreveross.vds.dto.fms.SettleTransportChargeV;
import com.foreveross.vds.dto.tms.TmsSendcarHeaderFmsE;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.vo.fms.FmsSettleTransportChargeV;

/**
 * 待结算运费查询视图
 */
public interface FmsSettleTransportChargeVService {

    /**
     * 分页查询送车交接单头数据作为树的第一层
     * 返回满足eazyui树格式的数据
     * @param pageIndex
     * @param pageSize
     * @param fmsSettleTransportChargeVRequest
     * @return
     */
    BasePageResponse pageFmsSettleTransportChargeTree(Integer pageIndex, Integer pageSize, FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest);

    /**
     * 待结算运费树查询
     * @param fmsSettleTransportChargeVRequest
     * @return
     */
    List<FmsSettleTransportChargeV> listFmsSettleTransportChargeTree(FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest);

    /**
     *
     * @param basePageResponse
     */
    void setEazyuiTreegridId(BasePageResponse basePageResponse);
    
    public RecRequest createSettleTransportCharge(SettleTransportChargeV settleTransportChargeV) throws BusinessServiceException;

    /**
     * 导出
     */
	List<TmsSendcarHeaderFmsE> export(FmsSettleTransportChargeVRequest fmsSettleTransportChargeVRequest);

	String importSettleTransport(Workbook workbook, Long userId, String sessionId, Long logisticsId);
}
