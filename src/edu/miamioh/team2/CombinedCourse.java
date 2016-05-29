package edu.miamioh.team2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Some courses have more than one entry in the CSV file because they have
 * a separate lab day or other reasons possibly
 * 
 * This class is used to combine courses with the same crn into one list container
 */
public class CombinedCourse {
    // the list of courses in this group
    public ArrayList<Course> courses;

    /**
     * Make an empty combined course object
     */
    public CombinedCourse() {
        courses = new ArrayList<>();
    }

    /**
     * Gets the earliest by time and day class in the list of courses
     *
     * Used because when there are more than one course times the exam time
     * for the combined course is based on the earliest entry
     *
     * @return the earliest course
     */
    public Course getEarliest() {
        // Make an array list of course references for each day
        ArrayList<ArrayList<Course>> coursesWithDay = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            coursesWithDay.add(new ArrayList<Course>());
        }

        // if a course occurs on a given day, add it to that day's list of courses
        for (int i = 0; i < courses.size(); ++i) {
            String days = courses.get(i).days;
            if (days.contains("M")) {
                coursesWithDay.get(0).add(courses.get(i));
            }
            if (days.contains("T")) {
                coursesWithDay.get(1).add(courses.get(i));
            }
            if (days.contains("W")) {
                coursesWithDay.get(2).add(courses.get(i));
            }
            if (days.contains("R")) {
                coursesWithDay.get(3).add(courses.get(i));
            }
            if (days.contains("F")) {
                coursesWithDay.get(4).add(courses.get(i));
            }
        }

        // for each day starting on monday
        for (int i = 0; i < 5; ++i) {
            // if only one class is on the first day with classes just return it
            if (coursesWithDay.get(i).size() == 1) {
                return coursesWithDay.get(i).get(0);
            } else if (coursesWithDay.get(i).size() > 1) { // otherwise if there are more than one
                // return the class on this day with the earliest time
                return Collections.min(coursesWithDay.get(i), new Comparator<Course>() {
                    @Override
                    public int compare(Course o1, Course o2) {
                        return o1.start_time.compareTo(o2.start_time);
                    }
                });
            }
        }

        return courses.get(0);
    }

    /**
     * Check if this combined course object overlaps in time slots with
     * another. If they do then then you would have to be in two places at
     * the same time to take both classes so it's impossible to.
     * ie intersecting courses cannot conflict
     *
     * @param rhs the other course to check for an intersection with
     * @return whether or not their times overlap
     */
    public boolean intersects(CombinedCourse rhs) {
        // if any two courses in the two lists intersect then the combined courses do
        for (int i = 0; i < courses.size(); ++i) {
            for (int j = 0; j < rhs.courses.size(); ++j) {
                if (courses.get(i).intersects(rhs.courses.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < courses.size() - 1; ++i) {
            out += courses.get(i).toString() + "\n";
        }
        out += courses.get(courses.size() - 1).toString();
        return out;
    }
}

