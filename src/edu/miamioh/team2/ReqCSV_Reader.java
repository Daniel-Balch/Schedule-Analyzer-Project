// Created by Will and Jimmy
package edu.miamioh.team2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.miamioh.team2.Requirements.CoursePair;

// Modified CSV_Reader to automatically import major requirements
public class ReqCSV_Reader {

	// ArrayList holding all required courses
	public static ArrayList<Requirements> list = new ArrayList<Requirements>();
	
	// CSV Reader for major requirements
	public static void readCSV(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = "";

		while ((line = br.readLine()) != null) {
			// Split by commas
			String[] splitLine = line.split(",");
			// Read as long as splitLine is valid
			if (splitLine.length != 0 && splitLine[0] != "") {
				try {
					// Requirements object holder
					Requirements req = new Requirements();
					// Read in name
					req.setName(splitLine[0]);
					// Read in course hours needed 
					req.setHoursNeeded(Integer.parseInt(splitLine[1]));
					
					// Adds courses to the Requirements classes' ArrayList
					// Jumps every 2 columns if multiple courses present 
					for(int i = 2; i < splitLine.length; i += 2){
						req.setCrs(new CoursePair(splitLine[i], Integer.parseInt(splitLine[i+1])));
					}
					// Append Requirements to main ArrayList
					list.add(req);
					
				} catch (Exception e) {
					e.getMessage();
				}
			}
		}
		// Test prints
//	   	System.out.println();
//	   	for(int i = 0; i < list.size(); i++) {
//	   		System.out.println(list.get(i).getName());
//	   		System.out.println(list.get(i).getHoursNeeded());
//	   		System.out.println(list.get(i).getCrs());
//	   		System.out.println();
//	   	}
		br.close(); // Close BufferedReader
	} // end readCSV
} // end ReqCSV_Reader
