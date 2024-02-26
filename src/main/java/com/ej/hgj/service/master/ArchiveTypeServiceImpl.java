package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.ArchiveTypeDaoMapper;
import com.ej.hgj.entity.master.ArchiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchiveTypeServiceImpl implements ArchiveTypeService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArchiveTypeDaoMapper archiveTypeDaoMapper;


    public List<ArchiveType> getList(ArchiveType archiveType){
        return archiveTypeDaoMapper.getList(archiveType);
    }

    @Override
    public void save(ArchiveType archiveType) {
        archiveTypeDaoMapper.save(archiveType);
    }

    @Override
    public void update(ArchiveType archiveType) {
        archiveTypeDaoMapper.update(archiveType);
    }

    @Override
    public void delete(String id) {
        archiveTypeDaoMapper.delete(id);
    }

}
