package swd392.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DatetimeUtils {

    public enum TimeUnit {
        HOUR, MINUTE
    }
    public static final ZoneOffset DEFAULT_CLIENT_ZONE_OFFSET = ZoneOffset.ofHours(7);
    public final static String DATE_FORMAT_SCREEN = "dd/MM/yyyy";

    public static LocalDate toLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDate toLocalDateWithClientZone(Instant instant) {
        return instant.atZone(DEFAULT_CLIENT_ZONE_OFFSET).toLocalDate();
    }

    public static LocalDateTime toLocalDateTimeWithClientZone(Instant instant, Integer clientOffsetTime) {
        return clientOffsetTime == null ? instant.atZone(DEFAULT_CLIENT_ZONE_OFFSET).toLocalDateTime()
                : instant.atOffset(ZoneOffset.ofHours(clientOffsetTime)).toLocalDateTime();
    }

    public static LocalTime toLocalTimeWithClientZone(Instant instant) {
        return instant.atZone(DEFAULT_CLIENT_ZONE_OFFSET).toLocalTime();
    }

    public static Instant toInstantWithClientZone(LocalDate localDate) {
        return localDate.atStartOfDay(DEFAULT_CLIENT_ZONE_OFFSET).toInstant();
    }

    public static Instant toInstantWithClientZone(LocalDateTime localDateTime) {
        return localDateTime.atZone(DEFAULT_CLIENT_ZONE_OFFSET).toInstant();
    }

    public static LocalDateTime fromClientZoneToSystemZone(LocalDateTime localDateTime) {
        return localDateTime.atOffset(ZoneOffset.ofHours(7))
                .withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public static LocalDateTime fromSystemZoneToClientZone(LocalDateTime localDateTime) {
        return localDateTime.atOffset(ZoneOffset.UTC)
                .withOffsetSameInstant(ZoneOffset.ofHours(7))
                .toLocalDateTime();
    }

    public static LocalDateTime getLocalDateWithToClientZone(LocalDateTime localDateTime, Integer clientOffsetTime) {
        return clientOffsetTime == null ? fromSystemZoneToClientZone(localDateTime) :
                    localDateTime.atOffset(ZoneOffset.UTC).withOffsetSameInstant(ZoneOffset.ofHours(clientOffsetTime))
                            .toLocalDateTime();
    }

    public static String format(LocalDate localDate, String pattern) {
        return localDate == null ? null : localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String format(Instant instant, String pattern){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return instant.atZone(ZoneId.systemDefault()).format(dateTimeFormatter);
    }


    public static Instant toInstant(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static Instant toInstant(LocalDateTime localDateTime, ZoneId zoneId){
        return localDateTime.atZone(zoneId).toInstant();
    }

    public static Date toUtilDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date toUtilDate(Instant instant){
        return new Date(instant.toEpochMilli());
    }

    public static double getDuration(Instant from, Instant to, TimeUnit timeUnit){
        Duration duration = Duration.between(from, to);
        switch (timeUnit){
            case HOUR -> {
                return duration.toHours();
            }
            case MINUTE -> {
                return duration.toMinutes();
            }
            default -> {
                return duration.toDays();
            }
        }
    }

    public static String convertToDisplayStyle(Integer totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        String displayTime = "";
        if (hours > 0) {
            displayTime += hours + "h ";
        }
        if (minutes > 0) {
            displayTime += minutes + "m";
        }
        if(hours == 0 && minutes == 0) {
            displayTime = "0m";
        }
        return displayTime.trim();
    }

    public static String convertDateToString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_SCREEN));
    }

    public static boolean isDateFormatValid(LocalDate date, String customFormat) {
        try {
            String dateString = date.format(DateTimeFormatter.ofPattern(customFormat));
            LocalDate parsedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(customFormat));
            return parsedDate.equals(date);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInRange(LocalDate date, LocalDate startDate, LocalDate endDate){
        return date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1));
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalTime convertDateToLocalTime(Date date) {
        // Định dạng đối tượng Date thành chuỗi thời gian
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String timeString = dateFormat.format(date);

        // Chuyển đổi chuỗi thời gian thành LocalTime
        return LocalTime.parse(timeString);
    }
}
