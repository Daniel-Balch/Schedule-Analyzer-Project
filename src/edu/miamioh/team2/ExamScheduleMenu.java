package edu.miamioh.team2;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The menu that looks like the exam schedule
 */
public class ExamScheduleMenu extends JPanel {
    private ExamTime examTimes[] = new ExamTime[6];

    /**
     * Just a random number to get rid of the warning
     */
    private static final long serialVersionUID = -5198196437838938441L;

    /**
     * For each exam rule make a list of combined courses in that exam block
     */
    HashMap<ExamRule, ArrayList<CombinedCourse>>
    getCourseLists() {
        HashMap<ExamRule, ArrayList<CombinedCourse>> courseLists = new HashMap<>();

        System.out.println("\nCourses not exactly matched: ");
        // for each class
        ArrayList<ExamRule> rulesMatchingDays = new ArrayList<>();
        classLoop:
        for (int k = 0; k < CSV_Reader.courseList.size(); ++k) {
            Course course = CSV_Reader.courseList.get(k).getEarliest();
            rulesMatchingDays.clear();
            for (int r = 0; r < examTimes.length; ++r) {
                for (int c = 0; c < examTimes[r].rules.length; ++c) { // for each exam block
                    ExamRule examRule = examTimes[r].rules[c];
                    if (examRule.shouldCheck() && examRule.matchesDayRule(course)) {
                        // if the course is an exact match add it to the course list for this block
                    	//System.out.println("TESTING: " + examRule.matchesTimeRule(course));
                        if (examRule.matchesTimeRule(course)) {
                            if (courseLists.get(examRule) == null) {
                                courseLists.put(examRule, new ArrayList<CombinedCourse>());
                            }
                            courseLists.get(examRule).add(CSV_Reader.courseList.get(k));
                            continue classLoop; // skip to the next class now
                        // otherwise add the block to the list of possible exam blocks for this class
                        } else if (examRule.afterTimeRule(course)){
                            rulesMatchingDays.add(examRule);
                        }
                    }
                }
            }

            // if we didn't get an exact match
            if (rulesMatchingDays.size() > 0) {
                System.out.println(course.toString());
                ExamRule roundedRule = Collections.min(rulesMatchingDays);
                if (courseLists.get(roundedRule) == null) {
                    courseLists.put(roundedRule, new ArrayList<CombinedCourse>());
                }
                courseLists.get(roundedRule).add(CSV_Reader.courseList.get(k));
            }
        }

        return courseLists;
    }

