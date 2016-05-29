package edu.miamioh.team2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

public class Display extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//  int howManyClasses = 4;
	//	ArrayList<int[]> x = new ArrayList<int[]>();
	//	ArrayList<int[]> y = new ArrayList<int[]>();
	//	ArrayList<double[]> walkingTime = new ArrayList<double[]>();
	//	ArrayList<double[]> timeBetweenClasses = new ArrayList<double[]>();
	private List<int[]> x;
	private List<int[]> y;
	private static List<String> originBuildings;
	private static List<String> destinationBuildings;
	private static List<Double> walkingTimes;
	private List<Double> allBetweenClassTimes;
	private List<Shape> pathStroke;
	//private java.awt.Shape pathStroke;
	private static final int NUM_REPS = 5;
	private static final double W_TIME_THRESHOLD = 5;
	private static final int MAX_BETWEEN_CLASS_TIME = 30;
	private static final int DEFAULT_PANEL_WIDTH = 1300;
	private static final int DEFAULT_PANEL_HEIGHT = 700;
	private static final int PICTURE_WIDTH = 3000;
	private static final int PICTURE_HEIGHT = 1671;
//
//	public static void main(String[] args) {
//		
//	}
	
	public static int[] getScaledCoordinates(int[] original)
	{
		double xScaler = (((double) (DEFAULT_PANEL_WIDTH + 20)) / ((double) PICTURE_WIDTH));
		double yScaler = (((double) (DEFAULT_PANEL_HEIGHT + 20)) / ((double) PICTURE_HEIGHT));
		double preX = Math.round((((double) original[0]) * xScaler));
		double preY = Math.round((((double) original[1]) * yScaler));
		int[] result = new int[2];
		if (preX > ((double) (DEFAULT_PANEL_WIDTH + 20))) {
			result[0] = (DEFAULT_PANEL_WIDTH + 20);
		} else {
			result[0] = ((int) preX);
		}
		if (preY > ((double) (DEFAULT_PANEL_HEIGHT + 20))) {
			result[1] = (DEFAULT_PANEL_HEIGHT + 20);
		} else {
			result[1] = ((int) preY);
		}
		return result;
	}

	public Display() {
		this.pathStroke = new ArrayList<Shape>();
		map();

		addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseMoved(MouseEvent e){
				//System.out.println(pathStroke.contains(e.getPoint()));
				boolean flag = false;
				System.out.println(pathStroke.size());
				System.out.println(pathStroke);
				System.out.println(originBuildings.size());
				for(int i = 0; i < originBuildings.size(); i++){
					if(pathStroke.get(i).contains(e.getPoint())) {
						setToolTipText(originBuildings.get(i) + "->"+ destinationBuildings.get(i));
						setToolTipText("Walking Time" + walkingTimes.get(i));
						flag = true;
						System.out.println("HEY" + pathStroke.size());
					}
				}
				if(!flag){
					setToolTipText(null);
					
				}
				ToolTipManager.sharedInstance().mouseMoved(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		}
				);

	}

	private void map() {
		setLayout(null);
		System.out.println("called");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// use generalpath in jpanel
		JPanel panel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				pathStroke = new ArrayList<Shape>();
				//setOpaque(false);
				GeneralPath path;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setStroke(new BasicStroke(5.5f));
				g2d.setColor(Color.BLACK);
				if (x.size() == 0) {
					System.out.println("No coordinates!");
				} else {
					System.out.println("Has coordinates.");
				}
				System.out.println("xSize: " + x.size());
				for (int i = 0; i < x.size(); i++) {
					int[] currentX = x.get(i);
					int[] currentY = y.get(i);
					path = new GeneralPath();
					if (currentX.length > 0) {
						path.moveTo(currentX[0], currentY[0]);
						for (int m = 1; m < currentX.length; m++) {
							path.lineTo(currentX[m], currentY[m]);
							path.moveTo(currentX[m],currentY[m]);
						}
						pathStroke.add((new BasicStroke(25)).createStrokedShape(path));
						//g2d.draw(path);
						g2d.setStroke(new BasicStroke(5));
						g2d.setColor(Color.BLACK);
						g2d.draw(path);
						g2d.setStroke(new BasicStroke(3));
						double currentWalkingTime;
						double timeBetweenClasses;
						if (i < walkingTimes.size()) {
							currentWalkingTime = walkingTimes.get(i);
						} else {
							currentWalkingTime = -1;
						}
						if (i < allBetweenClassTimes.size()) {
							timeBetweenClasses = allBetweenClassTimes.get(i);
						} else {
							timeBetweenClasses = 3.0;
						}
						if (Math.abs((currentWalkingTime - timeBetweenClasses)) <= W_TIME_THRESHOLD) {
							g2d.setColor(Color.YELLOW);
						} else if ((timeBetweenClasses - currentWalkingTime) < (-1*W_TIME_THRESHOLD)) {
							g2d.setColor(Color.RED);
						} else {
							g2d.setColor(Color.GREEN);
						}
						g2d.draw(path);
					}
				}

			}
		};
		panel.setBounds(0,0,DEFAULT_PANEL_WIDTH,DEFAULT_PANEL_HEIGHT);
		panel.setOpaque(false);
		//panel.repaint();
		add(panel);

		//read the image
		JPanel mapImg = new JPanel(null);
		mapImg.setBounds(10, 10, DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT);
		JLabel l = new JLabel();
		try {
			Image image = ImageIO.read(new File("map.png"));
			image = image.getScaledInstance(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT, 0);

			l.setIcon(new ImageIcon(image));

		} catch (IOException e) {
			e.printStackTrace();
		}
		l.setBounds(0,0, DEFAULT_PANEL_WIDTH,DEFAULT_PANEL_HEIGHT);
		mapImg.add(l);
		//System.out.println("test");
		add(mapImg);
		//System.out.println("test2");

		//repaint();
		setVisible(true);
	}

	public void setX(List<int[]> x) {
		this.x = x;
	}

	public void setY(List<int[]> y) {
		this.y = y;
	}

	public void setWalkingTimes(List<Double> walkingTimes) {
		this.walkingTimes = walkingTimes;
	}

	public void setTimesBetweenClasses(List<Double> allTimesBetweenClasses) {
		this.allBetweenClassTimes = allTimesBetweenClasses;
	}

	public void setEndpointBuildings(List<String> origins, List<String> destinations) {
		this.originBuildings = origins;
		this.destinationBuildings = destinations;
	}
}