package edu.ucalgary.oop;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class TestDesign {
    private Animal animal;
    private Schedule schedule;
    private MedicalTask medicalTask;

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/EWR";
        String username = "user1";
        String password = "ensf";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            // Query 1: Get all animals
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ANIMALS");
            System.out.println("All animals:");
            while (resultSet.next()) {
                System.out.println("AnimalID: " + resultSet.getInt("AnimalID") +
                        ", AnimalNickname: " + resultSet.getString("AnimalNickname") +
                        ", AnimalSpecies: " + resultSet.getString("AnimalSpecies"));
            }
            resultSet.close();

            ResultSet resultSetTasks = statement.executeQuery("SELECT * FROM TASKS");
            System.out.println("\nAll tasks:");
            while (resultSetTasks.next()) {
                System.out.println("TaskID: " + resultSetTasks.getInt("TaskID") +
                        ", Description: " + resultSetTasks.getString("Description") +
                        ", Duration: " + resultSetTasks.getInt("Duration") +
                        ", MaxWindow: " + resultSetTasks.getInt("MaxWindow"));
            }
            resultSetTasks.close();

            ResultSet resultSetTreatments = statement.executeQuery("SELECT TREATMENTS.TreatmentID, ANIMALS.AnimalNickname, TASKS.Description, TREATMENTS.StartHour " + "FROM TREATMENTS " + "INNER JOIN ANIMALS ON TREATMENTS.AnimalID = ANIMALS.AnimalID " + "INNER JOIN TASKS ON TREATMENTS.TaskID = TASKS.TaskID");
            System.out.println("\nAll treatments with animal nicknames and task descriptions:");
            while (resultSetTreatments.next()) {
                System.out.println("TreatmentID: " + resultSetTreatments.getInt("TreatmentID") +
                        ", AnimalNickname: " + resultSetTreatments.getString("AnimalNickname") +
                        ", TaskDescription: " + resultSetTreatments.getString("Description") +
                        ", StartHour: " + resultSetTreatments.getInt("StartHour"));
            }
            resultSetTreatments.close();

            ResultSet resultSetAnimalTreatments = statement.executeQuery("SELECT ANIMALS.AnimalNickname, COUNT(TREATMENTS.TreatmentID) AS TreatmentCount " + "FROM ANIMALS " + "LEFT JOIN TREATMENTS ON ANIMALS.AnimalID = TREATMENTS.AnimalID " + "GROUP BY ANIMALS.AnimalID");
            System.out.println("\nTotal number of treatments for each animal:");
            while (resultSetAnimalTreatments.next()) {
                System.out.println("AnimalNickname: " + resultSetAnimalTreatments.getString("AnimalNickname") +
                        ", TreatmentCount: " + resultSetAnimalTreatments.getInt("TreatmentCount"));
            }
            resultSetAnimalTreatments.close();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error connecting to the database:");
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        // Initialize objects before each test
        animal = new Animal(1, "coyote", "Buddy", "crepuscula");
        schedule = new Schedule(6, 4, 2023);
    }

    @Test
    public void testAnimalConstructor() {
        Animal testAnimal = new Animal(2, "coyote", "Biter", "diurnal");
        assertEquals(2, testAnimal.animalIdGetter());
        assertEquals("coyote", testAnimal.speciesGetter());
        assertEquals("Biter", testAnimal.nicknameGetter());
        assertEquals("diurnal", testAnimal.ActivityPatternGetter());
    }
    
    @Test
    public void testAnimalIdGetter() {
        assertEquals(1, animal.animalIdGetter());
    }
    
    @Test
    public void testSpeciesGetter() {
        assertEquals("coyote", animal.speciesGetter());
    }
    
    @Test
    public void testNicknameGetter() {
        assertEquals("Buddy", animal.nicknameGetter());
    }
    
    @Test
    public void testActivityPatternGetter() {
        assertEquals("crepuscula", animal.ActivityPatternGetter());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testAnimalConstructorWithInvalidSpecies() {
        new Animal(2, null, "Grey", "diurnal");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAnimalConstructorWithInvalidNickname() {
        new Animal(2, "WOLF", null, "diurnal");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAnimalConstructorWithInvalidActivityPattern() {
        new Animal(2, "WOLF", "Grey", null);
    }

    // AnimalInfo enum tests
    @Test
    public void testFeedingDurationGetter() {
        int coyoteDuration = AnimalEnum.FeedingDurationGetter("coyote");
        int porcupineDuration = AnimalEnum.FeedingDurationGetter("porcupine");
        int foxDuration = AnimalEnum.FeedingDurationGetter("fox");
    
        assertEquals(5, coyoteDuration);
        assertEquals(5, porcupineDuration);
        assertEquals(5, foxDuration);
    }
    
    @Test
    public void testCleaningCageDurationGetter() {
        int coyoteDuration = AnimalEnum.CleaningCageDurationGetter("coyote");
        int porcupineDuration = AnimalEnum.CleaningCageDurationGetter("porcupine");
        int foxDuration = AnimalEnum.CleaningCageDurationGetter("fox");
    
        assertEquals(5, coyoteDuration);
        assertEquals(10, porcupineDuration);
        assertEquals(5, foxDuration);
    }
    
    @Test
    public void testFeedingPrepDurationGetter() {
        int coyoteDuration = AnimalEnum.FeedingPrepDurationGetter("coyote");
        int porcupineDuration = AnimalEnum.FeedingPrepDurationGetter("porcupine");
        int foxDuration = AnimalEnum.FeedingPrepDurationGetter("fox");
    
        assertEquals(10, coyoteDuration);
        assertEquals(0, porcupineDuration);
        assertEquals(5, foxDuration);
    }
    
    @Test
    public void testActivityPatternGetterForEnum() {
        String coyoteActivityPattern = AnimalEnum.ActivityPatternGetter("coyote");
        String porcupineActivityPattern = AnimalEnum.ActivityPatternGetter("porcupine");
        String foxActivityPattern = AnimalEnum.ActivityPatternGetter("fox");
    
        assertEquals("crepuscular", coyoteActivityPattern);
        assertEquals("crepuscular", porcupineActivityPattern);
        assertEquals("nocturnal", foxActivityPattern);
    }
    
    @Test
    public void testInvalidSpecies() {
        int feedingDuration = AnimalEnum.FeedingDurationGetter("invalid_species");
        int cleaningCageDuration = AnimalEnum.CleaningCageDurationGetter("invalid_species");
        int feedingPrepDuration = AnimalEnum.FeedingPrepDurationGetter("invalid_species");
        String activityPattern = AnimalEnum.ActivityPatternGetter("invalid_species");
    
        assertEquals(0, feedingDuration);
        assertEquals(0, cleaningCageDuration);
        assertEquals(0, feedingPrepDuration);
        assertNull(activityPattern);
    }
    // AnimalTreatment class tests
    @Test
    public void testAnimalTreatments() {
        AnimalTreatments animalTreatment = new AnimalTreatments(medicalTask, animal, 5);
    
        assertEquals(medicalTask, animalTreatment.MedicalTaskGetter());
        assertEquals(animal, animalTreatment.AnimalTreatedGetter());
        assertEquals(5, animalTreatment.startHourGetter());
    }
    
    @Test
    public void testStartHourSetter() {
        AnimalTreatments animalTreatment = new AnimalTreatments(medicalTask, animal, 5);
        animalTreatment.startHourSetter(10);
    
        assertEquals(10, animalTreatment.startHourGetter());
    }
    // MedicalTask class tests
    @Test
    public void testMedicalTaskConstructor() {
        MedicalTask medicalTask = new MedicalTask("Flush Neck Wound", 25, 1, 1, "Loner");

        assertEquals(1, medicalTask.idTaskGetter());
        assertEquals("Flush Neck Wound", medicalTask.descriptionGetter());
        assertEquals(25, medicalTask.durationGetter());
        assertEquals(1, medicalTask.MaxWindowGetter());
        assertEquals("Loner", medicalTask.nicknameGetter());
    }
    
    @Test
    public void testMedicalTaskConstructorNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MedicalTask("Flush Neck Wound", 25, 1, -1, "Loner");
        });
    }    
    // Task class tests
    public class TestTask extends Task {

        public TestTask(String description, int duration, int maxWindow, String nickname) {
            super(description, duration, maxWindow, nickname);
        }
    }
    
    @Test
    public void testTaskConstructor() {
        TestTask task = new TestTask("Kit feeding", 30, 2, "Annie, Oliver and Mowgli");
    
        assertEquals("Annie, Oliver and Mowgli", task.nicknameGetter());
        assertEquals(2, task.MaxWindowGetter());
        assertEquals(30, task.durationGetter());
        assertEquals("Kit feeding", task.descriptionGetter());
    }
    
    @Test
    public void testTaskConstructorInvalidDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TestTask(null, 30, 2, "Annie, Oliver and Mowgli");
        });
    }
    
    @Test
    public void testTaskConstructorInvalidMaxWindow() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TestTask("Kit feeding", 30, 24, "Annie, Oliver and Mowgli");
        });
    }
    
    @Test
    public void testTaskConstructorInvalidDuration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TestTask("Kit feeding", -1, 2, "Annie, Oliver and Mowgli");
        });
    }

    // OtherTaskCageCleaning class tests
    @Test
    public void testOtherTaskCageCleaningConstructor() {
        OtherTaskCageCleaning task = new OtherTaskCageCleaning("Cage cleaning", 30, 2, "Annie, Oliver and Mowgli");
    
        assertEquals("Annie, Oliver and Mowgli", task.nicknameGetter());
        assertEquals(2, task.MaxWindowGetter());
        assertEquals(30, task.durationGetter());
        assertEquals("Cage cleaning", task.descriptionGetter());
    }
    
    @Test
    public void testOtherTaskCageCleaningConstructorInvalidDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OtherTaskCageCleaning(null, 30, 2, "Annie, Oliver and Mowgli");
        });
    }
    
    @Test
    public void testOtherTaskCageCleaningConstructorInvalidMaxWindow() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OtherTaskCageCleaning("Cage cleaning", 30, 24, "Annie, Oliver and Mowgli");
        });
    }
    
    
    // OtherTaskTaskFeeding class tests
    @Test
    public void testOtherTaskFeedingTaskConstructor() {
        OtherTaskFeedingTask task = new OtherTaskFeedingTask("Feeding", 30, 2, "Annie", "COYOTE");
    
        assertEquals("Annie", task.nicknameGetter());
        assertEquals(2, task.MaxWindowGetter());
        assertEquals(30, task.durationGetter());
        assertEquals("Feeding", task.descriptionGetter());
        assertEquals("COYOTE", task.animalSpeciesGetter());
    }
    
    @Test
    public void testOtherTaskFeedingTaskConstructorInvalidDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OtherTaskFeedingTask(null, 30, 2, "Annie", "COYOTE");
        });
    }
    
    @Test
    public void testOtherTaskFeedingTaskConstructorInvalidMaxWindow() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OtherTaskFeedingTask("Feeding", 30, 24, "Annie", "COYOTE");
        });
    }
    
    // Schedule class tests
    @Test
    public void testScheduleDay() {
        assertEquals(6, schedule.getDay());
    }

    @Test
    public void testScheduleMonth() {
        assertEquals(4, schedule.getMonth());
    }

    @Test
    public void testScheduleYear() {
        assertEquals(2023, schedule.getYear());
    }

    @Test
    public void testScheduleDate() {
        assertEquals(LocalDate.of(2023, 4, 6), schedule.getDate());
    }
    
    // ScheduleSlot tests
    @Test
    public void testScheduleSlotDefaultConstructor() {
        ScheduleSlot slot = new ScheduleSlot();

        assertEquals(60, slot.timeAvaliableGetter());
        assertFalse(slot.backupVolunteerGetter());
        assertTrue(slot.taskGetter().isEmpty());
    }

    @Test
    public void testScheduleSlotConstructorWithTimeAvailable() {
        ScheduleSlot slot = new ScheduleSlot(45);

        assertEquals(45, slot.timeAvaliableGetter());
        assertFalse(slot.backupVolunteerGetter());
        assertTrue(slot.taskGetter().isEmpty());
    }

    @Test
    public void testScheduleSlotSettersAndGetters() {
        ScheduleSlot slot = new ScheduleSlot();

        slot.timeAvailableSetter(50);
        slot.backupVolunteerSetter(true);

        assertEquals(50, slot.timeAvaliableGetter());
        assertTrue(slot.backupVolunteerGetter());
    }

    @Test
    public void testScheduleSlotAddTask() throws IllegalArgumentException {
        ScheduleSlot slot = new ScheduleSlot();
        Task task = new OtherTaskFeedingTask("Feeding", 30, 2, "Annie", "COYOTE");

        slot.taskAdder(task);

        assertEquals(1, slot.taskGetter().size());
        assertEquals(task, slot.taskGetter().get(0));
    }

    @Test
    public void testScheduleSlotAddTimeAvailable() {
        ScheduleSlot slot = new ScheduleSlot();

        slot.timeAvailableAdder(-10);

        assertEquals(50, slot.timeAvaliableGetter());
    }

    @Test
    public void testScheduleSlotAddTimeAvailableNegativeException() {
        ScheduleSlot slot = new ScheduleSlot();

        assertThrows(IllegalArgumentException.class, () -> {
            slot.timeAvailableAdder(-100);
        });
    }

    
}