package edu.miamioh.team2;

/**
 * Represents a point in time. The default java classes didn't do what we needed
 * easily so we made our own.
 */
public class Time implements Comparable<Time> {
    private int hour;
    private int min;

    /**
     * Construct a default time object, the time is midnight
     */
    public Time() {
        hour = 0;
        min = 0;
    }

    /**
     * Construct a time object at a specified time
     * 
     * @param hour hour in military time (eg 2pm -> 14, 3am -> 3)
     * @param min the minutes into the hour
     */
    public Time(int hour, int min) {
        this.hour = 0;
        this.min = 0;
        this.set(hour, min);
    }

    /**
     * Change the time
     * 
     * @param hour hour in military time (eg 2pm -> 14, 3am -> 3)
     * @param min the minutes into the hour
     * @return whether or not your parameters were valid
     */
    public boolean set(int hour, int min) {
        if (0 <= hour && hour < 24 &&
            0 <= min  && min < 60) {

            this.hour = hour;
            this.min = min;

            return true;
        } else {
            return false;
        }
    }

    /**
     * Add a leading 0 to the number string if needed
     */
    private static String numToString(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return "" + num;
        }
    }

    /**
     * Get a string format of the time in 12 hour time with am and pm
     */
    public String toString() {
        if (hour >= 1 && hour < 12) { // am hours
            return numToString(hour) + ":" + numToString(min) + " a.m.";
        } else if (hour == 12) { // noon
            return numToString(hour) + ":" + numToString(min) + " p.m.";
        } else if (hour > 12){ // pm hours
            return numToString(hour - 12) + ":" + numToString(min) + " p.m.";
        } else if (hour == 0) { // midnight
            return "12:" + numToString(min) + " a.m.";
        }

        return "error - impossible";
    }

    /**
     * Check if two time objects represent the same time
     */
    public boolean equals(Time time) {
        return hour == time.hour && min == time.min;
    }

    /**
     * Check if a time object is after this time object
     * 
     * one.isAfter(two) is read "is two after one"
     */
    public boolean isAfter(Time time) {
        if (time.hour > hour) {
            return true;
        }
        if (time.hour == hour && time.min > min) {
            return true;
        }
        return false;
    }

    /**
     * The java compare to method so we can sort the time objects
     */
    @Override
    public int compareTo(Time o) {
        if (equals(o)) {
            return 0;
        }

        if (isAfter(o)) {
            return 1;
        } else {
            return -1;
        }
    }
}