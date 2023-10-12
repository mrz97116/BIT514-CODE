package com.dongxin.scm.utils;

import cn.hutool.core.date.DateUtil;
import com.dongxin.scm.exception.ScmException;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ：melon
 * @date ：Created in 2020/12/21 9:34
 */
@Slf4j
public class DateUtils {

    /**
     * 清除Date中的时分秒
     */
    public static Date clearHourMinuteSecond(Date date) {
        if (null == date) {
            throw new ScmException("日期参数为空");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getDateLastTime(Date date) {
        if (null == date) {
            throw new ScmException("日期参数为空");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static String gapDays(Date beginDate, Date endDate) {
        if (beginDate == null || endDate == null) {
            return "";
        }
        if (beginDate.after(endDate)) {
            return "已到期";
        }

        return DateUtil.betweenDay(beginDate, endDate, true) + "天";
    }

    //得到本月String类型年月，例：2021-01
    public static String getThisMonth(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String ThisMonth = sdf.format(today);
        return ThisMonth;
    }


    //得到今天String类型年月日，例：2021-01-07
    public static String getToday(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String Today = sdf.format(today);
        return Today;
    }

    //得到昨天String类型年月日，例如：2021-01-06
    public static String getYesterday(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        today = calendar.getTime();
        String Yesterday = sdf.format(today);
        return Yesterday;
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (Exception e) {
            log.error("parseDate error from {}", date);
            return null;
        }
    }

    //获取当月String类型年月
    public static String getCurrentMonth() {
        Date now = new Date();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
        String date = sDateFormat.format(now);
        return date;
    }

    //获取下个月String类型年月
    public static String getNextMonth() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date date1 = calendar.getTime();
        String nextDate = sDateFormat.format(date1);
        return nextDate;
    }

    //获取当月第一天String类型年月日时分秒(2021-04-01 00:00:00)
    public static String getFirstDay() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar cale = Calendar.getInstance();
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        String date = sDateFormat.format(cale.getTime());
        return date;
    }

    //获取当月最后一天String类型年月日类型年月日时分秒(2021-04-30 23:59:59)
    public static String getLastDay() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar cale = Calendar.getInstance();
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        String date = sDateFormat.format(cale.getTime());
        return date;
    }

    //获取上月倒数第二天 类型年月日时分秒(2021-04-01 00:00:00)
    public static Date getLastMonthLast2Day() {
        Calendar cale = Calendar.getInstance();
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, cale.get(Calendar.DAY_OF_MONTH) - 2);
        cale.set(Calendar.HOUR_OF_DAY, 0);
        cale.set(Calendar.MINUTE, 0);
        cale.set(Calendar.SECOND, 0);
        return cale.getTime();
    }

    //获取今年指定月的首日0时0分0秒
    public static Date getTheFirstDayOfTheMonth(int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.MONTH, month);
        return c.getTime();
    }


    //获取今年指定月的当前时间
    public static Date getsTheCurrentTimeOfThePreviousMonth(int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, month);
        return c.getTime();
    }

    //获取当前时间的前后几天（例：-1=昨天；1=明天）
    public static Date getDaysBeforeAndAfterTheCurrentTime(int day) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.add(Calendar.DATE, day);
        return ca.getTime();
    }

    //获取某个时间的前后几天（例：-1=某个时间的昨天；1=某个时间的明天）
    public static Date getDaysBeforeAndAfterTheCurrentTime(Date date, int day) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.add(Calendar.DATE, day);
        return ca.getTime();
    }
}
