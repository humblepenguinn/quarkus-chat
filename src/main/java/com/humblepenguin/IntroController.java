package com.humblepenguin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.InetAddress;

/**
 * Intro Controller Class - handles the intro scene
 */
public class IntroController {
    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField serverPortField;
    @FXML
    private TextField userNameField;
    @FXML
    private Button connectButton;
    @FXML
    private ImageView logo;

    private MainClient mainClient;

    /**
     * Initialize the scene
     */
    @FXML
    private void initialize() {
        String imageUrl = MainClient.class.getResource("logo.png").toExternalForm();
        logo.setImage(new Image(imageUrl));

        serverAddressField.setPromptText("server address");
        serverAddressField.setText("localhost");
        serverPortField.setPromptText("server port");
        serverPortField.setText("8080");
        userNameField.setPromptText("user name");

        connectButton.setDefaultButton(true);

        try {
            System.out.printf("\nClient running (%s)...\n\n", InetAddress.getLocalHost());
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /*
    * Method to initialize the reference to the main client
    */
    public void setMainClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }

    /*
    * 'Login' button action handler
    */
    @FXML
    private void handleConnectButton() {
        if (serverPortField.getText().isEmpty() || serverAddressField.getText().isEmpty() || userNameField.getText().isEmpty()) {
            showWarningAlert("Empty input field!", "Please enter the server details and user name before hitting 'Login'!");
        } else {
            String serverAddress = serverAddressField.getText();
            int serverPort = Integer.parseInt(serverPortField.getText());
            String userName = userNameField.getText();

            boolean connectedSuccessfully = TCPClient.connectToServer(serverAddress, serverPort, userName, this);

            if (connectedSuccessfully) {
                mainClient.initChatScene();
            }
        }
    }

    /*
    * Show warning alert
    */
    public void showWarningAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(mainClient.getStage());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}