package edu.miamioh.team2;

import java.util.ArrayList;

public class PreReq {
	String course;
	int hoursNeeded;
	ArrayList<String> pre;
	
	public PreReq(String c, int h, ArrayList<String> p){
		course = c;
		hoursNeeded = h;
		pre = p;
	}
}
