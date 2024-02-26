package com.ej.hgj.service.contract;

import com.ej.hgj.dao.contract.HgjContractDaoMapper;
import com.ej.hgj.entity.contract.HgjContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ContractServiceImpl implements ContractService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HgjContractDaoMapper hgjContractDaoMapper;


    public List<HgjContract> getList(HgjContract hgjContract){
        return hgjContractDaoMapper.getList(hgjContract);
    }

}
