package com.ej.hgj.task.service;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoHouseDaoMapper;
import com.ej.hgj.dao.repair.RepairLogDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
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

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    public void cstIntoTask(){
        logger.info("----------------------客户入住定时任务处理开始--------------------------- ");
        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.NOT_CST_INTO_DELETE_DAY);
        // 删除入住信息
        Integer day = Integer.valueOf(constantConfig.getConfigValue());
        CstInto cstInto = new CstInto();
        cstInto.setDay(day);
        cstIntoDaoMapper.deleteByStatusAndTime(cstInto);
        // 删除入住房屋信息
        CstIntoHouse cstIntoHouse = new CstIntoHouse();
        cstIntoHouse.setDay(day);
        cstIntoHouseDaoMapper.deleteByStatusAndTime(cstIntoHouse);
        logger.info("----------------------客户入住定时任务处理结束--------------------------- ");
    }

}
