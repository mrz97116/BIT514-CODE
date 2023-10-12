package com.dongxin.scm.utils;

import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.collection.CollUtil.newArrayList;
import static cn.hutool.core.collection.CollUtil.newHashMap;

/**
 * @author ：melon
 * @date ：Created in 2021/3/21 10:14
 */
public class FileReadUtils {

    //效率高
    public static Map<String, String> readTxtFile(String filePath, List<String> ids) {
        Map<String, String> maps = newHashMap();
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF8");
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    if (lineTxt.contains("INSERT")) {
                        for (String id : ids) {
                            if (lineTxt.contains(id)) {
                                String carNo = getCarNo(lineTxt);
                                if (StrUtil.isNotBlank(carNo)) {
                                    maps.put(id, carNo);
                                }
                            }
                        }
                    }
                }
                br.close();
            } else {
                System.out.println("文件不存在!");
            }
        } catch (Exception e) {
            System.out.println("文件读取错误!");
        }

        return maps;
    }


    public static String getCarNo(String str) {


        String[] strArray = str.split(",");
        Pattern compile = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$");

        for (String s : strArray) {
            if (!s.equalsIgnoreCase("null"))
                s = s.replace("'", "").trim();
            Matcher matcher = compile.matcher(s);

            while (matcher.find()) {
                return  matcher.group(0);
            }
        }

        return "";
    }


}
