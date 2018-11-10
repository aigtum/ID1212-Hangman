package client.net;

import client.view.UserInterface;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnector implements Runnable {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;
    private UserInterface ui;
    private ArrayList<String> messageBuffer = new ArrayList<>();

    public void connect(String host, int port) throws IOException {
        socket = new Socket();
        ui = new UserInterface();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        boolean autoFlush = true;
        toServer = new PrintWriter(socket.getOutputStream(), autoFlush);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(this).start();
    }

    public void disconnect() throws IOException {
        sendMsg("Disconnect");
        socket.close();
        socket = null;
        connected = false;
    }

    public void sendMsg(String msg) {
        toServer.println(msg);
    }

    private int checkSize(String msg) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream ( ) ;
        ObjectOutputStream objectOutputStream = new ObjectOutputStream ( byteOutputStream ) ;
        objectOutputStream.writeObject(msg);
        objectOutputStream.flush();
        objectOutputStream.close();
        return byteOutputStream.toByteArray().length;
    }

    @Override
    public void run() {
        try {
            while(true) {
                String msgFromServer = fromServer.readLine();
                StringBuilder sb = new StringBuilder();

                if (msgFromServer.contains("##")) {
                    String[] split = msgFromServer.split("##");
                    int header = Integer.parseInt(split[0]);
                    String msg = split[1];
                    System.out.println("HEADER: " + header + " Size: " + msg.getBytes().length);
                    if (header - checkSize(msg) == 0 && header <= 65535) {
                        ui.showOutput(msg);
                    } else {
                        System.out.println("Message too big, combining.");
                        messageBuffer.add(msgFromServer);
                    }
                }
                while (!messageBuffer.isEmpty()) {
                    System.out.println("Combining...");
                    String msgFromBuffer = messageBuffer.get(0);
                    sb.append(msgFromBuffer);
                    messageBuffer.remove(0);
                }
                if (!sb.toString().equals("")) {
                    String newMsg = sb.toString();
                    String[] newSplit = newMsg.split("##");
                    ui.showOutput(newSplit[1]);
                }
            }
        } catch (Exception e) {
            if (connected) {
                System.out.println("Lost connection");
            }
        }
    }
}
