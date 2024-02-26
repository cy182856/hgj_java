package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.ArchiveStoreyDaoMapper;
import com.ej.hgj.entity.master.ArchiveStorey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchiveStoreyServiceImpl implements ArchiveStoreyService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArchiveStoreyDaoMapper archiveStoreyDaoMapper;


    public List<ArchiveStorey> getList(ArchiveStorey archiveStorey){
        return archiveStoreyDaoMapper.getList(archiveStorey);
    }

    @Override
    public void save(ArchiveStorey archiveStorey) {
        archiveStoreyDaoMapper.save(archiveStorey);
    }

    @Override
    public void update(ArchiveStorey archiveStorey) {
        archiveStoreyDaoMapper.update(archiveStorey);
    }

    @Override
    public void delete(String id) {
        archiveStoreyDaoMapper.delete(id);
    }

}
