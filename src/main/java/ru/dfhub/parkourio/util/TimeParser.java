package ru.dfhub.parkourio.util;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeParser {

    public static long stringToLong(String timeString) throws Exception{
        if (List.of("permanent", "permanently", "perm", "forever", "перманент", "пермач").contains(timeString)) return -1;
        int number = Integer.parseInt(timeString.substring(0, timeString.length() - 1));

        return switch (timeString.charAt(timeString.length() - 1)) {
            case 'd' -> TimeUnit.DAYS.toMillis(number);
            case 'm' -> TimeUnit.MINUTES.toMillis(number);
            case 'M' -> TimeUnit.DAYS.toMillis(number * 30L);
            case 's' -> TimeUnit.SECONDS.toMillis(number);
            case 'h' -> TimeUnit.HOURS.toMillis(number);
            case 'w' -> TimeUnit.DAYS.toMillis(number * 7L);
            default -> 0L;
        };
    }

    public static String longToString(long time) {
        if (time == -1) return "навсегда";
        Duration duration = Duration.ofMillis(time);
        StringBuilder sb = new StringBuilder();

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (days == 0) {
        } else if (lastNumOf(days) == 1) {
            sb.append(days).append(" день, ");
        } else if (List.of(2, 3, 4).contains(lastNumOf(days))) {
            sb.append(days).append(" дня, ");
        } else if (List.of(5, 6, 7, 8, 9, 0).contains(lastNumOf(days))) {
            sb.append(days).append(" дней, ");
        }

        if (hours == 0) {
        } else if (lastNumOf(hours) == 1) {
            sb.append(hours).append(" час, ");
        } else if (List.of(2, 3, 4).contains(lastNumOf(hours))) {
            sb.append(hours).append(" часа, ");
        } else if (List.of(5, 6, 7, 8, 9, 0).contains(lastNumOf(hours))) {
            sb.append(hours).append(" часов, ");
        }

        if (minutes == 0) {
        } else if (lastNumOf(minutes) == 1) {
            sb.append(minutes).append(" минута, ");
        } else if (List.of(2, 3, 4).contains(lastNumOf(minutes))) {
            sb.append(minutes).append(" минуты, ");
        } else if (List.of(5, 6, 7, 8, 9, 0).contains(lastNumOf(minutes))) {
            sb.append(minutes).append(" минут, ");
        }

        if (seconds == 0) {
        } else if (lastNumOf(seconds) == 1) {
            sb.append(seconds).append(" секунда, ");
        } else if (List.of(2, 3, 4).contains(lastNumOf(seconds))) {
            sb.append(seconds).append(" секунды, ");
        } else if (List.of(5, 6, 7, 8, 9, 0).contains(lastNumOf(seconds))) {
            sb.append(seconds).append(" секунд, ");
        }

        String parsedString = sb.toString();
        return parsedString.substring(0, parsedString.length() - 2);
    }

    /**
     * На какую цифру заканчивается число
     * @param number Число
     * @return Последняя цифра в числе
     */
    private static int lastNumOf(long number) {
        String str = String.valueOf(number);
        return Integer.parseInt(String.valueOf(str.charAt(str.length() - 1)));
    }
}
