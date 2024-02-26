package com.ej.hgj.dao.cst;

import com.ej.hgj.entity.cst.CstPayPer;
import com.ej.hgj.entity.menu.mini.MenuMini;
import com.ej.hgj.entity.role.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CstPayPerDaoMapper {

    void delete(String cstCode);

    void insertList(@Param("list") List<CstPayPer> cstPayPerList);

    List<CstPayPer> findByCstCode(String cstCode);



}
