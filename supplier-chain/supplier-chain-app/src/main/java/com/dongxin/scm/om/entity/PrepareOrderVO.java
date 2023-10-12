package com.dongxin.scm.om.entity;

import lombok.Data;

import java.util.List;

/**
 * @author ：melon
 * @date ：Created in 2021-11-27 21:22
 */
@Data
public class PrepareOrderVO {

    private String customerId;

    List<PrepareOrderDetVO> prepareOrderDetVOList;

}
