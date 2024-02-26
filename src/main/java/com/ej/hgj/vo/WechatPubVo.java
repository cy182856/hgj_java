package com.ej.hgj.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 微信公众号
 * @version V1.0
 */
@Data
public class WechatPubVo {

    private String[] id;

}