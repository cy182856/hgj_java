package com.ej.hgj.entity.gonggao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Gonggao {

    private String id;

    private String proNum;

    private String articleId;

    private String mediaId;

    private String type;

    private String typeName;

    private String title;

    private String author;

    private String isDeleted;

    private String url;

    private String thumbUrl;

    private Integer isShow;

    private Integer source;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String content;

}
