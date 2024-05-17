package com.ej.hgj.dao.feedback;

import com.ej.hgj.entity.feedback.FeedBack;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FeedbackDaoMapper {

    FeedBack getById(String id);

    List<FeedBack> getByList(FeedBack feedBack);

    void save(FeedBack feedBack);

    void update(FeedBack feedBack);

    void delete(String id);

}
