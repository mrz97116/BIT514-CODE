package com.dongxin.scm.test;

import lombok.Data;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author Succy
 * create on 2021/3/19
 */
public class NumberTest {
    @Test
    public void testIsNumber() {
        int a = 23;
        double b = 2.33;


        System.out.println( (Object) b instanceof Number);

        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            Type genericType = field.getGenericType();
            System.out.println(genericType);

            if (genericType instanceof Number) {
                System.out.println("is number");
            }
        }
    }

    @Data
    static class User {
        String name;
        Integer age;
    }
}
