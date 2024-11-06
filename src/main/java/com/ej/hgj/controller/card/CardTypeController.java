package com.ej.hgj.controller.card;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardDaoMapper;
import com.ej.hgj.dao.card.CardTypeDaoMapper;
import com.ej.hgj.entity.card.Card;
import com.ej.hgj.entity.card.CardType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/cardType")
public class CardTypeController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CardTypeDaoMapper cardTypeDaoMapper;

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(CardType cardType){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        List<CardType> list = cardTypeDaoMapper.getList(cardType);
        map.put("list",list);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }

}
