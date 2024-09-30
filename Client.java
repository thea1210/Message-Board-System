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

public class Client {
    private static String SERVER_HOST = "localhost";
    private static int SERVER_PORT = 12345;
    private static boolean shouldRun = true;
    private static Map<String, String> convert(String[] message) {
		Map<String,String> myMap = new HashMap<String,String>(); 
		switch (message[0]) {
			case "/register":
				myMap.put("command", message[0]);
				if(message.length>1)
					myMap.put("handle", message[1]);
				if(message.length>2)
					myMap.put("error", message[2]);
				break;
			case "/all":
				myMap.put("command", message[0]);
				if(message.length>2)
					myMap.put("message", message[1].concat(" ").concat(message[2]));
				else if(message.length >1)
					myMap.put("message", message[1]);
				break;
			case "/msg":
				myMap.put("command", message[0]);
				if(message.length >1)
					myMap.put("handle", message[1]);
				if(message.length>2)
					myMap.put("message", message[2]);
				break;
			case "/?":
				myMap.put("command", message[0]);
				if(message.length>2)
					myMap.put("error", message[1].concat(message[2]));
				else if(message.length >1)
					myMap.put("error", message[1]);
				break;
			case "/leave":
				myMap.put("command", message[0]);
				if(message.length>2)
					myMap.put("error", message[1].concat(message[2]));
				else if(message.length >1)
					myMap.put("error", message[1]);
				break;
			default:
				myMap.put("command", "/error");
		}
		 
		return myMap;
	}
    
    public static boolean userJoin(String[] cmd) {
    	if(cmd[0].equalsIgnoreCase("/join")) {
			if(cmd.length >= 3 && (cmd[1].equalsIgnoreCase("localhost") || 
			cmd[1].equalsIgnoreCase("127.0.0.1")) && cmd[2].equalsIgnoreCase("12345")) {
				System.out.println("Connection to the Message Board Server is successful!");
				return true;
			}
			else {
				System.out.println("Error: Connection to the message board has failed. Please check IP Address and Port Number.");
				return false;
			}
    	}
    	else if(cmd[0].equalsIgnoreCase("/?")) {
    		System.out.println("List of Commands \n"
        			+ "/join <server_ip> <port> - connects to the server. \n"
        			+ "/? - lists of commands. \n");
    		return false;
    	}
    	else if(cmd[0].equalsIgnoreCase("/leave") || cmd[0].equalsIgnoreCase("/register") ||
    			cmd[0].equalsIgnoreCase("/msg") || cmd[0].equalsIgnoreCase("/all")) {
    		System.out.println("Error: Disconnection failed. Please connect to the server first.");
    		return false;
    	}
    	else {
    		System.out.println("Error : Command not found.");
    		return false;
    	}
    }
    
    public static void main(String[] args) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        boolean isConnected = false;

        byte[] sendData;
        byte[] receiveData = new byte[1024];

        System.out.println("Client is starting... Please Connect to the Server.");
        // Start a new thread to listen for incoming messages from the server
        Thread receiveThread = new Thread(() -> {
            while (shouldRun) { // use the final variable
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    clientSocket.receive(receivePacket);
                    String modifiedMessage = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                    JSONObject jsR = new JSONObject(modifiedMessage);
                    System.out.println("> " + jsR.get("message"));
                } catch (IOException e) {
                    // catch the SocketException thrown when the socket is closed
                    if (shouldRun) { // no error here
                        e.printStackTrace();
                    }
                }
            }
        });

        while (true) {
            System.out.println();
            String message = inFromUser.readLine();
            String[] splitMessage = message.split(" ", 3);

            if (isConnected == false) {
                if (message.equals("quit")) {
                    System.out.println("Program Terminated. Thank you!");
                    break;
                }
                isConnected = userJoin(splitMessage);
            } else {
                JSONObject js = new JSONObject(convert(splitMessage));
                String jsString = js.toString();

                sendData = jsString.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);
                js.clear();

                if (message.equals("/leave")) {
                    // close the socket and stop the receive thread
                    clientSocket.close();
                    shouldRun = false;
                    try {
                        receiveThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isConnected = false;
                    System.out.println("Disconnected from server.");
                    continue; // skip the rest of the loop
                }
                if (!receiveThread.isAlive()) {
                    receiveThread.start(); // start the receive thread only once
                }
            }
        }
    }

}

