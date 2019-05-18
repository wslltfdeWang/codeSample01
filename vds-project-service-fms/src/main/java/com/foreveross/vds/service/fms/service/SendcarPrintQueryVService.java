package com.foreveross.vds.service.fms.service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.service.common.service.BaseService;
import com.foreveross.vds.vo.fms.SendcarPrintQueryV;

import java.util.List;

/**
 * @author ym
 * @desc
 * @since 2018/7/23-11:01
 */
public interface SendcarPrintQueryVService extends BaseService<SendcarPrintQueryV, Long>{

    /**
     * 获取出场外库时间
     * @param sendcarPrintQueryVList
     */
    void selectOutDate(List<SendcarPrintQueryV> sendcarPrintQueryVList);

    void setFooter(BasePageResponse basePageResponse);

}
