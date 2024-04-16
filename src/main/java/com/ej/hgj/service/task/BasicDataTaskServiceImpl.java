package com.ej.hgj.service.task;

import com.ej.hgj.dao.contract.HgjContractDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.house.HgjHouseDaoMapper;
import com.ej.hgj.dao.rcr.HgjRcrDaoMapper;
import com.ej.hgj.entity.contract.SyContract;
import com.ej.hgj.entity.cst.SyCst;
import com.ej.hgj.entity.house.SyHouse;
import com.ej.hgj.entity.rcr.SyRcr;
import com.ej.hgj.sy.dao.contract.SyContractDaoMapper;
import com.ej.hgj.sy.dao.cst.SyCstDaoMapper;
import com.ej.hgj.sy.dao.house.SyHouseDaoMapper;
import com.ej.hgj.sy.dao.rcr.SyRcrDaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class BasicDataTaskServiceImpl implements BasicDataTaskService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SyContractDaoMapper syContractDaoMapper;

    @Autowired
    private HgjContractDaoMapper hgjContractDaoMapper;

    @Autowired
    private SyHouseDaoMapper syHouseDaoMapper;

    @Autowired
    private HgjHouseDaoMapper hgjHouseDaoMapper;

    @Autowired
    private SyCstDaoMapper syCstDaoMapper;

    @Autowired
    private HgjCstDaoMapper hgjCstDaoMapper;

    @Autowired
    private SyRcrDaoMapper syRcrDaoMapper;

    @Autowired
    private HgjRcrDaoMapper hgjRcrDaoMapper;

    @Override
    public void basicDataSync() {
        logger.info("------------------基础数据同步开始!---------------");
        /**合同同步*/
        List<SyContract> syContractList = syContractDaoMapper.getList();
        if(!syContractList.isEmpty()) {
            for(SyContract syContract : syContractList){
                syContract.setCreateTime(new Date());
                syContract.setUpdateTime(new Date());
            }
            logger.info("查询思源合同数量:" + syContractList.size());
            // 删除惠管家所有合同
            hgjContractDaoMapper.delete();
            logger.info("删除惠管家合同成功!");
            // 插入思源系统的合同到惠管家
            hgjContractDaoMapper.insertList(syContractList);
            logger.info("合同插入成功,插入数量:" + syContractList.size());
        }

        /**房屋同步*/
        List<SyHouse> syHouseList = syHouseDaoMapper.getList();
        if(!syHouseList.isEmpty()){
            for (SyHouse syHouse : syHouseList){
                syHouse.setCreateTime(new Date());
                syHouse.setUpdateTime(new Date());
            }
            logger.info("查询思源房屋数量:" + syHouseList.size());
            // 删除惠管家所有房屋
            hgjHouseDaoMapper.delete();
            logger.info("删除惠管家房屋成功!");
            // 插入思源的房屋到惠管家
            hgjHouseDaoMapper.insertList(syHouseList);
            logger.info("房屋插入成功,插入数量:" + syHouseList.size());
        }

        /**客户档案*/
        // 10000 - 东方渔人码头
        List<SyCst> syCstListOfw = syCstDaoMapper.getListByProNumOfw();
        if(!syCstListOfw.isEmpty()){
            for (SyCst syCst : syCstListOfw){
                syCst.setCreateTime(new Date());
                syCst.setUpdateTime(new Date());
            }
            logger.info("查询思源东方渔人码头客户档案数量:" + syCstListOfw.size());
            // 删除惠管家所有客户档案
            hgjCstDaoMapper.delete("10000");
            logger.info("删除惠管家东方渔人码头客户档案成功!");
            // 插入思源系统的客户档案到惠管家
            hgjCstDaoMapper.insertList(syCstListOfw);
            logger.info("东方渔人码头客户档案插入成功,插入数量:" + syCstListOfw.size());
        }
        // 10001 - 新弘北外滩
        List<SyCst> syCstListXh = syCstDaoMapper.getListByProNumXh();
        if(!syCstListXh.isEmpty()){
            for (SyCst syCst : syCstListXh){
                syCst.setCreateTime(new Date());
                syCst.setUpdateTime(new Date());
            }
            logger.info("查询思源新弘北外滩客户档案数量:" + syCstListXh.size());
            // 删除惠管家所有客户档案
            hgjCstDaoMapper.delete("10001");
            logger.info("删除惠管家新弘北外滩客户档案成功!");
            // 插入思源系统的客户档案到惠管家
            hgjCstDaoMapper.insertList(syCstListXh);
            logger.info("新弘北外滩客户档案插入成功,插入数量:" + syCstListXh.size());
        }

        /**客户合同资源关联信息*/
        List<SyRcr> syRcrList = syRcrDaoMapper.getList();
        if(!syRcrList.isEmpty()){
            for (SyRcr syRcr : syRcrList){
                syRcr.setCreateTime(new Date());
                syRcr.setUpdateTime(new Date());
            }
            logger.info("查询思源客户合同资源关联信息数量:" + syRcrList.size());
            // 删除惠管家所有合同资源关联信息
            hgjRcrDaoMapper.delete();
            logger.info("删除惠管家客户合同资源关联信息成功!");
            // 插入思源的客户合同资源关联信息到惠管家
            hgjRcrDaoMapper.insertList(syRcrList);
            logger.info("客户合同资源关联信息插入成功,插入数量:" + syRcrList.size());
        }
        logger.info("------------------基础数据同步结束!---------------");

    }

}
