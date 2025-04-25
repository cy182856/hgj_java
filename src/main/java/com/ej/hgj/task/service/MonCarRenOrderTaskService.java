package com.ej.hgj.task.service;

import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.carpay.ParkPayOrderDaoMapper;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoHouseDaoMapper;
import com.ej.hgj.dao.moncarren.MonCarRenOrderDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.cstInto.CstIntoHouse;
import com.ej.hgj.entity.moncarren.MonCarRenOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MonCarRenOrderTaskService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MonCarRenOrderDaoMapper monCarRenOrderDaoMapper;

    public void monCarRenOrderTask(){
        logger.info("----------------------OFW月租车续费未支付订单删除定时任务处理开始--------------------------- ");
        monCarRenOrderDaoMapper.deleteByOrderStatusAndCreateTime();
        logger.info("----------------------OFW月租车续费未支付订单删除定时任务处理结束--------------------------- ");
    }

}
