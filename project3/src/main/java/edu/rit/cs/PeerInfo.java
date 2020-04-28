package edu.rit.cs;

/**
 * This class represents the networking information behind a peer for the Kademlia DHT P2P Network.
 */
public class PeerInfo {

  private String ipAddress;
  private String port;

  /**
   * Constructor
   *
   * @param ipAddress this peer's ip address
   * @param port this peer's port number
   */
  public PeerInfo(String ipAddress, String port) {
    this.ipAddress = ipAddress;
    this.port = port;
  }

  /**
   * @return this peer's ip address
   */
  public String getIpAddress() {
    return this.ipAddress;
  }

  /**
   * @return this peer's port number
   */
  public String getPort() {
    return this.port;
  }
}
