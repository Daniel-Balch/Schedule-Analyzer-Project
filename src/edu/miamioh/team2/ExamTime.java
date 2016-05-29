package edu.miamioh.team2;

/**
 * This represents a row in the exam schedule, it's just an array of 5 ExamRules,
 * one for each day.
 */
public class ExamTime {
    private Time startTime;
    private Time endTime;

    public ExamRule rules[] = new ExamRule[5];

    public ExamTime() {
        startTime = new Time();
        endTime = new Time();
    }

    public ExamTime(int startHour, int startMin, int endHour, int endMin, ExamRule rules[]) {
        startTime = new Time(startHour, startMin);
        endTime = new Time(endHour, endMin);
        if (rules.length != 5) {
            System.err.println("Incorrect number of exam rules");
            System.exit(1);
        }
        this.rules = rules;
    }

    @Override
    public String toString() {
        return startTime.toString()
               + " - " +
               endTime.toString();
    }
}