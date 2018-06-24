package ffff;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {

	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);
	JPanel pnl = new JPanel(new GridLayout(6, 2));
	static String con ="KEEP ALIVE"; 
	static JCheckBox close;

	public ChatClient() {

		// GUI

		// Add Listeners

	}

	private String getName() {
		return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Join to the Server ",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Connects to the server then enters the processing loop.
	 */
	private void run() throws Exception {
			
		// Make connection and initialize streams
		Socket socket = new Socket("127.0.0.1", 9000);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		File fo = new File("src/docroot/");
		File[] list = fo.listFiles();
		 close = new JCheckBox("close???");
		pnl.add(close);
		JButton btn;
		
		for (File f : list) {
			btn = new JButton(f.getName());
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(close.isSelected())con="Closed";
					else
						con="KEEP ALIVE";
					out.println("GET " + f.getName() + "`"+close.isSelected());
					System.out.println(close.isSelected());
					messageArea.append("Requested " + f.getName());

				}
			});
			pnl.add(btn);
		}

		textField.setEditable(false);
		messageArea.setEditable(false);

		frame.getContentPane().add(pnl, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack();
		frame.setVisible(true);

		// Process all messages from server, according to the protocol.
		while (true) {
			String line = in.readLine();

			if (line.startsWith("SUBMITNAME")) {
				out.println(getName());
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
				frame.setTitle("Cahtter :" + line.substring(12));
			} else if (line.startsWith("LOLXD")) {
				messageArea.append(line.substring(5) + "\n");
			} else if (line.startsWith("er")) {
				messageArea.append("please Enter Valid instruction" + "\n");
			}
			if (line.startsWith("true")) {
				con = close.isSelected()?"Close":"KEEP ALIVE";
				String name = line.substring(4);
				// System.out.println("HH");
				String type = "";
				int y=0;
				for (int i = name.length() - 1; i >= 0; i--) {
					if (name.charAt(i) == '.')
					{
						y=i+1;
						break;
					}
				}
						type += name.substring(y);
						
				
				String response = "HTTP/1.1 200 OK\n" + new Date().toString() + "\n" + "Server: Apache/2.2.14 (Win32)\n"
						+ "Format :" + new StringBuilder(type).toString() + "\n" + "Content-Length: 88\n"
						+ "Content-Type: text/html\n" + "Connection:"+con+"\n"
						+ "            			\"Host :LocalHost\";\n" + "";
				
				messageArea.append(response);
				if(socket.isClosed())continue;
				ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());

				ObjectOutputStream saveImage = new ObjectOutputStream(
						new FileOutputStream("src" + File.separator + "client" + File.separator + name));
				byte[] content;
				try {

					content = (byte[]) inFromServer.readObject();
					System.out.println("DONE");
					writeBytes2(content, "src" + File.separator + "client" + File.separator + name);
					if(close.isSelected())
					{
						//socket.close();
						//inFromServer.close();
					}
					//inFromServer.close();

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} 
		}
	}

	public static void main(String[] args) throws Exception {
		ChatClient client = new ChatClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}

	private static void writeBytes2(byte[] bFile, String fileDest) {

		FileOutputStream fileOuputStream = null;

		try {
			fileOuputStream = new FileOutputStream(fileDest);
			fileOuputStream.write(bFile);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOuputStream != null) {
				try {
					fileOuputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}