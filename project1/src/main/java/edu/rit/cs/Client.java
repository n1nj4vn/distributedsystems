package edu.rit.cs;

import java.io.*;
import java.net.*;

/**
 * This Client will send the affr.csv to the Server program. The Server program will perform a word
 * count on the file and generate a text file containing all results. It will then send the text
 * file back to the Client who will then print the result.
 */
public class Client {

  public static void main(String args[]) {
    // arguments supply location of affr.csv file
    String csv_location = args[0];
    String word_count = "word_count_client.txt";
    String server_address = "172.16.238.2";

    Socket s = null;
    try {
      int serverPortRecv = 7896;
      s = new Socket(server_address, serverPortRecv);
      // Send the CSV file
      FileInputStream fis;
      BufferedInputStream bis;
      OutputStream os;
      File fileToSend = new File(csv_location);
      byte[] fileByteArray = new byte[(int) fileToSend.length()];
      fis = new FileInputStream(fileToSend);
      bis = new BufferedInputStream(fis);
      bis.read(fileByteArray, 0, fileByteArray.length);
      os = s.getOutputStream();
      os.write(fileByteArray, 0, fileByteArray.length);
      os.flush();
      System.out.println("CSV Sent!");
    } catch (UnknownHostException e) {
      System.out.println("Sock: " + e.getMessage());
    } catch (EOFException e) {
      System.out.println("EOF: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    } finally {
      if (s != null) {
        try {
          s.close();
        } catch (IOException e) {
          System.out.println("close:" + e.getMessage());
        }
      }
    }

    try {
      Thread.sleep(1000 * 25);
      int serverPortRecv = 7897;
      s = new Socket(server_address, serverPortRecv);
      // Receive the results file
      FileOutputStream fos;
      BufferedOutputStream bos;
      int bytesRead;
      int current = 0;
      byte[] fileByteArray = new byte[450000];
      InputStream is = s.getInputStream();
      fos = new FileOutputStream(word_count);
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

      System.out.println("Results received from Server");

      BufferedReader br = new BufferedReader(new FileReader(new File(word_count)));
      String currResult = br.readLine();
      while (currResult != null) {
        System.out.println(currResult);
        currResult = br.readLine();
      }
      br.close();
      System.out.println("End of Results!");
    } catch (UnknownHostException e) {
      System.out.println("Sock: " + e.getMessage());
    } catch (EOFException e) {
      System.out.println("EOF: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    } catch (InterruptedException e) {
      e.printStackTrace();
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

