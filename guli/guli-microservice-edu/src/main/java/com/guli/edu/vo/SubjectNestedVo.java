package com.guli.edu.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SubjectNestedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private List<SubjectVo> children = new ArrayList<>();
}