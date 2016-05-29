package edu.miamioh.team2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// CSV Reader for student's past course history
public class PreReqReader {

	 // Holds all past courses
	public static ArrayList<PreReq> preReqs = new ArrayList<PreReq>();
	
	// CSV Reader for major requirements
		public static void readCSV(String file) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(file));
			// Holds credit hours from first line
			String line = "";

			while ((line = br.readLine()) != null) {
				try {
					String parsed[] = line.split(",");
					ArrayList<String> pre = new ArrayList<String>();
					for(int i=2; i<parsed.length; i++) {
						pre.add(parsed[i]);
					}
					PreReq temp = new PreReq(parsed[0], Integer.parseInt(parsed[1]), pre);
					preReqs.add(temp);
				} catch (Exception e) {
					e.getMessage();
				}
			}
			// Test prints
			System.out.println(preReqs.size());
			br.close(); // Close BufferedReader
		} // end readCSV
} // end CourseHistoryCSV
