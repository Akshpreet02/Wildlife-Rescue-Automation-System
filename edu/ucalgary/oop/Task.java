package edu.ucalgary.oop;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.7
 * @since 1.0
 */

// create task class that is used as an extension of meduval task and similar to
// the other task class.
public abstract class Task {
    // create private variables associated to the task class.
    private String nickname;
    private int maxWindow;
    private int duration;
    private String description;

    // constructor for the task class that instantiates the privaste variable for
    // each object.
    public Task(String description, int duration, int maxWindow, String nickname) throws IllegalArgumentException {
        if (description == null || maxWindow < -1 || 23 < maxWindow | duration < 0) {
            throw new IllegalArgumentException("Invalid Task Attributes Provided");
        }
        this.nickname = nickname;
        this.maxWindow = maxWindow;
        this.duration = duration;
        this.description = description;
    }

    // get the animal nickname and return String type
    public String nicknameGetter() {
        return this.nickname;
    }

    // get the max window and return as int type
    public int MaxWindowGetter() {
        return this.maxWindow;
    }

    // get the duration and return the int type.
    public int durationGetter() {
        return this.duration;
    }

    // get the description and return string type.
    public String descriptionGetter() {
        return this.description;
    }
}