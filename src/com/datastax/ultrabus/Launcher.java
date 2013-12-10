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

public class Launcher {

	private JFrame frame;
	private final ButtonGroup startStopButtons = new ButtonGroup();
	private final JLabel maxMemoryLabel = new JLabel("1024 MB");
	private final JLabel cassStatusLabel = new JLabel("stopped");
	public int maximumMemory = 1024;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Launcher window = new Launcher();
					window.frame.setVisible(true);
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
		initialize();
	}

	private void setMaxMemory(int mem) {
		maximumMemory = mem;
		maxMemoryLabel.setText(mem + " MB");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblRunApacheCassandra = new JLabel("Run Apache Cassandra");
		lblRunApacheCassandra.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRunApacheCassandra.setBounds(22, 13, 247, 44);
		frame.getContentPane().add(lblRunApacheCassandra);

		maxMemoryLabel.setBounds(22, 81, 203, 16);
		frame.getContentPane().add(maxMemoryLabel);

		cassStatusLabel.setForeground(Color.RED);
		cassStatusLabel.setBounds(332, 28, 56, 16);
		frame.getContentPane().add(cassStatusLabel);

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

		frame.getContentPane().add(slider);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startCassandra();
			}
		});
		btnStart.setLocation(22, 182);
		btnStart.setSize(new Dimension(120, 60));
		startStopButtons.add(btnStart);
		frame.getContentPane().add(btnStart);

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopCassandra();
			}
		});
		btnStop.setLocation(300, 182);
		btnStop.setSize(new Dimension(120, 60));
		startStopButtons.add(btnStop);
		frame.getContentPane().add(btnStop);

	}

	private void startCassandra() {
		cassStatusLabel.setForeground(Color.GREEN);
		cassStatusLabel.setText("running");
		// Runtime.getRuntime().exec(args[0]);
	}

	private void stopCassandra() {
		cassStatusLabel.setForeground(Color.RED);
		cassStatusLabel.setText("stopped");
	}

}
