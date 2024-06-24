package com.bin.webmonitor.common.util;

import com.bin.webmonitor.common.Constants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {

    public static final String NORMAL_FORMATER = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMATER = "yyyy-MM-dd";
    public static final String TIME_FORMATER = "HH:mm:ss";
    public static final String MS_FORMATER = "yyyy-MM-dd HH:mm:ss.S";
    public static final String YEAR_MONTH = "yyyy-MM";
    public static final String STANDER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyyMMddHHmmssSSS";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM = "yyyyMMddHHmm";


    public static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public static final DateTimeFormatter DATE_MS_FORMATTER = DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_SSS);

    public static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern(
            NORMAL_FORMATER);

    public static final DateTimeFormatter MISS_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            DATE_FORMATER);

    public static final DateTimeFormatter MS_FORMATTER = DateTimeFormatter.ofPattern(MS_FORMATER);

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(
            TIME_FORMATER);

    public static final DateTimeFormatter ALL_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS);

    public static final DateTimeFormatter DATE_MIN_FORMATTER = DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM);


    public static final DateTimeFormatter DATE_MONTH_FORMATTER =
            DateTimeFormatter.ofPattern(YEAR_MONTH);

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(STANDER_FORMAT);
    /**
     * One minute microseconds.
     */
    public static final int MINUTE_MS = 1000 * 60;
    /**
     * One hour microseconds.
     */
    public static final int HOUR_MS = MINUTE_MS * 60;
    /**
     * One day microseconds.
     */
    public static final int DAY_MS = HOUR_MS * 24;

    /**
     * One day seconds.
     */
    public static final long ONE_DAY_SECOND = 24 * 60 * 60;

    /**
     * 将 yyyy-MM-dd HH:mm:ss 字符串转换成LocalDateTime类型.
     */
    public static LocalDateTime str2LocalDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, FULL_FORMATTER);
    }

    /**
     * 获取 yyyy-MM-dd HH:mm:ss 样式的字符串.
     */
    public static String localDateTime2Str(LocalDateTime localDateTime) {
        return localDateTime.format(FULL_FORMATTER);
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss样式的当前时间，去掉毫秒数的格式.
     */
    public static String getNowString() {
        return LocalDateTime.now().format(FULL_FORMATTER);
    }

    /**
     * LocalDateTime -> Date .
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return Date.from(instant);
    }

    /**
     * LocalDate -> Date .
     */
    public static Date localDate2Date(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay(ZONE_ID).toInstant();
        return Date.from(instant);
    }

    /**
     * str -> Date.
     */
    public static Date str2Date(String str) {
        LocalDate localDate = LocalDate.parse(str, DATE_FORMATTER);
        return localDate2Date(localDate);
    }

    /**
     * str -> Date.
     */
    public static Date str2Date(String str, String format) {
        LocalDate localDate = LocalDate.parse(str, DateTimeFormatter.ofPattern(format));
        return localDate2Date(localDate);
    }



    /**
     * full str -> Date.
     */
    public static Date fullStr2Date(String str) {
        LocalDateTime localDate = LocalDateTime.parse(str, FULL_FORMATTER);
        return localDateTime2Date(localDate);
    }

    /**
     * LocalDateTime -> sys zone epoch milliseconds.
     */
    public static Long localDateTime2Ms(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * LocalDateTime -> sys zone epoch second.
     */
    public static Long localDateTime2Second(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZONE_ID).toInstant();
        return instant.getEpochSecond();
    }

    /**
     * ms -> LocalDateTime str.
     */
    public static String ms2LocalDateTimeStr(long value) {
        LocalDateTime date = ms2LocalDateTime(value);
        return date.format(FULL_FORMATTER);
    }

    public static LocalDateTime ms2LocalDateTime(long value) {
        Instant instant = Instant.ofEpochMilli(value);
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    /**
     * Date -> str.
     */
    public static String date2Str(Date date) {
        if (null == date) {
            return "";
        }
        LocalDate localDate = date2LocalDate(date);
        return localDate.format(DATE_FORMATTER);
    }


    /**
     * Date -> full str.
     */
    public static String date2fullStr(Date date) {
        if (null == date) {
            return "";
        }
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime.format(FULL_FORMATTER);
    }

    public static String date2MissStr(Date date) {
        if (null == date) {
            return "";
        }
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime.format(MISS_FORMATTER);
    }

    /**
     * Date -> str.
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Date -> str.
     */
    public static LocalDate date2LocalDate(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 获取指定时间对应的初始化延迟秒数.
     */
    public static long getInitDelay(String time) {
        LocalDate nowLocalDate = LocalDate.now();
        LocalTime localTime = LocalTime.parse(time, TIME_FORMATTER);
        LocalDateTime localDateTime = LocalDateTime.of(nowLocalDate, localTime);
        LocalDateTime nowLocalDateTime = LocalDateTime.now();

        Long second = TimeUtil.localDateTime2Second(localDateTime);
        Long nowSecond = TimeUtil.localDateTime2Second(nowLocalDateTime);
        long initDelay = second - nowSecond;
        initDelay = initDelay > 0 ? initDelay : ONE_DAY_SECOND + initDelay;
        return initDelay;
    }

    /**
     * now millis
     */
    public static long now() {
        LocalDateTime now = LocalDateTime.now();
        return localDateTime2Ms(now);
    }

    public static Date nowDate() {
        LocalDateTime now = LocalDateTime.now();
        return localDateTime2Date(now);
    }

    public static String date2TimeStr(Date date) {
        if (null == date) {
            return "";
        }
        LocalTime localTime = date2LocalTime(date);
        if (null == localTime) {
            return "";
        }
        return localTime.format(TIME_FORMATTER);
    }

    public static LocalTime date2LocalTime(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        return instant.atZone(ZONE_ID).toLocalTime();
    }

    public static Date timeStr2Date(String timeStr) {
        if (StringUtil.isBlank(timeStr)) {
            return null;
        }
        LocalTime localTime = LocalTime.parse(timeStr, TIME_FORMATTER);
        return localTime2Date(localTime);
    }

    public static Date localTime2Date(LocalTime localTime) {
        LocalDate localDate = LocalDate.of(Constants.NOW_YEAR, Constants.ONE, Constants.ONE);
        Instant instant = localTime.atDate(localDate).atZone(ZONE_ID).toInstant();
        return Date.from(instant);
    }


    public static Date allTimeStr2Date(String timeStr) {
        if (StringUtil.isBlank(timeStr)) {
            return null;
        }
        return localDateTime2Date(LocalDateTime.parse(timeStr, ALL_TIME_FORMATTER));
    }


    // yyyy-MM-dd 00:00:00
    public static long getCurrTimeFromStr() {
        LocalDate now = LocalDate.now();
        String format = now.format(DATE_FORMATTER) + " 00:00:00";
        Instant instant = LocalDateTime.parse(format, FULL_FORMATTER)
                .atZone(ZONE_ID).toInstant();
        return instant.toEpochMilli();

    }

    public static String getCurrTimeStr() {
        LocalDate now = LocalDate.now();
        return now.format(DATE_FORMATTER) + " 00:00:00";
    }

    public static long getMinuteMill() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static long getLastMinuteMill(int n) {
        return LocalDateTime.now().minusMinutes(n).
                toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 将控制中心传来的时间格式转换为时间戳
     *
     * @param time 控制中心的时间格式，yyyyMMddHHmmss的long类型，如long类型的时间time=20190916143212表示2019年9月16日14点32分12秒
     * @return unix时间戳   "yyyy-MM-dd HH:mm:ss"
     */
    public static String timeConvert(Long time) {
        return date2fullStr(str2Date(time.toString(), "yyyyMMddHHmmss"));
    }

    public static long time2Ms(Long time) {
        LocalDateTime ldt = LocalDateTime.parse(time.toString(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
       return localDateTime2Ms(ldt);
    }

    public static String dateMin2fullStr(Date date) {
        if (null == date) {
            return "";
        }
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime.format(DATE_MIN_FORMATTER);
    }
}

