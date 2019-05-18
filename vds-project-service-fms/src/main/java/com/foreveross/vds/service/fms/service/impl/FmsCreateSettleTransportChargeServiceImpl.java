package com.foreveross.vds.service.fms.service.impl;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeader;
import com.foreveross.vds.dto.fms.FmsTransportSettledLine;
import com.foreveross.vds.dto.fms.SettleTransportChargeV;
import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.FmsStatusService;
import com.foreveross.vds.service.fms.mapper.FmsCreateSettleTransportChargeMapper;
import com.foreveross.vds.service.fms.service.FmsCreateSettleTransportChargeService;
import com.foreveross.vds.vo.fms.FmsTransportContractLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class FmsCreateSettleTransportChargeServiceImpl implements FmsCreateSettleTransportChargeService {
    @Autowired
    private FmsCreateSettleTransportChargeMapper fmsCreateSettleTransportChargeMapper;
    @Autowired
    private FmsStatusService fmsStatusService;

    @Override
    public List<SettleTransportChargeV> getSettleTransportChargeV(SettleTransportChargeV settleTransportChargeV) {
        return fmsCreateSettleTransportChargeMapper.getSettleTransportChargeV(settleTransportChargeV);
    }

    @Override
    public void addFmsTransportSettledLine(FmsTransportSettledLine fmsTransportSettledLine) {
        // TODO Auto-generated method stub
        fmsCreateSettleTransportChargeMapper.addFmsTransportSettledLine(fmsTransportSettledLine);
    }

    @Override
    public FmsTransportSettledHeader addFmsTransportSettledHeader(FmsTransportSettledHeader fmsTransportSettledHeader) {
        // TODO Auto-generated method stub
        fmsCreateSettleTransportChargeMapper.addFmsTransportSettledHeader(fmsTransportSettledHeader);
        return fmsTransportSettledHeader;
    }

    @Override
    public BigDecimal getPrice(Long sendcarHeaderId) {
        return fmsCreateSettleTransportChargeMapper.getPrice(sendcarHeaderId);
    }

    @Override
    public void updateSendCarHeader(Long sendcarHeaderId) {
        // TODO Auto-generated method stub
        fmsCreateSettleTransportChargeMapper.updateSendCarHeader(sendcarHeaderId);
    }

    @Override
    public String getBatchCode(FmsTransportSettledHeader fmsTransportSettledHeader) {
        // TODO Auto-generated method stub
        return fmsCreateSettleTransportChargeMapper.getBatchCode(fmsTransportSettledHeader);
    }

    @Override
    @CustTx
    public void relateData(FmsTransportSettledLine fmsTransportSettledLine) throws BusinessServiceException {
        List<TmsSendcarLine> sendcarLines = fmsCreateSettleTransportChargeMapper.querySendcarLineByHeaderId(fmsTransportSettledLine);

        for (TmsSendcarLine entity : sendcarLines) {
            List<Integer> relatedIds = fmsCreateSettleTransportChargeMapper.queryFeeRelateByLineId(entity);


            if (relatedIds.size() > 1) {
                throw new BusinessServiceException("送车交接单数据重复关联，请联系系统管理员");
            }

            entity.setCreatedBy(fmsTransportSettledLine.getCreatedBy());
            entity.setCreationDate(new Date());
            entity.setLastUpdatedBy(fmsTransportSettledLine.getLastUpdatedBy());
            entity.setLastUpdateLogin(fmsTransportSettledLine.getLastUpdateLogin());
            entity.setLastUpdateDate(new Date());

            if (relatedIds.size() == 0) {
                //新增交接单行合同行关联表数据
                fmsCreateSettleTransportChargeMapper.saveFeeRelate(entity);
                //更新结算状态
                fmsStatusService.updateSendcarSettleStatus(fmsTransportSettledLine.getSendcarHeaderId(), "UN_SUBMIT", fmsTransportSettledLine.getLastUpdatedBy(), fmsTransportSettledLine.getLastUpdateLogin());
            }

            //根据送车交接单行查询车型大类ID
            List<Integer> carCategoryId = fmsCreateSettleTransportChargeMapper.queryCarCategoryId(entity);
            if (carCategoryId.size() > 1) {
                throw new BusinessServiceException("送车交接单行关联多个车型大类，请联系系统管理员！");
            } else if (carCategoryId.size() == 1) {
                //如果有匹配的车型大类即进行数据关联
                entity.setCarCategoryId(Long.valueOf(carCategoryId.get(0)));

                //将车型大类数据数据写入送车交接单行表
                fmsCreateSettleTransportChargeMapper.matchSendcarLineCarCategory(entity);

                //根据关联后的数据查询合同行数据
                List<FmsTransportContractLine> conctracts = fmsCreateSettleTransportChargeMapper.selectContractDataBySendcarLineId(entity);
                FmsTransportContractLine fmsTransportContractLine = new FmsTransportContractLine();
                fmsTransportContractLine.setSencarLineId(entity.getSencarLineId());

                if (conctracts.size() > 2) {
                    throw new BusinessServiceException("符合条件的合同数据超过2条");
                }

                //如果满足条件的合同数据有2条则比较优先级
                if (conctracts.size() == 2) {
                    if (conctracts.get(0).getContractPriority() > conctracts.get(1).getContractPriority()) {
                        fmsTransportContractLine = conctracts.get(0);
                        fmsTransportContractLine.setSencarLineId(entity.getSencarLineId());
                    } else {
                        fmsTransportContractLine = conctracts.get(1);
                        fmsTransportContractLine.setSencarLineId(entity.getSencarLineId());
                    }
                } else if (conctracts.size() == 1) {
                    fmsTransportContractLine = conctracts.get(0);
                    fmsTransportContractLine.setSencarLineId(entity.getSencarLineId());
                } else {
                    continue;
                }

                //更新送车交接单行合同明细关联表数据
                fmsTransportContractLine.setLastUpdatedBy(fmsTransportSettledLine.getLastUpdatedBy());
                fmsTransportContractLine.setLastUpdateLogin(fmsTransportSettledLine.getLastUpdateLogin());
                fmsCreateSettleTransportChargeMapper.updateFeeRelateBySendcarLineId(fmsTransportContractLine);
            }

        }

    }

}
