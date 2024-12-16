package com.ej.hgj.task.service;

import com.ej.hgj.dao.opendoor.OpenDoorQuickCodeDaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class QuickCodeTaskService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OpenDoorQuickCodeDaoMapper openDoorQuickCodeDaoMapper;

    public void quickCodeTask(){
        logger.info("----------------------删除无效快速通行码定时任务处理开始--------------------------- ");
        openDoorQuickCodeDaoMapper.deleteQuickCode();
        logger.info("----------------------删除无效快速通行码定时任务处理结束--------------------------- ");
    }

}
