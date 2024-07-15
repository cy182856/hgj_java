package com.ej.hgj.task.service;

import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoHouseDaoMapper;
import com.ej.hgj.dao.repair.RepairLogDaoMapper;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.cstInto.CstIntoHouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CstIntoTaskService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private CstIntoHouseDaoMapper cstIntoHouseDaoMapper;

    public void cstIntoTask(){
        logger.info("----------------------客户入住定时任务处理开始--------------------------- ");
        cstIntoDaoMapper.deleteByStatusAndTime(new CstInto());
        cstIntoHouseDaoMapper.deleteByStatusAndTime(new CstIntoHouse());
        logger.info("----------------------客户入住定时任务处理结束--------------------------- ");
    }

}
