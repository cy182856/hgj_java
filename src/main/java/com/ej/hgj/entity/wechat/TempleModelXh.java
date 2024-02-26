package com.ej.hgj.entity.wechat;

import lombok.Data;

@Data
public class TempleModelXh extends Model {
    private ModelData first;
    
    private ModelData const2;

    private ModelData time4;

    private ModelData thing3;

    public TempleModelXh() {
    }

    public TempleModelXh(String first) {
        this.first = this.getModelData(first);
    }

    public TempleModelXh(String first, String const2) {
        this(first);
        this.const2 = this.getModelData(const2);
    }

    public TempleModelXh(String first, String const2, String time4) {
        this(first, const2);
        this.time4 = this.getModelData(time4);
    }

    public TempleModelXh(String first, String const2, String time4, String thing3) {
        this(first, const2, time4);
        this.thing3 = this.getModelData(thing3);
    }
    
}
