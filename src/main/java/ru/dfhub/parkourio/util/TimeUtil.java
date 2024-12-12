package ru.dfhub.parkourio.util;

/**
 * Утилита для работы со временем
 */
public class TimeUtil {

    /**
     * Форматировать время в формат мин:сек.мс
     * @param millis Миллисекунды для перевода
     * @return мин:сек.мс
     */
    public static String formatTime(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis - minutes * 60000) / 1000;
        long ms = millis - minutes * 60000 - seconds * 1000;

        return "%s:%s.%s".formatted(
                minutes >= 10 ? minutes : "0" + minutes,
                seconds >= 10 ? seconds : "0" + seconds,
                String.valueOf(ms).substring(0, 2)
        );
    }
}
