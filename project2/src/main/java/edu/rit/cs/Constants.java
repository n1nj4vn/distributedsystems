package edu.rit.cs;

/**
 * Holds constants used across multiple Java files throughout the Publisher Subscriber System
 */
public final class Constants {

  // Publisher/Subscriber Menu Options
  // Created as a constant for reference in multiple files, allows for easy refactoring and safe comparisons
  public static final String REGISTER = "REGISTER";
  public static final String LOGIN = "LOGIN";
  public static final String LOGOUT = "LOGOUT";
  public static final String SUBSCRIBE = "SUBSCRIBE";
  public static final String UNSUBSCRIBE = "UNSUBSCRIBE";
  public static final String ADVERTISE = "ADVERTISE";
  public static final String PUBLISH = "PUBLISH";
  public static final String SUBSCRIBED_TOPICS = "SUBSCRIBED_TOPICS";
  public static final String LIST_TOPICS = "LIST_TOPICS";

  public static final String MENU = "Menu:\n"
      + "Please select the corresponding option number (i.e. '1')\n"
      + "1. Logout\n"
      + "2. Subscribe\n"
      + "3. Unsubscribe\n"
      + "4. Advertise\n"
      + "5. Publish\n"
      + "6. Subscribed Topics\n"
      + "7. List Topics";

  public static final String LOGIN_MENU = "Login:\n"
      + "Please select the corresponding option number (i.e. '1')\n"
      + "1. Register\n"
      + "2. Login";

  // Dedicated Server Port
  public final static int SERVER_PORT = 10000;
}
