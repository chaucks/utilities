package com.xcoder.utilities.common;

import java.time.*;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author Chuck Lee
 */
public class DateUtensil {

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
//        final ZoneId zoneId = ZoneId.systemDefault();
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
//        final ZoneId zoneId = ZoneId.systemDefault();
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
//        final ZoneId zoneId = ZoneId.systemDefault();
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
//        final ZoneId zoneId = ZoneId.systemDefault();
        final ZonedDateTime zonedDateTime = instant.atZone(SYSTEM_ZONE_ID);
        final LocalDate localDate = zonedDateTime.toLocalDate();
        return localDate;
    }

    public static void main(String[] args) {
        localTimeToDate(LocalTime.now());
        dateToLocalTime(new Date());

        localDateTimeToDate(LocalDateTime.now());
        dateToLocalDateTime(new Date());

        localDateToDate(LocalDate.now());
        dateToLocalDate(new Date());

        System.out.println("complete");
    }
}
