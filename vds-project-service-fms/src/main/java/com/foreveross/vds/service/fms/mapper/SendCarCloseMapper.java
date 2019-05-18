package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.dto.tms.TmsSendcarLineV;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Villy
 */
@Repository
public interface SendCarCloseMapper {

    List<TmsSendcarLineV> queryTmsSendcarLineV(TmsSendcarLineV tmsSendcarLineV);

    List<TmsSendcarLineV> queryCloseTmsSendcarLineV(TmsSendcarLineV tmsSendcarLineV);

    void updateTmsSendcarHeader(TmsSendcarLine tmsSendcarLine);

    void updateTmsSendcarLine(TmsSendcarLine tmsSendcarLine);

    void recordLineClose(TmsSendcarLine tmsSendcarLine);
}
