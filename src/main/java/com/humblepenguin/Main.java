package com.humblepenguin;

import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.runtime.Quarkus;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import javafx.application.Application;
import picocli.CommandLine;

import com.humblepenguin.MainClient;
import com.humblepenguin.TCPServer;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = { StartServer.class, StartClient.class })
public class Main {

}

@CommandLine.Command(name = "server", description = "Start the server on port 8080")
class StartServer implements Runnable {

  @Override
  public void run() {
    System.out.println("Starting server");
    TCPServer server = new TCPServer();

    server.openPort(8080); 
    server.handleEntry(); 
  }
}

@CommandLine.Command(name = "client", description = "Start a client instance to connect to the server")
class StartClient implements Runnable {

  @Override
  public void run() {
    Application.launch(MainClient.class, new String[]{});
  }
}