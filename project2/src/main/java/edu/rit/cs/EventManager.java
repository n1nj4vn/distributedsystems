package edu.rit.cs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EventManager class in this Publisher Subscriber System
 * Handles running the server, receiving/sending to the Agents, tracking subscribers and active users,
 * credentials, notifies Agents of subscribed topics, etc.
 */
public class EventManager {

  // Topics and their subscribers
  private ConcurrentHashMap<String, ArrayList<String>> topicSubscribers;
  // All advertised topics
  private HashSet<String> topics;
  // User credentials
  private ConcurrentHashMap<String, String> subscriberIDPass;
  // Actively connected users
  private ConcurrentHashMap<String, ServerSendThread> activeUsers;
  // Notifications queued for users that have logged out
  private ConcurrentHashMap<String, ArrayList<String>> pendingNotifications;
  private int eventID;

  public EventManager() {
    this.topicSubscribers = new ConcurrentHashMap<>();
    this.topics = new HashSet<>();
    this.subscriberIDPass = new ConcurrentHashMap<>();
    this.activeUsers = new ConcurrentHashMap<>();
    this.pendingNotifications = new ConcurrentHashMap<>();
    this.eventID = 0;
  }

  /**
   * Get the list of topics and their subscribers
   *
   * @return list of topics and their subscribers
   */
  public ConcurrentHashMap<String, ArrayList<String>> getTopicSubscribers() {
    return topicSubscribers;
  }

  /**
   * Get the agent's password (currently an insecure implementation, but we're using sockets
   * anyways..) stored on the event manager to check
   *
   * @return String password
   */
  public String getPassword(String username) {
    return this.subscriberIDPass.getOrDefault(username, null);
  }

  /*
   * Start the repo service
   */
  private void startService() {
    // Handles CLI and displaying subscribers per topic(s)
    Thread subscriberListThread = new Thread(new SubscriberListThread(this));
    // Handles incoming connections
    // Creates threads to send/receive with Publisher Subscriber agents
    Thread serverHandlerThread = new Thread(new ServerHandlerThread(this));

    subscriberListThread.start();
    serverHandlerThread.start();
  }

  /**
   * Return the current event ID for unique event tracking
   *
   * @return event ID
   */
  public int getEventID() {
    return eventID++;
  }

  /*
   * notify all subscribers of new event
   */
  public void notifySubscribers(Event event, String username) {
    String topicName = event.getTopic().getName();
    ArrayList<String> subscribers = topicSubscribers.getOrDefault(topicName, new ArrayList<>());

    if (!subscribers.contains(username)) { // let the agent know their event has been published
      ServerSendThread sendThread = activeUsers.get(username);
      sendThread.setMessage("Event Published");
      sendThread.setPendingMessage(true);
    }

    for (String subscriber : subscribers) {
      if (activeUsers.containsKey(subscriber)) { // User is online so we send the message to them directly
        ServerSendThread sendThread = activeUsers.get(subscriber);
        String message =
            "New Event: Title: " + event.getTitle() + " Content: " + event.getContent();
        sendThread.setMessage(message);
        sendThread.setPendingMessage(true);
      } else { // User is offline so we queue messages for them until they log in again
        ArrayList<String> notifications = pendingNotifications
            .getOrDefault(subscriber, new ArrayList<>());
        String message =
            "New Event: Title: " + event.getTitle() + " Content: " + event.getContent();
        notifications.add(message);
        pendingNotifications.put(subscriber, notifications);
      }
    }
  }

