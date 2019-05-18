package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.common.DetailPageRequest;
import com.foreveross.vds.dto.fms.FmsTransportSettledHeaderRequest;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.FmsTransportSettledHeader;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Villy
 */
public interface FmsTransportSettledHeaderService extends BaseService<FmsTransportSettledHeader, Long> {

    /**
     * @param fmsTransportSettledHeader
     * @return
     * @throws BusinessServiceException
     */
    int saveHeader(
            @RequestBody FmsTransportSettledHeader fmsTransportSettledHeader
    ) throws BusinessServiceException;


    /**
     * @param fmsTransportSettledHeaderRequest
     * @return
     * @throws BusinessServiceException
     */
    int submitTransportSettledHeader(
            @RequestBody FmsTransportSettledHeaderRequest fmsTransportSettledHeaderRequest
    ) throws BusinessServiceException;

    @Transactional(rollbackFor = Exception.class)
    void delete(FmsTransportSettledHeader fmsTransportSettledHeader) throws BusinessServiceException;
}
