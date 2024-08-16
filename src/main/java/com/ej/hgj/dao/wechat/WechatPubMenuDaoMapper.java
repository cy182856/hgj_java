package com.ej.hgj.dao.wechat;

import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.wechat.WechatPubMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface WechatPubMenuDaoMapper {

    WechatPubMenu getById(Integer id);

    List<WechatPubMenu> getList(WechatPubMenu wechatPubMenu);

    void save(WechatPubMenu wechatPubMenu);

    void update(WechatPubMenu wechatPubMenu);

    WechatPubMenu getMaxId();

}
