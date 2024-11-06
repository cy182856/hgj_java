package com.ej.hgj.controller.parkpay;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardCstDaoMapper;
import com.ej.hgj.dao.card.CardDaoMapper;
import com.ej.hgj.dao.parkpay.ParkPayCstHourDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.coupon.Coupon;
import com.ej.hgj.entity.parkpay.ParkPayCstHour;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.TimestampGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/parkPayCstHour")
public class ParkPayCstHourController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @Autowired
    private CardDaoMapper cardDaoMapper;

    @Autowired
    private CardCstDaoMapper cardCstDaoMapper;

    @Autowired
    private ParkPayCstHourDaoMapper parkPayCstHourDaoMapper;

    /**
     * 按标签发时长
     * @return
     */
    @RequestMapping(value = "/sendHourByTag",method = RequestMethod.POST)
    public AjaxResult sendHourByTag(@RequestBody ParkPayCstHour parkPayCstHour){
        String tagId = parkPayCstHour.getTagId();
        String startTIme = parkPayCstHour.getStartTime();
        String endTime = parkPayCstHour.getEndTime();
        TagCst tagCst = new TagCst();
        tagCst.setTagId(tagId);
        // 查询标签下的客户
        List<TagCst> tagCstList = tagCstDaoMapper.getList(tagCst);
        if (!tagCstList.isEmpty()) {
            // 查询时长发放列表
            List<ParkPayCstHour> cstHourListAll = parkPayCstHourDaoMapper.getList(new ParkPayCstHour());
            List<ParkPayCstHour> cstHourList = new ArrayList<>();
            for (TagCst tc : tagCstList) {
                ParkPayCstHour payCstHour = new ParkPayCstHour();
                payCstHour.setId(TimestampGenerator.generateSerialNumber());
                payCstHour.setProNum(parkPayCstHour.getProNum());
                payCstHour.setCstCode(tc.getCstCode());
                payCstHour.setTotalHour(parkPayCstHour.getTotalHour());
                payCstHour.setApplyHour(0);
                payCstHour.setStartTime(startTIme);
                payCstHour.setEndTime(endTime);
                payCstHour.setIsExp(1);
                payCstHour.setCreateTime(new Date());
                payCstHour.setCreateBy("");
                payCstHour.setUpdateTime(new Date());
                payCstHour.setUpdateBy("");
                payCstHour.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 根据客户编号判断该客户是否发过时长，每个客户只能发一次
                List<ParkPayCstHour> cstHourListAllFilter = cstHourListAll.stream().filter(cardCst1 -> cardCst1.getCstCode().equals(tc.getCstCode())).collect(Collectors.toList());
                if(cstHourListAllFilter.isEmpty()) {
                    cstHourList.add(payCstHour);
                }
            }
            if(!cstHourList.isEmpty()){
                parkPayCstHourDaoMapper.insertList(cstHourList);
                logger.info("发放时长成功:"+ JSONObject.toJSONString(cstHourList));
            }
        }
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 按客户发时长
     * @return
     */
    @RequestMapping(value = "/sendHourByCst",method = RequestMethod.POST)
    public AjaxResult sendHourByCst(@RequestBody ParkPayCstHour parkPayCstHour){
        String startTIme = parkPayCstHour.getStartTime();
        String endTime = parkPayCstHour.getEndTime();
        List<String> cstCodeList = parkPayCstHour.getCstCodeList();
        if (!cstCodeList.isEmpty()) {
            // 查询时长发放列表
            List<ParkPayCstHour> cstHourListAll = parkPayCstHourDaoMapper.getList(new ParkPayCstHour());
            List<ParkPayCstHour> cstHourList = new ArrayList<>();
            for(int i = 0; i<cstCodeList.size(); i++){
                String cstCode = cstCodeList.get(i);
                ParkPayCstHour payCstHour = new ParkPayCstHour();
                payCstHour.setId(TimestampGenerator.generateSerialNumber());
                payCstHour.setProNum(parkPayCstHour.getProNum());
                payCstHour.setCstCode(cstCode);
                payCstHour.setTotalHour(parkPayCstHour.getTotalHour());
                payCstHour.setApplyHour(0);
                payCstHour.setStartTime(startTIme);
                payCstHour.setEndTime(endTime);
                payCstHour.setIsExp(1);
                payCstHour.setCreateTime(new Date());
                payCstHour.setCreateBy("");
                payCstHour.setUpdateTime(new Date());
                payCstHour.setUpdateBy("");
                payCstHour.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 根据客户编号判断该客户是否发过卡，每个客户同一张卡只能发一次
                List<ParkPayCstHour> cstHourListAllFilter = cstHourListAll.stream().filter(cardCst1 -> cardCst1.getCstCode().equals(cstCode)).collect(Collectors.toList());
                if(cstHourListAllFilter.isEmpty()) {
                    cstHourList.add(payCstHour);
                }
            }
            if(!cstHourList.isEmpty()){
                parkPayCstHourDaoMapper.insertList(cstHourList);
                logger.info("发放时长成功:"+ JSONObject.toJSONString(cstHourList));
            }
        }
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 按标签批量充值
     * @param cardCst
     * @return
     */
    @RequestMapping(value = "/rechargeByTag",method = RequestMethod.POST)
    public AjaxResult rechargeByTag(@RequestBody CardCst cardCst){
        Integer cardId = cardCst.getCardId();
        String tagId = cardCst.getTagId();
        //Integer rechargeNum = cardCst.getRechargeNum();
        CardCst cardCstPram = new CardCst();
        cardCstPram.setCardId(cardId);
        cardCstPram.setTagId(tagId);
        //cardCstPram.setRechargeNum(rechargeNum);
        cardCstDaoMapper.updateTotalNumByCardIdAndTag(cardCstPram);
        logger.info("批量充值成功:"+ JSONObject.toJSONString(cardCstPram));
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 充值
     * @param cardCst
     * @return
     */
    @RequestMapping(value = "/cardRecharge",method = RequestMethod.POST)
    public AjaxResult cardRecharge(@RequestBody CardCst cardCst){
       String id = cardCst.getId();
       //Integer rechargeNum = cardCst.getRechargeNum();
       CardCst cardCstPram = new CardCst();
       cardCstPram.setId(id);
       //cardCstPram.setRechargeNum(rechargeNum);
       cardCstPram.setUpdateTime(new Date());
       cardCstDaoMapper.update(cardCstPram);
       logger.info("充值成功:"+ JSONObject.toJSONString(cardCstPram));
       AjaxResult ajaxResult = new AjaxResult();
       ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
       ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
       return ajaxResult;
    }

    /**
     * 按标签批量续期
     * @param cardCst
     * @return
     */
    @RequestMapping(value = "/renewalByTag",method = RequestMethod.POST)
    public AjaxResult renewalByTag(@RequestBody CardCst cardCst){
        Integer cardId = cardCst.getCardId();
        String tagId = cardCst.getTagId();
//        String startTime = cardCst.getStartTime();
//        String endTime = cardCst.getEndTime();
        CardCst cardCstPram = new CardCst();
        cardCstPram.setCardId(cardId);
        cardCstPram.setTagId(tagId);
//        cardCstPram.setStartTime(startTime);
//        cardCstPram.setEndTime(endTime);
        cardCstDaoMapper.updateStartEndTimeByCardIdAndTag(cardCstPram);
        logger.info("批量续期成功:"+ JSONObject.toJSONString(cardCstPram));
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 续期
     * @param cardCst
     * @return
     */
    @RequestMapping(value = "/cardRenewal",method = RequestMethod.POST)
    public AjaxResult cardRenewal(@RequestBody CardCst cardCst){
        String id = cardCst.getId();
//        String startTime = cardCst.getStartTime();
//        String endTime = cardCst.getEndTime();
        CardCst cardCstPram = new CardCst();
        cardCstPram.setId(id);
//        cardCstPram.setStartTime(startTime);
//        cardCstPram.setEndTime(endTime);
        cardCstPram.setUpdateTime(new Date());
        cardCstDaoMapper.update(cardCstPram);
        logger.info("充值成功:"+ JSONObject.toJSONString(cardCstPram));
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 禁用
     * @param id
     * @return
     */
    @RequestMapping(value = "/disable",method = RequestMethod.GET)
    public AjaxResult disable(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        CardCst cardCst = cardCstDaoMapper.getById(id);
        CardCst cardCstPram = new CardCst();
        cardCstPram.setId(cardCst.getId());
        cardCstPram.setIsExp(0);
        cardCst.setUpdateTime(new Date());
        cardCstDaoMapper.update(cardCstPram);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 解除
     * @param id
     * @return
     */
    @RequestMapping(value = "/secure",method = RequestMethod.GET)
    public AjaxResult secure(@RequestParam(required=false, value = "id") String id){
        AjaxResult ajaxResult = new AjaxResult();
        CardCst cardCst = cardCstDaoMapper.getById(id);
        CardCst cardCstPram = new CardCst();
        cardCstPram.setId(cardCst.getId());
        cardCstPram.setIsExp(1);
        cardCst.setUpdateTime(new Date());
        cardCstDaoMapper.update(cardCstPram);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           ParkPayCstHour parkPayCstHour){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        PageHelper.offsetPage((page-1) * limit,limit);
        List<ParkPayCstHour> list = parkPayCstHourDaoMapper.getList(parkPayCstHour);
        PageInfo<ParkPayCstHour> pageInfo = new PageInfo<>(list);
        //计算总页数
        int pageNumTotal = (int) Math.ceil((double)pageInfo.getTotal()/(double)limit);
        if(page > pageNumTotal){
            PageInfo<Coupon> entityPageInfo = new PageInfo<>();
            entityPageInfo.setList(new ArrayList<>());
            entityPageInfo.setTotal(pageInfo.getTotal());
            entityPageInfo.setPageNum(page);
            entityPageInfo.setPageSize(limit);
            map.put("pageInfo",entityPageInfo);
        }else {
            map.put("pageInfo",pageInfo);
        }
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

}
