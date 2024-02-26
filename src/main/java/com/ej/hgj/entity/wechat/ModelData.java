package com.ej.hgj.entity.wechat;

public class ModelData {
    private String value;
    private String color;

    public ModelData() {
    }

    public ModelData(String value) {
        this.value = value;
    }

    public ModelData(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
