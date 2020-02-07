package edu.rit.cs;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This Client will read the affr.csv and send it to the Server program. The Server program will
 * perform a word count on the file and generate a text file containing all results. It will then
 * send the text file back to the Client who will then print the result.
 */
public class Client {

  public static void main(String args[]) {
    // arguments supply message and hostname of destination
    // String csv_location = args[0];
    String csv_location = "project1/affr.csv";
    String text_file_location = "project1/words_send.txt";
    String server_address = "172.16.238.2";
    int serverPort = 7896;

    try {
      BufferedReader reader = new BufferedReader(new FileReader(new File(csv_location)));
      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(text_file_location)));

      String nextLine = "";
      String[] lineArray;
      while ((nextLine = reader.readLine()) != null) {
        lineArray = nextLine.split("[^a-zA-Z0-9']+");
        for (String word : lineArray) {
          writer.write(word);
          writer.newLine();
        }
      }

      reader.close();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Socket s;
    ServerSocket ss;
    FileInputStream fis;
    BufferedInputStream bis;
    OutputStream os;
    try {
      ss = new ServerSocket(serverPort);
      System.out.println("Waiting");
      while (true) {
        try {
          s = ss.accept();
          System.out.println("Accepted connection: " + s);
          // send file
          File fileToSend = new File(text_file_location);
          byte[] fileByteArray = new byte[(int) fileToSend.length()];
          fis = new FileInputStream(fileToSend);
          bis = new BufferedInputStream(fis);
          bis.read(fileByteArray, 0, fileByteArray.length);
          os = s.getOutputStream();
          os.write(fileByteArray, 0, fileByteArray.length);
          os.flush();
          s.close();
          return;
        } catch (IOException ex) {
          ex.printStackTrace();
        }
//        } finally {
//          if (bis != null) bis.close();
//          if (os != null) os.close();
//          if (s!=null) s.close();
//        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

//    try {
//    s = new Socket(server_address, serverPort);
//      DataInputStream in = new DataInputStream(s.getInputStream());
//      DataOutputStream out =
//          new DataOutputStream(s.getOutputStream());
//      out.writeUTF(message);
//      System.out.println("Sent: " + message);
//      String data = in.readUTF();
//      System.out.println("Received: " + data);
//    } catch (UnknownHostException e) {
//      System.out.println("Sock:" + e.getMessage());
//    } catch (EOFException e) {
//      System.out.println("EOF:" + e.getMessage());
//    } catch (IOException e) {
//      System.out.println("IO:" + e.getMessage());
//    } finally {
//      if (s != null) {
//        try {
//          s.close();
//        } catch (IOException e) {
//          System.out.println("close:" + e.getMessage());
//        }
//      }
//    }

  }
}
