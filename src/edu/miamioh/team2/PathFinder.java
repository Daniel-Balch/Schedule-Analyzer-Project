/* Daniel Balch
   CSE 201 B (Professor Ann Sobel)
   Term Project, Path Manager
   2-21-2016
   
   This object takes an origin and a destination as parameters (and, optionally a node database file)
   and finds the shortest path(s) and distance between the origin and destination. It requires that a
   valid node database file is present in the same working directory.*/

package edu.miamioh.team2;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

public class PathFinder {
   private GeoNode origin;
   private GeoNode destination;
   private Map<String, GeoNode> allNodes;
   private Set<GeoPath> shortestPaths;
   private boolean fullyInitialized;
   private double distance;   // shortest distance (in miles)
   private double pixelDistance;   // shortest distance (in pixels)
   private static final String DEFAULT_IN_FILENAME = "NodeList.dat";
   private static final double DEFAULT_SPEED = 3.1;   // default walking speed (in mph)
   private static final double MIN_AWAY_THRESHOLD = 1.0;  // min. threshold for pixel distance traveled away from a destination for a path
   private static final double MAX_AWAY_THRESHOLD = 3434.0;  // max. threshold for pixel distance traveled away from a destination for a path
   private static final double AWAY_THRESHOLD_STARTING_MULTIPLIER = 1.005;
   private static final double AWAY_THRESHOLD_MULTIPLIER_INCREMENT = 0.005;
   
   // constructs partially initialized PathFinder with default input file
   // Requirement: valid input file
   public PathFinder()
   {
      File inFile = new File(DEFAULT_IN_FILENAME);
      if (!inFile.exists()) {
         throw new IllegalStateException("Default input file not found.");
      }
      this.origin = null;
      this.destination = null;
      this.allNodes = getAllNodes(inFile);
      this.shortestPaths = new HashSet<GeoPath>();
      this.fullyInitialized = false;
      this.distance = 0;
      this.pixelDistance = 0;
   }   

   // constructs partially initialized PathFinder with specified input file
   // Requirement: valid input file
   public PathFinder(String inputFilename)
   {
      File inFile = new File(inputFilename);
      if (!inFile.exists()) {
         inFile = new File(DEFAULT_IN_FILENAME);
         if (!inFile.exists()) {
            throw new IllegalStateException("Neither specified nor default input file could be found.");
         }   
      }
      this.origin = null;
      this.destination = null;
      this.allNodes = getAllNodes(inFile);
      this.shortestPaths = new HashSet<GeoPath>();
      this.fullyInitialized = false;
      this.distance = 0;
      this.pixelDistance = 0;
   }   
      
   // constructs path finder with default input file
   // Requirements: valid names of origin and destination
   public PathFinder(String originName, String destinationName)
   {
      File inFile = new File(DEFAULT_IN_FILENAME);
      if (!inFile.exists()) {
         throw new IllegalStateException("Default input file not found.");
      }
      this.allNodes = getAllNodes(inFile);
      updateTerminals(originName, destinationName);
      this.fullyInitialized = true;
   }
   
   // constructs path finder with specified input file (or default if specified file can't be found)
   // Requirements: valid names of origin and destination; valid input filename is desirable
   public PathFinder(String originName, String destinationName, String inputFilename)
   {
      File inFile = new File(inputFilename);
      if (!inFile.exists()) {
         inFile = new File(DEFAULT_IN_FILENAME);
         if (!inFile.exists()) {
            throw new IllegalStateException("Neither specified nor default input file could be found.");
         }   
      }
      this.allNodes = getAllNodes(inFile);
      updateTerminals(originName, destinationName);
      this.fullyInitialized = true;
   }
   
   // tells whether the object has been fully initialized (i.e. has a specified origin/destination)
   public boolean tellWhetherFullyInitialized()
   {
      return this.fullyInitialized;
   }   
   
