package com.ej.hgj.dao.moncarren;

import com.ej.hgj.entity.cstInto.CstInto;
import com.ej.hgj.entity.moncarren.MonCarRenOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MonCarRenOrderDaoMapper {

    MonCarRenOrder getById(String id);

    List<MonCarRenOrder> getList(MonCarRenOrder monCarRenOrder);

    void deleteByOrderStatusAndCreateTime();

}
