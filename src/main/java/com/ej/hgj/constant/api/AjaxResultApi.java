package com.ej.hgj.constant.api;

import com.ej.hgj.entity.menu.Menu;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class AjaxResultApi {

    private Integer resCode;

    private String resMsg;

    private HashMap data;
}
