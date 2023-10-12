package com.dongxin.scm.om.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 常量类
 *
 * @author Shen
 * create on 2020/10/21
 */
public final class Constants {

    public static final String ADD_MAT_LOCK_HEADER = "ADD_MAT";

    public static final String SETTLE_LOCK_HEADER = "SETTLE";

    //客户资金扣款操作
    public static final String CUSTOMER_LOCK_HEADER = "CUSTOMER_";

    public static final String INVOICE_APPLY = "INVOICE_APPLY";

    //客户修改入库单操作
    public static final String WARAHOUSEWARRANT_LOCK_HEADER = "WAREHOUSEWARRANT_";

    //客户修改提货委托单操作
    public static final String GOODENTRUSTMENTLETTER_LOCK_HEADER = "GOODENTRUSTMENTLETTER";

    //批量导入操作加锁
    public static final String ADD_MAT_BATCH_LOCK_HEADER = "ADD_MAT_BATCH";



    public static final List<String> DIRECT_SALE_TENANT_IDS = Stream.of("2", "9", "10", "11", "12").collect(Collectors.toList());

    public static final List<String> YRM_TENANT_IDS = Stream.of("5", "6", "7", "8", "13").collect(Collectors.toList());

}
