package com.foreveross.vds.service.fms.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.foreveross.vds.annotation.CustTx;
import com.foreveross.vds.service.common.exception.BusinessServiceException;
import com.foreveross.vds.service.common.service.LookupService;
import com.foreveross.vds.service.fms.service.*;
import com.foreveross.vds.vo.fms.*;
import com.foreveross.vds.vo.tms.TmsDeparture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.dto.common.BasePageResponse;
import com.foreveross.vds.dto.fms.TransportContract;
import com.foreveross.vds.dto.fms.TransportContractRequest;
import com.foreveross.vds.dto.fms.TransportContractResponse;
import com.foreveross.vds.service.common.service.impl.BaseServiceImpl;
import com.foreveross.vds.service.fms.mapper.FmsTransportContractHeaderMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FmsTransportContractHeaderServiceImpl extends BaseServiceImpl<FmsTransportContractHeader, Long> implements FmsTransportContractHeaderService {

    @Autowired
    private FmsTransportContractHeaderMapper fmsTransportContractHeaderMapper;

    @Autowired
    private FmsTransportContractLineService fmsTransportContractLineService;

    @Autowired
    private DistanceLineService distanceLineService;

    @Autowired
    private LookupService lookupService;

    @Override
    public BasePageResponse pageTransportContract(Integer page, Integer rows, TransportContractRequest transportContractRequest) {
        PageHelper.startPage(page, rows, transportContractRequest.getOrderBy());
        Page<TransportContractResponse> printLoadListResponses = (Page<TransportContractResponse>) this.selectTransportContract(transportContractRequest);
        return new BasePageResponse(printLoadListResponses.getTotal(), printLoadListResponses.getResult());
    }

    @Override
    public BasePageResponse pageTransportContractLine(Integer page, Integer rows, TransportContractRequest transportContractRequest) {
        PageHelper.startPage(page, rows, transportContractRequest.getOrderBy());
        Page<TransportContractResponse> printLoadListResponses = (Page<TransportContractResponse>) this.selectTransportContractLine(transportContractRequest);
        return new BasePageResponse(printLoadListResponses.getTotal(), printLoadListResponses.getResult());
    }

    @Override
    public List<TransportContractResponse> selectTransportContract(TransportContractRequest transportContractRequest) {
        return fmsTransportContractHeaderMapper.selectTransportContract(transportContractRequest);
    }

    @Override
    public List<TransportContractResponse> selectTransportContractLine(TransportContractRequest transportContractRequest) {
        return fmsTransportContractHeaderMapper.selectTransportContractLine(transportContractRequest);
    }

    @Override
    @CustTx
    public void saveTransportContract(TransportContract transportContract, Long userId, String sessionId) throws BusinessServiceException {
        Date now = new Date();
        FmsTransportContractHeader fmsTransportContractHeader = transportContract.getFmsTransportContractHeader();
        fmsTransportContractHeader.setCreatedBy(userId);
        fmsTransportContractHeader.setCreationDate(now);
        fmsTransportContractHeader.setLastUpdatedBy(userId);
        fmsTransportContractHeader.setLastUpdateDate(now);
        fmsTransportContractHeader.setLastUpdateLogin(sessionId);
        fmsTransportContractHeader.setEnabledFlag("Y");
        fmsTransportContractHeader.setConfirmFlag("N");
        fmsTransportContractHeaderMapper.insert(fmsTransportContractHeader);
    }

    @Override
    @CustTx
    public void saveTransportContractLine(TransportContract transportContract, Long userId, String sessionId) throws BusinessServiceException {
        if (transportContract.getFmsTransportContractLine().getDistanceLineId() == null) {
            findDistanceLine(transportContract);
        }

        this.findRegionInfo(transportContract);

        doCalculateUnitFee(transportContract);//计算单车运费

        FmsTransportContractHeader fmsTransportContractHeader = transportContract.getFmsTransportContractHeader();
        FmsTransportContractLine fmsTransportContractLine = transportContract.getFmsTransportContractLine();
        Date now = new Date();

        int lineCount = fmsTransportContractHeaderMapper.isContractExists(transportContract);

        if (lineCount > 1) {
            throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]的合同已有两条重复的合同数据！");
        } else if (lineCount > 0) {
            if (transportContract.getFmsTransportContractHeader().getOverrideContractHeaderId() == null) {
                throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]的合同有重复合同数据，且未选择被覆盖合同！");
            }


            TransportContract entity = fmsTransportContractHeaderMapper.selectContractLineSame(transportContract).get(0);

            
            if (!Objects.equals(entity.getFmsTransportContractHeader().getTransportContractHeaderId(), transportContract.getFmsTransportContractHeader().getOverrideContractHeaderId())) {
                throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]的合同有重复合同数据，且不属于选择的被覆盖合同！");
            }

            fmsTransportContractHeaderMapper.doSetContractPriority(entity);
            fmsTransportContractLine.setContractPriority(2);

        }



        fmsTransportContractLine.setTransportContractHeaderId(fmsTransportContractHeader.getTransportContractHeaderId());
        fmsTransportContractLine.setCreatedBy(userId);
        fmsTransportContractLine.setCreationDate(now);
        fmsTransportContractLine.setLastUpdatedBy(userId);
        fmsTransportContractLine.setLastUpdateDate(now);
        fmsTransportContractLine.setLastUpdateLogin(sessionId);
        fmsTransportContractLine.setEnabledFlag("Y");
        fmsTransportContractLine.setConfirmFlag("N");
        fmsTransportContractLineService.insert(fmsTransportContractLine);
    }

    @Override
    @CustTx
    public void saveTransportContract(List<TransportContract> transportContractList, Long userId, String sessionId) throws BusinessServiceException {
        for (TransportContract transportContract : transportContractList) {
            this.saveTransportContract(transportContract, userId, sessionId);
        }
    }

    /**
     * 根据物流商、所属基地、发运方式、车型大类、起点、终点判断是否已存在合同，存在：开始时间早于当前开始时间的合同置为失效
     *
     * @param transportContract
     * @param userId
     * @param sessionId
     */
    private void doInvalidOldContract(TransportContract transportContract, Long userId, String sessionId) {
        List<TransportContract> oldTransportContractList = findOldContract(transportContract);
        updateTransPortContractEnableFlag(oldTransportContractList, "N", userId, sessionId);
    }

    /**
     * 根据运距下拉框找运距行表，再根据起点、终点、发运方式找到行表数据，得到运距，如果填了过海费或铁路前端倒运费用计算单车运费时就要加上
     *
     * @param transportContract
     */
    private void findDistanceLine(TransportContract transportContract) throws BusinessServiceException {
        FmsDistanceLine fmsDistanceLine = distanceLineService.findDistanceLine(transportContract);
        if (fmsDistanceLine == null) {
            throw new BusinessServiceException("未找到对应运距！");
        }
        transportContract.getFmsTransportContractLine().setDistanceLineId(fmsDistanceLine.getDistanceLineId());
    }

    /**
     * 根据基地信息查询省市县
     *
     * @param transportContract
     */
    private void findRegionInfo(TransportContract transportContract) throws BusinessServiceException {
        TmsDeparture tmsDeparture = new TmsDeparture();
        tmsDeparture.setDepartureId(transportContract.getFmsTransportContractLine().getStartPointId());
        tmsDeparture = fmsTransportContractHeaderMapper.getReginInfoByDeparture(tmsDeparture).get(0);
        if (tmsDeparture == null) {
            throw new BusinessServiceException("未找到对应出发地！");
        }
        transportContract.getFmsTransportContractLine().setStartProvinceId(tmsDeparture.getProvinceId());
        transportContract.getFmsTransportContractLine().setStartCityId(tmsDeparture.getCityId());
        transportContract.getFmsTransportContractLine().setStartCountyId(tmsDeparture.getCountyId());
    }

    /**
     * 将旧合同的重复数据优先级设置更低
     *
     * @param transportContract
     */
    private void doSetContractPriority(TransportContract transportContract) throws BusinessServiceException {
        TmsDeparture tmsDeparture = new TmsDeparture();
        tmsDeparture.setDepartureId(transportContract.getFmsTransportContractLine().getStartPointId());
        tmsDeparture = fmsTransportContractHeaderMapper.getReginInfoByDeparture(tmsDeparture).get(0);
        if (tmsDeparture == null) {
            throw new BusinessServiceException("未找到对应出发地！");
        }
        transportContract.getFmsTransportContractLine().setStartProvinceId(tmsDeparture.getProvinceId());
        transportContract.getFmsTransportContractLine().setStartCityId(tmsDeparture.getCityId());
        transportContract.getFmsTransportContractLine().setStartCountyId(tmsDeparture.getCountyId());
    }

    /**
     * 计算单车运费
     *
     * @param transportContract
     */
    private void doCalculateUnitFee(TransportContract transportContract) {
        BigDecimal unitFee;
        Long railway = lookupService.getLookupId("TRANSPORT_METHOD","RAILWAY");//铁路
        Long waterway = lookupService.getLookupId("TRANSPORT_METHOD","WATERWAY");//水路
        FmsTransportContractLine fmsTransportContractLine = transportContract.getFmsTransportContractLine();


        BigDecimal unitPrice = fmsTransportContractLine.getUnitPrice();
        if (fmsTransportContractLine.getTransportMethod().longValue() == railway.longValue() || fmsTransportContractLine.getTransportMethod().longValue() == waterway.longValue()) {
            unitFee = unitPrice;
        } else {
            FmsDistanceLine fmsDistanceLine = distanceLineService.selectByPrimaryKey(fmsTransportContractLine.getDistanceLineId());
            BigDecimal transportDistance = new BigDecimal(fmsDistanceLine.getTransportDistance());
            unitFee = transportDistance.multiply(unitPrice);
        }

        BigDecimal overSeaFee = fmsTransportContractLine.getOverSeaFee();
        if (overSeaFee != null) {
            unitFee = unitFee.add(overSeaFee);
        }

        BigDecimal railwayPrepareFee = fmsTransportContractLine.getRailwayPrepareFee();
        if (railwayPrepareFee != null) {
            unitFee = unitFee.add(railwayPrepareFee);
        }

        fmsTransportContractLine.setUnitFee(unitFee);
    }

    /**
     * 保存
     *
     * @param transportContract
     * @param userId
     * @param sessionId
     */
    private void insertContract(TransportContract transportContract, Long userId, String sessionId) {
        Date now = new Date();
        FmsTransportContractHeader fmsTransportContractHeader = transportContract.getFmsTransportContractHeader();
        fmsTransportContractHeader.setCreatedBy(userId);
        fmsTransportContractHeader.setCreationDate(now);
        fmsTransportContractHeader.setLastUpdatedBy(userId);
        fmsTransportContractHeader.setLastUpdateDate(now);
        fmsTransportContractHeader.setLastUpdateLogin(sessionId);
        fmsTransportContractHeader.setEnabledFlag("Y");
        fmsTransportContractHeader.setConfirmFlag("N");
        fmsTransportContractHeaderMapper.insert(fmsTransportContractHeader);

        FmsTransportContractLine fmsTransportContractLine = transportContract.getFmsTransportContractLine();
        fmsTransportContractLine.setTransportContractHeaderId(fmsTransportContractHeader.getTransportContractHeaderId());
        fmsTransportContractLine.setCreatedBy(userId);
        fmsTransportContractLine.setCreationDate(now);
        fmsTransportContractLine.setLastUpdatedBy(userId);
        fmsTransportContractLine.setLastUpdateDate(now);
        fmsTransportContractLine.setLastUpdateLogin(sessionId);
        fmsTransportContractLine.setEnabledFlag("Y");
        fmsTransportContractLineService.insert(fmsTransportContractLine);
    }

    /**
     * 根据物流商、所属基地、发运方式、车型大类、起点、终点 查找合同
     *
     * @param transportContract
     * @return
     */
    @Override
    public List<TransportContract> findOldContract(TransportContract transportContract) {
        return fmsTransportContractHeaderMapper.selectOldTransportContract(transportContract);
    }

    /**
     * 修改合同有效标识
     *
     * @param oldTransportContractList
     * @param enableFlag
     * @param userId
     * @param sessionId
     */
    private void updateTransPortContractEnableFlag(List<TransportContract> oldTransportContractList, String enableFlag, Long userId, String sessionId) {
        Date now = new Date();
        for (TransportContract transportContract : oldTransportContractList) {
            FmsTransportContractHeader fmsTransportContractHeader = transportContract.getFmsTransportContractHeader();
            fmsTransportContractHeader.setEnabledFlag(enableFlag);
            fmsTransportContractHeader.setLastUpdatedBy(userId);
            fmsTransportContractHeader.setLastUpdateDate(now);
            fmsTransportContractHeader.setLastUpdateLogin(sessionId);
            this.updateByPrimaryKeySelective(fmsTransportContractHeader);

            FmsTransportContractLine fmsTransportContractLine = transportContract.getFmsTransportContractLine();
            fmsTransportContractLine.setEnabledFlag(enableFlag);
            fmsTransportContractLine.setLastUpdatedBy(userId);
            fmsTransportContractLine.setLastUpdateDate(now);
            fmsTransportContractLine.setLastUpdateLogin(sessionId);
            fmsTransportContractLineService.updateByPrimaryKeySelective(fmsTransportContractLine);
        }

    }

    @Override
    @CustTx
    public void updateTransportContract(TransportContract transportContract, Long userId) {
        Date now = new Date();
        doCalculateUnitFee(transportContract);//计算单车运费

        FmsTransportContractHeader fmsTransportContractHeader = transportContract.getFmsTransportContractHeader();
        fmsTransportContractHeader.setLastUpdateDate(now);
        fmsTransportContractHeader.setLastUpdatedBy(userId);
        fmsTransportContractHeaderMapper.updateByPrimaryKeySelective(fmsTransportContractHeader);

        FmsTransportContractLine fmsTransportContractLine = transportContract.getFmsTransportContractLine();
        fmsTransportContractLine.setLastUpdateDate(now);
        fmsTransportContractLine.setLastUpdatedBy(userId);
        fmsTransportContractLineService.updateByPrimaryKeySelective(fmsTransportContractLine);
    }

	@Override
	@CustTx
	public void deleteTransportContractHeader(TransportContract transportContract) throws BusinessServiceException {
		if (fmsTransportContractHeaderMapper
				.selectByPrimaryKey(transportContract.getFmsTransportContractHeader().getTransportContractHeaderId())
				.getConfirmFlag().equals("Y")) {
			throw new BusinessServiceException("不能删除已经开始审核的合同数据！");
		}
		fmsTransportContractHeaderMapper.deleteTransportContractHeader(transportContract);
		fmsTransportContractHeaderMapper.deleteTransportContractLine(transportContract);
	}

    @Override
    @CustTx
    public void deleteTransportContractLine(TransportContract transportContract) throws BusinessServiceException {
        if (transportContract.getFmsTransportContractLine().getTransportContractLineId() == null) {
            throw new BusinessServiceException("数据有误，请联系系统管理员！");
        }
        ;
        if(fmsTransportContractHeaderMapper.selectLineByPrimaryKey(transportContract.getFmsTransportContractLine().getTransportContractLineId()).getConfirmFlag().equals("Y")) {
        	throw new BusinessServiceException("不能删除已经审核的合同数据！");
        }
        fmsTransportContractHeaderMapper.deleteTransportContractLine(transportContract);
    }

    @Override
    @CustTx
    public void doConfirmTransportContract(List<Long> transportContractHeaderIds, Long userId, String sessionId) {
        Date now = new Date();
        for (Long transportContractHeaderId : transportContractHeaderIds) {
            FmsTransportContractHeader fmsTransportContractHeader = new FmsTransportContractHeader();
            fmsTransportContractHeader.setTransportContractHeaderId(transportContractHeaderId);
            fmsTransportContractHeader.setConfirmFlag("Y");
            fmsTransportContractHeader.setLastUpdateDate(now);
            fmsTransportContractHeader.setLastUpdatedBy(userId);
            fmsTransportContractHeader.setLastUpdateLogin(sessionId);
            fmsTransportContractHeaderMapper.updateByPrimaryKeySelective(fmsTransportContractHeader);
            FmsTransportContractLine fmsTransportContractLine = new FmsTransportContractLine();
            fmsTransportContractLine.setTransportContractHeaderId(transportContractHeaderId);
            fmsTransportContractLine.setConfirmFlag("Y");
            fmsTransportContractLine.setLastUpdateDate(now);
            fmsTransportContractLine.setLastUpdatedBy(userId);
            fmsTransportContractLine.setLastUpdateLogin(sessionId);
            fmsTransportContractLineService.updateByPrimaryKeySelectiveAll(fmsTransportContractLine);
            
        }
    }
    
    public void doConfirmTransportContractLine(List<Long> transportContractLineIds, Long userId, String sessionId) {
        Date now = new Date();
        for (Long transportContractLineId : transportContractLineIds) {
            
        	FmsTransportContractHeader fmsTransportContractHeaderForFlag = 
            		fmsTransportContractHeaderMapper.selectByPrimaryKey(fmsTransportContractLineService.selectByPrimaryKey(transportContractLineId).getTransportContractHeaderId());
            if(fmsTransportContractHeaderForFlag.getConfirmFlag().equalsIgnoreCase("N")) {
            	FmsTransportContractHeader fmsTransportContractHeader = new FmsTransportContractHeader();
                fmsTransportContractHeader.setTransportContractHeaderId(fmsTransportContractHeaderForFlag.getTransportContractHeaderId());
                fmsTransportContractHeader.setConfirmFlag("Y");
                fmsTransportContractHeader.setLastUpdateDate(now);
                fmsTransportContractHeader.setLastUpdatedBy(userId);
                fmsTransportContractHeader.setLastUpdateLogin(sessionId);
                fmsTransportContractHeaderMapper.updateByPrimaryKeySelective(fmsTransportContractHeader);
            }

            FmsTransportContractLine fmsTransportContractLine = new FmsTransportContractLine();
            fmsTransportContractLine.setTransportContractLineId(transportContractLineId);
            fmsTransportContractLine.setConfirmFlag("Y");
            fmsTransportContractLine.setLastUpdateDate(now);
            fmsTransportContractLine.setLastUpdatedBy(userId);
            fmsTransportContractLine.setLastUpdateLogin(sessionId);
            fmsTransportContractLineService.updateByPrimaryKeySelective(fmsTransportContractLine);
            
        }
    }

    @Override
    public void checkParams(TransportContract transportContract) throws BusinessServiceException {
        if (fmsTransportContractHeaderMapper.isContractNumberRepeat(transportContract) != 0) {
            throw new BusinessServiceException("合同号:["+ transportContract.getFmsTransportContractHeader().getContractNumber() + "]已被使用！");
        }

        if (fmsTransportContractHeaderMapper.isContractNameRepeat(transportContract) != 0) {
            throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]已被使用！");
        }

    }

    @Override
    public int isContractHeaderExists(TransportContract transportContract) {
        Integer ret = fmsTransportContractHeaderMapper.isContractNumberAndNameRepeat(transportContract);
        return ret == null ? 0 : ret;

    }

    @Override
    public void checkContractLine(TransportContract transportContract) throws BusinessServiceException {
        Long lookupId = lookupService.findLookUpId("CONTRACT_TYPE", "FORMAL");//正式合同类别


        if (fmsTransportContractHeaderMapper.isContractLineRepeat(transportContract) > 0) {
            throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]的合同存在重复合同数据！");
        }

        int isContractExists = fmsTransportContractHeaderMapper.isContractExists(transportContract);

        if (isContractExists > 1) {
            throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]的合同已有两条重复的合同数据！");
        }

        if (isContractExists > 0) {//该合同明细有符合条件的相同数据
            if (transportContract.getFmsTransportContractHeader().getOverrideContractHeaderId() == null) {
                throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]的合同有重复合同数据，且未选择被覆盖合同！");
            }
        }
        if (Objects.equals(transportContract.getFmsTransportContractHeader().getContractType(), lookupId)) {//如果是正式合同，则比较与前一条合同的结束日期+1是否吻合
            if (fmsTransportContractHeaderMapper.isContractDateMatch(transportContract) > 0) {
                throw new BusinessServiceException("合同名称:["+ transportContract.getFmsTransportContractHeader().getContractName() + "]的合同开始时间与原合同结束时间不吻合！");
            }
        }

    }
}
