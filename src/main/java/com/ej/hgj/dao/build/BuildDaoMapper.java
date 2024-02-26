package com.ej.hgj.dao.build;

import com.ej.hgj.entity.build.Build;
import com.ej.hgj.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface BuildDaoMapper {

    List<Build> getList(Build build);

    List<Build> getListAll(Build build);


}
