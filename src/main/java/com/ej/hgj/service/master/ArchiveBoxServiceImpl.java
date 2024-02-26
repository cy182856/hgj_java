package com.ej.hgj.service.master;

import com.ej.hgj.dao.master.ArchiveBoxDaoMapper;
import com.ej.hgj.entity.master.ArchiveBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchiveBoxServiceImpl implements ArchiveBoxService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArchiveBoxDaoMapper archiveBoxDaoMapper;


    public List<ArchiveBox> getList(ArchiveBox archiveBox){
        return archiveBoxDaoMapper.getList(archiveBox);
    }

    @Override
    public void save(ArchiveBox archiveBox) {
        archiveBoxDaoMapper.save(archiveBox);
    }

    @Override
    public void update(ArchiveBox archiveBox) {
        archiveBoxDaoMapper.update(archiveBox);
    }

    @Override
    public void delete(String id) {
        archiveBoxDaoMapper.delete(id);
    }

}
