package alpv_ws1415.ub1.webradio.ui;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import alpv_ws1415.ub1.webradio.communication.U1_akServer;

public class akServerGUI implements ServerUI {
	U1_akServer akServer;
	
	JFrame frame;
	JTextField audioField;

	public akServerGUI(U1_akServer s) {
		akServer=s;
	}

	@Override
	public void run() {
		buildWindow();
	}
	
	private void buildWindow() {
		frame = new JFrame("ak GUI Server");
		frame.setSize(300, 100);
		frame.setLocationRelativeTo(null);
		Panel panel = new Panel();
		frame.add(panel);
		panel.setLayout(new FlowLayout());
		audioField = new JTextField("Audio File Path", 20);
		panel.add(audioField);
		Button read_button = new Button("Open");
		read_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					akServer.playSong(audioField.getText());
				} 
				catch (UnsupportedAudioFileException | IOException e) {
					System.out.println("File not found");
				}
			}
		});

		Button close_button = new Button("Close");
		close_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Close");
				akServer.close();
				System.exit(0);
			}
		});
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	akServer.close();
				System.exit(0);
		    }
		});

		panel.add(read_button);
		panel.add(close_button);
		frame.setVisible(true);
	}
}
