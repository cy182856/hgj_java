package com.ej.hgj.task.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.material.MaterialApplyDaoMapper;
import com.ej.hgj.dao.message.MsgTemplateDaoMapper;
import com.ej.hgj.dao.repair.RepairLogDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubUserDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.message.MsgTemplate;
import com.ej.hgj.entity.repair.RepairLog;
import com.ej.hgj.entity.wechat.Miniprogram;
import com.ej.hgj.entity.wechat.TempleMessage;
import com.ej.hgj.entity.wechat.WechatPub;
import com.ej.hgj.entity.wechat.WechatPubUser;
import com.ej.hgj.entity.workord.Material;
import com.ej.hgj.entity.workord.MaterialApply;
import com.ej.hgj.entity.workord.ReturnVisit;
import com.ej.hgj.entity.workord.WorkOrd;
import com.ej.hgj.sy.dao.workord.MaterialDaoMapper;
import com.ej.hgj.sy.dao.workord.ReturnVisitDaoMapper;
import com.ej.hgj.sy.dao.workord.WorkOrdDaoMapper;
import com.ej.hgj.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Autowired
    private WechatPubDaoMapper wechatPubDaoMapper;

    @Autowired
    private MsgTemplateDaoMapper msgTemplateDaoMapper;

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;

    @Autowired
    private WechatPubUserDaoMapper wechatPubUserDaoMapper;

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

                        // 处理中工单发送模板消息
                        try {
                            // 查询报修信息
                            String repairNum = workOrd.getWoNo();
                            RepairLog findByRepNum = repairLogDaoMapper.getByRepNum(repairNum);
                            // 根据项目号查询公众号
                            WechatPub wechatPub = wechatPubDaoMapper.getByProNum(findByRepNum.getProjectNum());
                            String accessToken = JSONUtil.parseObj(HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+wechatPub.getAppId()+"&secret=" + wechatPub.getAppSecret())).get("access_token").toString();
                            repairWostaProcSendTempMsg(findByRepNum.getProjectNum(),findByRepNum.getWxOpenId(), accessToken,wechatPub.getProName(), repairNum);
                            logger.info("--处理中工单模板消息发送成功--||" + "项目：" + findByRepNum.getProjectName() + "||客户：" + findByRepNum.getCstName() + "||报修单号：" + repairNum);
                        }catch (Exception e){
                            logger.info("--处理中工单模板消息发送失败--" + e);
                        }

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
            if(costSum != null) {
                repairLog1.setLabourCost(costSum.getLabourCost());
                repairLog1.setMaterialCost(costSum.getMaterialCost());
            }else {
                repairLog1.setLabourCost(BigDecimal.ZERO);
                repairLog1.setMaterialCost(BigDecimal.ZERO);
            }
            repairLog1.setRepairStatus("WOSta_Finish");
            repairLog1.setUpdateTime(new Date());
            repairLogDaoMapper.update(repairLog1);

            // 已完工工单发送模板消息
            try {
                // 查询报修信息
                String repairNum = workOrd.getWoNo();
                RepairLog findByRepNum = repairLogDaoMapper.getByRepNum(repairNum);
                // 根据项目号查询公众号
                WechatPub wechatPub = wechatPubDaoMapper.getByProNum(findByRepNum.getProjectNum());
                String accessToken = JSONUtil.parseObj(HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+wechatPub.getAppId()+"&secret=" + wechatPub.getAppSecret())).get("access_token").toString();
                repairWostaFinishSendTempMsg(findByRepNum.getProjectNum(),findByRepNum.getWxOpenId(), accessToken, repairNum);
                logger.info("--已完工工单模板消息发送成功--||" + "项目：" + findByRepNum.getProjectName() + "||客户：" + findByRepNum.getCstName() + "||报修单号：" + repairNum);
            }catch (Exception e){
                logger.info("--已完工工单模板消息发送失败--" + e);
            }
        }
    }

    @Transactional
    public void repairWoStaFinishMsgUpdate() {
        logger.info("----------------------已完工工单自动评论定时任务处理开始--------------------------- ");
        // 查询惠管家前7天已完工工单
        List<RepairLog> repairLogList = repairLogDaoMapper.getListByStatusAndTime(new RepairLog());
        for(RepairLog repair : repairLogList){
            // 根据单号查询报修单ID
            WorkOrd workOrd = workOrdDaoMapper.getCsWorkOrd(repair.getRepairNum(),"WOSta_Finish");
            if(workOrd != null){
                // 根据报修单id查询思源已回访记录
                List<ReturnVisit> list = returnVisitDaoMapper.getList(workOrd.getId());
                // 未回访,调用思源回访接口，更新报修单状态
                if(list.isEmpty()){
                    // 获取思源接口地址
                    ConstantConfig constantConfig = constantConfDaoMapper.getByKey("sy_url");
                    // 获取token
                    String token = SyPostClient.getToken(constantConfig.getConfigValue());
                    String p7 = initReturnVisit(workOrd.getId(), "100", "100", "默认好评");
                    // 获取请求结果, 调用思源接口 94-客服回访接口，修改工单状态为已回访
                    JSONObject jsonObject = SyPostClient.userRev2ServiceCustomerServiceReturn("UserRev2_Service_CustomerServiceReturn", p7, token, constantConfig.getConfigValue());
                    String status = jsonObject.getString("Status");
                    if ("1".equals(status)) {
                        RepairLog repairLog = new RepairLog();
                        repairLog.setRepairNum(repair.getRepairNum());
                        repairLog.setRepairScore("5");
                        repairLog.setRepairMsg("默认好评");
                        repairLog.setSatisFaction("0");
                        repairLog.setRepairStatus("WOSta_Visit");
                        repairLogDaoMapper.update(repairLog);
                    }
                }
            }

        }
        logger.info("----------------------已完工工单自动评论定时任务处理结束--------------------------- ");
    }

    // 组装参数-客服回访
    public String initReturnVisit(String repairId, String satisFaction, String totalScore, String desc){
        String WOID = "{ \"WOID\":\"" + repairId + "\",";
        String ReturnVisitWay = "\"ReturnVisitWay\":\"Tel\",";
        String ReturnVisitDate = "\"ReturnVisitDate\":\"" + DateUtils.strYmdHms() + "\",";
        String Object = "\"Object\":\"17082215304300020066\",";
        String ObjectName = "\"ObjectName\":\"KF01\",";
        String SatisfiedVisit = "\"SatisfiedVisit\":\"" + satisFaction + "\",";
        //String FailureCause = "\"FailureCause\":\"很满意\",";
        String Remark = "\"Remark\":\"" + desc + "\",";
        String TotalScore = "\"TotalScore\":\"" + totalScore + "\",";
        String VisitState = "\"VisitState\":\"1\",";
        String UserId = "\"UserId\":\"Sam\",";
        UserId += "}";
        return WOID + ReturnVisitWay + ReturnVisitDate + Object + ObjectName + SatisfiedVisit + Remark + TotalScore + VisitState + UserId;
    }

    public void repairWostaProcSendTempMsg(String proNum, String wxOpenId, String accessToken, String proName, String repairNum) throws Exception {
        // 查询小程序配置
        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.MINI_PROGRAM_APP_EJ_ZHSQ);
        Miniprogram miniprogram = new Miniprogram(constantConfig.getAppId(), "subpages/repair/pages/repairQuery/repairQuery");
        // 查询消息模板
        MsgTemplate msgTemplate = msgTemplateDaoMapper.getByProNumAndKey(proNum, Constant.REPAIR_WOSTA_PROC_TEMPLATE);
        if(msgTemplate != null) {
            // 根据项目号，微信号查询已入住客户
            CstInto cstInto = new CstInto();
            cstInto.setProjectNum(proNum);
            cstInto.setWxOpenId(wxOpenId);
            cstInto.setIntoStatus(Constant.INTO_STATUS_Y);
            List<CstInto> cstIntoList = cstIntoDaoMapper.getList(cstInto);
            // 根据入住用户unionId获取公众号openId
            if (!cstIntoList.isEmpty()) {
                List<String> unionIdList = new ArrayList<>();
                for (CstInto cst : cstIntoList) {
                    if (StringUtils.isNotBlank(cst.getUnionId()))
                        unionIdList.add(cst.getUnionId());
                }
                // 根据项目号，unionids集合查询公众号用户
                if (!unionIdList.isEmpty()) {
                    WechatPubUser wechatPubUser = new WechatPubUser();
                    wechatPubUser.setProNum(proNum);
                    wechatPubUser.setUnionIdList(unionIdList);
                    List<WechatPubUser> wechatPubUserList = wechatPubUserDaoMapper.getList(wechatPubUser);
                    if (!wechatPubUserList.isEmpty()) {
                        for (WechatPubUser wp : wechatPubUserList) {
                            TempleMessage modelMessage = new TempleMessage();
                            modelMessage.setTouser(wp.getOpenid());
                            modelMessage.setTemplate_id(msgTemplate.getTemplateId());
                            modelMessage.setData("dataParam");
                            modelMessage.setMiniprogram(miniprogram);
                            String jsonMenu = JsonUtils.writeEntiry2JSON(modelMessage);
                            String data = msgTemplate.getTemplateData();
                            data = data.replace("param1", DateUtils.strYmdHms()).replace("param2", repairNum).replace("param3", proName);
                            jsonMenu = jsonMenu.replace("\"dataParam\"", data);
                            newSendModel(jsonMenu, accessToken);
                        }
                    }
                }
            }
        }
    }

    public void repairWostaFinishSendTempMsg(String proNum, String wxOpenId, String accessToken, String repairNum) throws Exception {
        // 查询小程序配置
        ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.MINI_PROGRAM_APP_EJ_ZHSQ);
        Miniprogram miniprogram = new Miniprogram(constantConfig.getAppId(), "subpages/repair/pages/repairQuery/repairQuery");
        // 查询消息模板
        MsgTemplate msgTemplate = msgTemplateDaoMapper.getByProNumAndKey(proNum, Constant.REPAIR_WOSTA_FINISH_TEMPLATE);
        if(msgTemplate != null) {
            // 根据项目号，微信号查询已入住客户
            CstInto cstInto = new CstInto();
            cstInto.setProjectNum(proNum);
            cstInto.setWxOpenId(wxOpenId);
            cstInto.setIntoStatus(Constant.INTO_STATUS_Y);
            List<CstInto> cstIntoList = cstIntoDaoMapper.getList(cstInto);
            // 根据入住用户unionId获取公众号openId
            if (!cstIntoList.isEmpty()) {
                List<String> unionIdList = new ArrayList<>();
                for (CstInto cst : cstIntoList) {
                    if (StringUtils.isNotBlank(cst.getUnionId()))
                        unionIdList.add(cst.getUnionId());
                }
                // 根据项目号，unionids集合查询公众号用户
                if (!unionIdList.isEmpty()) {
                    WechatPubUser wechatPubUser = new WechatPubUser();
                    wechatPubUser.setProNum(proNum);
                    wechatPubUser.setUnionIdList(unionIdList);
                    List<WechatPubUser> wechatPubUserList = wechatPubUserDaoMapper.getList(wechatPubUser);
                    if (!wechatPubUserList.isEmpty()) {
                        for (WechatPubUser wp : wechatPubUserList) {
                            TempleMessage modelMessage = new TempleMessage();
                            modelMessage.setTouser(wp.getOpenid());
                            modelMessage.setTemplate_id(msgTemplate.getTemplateId());
                            modelMessage.setData("dataParam");
                            modelMessage.setMiniprogram(miniprogram);
                            String jsonMenu = JsonUtils.writeEntiry2JSON(modelMessage);
                            String data = msgTemplate.getTemplateData();
                            data = data.replace("param1", repairNum).replace("param2", DateUtils.strYmdHms());
                            jsonMenu = jsonMenu.replace("\"dataParam\"", data);
                            newSendModel(jsonMenu, accessToken);
                        }
                    }
                }
            }
        }
    }

    public static int newSendModel(String jsonMenu, String accessToken) {
        int result = -1;
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN".replace("ACCESS_TOKEN", accessToken);
        jsonMenu = jsonMenu.replace("date", "Date").replace("money", "Money");
        //}
        JSONObject jsonObject = HttpClientUtil.sendPost(url, jsonMenu);
        if (null != jsonObject) {
            int errorCode = ((Integer) jsonObject.get("errcode"));
            if (0 != errorCode) {
                result = errorCode;
            } else {
                result = 0;
            }
        }

        return result;
    }
}
