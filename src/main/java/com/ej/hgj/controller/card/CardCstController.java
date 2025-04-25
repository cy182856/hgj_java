package com.ej.hgj.controller.card;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardCstBatchDaoMapper;
import com.ej.hgj.dao.card.CardCstBillDaoMapper;
import com.ej.hgj.dao.card.CardCstDaoMapper;
import com.ej.hgj.dao.card.CardDaoMapper;
import com.ej.hgj.dao.coupon.CouponDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantBatchDaoMapper;
import com.ej.hgj.dao.coupon.CouponGrantDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.tag.TagCstDaoMapper;
import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.card.CardCstBatch;
import com.ej.hgj.entity.card.CardCstBill;
import com.ej.hgj.entity.coupon.Coupon;
import com.ej.hgj.entity.coupon.CouponGrant;
import com.ej.hgj.entity.coupon.CouponGrantBatch;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.tag.Tag;
import com.ej.hgj.entity.tag.TagCst;
import com.ej.hgj.service.card.CardService;
import com.ej.hgj.service.coupon.CouponService;
import com.ej.hgj.utils.DateUtils;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.TokenUtils;
import com.ej.hgj.vo.card.CardReqVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/cardCst")
public class CardCstController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TagCstDaoMapper tagCstDaoMapper;

    @Autowired
    private CardDaoMapper cardDaoMapper;

    @Autowired
    private CardCstDaoMapper cardCstDaoMapper;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardCstBatchDaoMapper cardCstBatchDaoMapper;

    @Autowired
    private CardCstBillDaoMapper cardCstBillDaoMapper;

    /**
     * 按标签发卡
     * @param cardCst
     * @return
     */
    @RequestMapping(value = "/sendCardByTag",method = RequestMethod.POST)
    public AjaxResult sendCardByTag(@RequestBody CardCst cardCst){
        Integer cardId = cardCst.getCardId();
        String tagId = cardCst.getTagId();
//        String startTIme = cardCst.getStartTime();
//        String endTime = cardCst.getEndTime();
        TagCst tagCst = new TagCst();
        tagCst.setTagId(tagId);
        // 查询标签下的客户
        List<TagCst> tagCstList = tagCstDaoMapper.getList(tagCst);
        if (!tagCstList.isEmpty()) {
            // 查询卡列表
            List<CardCst> cardCstListAll = cardCstDaoMapper.getList(new CardCst());
            // 查询卡信息
            Card card = cardDaoMapper.getById(cardId);
            List<CardCst> cardCstList = new ArrayList<>();
            for (TagCst tc : tagCstList) {
                CardCst cc = new CardCst();
                cc.setId(TimestampGenerator.generateSerialNumber());
                cc.setProNum(card.getProNum());
                cc.setCardId(cardId);
                cc.setCardCode(DateUtils.strYmd() + card.getType() + tc.getCstCode());
                cc.setCstCode(tc.getCstCode());
//                cc.setTotalNum(cardCst.getTotalNum());
//                cc.setApplyNum(0);
//                cc.setStartTime(startTIme);
//                cc.setEndTime(endTime);
                cc.setIsExp(1);
                cc.setCreateTime(new Date());
                cc.setCreateBy("");
                cc.setUpdateTime(new Date());
                cc.setUpdateBy("");
                cc.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 根据卡ID，客户编号判断该客户是否发过卡，每个客户同一张卡只能发一次
                List<CardCst> cardCstListAllFilter = cardCstListAll.stream().filter(cardCst1 -> cardCst1.getCardId() == cardId && cardCst1.getCstCode().equals(tc.getCstCode())).collect(Collectors.toList());
                if(cardCstListAllFilter.isEmpty()) {
                    cardCstList.add(cc);
                }
            }
            if(!cardCstList.isEmpty()){
                cardCstDaoMapper.insertList(cardCstList);
                logger.info("发卡成功:"+ JSONObject.toJSONString(cardCstList));
            }
        }
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    /**
     * 按客户发卡
     * @param cardCst
     * @return
     */
    @RequestMapping(value = "/sendCardByCst",method = RequestMethod.POST)
    public AjaxResult sendCardByCst(@RequestBody CardCst cardCst){
        Integer cardId = cardCst.getCardId();
//        String startTIme = cardCst.getStartTime();
//        String endTime = cardCst.getEndTime();
        List<String> cstCodeList = cardCst.getCstCodeList();
        if (!cstCodeList.isEmpty()) {
            // 查询卡列表
            List<CardCst> cardCstListAll = cardCstDaoMapper.getList(new CardCst());
            // 查询卡信息
            Card card = cardDaoMapper.getById(cardId);
            List<CardCst> cardCstList = new ArrayList<>();
            for(int i = 0; i<cstCodeList.size(); i++){
                String cstCode = cstCodeList.get(i);
                CardCst cc = new CardCst();
                cc.setId(TimestampGenerator.generateSerialNumber());
                cc.setProNum(card.getProNum());
                cc.setCardId(cardId);
                cc.setCardCode(DateUtils.strYmd() + card.getType() + cstCode);
                cc.setCstCode(cstCode);
//                cc.setTotalNum(cardCst.getTotalNum());
//                cc.setApplyNum(0);
//                cc.setStartTime(startTIme);
//                cc.setEndTime(endTime);
                cc.setIsExp(1);
                cc.setCreateTime(new Date());
                cc.setCreateBy("");
                cc.setUpdateTime(new Date());
                cc.setUpdateBy("");
                cc.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                // 根据卡ID，客户编号判断该客户是否发过卡，每个客户同一张卡只能发一次
                List<CardCst> cardCstListAllFilter = cardCstListAll.stream().filter(cardCst1 -> cardCst1.getCardId() == cardId && cardCst1.getCstCode().equals(cstCode)).collect(Collectors.toList());
                if(cardCstListAllFilter.isEmpty()) {
                    cardCstList.add(cc);
                }
            }
            if(!cardCstList.isEmpty()){
                cardCstDaoMapper.insertList(cardCstList);
                logger.info("发卡成功:"+ JSONObject.toJSONString(cardCstList));
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
    public AjaxResult cardRecharge(HttpServletRequest request, @RequestBody CardCst cardCst){
        String userId = TokenUtils.getUserId(request);
        return cardService.cardRecharge(cardCst, userId);
    }

    /**
     * 扣减
     * @param cardCst
     * @return
     */
    @RequestMapping(value = "/cardDeduct",method = RequestMethod.POST)
    public AjaxResult cardDeduct(HttpServletRequest request, @RequestBody CardCst cardCst){
        String userId = TokenUtils.getUserId(request);
        return cardService.cardDeduct(cardCst, userId);
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
    public AjaxResult disable(HttpServletRequest request, @RequestParam(required=false, value = "id") String id){
        String userId = TokenUtils.getUserId(request);
        AjaxResult ajaxResult = new AjaxResult();
        CardCst cardCst = cardCstDaoMapper.getById(id);
        CardCst cardCstPram = new CardCst();
        cardCstPram.setId(cardCst.getId());
        cardCstPram.setIsExp(0);
        cardCst.setUpdateTime(new Date());
        cardCst.setUpdateBy(userId);
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
    public AjaxResult secure(HttpServletRequest request, @RequestParam(required=false, value = "id") String id){
        String userId = TokenUtils.getUserId(request);
        AjaxResult ajaxResult = new AjaxResult();
        CardCst cardCst = cardCstDaoMapper.getById(id);
        CardCst cardCstPram = new CardCst();
        cardCstPram.setId(cardCst.getId());
        cardCstPram.setIsExp(1);
        cardCst.setUpdateTime(new Date());
        cardCst.setUpdateBy(userId);
        cardCstDaoMapper.update(cardCstPram);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        return ajaxResult;
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public AjaxResult list(@RequestParam(value = "page",defaultValue = "1") int page,
                           @RequestParam(value = "limit",defaultValue = "10") int limit,
                           CardCst cardCst){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        //String startTime = cardCst.getStartTime();
        //String endTime = cardCst.getEndTime();
//        if(StringUtils.isNotBlank(startTime)){
//            cardCst.setStartYear(startTime.substring(0,4));
//            cardCst.setStartYearMonth(startTime.substring(0,7));
//        }
//        if(StringUtils.isNotBlank(endTime)){
//            cardCst.setEndYear(endTime.substring(0,4));
//            cardCst.setEndYearMonth(endTime.substring(0,7));
//        }
        PageHelper.offsetPage((page-1) * limit,limit);
        List<CardCst> list = cardCstDaoMapper.getList(cardCst);
        PageInfo<CardCst> pageInfo = new PageInfo<>(list);
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

    /**
     * 卡批量操作
     * @param cardReqVo
     * @return
     */
    @RequestMapping(value = "/cardBulkOperation",method = RequestMethod.POST)
    public AjaxResult cardBulkOperation(HttpServletRequest request, @RequestBody CardReqVo cardReqVo){
        String userId = TokenUtils.getUserId(request);
       return cardService.cardBulkOperation(cardReqVo, userId);
    }

}
