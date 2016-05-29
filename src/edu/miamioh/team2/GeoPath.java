/* Daniel Balch
   CSE 201 B (Professor Ann Sobel)
   Term Project, GeoPath (container)
   2-21-2016
   
   This object stores an array of GeoNodes and can calculate the cumulative distance
   between them.*/

package edu.miamioh.team2;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class GeoPath {
	private GeoNode[] path;
	private double distance;
	private double pixelDistance;
	
	// constructs object using an array of GeoNodes
	// Requirements: no repeating nodes in array;
	// adjacent nodes say they're connected
	public GeoPath(GeoNode[] path)
	{
		checkValid(path);
		this.path = path;
		this.distance = sumDistance();
		this.pixelDistance = sumPixelDistance();
	}
	
	// constructs object using a List of GeoNodes
	// Requirements: no repeating nodes in List;
	// adjacent nodes say they're connected
	public GeoPath(List<GeoNode> pathList)
	{
		GeoNode[] candidate = geoListToArray(pathList);
		checkValid(candidate);
		this.path = candidate;
		this.distance = sumDistance();
		this.pixelDistance = sumPixelDistance();
	}
   
   // converts a List of GeoNode objects to an array of them
	private GeoNode[] geoListToArray(List<GeoNode> original)
   {
      GeoNode[] result = new GeoNode[original.size()];
      for (int i = 0; i < original.size(); i++) {
         result[i] = original.get(i);
      }
      return result;
   }      
	
	// checks whether path candidate has repeats or not;
	// also checks if all nodes say they're connected;
	// throws IllegalArgumentException if repeats are found or connections missing
	private void checkValid(GeoNode[] candidate) 
	{
		List<String> nameList = new ArrayList<String>();
		Set<String> nameSet = new HashSet<String>();
		int missing = 0;
		for (int i = 0; i < candidate.length; i++) {
			GeoNode current = candidate[i];
			String currentName = current.getName();
			nameList.add(currentName);
			nameSet.add(currentName);
			Set<String> connections = current.getConnections();
			if (i > 0) {
				String prevName = candidate[(i-1)].getName();
				if (!connections.contains(prevName)) {
					missing++;
				}
			}
			if (i < (candidate.length - 1)) {
				String nextName = candidate[(i+1)].getName();
				if (!connections.contains(nextName)) {
					missing++;
				}
			}
		}
		boolean valid = ((nameList.size() == nameSet.size()) && (missing == 0));
		if (!valid) {
			throw new IllegalArgumentException("Illegal path.");
		}
	}
	
	// updates cumulative distance between nodes in path
	private double sumDistance()
	{
		double result = 0;
		if (path.length >= 2) {
			for (int i = 0; i < (this.path.length - 1); i++) {
				GeoNode current = this.path[i];
				GeoNode next = this.path[(i + 1)];
				double distance = current.calcDistance(next);
				result += distance;
			}
		}
		return result;
	}
	
	// updates cumulative pixel distance between nodes in path
	private double sumPixelDistance()
	{
		double result = 0;
		if (path.length >= 2) {
			for (int i = 0; i < (this.path.length - 1); i++) {
				GeoNode current = this.path[i];
				GeoNode next = this.path[(i + 1)];
				result += current.calcPixelDistance(next);
			}
		}
		return result;
	}
	
	// returns stored cumulative path distance in miles (does not update or recalculate)
	public double getDistance()
	{
		return this.distance;
	}
	
	// returns stored cumulative path distance in pixels (does not update or recalculate)
	public double getPixelDistance()
	{
		return this.pixelDistance;
	}
	
	// returns a copy of the path array
	public GeoNode[] getPathArray()
	{
		return Arrays.copyOf(this.path, this.path.length);
	}
	
	// returns a List of all GeoNodes in the path
	public List<GeoNode> getPathList()
	{
		List<GeoNode> result = new ArrayList<GeoNode>();
		for (int i = 0; i < this.path.length; i++) {
			result.add(this.path[i]);
		}
		return result;
	}
	
	// returns an array of just the names of the nodes in the path
	public String[] getNodeNames()
	{
		String[] result = new String[this.path.length];
		for (int i = 0; i < this.path.length; i++) {
			result[i] = this.path[i].getName();
		}
		return result;
	}
	
	// returns a List of the names of the nodes in the path
	public List<String> getNodeNameList()
	{
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < this.path.length; i++) {
			result.add(this.path[i].getName());
		}
		return result;
	}
	
	// adds a single GeoNode at the end of the path
	// Requirement 1: new node must not already exist in path
	// Requirement 2: new node must be connected to last node of existing path
	public void addNode(GeoNode other)
	{
		List<GeoNode> current = this.getPathList();
		current.add(other);
		GeoNode[] candidate = geoListToArray(current);
		this.checkValid(candidate);
		this.path = candidate;
		this.distance = sumDistance();
		this.pixelDistance = sumPixelDistance();
	}
	
	// adds a single GeoNode at the beginning of the path
	// Requirement 1: new node must not already exist in path
	// Requirement 2: new node must be connected to first node of existing path
	public void insertStartNode(GeoNode other) {
		List<GeoNode> current = this.getPathList();
		current.add(0, other);
		GeoNode[] candidate = geoListToArray(current);;
		this.checkValid(candidate);
		this.path = candidate;
		this.distance = sumDistance();
		this.pixelDistance = sumPixelDistance();
	}
	
	// adds another GeoPath at the tail end of this one
	// Requirement: new GeoPath does not share any nodes in common with this one
	public void addPath(GeoPath other)
	{
		String endNodeName = this.path[(path.length-1)].getName();
		List<GeoNode> current = getPathList();
		GeoNode[] extraNodes = other.getPathArray();
		for (int i = 0; i < extraNodes.length; i++) {
			if (i == 0) {
				extraNodes[i].addConnection(endNodeName);
			}
			current.add(extraNodes[i]);
		}
		GeoNode[] candidate = geoListToArray(current);
		checkValid(candidate);
		this.path = candidate;
		this.distance = sumDistance();
		this.pixelDistance = sumPixelDistance();
	}
	
	// returns a copy of this GeoPath object (to prevent modification of original)
	public GeoPath getCopy()
	{
		GeoNode[] pathArray = this.getPathArray();
		GeoPath result = new GeoPath(pathArray);
		return result;
	}
	
   // tells whether the path contains the given node
	public boolean hasNode(GeoNode candidate)
   {
		String candidateName = candidate.getName();
		return this.hasNodeByName(candidateName);
	}
   
   // tells whether the path contains a node with the given name
   public boolean hasNodeByName(String sampleName)
   {
   	for (GeoNode n : this.path) {
   		if (n.getName().equals(sampleName)) {
   			return true;
   		}
   	}
   	return false;
   }   
}
