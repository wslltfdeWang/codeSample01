package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.vo.fms.TmsSendcarPrintRecord;

public interface TmsSendcarPrintRecordMapper {
    int deleteByPrimaryKey(Short sendcarRecordId);

    int insert(TmsSendcarPrintRecord record);

    int insertSelective(TmsSendcarPrintRecord record);

    TmsSendcarPrintRecord selectByPrimaryKey(Short sendcarRecordId);

    int updateByPrimaryKeySelective(TmsSendcarPrintRecord record);

    int updateByPrimaryKey(TmsSendcarPrintRecord record);
}