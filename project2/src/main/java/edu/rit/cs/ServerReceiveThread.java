package edu.rit.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Thread for the Server to receive messages from the Publisher/Subscriber Client (Agent)
 * (AgentSendThread) through Java Sockets
 */
public class ServerReceiveThread extends Thread {

  // Reference to the socket to receive messages from
  private Socket clientSocket;
  private EventManager eventManager;
  // Reference to the parallel thread sending messages to the agent
  private ServerSendThread sendThread;
  private boolean isThreadActive;
  // The connected agent's credentials
  private String username;

  public ServerReceiveThread(Socket clientSocket, EventManager eventManager) {
    this.clientSocket = clientSocket;
    this.eventManager = eventManager;
    this.isThreadActive = true;
  }

  /**
   * Return if this thread should be active (agent logged in)
   *
   * @return thread activity
   */
  public boolean isThreadActive() {
    return isThreadActive;
  }

  /**
   * Set if this thread is active (agent logged in) or not (agent logged out)
   *
   * @param threadActive thread activity
   */
  public void setThreadActive(boolean threadActive) {
    isThreadActive = threadActive;
  }

  /**
   * Get the agent's username
   *
   * @return String username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Store the agent's username
   *
   * @param username agent's username (existing/created)
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Get the reference to the parallel thread sending messages to the agent
   *
   * @return ServerSendThread sending messages to the agent
   */
  public ServerSendThread getSendThread() {
    return sendThread;
  }

  /**
   * Set the reference to the parallel thread sending messages to the agent
   *
   * @param sendThread ServerSendThread sending messages to the agent
   */
  public void setSendThread(ServerSendThread sendThread) {
    this.sendThread = sendThread;
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      while (isThreadActive()) {
        String message = br.readLine()
            .trim(); // formatted using @@@ as a delimiter for ease of programming
        String[] splitMessage = message.split("@@@");
        setUsername(splitMessage[2]);
        // We call functions in the event manager based on the option received from the agent
        // Then we call functions in the sending thread to notify the agent/return results
        switch (splitMessage[0]) {
          case Constants.REGISTER:
            String newPassword = splitMessage[3];
            if (eventManager.createUser(getUsername(), newPassword)) {
              sendThread.setMessage("Account Created! Successfully Logged In!");
              sendThread.setPendingMessage(true);
              eventManager.addActiveLogin(getUsername(), getSendThread());
            } else {
              sendThread.setMessage("Existing Username! Please try with a different username!");
              sendThread.setPendingMessage(true);
            }
            break;
          case Constants.LOGIN:
            String checkPassword = splitMessage[3];
            String serverPassword = eventManager.getPassword(getUsername());
            if (serverPassword.equals(checkPassword)) {
              sendThread.setMessage("Successfully Logged In!");
              sendThread.setPendingMessage(true);
              eventManager.addActiveLogin(getUsername(), getSendThread());
              eventManager.notifyInactiveSubscriber(getUsername());
            } else {
              sendThread.setMessage("Unsuccessful Log In!");
              sendThread.setPendingMessage(true);
            }
            break;
          case Constants.LOGOUT:
            sendThread.setMessage("Successfully Logged Out!");
            sendThread.setPendingMessage(true);
            eventManager.removeActiveLogin(getUsername());
            setThreadActive(false);
            break;
          case Constants.SUBSCRIBE:
            eventManager.addSubscriber(new Topic(null, splitMessage[4]), getUsername());
            break;
          case Constants.UNSUBSCRIBE:
            eventManager.removeSubscriber(new Topic(null, splitMessage[4]), getUsername());
            sendThread.setMessage("Unsubscribed!");
            sendThread.setPendingMessage(true);
            break;
          case Constants.ADVERTISE:
            boolean isExisting = eventManager.addTopic(new Topic(null, splitMessage[4]));
            if (!isExisting) {
              sendThread.setMessage(splitMessage[4] + " exists!");
              sendThread.setPendingMessage(true);
            }
            break;
          case Constants.PUBLISH:
            if (eventManager.doesTopicExist(splitMessage[4])) {
              eventManager.notifySubscribers(
                  new Event(eventManager.getEventID(), new Topic(null, splitMessage[4]),
                      splitMessage[6], splitMessage[8]), getUsername());
            } else {
              sendThread.setMessage("Topic does not exist!");
              sendThread.setPendingMessage(true);
            }
            break;
          case Constants.SUBSCRIBED_TOPICS:
            sendThread.setMessage(eventManager.getSubscribedTopics(getUsername()));
            sendThread.setPendingMessage(true);
            break;
          case Constants.LIST_TOPICS:
            sendThread.setMessage(eventManager.getTopics());
            sendThread.setPendingMessage(true);
            break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
