package com.ej.hgj.controller.card;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.card.CardCstBatchDaoMapper;
import com.ej.hgj.dao.opendoor.OpenDoorLogDaoMapper;
import com.ej.hgj.entity.card.CardCstBatch;
import com.ej.hgj.entity.opendoor.OpenDoorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@CrossOrigin
@RestController
@RequestMapping("/cardCstBatch")
public class CardCstBatchController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CardCstBatchDaoMapper cardCstBatchDaoMapper;

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult select(@RequestParam(required=false, value = "cardCode") String cardCode){
        AjaxResult ajaxResult = new AjaxResult();
        HashMap map = new HashMap();
        CardCstBatch cardCstBatch = new CardCstBatch();
        cardCstBatch.setCardCode(cardCode);
        List<CardCstBatch> cardCstBatchList = cardCstBatchDaoMapper.getList(cardCstBatch);
        map.put("cardCstBatchList",cardCstBatchList);
        ajaxResult.setCode(Constant.SUCCESS_RESULT_CODE);
        ajaxResult.setMessage(Constant.SUCCESS_RESULT_MESSAGE);
        ajaxResult.setData(map);
        return ajaxResult;
    }
}
