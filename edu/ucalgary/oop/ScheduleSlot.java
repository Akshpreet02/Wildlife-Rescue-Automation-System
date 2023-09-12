package edu.ucalgary.oop;

import java.util.ArrayList;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.13
 * @since 1.0
 */

// create a class that will be the slots for each hoyr of the schedule.
public class ScheduleSlot {
    // have a variable for tiime remaining in slot, back upvoluntter and the list of
    // tasks.
    private int timeAvailable;
    private boolean backupVolunteer;
    private ArrayList<Task> tasks;

    // get the time available
    public int timeAvaliableGetter() {
        return this.timeAvailable;
    }

    // get the arraylist of tasks.
    public ArrayList<Task> taskGetter() {
        return this.tasks;
    }

    // get if there is a backup volunteer
    public boolean backupVolunteerGetter() {
        return this.backupVolunteer;
    }

    // set the time available.
    public void timeAvailableSetter(int timeAvailable) {
        this.timeAvailable = timeAvailable;
    }

    // add a task to the arraylist of the schedule slot.
    public void taskAdder(Task task) {
        this.tasks.add(task);
    }

    // set a backupvoluntter if needed.
    public void backupVolunteerSetter(boolean backupVolunteer) {
        this.backupVolunteer = backupVolunteer;
    }

    // add time to time available. throw an exception if the time available added is
    // less than allowed.
    public void timeAvailableAdder(int timeAvailable) throws IllegalArgumentException {
        this.timeAvailable += timeAvailable;
        if (this.timeAvailable < 0) {
            throw new IllegalArgumentException("The time available became negative!");
        }
    }

    // defalult constructor sets the time available to one hour slor and no
    // backupvolunteer.
    public ScheduleSlot() {
        this.timeAvailable = 60;
        this.backupVolunteer = false;
        this.tasks = new ArrayList<Task>();
    }

    // constructor if timeavailable provided, sets time available to the argument
    // and the backup volunteer to false.
    public ScheduleSlot(int timeAvailable) {
        this.timeAvailable = timeAvailable;
        this.backupVolunteer = false;
        this.tasks = new ArrayList<Task>();

    }
}