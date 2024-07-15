package com.ej.hgj.dao.adverts;

import com.ej.hgj.entity.adverts.Adverts;
import com.ej.hgj.entity.gonggao.Gonggao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface AdvertsDaoMapper {

    Adverts getById(String id);

    List<Adverts> getList(Adverts adverts);

    void save(Adverts adverts);

    void update(Adverts adverts);

    void delete(String id);

    void isShow(String id);

    void notIsShow(String id);

    void notIsShowByProNum(String proNum);

    void insertList(@Param("list") List<Adverts> advertsList);


}
