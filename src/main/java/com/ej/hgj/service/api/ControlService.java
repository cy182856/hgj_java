package com.ej.hgj.service.api;

import com.ej.hgj.constant.api.AjaxResultApi;
import com.ej.hgj.vo.QrCodeLogReqVo;

public interface ControlService {

    AjaxResultApi saveOpenDoorLog(QrCodeLogReqVo qrCodeLogResVo);

}
