package com.dongxin.scm.utils;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author ：melon
 * @date ：Created in 2021-12-3 16:18
 */
public class ScmStrUtils {

    //从list中找出存在重复的字符串
    public static List<String> getDuplicateElements(List<String> list) {
        return list.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum)) //获得元素出现频率的 Map，键为元素， 值为元素出现的次数
                .entrySet()
                .stream()                              // 所有 entry 对应的 Stream
                .filter(e -> e.getValue() > 1)         // 过滤出元素出现次数大于 1 (重复元素）的 entry
                .map(Map.Entry::getKey)                // 获得 entry 的键（重复元素）对应的 Stream
                .collect(Collectors.toList());         // 转化为 List
    }


}
