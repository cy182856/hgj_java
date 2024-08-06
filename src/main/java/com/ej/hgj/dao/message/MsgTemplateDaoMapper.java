package com.ej.hgj.dao.message;

import com.ej.hgj.entity.config.ConstantConfig;
import com.ej.hgj.entity.message.MsgTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MsgTemplateDaoMapper {

    MsgTemplate getByKey(String templateKey);

    MsgTemplate getByProNumAndKey(String proNum, String templateKey);

}
