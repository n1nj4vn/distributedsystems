package edu.rit.cs;

// The JSON-RPC 2.0 Base classes that define the JSON-RPC 2.0 protocol messages
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


/**
 * This class runs a miniServer allowing us to handle new nodes and track online peers/anchors!
 */
public class MiniServer {

  private static URL serverURL = null;
  private static Map<Integer, PeerInfo> onlinePeers = new HashMap<>();
  private static Map<Integer, PeerInfo> onlineAnchorNodes = new HashMap<>();
  private static int requestID = -1;

  /**
   * The port that the server listens on.
   */
  private static final int PORT = Integer.parseInt(Config.SERVER_PORT);

  /**
   * Add a peer to the network. If the peerID is specified as an anchor, we add the peer to both
   * the onlinePeer and onlineAnchorNodes map
   * @param peerID the new peer's ID
   * @param ipAddress the new peer's IP Address
   * @param port the new peer's port number
   */
  public static void addPeer(int peerID, String ipAddress, String port) {
    System.out.println("addPeer: (peerID: " + peerID + " ipAddress: " + ipAddress + " port: " + port + ")");
    PeerInfo newPeer = new PeerInfo(ipAddress, port);
    if (Config.ANCHOR_NODE_IDS.contains(peerID)) {
      onlinePeers.put(peerID, newPeer);
      onlineAnchorNodes.put(peerID, newPeer);
    } else {
      onlinePeers.put(peerID, newPeer);
      informAnchorNodes(onlineAnchorNodes, newPeer);
    }
  }

  /**
   * Remove a peer from the network. If the peer's ID exists in the anchor nodes it will be removed
   * as well.
   * @param peerID
   */
  public static void removePeer(int peerID) {
    System.out.println("removePeer: (peerID: " + peerID + ")");
    onlinePeers.remove(peerID);
    onlineAnchorNodes.remove(peerID);
  }

  /**
   * Determine if the specified peer is online in the network
   * @param peerID specified peer
   * @return true if online, false if offline
   */
  public static boolean isPeerOnline(int peerID) {
    return onlinePeers.containsKey(peerID);
  }

  /**
   * Get all the peers currently online
   * @return map containing all the peers currently online
   */
  public static void getOnlinePeers(String ipAddress) {
    // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
    // The JSON-RPC 2.0 server URL
    try {
      serverURL = new URL("http://" + ipAddress + ":" + Config.DEFAULT_PORT_PREFIX);
    } catch (MalformedURLException e) {
      // handle exception...
    }

    // Create new JSON-RPC 2.0 client session
    JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

    // Construct new request
    JSONRPC2Request request;
    Map<String, Object> myParams = new HashMap<String, Object>();
    List<String> onlinePeerList = new ArrayList<>();
    for (Map.Entry<Integer, PeerInfo> entry : onlinePeers.entrySet()) {
      onlinePeerList.add("" + entry.getKey());
    }
    request = new JSONRPC2Request("returnOnlineNodes", requestID++);
    myParams.put("onlinePeers", onlinePeerList);
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
        System.out.println("OnlinePeers Returned to " + ipAddress);
      } else {
        System.out.println("Failed!");
      }
    } else {
      System.out.println(response.getError().getMessage());
    }
  }

  /**
   * We inform the anchor nodes that a new peer (non-anchor) has joined the network. This will help
   * to allow all requests to go through the anchor nodes.
   * @param onlineAnchorNodes
   * @param newNode
   */
  public static void informAnchorNodes(Map<Integer, PeerInfo> onlineAnchorNodes, PeerInfo newNode) {
    for (Map.Entry<Integer, PeerInfo> peer : onlineAnchorNodes.entrySet()) {
      // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
      // The JSON-RPC 2.0 server URL
      try {
        serverURL = new URL("http://" + peer.getValue().getIpAddress() + ":" + Config.DEFAULT_PORT_PREFIX);
      } catch (MalformedURLException e) {
        // handle exception...
      }

      // Create new JSON-RPC 2.0 client session
      JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

      // Construct new request
      JSONRPC2Request request;
      Map<String, Object> myParams = new HashMap<String, Object>();
      request = new JSONRPC2Request("newNodeOnline", requestID++);
      myParams.put("ip", newNode.getIpAddress());
      myParams.put("port", newNode.getPort());
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
          System.out.println("Anchor Node " + peer.getValue().getIpAddress() + " notified!");
        } else {
          System.out.println("Failed!");
        }
      } else {
        System.out.println(response.getError().getMessage());
      }
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
      dispatcher.register(new JsonHandler.MiniServerHandler());
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

        System.out.println(body.toString());
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
   * Main method: Handles starting the miniServer for a Kademlia DHT network
   */
  public static void main(String[] args) throws Exception {

    System.out.println("The Kademlia MiniServer is running.");
    ServerSocket listener = new ServerSocket(PORT);
    try {
      while (true) {
        new Handler(listener.accept()).start();
      }
    } finally {
      listener.close();
    }
  }
}
