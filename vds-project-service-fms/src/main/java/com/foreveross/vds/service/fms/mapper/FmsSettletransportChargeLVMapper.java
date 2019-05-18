package com.foreveross.vds.service.fms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.foreveross.vds.dto.fms.FmsSettletransportChargeLVRequest;
import com.foreveross.vds.vo.fms.FmsSettletransportChargeLV;

/***
 *
 */
@Mapper
public interface FmsSettletransportChargeLVMapper {

    /**
     * 查询结算运距行数据
     * @param fmsSettletransportChargeLVRequest
     * @return
     */
    List<FmsSettletransportChargeLV> selectPmsSendcarLine(FmsSettletransportChargeLVRequest fmsSettletransportChargeLVRequest);
}
