package com.ej.hgj.entity.user;

import com.ej.hgj.entity.build.Build;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class User {
    private String id;

    private String corpId;

    private String corpName;

    private String projectNum;

    private String projectName;

    private String staffId;

    private String userId;

    private String userName;

    private String qrCode;

    private String roleId;

    private String roleName;

    private String password;

    private String alias;

    private String deptName;

    private String position;

    private String mobile;

    // 值班电话
    private String phone;

    private String bizMail;

    private String avatar;

    private String thumbAvatar;

    private Integer gender;

    private Integer status;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 房屋id
    private String[] budId;

    // 楼栋绑定列表
    private List<Build> buildList;

    // 旧密码
    private String oldPassword;

    // 新密码
    private String newPassword;

}
