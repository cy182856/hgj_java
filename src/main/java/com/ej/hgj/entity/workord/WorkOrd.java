package com.ej.hgj.entity.workord;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WorkOrd {

    private String id;

    private String woNo;

    private String workOrdState;

    // 人工费
    private BigDecimal labourCost;

    // 材料费
    private BigDecimal materialCost;

    // 接单人
    private String orders;

    // 开工时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ordersTime;

    // 完工时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completionTime;

}
