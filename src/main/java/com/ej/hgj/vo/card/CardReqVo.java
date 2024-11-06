package com.ej.hgj.vo.card;

import lombok.Data;

import java.util.List;

@Data
public class CardReqVo {

   private Integer cardId;

   // 卡操作 1-发卡 2-禁用 3-恢复
   private Integer cardOption;

   // 次数/小时
   private Integer totalNum;

   // 有效年份/月份
   private String expDate;

   private List<String> cstCodeList;
}
