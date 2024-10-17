package com.ej.hgj.service.qn;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.config.ConstantConfDaoMapper;
import com.ej.hgj.dao.cst.CstPayPerDaoMapper;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.menu.mini.MenuMiniDaoMapper;
import com.ej.hgj.dao.qn.QnDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubDaoMapper;
import com.ej.hgj.dao.wechat.WechatPubMenuDaoMapper;
import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.qn.Qn;
import com.ej.hgj.entity.wechat.WechatPub;
import com.ej.hgj.entity.wechat.WechatPubMenu;
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
public class QnServiceImpl implements QnService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private QnDaoMapper qnDaoMapper;

    @Autowired
    private WechatPubMenuDaoMapper wechatPubMenuDaoMapper;

    @Autowired
    private WechatPubDaoMapper wechatPubDaoMapper;

    @Autowired
    private ConstantConfDaoMapper constantConfDaoMapper;

    @Override
    public void save(Qn qn, String userId) {
        if(qn.getId() != null){
            qn.setUpdateBy(userId);
            qn.setUpdateTime(new Date());
            if(StringUtils.isBlank(qn.getTagId())){
                qn.setTagId("0");
            }
            qnDaoMapper.update(qn);
        }else{

            // 将问卷保存到公众号菜单
//            WechatPubMenu wechatPubMenu = new WechatPubMenu();
//
//            WechatPubMenu wpm = wechatPubMenuDaoMapper.getMaxId();
//            Integer maxId = wpm.getId();
//            wechatPubMenu.setId(maxId + 1);
//
//            WechatPub wechatPub = wechatPubDaoMapper.getByProNum(qn.getProNum());
//            wechatPubMenu.setWechatPubId(wechatPub.getId());
//
//            ConstantConfig constantConfig = constantConfDaoMapper.getByKey(Constant.WECHAT_PUB_MENU_PARENT_ID);
//            wechatPubMenu.setParentId(Integer.valueOf(constantConfig.getConfigValue()));
//            wechatPubMenu.setType("view");
//            wechatPubMenu.setName(qn.getTitle());
//            wechatPubMenu.setUrl(qn.getUrl());
//            wechatPubMenu.setSort(wechatPubMenu.getId());
//            wechatPubMenu.setCreateTime(new Date());
//            wechatPubMenu.setUpdateTime(new Date());
//            wechatPubMenu.setCreateBy(userId);
//            wechatPubMenu.setUpdateBy(userId);
//            wechatPubMenu.setDeleteFlag(Constant.DELETE_FLAG_YES);
//            wechatPubMenuDaoMapper.save(wechatPubMenu);

            qn.setId(TimestampGenerator.generateSerialNumber());
            //qn.setPubMenuId(wechatPubMenu.getId());
            qn.setMiniIsShow(0);
            qn.setPubMenuIsShow(0);
            if(StringUtils.isBlank(qn.getTagId())){
                qn.setTagId("0");
            }
            qn.setUpdateTime(new Date());
            qn.setCreateTime(new Date());
            qn.setCreateBy(userId);
            qn.setUpdateBy(userId);
            qn.setDeleteFlag(Constant.DELETE_FLAG_NOT);
            qnDaoMapper.save(qn);
        }
    }

//    @Override
//    public void pubMenuIsShow(String id, String userId) {
//        Qn qn = qnDaoMapper.getById(id);
//        WechatPubMenu wechatPubMenu = wechatPubMenuDaoMapper.getById(qn.getPubMenuId());
//        // 更新问卷公众号状态 显示
//        qnDaoMapper.pubMenuIsShow(id);
//        // 更新公众号菜单 状态为有效
//        wechatPubMenu.setDeleteFlag(Constant.DELETE_FLAG_NOT);
//        wechatPubMenu.setUpdateTime(new Date());
//        wechatPubMenu.setUpdateBy(userId);
//        wechatPubMenuDaoMapper.update(wechatPubMenu);
//    }
//
//    @Override
//    public void notPubMenuIsShow(String id, String userId) {
//        Qn qn = qnDaoMapper.getById(id);
//        WechatPubMenu wechatPubMenu = wechatPubMenuDaoMapper.getById(qn.getPubMenuId());
//        // 更新问卷公众号状态 不显示
//        qnDaoMapper.notPubMenuIsShow(id);
//        // 更新公众号菜单 状态为无效
//        wechatPubMenu.setDeleteFlag(Constant.DELETE_FLAG_YES);
//        wechatPubMenu.setUpdateTime(new Date());
//        wechatPubMenu.setUpdateBy(userId);
//        wechatPubMenuDaoMapper.update(wechatPubMenu);
//    }
}
