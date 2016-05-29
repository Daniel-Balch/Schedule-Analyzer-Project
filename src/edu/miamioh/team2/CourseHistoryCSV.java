package edu.miamioh.team2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// CSV Reader for student's past course history
public class CourseHistoryCSV {

	 // Holds all past courses
	public static ArrayList<String> hist = new ArrayList<String>();
	public static int totalCreditHours = 0;
	
	// CSV Reader for major requirements
		public static void readCSV(String file) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(file));
			// Holds credit hours from first line
			int hours = Integer.parseInt(br.readLine());
			String line = "";

			while ((line = br.readLine()) != null) {
				try {
					// Read in first line (total credit hours)
					totalCreditHours = hours;
					// Read in next lines (actual course history)
					hist.add(line);
				} catch (Exception e) {
					e.getMessage();
				}
			}
			// Test prints
		   	System.out.println(hist.size());
		   	for(int i = 0; i < hist.size(); i++) {
		   		System.out.println(hist.get(i));
		   	}
			br.close(); // Close BufferedReader
		} // end readCSV
} // end CourseHistoryCSV
