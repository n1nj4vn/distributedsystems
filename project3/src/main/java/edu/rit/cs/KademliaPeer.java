package edu.rit.cs;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class represents a Peer in a Kademlia DHT P2P Network.
 */
public class KademliaPeer extends Thread implements Peer {

  enum STATUS {
    ONLINE, OFFLINE;
  }

  private static KademliaPeer kPeer;
  private static URL serverURL = null; // For contacting the miniServer
  private static URL clientURL = null; // For contacting other peers
  static int nodeID;
  static String ipAddress;
  private static String port;
  private static boolean isOnline = false;
  private static boolean isAnchor = false;
  private static ArrayList<Integer> idealSuccessor = new ArrayList<>();
  private static ArrayList<Integer> actualSuccessor = new ArrayList<>();
  private static ArrayList<String> actualSuccessorIP = new ArrayList<>();
  private static ArrayList<FakeFile> files = new ArrayList<>();
  private static ArrayList<String> onlinePeers = new ArrayList<>();
  private static int requestID = -1;

  /**
   * The port that the peer listens on.
   */
  private static final int PORT = Integer.parseInt(Config.DEFAULT_PORT_PREFIX);

  /**
   * Construct a new Kademlia Peer
   * @param nodeID peer's ID
   * @param ipAddress peer's IP address
   * @param port peer's port
   */
  public KademliaPeer(int nodeID, String ipAddress, String port) {
    this.nodeID = nodeID;
    this.ipAddress = ipAddress;
    this.port = port;
    startPeer();
    this.isOnline = true;
  }

