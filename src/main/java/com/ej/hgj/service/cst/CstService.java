package com.ej.hgj.service.cst;

import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;

import java.text.ParseException;
import java.util.List;

public interface CstService {

    List<HgjCst> getList(HgjCst hgjCst);

    GetTempQrcodeResult qrcode(GetTempQrcodeRequest getTempQrcodeRequest);

    GetTempQrcodeResult cstIntoQrcode(GetTempQrcodeRequest getTempQrcodeRequest);

    GetTempQrcodeResult cstIntoStaffQrcode(GetTempQrcodeRequest getTempQrcodeRequest);

    void saveCstMenu(HgjCst hgjCst);


}
