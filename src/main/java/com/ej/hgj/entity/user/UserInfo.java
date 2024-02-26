package com.ej.hgj.entity.user;

import lombok.Data;

@Data
public class UserInfo {
    private String[] roles;
    private String introduction;
    private String avatar;
    private String name;
}
