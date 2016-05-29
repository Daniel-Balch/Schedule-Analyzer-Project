// Created by Will
package edu.miamioh.team2;
import java.util.ArrayList;

// Holds the different major's required classes by type
public class Requirements {

	// Identifying name of course blocks 
	private String sectionName;
	// Credit hours needed for block
	private int hrsNeeded;
	// Holds all courses needed, paired as course and credit hours
	private ArrayList<CoursePair> crs;
	
	// Constructor
	public Requirements() {
		sectionName = "";
		hrsNeeded = 0;
		crs = new ArrayList<CoursePair>();
	}

	// Setters
	public void setName(String s) {
		sectionName = s;
	}
	
	public void setHoursNeeded(int h) {
		hrsNeeded = h;
	}
	
	public void setCrs(CoursePair cp) {
		crs.add(new CoursePair(cp.name, cp.hours));
	}

	// Getters
	public String getName() {
		return sectionName;
	}
	
	public int getHoursNeeded() {
		return hrsNeeded;
	}
	
	public ArrayList<CoursePair> getCrs() {
		return crs;
	}

	// Class for the pair of course name and credit hours
	static class CoursePair {
			final String name;
			final int hours;
	 
	// Constructors
	public CoursePair() {
			name = "";
			hours = 0;
		}
	
	public CoursePair(String name, int hours) {
		 	this.name = name;
		 	this.hours = hours;
	  	}
	
	@Override
	public String toString() {
		return "Course Name: " + this.name + "\nCredit Hours: " + this.hours;
	}
	 
		} // End CoursePair
} // End Requirements
