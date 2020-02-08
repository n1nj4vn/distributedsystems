package edu.rit.cs;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Server will receive a file from a Client to perform a word count on and generate a text file
 * containing all results. It will then send the text file back to the Client who will then print
 * the result.
 */
public class Server {

  public static void main(String args[]) {
    int serverPortRecv = 7896;
    int serverPortSend = 7897;

    try {
      ServerSocket listenSocketRecv = new ServerSocket(serverPortRecv);
      ServerSocket listenSocketSend = new ServerSocket(serverPortSend);
      System.out.println("TCP Server is running and accepting client connections...");
      while (true) {
        Socket clientSocketRecv = listenSocketRecv.accept();
        ConnectionRecv cr = new ConnectionRecv(clientSocketRecv);
        //Thread.sleep(1000*15);
        Socket clientSocketSend = listenSocketSend.accept();
        ConnectionSend cs = new ConnectionSend(clientSocketSend);
      }
    } catch (IOException e) {
      System.out.println("Listen: " + e.getMessage());
    }
  }
}

class ConnectionRecv extends Thread {

  DataInputStream in;
  DataOutputStream out;
  Socket clientSocket;

  public ConnectionRecv(Socket aClientSocket) {
    try {
      clientSocket = aClientSocket;
      in = new DataInputStream(clientSocket.getInputStream());
      out = new DataOutputStream(clientSocket.getOutputStream());
      this.start();
    } catch (IOException e) {
      System.out.println("Connection: " + e.getMessage());
    }
  }

  public void run() {
    String csv_location = "amazon_reviews_recv.csv";

    // Here we are receiving the CSV file from the Client
    try {
      FileOutputStream fos;
      BufferedOutputStream bos;
      int bytesRead;
      int current = 0;
      byte[] fileByteArray = new byte[310000000];
      fos = new FileOutputStream(csv_location);
      bos = new BufferedOutputStream(fos);
      bytesRead = in.read(fileByteArray, 0, fileByteArray.length);
      current = bytesRead;

      do {
        bytesRead =
            in.read(fileByteArray, current, (fileByteArray.length - current));
        if (bytesRead >= 0) {
          current += bytesRead;
        }
      } while (bytesRead > -1);

      bos.write(fileByteArray, 0, current);
      bos.flush();

      System.out.println("CSV Received");

      List<AmazonFineFoodReview> allReviews = read_reviews(csv_location);
      List<KV<String, Integer>> kv_pairs = map(allReviews);
      Map<String, Integer> results = reduce(kv_pairs);
      print_word_count(results);

      System.out.println("Word Count Complete");
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

  /**
   * Read and parse all reviews
   *
   * @return list of reviews
   */
  public static List<AmazonFineFoodReview> read_reviews(String dataset_file) {
    List<AmazonFineFoodReview> allReviews = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(dataset_file))) {
      String reviewLine = null;
      // read the header line
      reviewLine = br.readLine();

      //read the subsequent lines
      while ((reviewLine = br.readLine()) != null) {
        allReviews.add(new AmazonFineFoodReview(reviewLine));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return allReviews;
  }

  /**
   * Print the list of words and their counts
   */
  public static void print_word_count(Map<String, Integer> wordcount) throws IOException {
    String word_count = "word_count_server.txt";
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(word_count)));
    for (String word : wordcount.keySet()) {
      writer.write(word + " : " + wordcount.get(word));
      writer.newLine();
    }
    writer.flush();
    writer.close();
  }

  /**
   * Emit 1 for every word and store this as a <key, value> pair
   */
  public static List<KV<String, Integer>> map(List<AmazonFineFoodReview> allReviews) {
    List<KV<String, Integer>> kv_pairs = new ArrayList<KV<String, Integer>>();

    for (AmazonFineFoodReview review : allReviews) {
      Pattern pattern = Pattern.compile("([a-zA-Z]+)");
      Matcher matcher = pattern.matcher(review.get_Summary());

      while (matcher.find()) {
        kv_pairs.add(new KV(matcher.group().toLowerCase(), 1));
      }
    }
    return kv_pairs;
  }


  /**
   * count the frequency of each unique word
   *
   * @return a list of words with their count
   */
  public static Map<String, Integer> reduce(List<KV<String, Integer>> kv_pairs) {
    Map<String, Integer> results = new HashMap<>();

    for (KV<String, Integer> kv : kv_pairs) {
      if (!results.containsKey(kv.getKey())) {
        results.put(kv.getKey(), kv.getValue());
      } else {
        int init_value = results.get(kv.getKey());
        results.replace(kv.getKey(), init_value, init_value + kv.getValue());
      }
    }
    return results;
  }
}

class ConnectionSend extends Thread {

  DataInputStream in;
  DataOutputStream out;
  Socket clientSocket;

  public ConnectionSend(Socket aClientSocket) {
    try {
      clientSocket = aClientSocket;
      in = new DataInputStream(clientSocket.getInputStream());
      out = new DataOutputStream(clientSocket.getOutputStream());
      this.start();
    } catch (IOException e) {
      System.out.println("Connection: " + e.getMessage());
    }
  }

  public void run() {
    // Here we are receiving the CSV file from the Client
    try {
      String word_count = "word_count_server.txt";
      FileInputStream fis;
      BufferedInputStream bis;
      OutputStream os;
      File fileToSend = new File(word_count);
      byte[] fileByteArray = new byte[(int) fileToSend.length()];
      fis = new FileInputStream(fileToSend);
      bis = new BufferedInputStream(fis);
      bis.read(fileByteArray, 0, fileByteArray.length);
      os = clientSocket.getOutputStream();
      os.write(fileByteArray, 0, fileByteArray.length);
      os.flush();

      System.out.println("Results sent to Client");
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
