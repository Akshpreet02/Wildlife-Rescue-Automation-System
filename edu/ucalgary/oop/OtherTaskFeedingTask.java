package edu.ucalgary.oop;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.2
 * @since 1.0
 */

//// create a class that is a subclass of task that is specificic for the
//// feeding.
public class OtherTaskFeedingTask extends Task {
    // create a private final variable for the animal species the feeding is
    // associated with.
    private final String ANIMALSPECIES;

    // get the animal for the specific feeding.
    public String animalSpeciesGetter() {
        return this.ANIMALSPECIES;
    }

    // the constructor will initialize the task the same way it would the Task class
    // but also add the animal species associated with the feeding task.
    public OtherTaskFeedingTask(String desc, int dur, int maxWin, String nickname, String animalSpecies)
            throws IllegalArgumentException {
        super(desc, dur, maxWin, nickname);
        this.ANIMALSPECIES = animalSpecies;

    }

}
