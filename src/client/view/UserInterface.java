package client.view;

import client.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface implements Runnable {
    private final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private boolean receivingCommands = false;
    private Controller controller;


    public void start() {
        System.out.println("Welcome to hangman. Type in 'Connect.' to connect to the sever. Type 'Disconnect.' to disconnect.");
        System.out.println("Enter 'ConnectAddr.' to connect to a specific IP.");
        System.out.println("Enter 'Start.' to start a new game after a connection has been established.");
        if (receivingCommands) {
            return;
        }
        receivingCommands = true;
        controller = new Controller();
        new Thread(this).start();
    }

    private String getUserInput() {
        String input = console.nextLine();
        return input;
    }

    @Override
    public void run() {
        while(true){
            try {
                String userMsg = getUserInput();

                switch (userMsg) {
                    case "Connect.":
                        controller.connect();
                        break;
                    case "ConnectAddr.":
                        System.out.print("Enter IP: ");
                        String ip = getUserInput();
                        if (!ip.equals("")) {
                            controller.connectAdr(ip);
                        } else {
                            controller.connectAdr("192.168.0.11");
                        }
                        break;
                    case "Disconnect.":
                        receivingCommands = false;
                        controller.disconnect();
                        break;
                    default:
                        controller.sendMsg(userMsg);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String messageSwitch(String msg) {
        String msgFromServer;
        switch (msg){
            case "w":
                msgFromServer = "You win. New game started";
                break;
            case "l":
                msgFromServer = "You lose. New game started";
                break;
            case "n":
                msgFromServer = "New Game Started";
                break;
            case "r":
                msgFromServer = "You restated the game.";
                break;
            case "d":
                msgFromServer = "Disconnect";
                break;
            case "cg":
                msgFromServer = "Correct Guess";
                break;
            case "wg":
                msgFromServer = "Wrong Guess";
                break;
            case "nr":
                msgFromServer = "Input not recognized. Try again.";
                break;
            default:
                msgFromServer = "Unknown Message";
        }
        return msgFromServer;
    }


    public void showOutput(String fromServer){
        if (fromServer.equals("Disconnect")) {
            System.out.println("Disconnected from server");
        } else {
            String[] dataToShow = fromServer.split("/");
            System.out.println("_______________________________________________");
            System.out.println(">>> " + messageSwitch(dataToShow[3]) + " <<<");
            System.out.println("Score: " + dataToShow[0] + "     Attempts: " + dataToShow[1]);
            System.out.println("Word:   " + dataToShow[2].substring(0,3));
            System.out.println("_______________________________________________");
            System.out.print(PROMPT);
        }
    }
}

