package edu.rit.cs;


/**
 * This class allows us to mock a file in order to avoid complexities dealing with file IO in the
 * Kademlia DHT P2P Network.
 */
public class FakeFile {

  String filename;
  String content;
  int fileHash;

  /**
   * Constructor
   *
   * @param filename the file's name
   * @param content the file's content
   */
  public FakeFile(String filename, String content) {
    this.filename = filename;
    this.content = content;
  }

  /**
   * @return the file's name
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @return the file's content
   */
  public String getContent() {
    return content;
  }

  /**
   * @return the file's hashcode from hashing the filename
   */
  public int getFileHash() {
    return fileHash;
  }

  /**
   * Set the file's hashcode
   *
   * @param fileHash hashcode
   */
  public void setFileHash(int fileHash) {
    this.fileHash = fileHash;
  }

  /**
   * Print the file in a human readable format
   */
  public void printFile() {
    System.out.println("Filename: " + filename + "\n"
        + "Content: " + content + "\n"
        + "FileHash: " + fileHash);
  }
}
