package com.ej.hgj.service.house;


import com.ej.hgj.entity.house.HgjHouse;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;

import java.util.List;

public interface HouseService {

    List<HgjHouse> getList(HgjHouse hgjHouse);

    GetTempQrcodeResult qrcode(GetTempQrcodeRequest getTempQrcodeRequest);

}
