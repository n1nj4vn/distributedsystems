package edu.rit.cs;

import java.util.Arrays;
import java.util.List;

/**
 * Holds configuration constants for the Kademlia DHT P2P Network.
 */
public class Config {

  public static final String SERVER_IP = "miniServer";
  public static final String SERVER_PORT = "8081";

  public static final String DEFAULT_PORT_PREFIX = "8081";

  public static final List<Integer> ANCHOR_NODE_IDS = Arrays.asList(2, 4);

  public static final int FINGER_TABLE_ROWS = 4;
  public static final int MAX_NODES = 16;
}
