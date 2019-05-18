package com.foreveross.vds.service.fms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foreveross.vds.dto.fms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foreveross.vds.service.fms.mapper.FmsExpenseAccountMapper;
import com.foreveross.vds.service.fms.service.FmsExpenseAccountService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class FmsExpenseAccountServiceImpl implements FmsExpenseAccountService {

    @Autowired
    private FmsExpenseAccountMapper fmsExpenseAccountMapper;

    @Override
    public FmsExpenseAccount addFmsExpenseAccount(FmsExpenseAccount fmsExpenseAccount) {
        fmsExpenseAccountMapper.addFmsExpenseAccount(fmsExpenseAccount);
        return fmsExpenseAccount;
    }

    @Override
    public Page<FmsExpenseAccount> queryFmsExpenseAccountByPage(FmsExpenseAccount fmsExpenseAccount) {
        PageHelper.startPage(fmsExpenseAccount.getPage(), fmsExpenseAccount.getRows());
        Page<FmsExpenseAccount> page = (Page<FmsExpenseAccount>) fmsExpenseAccountMapper.queryFmsExpenseAccountList(fmsExpenseAccount);
        return page;
    }

    @Override
    public FmsExpenseAccount getFmsExpenseAccountByProInstId(String proInstId) {
        return fmsExpenseAccountMapper.getFmsExpenseAccountByProInstId(proInstId);
    }

    @Override
    public void addFmsExpenseDetail(FmsExpenseDetail fmsExpenseDetail) {
        fmsExpenseAccountMapper.addFmsExpenseDetail(fmsExpenseDetail);
    }

    @Override
    public void addFmsExpenseContractDetail(FmsExpenseContractDetail fmsExpenseContractDetail) {
        fmsExpenseAccountMapper.addFmsExpenseContractDetail(fmsExpenseContractDetail);
    }

    @Override
    public List<FmsExpenseDetail> queryFmsExpenseDetailByExpenseAccountId(Long expenseAccountId) {
        return fmsExpenseAccountMapper.queryFmsExpenseDetailByExpenseAccountId(expenseAccountId);
    }

    @Override
    public List<FmsExpenseContractDetail> queryFmsContractExpenseDetailByExpenseAccountId(Long expenseAccountId) {
        return fmsExpenseAccountMapper.queryFmsExpenseContractDetailByExpenseAccountId(expenseAccountId);
    }

    @Override
    public Contarct getContByIdAndLookupId(Long contId, Long lookupId) {
        Map<String, Long> map = new HashMap<String, Long>();
        map.put("lookupId", lookupId);
        map.put("contId", contId);
        return fmsExpenseAccountMapper.getContByIdAndLookupId(map);
    }

    @Override
    public FmsExpenseAccount getFmsExpenseAccountById(Long expenseAccountId) {
        return fmsExpenseAccountMapper.getFmsExpenseAccountById(expenseAccountId);
    }
}