    /**
     * make buttons for each exam block
     */
    void addExamButtons() {
        HashMap<ExamRule, ArrayList<CombinedCourse>> courseLists = getCourseLists();

        for (int i = 0; i < examTimes.length; ++i) {
            JLabel label = new JLabel(examTimes[i].toString());
            label.setForeground(Color.white);
            add(label);
            for (int j = 0; j < examTimes[i].rules.length; ++j) { // for each exam rule
                // make the rule button
                JButton button = new JButton((examTimes[i].rules[j].toString()));
                button.setBackground(Color.white); // set the default button background color
                add(button);

                // if this rule has classes in it
                if (examTimes[i].rules[j].shouldCheck() && courseLists.get(examTimes[i].rules[j]) != null) {
                    // make the GUI table
                    final Table table = new Table(courseLists.get(examTimes[i].rules[j]));
                    if(courseLists.get(examTimes[i].rules[j]) != null) {
                    	button.setBackground(new Color(218, 184, 10));
                    }
                    if (table.hasConflict()) { // if there's a conflict recolor the button
                        button.setBackground(new Color(255, 0, 0));
                    }

                    // when the button is pressed open the table GUI
                    button.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e){
                            table.open();
                        }
                    });
                }
            }
        }
    }

    /**
     * Make the actual GUI
     */
    public ExamScheduleMenu() {
        GridLayout layout = new GridLayout(0, 6);
        setLayout(layout);
        setExamTimes();
        setBackground(Color.magenta);
        JLabel temp = new JLabel("Exam Times", JLabel.CENTER);
        temp.setForeground(Color.white);
        add(temp);
        temp = new JLabel("Monday", JLabel.CENTER);
        temp.setForeground(Color.white);
        add(temp);
        temp = new JLabel("Tuesday", JLabel.CENTER);
        temp.setForeground(Color.white);
        add(temp);
        temp = new JLabel("Wednesday", JLabel.CENTER);
        temp.setForeground(Color.white);
        add(temp);
        temp = new JLabel("Thursday", JLabel.CENTER);
        temp.setForeground(Color.white);
        add(temp);
        temp = new JLabel("Friday", JLabel.CENTER);
        temp.setForeground(Color.white);
        add(temp);

        addExamButtons();
    }

    /**
     * Actually set the exam schedule data
     */
    private void setExamTimes() {
        examTimes[0] = new ExamTime(8,0,10,0, new ExamRule[]{
                new ExamRule("Group Exam #1"),
                new ExamRule(new Time[] {new Time(8,00), new Time(8,30)}, ExamRule.DayGroup.TR),
                new ExamRule(new Time[] {new Time(8,00), new Time(8,30)}, ExamRule.DayGroup.MWF),
                new ExamRule(new Time[] {new Time(10,00)}, ExamRule.DayGroup.TR),
                new ExamRule("Group Exam #8")});
        examTimes[1] = new ExamTime(10,15,12,15, new ExamRule[]{
                new ExamRule("Group Exam #2"),
                new ExamRule("Group Exam #4"),
                new ExamRule(new Time[] {new Time(11,30)}, ExamRule.DayGroup.MWF),
                new ExamRule("Group Exam #6"),
                new ExamRule(new Time[] {new Time(10,00)}, ExamRule.DayGroup.MWF)});
        examTimes[2] = new ExamTime(12,45,14,45, new ExamRule[]{
                new ExamRule(new Time[] {new Time(13,00)}, ExamRule.DayGroup.MWF),
                new ExamRule(new Time[] {new Time(11,30)}, ExamRule.DayGroup.TR),
                new ExamRule("Group Exam #5"),
                new ExamRule(new Time[] {new Time(13,00)}, ExamRule.DayGroup.TR),
                new ExamRule("Group Exam #9")});
        examTimes[3] = new ExamTime(15,00,17,00, new ExamRule[]{
                new ExamRule(new Time[] {new Time(16,00)}, ExamRule.DayGroup.MWF),
                new ExamRule(new Time[] {new Time(14,30)}, ExamRule.DayGroup.TR),
                new ExamRule(new Time[] {new Time(14,30)}, ExamRule.DayGroup.MWF),
                new ExamRule(new Time[] {new Time(16,00)}, ExamRule.DayGroup.TR),
                new ExamRule("None")});
        examTimes[4] = new ExamTime(17,30,19,30, new ExamRule[]{
                new ExamRule("Group Exam #3"),
                new ExamRule(new Time[] {new Time(17,30), new Time(18,00), null}, ExamRule.DayGroup.TR2),
                new ExamRule(new Time[] {new Time(17,30), new Time(18,00), null}, ExamRule.DayGroup.MW2),
                new ExamRule("Group Exam #7"),
                new ExamRule("Regular class time")});
        examTimes[5] = new ExamTime(19,45,21,45, new ExamRule[]{
                new ExamRule(new Time[] {new Time(17,30), new Time(18,00), null}, ExamRule.DayGroup.M),
                new ExamRule(new Time[] {new Time(17,30), new Time(18,00), null}, ExamRule.DayGroup.W),
                new ExamRule(new Time[] {new Time(17,30), new Time(18,00), null}, ExamRule.DayGroup.T),
                new ExamRule(new Time[] {new Time(17,30), new Time(18,00), null}, ExamRule.DayGroup.R),
                new ExamRule("Regular class time")});
    }
}