   // update origin and destination (calculates new paths and updates distance as well)
   // Requirements: origin and destination node names must be valid and present in input file
   public void updateTerminals(String originName, String destinationName)
   {
	  if ((!this.allNodes.containsKey(originName)) || (!this.allNodes.containsKey(destinationName))) {
	      throw new IllegalArgumentException("Data for origin and/or destination node not found.");
	   }
	   this.origin = allNodes.get(originName);
	   this.destination = allNodes.get(destinationName);
	   if (originName.equals(destinationName)) {
		   GeoNode[] preNonPath = new GeoNode[1];
		   preNonPath[0] = this.origin;
		   GeoPath nonPath = new GeoPath(preNonPath);
		   Set<GeoPath> onlyPath = new HashSet<GeoPath>();
		   onlyPath.add(nonPath);
		   this.shortestPaths = onlyPath;
		   this.distance = 0;
		   this.pixelDistance = 0;
	   } else {
		   GeoNode[] blankNodeArray = new GeoNode[0];
		   GeoPath emptyPath = new GeoPath(blankNodeArray);
		   int numPaths = 0;
		   double awayThreshold = MIN_AWAY_THRESHOLD;
		   double multiplier = AWAY_THRESHOLD_STARTING_MULTIPLIER;
		   while ((numPaths == 0) && (awayThreshold <= MAX_AWAY_THRESHOLD)) {
			   double[] awayDistances = new double[2];
			   awayDistances[0] = 0.0;
			   awayDistances[1] = awayThreshold;
			   this.shortestPaths = getShortestPaths(this.origin, this.destination, emptyPath, awayDistances);
			   numPaths = getNumValidPaths();
			   awayThreshold *= multiplier;
			   multiplier += AWAY_THRESHOLD_MULTIPLIER_INCREMENT;
         }
         if (shortestPaths.size() > 0) {
		     if (shortestPaths.size() > 1) {
		    	 this.optimizePathSet();
		     }
        	 Iterator<GeoPath> itr = shortestPaths.iterator();
		      GeoPath currentPath = itr.next();
		      this.distance = currentPath.getDistance();
		      this.pixelDistance = currentPath.getPixelDistance();
         } else {
            this.distance = 0;
            this.pixelDistance = 0;
         }      
	   }
      this.fullyInitialized = true;
   }
   
   public int getNumValidPaths()
   {
      int result = 0;
      for (GeoPath n : this.shortestPaths) {
         GeoNode[] current = n.getPathArray();
         if (current.length >= 2) {
            boolean correctOrigin = this.origin.getName().equals(current[0].getName());
            boolean correctDestination = this.destination.getName().equals(current[(current.length - 1)].getName());
            if (correctOrigin && correctDestination) {
               result++;
            }
         }
      }
      return result;
   }            
   
   // similar to updateTerminals method above, but only changes origin
   public void updateOrigin(String originName)
   {
	   updateTerminals(originName, this.destination.getName());
   }
   
   // similar to updateTerminals method above, but only changes destination
   public void updateDestination(String destinationName)
   {
	   updateTerminals(this.origin.getName(), destinationName);
   }
   
   // returns approximate distance of shortest path (in mph)
   public double getDistance()
   {
      return this.distance;
   }
   
   public double getPixelDistance()
   {
	   return this.pixelDistance;
   }
   
   // returns estimated walking time in minutes (using default speed)
   public double getWalkTime()
   {
      return ((this.distance / DEFAULT_SPEED) * 60.0);
   }
   
   // returns estimated walking time in minutes (using specified speed)
   public double getWalkTime(double speed)
   {
      if (speed <= 0.0) {
         throw new IllegalArgumentException("Impossible value for specified walking speed.");
      }   
      return ((this.distance / speed) * 60.0);
   }   
   
   // returns shortest path(s) in a HashSet
   public Set<GeoPath> getPaths()
   {
      Set<GeoPath> result = new HashSet<GeoPath>();
      for (GeoPath n : this.shortestPaths) {
         result.add(n.getCopy());
      }   
      return result;
   }
   
   // returns names of origin and destination in an array of strings
   public String[] getEdgeNodeNames()
   {
      String[] result = new String[2];
      if (this.fullyInitialized) {
         result[0] = this.origin.getName();
         result[1] = this.destination.getName();
      }   
      return result;
   }
   
