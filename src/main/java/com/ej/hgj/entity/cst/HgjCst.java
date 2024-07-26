package com.ej.hgj.entity.cst;

import com.ej.hgj.entity.tag.Tag;
import com.ej.hgj.entity.tag.TagCst;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class HgjCst {
    private String id;

    private String orgId;

    private String code;

    private String cstName;

    private String cstType;

    private String cstLevel;

    private String isAffect;

    private String contractCharacter;

    private String cerType;

    private String cerNo;

    private String licNo;

    private String mobile;

    private List<Integer> webMenuIds;

    private List<Integer> weComMenuIds;

    private List<Tag> tagList;

    private List<String> proNumList;

    private List<String> houseList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String projectName;

}
