package edu.rit.cs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Thread for the Publisher/Subscriber Client (Agent) to send messages to the Server
 * (ServerReceiveThread) through Java Sockets
 */
public class AgentSendThread extends Thread {

  // Reference to the pubSubAgent running this thread
  private PubSubAgent pubSubAgent;
  // Reference to the socket to receive messages to
  private Socket socket;
  // Message to be sent
  private String message;
  // Read input from user
  private Scanner scanner;
  // User credentials
  private String username;
  private String password;
  // Options from Constants.java
  private String userOption;
  // Topic member variables
  private String topic;
  private String title;
  private String content;

  public AgentSendThread(PubSubAgent pubSubAgent, Socket socket, Scanner scanner) {
    this.pubSubAgent = pubSubAgent;
    this.socket = socket;
    this.scanner = scanner;
    this.message = null;
  }

  /**
   * Get the stored message
   *
   * @return String stored message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Store the message to be sent
   *
   * @param message message to be sent
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Get the agent's username
   *
   * @return String username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Store the agent's username
   *
   * @param username agent's username (existing/created)
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Get the agent's password (currently an insecure implementation, but we're using sockets
   * anyways..)
   *
   * @return String password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Store the agent's password
   *
   * @param password password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Get the agent's scanner for input
   *
   * @return String
   */
  public Scanner getScanner() {
    return scanner;
  }

  /**
   * Get the menu option the agent selected
   *
   * @return String
   */
  public String getUserOption() {
    return userOption;
  }

  /**
   * Store the menu option the agent selected
   *
   * @param userOption one of the options from Constants.java
   */
  public void setUserOption(String userOption) {
    this.userOption = userOption;
  }

  /**
   * Get the stored Topic
   *
   * @return String
   */
  public String getTopic() {
    return topic;
  }

  /**
   * Store the Topic
   *
   * @param topic topic
   */
  public void setTopic(String topic) {
    this.topic = topic;
  }

  /**
   * Get the Topic's title
   *
   * @return String
   */
  public String getTitle() {
    return title;
  }

  /**
   * Store the topic's title
   *
   * @param title title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Get the Topic's content
   *
   * @return String
   */
  public String getContent() {
    return content;
  }

  /**
   * Store the topic's content
   *
   * @param content content
   */
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public void run() {
    try {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      loginToEventManager(bw); // login to the event manager server
      while (this.pubSubAgent.isClientActive) {
        System.out.println(Constants.MENU);
        String cliInput = getScanner().nextLine();
        parseCLI(cliInput);
        if (getMessage() != null) {
          bw.write(getMessage());
          bw.flush();
          setMessage(null); // clear message so we do not send duplicate actions
        }
        Thread.sleep(1000); // Allow time for processing
      }
      bw.close();
      socket.close();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handle's user input for login/registration then forwards to server
   *
   * @param bw writer for writing the message containing the options & respective contents
   */
  private void loginToEventManager(BufferedWriter bw) {
    System.out.println(Constants.LOGIN_MENU);
    int userLoginOption = Integer.parseInt(getScanner().nextLine());
    switch (userLoginOption) {
      case 1: // Registration
        System.out.println("Please enter a new username");
        setUsername(getScanner().nextLine());
        System.out.println("Please enter a new password");
        setPassword(getScanner().nextLine());
        message = // formatted using @@@ as a delimiter for ease of programming
            Constants.REGISTER + "@@@USERNAME@@@" + getUsername() + "@@@" + getPassword() + "\n";
        try {
          bw.write(message);
          bw.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      case 2: // Existing Login
        System.out.println("Please enter a username");
        setUsername(getScanner().nextLine());
        System.out.println("Please enter a password");
        setPassword(getScanner().nextLine());
        // formatted using @@@ as a delimiter for ease of programming
        message = Constants.LOGIN + "@@@USERNAME@@@" + getUsername() + "@@@" + getPassword() + "\n";
        try {
          bw.write(message);
          bw.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      default:
        System.err.println("Invalid Option! Please try again!");
    }
  }

  /**
   * Process the user's input for publisher/subscriber options then forwards to the server
   *
   * @param cliInput user's selected option (integer)
   */
  private void parseCLI(String cliInput) {
    int userLoginOption = Integer.parseInt(cliInput);
    switch (userLoginOption) {
      case 1:
        setUserOption(Constants.LOGOUT);
        System.out.println("Logging Out!");
        break;
      case 2:
        setUserOption(Constants.SUBSCRIBE);
        System.out.println("Please enter the topic you want to subscribe to:");
        setTopic(getScanner().nextLine());
        break;
      case 3:
        setUserOption(Constants.UNSUBSCRIBE);
        System.out.println("Please enter the topic you want to unsubscribe from:");
        setTopic(getScanner().nextLine());
        break;
      case 4:
        setUserOption(Constants.ADVERTISE);
        System.out.println("Please enter the topic you want to advertise:");
        setTopic(getScanner().nextLine());
        break;
      case 5:
        setUserOption(Constants.PUBLISH);
        System.out.println("Please enter the topic you want to publish:");
        setTopic(getScanner().nextLine());
        System.out.println("Please enter the title you want to publish:");
        setTitle(getScanner().nextLine());
        System.out.println("Please enter the content you want to publish:");
        setContent(getScanner().nextLine());
        break;
      case 6:
        setUserOption(Constants.SUBSCRIBED_TOPICS);
        System.out.println("Listing Subscribed Topics");
        break;
      case 7:
        setUserOption(Constants.LIST_TOPICS);
        System.out.println("Listing All Topics");
        break;
      default:
        System.err.println("Invalid Option! Please try again!");
    }
    setMessage(getUserOption() // formatted using @@@ as a delimiter for ease of programming
        + "@@@USERNAME@@@" + getUsername()
        + "@@@TOPIC@@@" + getTopic()
        + "@@@TITLE@@@" + getTitle()
        + "@@@CONTENT@@@" + getContent()
        + "\n");
  }
}