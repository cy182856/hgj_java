package com.ej.hgj.dao.cstInto;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CstIntoCardMapper {

    void deleteCardPerm(String proNum, String tenantWxOpenId);
}