   // reads in names, connections, and coordinates for all nodes from input file
   private Map<String, GeoNode> getAllNodes(File inFile)
   {
	  Map<String, GeoNode> result = new HashMap<String, GeoNode>();
	  try {
		  Scanner input = new Scanner(inFile);
	      List<GeoNode> nodeList = new ArrayList<GeoNode>();
	      String name = "";
	      Set<String> connections = new HashSet<String>();
	      int[] coordinates = new int[2];
	      boolean isIntermediate = false;
	      int nameCount = 0;
	      while (input.hasNextLine()) {
	    	  String line = input.nextLine();
	    	  boolean isValid = checkValidLine(line);
	    	  if (isValid) {
	    		  if (line.contains(":")) {
	    			  String[] lineSegs = line.split(":");
	    			  String nameSeg = lineSegs[0];
	    			  String connSeg = lineSegs[1];
	    			  if (nameCount > 0) {
	    				  GeoNode freshNode = new GeoNode(name, isIntermediate, coordinates, connections);
	    				  nodeList.add(freshNode);
	    			     name = "";
	    			     connections = new HashSet<String>();
	    			     coordinates = new int[2];
	    			     isIntermediate = false;
	    			  }
	    			  isIntermediate = nameSeg.contains("<");
	    			  int begin;
	    			  int end;
	    			  if (isIntermediate) {
	    				  begin = nameSeg.indexOf('<');
	    				  end = nameSeg.indexOf('>');
	    			  } else {
	    				  begin = nameSeg.indexOf('[');
	    				  end = nameSeg.indexOf(']');
	    			  }
	    			  name = nameSeg.substring((begin+1), end);
	    			  nameCount++;
	    			  String[] connSegs = connSeg.split(";");
	    			  for (String n : connSegs) {
	    				  connections.add(n);
	    			  }
	    		  } else {
	    			  String[] preCoordinates = line.split(",");
	    			  coordinates[0] = Integer.parseInt(preCoordinates[0]);
	    			  coordinates[1] = Integer.parseInt(preCoordinates[1]);
	    		  }
	    	  }
	    	  if ((nameCount > 0) && (!input.hasNextLine())) {
	    		  GeoNode freshNode = new GeoNode(name, isIntermediate, coordinates, connections);
				  nodeList.add(freshNode);
	    	  }
	      }
	      for (GeoNode n : nodeList) {
	    	  String currentName = n.getName();
	    	  result.put(currentName, n);
	      }
	      input.close();
	  } catch (Exception e) {}
      return result;
   }
   
   // checks whether a line from the input file is usable input for the getAllNodes method
   private boolean checkValidLine(String line)
   {
	   if (line.contains(":") || line.contains(",")) {
		   String filteredLine = removeCharacters(line, " \t");
		   if (filteredLine.contains(":")) {
			   String[] lineSegs = filteredLine.split(":");
			   if (lineSegs.length < 2) {
				   return false;
			   } else {
				   String name = lineSegs[0];
				   String connections = lineSegs[1];
				   int[] arrowCounts = new int[2];
				   int[] bracketCounts = new int[2];
				   int netLength = 0;
				   for (int i = 0; i < name.length(); i++) {
					   char currentChar = name.charAt(i);
					   boolean pastLeft = ((arrowCounts[0] > 0) || (bracketCounts[0] > 0));
					   boolean beforeRight = ((arrowCounts[1] == 0) && (bracketCounts[1] == 0));
					   if (beforeRight) {
						   if (pastLeft) {
							   if (currentChar == '>') {
								   arrowCounts[1]++;
							   } else if (currentChar == ']') {
								   bracketCounts[1]++;
							   } else {
								   netLength++;
							   }
						   } else if (currentChar == '[') {
							   bracketCounts[0]++;
						   } else if (currentChar == '<') {
							   arrowCounts[0]++;
						   }
					   } else {
						   break;
					   }
				   }
				   boolean arrowPair = ((arrowCounts[0] >= 1) && (arrowCounts[1] >= 1));
				   boolean noArrows = ((arrowCounts[0] == 0) && (arrowCounts[1] == 0));
				   boolean bracketPair = ((bracketCounts[0] >= 1) && (bracketCounts[1] >= 1));
				   boolean noBrackets = ((bracketCounts[0] == 0) && (bracketCounts[1] == 0));
				   boolean validIntermediateName = (arrowPair && noBrackets && (netLength > 0));
				   boolean validTerminalName = (bracketPair && noArrows && (netLength > 0));
				   boolean validName = (validIntermediateName || validTerminalName);
				   int badSemicolons = 0;
				   int netConnectLength = 0;
				   for (int i = 0; i < connections.length(); i++) {
					   char currentChar = connections.charAt(i);
					   if ((i == 0) || (i == (connections.length() - 1))) {
						   if (currentChar == ';') {
							   badSemicolons++;
						   } else {
							   netConnectLength++;
						   }
					   } else {
						   if (currentChar == ';') {
							   boolean leftSemicolon = (connections.charAt(i-1) == ';');
							   boolean rightSemicolon = (connections.charAt(i+1) == ';');
							   if (leftSemicolon || rightSemicolon) {
								   badSemicolons++;
							   }
						   } else {
							   netConnectLength++;
						   }
					   }
				   }
				   boolean validConnections = ((badSemicolons == 0) && (netConnectLength > 0));
				   return (validName && validConnections);
			   }
		   } else {
			   int badCommas = 0;
			   int correctCommas = 0;
			   for (int i = 0; i < line.length(); i++) {
				   boolean isComma = (line.charAt(i) == ',');
				   boolean edge = ((i == 0) || (i == (line.length() - 1)));
				   if (isComma && edge) {
					   badCommas++;
				   } else if (isComma) {
					   correctCommas++;
				   }
			   }
			   if ((badCommas == 0) && (correctCommas >= 1)) {
				   String[] lineSegs = line.split(",");
				   if (lineSegs.length >= 2) {
					   for (int i = 0; i < lineSegs.length; i++) {
						   String preNumber = lineSegs[i];
						   try {
							   Double.parseDouble(preNumber);
						   } catch (Exception e) {
							   return false;
						   }
					   }
					   return true;
				   } else {
					   return false;
				   }
			   } else {
				   return false;
			   }
		   }
	   } else {
		   return false;
	   }
   }
   
