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
     * @param date date
     * @param sdf  sdf
     * @return
     * @throws ParseException
     */
    public static final Date parse(final Date date, final SimpleDateFormat sdf) throws ParseException {
        final String dateString = format(date, sdf);
        final Date result = sdf.parse(dateString);
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
}
