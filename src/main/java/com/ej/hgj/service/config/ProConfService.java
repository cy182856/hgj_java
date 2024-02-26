package com.ej.hgj.service.config;



import com.ej.hgj.entity.config.ProConfig;

import java.util.List;

public interface ProConfService {

    ProConfig getById(String id);

    List<ProConfig> getList(ProConfig proConfig);

    void save(ProConfig proConfig);

    void update(ProConfig proConfig);

    void delete(String id);


}