   // removes characters from a string
   private String removeCharacters(String original, String badCharacters) {
	   String result = "";
	   for (int i = 0; i < original.length(); i++) {
		   String digit = original.substring(i, (i+1));
		   if (!badCharacters.contains(digit)) {
			   result += digit;
		   }
	   }
	   return result;
   }
   
   // returns a set of all building node names
   public Set<String> getBuildingNames()
   {
      Set<String> result = new HashSet<String>();
      Set<String> nodeKeys = this.allNodes.keySet();
      for (String n : nodeKeys) {
         GeoNode current = this.allNodes.get(n);
         if (!current.tellWhetherIntermediate()) {
            result.add(n);
         }
      }
      return result;
   }
   
   // calculates the shortest paths between the origin and destination nodes
   private Set<GeoPath> getShortestPaths(GeoNode origin, GeoNode destination, GeoPath previous, double[] sumAwayDistances)
   {
	   Set<GeoPath> paths = new HashSet<GeoPath>();
	   Set<String> connectNames = origin.getConnections();
	   if (connectNames.contains(destination.getName())) {
		   GeoNode[] pair = new GeoNode[2];
		   pair[0] = origin;
		   pair[1] = destination;
		   GeoPath shortPath = new GeoPath(pair);
		   paths.add(shortPath);
	   } else {
		   GeoPath currentPath = previous.getCopy();
		   sumAwayDistances[0] += getAwayDistance(previous, origin, destination);
		   currentPath.addNode(origin);
		   boolean atOrBelowThresholds = (sumAwayDistances[0] <= sumAwayDistances[1]);
		   boolean correctPathStructure = checkPathStructure(currentPath);
		   if (atOrBelowThresholds && correctPathStructure) {
           String[] sortedConnectNames = sortConnectionsByVector(origin, destination, connectNames);
   		   for (int j = 0; j < sortedConnectNames.length; j++) {
   			   String currentName = sortedConnectNames[j];
   			   if (this.allNodes.containsKey(currentName) && !currentPath.hasNodeByName(currentName)) {
   				   GeoNode currentNode = this.allNodes.get(currentName);
   				   Set<GeoPath> potentialPaths = getShortestPaths(currentNode, destination, currentPath, sumAwayDistances);
   				   Iterator<GeoPath> itr = potentialPaths.iterator();
   				   while (itr.hasNext()) {
   					   GeoPath currentPathCandidate = itr.next();
   					   currentPathCandidate.insertStartNode(origin);
   					   double currentCandidateDistance = currentPathCandidate.getPixelDistance();
   					   Iterator<GeoPath> itr2 = paths.iterator();
   					   int betterPathCount = 0;
   					   while (itr2.hasNext()) {
   						   GeoPath currentComparisonPath = itr2.next();
   						   double comparisonDistance = currentComparisonPath.getPixelDistance();
   						   if (comparisonDistance < currentCandidateDistance) {
   							   betterPathCount++;
   							   break;
   						   } else if (comparisonDistance > currentCandidateDistance) {
   							   itr2.remove();
   						   }
   					   }
   					   if (betterPathCount == 0) {
   						   paths.add(currentPathCandidate);
   					   }
   				   }
   			   }
   		   	}
         }   
	   }
      return paths;
   }
   
