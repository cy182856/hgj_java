package com.ej.hgj.sy.dao.workord;

import com.ej.hgj.entity.workord.Material;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MaterialDaoMapper {

    List<Material> getList(String woId);

}
