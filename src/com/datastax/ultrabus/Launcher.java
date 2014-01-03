package com.datastax.ultrabus;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.ButtonGroup;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {
	public int maximumMemory = 1024;
	
	// main window
	private JFrame mainFrame;
	private JFrame logViewerFrame;

	private final ButtonGroup startStopButtons = new ButtonGroup();
	private final JLabel maxMemoryLabel = new JLabel("1024 MB");
	private final JLabel cassStatusLabel = new JLabel("stopped");
	private ProcessBuilder pb;
	private Process cassandraProcess;
	private JButton btnStart = new JButton("Start");
	private JButton btnStop = new JButton("Stop");
	
	// for the log viewer
    private Runnable logViewerThread;
    private final LogViewerPane logPane = new LogViewerPane();
	public Path logfilePath = Paths.get("C:\\Users\\al\\eclipse\\artifacts.xml");
    static final long MAX_LOG_LINES = 1024;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Launcher window = new Launcher();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Launcher() {
		System.out.println("-> Launcher()\n");
		initializePB();
		initializeUI();
		initializeLogViewer();
		System.out.println("<- Launcher()\n");
	}

	private void setMaxMemory(int mem) {
		maximumMemory = mem;
		maxMemoryLabel.setText(mem + " MB");
	}
	
	private void setLogfilePath(String logfile) {
		logfilePath = Paths.get(logfile);
	}
	
	/**
	 * TODO: launch Cassandra, pick up version from command-line args
	 */
	private void initializePB() {
		pb = new ProcessBuilder("notepad.exe");
		pb.directory(new File("G:\\UltraBus\\apache-cassandra-2.0.3"));
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeUI() {
		mainFrame = new JFrame();
		mainFrame.setBounds(100, 100, 450, 300);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);

		JLabel lblRunApacheCassandra = new JLabel("Run Apache Cassandra");
		lblRunApacheCassandra.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRunApacheCassandra.setBounds(22, 13, 247, 44);
		mainFrame.getContentPane().add(lblRunApacheCassandra);

		maxMemoryLabel.setBounds(22, 81, 203, 16);
		mainFrame.getContentPane().add(maxMemoryLabel);

		// show current C* status
		cassStatusLabel.setForeground(Color.RED);
		cassStatusLabel.setBounds(332, 28, 56, 16);
		mainFrame.getContentPane().add(cassStatusLabel);

		// Memory Slider
		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					setMaxMemory((int) source.getValue());
				}
			}
		});
		slider.setPaintTicks(true);
		slider.setMaximum(8192);
		slider.setMinimum(1024);
		slider.setMajorTickSpacing(1024);
		slider.setName("MaxMemory");
		slider.setPaintLabels(true);
		slider.setToolTipText("Maximum memory for Cassandra");
		slider.setLocation(22, 109);
		slider.setSize(new Dimension(398, 60));

		mainFrame.getContentPane().add(slider);

		// START button
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startCassandra();
			}
		});
		btnStart.setLocation(22, 182);
		btnStart.setSize(new Dimension(120, 60));
		startStopButtons.add(btnStart);
		mainFrame.getContentPane().add(btnStart);

		// STOP button
		btnStop.setEnabled(false);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopCassandra();
			}
		});
		btnStop.setLocation(154, 182);
		btnStop.setSize(new Dimension(120, 60));
		startStopButtons.add(btnStop);
		mainFrame.getContentPane().add(btnStop);
		
		// Logfile Viewer Button
		JButton btnLogfileViewer = new JButton("Logfile Viewer");
		btnLogfileViewer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayLogfileViewer();
			}
		});
		btnLogfileViewer.setBounds(300, 182, 120, 31);
		mainFrame.getContentPane().add(btnLogfileViewer);
		
		// Advanced Settings Button
		JButton btnAdvanced = new JButton("Advanced");
		btnAdvanced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayAdvancedOptions();
			}
		});
		btnAdvanced.setBounds(300, 214, 120, 31);
		mainFrame.getContentPane().add(btnAdvanced);

	}
	
	private void initializeLogViewer() {
		logPane.setEditable(false);
	    logViewerThread = new LogViewerThread(logPane);
        Thread thread = new Thread(logViewerThread);
        thread.start();
	}

	private void startCassandra() {
		btnStart.setEnabled(false);
		btnStop.setEnabled(false);

		try {
			cassandraProcess = pb.start();
		}
		catch (Exception e) {
			cassStatusLabel.setText("error");
			btnStart.setEnabled(true);
			return;
		}
		cassStatusLabel.setForeground(Color.GREEN);
		cassStatusLabel.setText("running");
		btnStop.setEnabled(true);
	}

	private void stopCassandra() {
		btnStart.setEnabled(false);
		btnStop.setEnabled(false);
		
		// TODO: use JMX to drain C*
		try {
			cassandraProcess.destroy();
		}
		catch (Exception e) {
			cassStatusLabel.setText("error!");
			btnStop.setEnabled(true);
			return;
		}
		cassStatusLabel.setForeground(Color.RED);
		cassStatusLabel.setText("stopped");
		btnStart.setEnabled(true);
	}
	
	private void displayLogfileViewer() {
		System.out.println("creating console");

		logViewerFrame = new JFrame();
		logViewerFrame.setBounds(100, 100, 800, 600);
		logViewerFrame.setVisible(true);
		logViewerFrame.add(logPane);
		
		System.out.println("con.run() returned");
	}
	
	private void displayAdvancedOptions() {
	}
}
