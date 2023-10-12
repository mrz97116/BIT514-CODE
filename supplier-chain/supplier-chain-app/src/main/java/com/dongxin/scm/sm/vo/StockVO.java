package com.dongxin.scm.sm.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StockVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> ids;
}
