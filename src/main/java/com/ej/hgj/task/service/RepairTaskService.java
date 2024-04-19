package com.ej.hgj.task.service;

import com.ej.hgj.dao.material.MaterialApplyDaoMapper;
import com.ej.hgj.dao.repair.RepairLogDaoMapper;
import com.ej.hgj.entity.repair.RepairLog;
import com.ej.hgj.entity.workord.Material;
import com.ej.hgj.entity.workord.MaterialApply;
import com.ej.hgj.entity.workord.ReturnVisit;
import com.ej.hgj.entity.workord.WorkOrd;
import com.ej.hgj.sy.dao.workord.MaterialDaoMapper;
import com.ej.hgj.sy.dao.workord.ReturnVisitDaoMapper;
import com.ej.hgj.sy.dao.workord.WorkOrdDaoMapper;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RepairTaskService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RepairLogDaoMapper repairLogDaoMapper;

    @Autowired
    private WorkOrdDaoMapper workOrdDaoMapper;

    @Autowired
    private MaterialDaoMapper materialDaoMapper;

    @Autowired
    private MaterialApplyDaoMapper materialApplyDaoMapper;

    @Autowired
    private ReturnVisitDaoMapper returnVisitDaoMapper;

    @Transactional
    public void repairWoStaSubUpdate() {
        logger.info("----------------------已提交工单定时任务处理开始--------------------------- ");
        // 查询惠管家已提交工单
        RepairLog repairLog = new RepairLog();
        repairLog.setRepairStatus("1");
        List<RepairLog> wOStaSublist = repairLogDaoMapper.getList(repairLog);

        // 根据惠管家工单编号获取思源工单数据
        List<String> woNoList = new ArrayList<>();
        for(RepairLog r : wOStaSublist){
            woNoList.add(r.getRepairNum());
        }
        if (!woNoList.isEmpty()) {
            List<WorkOrd> syWorkOrdList = workOrdDaoMapper.getList(woNoList);
            if(!syWorkOrdList.isEmpty()){
                for(WorkOrd workOrd : syWorkOrdList){
                    // 如果思源工单状态是处理中，就获取处理中数据到惠管家，同时修改惠管家工单状态为处理中
                    if("WOSta_Proc".equals(workOrd.getWorkOrdState())){
                        RepairLog repairLog1 = new RepairLog();
                        repairLog1.setRepairNum(workOrd.getWoNo());
                        repairLog1.setOrders(workOrd.getOrders());
                        repairLog1.setOrdersTime(workOrd.getOrdersTime());
                        repairLog1.setRepairStatus("WOSta_Proc");
                        repairLog1.setUpdateTime(new Date());
                        repairLogDaoMapper.update(repairLog1);
                    }else if("WOSta_Finish".equals(workOrd.getWorkOrdState())){
                        // 已完工报修单处理
                        repairFinish(workOrd);
                    }
                }
            }
        }
        logger.info("----------------------已提交工单定时任务处理结束--------------------------- ");
    }


    @Transactional
    public void repairWoStaProcUpdate() {
        logger.info("----------------------处理中工单定时任务处理开始--------------------------- ");
        // 查询惠管家处理中工单
        RepairLog repairLog = new RepairLog();
        repairLog.setRepairStatus("2");
        List<RepairLog> wOStaProcList = repairLogDaoMapper.getList(repairLog);

        // 根据惠管家工单编号获取思源工单数据
        List<String> woNoList = new ArrayList<>();
        for(RepairLog r : wOStaProcList){
            woNoList.add(r.getRepairNum());
        }
        if (!woNoList.isEmpty()) {
            List<WorkOrd> syWorkOrdList = workOrdDaoMapper.getList(woNoList);
            // 如果思源工单状态是已完成，就获取处理中数据到惠管家，同时修改惠管家工单状态为已完成
            if(!syWorkOrdList.isEmpty()){
                for(WorkOrd workOrd : syWorkOrdList){
                    // 已完工报修单处理
                    repairFinish(workOrd);
                }
            }
        }
        logger.info("----------------------处理中工单定时任务处理结束--------------------------- ");
    }


    @Transactional
    public void repairWoStaFinishUpdate() {
        logger.info("----------------------已完工工单定时任务处理开始--------------------------- ");
        // 查询惠管家已完工工单
        RepairLog repairLog = new RepairLog();
        repairLog.setRepairStatus("10");
        List<RepairLog> wOStaFinishList = repairLogDaoMapper.getList(repairLog);

        // 根据惠管家工单编号获取思源工单数据
        List<String> woNoList = new ArrayList<>();
        for(RepairLog r : wOStaFinishList){
            woNoList.add(r.getRepairNum());
        }
        if (!woNoList.isEmpty()) {
            List<WorkOrd> syWorkOrdList = workOrdDaoMapper.getList(woNoList);
            // 如果思源工单状态是已回访，就获取已回访数据到惠管家，同时修改惠管家工单状态为已回访
            if(!syWorkOrdList.isEmpty()){
                for(WorkOrd workOrd : syWorkOrdList){
                    if("WOSta_Visit".equals(workOrd.getWorkOrdState())){
                        // 根据报修单id查询思源已回访记录
                        List<ReturnVisit> list = returnVisitDaoMapper.getList(workOrd.getId());
                        // 根据报修单id查询人工费、材料费
                        WorkOrd costSum = workOrdDaoMapper.getCostSum(workOrd.getId());
                        if(!list.isEmpty()){
                            ReturnVisit returnVisit = list.get(0);
                            RepairLog rep = new RepairLog();
                            rep.setRepairNum(workOrd.getWoNo());
                            rep.setOrders(workOrd.getOrders());
                            rep.setOrdersTime(workOrd.getOrdersTime());
                            rep.setCompletionTime(workOrd.getCompletionTime());
                            rep.setLabourCost(costSum.getLabourCost());
                            rep.setMaterialCost(costSum.getMaterialCost());
                            // 满意
                            if("Satisfaction2".equals(returnVisit.getReturnSatisfaction())){
                                rep.setSatisFaction("0");
                            // 不满意
                            }else if("NotSatisfaction1".equals(returnVisit.getReturnSatisfaction())){
                                rep.setSatisFaction("1");
                            }
                            String repairScore = "";
                            if(StringUtils.isNotBlank(returnVisit.getTotalScore())){
                                Double doubleScore = new Double(returnVisit.getTotalScore());
                                if(doubleScore >= 20 && doubleScore < 40){
                                    repairScore = "1";
                                } else if(doubleScore >= 40 && doubleScore < 60){
                                    repairScore = "2";
                                } else if(doubleScore >= 60 && doubleScore < 80){
                                    repairScore = "3";
                                } else if(doubleScore >= 80 && doubleScore < 100){
                                    repairScore = "4";
                                } else if(doubleScore == 100){
                                    repairScore = "5";
                                }
                            }
                            rep.setRepairScore(repairScore);
                            rep.setRepairMsg(returnVisit.getMemo());
                            rep.setRepairStatus("WOSta_Visit");
                            rep.setUpdateTime(new Date());
                            repairLogDaoMapper.update(rep);
                        }
                    }else if("WOSta_Close".equals(workOrd.getWorkOrdState())){
                        RepairLog repairLog1 = new RepairLog();
                        repairLog1.setRepairNum(workOrd.getWoNo());
                        repairLog1.setOrders(workOrd.getOrders());
                        repairLog1.setOrdersTime(workOrd.getOrdersTime());
                        repairLog1.setRepairStatus("WOSta_Close");
                        repairLog1.setUpdateTime(new Date());
                        repairLogDaoMapper.update(repairLog1);
                    }
                }
            }
        }
        logger.info("----------------------已完工工单定时任务处理结束--------------------------- ");
    }

    // 已完工报修单处理
    public void repairFinish(WorkOrd workOrd){
        if("WOSta_Finish".equals(workOrd.getWorkOrdState())){
            // 根据报修单id查询材料领用明细
            List<Material> list = materialDaoMapper.getList(workOrd.getId());
            List<MaterialApply> materialApplyList = new ArrayList<>();
            for(Material material : list){
                MaterialApply materialApply = new MaterialApply();
                materialApply.setId(GenerateUniqueIdUtil.getGuid());
                materialApply.setRepairId(workOrd.getId());
                materialApply.setRepairNum(workOrd.getWoNo());
                materialApply.setMaterialName(material.getMaterialName());
                materialApply.setPlanNum(material.getPlanNum());
                materialApply.setCreateTime(new Date());
                materialApply.setUpdateTime(new Date());
                materialApply.setDeleteFlag(0);
                materialApplyList.add(materialApply);
            }
            if(!materialApplyList.isEmpty()){
                materialApplyDaoMapper.insertList(materialApplyList);
            }
            // 根据报修单id查询人工费、材料费
            WorkOrd costSum = workOrdDaoMapper.getCostSum(workOrd.getId());
            RepairLog repairLog1 = new RepairLog();
            repairLog1.setRepairNum(workOrd.getWoNo());
            repairLog1.setOrders(workOrd.getOrders());
            repairLog1.setOrdersTime(workOrd.getOrdersTime());
            repairLog1.setCompletionTime(workOrd.getCompletionTime());
            repairLog1.setLabourCost(costSum.getLabourCost());
            repairLog1.setMaterialCost(costSum.getMaterialCost());
            repairLog1.setRepairStatus("WOSta_Finish");
            repairLog1.setUpdateTime(new Date());
            repairLogDaoMapper.update(repairLog1);
        }
    }

}
