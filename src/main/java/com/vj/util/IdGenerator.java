package com.vj.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;

public class IdGenerator {
    private static final String YEAR_OFFSET = String.valueOf(1000 + LocalDate.now().getYear() - 2024).substring(3);
    private static final DateTimeFormatter DAYS = new DateTimeFormatterBuilder()
            .appendValue(DAY_OF_YEAR, 3)
            .toFormatter();

    public static long nextId() {
        LocalTime now = LocalTime.now();
        String id = "1" + YEAR_OFFSET + String.valueOf(1000 + Long.valueOf(LocalDate.now().format(DAYS))).substring(1) + String.valueOf(100000 + now.getSecond() + now.getMinute() * 60 + now.getHour() * 60 * 60).substring(1);
        return Long.valueOf(id).longValue();
    }
}
