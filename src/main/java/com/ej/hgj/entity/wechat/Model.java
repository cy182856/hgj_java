package com.ej.hgj.entity.wechat;

import com.ej.hgj.entity.wechat.ModelData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Model {
    public Model() {
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public ModelData getModelData(String value) {
        return new ModelData(value);
    }

    public ModelData getModelData(String value, String color) {
        return new ModelData(value, color);
    }
}
