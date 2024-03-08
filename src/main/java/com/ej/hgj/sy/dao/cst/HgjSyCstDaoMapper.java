package com.ej.hgj.sy.dao.cst;

import com.ej.hgj.entity.cst.HgjCst;
import com.ej.hgj.entity.cst.SyCst;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HgjSyCstDaoMapper {

    HgjCst getCstNameByResId(String resId);

}
