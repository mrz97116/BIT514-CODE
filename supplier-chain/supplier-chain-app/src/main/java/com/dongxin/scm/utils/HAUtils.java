package com.dongxin.scm.utils;


import java.util.ArrayList;
import java.util.List;

/**
 * @author ：melon
 * @date ：Created in 2020/11/2 20:18
 */
public class HAUtils {
    public static <T> List<T> objToList(Object obj, Class<T> cla) {
        List<T> list = new ArrayList<T>();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (List<?>) obj) {
                list.add(cla.cast(o));
            }
            return list;
        }
        return null;
    }
}
