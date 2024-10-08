package com.ej.hgj.entity.tag;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class Tag {
    private String id;

    private String proNum;

    private String proName;

    // 0-系统标签 1-自建标签
    private Integer type;

    private String name;

    // 选择范围：1-客户  2-个人
    private Integer range;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private List<String> cstCodes;

    private List<String> wxOpenIds;

    private String cstCode;

}
