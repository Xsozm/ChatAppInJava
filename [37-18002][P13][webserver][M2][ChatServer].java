package ffff;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class ChatServer {


    private static final int PORT = 9000;

    private static ArrayList<String> names = new ArrayList<String>();

    private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();
    static Queue q=new LinkedList<>();
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

      
        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                //eb3t en el esm tamam
                out.println("NAMEACCEPTED"+name);
                writers.add(out);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }else
                    	
                  if(input.startsWith(("GET"))){
                	  String[] nnnn = input.split("`");
                	  q.add(nnnn[0].substring(4));
                	  String con = nnnn[1].equals("true")?"Closed":"Keep ALive";
                	  System.out.println("GET 1.1"+nnnn[0].substring(4)+"\n"+"LOCAL HOST \n"+"jpg/ping/mp4 \n KEEP ALIVE");
		                out.println(new File("src"+File.separator+"docroot"+File.separator+nnnn[0].substring(4)).exists()+nnnn[0].substring(4));
		                if(!new File("src"+File.separator+"docroot"+File.separator+nnnn[0].substring(4)).exists()) continue;
                	  String filename = nnnn[0].substring(4);
                				File f = new File("src"+File.separator+"docroot"+File.separator+filename);
                				ObjectOutputStream outToClient = new ObjectOutputStream(
				                       socket.getOutputStream());
							 byte[] content = Files.readAllBytes(f.toPath());
							 
							 outToClient.writeObject(content);
							 if(ChatClient.close !=null && ChatClient.close .isSelected())
								 this.socket.close();
				                outToClient.flush();
				                q.poll();
                  }
                  
                    
                    
                    
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}