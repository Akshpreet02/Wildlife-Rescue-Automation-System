package edu.ucalgary.oop;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.3
 * @since 1.0
 */

// creates a class called medical task that is a subclass of task that is unique
// as it has a task id compared to the regular task class.
public class MedicalTask extends Task {
    // create a variable for the id of task.
    private int idTask;

    // return the id of task for the medicaltask object.
    public int idTaskGetter() {
        return this.idTask;
    }

    // creates the medical task by calling the task class constructor and checks if
    // the task id is valid.
    public MedicalTask(String desc, int dur, int maxWin, int idTask, String nickname) throws IllegalArgumentException {
        super(desc, dur, maxWin, nickname);
        if (!(idTask >= 0)) {
            throw new IllegalArgumentException("invalid id of task provided");
        }
        // sets the idtask of the mdicaltask object
        this.idTask = idTask;

    }

}
