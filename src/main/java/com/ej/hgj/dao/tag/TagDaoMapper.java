package com.ej.hgj.dao.tag;

import com.ej.hgj.entity.gonggao.GonggaoType;
import com.ej.hgj.entity.tag.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface TagDaoMapper {

    Tag getById(String id);

    List<Tag> getList(Tag tag);

    List<Tag> getByName(String name);

    List<Tag> getCstTag(Tag tag);

    void save(Tag tag);

    void update(Tag tag);

    void delete(String id);

}
