package com.dongxin.scm.sm.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OptionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String text;

    private String value;
}
