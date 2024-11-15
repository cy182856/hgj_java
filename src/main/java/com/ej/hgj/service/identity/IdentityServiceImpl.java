package com.ej.hgj.service.identity;

import com.alibaba.fastjson.JSONObject;
import com.ej.hgj.constant.Constant;
import com.ej.hgj.dao.cst.HgjCstDaoMapper;
import com.ej.hgj.dao.identity.IdentityMenuDaoMapper;
import com.ej.hgj.dao.menu.MenuDaoMapper;
import com.ej.hgj.dao.menu.cstmenumini.CstMenuMiniDaoMapper;
import com.ej.hgj.entity.cst.CstMenu;
import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.identity.IdentityMenu;
import com.ej.hgj.entity.menu.Menu;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.service.menu.MenuService;
import com.ej.hgj.utils.TimestampGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IdentityServiceImpl implements IdentityService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdentityMenuDaoMapper identityMenuDaoMapper;

    @Override
    public void saveIdentityMenu(IdentityMenu identityMenu) {
        Integer menuId = identityMenu.getMenuId();
        String[] proNums = identityMenu.getProNums();
        Integer[] identityCodes = identityMenu.getIdentityCodes();
        identityMenuDaoMapper.delete(menuId);
        logger.info("删除身份小程序菜单成功menuId:" + menuId);
        List<IdentityMenu> identityMenuList = new ArrayList<>();
        if(proNums != null && identityCodes != null){
            for(int i = 0; i<proNums.length; i++){
                for(int j=0; j<identityCodes.length; j++ ){
                    IdentityMenu im = new IdentityMenu();
                    im.setId(TimestampGenerator.generateSerialNumber());
                    im.setProNum(proNums[i]);
                    im.setIdentityCode(identityCodes[j]);
                    im.setMenuId(menuId);
                    im.setCreateBy("");
                    im.setCreateTime(new Date());
                    im.setUpdateBy("");
                    im.setUpdateTime(new Date());
                    im.setDeleteFlag(Constant.DELETE_FLAG_NOT);
                    identityMenuList.add(im);
                }
            }
        }
        // 新增身份菜单
        if(!identityMenuList.isEmpty()){
            identityMenuDaoMapper.insertList(identityMenuList);
            logger.info("新增身份菜单成功:"+ JSONObject.toJSONString(identityMenuList));
        }
    }

}
