package com.lue.client.tests.android;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/*
 * ONLY supports one device / emulator at the moment. Multi device support not implemented, yet!
 * 
 * NOT THREADSAFE!!!
 */

public class AdbClient {
    public static String HOSTNAME = "localhost";
    public static int PORT = 5037;
    private static Logger LOGGER = Logger.getLogger("ADB_CLIENT");
    AdbConnection adbConnection;



    public AdbClient() throws Exception {
	adbConnection = new AdbConnection(HOSTNAME, PORT);
    }

    public String sendMessage(String message) throws Exception {
	LOGGER.info("sending " + message + "...");
	adbConnection.sendMessage(message);
	LOGGER.info("sent");
	LOGGER.info("Retrieving response...");
	String response = adbConnection.readString();
	System.out.println("response: " + response);
	adbConnection.connect();
	return response;
    } 

    public String sendShellCommand(String command) throws Exception {
	LOGGER.info("executing " + command + "...");
	adbConnection.sendMessage(command);
	LOGGER.info("sent");
	LOGGER.info("Retrieving response...");
	String response = adbConnection.readAllLines();
	System.out.println(response);
	adbConnection.connect();
	return response;
    }

    public void sendShellCommandAsync(String command, final AdbClientCallbackIF callback) throws Exception {
	LOGGER.info("executing " + command + "...");
	adbConnection.sendMessage(command);
	LOGGER.info("sent");
	if(callback != null){
	    new Thread() {
		@Override
		public void run() {
		    LOGGER.info("Retrieving response...");
		    String response = null;
		    try {
			response = adbConnection.readAllLines();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		    System.out.println(response);
		    try {
			adbConnection.connect();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		    callback.callbackSendResponse(response);
		}
	    }.start();
	}
    }


    public static void main(String[] args) throws Exception {
	AdbClient client = new AdbClient();
	client.sendMessage("host:devices");
//	Date d = new Date();
//	String timeStamp = new SimpleDateFormat("yyyyMMdd.HHmmss").format(d);
//	client.sendShellCommand("shell:date -s " + timeStamp);
//	client.sendShellCommand("shell:am instrument -w -e class com.lue.max.testapp3.MainActivityTest com.lue.max.testapp3.test/com.lue.max.testapp3.MyTestRunner");

    }

    private class AdbConnection {
	private String host;
	private int port;
	private OutputStream outputStream;
	private InputStream inputStream;
	private Socket socket;
	private Device device;


	public AdbConnection(String host, int port) throws Exception {
	    this.host = host;
	    this.port = port;
	    connect();
	    device = getDevices().get(0);
	}

	private void connect() throws IOException {
	    if(socket != null) {
		socket.close();
	    }
	    socket = new Socket(host, port);
	    LOGGER.info("Connected to ADB!");
	    outputStream = socket.getOutputStream();
	    inputStream = socket.getInputStream();
	}

	public void sendMessage(String message) throws Exception {
	    forceTransport();
	    sendString(message);
	    LOGGER.info("sent");
	    LOGGER.info("Retrieving response...");
	    verifyResponse();
	}

	private void forceTransport() throws Exception {
	    String transportMsg = "host:transport:" + device.getSerial();
	    sendString(transportMsg);
	    verifyResponse();
	}

	private void sendString(String s) throws IOException {
	    OutputStreamWriter writer = new OutputStreamWriter(outputStream);
	    writer.write(getMessageLength(s));
	    writer.write(s);
	    writer.flush();
	}

	private String getMessageLength(String message) {
	    return String.format("%04x", message.length());
	}

	private void verifyResponse() throws Exception {
	    String response = readString(4);
	    if (!"OKAY".equals(response))
	    {
		String error = readString();
		throw new Exception("command failed: " + error);
	    }
	}

	private String readString() throws IOException {
	    String encodedLength = readString(4);
	    int length = Integer.parseInt(encodedLength, 16); 		
	    return readString(length);
	}

	private String readString(int length) throws IOException {
	    DataInput reader = new DataInputStream(inputStream);
	    byte[] responseBuffer = new byte[length];		
	    reader.readFully(responseBuffer);
	    return new String(responseBuffer, Charset.forName("utf-8"));
	}

	private String readAllLines() throws IOException {
	    DataInput reader = new DataInputStream(inputStream);
	    StringBuilder sBuilder = new StringBuilder();
	    String line;
	    while((line=reader.readLine()) != null) {
		sBuilder.append(line).append("\n");
	    }
	    return sBuilder.toString();
	}

	private List<Device> getDevices() throws Exception
	{
	    sendString("host:devices");
	    verifyResponse();
	    String body = readString();
	    System.out.println("response: " + body);
	    connect();
	    return parseDevices(body);
	}

	private List<Device> parseDevices(String body) {
	    String[] lines = body.split("\n");
	    List<Device> devices = new ArrayList<>();
	    for (String line : lines)
	    {
		String[] parts = line.split("\t");
		if (parts.length > 1) {
		    devices.add(new Device(parts[0], parts[1]));
		}
	    }
	    return devices;
	}

    }

    private class Device {
	String serial;
	String type;

	public Device(String serial, String type) {
	    this.serial = serial;
	    this.type = type;
	}
	public String getSerial() {
	    return serial;
	}
    }
}
