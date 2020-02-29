package edu.rit.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * PubSubAgent
 */
public class PubSubAgent implements Publisher, Subscriber {

  private String serverIP;
  private Socket clientSocket;
  private int clientPort;
  protected boolean isClientActive;

  /**
   * The functions below have been implemented in the respective Publisher/Subscriber threads
   */

  @Override
  public void subscribe(Topic topic) {
  }

  @Override
  public void subscribe(String keyword) {
  }

  @Override
  public void unsubscribe(Topic topic) {
  }

  @Override
  public void unsubscribe() {
  }

  @Override
  public void listSubscribedTopics() {
  }

  @Override
  public void publish(Event event) {
  }

  @Override
  public void advertise(Topic newTopic) {
  }

  /**
   * Get the event manager's (server) IP
   *
   * @return String the event manager's (server) IP
   */
  public String getServerIP() {
    return serverIP;
  }

  /**
   * Set the event manager's (server) IP
   *
   * @param serverIP the event manager's (server) IP
   */
  public void setServerIP(String serverIP) {
    this.serverIP = serverIP;
  }

  /**
   * Receive the designated port to use from the server for this session (allowing for simultaneous
   * connections)
   *
   * @param serverIP the Event Manager IP
   */
  public void getPort(String serverIP) {
    try {
      this.clientSocket = new Socket(serverIP, Constants.SERVER_PORT);

      BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      this.clientPort = Integer.parseInt(br.readLine());
      br.close();
      this.clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Connect to the event manager using the port received (allowing for simultaneous connections)
   */
  public void connectToEventManager() {
    try {
      this.clientSocket = new Socket(serverIP, clientPort);
      this.isClientActive = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Incorrect number of arguments! Please specify only the Event Manager IP");
    }
    PubSubAgent agent = new PubSubAgent();
    agent.setServerIP(args[0]);
    agent.getPort(agent.getServerIP());
    agent.connectToEventManager();
    // Scanner to read in CLI from the agent
    Scanner scanner = new Scanner(System.in);
    // Threads for the agent to send/receive to the event manager (server)
    AgentReceiveThread receiveThread = new AgentReceiveThread(agent, agent.clientSocket);
    AgentSendThread sendThread = new AgentSendThread(agent, agent.clientSocket, scanner);
    receiveThread.start();
    sendThread.start();
  }

}
