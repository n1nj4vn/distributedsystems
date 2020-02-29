package edu.rit.cs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Thread for the Server to send messages to the Publisher/Subscriber Client (Agent)
 * (AgentReceiveThread) through Java Sockets
 */
public class ServerSendThread extends Thread {

  // The agent's socket
  private Socket clientSocket;
  private boolean isThreadActive;
  // Message to be sent
  private String message;
  // If there is a message to be sent to the agent
  private boolean pendingMessage;

  public ServerSendThread(Socket clientSocket) {
    this.clientSocket = clientSocket;
    this.isThreadActive = true;
    this.pendingMessage = false;
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
   * Get the stored message
   *
   * @return String stored message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Store the message to be sent
   *
   * @param message message to be sent
   */
  public void setMessage(String message) {
    this.message = message + "\n";
  }

  /**
   * Return if there is a message to be sent to the agent
   *
   * @return true if there is a message to be sent to the agent
   */
  public boolean isPendingMessage() {
    return pendingMessage;
  }

  /**
   * Set if there is a message to be sent to the agent
   *
   * @param pendingMessage true if there is a message to be sent to the agent
   */
  public void setPendingMessage(boolean pendingMessage) {
    this.pendingMessage = pendingMessage;
  }

  @Override
  public void run() {
    try {
      BufferedWriter bw = new BufferedWriter(
          new OutputStreamWriter(clientSocket.getOutputStream()));
      while (isThreadActive()) {
        // Send the message to the agent if one is pending
        if (isPendingMessage()) {
          bw.write(getMessage());
          bw.flush();
          // Log the user out
          if (message.equals(Constants.LOGOUT)) {
            setThreadActive(false);
          } else {
            setMessage(null); // clear message so we do not send duplicate actions
            setPendingMessage(false);
          }
        }
        Thread.sleep(1000); // Allow time for processing
      }
      bw.close();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
