package alpv_ws1415.ub1.webradio.ui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import alpv_ws1415.ub1.webradio.communication.U1_akClient;

public class akClientGUI implements ClientUI {

	String uname;
	U1_akClient akClient;
	JTextField msgField;
	JTextArea txtArea;
	JFrame frame;
	
	@Override
	public void run() {
		buildWindow();

	}
	
	public akClientGUI(U1_akClient c) {
		akClient=c;

		txtArea = new JTextArea(2, 20);
		txtArea.setEditable(false);
	}

	private void buildWindow() {
		frame = new JFrame("ak GUI Client");
		frame.setSize(300, 400);
		frame.setLocationRelativeTo(null);

		Panel panel = new Panel();

		frame.add(panel);

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		msgField = new JTextField("Chat Message", 20);
		msgField.setMinimumSize(new Dimension(400, 20));
		msgField.setMaximumSize(msgField.getMinimumSize());

		Button read_button = new Button("Send");
		read_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String temp = msgField.getText();
				if (!temp.isEmpty()) {
					try {
						akClient.sendChatMessage(temp);
						msgField.setText("");
					} 
					catch (IOException e) { }
				}
			}
		});

		//shut process
		
		Button shutdown = new Button("Close");
		shutdown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				akClient.close();
				System.exit(0);
			}
		});
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	akClient.close();
				System.exit(0);
		    }
		});
		
		//add to ui
		panel.add(txtArea);
		panel.add(msgField);
		panel.add(read_button);
		panel.add(shutdown);

		frame.setVisible(true);
	}


	@Override
	public String getUserName() {
		return "user";
	}


	@Override
	public void pushChatMessage(String message) {
		txtArea.append(message);
	}
}