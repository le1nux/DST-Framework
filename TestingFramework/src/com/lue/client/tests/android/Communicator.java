package com.lue.client.tests.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lue.common.util.JsonProcessor;

public class Communicator {
    public static final int PORT = 4711;
    protected Map<String, List<CommunicatorCallbackIF>> commandToCallbackObjects;
    protected ServerSocket serverSocket;
    protected List<Client> clients;

    public Communicator() throws IOException {
	commandToCallbackObjects = new HashMap<String, List<CommunicatorCallbackIF>>();
	clients = new ArrayList<Communicator.Client>();
	serverSocket = new ServerSocket(PORT);
	acceptClients();
    }

    protected void acceptClients() {
	Thread t = new Thread() {
	    @Override
	    public void run() {
		while(true) {
		    System.out.println("Waiting for client to connect...");
		    try {
			Socket clientSocket = serverSocket.accept();
			System.out.println("Client connected.");
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			clients.add(new Client(out, in, clientSocket));
		    } catch(Exception e) {
			e.printStackTrace();
			if(serverSocket.isClosed()) {
			    break;
			}
		    }
		}
	    }
	};
	t.start();
    }

    protected synchronized void informCallbackObjects(Command command) {
	if(commandToCallbackObjects.containsKey(command.getIdentifier())) {
	    List<CommunicatorCallbackIF> callbackObjects = commandToCallbackObjects.get(command.getIdentifier());
	    for(CommunicatorCallbackIF callbackIF : callbackObjects) {
		callbackIF.callback(command);
	    }
	}
    }

    public void addCallbackObject(String command, CommunicatorCallbackIF callbackIF) {
	if(!commandToCallbackObjects.containsKey(command)) {
	    commandToCallbackObjects.put(command, new ArrayList<CommunicatorCallbackIF>());
	}
	commandToCallbackObjects.get(command).add(callbackIF);
    }

    public void close() throws IOException {
	serverSocket.close();	
    }

    public static void main(String[] args) {	// starter method to test communicator
	try {
	    new Communicator();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static class Command {
	String identifier;
	String payload;

	public Command() {

	}

	public Command(String identifier, String payload) {
	    this.identifier = identifier;
	    this.payload = payload;
	}

	public String getIdentifier() {
	    return identifier;
	}

	public void setIdentifier(String identifier) {
	    this.identifier = identifier;
	}

	public String getPayload() {
	    return payload;
	}

	public void setPayload(String payload) {
	    this.payload = payload;
	}
    }

    public class Client {
	protected PrintWriter out;
	protected BufferedReader in;
	protected Socket socket;

	public Client(PrintWriter out, final BufferedReader in, Socket socket) {
	    this.out = out;
	    this.in = in;
	    this.socket = socket;
	    Thread t = new Thread() {
		@Override
		public void run() {
		    String line = null;
		    try {
			while ((line = in.readLine()) != null) {
			    System.out.println("command: " + line);
			    Command command = (Command) JsonProcessor.fromJson(line, Command.class);
			    informCallbackObjects(command);
			}
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    };
	    t.start();
	}

	public PrintWriter getOut() {
	    return out;
	}

	public void setOut(PrintWriter out) {
	    this.out = out;
	}

	public BufferedReader getIn() {
	    return in;
	}

	public void setIn(BufferedReader in) {
	    this.in = in;
	}

	public Socket getServerSocket() {
	    return socket;
	}

	public void setServerSocket(Socket socket) {
	    this.socket = socket;
	}
    }
}
