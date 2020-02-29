package edu.rit.cs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles the creation of worker threads per connection to the event manager
 */
public class ServerHandlerThread implements Runnable {

  // Starting client port, to be incremented by 1 per connection
  private int CLIENT_PORT = 11000;
  // Event manager server's socket
  private ServerSocket serverSocket;
  private EventManager eventManager;

  public ServerHandlerThread(EventManager eventManager) {
    this.eventManager = eventManager;
  }

  @Override
  public void run() {
    try {
      this.serverSocket = new ServerSocket(Constants.SERVER_PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // While the server is running, we accept connections to the SERVER_PORT and send the agent
    // a free port to connect to, this allows us ease of programming and flexibility with Docker containers
    while (true) {
      try {
        Socket clientSocket = this.serverSocket.accept();
        OutputStream os = clientSocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        // Send the agent a free port and increment for next agent
        int newClientPort = this.CLIENT_PORT++;
        bw.write("" + newClientPort);
        bw.flush();
        clientSocket.close();
        new Thread(new ServerWorkerThread(new ServerSocket(newClientPort), this.eventManager))
            .start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
