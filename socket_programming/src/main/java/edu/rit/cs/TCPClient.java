package edu.rit.cs;

import java.net.*;
import java.io.*;

public class TCPClient {

    public static void main(String args[]) {
        // arguments supply message and hostname of destination
        String message = args[0];
        String server_address = args[1];

        Socket s = null;
        try {
            int serverPort = 9092;
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
            if (s != null)
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
        }
    }
}

