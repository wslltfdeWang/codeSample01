package com.foreveross.vds.service.fms.mapper;

import com.foreveross.vds.service.common.mapper.BaseMapper;
import com.foreveross.vds.vo.fms.SendcarPrintOutDate;
import com.foreveross.vds.vo.fms.SendcarPrintQueryV;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SendcarPrintQueryVMapper extends BaseMapper<SendcarPrintQueryV, Long>{

    List<SendcarPrintOutDate> selectCarOutDate(Long sendcarHeaderId);

}