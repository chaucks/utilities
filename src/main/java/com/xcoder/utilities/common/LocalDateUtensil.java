package com.xcoder.utilities.common;

import java.time.*;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author Chuck Lee
 */
public class LocalDateUtensil {

    /**
     * System zoneId
     */
    public static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

    /**
     * LocalTime to Date
     *
     * @param localTime localTime
     * @return
     */
    public static final Date localTimeToDate(final LocalTime localTime) {
        final LocalDate localDate = LocalDate.now();
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        final Date date = localDateTimeToDate(localDateTime);
        return date;
    }

    /**
     * Date to LocalTime
     *
     * @param date date
     * @return
     */
    public static final LocalTime dateToLocalTime(final Date date) {
        final LocalDateTime localDateTime = dateToLocalDateTime(date);
        final LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    /**
     * LocalDateTime to Date
     *
     * @param localDateTime localDateTime
     * @return
     */
    public static final Date localDateTimeToDate(final LocalDateTime localDateTime) {
        final ZonedDateTime zonedDateTime = localDateTime.atZone(SYSTEM_ZONE_ID);
        final Instant instant = zonedDateTime.toInstant();
        final Date date = Date.from(instant);
        return date;
    }

    /**
     * Date to LocalDateTime
     *
     * @param date date
     * @return
     */
    public static final LocalDateTime dateToLocalDateTime(final Date date) {
        final Instant instant = date.toInstant();
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, SYSTEM_ZONE_ID);
        return localDateTime;
    }

    /**
     * LocalDate to Date
     *
     * @param localDate localDate
     * @return
     */
    public static final Date localDateToDate(final LocalDate localDate) {
        final ZonedDateTime zonedDateTime = localDate.atStartOfDay(SYSTEM_ZONE_ID);
        final Instant instant = zonedDateTime.toInstant();
        final Date date = Date.from(instant);
        return date;
    }

    /**
     * Date to LocalDate
     *
     * @param date date
     * @return
     */
    public static final LocalDate dateToLocalDate(final Date date) {
        final Instant instant = date.toInstant();
        final ZonedDateTime zonedDateTime = instant.atZone(SYSTEM_ZONE_ID);
        final LocalDate localDate = zonedDateTime.toLocalDate();
        return localDate;
    }

    /**
     * Day difference
     *
     * @param date0      date0
     * @param localDate1 localDate1
     * @return date0 minus localDate1
     */
    public static final long dayDiff(final Date date0, final LocalDate localDate1) {
        final LocalDate localDate0 = dateToLocalDate(date0);
        return dayDiff(localDate0, localDate1);
    }

    /**
     * Day difference
     *
     * @param localDate0 localDate0
     * @param date1      date1
     * @return localDate0 minus date1
     */
    public static final long dayDiff(final LocalDate localDate0, final Date date1) {
        final LocalDate localDate1 = dateToLocalDate(date1);
        return dayDiff(localDate0, localDate1);
    }

    /**
     * Day difference
     *
     * @param date0 date0
     * @param date1 date1
     * @return date0 minus date1
     */
    public static final long dayDiff(final Date date0, final Date date1) {
        final LocalDate localDate0 = dateToLocalDate(date0);
        final LocalDate localDate1 = dateToLocalDate(date1);
        return dayDiff(localDate0, localDate1);
    }

    /**
     * Day difference
     *
     * @param localDate0 localDate0
     * @param localDate1 localDate1
     * @return localDate0 minus localDate1
     */
    public static final long dayDiff(final LocalDate localDate0, final LocalDate localDate1) {
        final long epochDay0 = localDate0.toEpochDay();
        final long epochDay1 = localDate1.toEpochDay();
        final long result = epochDay0 - epochDay1;
        return result;
    }
}
