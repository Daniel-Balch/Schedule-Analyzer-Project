package edu.miamioh.team2;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import edu.miamioh.team2.CombinedCourse;
import edu.miamioh.team2.Course;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The table of classes displayed in a popup menu
 */
public class Table extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JTable table;
	private JPanel panel;
	
	// whether or not this table found a conflict in its list
	private boolean hasConflict;

	public Table(ArrayList<CombinedCourse> combinedCourses) {
		// sets up the Jframe for the table
	    hasConflict = false;
		frame = new JFrame("Final Exam Conflict Resolution");
		frame.setLayout(new BorderLayout());


		// uncombine the combined courses for displaying in the table
		ArrayList<Course> courses = new ArrayList<>();
		for (int i = 0; i < combinedCourses.size(); ++i) {
			courses.addAll(combinedCourses.get(i).courses);
		}
		
		// Read in the course array and write it to the table 
		table = new JTable(courses.size(), 10);
		table.setEnabled(false);
		for (int i = 0; i < courses.size(); i++) {

		    // set the column headers
			String header[] = { "CRN", "Subject", "Course", "Section", "Title",
					"Credits", "Start Time", "End Time", "Days", "Instructor" };

			for (int j = 0; j < 10; j++) {
				TableColumn column1 = table.getTableHeader().getColumnModel()
						.getColumn(j);

				column1.setHeaderValue(header[j]);
				
			}

			table.setValueAt(courses.get(i).crn, i, 0);
			table.setValueAt(courses.get(i).subject, i, 1);
			table.setValueAt(courses.get(i).course, i, 2);
			table.setValueAt(courses.get(i).section, i, 3);
			table.setValueAt(courses.get(i).title, i, 4);
			table.setValueAt(courses.get(i).credits, i, 5);
			table.setValueAt(courses.get(i).start_time, i, 6);
			table.setValueAt(courses.get(i).end_time, i, 7);
			table.setValueAt(courses.get(i).days, i, 8);
			table.setValueAt(courses.get(i).instructor, i, 9);
		}

		// panel for the conflict buttons
		panel = new JPanel();
		JScrollPane bottomScrollPane = new JScrollPane(panel);
		bottomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.setBackground(Color.white);
		frame.add(bottomScrollPane, BorderLayout.SOUTH);
		
		//sets all of the formatting and colors of the table frame
		frame.getContentPane().setBackground(Color.white);
		table.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.white);
		frame.add(scrollPane, BorderLayout.CENTER);

		frame.setPreferredSize(new Dimension(800, 300));
		frame.pack();

		// find conflicts
		// if two classes in this exam block don't intersect then you can take both so they conflict
		for (int i = 0; i < combinedCourses.size(); ++i) {
			for (int j = i + 1; j < combinedCourses.size(); ++j) {
				if (!combinedCourses.get(i).intersects(combinedCourses.get(j))) {
					addConflict(combinedCourses.get(i).courses.get(0).crn,
							combinedCourses.get(j).courses.get(0).crn);
				}
			}
			//addConflict(combinedCourses.get(i).courses.get(0).crn,combinedCourses.get(i).courses.get(0).crn);
		}
	}

	/**
	 * Open the window
	 */
	public void open() { frame.setVisible(true); }
	/**
	 * Check if this table has a conflict in its list
	 */
	public boolean hasConflict() { return hasConflict; }
	
	/**
	 * Adds a conflict button for any classes that conflict with each other
	 */
	private void addConflict(final int crn1, final int crn2) {
	    hasConflict = true; // set the status variable
	
		System.out.println("Conflict: " + crn1 + ", " + crn2);
		JPanel subPanel = new JPanel();
		JButton button = new JButton(Integer.toString(crn1) + ", "
				+ Integer.toString(crn2));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				highlightPair(crn1, crn2);
			}
		});
		button.setBackground(Color.red);
		
		button.setForeground(Color.white);
		subPanel.add(button);
		subPanel.setBackground(Color.white);
		panel.add(subPanel);
	}

	/**
	 * Highlight any rows with crn1 or crn2 using a selection (as if you had
	 * shift clicked)
	 * 
	 * This is used to show the classes with conflicts
	 */
	public void highlightPair(int crn1, int crn2) {
		table.clearSelection();
		for (int i = 0; i < table.getRowCount(); ++i) {
			int crn = ((Integer) table.getValueAt(i, 0)).intValue();
			if (crn1 == crn || crn2 == crn) {
				table.addRowSelectionInterval(i, i);
			}
		}
	}
}
