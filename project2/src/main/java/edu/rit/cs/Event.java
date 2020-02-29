package edu.rit.cs;

import java.io.Serializable;

/**
 * Represents an Event part of the Publisher Subscriber System
 */
public class Event implements Serializable {

  private int id;
  private Topic topic;
  private String title;
  private String content;

  public Event(int id, Topic topic, String title, String content) {
    this.id = id;
    this.topic = topic;
    this.title = title;
    this.content = content;
  }

  public int getId() {
    return id;
  }

  /**
   * Return the topic related to this event
   *
   * @return Topic
   */
  public Topic getTopic() {
    return topic;
  }

  /**
   * Return the title of this event
   *
   * @return String
   */
  public String getTitle() {
    return title;
  }

  /**
   * Return the content of this event
   *
   * @return String
   */
  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "Event: " + this.getTitle() +
        "\nTopic: " + this.getTopic() +
        "\nContent: " + this.getContent();
  }
}