   // checks to see if path structure follows desired format (buildings at the ends, waypoints in between)
   private boolean checkPathStructure(GeoPath current)
   {
      GeoNode[] currentArray = current.getPathArray();
      String structure = "";
      boolean previousState = false;
      for (int i = 0; i < currentArray.length; i++) {
         GeoNode currentNode = currentArray[i];
         boolean isWaypoint = currentNode.tellWhetherIntermediate();
         if ((i == 0) || (isWaypoint != previousState)) {
            if (isWaypoint) {
               structure += "W";
            } else {
               structure += "B";
            }
         }
         previousState = isWaypoint;
      }
      return (structure.equals("B") || structure.equals("BW") || structure.equals("BWB"));
   }      
                  
   
   // get current distance traveled away from destination (or 0 if not getting further away)
   private double getAwayDistance(GeoPath previous, GeoNode origin, GeoNode destination)
   {
      double result = 0;
      GeoNode[] previousArray = previous.getPathArray();
      if (previousArray.length > 0) {
         GeoNode previousEndpoint = previousArray[(previousArray.length - 1)];
         if (previousEndpoint.getName().equals(origin.getName())) {
            throw new IllegalStateException();
         }   
         double previousDistance = previousEndpoint.calcPixelDistance(destination);
         double currentDistance = origin.calcPixelDistance(destination);
         if (currentDistance > previousDistance) {
            result = Math.abs(currentDistance - previousDistance);
         }
      }
      return result;   
   }         
   
   // removes non-optimal paths found by getShortestPaths method, if it somehow failed
   private void optimizePathSet()
   {
	   int unequalCount = 0;
	   int pathsProcessed = 0;
	   double prevDistance = 0;
	   double minDistance = 0;
	   Iterator<GeoPath> itr = this.shortestPaths.iterator();
	   while (itr.hasNext()) {
		   GeoPath current = itr.next();
		   double currentDistance = current.getPixelDistance();
		   if (pathsProcessed == 0) {
			   minDistance = currentDistance;
		   } else if (prevDistance != currentDistance) {
			   unequalCount++;
			   minDistance = Math.min(minDistance, currentDistance);
		   }
		   pathsProcessed++;
		   prevDistance = currentDistance;
	   }
	   if (unequalCount > 0) {
		   Set<GeoPath> updatedPaths = new HashSet<GeoPath>();
		   itr = this.shortestPaths.iterator();
		   while (itr.hasNext()) {
			   GeoPath current = itr.next();
			   double currentDistance = current.getPixelDistance();
			   if (currentDistance <= minDistance) {
				   updatedPaths.add(current);
			   }
		   }
		   this.shortestPaths = updatedPaths;
	   }
   }
   
   // sorts connecting nodes (for getShortestPaths method) by how close they are to the correct direction
   private String[] sortConnectionsByVector(GeoNode origin, GeoNode destination, Set<String> connectNames)
   {
      double[] idealVector = origin.calcUnitVectorTo(destination);
      Map<String, Double> vectorDeviations = new HashMap<String, Double>();
      Map<Double, String> reverseVectorDeviations = new TreeMap<Double, String>();
      for (String n : connectNames) {
         GeoNode current = this.allNodes.get(n);
         double[] currentVector = origin.calcUnitVectorTo(current);
         double deviation = Math.sqrt(((Math.pow((currentVector[0]-idealVector[0]), 2)) + (Math.pow((currentVector[1]-idealVector[1]), 2))));
         vectorDeviations.put(n, deviation);
         reverseVectorDeviations.put(deviation, n);
      }
      String[] results = new String[connectNames.size()];
      Set<Double> reverseKeys = reverseVectorDeviations.keySet();
      Iterator<Double> itr = reverseKeys.iterator();
      int i = 0;
      while (itr.hasNext() && (i < results.length)) {
         double currentNum = itr.next();
         if (connectNames.size() == reverseVectorDeviations.keySet().size()) {
            results[i] = reverseVectorDeviations.get(currentNum);
            i++;
         } else {
            for (String n : vectorDeviations.keySet()) {
               double otherNum = vectorDeviations.get(n);
               if ((currentNum == otherNum) && (i < results.length)) {
                  results[i] = n;
                  i++;
               }
            }
         }      
      }
      return results;
   }
}