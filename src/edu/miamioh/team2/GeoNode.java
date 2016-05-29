/* Daniel Balch
   CSE 201 B (Professor Ann Sobel)
   Term Project, GeoNode (container)
   2-21-2016
   
   This object stores the name, coordinates, and connecting (immediately adjacent) nodes of a 
   building where classes are held, or an intermediate node between buildings.*/

package edu.miamioh.team2;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class GeoNode {
	//private static final double LAT_CONV_FACTOR = 68.98788; // conversion factor for latitude to miles in Oxford
	//private static final double LONG_CONV_FACTOR = 53.44053; // conversion factor for longitude to miles in Oxford
   private static final double LAT_CONV_SLOPE = 0.0000075227272727272727272727272727273;
   private static final double LAT_CONV_INTERCEPT = 39.498865090909090909090909090909;
   private static final double LONG_CONV_SLOPE = 0.0000097465373961218836565096952908587;
   private static final double LONG_CONV_INTERCEPT = -84.739871463988919667590027700831;
   //private static final double PIXEL_X_SCALER = 1926.6666666666666666666666666667; // conversion factor for miles to x-pixels on the map pic.
   //private static final double PIXEL_Y_SCALER = 1537.8947368421052631578947368421; // conversion factor for miles to y-pixels on the map pic.
   private static final double EARTH_RADIUS = 3956.5913;
   private static final int MIN_X_COORD = 50;
   private static final int MAX_X_COORD = 2990;
   private static final int MIN_Y_COORD = 70;
   private static final int MAX_Y_COORD = 1616;
	private boolean isIntermediate;
	private String name;
	private int[] coordinates; // map coordinates (x,y) in pixels
	private Set<String> connectingNodes;
	
	// constructs object with no connecting nodes (object will need connecting nodes later on)
	// Requirements: Coordinates array has two entries; 1st coordinate is a valid latitude
	// (in degrees); 2nd coordinate is a valid longitude (in degrees)
	public GeoNode(String name, boolean isIntermediate, int[] coordinates)
	{
		if (coordinates.length != 2) {
			throw new IllegalArgumentException("Coordinates array is of illegal length.");
		}
      boolean badX = ((coordinates[0] < MIN_X_COORD) || (coordinates[0] > MAX_X_COORD));
      boolean badY = ((coordinates[1] < MIN_Y_COORD) || (coordinates[1] > MAX_Y_COORD));
		if (badX || badY) {
			throw new IllegalArgumentException("Illegal map coordinate values for: " + name + " (x: " + coordinates[0] + ", y: " + coordinates[1] + ")");
		}
		this.isIntermediate = isIntermediate;
		this.name = name;
		this.coordinates = coordinates;
		this.connectingNodes = new HashSet<String>();
	}
	
	// constructs object with all connecting nodes pre-defined
	// Requirements: Coordinates array has two entries; 1st coordinate is a valid latitude
	// (in degrees); 2nd coordinate is a valid longitude (in degrees); connecting nodes do not
	// contain this node
	public GeoNode(String name, boolean isIntermediate, int[] coordinates, Set<String> connections)
	{
		if (coordinates.length != 2) {
			throw new IllegalArgumentException("Coordinates array is of illegal length.");
		}
      boolean badX = ((coordinates[0] < MIN_X_COORD) || (coordinates[0] > MAX_X_COORD));
      boolean badY = ((coordinates[1] < MIN_Y_COORD) || (coordinates[1] > MAX_Y_COORD));
		if (badX || badY) {
			throw new IllegalArgumentException("Illegal map coordinate values for: " + name + " (x: " + coordinates[0] + ", y: " + coordinates[1] + ")");
		}
		if (connections.contains(name)) {
			throw new IllegalArgumentException("Node connections include itself.");
		}
		this.isIntermediate = isIntermediate;
		this.name = name;
		this.coordinates = coordinates;
		this.connectingNodes = connections;
	}
	
	// allows additional connecting nodes to be added
	// Requirements: connections cannot contain parent node
	public void addConnections(Set<String> connections)
	{
		if (connections.contains(this.name)) {
			throw new IllegalArgumentException("Node connections include itself.");
		}
		this.connectingNodes.addAll(connections);
	}
	
	// adds a single additional connection node
	// Requirement: connection cannot have same name as this node
	public void addConnection(String connection)
	{
		if (this.name.equals(connection)) {
			throw new IllegalArgumentException("Node cannot be connected to itself.");
		}
		this.connectingNodes.add(connection);
	}
	
	// returns set of connecting nodes
	public Set<String> getConnections()
	{
		Set<String> result = new HashSet<String>();
		for (String n : this.connectingNodes) {
			result.add(n);
		}
		return result;
	}
	
	// returns name of node
	public String getName()
	{
		return this.name;
	}
	
	// returns whether node is an intermediate node or not
	public boolean tellWhetherIntermediate()
	{
		return this.isIntermediate;
	}
	
	// returns map picture coordinates of the node
	public int[] getCoordinates()
	{
		return Arrays.copyOf(this.coordinates, this.coordinates.length);
	}
   
   // returns the approximate latitude and longitude of the current node
   public double[] getLatLong()
   {
      double[] result = new double[2];
      double x = ((double) this.coordinates[0]);
      double y = ((double) this.coordinates[1]);
      result[0] = ((LAT_CONV_SLOPE * x) + LAT_CONV_INTERCEPT);
      result[1] = ((LONG_CONV_SLOPE * y) + LONG_CONV_INTERCEPT);
      return result;
   }

	// returns the distance (in miles) to another node (may be inaccurate for short distances)
	public double calcDistance(GeoNode other)
   {
		double[] latLongA = this.getLatLong();
		double[] latLongB = other.getLatLong();
		double lat1 = (latLongA[0] * Math.PI / 180);
		double lat2 = (latLongB[0] * Math.PI / 180);
		double long1 = (latLongA[1] * Math.PI / 180);
		double long2 = (latLongB[1] * Math.PI / 180);
		double numerator = (Math.sqrt(Math.pow((Math.cos(lat2) * Math.sin(long2 - long1)), 2) + Math.pow(((Math.cos(lat1) * Math.sin(lat2)) - (Math.sin(lat1) * Math.cos(lat2) * Math.cos(long2 - long1))), 2)));
		double denominator = ((Math.sin(lat1) * Math.sin(lat2)) + (Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - long1)));
		double centralAngle = Math.atan2(numerator, denominator);
		double result = centralAngle * EARTH_RADIUS;
		return result;
	}
   
   // returns the unit vector of the direction to another node (based on picture coordinates)
   public double[] calcUnitVectorTo(GeoNode other)
   {
	   double[] result = new double[2];
	   int[] coord2 = other.getCoordinates();
	   double deltaX = ((double) (coord2[0]-this.coordinates[0]));
	   double deltaY = ((double) (coord2[1]-this.coordinates[1]));
	   double divisor = Math.sqrt((Math.pow(deltaX, 2)) + (Math.pow(deltaY, 2)));
	   result[0] = (deltaX / divisor);
	   result[1] = (deltaY / divisor);
	   return result;
   }
   
   // returns the distance to another node in pixels
   public double calcPixelDistance(GeoNode other)
   {
	   int[] coord2 = other.getCoordinates();
	   double x1 = ((double) this.coordinates[0]);
	   double y1 = ((double) this.coordinates[1]);
	   double x2 = ((double) coord2[0]);
	   double y2 = ((double) coord2[1]);
	   double xTerm = Math.pow((x2 - x1), 2);
	   double yTerm = Math.pow((y2 - y1), 2);
	   return Math.sqrt((xTerm + yTerm));
   }
}