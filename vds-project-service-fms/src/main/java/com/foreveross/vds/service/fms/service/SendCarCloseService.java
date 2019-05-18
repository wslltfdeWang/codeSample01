package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.tms.TmsSendcarDto;
import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.dto.tms.TmsSendcarLineV;
import com.github.pagehelper.Page;

/**
 * @author Villy
 */
public interface SendCarCloseService {
    /**
     * 分页查询送车交接单视图
     *
     * @param tmsSendcarLineV
     * @return
     */
    BasePageResponse queryTmsSendcarLineV(TmsSendcarLineV tmsSendcarLineV);

    BasePageResponse queryCloseTmsSendcarLineV(TmsSendcarLineV tmsSendcarLineV);

    /**
     * 修改送车交接单关闭状态
     * @param tmsSendcarLine
     * @exception Exception
     */
    void updateTmsSendcarLine(TmsSendcarLine tmsSendcarLine) throws Exception;
}
