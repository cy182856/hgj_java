package com.ej.hgj.service.tag;



import com.ej.hgj.entity.role.Role;
import com.ej.hgj.entity.role.RoleMenu;
import com.ej.hgj.entity.tag.Tag;
import com.ej.hgj.entity.tag.TagCst;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagCstService {

    void saveTagCst(Tag tag);

}
