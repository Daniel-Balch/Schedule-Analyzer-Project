package edu.miamioh.team2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

public class ScheduleBuilder extends JPanel {

	//private JPanel contentPane;
	private JTable table;
	private JCheckBox chckbxShowHidden;
	
	private	Vector<String>			columnNames;
	private	Vector<Vector<String>>	allDataValues;
	private	Vector<Vector<String>>	dataValues;
	private final NonEditableModel tableModel;
	JFrame topFrame;

	/**
	 * Create the frame.
	 */
	public ScheduleBuilder(JFrame frame) {
		topFrame = frame;
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1314, 900); //816
		setLayout(null);
		//contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//setContentPane(contentPane);
		//contentPane.setLayout(null);
		
		final ArrayList<Course> schedule = new ArrayList<Course>();
		final ArrayList<JButton> classes = new ArrayList<JButton>();
		
		final JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(93, 194, 1195, 614); // 258 454
		add(panel);
		panel.setLayout(null);
		
		JSeparator separator = new JSeparator();
		separator.setBackground(Color.WHITE);
		separator.setBounds(239, 0, 2, 614);
		panel.add(separator);
		separator.setForeground(Color.GRAY);
		separator.setOrientation(SwingConstants.VERTICAL);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setForeground(Color.GRAY);
		separator_1.setBackground(Color.WHITE);
		separator_1.setBounds(478, 0, 2, 614);
		panel.add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		separator_2.setForeground(Color.GRAY);
		separator_2.setBackground(Color.WHITE);
		separator_2.setBounds(717, 0, 2, 614);
		panel.add(separator_2);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		separator_3.setForeground(Color.GRAY);
		separator_3.setBackground(Color.WHITE);
		separator_3.setBounds(956, 0, 2, 614);
		panel.add(separator_3);

		JButton btnNewButton = new JButton("Final Exam");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(schedule);
				CSV_Reader.uncombinedList = schedule;
				CSV_Reader.combineList();
				JFrame frame = new JFrame();
				frame.setPreferredSize(new Dimension(1314, 900));
				frame.setContentPane(new ExamScheduleMenu());
				frame.getContentPane().setBackground(new Color(214, 0, 39));
				frame.pack();
				frame.revalidate();
				frame.repaint();
				frame.setVisible(true);
//				 //setContentPane(new ExamScheduleMenu()); // swap out the content for the exam menu content
//	            setContentPane(new ScheduleBuilder()); //James
//	            //getContentPane().setBackground(new Color(214, 0, 39)); // set the exam menu background color
//
//	            pack();
//	            revalidate();
//	            repaint();
			}
		});
		btnNewButton.setBounds(1138, 814, 150, 38);
		add(btnNewButton);
		
		JButton button = new JButton("Show Paths");
		button.setFont(new Font("Tahoma", Font.PLAIN, 16));
		button.setBounds(10, 814, 150, 38); //728
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Add code to call Desmond's Thing
				PathFinder database = new PathFinder();
				Set<String> allBuildings = database.getBuildingNames();
				System.out.println("Number of available buildings: " + allBuildings.size());
				List<String> buildingsList = new ArrayList<String>();
				Display painter = new Display();
				for (String n : allBuildings) {
					buildingsList.add(n);
				}
				List<String> origins = new ArrayList<String>();
				List<String> destinations = new ArrayList<String>();
				List<int[]> allX = new ArrayList<int[]>();
				List<int[]> allY = new ArrayList<int[]>();
				List<Double> allWalkingTimes = new ArrayList<Double>();
				List<Double> betweenClassTimes = new ArrayList<Double>();
				int i = 0;
				boolean onDestination = false;
				ArrayList<Course> org = new ArrayList<Course>();
				ArrayList<Course> des = new ArrayList<Course>();
				setBuildings(org, des, schedule);

				System.out.println(origins);
				System.out.println(destinations);
				
				for(int ind=0; ind<org.size(); ind++) {
					origins.add(org.get(ind).building);
					destinations.add(des.get(ind).building);
					betweenClassTimes.add((double) (minuteTime(des.get(ind).start_time_str)-minuteTime(org.get(ind).end_time_str)));
					database.updateTerminals(org.get(ind).building, des.get(ind).building);
					Set<GeoPath> paths = database.getPaths();
					allWalkingTimes.add(database.getWalkTime());
					for (GeoPath n : paths) {
						GeoNode[] currentPathArray = n.getPathArray();
						int[] currentX = new int[currentPathArray.length];
						int[] currentY = new int[currentPathArray.length];
						for (int j = 0; j < currentPathArray.length; j++) {
							GeoNode currentNode = currentPathArray[j];
							int[] currentCoordinates = currentNode.getCoordinates();
							int[] scaledCoordinates = Display.getScaledCoordinates(currentCoordinates);
							currentX[j] = scaledCoordinates[0];
							currentY[j] = scaledCoordinates[1];
						}
						allX.add(currentX);
						allY.add(currentY);
					}
				}
				
				
