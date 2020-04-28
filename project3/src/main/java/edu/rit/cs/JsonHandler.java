package edu.rit.cs;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import java.util.ArrayList;
import java.util.Map;

/**
 * Handles JSONRPC2 requests for Kademlia
 */
public class JsonHandler {

  /**
   * Handles the JSONRPC2 requests for the MiniServer
   */
  public static class MiniServerHandler implements RequestHandler {

    // Reports the method names of the handled requests
    public String[] handledRequests() {
      return new String[]{"online", "offline", "ispeeronline", "getOnlinePeers"};
    }

    // Processes the requests
    public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
      Map<String, Object> myParams = req.getNamedParams();
      Long lID = (Long) myParams.get("id");
      int id = lID.intValue();
      switch (req.getMethod()) {
        case "online":
          Object ip = myParams.get("ip");
          Object port = myParams.get("port");
          MiniServer.addPeer(id, ip.toString(), port.toString());
          return new JSONRPC2Response(true, req.getID());
        case "offline":
          MiniServer.removePeer(id);
          return new JSONRPC2Response(true, req.getID());
        case "ispeeronline":
          return new JSONRPC2Response(MiniServer.isPeerOnline(id), req.getID());
        case "getOnlinePeers":
          ip = myParams.get("ip");
          MiniServer.getOnlinePeers(ip.toString());
          return new JSONRPC2Response(true, req.getID());
        default:
          return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
      }
    }
  }

  /**
   * Handles the JSONRPC2 requests for the Kademlia Peer
   */
  public static class KademliaPeerHandler implements RequestHandler {

    // Reports the method names of the handled requests
    public String[] handledRequests() {
      return new String[]{"newNodeOnline", "returnOnlineNodes", "insertFile", "getFile"};
    }

    // Processes the requests
    public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
      Map<String, Object> myParams = req.getNamedParams();
      switch (req.getMethod()) {
        case "newNodeOnline":
          Object ip = myParams.get("ip");
          Object port = myParams.get("port");
          KademliaPeer.newPeerOnline(ip.toString(), port.toString());
          return new JSONRPC2Response(true, req.getID());
        case "returnOnlineNodes":
          Object onlinePeers = myParams.get("onlinePeers");
          ArrayList<String> onlinePeersList = (ArrayList<String>) onlinePeers;
          KademliaPeer.saveOnlinePeers(onlinePeersList);
          return new JSONRPC2Response(true, req.getID());
        case "insertFile":
          String filename = (String) myParams.get("filename");
          String content = (String) myParams.get("content");
          int hashcodeInt = ((Long) myParams.get("filehash")).intValue();
          FakeFile fileToInsert = new FakeFile(filename, content);
          fileToInsert.setFileHash(hashcodeInt);
          System.out.println("Received File: " + fileToInsert.getFilename());
          KademliaPeer.insertFile(fileToInsert);
          return new JSONRPC2Response(true, req.getID());
        case "getFile":
          Object hashcode = myParams.get("hashcode");
          String hashcodeS = (String) hashcode;
          Object requestingNodeID = myParams.get("reqNodeID");
          int NodeID = ((Long) requestingNodeID).intValue();
          KademliaPeer.sendFile(NodeID, hashcodeS);
          return new JSONRPC2Response(true, req.getID());
        default:
          return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
      }
    }
  }
}