  /**
   * Start a Handler to listen for incoming RPC to this Kademlia Peer!
   */
  public void run() {
    System.out.println("The Kademlia Peer Handler is running.");
    ServerSocket listener = null;
    try {
      listener = new ServerSocket(PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      while (true) {
        new Handler(listener.accept()).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        listener.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * Start this peer up by contacting the miniServer
   */
  public void startPeer() {
    tellMiniServer(KademliaPeer.STATUS.ONLINE);
  }

  /**
   * Shut this peer down by contacting the miniServer
   */
  public void stopPeer() {
    tellMiniServer(KademliaPeer.STATUS.OFFLINE);
  }

  /**
   * Perform any startup operations required for a new peer
   */
  public void startNewPeer() {
    kPeer.initializeFingerTable();
  }

  /**
   * Check if the specified peer is online
   * @param peerID specified peer
   * @return true if online, false if offline
   */
  public boolean isPeerOnline(int peerID) {
    // Create new JSON-RPC 2.0 client session
    JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

    // Construct new request
    JSONRPC2Request request = new JSONRPC2Request("ispeeronline", requestID++);
    Map<String, Object> myParams = new HashMap<String, Object>();
    myParams.put("id", peerID);
    request.setNamedParams(myParams);

    // Send request
    JSONRPC2Response response = null;
    try {
      response = mySession.send(request);
    } catch (JSONRPC2SessionException e) {
      System.err.println(e.getMessage());
      // handle exception...
    }

    // Print response result / error
    if (response.indicatesSuccess()) {
      return (Boolean) response.getResult();
    } else {
      System.out.println(response.getError().getMessage());
      return false;
    }
  }

  public void tellMiniServer(KademliaPeer.STATUS status) {
    // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
    // The JSON-RPC 2.0 server URL
    try {
      serverURL = new URL("http://" + Config.SERVER_IP + ":" + Config.SERVER_PORT);
    } catch (MalformedURLException e) {
      // handle exception...
    }

    // Create new JSON-RPC 2.0 client session
    JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

    // Construct new request
    JSONRPC2Request request;
    Map<String, Object> myParams = new HashMap<String, Object>();
    myParams.put("id", this.nodeID);
    switch (status) {
      case ONLINE:
        request = new JSONRPC2Request("online", requestID++);
        myParams.put("ip", this.ipAddress);
        myParams.put("port", this.port);
        break;
      case OFFLINE:
      default:
        request = new JSONRPC2Request("offline", requestID);
        break;
    }
    request.setNamedParams(myParams);

    // Send request
    JSONRPC2Response response = null;
    try {
      response = mySession.send(request);
    } catch (JSONRPC2SessionException e) {
      System.err.println(e.getMessage());
      // handle exception...
    }

    // Print response result / error
    if (response.indicatesSuccess()) {
      if ((Boolean) response.getResult()) {
        System.out.println("Successful!");
      } else {
        System.out.println("Failed!");
      }
    } else {
      System.out.println(response.getError().getMessage());
    }
  }

  /**
   * We have received notice a new peer has joined the network!
   * @param newPeerIP the new peer's IP address
   * @param port the new peer's port number
   */
  public static void newPeerOnline(String newPeerIP, String port) {
    System.out.println("NEW PEER: " + newPeerIP + ":" + port);
  }
  /**
   * Get all the peers currently online
   */
  public static void getOnlinePeers() {
    // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
    // The JSON-RPC 2.0 server URL
    try {
      serverURL = new URL("http://" + Config.SERVER_IP + ":" + Config.SERVER_PORT);
    } catch (MalformedURLException e) {
      // handle exception...
    }

    // Create new JSON-RPC 2.0 client session
    JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

    // Construct new request
    JSONRPC2Request request;
    Map<String, Object> myParams = new HashMap<String, Object>();
    myParams.put("id", nodeID);
    myParams.put("ip", ipAddress);
    request = new JSONRPC2Request("getOnlinePeers", requestID++);
    request.setNamedParams(myParams);

    // Send request
    JSONRPC2Response response = null;
    try {
      response = mySession.send(request);
    } catch (JSONRPC2SessionException e) {
      System.err.println(e.getMessage());
      // handle exception...
    }

    // Print response result / error
    if (response.indicatesSuccess()) {
      if ((Boolean) response.getResult()) {
        System.out.println("Successful!");
      } else {
        System.out.println("Failed!");
      }
    } else {
      System.out.println(response.getError().getMessage());
    }
  }

  public static void saveOnlinePeers(ArrayList<String> newOnlinePeers) {
    onlinePeers = newOnlinePeers;
  }

  /**
   * Calculate the XOR distance between two nodes per Kademlia specifications
   * @param nodeID current node
   * @param destID destination node
   * @return XOR distance
   */
  public int distance(int nodeID, int destID) {
    return nodeID ^ destID;
  }

  /**
   * Return the closest node to the specified value
   * @param value specified value
   * @param onlineNodes list of online nodes
   * @return closest node ID
   */
  public int closestNode(int value, List<Integer> onlineNodes) {
    int min = Integer.MAX_VALUE;
    int closest = value;

    for (int node : onlineNodes) {
      final int diff = Math.abs(node - value);
      if (diff < min) {
        min = diff;
        closest = node;
      }
    }

    return closest;
  }

  /**
   * Initialize this peer's finger table
   */
  public static void initializeFingerTable() {
    for (int i = 0; i < (int) (Math.log(Config.MAX_NODES) / Math.log(2)); i++) {
      idealSuccessor.add((int) (nodeID + Math.pow(2, i)) % Config.MAX_NODES);
      actualSuccessor.add(nodeID);
      actualSuccessorIP.add(ipAddress);
    }
    kPeer.printFingerTable();
  }

  /**
   * Print out this peer's finger table
   */
  public void printFingerTable() {
    System.out.println("Finger Table for Node: " + nodeID);
    System.out.println("\ti\t|j + 2^i\t| actual successor |");
    for (int i = 0; i < (int) (Math.log(Config.MAX_NODES) / Math.log(2)); i++) {
      System.out.println("\t" + i + " \t|" + idealSuccessor.get(i) + "\t\t|" + actualSuccessor.get(i) + " (" + actualSuccessorIP
          .get(i) + ") |");
    }
  }

  /**
   * This method allows you to easily establish an RPC session and send a request.
   *
   * @param ipAddressConn IP you are RPCing to
   * @param method JSON Method
   * @param paramMap map of parameters/data
   * @param requestID request ID
   */
  public void RPC2OtherPeer(String ipAddressConn, String method, Map<String, Object> paramMap, int requestID) {
    // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
    // The JSON-RPC 2.0 server URL
    try {
      clientURL = new URL("http://" + ipAddressConn + ":" + Config.DEFAULT_PORT_PREFIX);
    } catch (MalformedURLException e) {
      // handle exception...
    }

    // Create new JSON-RPC 2.0 client session
    JSONRPC2Session mySession = new JSONRPC2Session(clientURL);

    // Construct new request
    JSONRPC2Request request;
    request = new JSONRPC2Request(method, requestID);
    request.setNamedParams(paramMap);

    // Send request
    JSONRPC2Response response = null;
    try {
      response = mySession.send(request);
    } catch (JSONRPC2SessionException e) {
      System.err.println(e.getMessage());
      // handle exception...
    }

    // Print response result / error
    if (response.indicatesSuccess()) {
      if ((Boolean) response.getResult()) {
        System.out.println("Success!");
      } else {
        System.out.println("Failed!");
      }
    } else {
      System.out.println(response.getError().getMessage());
    }
  }

  public void viewFiles() {
    for (int i = 0; i < files.size(); i++) {
      System.out.println("File #" + i);
      System.out.println("Filename: " + files.get(i).getFilename());
    }
    if (files.size() == 0) {
      System.out.println("There are no files on this peer!");
    }
  }

  @Override
  public void insert(FakeFile file) {
    System.out.println("File: " + file.getFilename());
    int hash = file.getFilename().hashCode();
    file.setFileHash(hash);
    System.out.println("Hashcode: " + hash);
    int destinationID = Math.floorMod(hash, Config.MAX_NODES);
    System.out.println("DestinationID: " + destinationID);
    if (nodeID == destinationID) {
      System.out.println(file.getFilename() + " has been inserted on node " + nodeID);
      insertFile(file);
    } else {
      getOnlinePeers();
      ArrayList<Integer> onlinePeerList = new ArrayList<>();
      for (int i = 0; i < onlinePeers.size(); i++) {
       onlinePeerList.add(Integer.parseInt(onlinePeers.get(i)));
      }
      int closestNode = closestNode(destinationID, onlinePeerList);
      System.out.println("Insert closest Node is: " + closestNode);
      if (nodeID == closestNode) {
        insertFile(file);
      } else {
        insertFileClosestNode(closestNode, file);
      }
      System.out.println(file.getFilename() + " has been inserted on node " + closestNode);
    }
  }

  /**
   * Insert the file on the closest node via RPC
   * @param closestNode node for file insertion
   * @param file file to be inserted
   */
  public void insertFileClosestNode(int closestNode, FakeFile file) {
    String ip = "peer" + closestNode;
    Map<String, Object> myParams = new HashMap<String, Object>();
    myParams.put("filename", file.getFilename());
    myParams.put("content", file.getContent());
    myParams.put("filehash", file.getFileHash());
    RPC2OtherPeer(ip, "insertFile", myParams, requestID++);
  }

  /**
   * Insert the file into this node
   * @param file file to be inserted
   */
  public static void insertFile(FakeFile file) {
    files.add(file);
  }

  @Override
  public FakeFile lookup(String hashCode) {
    FakeFile lookupFile = new FakeFile("File Not Found", "File Not Found");
    int hashInt = Integer.parseInt(hashCode);
    int destinationID = Math.floorMod(hashInt, Config.MAX_NODES);
    if (destinationID == nodeID) {
      for (int i = 0; i < files.size(); i++) {
        if (files.get(i).getFileHash() == hashInt) {
          lookupFile = files.get(i);
        }
      }
    } else {
      ArrayList<Integer> onlinePeerList = new ArrayList<>();
      for (int i = 0; i < onlinePeers.size(); i++) {
        onlinePeerList.add(Integer.parseInt(onlinePeers.get(i)));
      }
      System.out.println(onlinePeerList);
      int closestNode = closestNode(destinationID, onlinePeerList);
      System.out.println("Lookup Closest Node is: " + closestNode);
      getFile(closestNode, hashCode);
      for (int i = 0; i < files.size(); i++) {
        if (files.get(i).getFileHash() == hashInt) {
          lookupFile = files.get(i);
        }
      }
    }
    return lookupFile;
  }

  public static FakeFile lookupFileHere(String hashCode) {
    FakeFile lookupFile = new FakeFile("File Not Found", "File Not Found");
    int hashInt = Integer.parseInt(hashCode);
      for (int i = 0; i < files.size(); i++) {
        if (files.get(i).getFileHash() == hashInt) {
          lookupFile = files.get(i);
        }
      }
    return lookupFile;
  }

  /**
   * Get the file from a remote peer
   * @param destinationID remote peer
   * @param hashcode file hashcode
   */
  public void getFile(int destinationID, String hashcode) {
    String ip = "peer" + destinationID;
    Map<String, Object> myParams = new HashMap<String, Object>();
    myParams.put("hashcode", hashcode);
    myParams.put("reqNodeID", nodeID);
    RPC2OtherPeer(ip, "getFile", myParams, requestID++);
  }

  /**
   * Send the requested file to the remote peer
   * @param destinationID remote peer
   */
  public static void sendFile(int destinationID, String hashcode) {
    String ip = "peer" + destinationID;
    FakeFile lookupFile = lookupFileHere(hashcode);

    try {
      clientURL = new URL("http://" + ip + ":" + Config.DEFAULT_PORT_PREFIX);
    } catch (MalformedURLException e) {
      // handle exception...
    }

    // Create new JSON-RPC 2.0 client session
    JSONRPC2Session mySession = new JSONRPC2Session(clientURL);

    // Construct new request
    JSONRPC2Request request;
    Map<String, Object> myParams = new HashMap<String, Object>();
    myParams.put("filename", lookupFile.getFilename());
    myParams.put("content", lookupFile.getContent());
    myParams.put("filehash", lookupFile.getFileHash());
    request = new JSONRPC2Request("insertFile", requestID++);
    request.setNamedParams(myParams);

    // Send request
    JSONRPC2Response response = null;
    try {
      response = mySession.send(request);
    } catch (JSONRPC2SessionException e) {
      System.err.println(e.getMessage());
      // handle exception...
    }

    // Print response result / error
    if (response.indicatesSuccess()) {
      if ((Boolean) response.getResult()) {
        System.out.println("Success!");
      } else {
        System.out.println("Failed!");
      }
    } else {
      System.out.println(response.getError().getMessage());
    }
  }

  /**
   * A handler thread class. Handlers are spawned from the listening loop and are responsible for a
   * dealing with a single client and broadcasting its messages.
   */
  private static class Handler extends Thread {

    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Dispatcher dispatcher;

    /**
     * Constructs a handler thread, squirreling away the socket. All the interesting work is done in
     * the run method.
     */
    public Handler(Socket socket) {
      System.out.println("Connected IP: " + socket.getInetAddress() + ":" + socket.getLocalPort());
      this.socket = socket;

      // Create a new JSON-RPC 2.0 request dispatcher
      this.dispatcher = new Dispatcher();

      // Register the handlers with it
      dispatcher.register(new JsonHandler.KademliaPeerHandler());
    }

    /**
     * Services this thread's client by repeatedly requesting a screen name until a unique one has
     * been submitted, then acknowledges the name and registers the output stream for the client in
     * a global set, then repeatedly gets inputs and broadcasts them.
     */
    public void run() {
      try {
        // Create character streams for the socket.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // read request
        String line;
        line = in.readLine();
        StringBuilder raw = new StringBuilder();
        raw.append("" + line);
        boolean isPost = line.startsWith("POST");
        int contentLength = 0;
        while (!(line = in.readLine()).equals("")) {
          //System.out.println(line);
          raw.append('\n' + line);
          if (isPost) {
            final String contentHeader = "Content-Length: ";
            if (line.startsWith(contentHeader)) {
              contentLength = Integer.parseInt(line.substring(contentHeader.length()));
            }
          }
        }
        StringBuilder body = new StringBuilder();
        if (isPost) {
          int c = 0;
          for (int i = 0; i < contentLength; i++) {
            c = in.read();
            body.append((char) c);
          }
        }

        System.out.println("INFO:" + body.toString());
        JSONRPC2Request request = JSONRPC2Request.parse(body.toString());
        JSONRPC2Response resp = dispatcher.process(request, null);

        // send response
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: application/json\r\n");
        out.write("\r\n");
        out.write(resp.toJSONString());
        // do not in.close();
        out.flush();
        out.close();
        socket.close();
      } catch (IOException e) {
        System.out.println(e);
      } catch (JSONRPC2ParseException e) {
        e.printStackTrace();
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
        }
      }
    }
  }

  /**
   * Main method: Handles starting the peer up and CLI input
   *
   * @param args NodeID (int) and Hostname (String)
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println(
          "Incorrect number of arguments! Please specify NodeID and Hostname!");
      System.exit(0);
    }
    System.out.println("Starting Kademlia Peer!");
    kPeer = new KademliaPeer(Integer.parseInt(args[0]), args[1], Config.DEFAULT_PORT_PREFIX);
    kPeer.start();
    kPeer.startNewPeer();

    if (Config.ANCHOR_NODE_IDS.contains(kPeer.nodeID)) {
      kPeer.isAnchor = true;
    }

    while (kPeer.isOnline) {
      System.out.println("Please select one of the following options by number (i.e. '1'):\n"
          + "1. Upload File\n"
          + "2. View Files on this Peer\n"
          + "3. Download File\n"
          + "4. View Finger Table\n"
          + "5. Logout\n");
      Scanner scanner = new Scanner(System.in);
      try {
        int option = Integer.parseInt(scanner.nextLine());
        switch (option) {
          case 1:
            System.out.println("Please enter file name (one line/no spaces): ");
            String filename = scanner.nextLine();
            System.out.println("Please enter file contents (one line): ");
            String contents = scanner.nextLine();
            FakeFile fFile = new FakeFile(filename, contents);
            kPeer.insert(fFile);
            break;
          case 2:
            kPeer.viewFiles();
            break;
          case 3:
            System.out.println("Please enter file hashcode: ");
            String hashcode = scanner.next();
            FakeFile lookup = kPeer.lookup(hashcode);
            lookup.printFile();
            break;
          case 4:
            kPeer.printFingerTable();
            break;
          case 5:
            kPeer.stopPeer();
            kPeer.isOnline = false;
            System.out.println("Node " + nodeID + " successfully signed out!");
            System.exit(0);
            break;
          default:
            System.out.println("Invalid Option! Please try again!");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
