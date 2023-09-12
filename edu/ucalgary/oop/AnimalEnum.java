package edu.ucalgary.oop;

/**
 * @author Rohan Kapila, Fayzan Toor, Zaid Ahmed, Akshpreet Singh <a
 *         href="mailto:rohan.kapila@ucalgary.ca">rohan.kapila@ucalgary.ca</a>
 * @version 1.5
 * @since 1.0
 */

enum AnimalEnum {

    COYOTE(5, 10, 5, "crepuscular"),
    PORCUPINE(5, 0, 10, "crepuscular"),
    FOX(5, 5, 5, "nocturnal"),
    RACCOON(5, 0, 5, "nocturnal"),
    BEAVER(5, 0, 5, "diurnal");

    public final String activePattern;
    public final int cleanCageDur;
    public final int feedAnimalDur;
    public final int feedAnimalPrep;

    // initializing enum with feeding time, prep Time, cleaning Time, activity
    // Pattern
    AnimalEnum(int feedAnimalDur, int feedAnimalPrep, int cleanCageDur, String activePattern) {
        // setting feedingtime, prep time, cleaning time and activity pattern with
        // constructor.
        this.feedAnimalDur = feedAnimalDur;
        this.feedAnimalPrep = feedAnimalPrep;
        this.cleanCageDur = cleanCageDur;
        this.activePattern = activePattern;
    }

    public static int FeedingDurationGetter(String species) {
        for (AnimalEnum i : AnimalEnum.values()) {
            // check if the enum name is equal to the animal parameter
            if (i.name().equalsIgnoreCase(species)) {
                // if the animal is equal to the name of enum return the feedingTime.
                return i.feedAnimalDur;
            }
        }

        return 0;

    }

    public static int CleaningCageDurationGetter(String species) {
        for (AnimalEnum i : AnimalEnum.values()) {
            // check if the enum name is equal to the animal parameter
            if (i.name().equalsIgnoreCase(species)) {
                // if the animal is equal to the name of enum return the cleaning Time.
                return i.cleanCageDur;
            }
        }
        return 0;

    }

    public static int FeedingPrepDurationGetter(String species) {
        for (AnimalEnum i : AnimalEnum.values()) {
            // check if the enum name is equal to the animal parameter
            if (i.name().equalsIgnoreCase(species)) {
                // if the animal is equal to the name of enum return the feeding prep Time.
                return i.feedAnimalPrep;
            }
        }
        return 0;
    }

    public static String ActivityPatternGetter(String species) {
        for (AnimalEnum i : AnimalEnum.values()) {
            // check if the enum name is equal to the animal parameter
            if (i.name().equalsIgnoreCase(species)) {
                // if the animal is equal to the name of enum return the activity Pattern
                return i.activePattern;
            }
        }
        return null;
    }

}
