package edu.ucalgary.oop;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.3
 * @since 1.0
 */

// create class associated with the animal treatments that associates the
// starthour of task the anoimal species and the medical task itself.
public class AnimalTreatments {
    // create private variables.
    private MedicalTask medicalTask;
    private Animal animalTreated;
    private int startHour;

    // retrieves the medicaltask
    public MedicalTask MedicalTaskGetter() {
        return this.medicalTask;
    }

    // retrieves the animal that is getting treated.
    public Animal AnimalTreatedGetter() {
        return this.animalTreated;
    }

    // sets starthour of the treatment.
    public void startHourSetter(int startHour) {
        this.startHour = startHour;
    }

    // retrieves the starthour of the treatment.
    public int startHourGetter() {
        return this.startHour;
    }

    // constructor that sets the class animaltrated by associating a medicaltask the
    // animal to be treated and the start time of the treatment.
    public AnimalTreatments(MedicalTask medicalTask, Animal animalTreated, int startHour) {
        this.medicalTask = medicalTask;
        this.animalTreated = animalTreated;
        this.startHour = startHour;
    }

}
