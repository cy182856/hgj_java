package com.ej.hgj.service.cstInto;

import com.ej.hgj.constant.AjaxResult;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.vo.IntoVo;

import java.util.List;

public interface CstIntoService {

    void owner(String id);

    String saveCstIntoInfo(IntoVo intoVo);

    AjaxResult unbind(AjaxResult ajaxResult, String id, String cstIntoHouseId);
}


