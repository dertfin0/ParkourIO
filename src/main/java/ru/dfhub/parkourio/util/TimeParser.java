package ru.dfhub.parkourio.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeParser {

    public static long stringToLong(String timeString) {
        if (List.of("permanent", "permanently", "perm", "forever", "перманент", "пермач").contains(timeString)) return -1;
        int number;
        try {
            number = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
        } catch (IllegalArgumentException e) {
            return 0L; // Can't parse number
        }

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
}
