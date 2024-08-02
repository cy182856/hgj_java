package com.ej.hgj.dao.wechat;

import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.entity.wechat.WechatPub;
import com.ej.hgj.entity.wechat.WechatPubUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface WechatPubUserDaoMapper {

    WechatPubUser getById(String id);

    List<WechatPubUser> getList(WechatPubUser wechatPubUser);

    void update(WechatPubUser wechatPubUser);

    void save(WechatPubUser wechatPubUser);

    void insertList(@Param("list") List<WechatPubUser> wechatPubUserList);

    void deleteByWechatPub(String appId);


}
