package server.net;

import server.controller.Controller;
import server.model.Word;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/*
* Client handler of each client connected. Each client is started in a new thread.
* */

public class ClientHandler implements Runnable {
    private final GameServer server;
    private final Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private Controller controller = new Controller();
    private boolean connected = false;

    ClientHandler(GameServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        System.out.println("Client connected: " + clientSocket);
        connected = true;
    }

    @Override
    public void run() {
        try {
            boolean autoFlush = true;
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), autoFlush);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (connected) {
            try {
                String msg = fromClient.readLine();
                System.out.println("Received msg from client: " + msg);
                if (msg == null) {
                    System.err.println("No message received");
                }
                switch (msg) {
                    case "Start.":
                        sendMsg(controller.startGame());
                        break;
                    case "Test.":
                        sendBigData();
                        sendMsg(controller.startGame());
                        break;
                    case "Restart.":
                        sendMsg(controller.restart());
                        break;
                    case "Disconnect.":
                        disconnectClient();
                        break;
                    default:
                        sendMsg(controller.gameEntry(msg));
                        break;
                }
            } catch (Exception e) {
                System.err.println("Message not recognized.");
            }
        }
    }

    // helper method for debugging sending of large messages
    private  void sendBigData() throws IOException {
        ArrayList<String> big = Word.getAllWords();
        sendMsg("s/s/" +big+"/s");
        toClient.println("somemoredata");
    }

    // check size of the message sent
    private int checkSize(String msg) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream ( ) ;
        ObjectOutputStream objectOutputStream = new ObjectOutputStream ( byteOutputStream ) ;
        objectOutputStream.writeObject(msg);
        objectOutputStream.flush();
        objectOutputStream.close();
        return byteOutputStream.toByteArray().length;
    }

    private void sendMsg(String msg) throws IOException {
        // calculate message size before sending it to the client
        int msgLength = checkSize(msg);
        // prepend message length to the message
        toClient.println(msgLength+"##"+msg);
    }

    private void disconnectClient() {
        try {
            System.out.println(clientSocket + " disconnected.");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connected = false;
        server.removeHandler(this);
    }
}
