package client.controller;

import client.net.ServerConnector;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/*
* Controller class for the client
* Connects the input from user with net Net-layer
* */

public class Controller {
    private final ServerConnector serverConnector = new ServerConnector();
    String host = "localhost";
    int port = 8080;

    // usd to connect to a specific IP address
    public void connectAdr(String address) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnector.connect(address, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> System.out.println("Connected to " + host + ":" + port));
    }

    // connect to localhost
    public void connect() {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnector.connect(host, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> System.out.println("Connected to " + host + ":" + port));
    }

    public void disconnect() throws IOException {
        serverConnector.disconnect();
    }

    // send message to the server
    public void sendMsg(String msg) {
        CompletableFuture.runAsync(() -> serverConnector.sendMsg(msg));
    }

}
