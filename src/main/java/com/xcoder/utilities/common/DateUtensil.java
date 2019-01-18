package com.xcoder.utilities.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author Chuck Lee
 */
public class DateUtensil {

    /**
     * Date format
     *
     * @param date    date
     * @param pattern pattern
     * @return
     */
    public static final String format(final Date date, final String pattern) {
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return format(date, sdf);
    }

    /**
     * Date format
     *
     * @param date date
     * @param sdf  sdf
     * @return
     */
    public static final String format(final Date date, final SimpleDateFormat sdf) {
        final String result = sdf.format(date);
        return result;
    }

    /**
     * Date parse
     *
     * @param date    date
     * @param pattern pattern
     * @return
     * @throws ParseException
     */
    public static final Date parse(final Date date, final String pattern) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return parse(date, sdf);
    }

    /**
     * Date parse
     *
     * @param date    date
     * @param pattern pattern
     * @return
     * @throws ParseException
     */
    public static final Date parse(final String date, final String pattern) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return parse(date, sdf);
    }

    /**
     * Date parse
     *
     * @param date date
     * @param sdf  sdf
     * @return
     * @throws ParseException
     */
    public static final Date parse(final Date date, final SimpleDateFormat sdf) throws ParseException {
        final String dateString = format(date, sdf);
        final Date result = parse(dateString, sdf);
        return result;
    }

    /**
     * Date parse
     *
     * @param date date
     * @param sdf  sdf
     * @return
     * @throws ParseException
     */
    public static final Date parse(final String date, final SimpleDateFormat sdf) throws ParseException {
        final Date result = sdf.parse(date);
        return result;
    }

    /**
     * Get Calendar
     *
     * @param date date
     * @return
     */
    public static final Calendar getCalendar(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Add date
     *
     * @param date   date
     * @param field  field
     * @param amount amount
     * @return
     */
    public static final Date addDate(final Date date, final int field, final int amount) {
        final Calendar calendar = getCalendar(date);
        calendar.add(field, amount);
        final Date result = calendar.getTime();
        return result;
    }

    /**
     * Last day of current month
     *
     * @return
     */
    public static final Date lastDayOfMonth() {
        return lastDayOfMonth(new Date());
    }

    /**
     * Last day of date's month
     *
     * @param date date
     * @return
     */
    public static final Date lastDayOfMonth(final Date date) {
        return lastDayOfMonth(date, Calendar.getInstance());
    }

    /**
     * Last day of date's month
     *
     * @param date     date
     * @param calendar calendar
     * @return
     */
    public static final Date lastDayOfMonth(final Date date, final Calendar calendar) {
        // 顺序勿动
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        final Date result = calendar.getTime();
        return result;
    }

    /**
     * First day of current month
     *
     * @return
     */
    public static final Date firstDayOfMonth() {
        return firstDayOfMonth(new Date());
    }

    /**
     * First day of date's month
     *
     * @param date date
     * @return
     */
    public static final Date firstDayOfMonth(final Date date) {
        return firstDayOfMonth(date, Calendar.getInstance());
    }


    /**
     * First day of date's month
     *
     * @param date     date
     * @param calendar calendar
     * @return
     */
    public static final Date firstDayOfMonth(final Date date, final Calendar calendar) {
        // 顺序勿动
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final Date result = calendar.getTime();
        return result;
    }
}
