package com.ej.hgj.service.qn;

import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.qn.Qn;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;

import java.util.List;

public interface QnService {

    void save(Qn qn, String userId);

    //void pubMenuIsShow(String id, String userId);

    //void notPubMenuIsShow(String id, String userId);


}
