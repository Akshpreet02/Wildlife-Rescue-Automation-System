package edu.ucalgary.oop;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.2
 * @since 1.0
 */

// create a class clalled animal that will create each animal with there typr,
// animal id , nicknam and species.
public class Animal {
    // create attributes for each animal object
    private String activityPattern;
    private int animalID;
    private String nickname;
    private String species;

    // return pattern
    public String ActivityPatternGetter() {
        return this.activityPattern;
    }

    // return nickname.
    public String nicknameGetter() {
        return this.nickname;
    }

    // return the species of animal
    public String speciesGetter() {
        return this.species;
    }

    // return the id of animal
    public int animalIdGetter() {
        return this.animalID;

    }

    // animal constructor check s if animals attributes are valid and assigns each
    // argument to the attributes of the animal class.
    public Animal(int animalID, String species, String nickname, String activityPattern)
            throws IllegalArgumentException {
        if (activityPattern == null || nickname == null || species == null || animalID < 0) {
            throw new IllegalArgumentException("Invalid animal created");
        }
        this.activityPattern = activityPattern;
        this.animalID = animalID;
        this.nickname = nickname;
        this.species = species;

    }
}
