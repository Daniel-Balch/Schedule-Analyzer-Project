package edu.miamioh.team2;

import java.util.ArrayList;

/**
 * Represents one possible exam block
 * Shown as a button in the GUI
 */
public class ExamRule implements Comparable<ExamRule> {
   // the rule type
   enum DayGroup {
        TR, MWF, M, T, W, R, F, TR2, MW2
    };

    // what start times are in this exam block
    private ArrayList<Time> startTimes;
    // what group is this exam block
    private DayGroup dayGroup;

    // for exam rules that don't actually work, just display a string on the button
    String dummyString;
    private boolean shouldCheck;

    /**
     * Make a dummy exam block that just displays a string and has no classes in it
     */
    public ExamRule(String dummyEntry) {
        dummyString = dummyEntry;
        this.shouldCheck = false;
    }

    /**
     * A working exam block with a set of start times and a rule class. See
     * the maintenance document for further explanation.
     *
     * null time means "or later" (sorry to any future maintainers)
     *
     * @param times the start times for this block
     * @param dayGroup the rule class
     */
    public ExamRule(Time times[], DayGroup dayGroup) {
        startTimes = new ArrayList<>();
        for (int i = 0; i < times.length; ++i) {
            startTimes.add(times[i]);
        }

        this.dayGroup = dayGroup;
        this.shouldCheck = true;
    }

    public boolean shouldCheck() {
        return this.shouldCheck;
    }

    /**
     * Check if a course fits in this rule's day group
     */
    public boolean matchesDayRule(Course course) {
        if (!shouldCheck) {
            return false;
        }

        // check if the day rules are correct
        boolean daysCorrect = false;
        switch(dayGroup) {
        case TR:
            if (course.days.contains("T") || course.days.contains("R")) {
                daysCorrect = true;
            }
            break;
        case MWF:
            if (course.days.contains("M") || course.days.contains("W") || course.days.contains(("F"))) {
                daysCorrect = true;
            }
            break;
        case M:
            if (course.days.equals("M")) {
                daysCorrect = true;
            }
            break;
        case T:
            if (course.days.equals("T")) {
                daysCorrect = true;
            }
            break;
        case W:
            if (course.days.equals("W")) {
                daysCorrect = true;
            }
            break;
        case R:
            if (course.days.equals("R")) {
                daysCorrect = true;
            }
            break;
        case F:
            if (course.days.equals("F")) {
                daysCorrect = true;
            }
            break;
        case TR2:
            if (course.days.contains("T") && course.days.contains("R")) {
                daysCorrect = true;
            }
            break;
        case MW2:
            if (course.days.contains("M") && course.days.contains("W")) {
                daysCorrect = true;
            }
            break;
        }

        return daysCorrect;
    }

    /**
     * Check if a given course time matches this rule's time requirements
     */
    public boolean matchesTimeRule(Course course) {
        Time start = course.start_time;
        boolean matched = false;
        for (int i = 0; i < startTimes.size(); ++i) {
            if (startTimes.get(i) == null) { // the "or later" rule
                if (startTimes.get(i-1).isAfter(start)) {
                    matched = true;
                    break;
                }
            }else if (start.equals(startTimes.get(i))) {
                matched = true;
                break;
            }
        }

        return matched;
    }

    /**
     * Check if a course's time rule is after this rule's time block
     *
     * Used for rounding up non-standard exam times
     */
    public boolean afterTimeRule(Course course) {
        for (int i = 0; i < startTimes.size(); ++i) {
            if (startTimes.get(i) != null) {
                if (startTimes.get(i).isAfter(course.start_time)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if this exam block is before or after another
     * 
     * Only valid if their day group matches, if they don't they're incomparable
     * @throws IllegalArgumentException if the day groups don't match
     */
    public int compareTo(ExamRule er) {
        if (this.dayGroup != er.dayGroup) {
            throw new IllegalArgumentException("Day groups don't match");
        }
        return this.startTimes.get(0).compareTo(er.startTimes.get(0));
    }

    /**
     * Make a string representation of this exam rule, this is shown on the
     * button and should exactly match what is on the official exam schedule.
     */
    public String toString() {
        if (!shouldCheck) {
            return dummyString;
        } else {
            String timesString = "";
            for (int i = 0; i < startTimes.size(); ++i) {
                if (startTimes.get(i) != null) {
                    if(i > 0) {
                        timesString += " or ";
                    }

                    timesString += startTimes.get(i).toString();
                } else {
                    timesString += " or later";
                }
            }

            // these are put in a jlabel so we can do html for formatting
            switch(dayGroup) {
            case TR:
                return "<html><center>" + timesString + "<br>T;Th; T & Th</center></html>";
            case MWF:
                return "<html><center>" + timesString + "<br>M and/or W and/or F;<br>also 4-5 days</center></html>";
            case M:
                return "<html><center>Classes meeting<br>Mondays only<br>" + timesString + "</center></html>";
            case T:
                return "<html><center>Classes meeting<br>Tuesdays only<br>" + timesString + "</center></html>";
            case W:
                return "<html><center>Classes meeting<br>Wednesdays only<br>" + timesString + "</center></html>";
            case R:
                return "<html><center>Classes meeting<br>Thursdays only<br>" + timesString + "</center></html>";
            case F:
                return "<html><center>Classes meeting<br>Fridays only<br>" + timesString + "</center></html>";
            case TR2:
                return "<html><center>Classes meeting twice<br>weekly, T and Th<br>" + timesString + "</center></html>";
            case MW2:
                return "<html><center>Classes meeting twice<br>weekly, M and W<br>" + timesString + "</center></html>";
            }

            return "error - impossible";
        }
    }
}