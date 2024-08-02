package com.ej.hgj.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 微信公众号
 * @version V1.0
 */
@Data
public class WechatPubVo {

    private String[] id;

    private String count;

    private String total;

    private String nextOpenId;

    private List<String> openIdList;

}
