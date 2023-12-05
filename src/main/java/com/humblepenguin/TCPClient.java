package com.humblepenguin;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * TCP Client Class
 * It handles the connection to the server and the communication with it.
 * It has been modified to work with JavaFX. Before it was a console application.
 */
public class TCPClient {
    private static InetAddress serverAddress;
    private static Socket socket;
    private static String userName;
    private static MessageListener messageListener;
    private static Scanner chatInput;
    private static PrintWriter chatOutput;

    public static Scanner getChatInput() {
        return chatInput;
    }

    public static void initialize(ClientController client) {
        Thread alive = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60000);
                        chatOutput.println("ALVE i am " + userName + ".");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        alive.start();
        messageListener = new MessageListener(client);
        messageListener.start();
    }

    public static boolean connectToServer(String serverIP, int serverPort, String user, IntroController introController) {
        boolean connectedSuccessful = false;
        boolean isUserNameOK;

        do {
            isUserNameOK = checkUserName(user);

            if (isUserNameOK) {
                try {
                    serverAddress = InetAddress.getByName(serverIP);
                    socket = new Socket(serverAddress, serverPort);
                    chatOutput = new PrintWriter(socket.getOutputStream(), true);
                    chatInput = new Scanner(socket.getInputStream());
                } catch (Exception e) {
                    try {
                        introController.showWarningAlert("Bad address!", "Server address not found.\nPlease try again.");
                        return connectedSuccessful;
                    } catch (Exception f) {
                        System.out.println(f);
                        return connectedSuccessful;
                    }
                }

                chatOutput.printf("JOIN %s, %s:%s\n", userName, serverAddress, serverPort);
                String response = chatInput.nextLine();

                switch (response) {
                    case "J_OK":
                        System.out.println("Successfully connected to the server");
                        connectedSuccessful = true;
                        break;
                    case "J_ERR":
                        introController.showWarningAlert("Username taken!", "This username is already in use.\nTry another username.");
                        break;
                }

                return connectedSuccessful;
            } else {
                introController.showWarningAlert("Wrong input!", "Your username should be max 12 characters long and should only contain chars, digits, '-' and '_'");
                return connectedSuccessful;
            }

        } while (!isUserNameOK);
    }

    public static void sendButton(String message) {
        String dataMessage;
        dataMessage = "DATA " + userName + ": " + message;
        chatOutput.println(dataMessage);
    }

    public static void exit() {
        System.out.println("Closing connection...");
        messageListener.stopRunning();
        chatOutput.println("QUIT i am " + userName + ".");
        System.exit(1);
    }

    private static boolean checkUserName(String user) {
        userName = user;
        if (userName.length() < 13 && userName.matches("^[a-zA-Z0-9_-]+$")) {
            return true;
        } else {
            System.out.println("Wrong username input!");
            return false;
        }
    }
}

class MessageListener extends Thread {
    private String response, key;
    private Scanner keyScanner;
    private ClientController clientController;
    private int getMessageFails = 0;
    private volatile boolean running = true;

    public MessageListener(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void run() {
        while (running) {
            try {
                response = TCPClient.getChatInput().nextLine();
                keyScanner = new Scanner(response);
                key = keyScanner.next();

                switch (key) {
                    case "DATA":
                        clientController.handleChatField(response.substring(response.indexOf(" ") + 1));
                        break;
                    case "LIST":
                        String userList = response.substring(response.indexOf(" ") + 1);
                        clientController.handleActiveUsersField(userList);
                        break;
                    case "J_ERR":
                        clientController.handleChatField("\nAn Error occured. Closing connection");
                        System.out.println("Closing connection..");
                        stopRunning();
                        break;
                    default:
                        clientController.handleChatField("Server system message: " + response);
                }
            } catch (Exception e) {
                getMessageFails++;
                if (getMessageFails > 1) {
                    stopRunning();
                    System.out.println("Too many exceptions occured. Closing connection..");
                    System.exit(1);
                }
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
