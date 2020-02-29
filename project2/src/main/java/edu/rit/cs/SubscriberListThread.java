package edu.rit.cs;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This thread handles listing subscribers and receiving CLI input!
 */
public class SubscriberListThread implements Runnable {

  private EventManager eventManager;

  public SubscriberListThread(EventManager eventManager) {
    this.eventManager = eventManager;
  }

  @Override
  public void run() {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println(
          "Enter 'ListSubscribers' to view all subscribers! Or enter a specific topic to view it's subscribers!");
      while (scanner.hasNextLine()) {
        String userInput = scanner.nextLine();
        ConcurrentHashMap<String, ArrayList<String>> topicSubscribers = eventManager
            .getTopicSubscribers();
        ArrayList<String> subscribers;
        // Lists all topics & subscribers
        if (userInput.equals("ListSubscribers")) {
          for (Map.Entry<String, ArrayList<String>> entry : topicSubscribers.entrySet()) {
            System.out.println("Topic: " + entry.getKey());
            subscribers = entry.getValue();
            System.out.println("Subscribers: " + createSubscribersString(subscribers));
          }
        } else { // Lists subscribers for a selected topic
          if (topicSubscribers.containsKey(userInput)) {
            System.out.println("Topic: " + userInput);
            subscribers = topicSubscribers.get(userInput);
            System.out.println("Subscribers: " + createSubscribersString(subscribers));
          } else {
            System.out.println("The topic requested does not exist!");
          }
        }
      }
    }
  }

  /**
   * Appends the subscribers to a formatted string to be returned for use
   *
   * @param subscribers list of subscribers
   * @return formatted string of subscribers for a topic
   */
  private StringBuilder createSubscribersString(ArrayList<String> subscribers) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.setLength(0);
    for (int i = 0; i < subscribers.size(); i++) {
      if (i == subscribers.size() - 1) {
        stringBuilder.append(subscribers.get(i));
      } else {
        stringBuilder.append(subscribers.get(i)).append(", ");
      }
    }
    return stringBuilder;
  }
}
