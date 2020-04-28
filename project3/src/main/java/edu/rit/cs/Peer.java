package edu.rit.cs;

/**
 * Represents the required functions in a Kademlia DHT P2P Network.
 */
public interface Peer {

  /**
   * Given a file, a peer will generate a hashCode for this file and place it on a node according to
   * the underlying DHT algorithm
   */
  public void insert(FakeFile file);

  /**
   * Given the hashCode of a file, a peer should return the file.
   *
   * @return the file corresponding to the hashCode
   */
  public FakeFile lookup(String hashCode);

}
