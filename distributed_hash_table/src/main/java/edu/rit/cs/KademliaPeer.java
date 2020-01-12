package edu.rit.cs;

public class KademliaPeer {
    enum STATUS {
        ONLINE, OFFLINE;
    }

    private int nodeID;
    private String ipAddress=Config.DEFAULT_PEER_IP;
    private String port;

    private boolean isAnchor;



}