  /*
   * add new topic when received advertisement of new topic
   */
  public Boolean addTopic(Topic topic) {
    if (!topics.contains(topic.getName())) { // Ensure topic does not exist
      topics.add(topic.getName());
      Set<String> allSubscribers = subscriberIDPass.keySet();
      for (String sub : allSubscribers) {
        if (activeUsers.containsKey(sub)) { // User is online so we send the message to them directly
          ServerSendThread sendThread = activeUsers.get(sub);
          String message = "New Topic: " + topic.getName() + " advertised!";
          sendThread.setMessage(message);
          sendThread.setPendingMessage(true);
        } else { // User is offline so we queue messages for them until they log in again
          ArrayList<String> notifications = pendingNotifications
              .getOrDefault(sub, new ArrayList<>());
          String message = "New Topic: " + topic.getName() + "advertised!";
          notifications.add(message);
          pendingNotifications.put(sub, notifications);
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Returns if the topic exists in the set for when an agent is advertising
   *
   * @param topic proposed topic
   * @return true if it exists
   */
  public boolean doesTopicExist(String topic) {
    return this.topics.contains(topic);
  }

  /**
   * Agent is online again, check if they have any messages queued and send to the agent
   *
   * @param username agent's username
   */
  public void notifyInactiveSubscriber(String username) {
    if (pendingNotifications.containsKey(username)) {
      ArrayList<String> notifications = pendingNotifications.get(username);
      String message = "Here are the events you missed since you logged out!\n";

      for (String notification : notifications) {
        message += notification + "\n";
      }

      ServerSendThread sendThread = activeUsers.get(username);
      sendThread.setMessage(message);
      sendThread.setPendingMessage(true);
      pendingNotifications.remove(username);
    }
  }

  /**
   * Return the list of topics advertised to the event manager
   *
   * @return list of topics advertised to the event manager
   */
  public String getTopics() {
    String topicList = "Topics: \n";
    for (String topic : this.topics) {
      topicList += topic + "\n";
    }
    return topicList;
  }

  /*
   * add subscriber to the internal list
   */
  public void addSubscriber(Topic topic, String username) {
    String topicName = topic.getName();
    ServerSendThread sendThread = activeUsers.get(username);
    if (topics.contains(topicName)) { // Make sure the topic name exists!
      ArrayList<String> subscribers = topicSubscribers.getOrDefault(topicName, new ArrayList<>());
      if (subscribers.contains(username)) {
        sendThread.setMessage("You have previously subscribed!");
      } else {
        subscribers.add(username);
        topicSubscribers.put(topicName, subscribers);
        sendThread.setMessage("Subscribed!");
      }
    } else {
      sendThread.setMessage("Topic does not exist!");
    }
    sendThread.setPendingMessage(true);
  }

  /*
   * remove subscriber from the list
   */
  public void removeSubscriber(Topic topic, String username) {
    String topicName = topic.getName();
    ArrayList<String> subscribers = topicSubscribers.getOrDefault(topicName, new ArrayList<>());

    if (subscribers.contains(username)) {
      subscribers.remove(username);
      topicSubscribers.put(topicName, subscribers);
    }
  }

  /**
   * Return formatted string of topics the agent has subscribed to
   *
   * @param username agent's username
   * @return formatted string of topics the agent has subscribed to
   */
  public String getSubscribedTopics(String username) {
    String topics = "Subscribed Topics: \n";
    for (Map.Entry<String, ArrayList<String>> pair : topicSubscribers.entrySet()) {
      if (pair.getValue().contains(username)) {
        topics += pair.getKey() + "\n";
      }
    }
    return topics;
  }

  /*
   * show the list of subscriber for a specified topic
   */
  public ArrayList<String> showSubscribers(Topic topic) {
    String topicName = topic.getName();
    return topicSubscribers.getOrDefault(topicName, new ArrayList<>());
  }

  /**
   * Create a new user/pass if it does not already exist for another agent
   *
   * @param username agent's username
   * @param newPassword agent's new password
   * @return true if the account was created successfully (not existing), false if the account is existing
   */
  public Boolean createUser(String username, String newPassword) {
    if (!subscriberIDPass.containsKey(username)) {
    subscriberIDPass.put(username, newPassword);
    return true;
    } else {
      return false;
    }
  }

  /**
   * The user has successfully connected with valid credentials, add them to the active users
   * map and store a thread to send messages with
   * @param username agent's username
   * @param sendThread thread to send messages to the agent with
   */
  public void addActiveLogin(String username, ServerSendThread sendThread) {
    this.activeUsers.put(username, sendThread);
  }

  /**
   * The user has logged out/gone offline, remove them from the active users map
   *
   * @param username agent's username
   */
  public void removeActiveLogin(String username) {
    this.activeUsers.remove(username);
  }

  public static void main(String[] args) {
    new EventManager().startService();
  }
}
