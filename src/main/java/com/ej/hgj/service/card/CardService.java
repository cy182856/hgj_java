package com.ej.hgj.service.card;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.entity.card.CardCst;
import com.ej.hgj.entity.qn.Qn;
import com.ej.hgj.vo.card.CardReqVo;

public interface CardService {

    AjaxResult cardBulkOperation(CardReqVo cardReqVo, String userId);

    AjaxResult cardRecharge(CardCst cardCst, String userId);

    AjaxResult cardDeduct(CardCst cardCst, String userId);

}
