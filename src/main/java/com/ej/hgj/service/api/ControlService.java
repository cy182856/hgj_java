package com.ej.hgj.service.api;

import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.vo.QrCodeLogResVo;

import java.util.List;

public interface ControlService {

    AjaxResultApi saveOpenDoorLog(QrCodeLogResVo qrCodeLogResVo);

}
