package edu.miamioh.team2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

/**
 * The first window that allows you to select a major
 */
public class FileLoadMainMenu extends JFrame {
    //Just a random number to get rid of the warning
    private static final long serialVersionUID = 5984762144776633825L;
    private JButton seButton;
    private JButton csButton;
    private JLabel infoText;
    public String fileName = "";

    public FileLoadMainMenu() {
        initComponents();
    }

    private void initComponents() {
        infoText = new JLabel();
        seButton = new JButton();
        csButton = new JButton();

        getContentPane().setBackground(new Color(214, 0, 39));
        infoText.setForeground(Color.white);
        seButton.setBackground(Color.white);
        csButton.setBackground(Color.white);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Will changed
        // Prompts user to select their major
        infoText.setText("Please select your major.");
        
        // Software Engineering button
        seButton.setText("Software Engineering");
        seButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });
        // Computer Science button
        csButton.setText("Computer Science");
        csButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	submitActionPerformed(evt);
            }
        });
        
        // done with a GUI builder application
        // if you want to change this probably just scrap all of this and start over (sorry again)
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
            .addGroup(layout.createSequentialGroup()
            .addComponent(seButton, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(csButton, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE))
            .addComponent(infoText, GroupLayout.PREFERRED_SIZE, 332, GroupLayout.PREFERRED_SIZE))))));

        layout.setVerticalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            .addComponent(infoText, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(seButton, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
            .addComponent(csButton, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
            .addContainerGap(27, Short.MAX_VALUE)));
     // End Will changed
        pack();
    }

    /**
     * Called when we finalize what file we want and press the submit button
     * @param evt
     */
    private void submitActionPerformed(ActionEvent evt) {
    	// Will: Hard coded the csv files in like she wanted
    	fileName = "CSE Course Data.csv";
    	String se = "Software Engineering.csv";
    	String cs = "Computer Science.csv";
        if (fileName != "") { // switch to the scheduling menu
            System.out.println("Go to Scheduling");
            setPreferredSize(new Dimension(1314, 900)); // resize the window

            try {
            	if(evt.getSource().equals(seButton)) {
            		CSV_Reader.readCSVfile(fileName); // read in the requirements csv
            		// read in the software engineering requirements csv
            		ReqCSV_Reader.readCSV(se); 
            		CourseHistoryCSV.readCSV("History.csv");
            		PreReqReader.readCSV("CSEPreReqList.csv");
            	} else {
            		CSV_Reader.readCSVfile(fileName); // read in the requirements csv
            		// read in the computer science requirements csv
            		ReqCSV_Reader.readCSV(cs); 
            		CourseHistoryCSV.readCSV("History.csv");
            		PreReqReader.readCSV("CSEPreReqList.csv");
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            for(int i=0; i<CourseHistoryCSV.hist.size(); i++) {
            	for(int j=0; j<ReqCSV_Reader.list.size(); j++) {
            		for(int k=0; k<ReqCSV_Reader.list.get(j).getCrs().size(); k++) {
            			if(CourseHistoryCSV.hist.get(i).equals(ReqCSV_Reader.list.get(j).getCrs().get(k).name)){
            				ReqCSV_Reader.list.get(j).setHoursNeeded(ReqCSV_Reader.list.get(j).getHoursNeeded()-ReqCSV_Reader.list.get(j).getCrs().get(k).hours);
            			}
            		}
            	}
            }

            //setContentPane(new ExamScheduleMenu()); // swap out the content for the exam menu content
            //getContentPane().setBackground(new Color(214, 0, 39)); // set the exam menu background color
            setContentPane(new ScheduleBuilder(this));

            pack();
            revalidate();
            repaint();
        } else {
            // no file selected
            infoText.setText("Error importing files");
        }
    }
  
    public static void main(String args[]) {
        // Set the Nimbus look and feel if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error setting look and feel");
            System.err.println(ex.getMessage());
        }

        new FileLoadMainMenu().setVisible(true);
    }
}
