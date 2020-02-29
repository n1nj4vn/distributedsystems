package edu.rit.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Thread for the Publisher/Subscriber Client (Agent) to receive messages from the Server
 * (ServerSendThread) through Java Sockets
 */
public class AgentReceiveThread extends Thread {

  // Reference to the pubSubAgent running this thread
  private PubSubAgent pubSubAgent;
  // Reference to the socket to receive messages from
  private Socket socket;
  private String message;

  public AgentReceiveThread(PubSubAgent pubSubAgent, Socket socket) {
    this.pubSubAgent = pubSubAgent;
    this.socket = socket;
    this.message = null;
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.message = br.readLine().trim(); // Remove extraneous message text
      while (this.pubSubAgent.isClientActive) {
        System.out.println(message);
        this.message = br.readLine().trim();
        // Notify the client they have been successfully logged out and can restart the agent
        if (message.equals(Constants.LOGOUT)) {
          this.pubSubAgent.isClientActive = false;
          System.out.println("You have been logged out successfully!");
        }
      }
      br.close();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}