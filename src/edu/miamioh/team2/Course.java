package edu.miamioh.team2;

/**
 * Represents one line of the CSV file
 */
public class Course {

	int crn;
	String subject;
	int course;
	String section;
	String title;
	int credits;
	int enrolled;
	int max_enrolled;
	String start_time_str;
	Time start_time;
	String end_time_str;
	Time end_time;
	String days;
	String building;
	int room;
	String instructor;
	
	public Course(int crn, String subject, int course, String section,
			String title, int credits, int enrolled, int max_enrolled,
			String start_time, int start_time_hours, int start_time_minutes,
			String end_time, int end_time_hours, int end_time_minutes,
			String days, String building, int room, String instructor) {

		this.crn = crn;
		this.subject = subject;
		this.course = course;
		this.section = section;
		this.title = title;
		this.credits = credits;
		this.enrolled = enrolled;
		this.max_enrolled = max_enrolled;
		this.start_time_str = start_time;
		this.start_time = new Time(start_time_hours, start_time_minutes);
		this.end_time = new Time(end_time_hours, end_time_minutes);
		this.end_time_str = end_time; //Jimmy Added this
		this.days = days;
		this.building = building;
		this.room = room;
		this.instructor = instructor;

	}
	
	public Course clone() {
		Course c = new Course(
		this.crn,
		this.subject,
		this.course,
		this.section,
		this.title,
		this.credits,
		this.enrolled,
		this.max_enrolled,
		this.start_time_str,
		0,0,
		this.end_time_str,
		0,0,
		this.days,
		this.building,
		this.room,
		this.instructor);
		
		c.end_time = this.end_time;
		c.start_time = this.start_time;
		
		return c;

	}

	public String toString() {
		return crn + " " + subject + " " + course + " " + section + " " + title
				+ " " + credits + " " + enrolled + " " + max_enrolled + " "
				+ start_time + " " + end_time + " " + days + " " + building
				+ " " + room + " " + instructor;
	}

	/**
	 * Check if two courses occur at the same time or not
	 */
    public boolean intersects(Course rhs) {
        for (int i = 0; i < days.length(); ++i) {
            if (rhs.days.contains(days.substring(i, i+1))) { // find the first day they both have
                // if the classes are NOT totally before or totally after eachother then they conflict
                // it was done this way because there would be many more cases in the negation.
                if (!((start_time.isAfter(rhs.start_time) && end_time.isAfter(rhs.start_time)) ||
                      (rhs.start_time.isAfter(start_time) && rhs.end_time.isAfter(start_time)))) {
                        return true;
                    }
                break;
            }
        }
        return false;
    }
}
