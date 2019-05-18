package com.foreveross.vds.service.fms.service.impl;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.dto.tms.TmsSendcarLineV;
import com.foreveross.vds.service.fms.mapper.SendCarCloseMapper;
import com.foreveross.vds.service.fms.service.SendCarCloseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Villy
 */
@Service
public class SendCarCloseServiceImpl implements SendCarCloseService {

    @Autowired
    private SendCarCloseMapper sendCarCloseMapper;

    @Override
    public BasePageResponse queryTmsSendcarLineV(TmsSendcarLineV tmsSendcarLineV) {
        PageHelper.startPage(tmsSendcarLineV.getPage(), tmsSendcarLineV.getRows(), tmsSendcarLineV.getOrderBy());
        Page<TmsSendcarLineV> page = (Page<TmsSendcarLineV>) sendCarCloseMapper.queryTmsSendcarLineV(tmsSendcarLineV);

        BasePageResponse basePageResponse = new BasePageResponse(page.getTotal(), page.getResult());
        return basePageResponse;
    }

    @Override
    public BasePageResponse queryCloseTmsSendcarLineV(TmsSendcarLineV tmsSendcarLineV) {
        PageHelper.startPage(tmsSendcarLineV.getPage(), tmsSendcarLineV.getRows(), tmsSendcarLineV.getOrderBy());
        Page<TmsSendcarLineV> page = (Page<TmsSendcarLineV>) sendCarCloseMapper.queryCloseTmsSendcarLineV(tmsSendcarLineV);

        BasePageResponse basePageResponse = new BasePageResponse(page.getTotal(), page.getResult());
        return basePageResponse;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTmsSendcarLine(TmsSendcarLine tmsSendcarLine) throws Exception {
        sendCarCloseMapper.updateTmsSendcarHeader(tmsSendcarLine);
        sendCarCloseMapper.updateTmsSendcarLine(tmsSendcarLine);
        sendCarCloseMapper.recordLineClose(tmsSendcarLine);
    }
}
