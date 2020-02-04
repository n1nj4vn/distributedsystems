package edu.rit.cs;

import java.io.*;
import java.net.*;

public class Client {

  public static void main(String args[]) {
    // arguments supply message and hostname of destination
    String message = "TEST_MESSAGE";
    String server_address = "172.16.238.2";

    Socket s = null;
    try {
      int serverPort = 7896;
      s = new Socket(server_address, serverPort);
      DataInputStream in = new DataInputStream(s.getInputStream());
      DataOutputStream out =
          new DataOutputStream(s.getOutputStream());
      out.writeUTF(message);
      System.out.println("Sent: " + message);
      String data = in.readUTF();
      System.out.println("Received: " + data);
    } catch (UnknownHostException e) {
      System.out.println("Sock:" + e.getMessage());
    } catch (EOFException e) {
      System.out.println("EOF:" + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO:" + e.getMessage());
    } finally {
      if (s != null) {
        try {
          s.close();
        } catch (IOException e) {
          System.out.println("close:" + e.getMessage());
        }
      }
    }
  }
}
