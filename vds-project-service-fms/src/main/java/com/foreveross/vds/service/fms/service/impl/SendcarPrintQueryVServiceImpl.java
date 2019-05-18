package com.foreveross.vds.service.fms.service.impl;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.SendcarPrintQueryVMapper;
import com.foreveross.vds.service.fms.service.SendcarPrintQueryVService;
import com.foreveross.vds.vo.fms.SendcarPrintOutDate;
import com.foreveross.vds.vo.fms.SendcarPrintQueryV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ym
 * @desc
 * @since 2018/7/23-11:01
 */
@Service
public class SendcarPrintQueryVServiceImpl extends BaseServiceImpl<SendcarPrintQueryV, Long> implements SendcarPrintQueryVService {

    @Autowired
    private SendcarPrintQueryVMapper sendcarPrintQueryVMapper;

    @Override
    public void selectOutDate(List<SendcarPrintQueryV> sendcarPrintQueryVList) {
        for(SendcarPrintQueryV sendcarPrintQueryV : sendcarPrintQueryVList){
            selectOutDate(sendcarPrintQueryV);
        }
    }

    @Override
    public void setFooter(BasePageResponse basePageResponse) {
        List<SendcarPrintQueryV> sendcarPrintQueryVList = basePageResponse.getRows();
        long totalCarQuantity = 0;
        for(SendcarPrintQueryV sendcarPrintQueryV : sendcarPrintQueryVList){
            Long carQuantity = sendcarPrintQueryV.getCarQuantity() == null ? 0 : sendcarPrintQueryV.getCarQuantity();
            totalCarQuantity += carQuantity;
        }


        List footer = new ArrayList();
        Map map = new HashMap<>();
        map.put("manager", "数量合计");
        map.put("carQuantity", totalCarQuantity);
        footer.add(map);
        basePageResponse.setFooter(footer);
    }

    public void selectOutDate(SendcarPrintQueryV sendcarPrintQueryV){
        List<SendcarPrintOutDate> sendcarPrintOutDateList = sendcarPrintQueryVMapper.selectCarOutDate(sendcarPrintQueryV.getSendcarHeaderId());

        for(SendcarPrintOutDate sendcarPrintOutDate : sendcarPrintOutDateList){
            Date outDate = sendcarPrintOutDate.getOutDate();
            if(outDate == null){
                return;
            }
        }

        sendcarPrintQueryV.setOutDate(sendcarPrintOutDateList.get(0).getOutDate());
    }
}
