package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import com.foreveross.vds.dto.tms.TmsSendcarLine;
import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.SendcarPrintVNew;

/**
 * 打印送车交接单
 */
public interface SendcarPrintVMapper extends BaseMapper<SendcarPrintVNew, Long>{
	List<TmsSendcarLine> queryLineList(SendcarPrintVNew entity);
}