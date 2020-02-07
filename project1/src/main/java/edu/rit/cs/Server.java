package edu.rit.cs;

import java.io.*;
import java.net.*;

/**
 * This Server will receive a file from a Client to perform a word count on and generate a text
 * file containing all results. It will then send the text file back to the Client who will then
 * print the result.
 */
public class Server {

  public static void main(String args[]) {
//    String client_address = "172.16.238.3";
    String client_address = "localhost";
    int serverPort = 7896;
    int bytesRead;
    int current = 0;
    FileOutputStream fos;
    BufferedOutputStream bos;
    Socket s;

    try {
      s = new Socket(client_address, serverPort);
      byte[] fileByteArray = new byte[300000000];
      InputStream is = s.getInputStream();
      fos = new FileOutputStream("project1/words_recv.txt");
      bos = new BufferedOutputStream(fos);
      bytesRead = is.read(fileByteArray, 0, fileByteArray.length);
      current = bytesRead;

      do {
        bytesRead =
            is.read(fileByteArray, current, (fileByteArray.length - current));
        if (bytesRead >= 0) {
          current += bytesRead;
        }
      } while (bytesRead > -1);

      bos.write(fileByteArray, 0, current);
      bos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

//    try {
//      ServerSocket listenSocket = new ServerSocket(serverPort);
//      System.out.println("TCP Server is running and accepting client connections...");
//      while (true) {
//        Socket clientSocket = listenSocket.accept();
//        Connection c = new Connection(clientSocket);
//      }
//    } catch (IOException e) {
//      System.out.println("Listen :" + e.getMessage());
//    }
  }
}

class Connection extends Thread {

  DataInputStream in;
  DataOutputStream out;
  Socket clientSocket;

  public Connection(Socket aClientSocket) {
    try {
      clientSocket = aClientSocket;
      in = new DataInputStream(clientSocket.getInputStream());
      out = new DataOutputStream(clientSocket.getOutputStream());
      this.start();
    } catch (IOException e) {
      System.out.println("Connection:" + e.getMessage());
    }
  }

  public void run() {
    try {   // an echo server
      String data = in.readUTF();
      System.out.println("Received \"" + data + "\", echo back now...");
      out.writeUTF(data);
    } catch (EOFException e) {
      System.out.println("EOF:" + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO:" + e.getMessage());
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {/*close failed*/}
    }
  }
}
