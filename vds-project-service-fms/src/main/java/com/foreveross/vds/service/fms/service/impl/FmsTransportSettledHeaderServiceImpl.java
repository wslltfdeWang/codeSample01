package com.foreveross.vds.service.fms.service.impl;

import com.foreveross.vds.dto.fms.FmsTransportSettledHeaderRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.FmsStatusService;
import com.foreveross.vds.service.common.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.DetailPageRequest;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsTransportSettledHeaderMapper;
import com.foreveross.vds.service.fms.service.FmsTransportSettledHeaderService;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
public class FmsTransportSettledHeaderServiceImpl extends BaseServiceImpl<FmsTransportSettledHeader, Long> implements FmsTransportSettledHeaderService {

    @Autowired
    private FmsTransportSettledHeaderMapper fmsTransportSettledHeaderMapper;
    @Autowired
    private LookupService lookupService;
    @Autowired
    private FmsStatusService fmsStatusService;

    @Override
    public int saveHeader(
            @RequestBody FmsTransportSettledHeader fmsTransportSettledHeader
    ) throws BusinessServiceException {
        if (fmsTransportSettledHeader.getRejectFlag() == null) {
            throw new BusinessServiceException("审批状态为空！");
        }

        int quickSave;

        String n = "N";
        fmsTransportSettledHeader.setApproveDate(new Date());

        if (fmsTransportSettledHeader.getRejectFlag().equals(n)) {
            quickSave = updateByPrimaryKeySelective(fmsTransportSettledHeader);
            fmsStatusService.updateTransportSettleStatus(fmsTransportSettledHeader.getTransportSettledHeaderId(), "AUDITED", fmsTransportSettledHeader.getLastUpdatedBy(), fmsTransportSettledHeader.getLastUpdateLogin());
        } else {
            quickSave = updateByPrimaryKeySelective(fmsTransportSettledHeader);
            fmsStatusService.updateTransportSettleStatus(fmsTransportSettledHeader.getTransportSettledHeaderId(), "REJECT", fmsTransportSettledHeader.getLastUpdatedBy(), fmsTransportSettledHeader.getLastUpdateLogin());

        }

        return quickSave;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitTransportSettledHeader(
            @RequestBody FmsTransportSettledHeaderRequest fmsTransportSettledHeaderRequest
    ) throws BusinessServiceException {
        Long[] ids = fmsTransportSettledHeaderRequest.getDetail().getTransportSettledHeaderIds();
        if (ids == null || ids.length == 0) {
            throw new BusinessServiceException("数据有误！");
        }
        for (Long id : ids) {
            fmsStatusService.updateTransportSettleStatus(id, "SUBMITED", fmsTransportSettledHeaderRequest.getUserId(), fmsTransportSettledHeaderRequest.getSessionId());
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(FmsTransportSettledHeader fmsTransportSettledHeader) throws BusinessServiceException {
        Long[] ids = fmsTransportSettledHeader.getTransportSettledHeaderIds();
        if (ids == null || ids.length == 0) {
            throw new BusinessServiceException("数据有误！");
        }
        FmsTransportSettledHeader entity = fmsTransportSettledHeader;
        for (Long id : ids) {
            entity.setTransportSettledHeaderId(id);
            //将头置为失效
            fmsTransportSettledHeader.setEnabledFlag("N");
            updateByPrimaryKeySelective(entity);

            //更新行表为失效
            fmsTransportSettledHeaderMapper.updateTransportSettledLineByTransportSettledHeader(entity);

            //释放送车交接单
            fmsStatusService.releaseSendcarData(entity.getTransportSettledHeaderId(), entity.getLastUpdatedBy(), entity.getLastUpdateLogin());
        }

    }


}
