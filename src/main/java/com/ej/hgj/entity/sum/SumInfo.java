package com.ej.hgj.entity.sum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Data
public class SumInfo {

    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fileDate;

    // 公司代码
    private String companyCode;

    // 项目代码
    private String projectCode;

    // 所属部门
    private String affDept;

    // 档案类型代码
    private String archiveTypeCode;

    // 租户楼层
    private String tenantFloor;

    // 租户单元
    private String tenantUnit;

    // 存放柜号
    private String depositCabinetNum;

    // 存放层数
    private String depositNum;

    // 存放盒号
    private String depositBoxNum;

    // 文件数量
    private Integer fileNum;

    // 原件份数
    private Integer scriptNum;

    // 原件页数
    private Integer scriptPage;

    // 复印件份数
    private Integer copyNum;

    // 复印件页数
    private Integer copyPage;

    // 文件密级
    private String fileSecLevel;

    // 存放位置号
    private String positionNum;

    // 文件编号
    private String filesCode;

    // 附件名称
    private String fileName;

    // 合同开始日
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date contractStartDate;

    // 合同结束日
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date contractEndDate;

    // 删除标识
    private Integer deleteFlag;

    private String createBy;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
