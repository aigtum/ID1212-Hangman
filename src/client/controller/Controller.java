package client.controller;

import client.net.ServerConnector;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Controller {
    private final ServerConnector serverConnector = new ServerConnector();
    String host = "localhost";
    int port = 8080;

    public void connectAdr(String address) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnector.connect(address, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> System.out.println("Connected to " + host + ":" + port));
    }

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
        System.out.println("Disconnected from the server");
        serverConnector.disconnect();
    }

    public void sendMsg(String msg) {
        CompletableFuture.runAsync(() -> serverConnector.sendMsg(msg));
    }

}
