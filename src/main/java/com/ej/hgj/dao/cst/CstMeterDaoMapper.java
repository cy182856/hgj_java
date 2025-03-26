package com.ej.hgj.dao.cst;

import com.ej.hgj.entity.cst.CstMeter;
import com.ej.hgj.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CstMeterDaoMapper {

    CstMeter getById(String id);

    CstMeter getByCstCodeAndUserId(String cstCode, String userId);

    List<CstMeter> getList(CstMeter cstMeter);

    void update(CstMeter cstMeter);

    void save(CstMeter cstMeter);

    void delete(String id);

}
