package com.ej.hgj.entity.wechat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 微信菜单
 * @version V1.0
 */
@Data
public class WechatPubMenu {

    private Integer id;

    private Integer wechatPubId;

    private Integer parentId;

    /**
     * 一级菜单数组，个数应为1~3个
     */
    private List<WechatPubMenu> button;

    /**
     * 二级菜单数组，个数应为1~5个
     */
    private List<WechatPubMenu> sub_button;

    /**
     * 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
     */
    private String type;

    /**
     * 菜单标题，不超过16个字节，子菜单不超过60个字节
     */
    private String name;

    /**
     * click等点击类型必须
     * 菜单KEY值，用于消息接口推送，不超过128字节;
     */
    private String key;

    /**
     * view、miniprogram类型必须;
     * 网页链接，用户点击菜单可打开链接，不超过1024字节。type为miniprogram时，不支持小程序的老版本客户端将打开本url。
     */
    private String url;

    /**
     * media_id类型和view_limited类型必须
     * 调用新增永久素材接口返回的合法media_id
     */
    private String media_id;

    /**
     * miniprogram类型必须
     * 小程序的appid（仅认证公众号可配置）
     */
    private String appid;

    /**
     * miniprogram类型必须;
     * 小程序的页面路径
     */
    private String pagepath;

    private Integer sort;

    private String createBy;

    private String updateBy;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}