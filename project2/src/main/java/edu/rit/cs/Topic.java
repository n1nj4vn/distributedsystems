package edu.rit.cs;

import java.util.List;
import java.util.Objects;

/**
 * Represents an Topic part of the Publisher Subscriber System
 */
public class Topic {

  private int id;
  private List<String> keywords;
  private String name;

  public Topic(List<String> keywords, String name) {
    this.keywords = keywords;
    this.name = name;
  }

  /**
   * Return the topic's ID
   *
   * @return int
   */
  public int getId() {
    return id;
  }

  /**
   * Return the topic's name
   *
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * Return the topic's keywords
   *
   * @return List<String>
   */
  public List<String> getKeywords() {
    return keywords;
  }

  @Override
  public String toString() {
    return "Topic: "
        + "\nID: " + getId()
        + "\nName: " + getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Topic topic = (Topic) o;
    return Objects.equals(name, topic.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
