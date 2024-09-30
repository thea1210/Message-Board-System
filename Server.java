/* CSNETWK S11 : Group 9
 * ARCETA, ALTHEA ZYRIE MANUEL
 * LADA, MARTIN JOSE MERCHAN
 * PALAFOX, LUIS BENEDICT MATIENZO
 * */

package csnetwk;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.*;

public class Server {
    private static int SERVER_PORT = 12345;
    private static Map<Integer, String> clientList = new HashMap<Integer, String>(); 

    public static String Clientlist(int port) {
    	boolean isHere = clientList.containsKey(port);
    	if(isHere == false) {
    		String username = "User" + Integer.toString(clientList.size());
    		clientList.put(port, username);
    	}
    	
    	return clientList.get(port);
    }
    
    //register 
    public static void register(JSONObject jsR, InetAddress clientAddress, int clientPort, DatagramSocket serverSocket) {
    	JSONObject js = new JSONObject();
    	byte[] sendData;
    	
    	if (!jsR.has("handle")) {
            // Send error message to the sender
            js.put("message", "Error: No Handle or alias is found.");
            String modifiedMessageT = js.toString();
            sendData = modifiedMessageT.getBytes();
            DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            try {
                serverSocket.send(sendPacketT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    	
    	String name = jsR.getString("handle");
    	
    	boolean isHere = false;
    	for (String userName : clientList.values()) {
    		if (userName.equals(name)) {
    			isHere = true;
    		}
    	}
    	
    	if (jsR.has("error")) {
            // Send error message to the sender
            js.put("message", "Error: Incorrect syntax.");
            String modifiedMessageT = js.toString();
            sendData = modifiedMessageT.getBytes();
            DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            try {
                serverSocket.send(sendPacketT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

    	
    	if(isHere) {
    		js.put("message", "Error : Registration failed. Handle or Alias already exists.");
    		String modifiedMessage = js.toString();
    		sendData = modifiedMessage.getBytes();
    		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
    		try {
    			serverSocket.send(sendPacket);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	else {
    		clientList.put(clientPort, name);
    	
    		js.put("message", "Welcome " + name + "!");
    		String modifiedMessage = js.toString();
    		sendData = modifiedMessage.getBytes();
    		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
    		try {
    			serverSocket.send(sendPacket);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
		}
    }
    
    //?
    public static void listOfCmds(JSONObject jsR, InetAddress clientAddress, int clientPort, DatagramSocket serverSocket) {
    	JSONObject jsS = new JSONObject();
    	byte[] sendData = new byte[1024];
    	
    	if (jsR.has("error")) {
            // Send error message to the sender
            jsS.put("message", "Error: Incorrect syntax. Double check parameters.");
            String modifiedMessageT = jsS.toString();
            sendData = modifiedMessageT.getBytes();
            DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            try {
                serverSocket.send(sendPacketT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    	
    	jsS.put("message", "List of Commands \n"
    			+ "/join <server_ip> <port> - connects to the server \n"
    			+ "/leave - disconnect the server \n"
    			+ "/register <handle> - register a unique alias or handle \n"
    			+ "/msg <handle> <message> - sends a dm to a specific client \n");
    	String modifiedMessage = jsS.toString();
        sendData = modifiedMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //leave
    public static void leave(JSONObject jsR, InetAddress clientAddress, int clientPort, DatagramSocket serverSocket) {
    	JSONObject jsS = new JSONObject();
    	clientList.remove(clientPort);
    	byte[] sendData = new byte[1024];
    	
    	if (jsR.has("error")) {
            // Send error message to the sender
            jsS.put("message", "Error: Incorrect syntax. Double check parameters.");
            String modifiedMessageT = jsS.toString();
            sendData = modifiedMessageT.getBytes();
            DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            try {
                serverSocket.send(sendPacketT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    	
    	jsS.put("message", "Connection is closed. Thank you!");
    	String modifiedMessage = jsS.toString();
        sendData = modifiedMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //error
    public static void error(InetAddress clientAddress, int clientPort, DatagramSocket serverSocket) {
    	JSONObject js = new JSONObject();
    	byte[] sendData = new byte[1024];
    	
    	js.put("message", "Error : Invalid Command.");
    	String modifiedMessage = js.toString();
        sendData = modifiedMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static int getUser(Map<Integer, String> clientList, String value) {
        for (Map.Entry<Integer, String> entry : clientList.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        
        return -1; // Return -1 if value not found
    }
    
    public static void msg(JSONObject js, InetAddress clientAddress, int clientPort, DatagramSocket serverSocket) {
    	JSONObject jsS = new JSONObject();
    	JSONObject jsR = new JSONObject();
    	byte[] sendData = new byte[1024];
    	
    	if (!js.has("handle")) {
        	// Send error message to the sender
            jsS.put("message", "Error: No handler is found.");
            String modifiedMessageT = jsS.toString();
            sendData = modifiedMessageT.getBytes();
            DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            try {
                serverSocket.send(sendPacketT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    	
    	String dmUser = js.getString("handle");
    	String sender = Clientlist(clientPort);
    	int dmPort = getUser(clientList, dmUser);
    	
    	 if (!js.has("message")) {
         	// Send error message to the sender
             jsS.put("message", "Error: No message is found");
             String modifiedMessageT = jsS.toString();
             sendData = modifiedMessageT.getBytes();
             DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
             try {
                 serverSocket.send(sendPacketT);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             return;
         }
    	
    	// Check if the user exists in the client list
        if (dmPort == -1) {
            // Send error message to the sender
            jsS.put("message", "Error: Handle or alias not found.");
            String modifiedMessageT = jsS.toString();
            sendData = modifiedMessageT.getBytes();
            DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            try {
                serverSocket.send(sendPacketT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
       
    	
    	//receiver
    	jsR.put("message", "[From " + sender + "]: " + js.getString("message"));
    	String modifiedMessageR = jsR.toString();
        sendData = modifiedMessageR.getBytes();
        DatagramPacket sendPacketR = new DatagramPacket(sendData, sendData.length, clientAddress, dmPort);
        try {
			serverSocket.send(sendPacketR);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //sender
        jsS.put("message", "[To " + dmUser + "]: " + js.getString("message"));
        String modifiedMessageS = jsS.toString();
        sendData = modifiedMessageS.getBytes();
        DatagramPacket sendPacketS = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        try {
			serverSocket.send(sendPacketS);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
    
    public static void all(JSONObject js, InetAddress clientAddress, int clientPort, DatagramSocket serverSocket) {
    	JSONObject jsS = new JSONObject();
    	byte[] sendData = new byte[1024];
    	String sender = Clientlist(clientPort);
    	
    	if (!js.has("message")) {
        	// Send error message to the sender
            jsS.put("message", "Error: No message is found");
            String modifiedMessageT = jsS.toString();
            sendData = modifiedMessageT.getBytes();
            DatagramPacket sendPacketT = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            try {
                serverSocket.send(sendPacketT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    	
    	for (int port : clientList.keySet()) {
    		jsS.put("message", sender + ": " + js.getString("message"));
    		String modifiedMessage = jsS.toString();
    		sendData = modifiedMessage.getBytes();
    		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, port);
    		try {
    			serverSocket.send(sendPacket);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void command(JSONObject js, InetAddress clientAddress, int clientPort, DatagramSocket serverSocket ) {
    	String cmd = js.getString("command");
    	switch (cmd) {
    		case "/register":
    			register(js, clientAddress, clientPort, serverSocket);
    			break;
    		case "/msg":
    			msg(js, clientAddress, clientPort, serverSocket);
    			break;
    		case "/all":
    			all(js, clientAddress, clientPort, serverSocket);
    			break;
    		case "/?":
    			listOfCmds(js, clientAddress, clientPort, serverSocket);
    			break;
    		case "/leave":
    			leave(js, clientAddress, clientPort, serverSocket);
    			break;
    		case "/error":
    			error(clientAddress, clientPort, serverSocket);
    			break;
    	}
    }
    
    public static void main(String[] args) throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);

        System.out.println("Server started and listening on port " + SERVER_PORT);

        while (true) {
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String message = new String(receivePacket.getData());
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            String clientName = Clientlist(clientPort);
            //sending here
            JSONObject js = new JSONObject(message);     
            command(js, clientAddress, clientPort, serverSocket);
            js.clear();
            
            // Handle client request in a separate thread
            Thread clientThread = new Thread(new ClientHandler(serverSocket, receivePacket));
            clientThread.start();
        }
    }
    
    static class ClientHandler implements Runnable {
        private DatagramSocket serverSocket;
        private DatagramPacket receivePacket;

        public ClientHandler(DatagramSocket serverSocket, DatagramPacket receivePacket) {
            this.serverSocket = serverSocket;
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            String message = new String(receivePacket.getData());
            System.out.println("Handling " + clientList.get(clientPort) + " " + clientAddress.getHostAddress() + ":" + clientPort + ": " + message);
            
            //here
        }
    }
}