package edu.rit.cs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread to be spawned from ServerHandlerThread
 * Handles each agent's send/receive connection to the server
 */
public class ServerWorkerThread implements Runnable {

  // The event manager server socket
  private ServerSocket serverSocket;
  private EventManager eventManager;

  public ServerWorkerThread(ServerSocket serverSocket, EventManager eventManager) {
    this.serverSocket = serverSocket;
    this.eventManager = eventManager;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Socket clientSocket = this.serverSocket.accept();
        // Threads for the server to send/receive from an agent
        ServerSendThread send = new ServerSendThread(clientSocket);
        ServerReceiveThread receive = new ServerReceiveThread(clientSocket, this.eventManager);
        receive.setSendThread(send);
        send.start();
        receive.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
