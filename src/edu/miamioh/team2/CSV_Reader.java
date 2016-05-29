package edu.miamioh.team2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Loads the CSV file into Course objects in a public array list
 */
public class CSV_Reader {
    // the final combined list of courses
    public static ArrayList<CombinedCourse> courseList = new ArrayList<>();
    // the raw, unconbined, list of courses directly from the CSV file

	public static ArrayList<Course> uncombinedList = new ArrayList<Course>();

	// used for converting time formats, should probably not be global but at
	// least we made them private for you (sorry)
	private static int time_hours = 0;
	private static int time_minutes = 0;
	private static String time_string;

	/**
	 * Read a file and fill the uncombined list with every entry in the file
	 *
	 * @param file
	 * @throws IOException
	 */
	public static void readCSVfile(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = "";

		br.readLine();

		// for every line of the file, a Course object will be created and each
		// variable is assigned accordingly
		while ((line = br.readLine()) != null) {
			// split by commas, ignoring the commas in a title
			String[] splitLine = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			if (splitLine[0] != "") {
				try {
					// each variable in Course object is assigned
					// change this if the format of csv file changes!
					int crn = Integer.parseInt(splitLine[0]);
					String subject = splitLine[1];
					int course = Integer.parseInt(splitLine[2]);
					String section = splitLine[3];
					// remove spaces
					section = section.replace(" ", "");
					String title = splitLine[4];
					// remove quotes from title
					title = title.replace("\"", "");
					int credits = Integer.parseInt(splitLine[5]);
					int enrolled = Integer.parseInt(splitLine[6]);
					int max_enrolled = Integer.parseInt(splitLine[7]);
					int start_time = Integer.parseInt(splitLine[8]);
					// splits the time into a minutes and a hours variable
					parseTime(start_time);
					String start_time_str = time_string;
					int start_time_hours = time_hours;
					int start_time_minutes = time_minutes;
					int end_time = Integer.parseInt(splitLine[9]);
					// splits the time into a minutes and a hours variable
					parseTime(end_time);
					int end_time_hours = time_hours;
					int end_time_minutes = time_minutes;
					String days = splitLine[10];
					String building = splitLine[11];
					int room = Integer.parseInt(splitLine[12]);
					String instructor = splitLine[13];
					// remove quotes around instructor name
					instructor = instructor.replace("\"", "");

					// As long as the variable days is not null then a new
					// object is created and added to a list
					if (days != "" || days != null) {
						Course newCourse = new Course(crn, subject, course,
								section, title, credits, enrolled,
								max_enrolled, start_time_str, start_time_hours,
								start_time_minutes, time_string,
								end_time_hours, end_time_minutes, days,
								building, room, instructor);
						System.out.println(newCourse);
						uncombinedList.add(newCourse);

					}
				} catch (Exception e) {

				}
			}
		}

		br.close();
		combineList();
	}

	/**
	 * Find any courses in the uncombined list that have the same crn and
	 * combine them
	 */
	public static void combineList() {
	    // sort by crn so the ones that need combining are next to eachother
		courseList = new ArrayList<>();
	    Collections.sort(uncombinedList, new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return Integer.compare(o1.crn, o2.crn);
            }
	    });
	
	    // combine if we see ones with the same CRN in a row
	    int lastCRN = -1;
	    CombinedCourse nextCombined = null;
	    for (int i = 0; i < uncombinedList.size(); ++i) {
	        if (uncombinedList.get(i).crn != lastCRN) {
	            nextCombined = new CombinedCourse();
	            lastCRN = uncombinedList.get(i).crn;
	            courseList.add(nextCombined);
	        }
	        nextCombined.courses.add(uncombinedList.get(i));
	    }
	}
	
	/**
	 * Convert the time format from the csv file to a format we want
	 * @param milTime military time integer fromat of the time (sorry)
	 *                eg: 2:20pm -> 1420
	 */
	private static void parseTime(int milTime) {
		Date date = null;

		try {
			date = new SimpleDateFormat("HHmm").parse(String.format("%04d",
					milTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("HH mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");

		String parse = sdf.format(date);
		String[] split = parse.split(" ");

		time_hours = Integer.parseInt(split[0]);
		time_minutes = Integer.parseInt(split[1]);
		time_string = sdf2.format(date);
	}

} // end csv_reader
