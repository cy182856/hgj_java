package com.ej.hgj.dao.message;

import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.message.MsgTemplate;
import com.ej.hgj.entity.wechat.WechatPubMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MsgTemplateDaoMapper {

    MsgTemplate getByKey(String templateKey);

    MsgTemplate getByProNumAndKey(String proNum, String templateKey);

    List<MsgTemplate> getList(MsgTemplate msgTemplate);

    void update(MsgTemplate msgTemplate);

}
