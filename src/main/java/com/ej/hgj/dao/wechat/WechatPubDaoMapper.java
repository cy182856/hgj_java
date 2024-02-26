package com.ej.hgj.dao.wechat;

import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.wechat.WechatPub;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface WechatPubDaoMapper {

    WechatPub getById(String id);

    List<WechatPub> getList(WechatPub wechatPub);

}
