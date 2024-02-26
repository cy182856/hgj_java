package com.ej.hgj.service.cstInto;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.cstInto.CstIntoDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.CstPayPer;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.enums.QrRespEnum;
import com.ej.hgj.enums.QrSceneEnum;
import com.ej.hgj.enums.ScanQrEnum;
import com.ej.hgj.request.GetTempQrcodeRequest;
import com.ej.hgj.request.GetTempQrcodeResult;
import com.ej.hgj.utils.GenerateUniqueIdUtil;
import com.ej.hgj.utils.HttpClientUtil;
import com.ej.hgj.utils.TimestampGenerator;
import com.ej.hgj.utils.wechat.WechatPubNumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CstIntoServiceImpl implements CstIntoService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CstIntoDaoMapper cstIntoDaoMapper;


    @Override
    public void owner(String id) {
        // 查询该租户已绑定的房屋列表
        CstInto cstInto = cstIntoDaoMapper.getById(id);
        // 删除该租户已绑定的房屋
        cstIntoDaoMapper.deleteByCstCodeAndWxOpenId(cstInto.getCstCode(),cstInto.getWxOpenId());
        // 设为业主
        CstInto cInto = new CstInto();
        cInto.setId(TimestampGenerator.generateSerialNumber());
        cInto.setWxOpenId(cstInto.getWxOpenId());
        cInto.setUserName(cstInto.getUserName());
        cInto.setCstCode(cstInto.getCstCode());
        cInto.setIntoRole(Constant.INTO_ROLE_OWNER);
        cInto.setIntoStatus(Constant.INTO_STATUS_Y);
        cInto.setCreateTime(new Date());
        cInto.setUpdateTime(new Date());
        cInto.setDeleteFlag(Constant.DELETE_FLAG_NOT);
        cstIntoDaoMapper.save(cInto);
    }
}
