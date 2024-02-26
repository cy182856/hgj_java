package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.ArchiveCabinetDaoMapper;
import com.ej.hgj.entity.master.ArchiveCabinet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArchiveCabinetServiceImpl implements ArchiveCabinetService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArchiveCabinetDaoMapper archiveCabinetDaoMapper;


    public List<ArchiveCabinet> getList(ArchiveCabinet archiveCabinet){
        return archiveCabinetDaoMapper.getList(archiveCabinet);
    }

    @Override
    public void save(ArchiveCabinet archiveCabinet) {
        archiveCabinetDaoMapper.save(archiveCabinet);
    }

    @Override
    public void update(ArchiveCabinet archiveCabinet) {
        archiveCabinetDaoMapper.update(archiveCabinet);
    }

    @Override
    public void delete(String id) {
        archiveCabinetDaoMapper.delete(id);
    }

}