//				while (i <= 5) {//NUM_REPS
//					//String randomBuilding = buildingsList.get(r.nextInt(buildingsList.size()));
////					while (origins.contains(randomBuilding) || destinations.contains(randomBuilding)) {
////						randomBuilding = buildingsList.get(r.nextInt(buildingsList.size()));
////					}
//					if (onDestination) {
//						destinations.add(randomBuilding);
//						betweenClassTimes.add((double) (r.nextInt(30))); //MAX_BETWEEN_CLASS_TIME
//						if ((origins.size() == destinations.size()) && (i < origins.size())) {
//							System.out.println("Updataing");
//							database.updateTerminals(origins.get(i), destinations.get(i));
//							Set<GeoPath> paths = database.getPaths();
//							allWalkingTimes.add(database.getWalkTime());
//							for (GeoPath n : paths) {
//								GeoNode[] currentPathArray = n.getPathArray();
//								int[] currentX = new int[currentPathArray.length];
//								int[] currentY = new int[currentPathArray.length];
//								for (int j = 0; j < currentPathArray.length; j++) {
//									GeoNode currentNode = currentPathArray[j];
//									int[] currentCoordinates = currentNode.getCoordinates();
//									int[] scaledCoordinates = Display.getScaledCoordinates(currentCoordinates);
//									currentX[j] = scaledCoordinates[0];
//									currentY[j] = scaledCoordinates[1];
//								}
//								allX.add(currentX);
//								allY.add(currentY);
//							}
//						}
//						i++;
//					} else {
//						origins.add(randomBuilding);
//					}
//					onDestination = !onDestination;
//				}
				//System.out.println(origins);
				//for (int p = 0; p < origins.size(); p++) {
				//	originBuildings.add(origins.get(p));
				//destinationBuildings.add(destinations.get(p));
				//}
				//originBuildings.addAll(origins);
				//destinationBuildings.addAll(destinations);
				painter.setX(allX);
				painter.setY(allY);
				painter.setWalkingTimes(allWalkingTimes);
				painter.setTimesBetweenClasses(betweenClassTimes);
				painter.setEndpointBuildings(origins, destinations);
				JFrame frame = new JFrame();
				//Display display = new Display();
				frame.setBounds(0,0,(1300 + 20),700); //DEFAULT_PANEL_WIDTH
				frame.add(painter);
				frame.setVisible(true);
				//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
		add(button);
		
		JButton load = new JButton("Load Schedule");
		load.setFont(new Font("Tahoma", Font.PLAIN, 16));
		load.setBounds(489, 814, 150, 38); //728
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader("schedule.csv"));
					// Holds credit hours from first line
					String line = "";
					schedule.clear();
					while ((line = br.readLine()) != null) {
						try {
							String parsed[] = line.split(",");
							int startTimeHour = Integer.parseInt(parsed[6].substring(0, 2));
				        	int endTimeHour = Integer.parseInt(parsed[7].substring(0, 2));
				        	if (parsed[6].substring(6, 8).equals("PM")){
				        		startTimeHour+=12;
				        	}
				        	if (parsed[7].substring(6, 8).equals("PM")){
				        		endTimeHour+=12;
				        	}
							Course temp = new Course(Integer.parseInt(parsed[0]),
									parsed[1],
        							Integer.parseInt(parsed[2]),
        							parsed[3],
        							parsed[4],
        							Integer.parseInt(parsed[5]),
        							0,
        							0,
        							parsed[6],
        							startTimeHour,
        							Integer.parseInt(parsed[6].substring(3, 5)),
        							parsed[7],
        							endTimeHour,
        							Integer.parseInt(parsed[7].substring(3, 5)),
        							parsed[8],
        							parsed[9], 
        							Integer.parseInt(parsed[10]), 
        							parsed[11]);
							schedule.add(temp);
						} catch (Exception e) {
							e.getMessage();
						}
					}
					// Test prints
					br.close(); // Close BufferedReader
					addClasses(classes, schedule, panel);
	        		filterClasses(schedule);
	        		tableModel.setDataVector(dataValues, columnNames);
	        		table.repaint();
	        		System.out.println(schedule);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(topFrame,
						    "File Not Found",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		add(load);
		
		JButton export = new JButton("Export Schedule");
		export.setFont(new Font("Tahoma", Font.PLAIN, 16));
		export.setBounds(659, 814, 150, 38); //728
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PrintWriter writer = null;
				try{
					writer = new PrintWriter("schedule.csv", "UTF-8");
					for(int i=0; i<schedule.size(); i++) {
						writer.println(schedule.get(i).crn + "," + schedule.get(i).subject+ "," +
								schedule.get(i).course + "," + schedule.get(i).section + ",\"" + schedule.get(i).title + "\"," + 
								schedule.get(i).credits + "," + schedule.get(i).start_time_str + "," + schedule.get(i).end_time_str + "," + 
								schedule.get(i).days + "," + schedule.get(i).building + "," + schedule.get(i).room + ",\"" + 
								schedule.get(i).instructor + "\",");
					}
				} catch(FileNotFoundException e) {
		            e.printStackTrace();
		        } catch(SecurityException e) {
		            e.printStackTrace();
		        } catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
		            // always close the output stream
		            if(writer != null){
		            	writer.close();
		            }
		        }
				JOptionPane.showMessageDialog(topFrame,
					    "Your schedule has been exported.",
					    "Export",
					    JOptionPane.PLAIN_MESSAGE);
			}
		});
		add(export);
		
		JLabel lblShowHidden = new JLabel("<html><body>Show<br>Hidden</body></html>");
		lblShowHidden.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblShowHidden.setBounds(6, 54, 34, 23);
		add(lblShowHidden);
		
		JLabel lblAm = new JLabel("8 AM");
		lblAm.setBounds(49, 194, 34, 14); //258
		add(lblAm);
		
		JLabel lblAm_1 = new JLabel("9 AM");
		lblAm_1.setBounds(49, 234, 34, 14); // +45
		add(lblAm_1);
		
		JLabel lblAm_2 = new JLabel("10 AM");
		lblAm_2.setBounds(49, 274, 34, 14);
		add(lblAm_2);
		
		JLabel lblAm_3 = new JLabel("11 AM");
		lblAm_3.setBounds(49, 314, 34, 14);
		add(lblAm_3);
		
		JLabel lblAm_4 = new JLabel("12 AM");
		lblAm_4.setBounds(49, 354, 34, 14);
		add(lblAm_4);
		
		JLabel lblPm = new JLabel("1 PM");
		lblPm.setBounds(49, 394, 34, 14);
		add(lblPm);
		
		JLabel lblPm_1 = new JLabel("2 PM");
		lblPm_1.setBounds(49, 434, 34, 14);
		add(lblPm_1);
		
		JLabel lblPm_2 = new JLabel("3 PM");
		lblPm_2.setBounds(49, 474, 34, 14);
		add(lblPm_2);
		
		JLabel lblPm_3 = new JLabel("4 PM");
		lblPm_3.setBounds(49, 514, 34, 14);
		add(lblPm_3);
		
		JLabel lblPm_4 = new JLabel("5 PM");
		lblPm_4.setBounds(49, 554, 34, 14);
		add(lblPm_4);
		
		JLabel lblPm_5 = new JLabel("6 PM");
		lblPm_5.setBounds(49, 594, 34, 14);
		add(lblPm_5);
		
		JLabel lblPm_6 = new JLabel("7 PM");
		lblPm_6.setBounds(49, 634, 34, 14);
		add(lblPm_6);
		
		JLabel lblPm_7 = new JLabel("8 PM");
		lblPm_7.setBounds(49, 674, 34, 14);
		add(lblPm_7);
		
		JLabel lblPm_8 = new JLabel("9 PM");
		lblPm_8.setBounds(49, 714, 34, 14);
		add(lblPm_8);
		
		JLabel lblPm_9 = new JLabel("10 PM");
		lblPm_9.setBounds(49, 754, 34, 14);
		add(lblPm_9);

		JLabel lblPm_10 = new JLabel("11 PM");
		lblPm_10.setBounds(49, 794, 34, 14);
		add(lblPm_10);
		
		JLabel lblNewLabel = new JLabel("Monday");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(93, 169, 239, 23);
		add(lblNewLabel);
		
		JLabel lblTuesday = new JLabel("Tuesday");
		lblTuesday.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTuesday.setHorizontalAlignment(SwingConstants.CENTER);
		lblTuesday.setBounds(332, 169, 239, 23);
		add(lblTuesday);
		
		JLabel lblWednesday = new JLabel("Wednesday");
		lblWednesday.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblWednesday.setHorizontalAlignment(SwingConstants.CENTER);
		lblWednesday.setBounds(571, 169, 239, 23);
		add(lblWednesday);
		
		JLabel lblThursday = new JLabel("Thursday");
		lblThursday.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblThursday.setHorizontalAlignment(SwingConstants.CENTER);
		lblThursday.setBounds(810, 169, 239, 23);
		add(lblThursday);
		
		JLabel lblFriday = new JLabel("Friday");
		lblFriday.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblFriday.setHorizontalAlignment(SwingConstants.CENTER);
		lblFriday.setBounds(1049, 169, 239, 23); // 233
		add(lblFriday);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(46, 0, 1242, 160); //224
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		chckbxShowHidden = new JCheckBox("");
		chckbxShowHidden.setBounds(10, 30, 21, 23);
		
		add(chckbxShowHidden);
		
		// Create columns
		CreateColumns();
		CreateData();
		
		
		
		// Create a new table instance
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setReorderingAllowed(false);
		filterClasses(schedule);
		tableModel = new NonEditableModel(dataValues, columnNames);
		table.setModel(tableModel);
		//table.setShowVerticalLines(true);
		//table.setShowHorizontalLines(true);
		table.setShowGrid(true);
		table.setRowSelectionAllowed( true );

		// Change the selection color
		table.setSelectionForeground( Color.white );
		table.setSelectionBackground( Color.red );
				
		
		table.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		        JTable table =(JTable) me.getSource();
		        Point p = me.getPoint();
		        int row = table.rowAtPoint(p);
		        if (me.getClickCount() == 2) {
		        	int startTimeHour = Integer.parseInt((""+table.getValueAt(row, 6)).substring(0, 2));
		        	int endTimeHour = Integer.parseInt((""+table.getValueAt(row, 7)).substring(0, 2));
		        	if ((""+table.getValueAt(row, 6)).substring(6, 8).equals("PM") && startTimeHour!=12){
		        		startTimeHour+=12;
		        	}
		        	if ((""+table.getValueAt(row, 7)).substring(6, 8).equals("PM") && endTimeHour!=12){
		        		endTimeHour+=12;
		        	}
//		        	System.out.println(temp);
		        	Course temp = new Course(Integer.parseInt(""+table.getValueAt(row, 0)),
		        							""+table.getValueAt(row, 1),
		        							Integer.parseInt(""+table.getValueAt(row, 2)),
		        							""+table.getValueAt(row, 3),
		        							""+table.getValueAt(row, 4),
		        							Integer.parseInt(""+table.getValueAt(row, 5)),
		        							0,
		        							0,
		        							""+table.getValueAt(row, 6),
		        							startTimeHour,
		        							Integer.parseInt((""+table.getValueAt(row, 6)).substring(3, 5)),
		        							""+table.getValueAt(row, 7),
		        							endTimeHour,
		        							Integer.parseInt((""+table.getValueAt(row, 7)).substring(3, 5)),
		        							""+table.getValueAt(row, 8),
		        							(""+table.getValueAt(row, 9)).split("\\s+")[0], 
		        							Integer.parseInt((""+table.getValueAt(row, 9)).split("\\s+")[1]), 
		        							""+table.getValueAt(row, 10));
		        	if(makeChecks(schedule, temp, false)) {
		        		System.out.println(temp);
		        		ArrayList<Course> l = new ArrayList<Course>();
		        		boolean flag = true;
		        		for(int i=0; i<dataValues.size(); i++) {
		    				if(temp.crn == Integer.parseInt(dataValues.get(i).get(0)) && !temp.days.equals(dataValues.get(i).get(8))) {
		    					System.out.println("found");
		    					
		    					startTimeHour = Integer.parseInt(dataValues.get(i).get(6).substring(0, 2));
		    		        	endTimeHour = Integer.parseInt(dataValues.get(i).get(7).substring(0, 2));
		    		        	System.out.println(startTimeHour);
		    		        	if (dataValues.get(i).get(6).substring(6, 8).equals("PM") && startTimeHour!=12){
		    		        		startTimeHour+=12;
		    		        	}
		    		        	if (dataValues.get(i).get(7).substring(6, 8).equals("PM") && endTimeHour!=12){
		    		        		endTimeHour+=12;
		    		        	}
		    		        	System.out.println(temp);
			    				Course add = new Course(Integer.parseInt(dataValues.get(i).get(0)),
			    						dataValues.get(i).get(1),
	        							Integer.parseInt(dataValues.get(i).get(2)),
	        							dataValues.get(i).get(3),
	        							dataValues.get(i).get(4),
	        							Integer.parseInt(dataValues.get(i).get(5)),
	        							0,
	        							0,
	        							dataValues.get(i).get(6),
	        							startTimeHour,
	        							Integer.parseInt(dataValues.get(i).get(6).substring(3, 5)),
	        							dataValues.get(i).get(7),
	        							endTimeHour,
	        							Integer.parseInt(dataValues.get(i).get(7).substring(3, 5)),
	        							dataValues.get(i).get(8),
	        							dataValues.get(i).get(9).split("\\s+")[0], 
	        							Integer.parseInt(dataValues.get(i).get(9).split("\\s+")[1]), 
	        							dataValues.get(i).get(10));
			    				l.add(add);
			    				System.out.println(add);
			    				if(!makeChecks(schedule, add, true)) {
			    					flag = false;
			    				}
		    				}
		    			}
		        		if(flag) {
		        			schedule.add(temp);
		        			schedule.addAll(l);
		        		}
		        		addClasses(classes, schedule, panel);
		        		filterClasses(schedule);
		        		tableModel.setDataVector(dataValues, columnNames);
		        		table.repaint();
//		        		if(schedule.size()>4){
//		        			setBuildings(schedule);
//		        		}
		        	}
		        	
		        }
		    }
		});
		
		JScrollPane scrollPane = table.createScrollPaneForTable(table);
		
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		chckbxShowHidden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				filterClasses(schedule);
        		tableModel.setDataVector(dataValues, columnNames);
        		table.repaint();
			}
		});
		
	}
	
	protected void setBuildings(ArrayList<Course> org,
			ArrayList<Course> des, ArrayList<Course> schedule) {
		// TODO Auto-generated method stub
		ArrayList<Course> unSorted = new ArrayList<Course>();
		unSorted.addAll(schedule);
		ArrayList<Course> sorted = new ArrayList<Course>();
		Course earliest;
		while(!unSorted.isEmpty()){
			earliest = unSorted.get(0);
			int ind = 0;
			for(int i=1; i<unSorted.size(); i++) {
				if(minuteTime(earliest.start_time_str)>minuteTime(unSorted.get(i).start_time_str)){
					earliest = unSorted.get(i);
					ind = i;
				}
			}
			unSorted.remove(ind);
			sorted.add(earliest);
		}
		String days = "MTWRF";
//		ArrayList<Course> org = new ArrayList<Course>();
//		ArrayList<Course> des = new ArrayList<Course>();
		for(int i=0; i<days.length(); i++) {
			Course temp = null;
			for(int j=0; j<sorted.size(); j++){
				if(sorted.get(j).days.indexOf(days.charAt(i)) != -1){
					if(temp == null){
						temp = sorted.get(j);
					} else {
						System.out.println(temp);
						System.out.println(sorted.get(j));
						org.add(temp.clone());
						des.add(sorted.get(j));
						temp = sorted.get(j);
					}
				}
			}
		}
//		System.out.println(org);
//		System.out.println(des);
		
		for(int i=0; i<org.size()-1; i++) {
			for(int j=i+1; j<org.size(); j++) {
				if(org.get(i).crn == org.get(j).crn && des.get(i).crn == des.get(j).crn){
					org.remove(j);
					des.remove(j);
					j--;
				}
			}
		}
//		for(int i=0; i<org.size(); i++){
//			origins.add(org.get(i).building);
//			destinations.add(des.get(i).building);
//		}
	}

	private void filterClasses(ArrayList<Course> schedule) {
		// TODO Auto-generated method stub
		dataValues = new Vector<Vector<String>>();
		
		if(!chckbxShowHidden.isSelected()){
			for(int i=0; i<allDataValues.size(); i++) {
				boolean flag = true;
				String subj = allDataValues.get(i).get(1);
				int course = Integer.parseInt(allDataValues.get(i).get(2));
				for(int j=0; j<schedule.size(); j++) {
					Course temp = schedule.get(j);
					if(temp.subject.equals(subj) && temp.course == course) {
						flag = false;
					}
				}
				
				//Check to see if they meet the prereqs
				for(int j=0; j<PreReqReader.preReqs.size(); j++) {
					if(PreReqReader.preReqs.get(j).course.equals(subj + " " + course)){
						if(CourseHistoryCSV.totalCreditHours<PreReqReader.preReqs.get(j).hoursNeeded) {
							//you do not meet the required number of hours for this course
							flag = false;
						}
						if(flag){
							boolean tempFlag = false;
							for(int k=0; k<PreReqReader.preReqs.get(j).pre.size(); k++){
								for(int l=0; l<CourseHistoryCSV.hist.size(); l++) {
									if(PreReqReader.preReqs.get(j).pre.get(k).equals(CourseHistoryCSV.hist.get(l))){
										tempFlag = true;
									}
								}
							}
							flag = flag && tempFlag;
						}
					}
				}
				
				if(flag)
					dataValues.add(allDataValues.get(i));
			}
		} else {
			dataValues.addAll(allDataValues);
		}
	}

	protected int minuteTime(String s){
		int hour = Integer.parseInt(s.substring(0,2));
		System.out.println("ADDING" + hour);
		if(s.substring(6,8).equals("PM") && hour!=12)
			hour += 12;
		return hour*60+Integer.parseInt(s.substring(3,5));
	}
	
	protected boolean sameDay(Course c1, Course c2) {
		for(int i=0; i<c1.days.length(); i++){
			if(c2.days.indexOf(c1.days.charAt(i))!=-1){
				return true;
			}
		}
		return false;
	}
	
	protected boolean makeChecks(ArrayList<Course> schedule, Course add, boolean lab) {
		// TODO Auto-generated method stub
		int sTime = minuteTime(add.start_time_str);
		int eTime = minuteTime(add.end_time_str);
		for(int i=0; i<schedule.size(); i++){
			Course temp = schedule.get(i);
			if(temp.subject.equals(add.subject) && temp.course == add.course){
				JOptionPane.showMessageDialog(topFrame,
					    "You have already signed up for this class.",
					    "Error Can't Add Course",
					    JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(sameDay(temp, add)){
				System.out.println(minuteTime(temp.start_time_str)<=sTime);
				System.out.println(minuteTime(temp.end_time_str)>=sTime);
				System.out.println(minuteTime(temp.start_time_str)>=eTime);
				System.out.println(minuteTime(temp.end_time_str)<=eTime);
				if((minuteTime(temp.start_time_str)<=sTime && minuteTime(temp.end_time_str)>=sTime) || 
						(minuteTime(temp.end_time_str)>=eTime && minuteTime(temp.end_time_str)<=eTime)) {
					JOptionPane.showMessageDialog(topFrame,
						    "You have already signed up for a class at this time.",
						    "Error Can't Add Course",
						    JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
			}
		}
		
		//Check to see if they meet the prereqs
		for(int i=0; i<PreReqReader.preReqs.size(); i++) {
			if(PreReqReader.preReqs.get(i).course.equals(add.subject + " " + add.course)){
				boolean flag = false;
				if(CourseHistoryCSV.totalCreditHours<PreReqReader.preReqs.get(i).hoursNeeded) {
					//you do not meet the required number of hours for this course
					JOptionPane.showMessageDialog(topFrame,
						    "You Do not meet the requirements for this course.",
						    "Error Can't Add Course",
						    JOptionPane.ERROR_MESSAGE);
					return false;
				}
				for(int j=0; j<PreReqReader.preReqs.get(i).pre.size(); j++){
					for(int k=0; k<CourseHistoryCSV.hist.size(); k++) {
						if(PreReqReader.preReqs.get(i).pre.get(j).equals(CourseHistoryCSV.hist.get(k))){
							flag = true;
						}
					}
				}
				if(!flag) {
					JOptionPane.showMessageDialog(topFrame,
						    "You Do not meet the requirments for this course.",
						    "Error Can't Add Course",
						    JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		if(!lab){
			//Check if it meets a requirement
			boolean flag = false;
			for(int i=0; i<ReqCSV_Reader.list.size(); i++) {
				//System.out.println(ReqCSV_Reader.list.get(i).getName() + ": " + ReqCSV_Reader.list.get(i).getHoursNeeded());
				if(ReqCSV_Reader.list.get(i).getHoursNeeded()>0){
					
					for(int j=0; j<ReqCSV_Reader.list.get(i).getCrs().size(); j++) {
						if(ReqCSV_Reader.list.get(i).getCrs().get(j).name.equals(add.subject + " " + add.course)){
							flag = true;
						}
					}
				}
			}
			if(!flag) {
				int ans = JOptionPane.showConfirmDialog(topFrame,
					    "This class does not meet any major requirments that you have left to complete." + 
					    " Do you still want to sign up for it?",
					    "Warning",
					    JOptionPane.YES_NO_OPTION);
				if(ans == JOptionPane.YES_OPTION){
					return true;
				} else {
					return false;
				}
			}
		}
		
		return true;
	}

	protected void addClasses(final ArrayList<JButton> classes, final ArrayList<Course> schedule, final JPanel panel) {
		for(int i=0; i<classes.size(); i++) {
			panel.remove(classes.get(i));
		}
		for(int j=0; j<schedule.size(); j++) {
			for(int i=0; i<schedule.get(j).days.length(); i++) {
	    		int x;
	    		switch (schedule.get(j).days.charAt(i)) {
	    			case 'M':
	    				x=10;
	    				break;
	    			case 'T':
	    				x=249;
	    				break;
	    			case 'W':
	    				x=488;
	    				break;
	    			case 'R':
	    				x=727;
	    				break;
	    			case 'F':
	    				x=966;
	    				break;
	    			default:
	    				x=-1;
	    				break;
	    		}
	    		JButton tmpButton = new JButton(getClassButtonText(schedule.get(j)));
	        	tmpButton.setFont(new Font("Tahoma", Font.PLAIN, 9));
	        	tmpButton.setBounds(x, getY(schedule.get(j).start_time_str), 219, 
	        			getHeight(schedule.get(j).start_time_str,""+schedule.get(j).end_time_str));
	        	tmpButton.addActionListener(new ActionListener() {
	    			public void actionPerformed(ActionEvent arg0) {
	    				JButton buttonThatWasClicked = (JButton)arg0.getSource();
	    				removeClass(schedule, buttonThatWasClicked.getText());
	    				panel.remove(buttonThatWasClicked);
	    				addClasses(classes, schedule, panel);
	    				filterClasses(schedule);
		        		tableModel.setDataVector(dataValues, columnNames);
		        		table.repaint();
	    			}
	    		});
	        	System.out.println("ADDEDABUTTON at y: " + getY(schedule.get(j).start_time_str));
	    		classes.add(tmpButton);
	    		panel.add(tmpButton);
	    	}
			
		}
		panel.repaint();
	}

	protected void removeClass(ArrayList<Course> schedule, String text) {
		String firstLine[] = text.substring(20).split("<br>")[0].split("\\s+");
		String subj = firstLine[0];
		int course = Integer.parseInt(firstLine[1]);
		String sec = firstLine[2];
		for(int i=0; i<schedule.size(); i++) {
			Course temp = schedule.get(i);
			if(temp.subject.equals(subj) && temp.course == course && temp.section.equals(sec)) {
				schedule.remove(i);
				i--;
				//break;
			}
		}
	}

	public String getClassButtonText(Course crs) {
		String firstLine = crs.subject + " " + crs.course + " " + crs.section;
		String secondLine = crs.start_time_str + " - " + crs.end_time_str;
		String thirdLine = "" + crs.building + " " + crs.room;
		return "<html><body><center>"+ firstLine +"<br>" + secondLine +"<br>" + thirdLine + "</center></body></html>";
	}
	
	public int getHeight(String start, String end) {
		int startTime = minuteTime(start);//Integer.parseInt(start.substring(0, 2))*60 + Integer.parseInt(start.substring(3, 5));
		int endTime = minuteTime(end); //Integer.parseInt(end.substring(0, 2))*60 + Integer.parseInt(end.substring(3, 5));
		return (int) Math.ceil(40*((endTime-startTime)/60.0));
	}
	
	public int getY(String start) {
		int startTime = minuteTime(start);
//		if(start.substring(6, 8).equals("AM")){
//			startTime = Integer.parseInt(start.substring(0, 2))*60 + Integer.parseInt(start.substring(3, 5)) - 480;
//		} else {
//			startTime = (Integer.parseInt(start.substring(0, 2))+12)*60 + Integer.parseInt(start.substring(3, 5)) - 480;
//		}
		return (int) Math.ceil(40*(startTime-480)/60.0);
	}
	
	public void CreateColumns()
	{
		// Create column string labels
		columnNames = new Vector<String>();

		columnNames.add("CRN");
		columnNames.add("Subject");
		columnNames.add("Course");
		columnNames.add("Section");
		columnNames.add("Title");
		columnNames.add("Hours");
		columnNames.add("Start Time");
		columnNames.add("End Time");
		columnNames.add("Days");
		columnNames.add("Location");
		columnNames.add("Instructor");
	}

	public void CreateData()
	{
		// Create data for each element
		allDataValues = new Vector<Vector<String>>();
		for( int iY = 0; iY < CSV_Reader.courseList.size(); iY++ )
		{
			Vector<String> temp = new Vector<String>();
			temp.add(""+CSV_Reader.uncombinedList.get(iY).crn);
			temp.add(CSV_Reader.uncombinedList.get(iY).subject);
			temp.add(""+CSV_Reader.uncombinedList.get(iY).course);
			temp.add(CSV_Reader.uncombinedList.get(iY).section);
			temp.add(CSV_Reader.uncombinedList.get(iY).title);
			temp.add(""+CSV_Reader.uncombinedList.get(iY).credits);
			temp.add(CSV_Reader.uncombinedList.get(iY).start_time_str);
			temp.add(""+CSV_Reader.uncombinedList.get(iY).end_time_str);
			temp.add(CSV_Reader.uncombinedList.get(iY).days);
			temp.add(CSV_Reader.uncombinedList.get(iY).building + " " + CSV_Reader.uncombinedList.get(iY).room);
			temp.add(CSV_Reader.uncombinedList.get(iY).instructor);
			allDataValues.add(temp);
		}
	}
}
