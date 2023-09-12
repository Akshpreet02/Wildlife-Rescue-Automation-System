package edu.ucalgary.oop;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.2
 * @since 1.0
 */

// create a class that is a subclass of task that is specificic for the cage
// cleaning.
public class OtherTaskCageCleaning extends Task {
    // the constructor will initialize the task the same way it would the Task class
    public OtherTaskCageCleaning(String desc, int dur, int maxWin, String nickname) throws IllegalArgumentException {
        super(desc, dur, maxWin, nickname);
    }
}
