package com.ej.hgj.entity.wechat;


import com.ej.hgj.entity.wechat.Model;
import com.ej.hgj.entity.wechat.ModelData;

public class TempleModel extends Model {
    private ModelData first;
    private ModelData keyword1;
    private ModelData keyword2;
    private ModelData keyword3;
    private ModelData keyword4;
    private ModelData keyword5;
    private ModelData keyword6;
    private ModelData keyword7;
    private ModelData remark;

    public TempleModel() {
    }

    public TempleModel(String first) {
        this.first = this.getModelData(first);
    }

    public TempleModel(String first, String keyword1) {
        this(first);
        this.keyword1 = this.getModelData(keyword1);
    }

    public TempleModel(String first, String keyword1, String keyword2) {
        this(first, keyword1);
        this.keyword2 = this.getModelData(keyword2);
    }

    public TempleModel(String first, String keyword1, String keyword2, String keyword3) {
        this(first, keyword1, keyword2);
        this.keyword3 = this.getModelData(keyword3);
    }

    public TempleModel(String first, String keyword1, String keyword2, String keyword3, String keyword4) {
        this(first, keyword1, keyword2, keyword3);
        this.keyword4 = this.getModelData(keyword4);
    }

    public TempleModel(String first, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        this(first, keyword1, keyword2, keyword3, keyword4);
        this.keyword5 = this.getModelData(keyword5);
    }

    public ModelData getFirst() {
        return this.first;
    }

    public void setFirst(ModelData first) {
        this.first = first;
    }

    public ModelData getKeyword1() {
        return this.keyword1;
    }

    public void setKeyword1(ModelData keyword1) {
        this.keyword1 = keyword1;
    }

    public ModelData getKeyword2() {
        return this.keyword2;
    }

    public ModelData getRemark() {
        return this.remark;
    }

    public void setRemark(ModelData remark) {
        this.remark = remark;
    }

    public void setKeyword2(ModelData keyword2) {
        this.keyword2 = keyword2;
    }

    public ModelData getKeyword3() {
        return this.keyword3;
    }

    public void setKeyword3(ModelData keyword3) {
        this.keyword3 = keyword3;
    }

    public ModelData getKeyword4() {
        return this.keyword4;
    }

    public void setKeyword4(ModelData keyword4) {
        this.keyword4 = keyword4;
    }

    public ModelData getKeyword5() {
        return this.keyword5;
    }

    public void setKeyword5(ModelData keyword5) {
        this.keyword5 = keyword5;
    }

    public ModelData getKeyword6() {
        return this.keyword6;
    }

    public void setKeyword6(ModelData keyword6) {
        this.keyword6 = keyword6;
    }

    public ModelData getKeyword7() {
        return this.keyword7;
    }

    public void setKeyword7(ModelData keyword7) {
        this.keyword7 = keyword7;
    }
}
