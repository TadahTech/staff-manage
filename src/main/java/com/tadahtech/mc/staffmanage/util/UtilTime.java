package com.tadahtech.mc.staffmanage.util;

import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTime {

    public static final String DATE_FORMAT_NOW = "MM-dd-yyyy HH:mm:ss";
    public static final String DATE_FORMAT_DAY = "MM-dd-yyyy";
    // Avoid compiling pattern every time
    private static final Pattern MILLIS_PATTERN = Pattern.compile("(\\d*)([a-zA-Z])");

    public static Date getDate(int month, int day, int year) {
        return getTime(month, day, year, 0, 0, 0);
    }

    public static Date getTime(int month, int day, int year, int hour, int minute, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, sec);
        return calendar.getTime();
    }

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static String when(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(time);
    }

    public static String date() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DAY);
        return sdf.format(cal.getTime());
    }

    public static String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(date);
    }

    public static String formatTime(int timeLeft) {
        int seconds = timeLeft % 60;
        int minutes = (timeLeft - seconds) / 60;
        return (minutes < 10 ? "0" : "" + minutes) + (seconds < 10 ? "0" : "" + seconds);
    }

    public static String tickToTimer(int ticks) {
        return toTimerSecond(ticks * 50);
    }

    public static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static boolean hasElapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static boolean hasPassed(long timestamp) {
        return System.currentTimeMillis() - timestamp > 0;
    }

    public static long getTicksLeft(long timestamp) {
        long diff = timestamp - System.currentTimeMillis();
        return diff / 50;
    }

    public static long getTimeLeft(long from, long amount, TimeUnit unit) {
        return getTimeLeft(from, unit.toMillis(amount));
    }

    public static long getTimeLeft(long from, long amount) {
        return getTimeLeft(from + amount);
    }

    public static long getTimeLeft(long timestamp) {
        return timestamp - System.currentTimeMillis();
    }

    public static long tickToMillis(long ticks) {
        return ticks * 50L;
    }

    public static long millisToTicks(long millis) {
        return millis / 50L;
    }

    public static long getDifference(long timestamp1, long timestamp2) {
        return Math.abs(timestamp1 - timestamp2);
    }

    public static long getCountSince(long since, TimeUnit unit) {
        return getCountBetween(since, System.currentTimeMillis(), unit);
    }

    public static long getCountBetween(long since, long till, TimeUnit unit) {
        long difference = getDifference(since, till);
        return (long) Math.floor((double) difference / (double) unit.toMillis(1));
    }

    /**
     * Returns the milliseconds formatted as a timer where second is the most accurate
     * unit used.
     *
     * @param millis The milliseconds
     * @return The timer
     */
    public static String toTimerSecond(long millis) {
        if (millis > TimeUnit.HOURS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String toHoursAndMinutes(long millis) {
        if (millis < TimeUnit.HOURS.toMillis(1)) {
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis);
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        }

        int hours = (int) TimeUnit.MILLISECONDS.toHours(millis);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hours));
        return hours + " hour" + (hours == 1 ? "" : "s") + (minutes > 0 ? " and " + (minutes + " minute" + (minutes == 1 ? "" : "s")) : "");
    }

    public static String toSecondsLeft(long progress, long total) {
        return toSecondsLeft(total - progress);
    }

    public static String toSecondsLeft(long ticks) {
        int seconds = (int) Math.ceil((double) ticks / 20.0);
        return withSuffix(seconds);
    }

    public static String withSuffix(long seconds) {
        return seconds + " second" + (seconds == 1 ? "" : "s");
    }

    /**
     * Converts an amount of milliseconds into an easily interpretable sentence
     *
     * @param millis The milliseconds
     * @return Easily interpretable sentence
     */
    public static String toSentence(long millis) {
        return toSentence(millis, "Instant");
    }

    public static String toSentence(long millis, String tooShort) {
        if (millis < 1000) {
            return tooShort;
        }

        long weeks = TimeUnit.MILLISECONDS.toDays((millis - (millis % 7)) / 7);
        millis -= TimeUnit.DAYS.toMillis(weeks) * 7;
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        List<String> list = Lists.newLinkedList();

        if (weeks != 0) {
            list.add(weeks + " " + (weeks > 1 ? "weeks" : "week"));
        }

        if (days != 0) {
            list.add(days + " " + (days > 1 ? "days" : "day"));
        }

        if (hours != 0) {
            list.add(hours + " " + (hours > 1 ? "hours" : "hour"));
        }

        if (minutes != 0) {
            list.add(minutes + " " + (minutes > 1 ? "minutes" : "minute"));
        }

        if (seconds != 0) {
            list.add(seconds + " " + (seconds > 1 ? "seconds" : "second"));
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            builder.append(", ").append(list.get(i));
        }

        if (builder.length() > 0) {
            builder.append(" and ");
        }

        builder.append(list.get(list.size() - 1));
        if (list.size() > 1) {
            return builder.substring(2);
        }

        return builder.toString();
    }

    /**
     * Converts from "simple format" to milliseconds. Simple format meaning for example
     * 2 weeks, 5 days and 10 seconds shortened to "2w5d10s".
     *
     * @param simple Amount of time written using "simple format"
     * @return Milliseconds
     */
    public static long toMillis(String simple) {
        Matcher matcher = MILLIS_PATTERN.matcher(simple);

        long total = 0;
        while (matcher.find()) {
            int amount = Integer.valueOf(matcher.group(1));
            char ch = matcher.group(2).charAt(0);

            switch (ch) {
                case 'w':
                    total += TimeUnit.DAYS.toMillis(7 * amount);
                    break;
                case 'd':
                    total += TimeUnit.DAYS.toMillis(amount);
                    break;
                case 'h':
                    total += TimeUnit.HOURS.toMillis(amount);
                    break;
                case 'm':
                    total += TimeUnit.MINUTES.toMillis(amount);
                    break;
                case 's':
                    total += TimeUnit.SECONDS.toMillis(amount);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected character " + ch + " found while parsing milliseconds from simple format");
            }
        }

        return total;
    }
}
