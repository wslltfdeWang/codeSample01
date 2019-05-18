package com.foreveross.vds.service.fms.controller;

import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.vds.dto.BaseResponse;
import com.foreveross.vds.dto.DetailResponse;
import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.DetailPageRequest;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeaderRequest;
import com.foreveross.vds.service.fms.service.FmsTransportSettledHeaderService;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;

@Service
@RequestMapping("/fms/transport_settled_header")
public class FmsTransportSettledHeaderController {

    @Autowired
    private FmsTransportSettledHeaderService fmsTransportSettledHeaderService;
    @Autowired
    private LookupService lookupService;

    @RequestMapping("/query_page")
    @ResponseBody
    public BaseResponse queryPage(
            @RequestBody FmsTransportSettledHeaderRequest fmsTransportSettledHeaderRequest
    ) {
        fmsTransportSettledHeaderRequest.setOrderBy("CREATION_DATE desc");
        BasePageResponse<FmsTransportSettledHeader> queryPage = fmsTransportSettledHeaderService.queryPage(fmsTransportSettledHeaderRequest);
        return new DetailResponse<>(queryPage);
    }

    @RequestMapping("/query_confirm")
    @ResponseBody
    public BaseResponse queryConfirm(
            @RequestBody FmsTransportSettledHeaderRequest fmsTransportSettledHeaderRequest
    ) {
        fmsTransportSettledHeaderRequest.setOrderBy("CREATION_DATE desc");
        Long lookupId = lookupService.getLookupId("SETTLE_STATUS", "SUBMITED");
        fmsTransportSettledHeaderRequest.getDetail().setSettledStatus(lookupId);
        BasePageResponse<FmsTransportSettledHeader> queryPage = fmsTransportSettledHeaderService.queryPage(fmsTransportSettledHeaderRequest);
        return new DetailResponse<>(queryPage);
    }

    /**
     * @param fmsTransportSettledHeaderRequest
     * @return
     */
    @RequestMapping("/submit")
    @ResponseBody
    public BaseResponse submibTransportSettledHeader(
            @RequestBody FmsTransportSettledHeaderRequest fmsTransportSettledHeaderRequest
    ) {
        try {
            fmsTransportSettledHeaderService.submitTransportSettledHeader(fmsTransportSettledHeaderRequest);
        } catch (BusinessServiceException e) {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setMessage(e.getMessage());
        }
        return new BaseResponse();
    }

    @RequestMapping("/save")
    @ResponseBody
    public BaseResponse save(
            @RequestBody FmsTransportSettledHeader fmsTransportSettledHeader
    ) throws BusinessServiceException {
        if (fmsTransportSettledHeader.getRejectFlag() == null) {
            throw new BusinessServiceException("审批状态为空！");
        }

        if (fmsTransportSettledHeader.getRejectFlag().equals("Y")) {

        }

        int quickSave = fmsTransportSettledHeaderService.saveHeader(fmsTransportSettledHeader);
        return new DetailResponse<>(quickSave);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public BaseResponse delete(
            @RequestBody FmsTransportSettledHeader fmsTransportSettledHeader
    ) {
        try {
            fmsTransportSettledHeaderService.delete(fmsTransportSettledHeader);
        } catch (BusinessServiceException e) {
            BaseResponse br = new BaseResponse();
            br.setMessage(e.getMessage());
        }
        return new BaseResponse();
    }
}
