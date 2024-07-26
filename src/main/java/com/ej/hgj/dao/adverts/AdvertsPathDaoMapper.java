package com.ej.hgj.dao.adverts;

import com.ej.hgj.entity.adverts.Adverts;
import com.ej.hgj.entity.adverts.AdvertsPath;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface AdvertsPathDaoMapper {

    Adverts getById(String id);

    List<AdvertsPath> getList(AdvertsPath advertsPath);

    void save(AdvertsPath advertsPath);

    void update(AdvertsPath advertsPath);

    void delete(String id);

    void insertList(@Param("list") List<AdvertsPath> advertsPathList);


}
