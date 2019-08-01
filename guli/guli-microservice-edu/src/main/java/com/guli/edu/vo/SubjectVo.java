package com.guli.edu.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubjectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
}