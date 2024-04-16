package com.ej.hgj.entity.tag;

import lombok.Data;

import java.util.List;

@Data
public class TwoChildren {

    private String id;

    private String label;

    private List<ThreeChildren> children;

}
