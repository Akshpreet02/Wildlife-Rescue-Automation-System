package edu.ucalgary.oop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class Schedule {
    /* TreeMap<time period, list of treatments> */
    private TreeMap<Integer, ArrayList<AnimalTreatments>> animalTreatmentTasks;

    /* HashMap <species, list of feeding tasks> */
    private HashMap<String, ArrayList<Task>> otherTaskFeeding;

    /* This is the entire list of cage cleaning tasks */
    private ArrayList<OtherTaskCageCleaning> otherTaskCage;

    /* This is the entire schedule of the day */
    private HashMap<Integer, ScheduleSlot> schedule;

    private LocalDate date;
    private String finalSchedule;
    private int day;
    private int month;
    private int year;

    private Connection dbConnection;
    private final String URL = "jdbc:mysql://localhost/EWR";
    private final String USERNAME = "oop";
    private final String PASSWORD = "password";

    private ArrayList<AnimalTreatments> invalidTreatmentsList;
    private int invalidTreatmentsListIndex;

    public ArrayList<AnimalTreatments> getInvalidTreatmentsList() {
        return invalidTreatmentsList;
    }

    public int getInvalidTreatmentsListIndex() {
        return invalidTreatmentsListIndex;
    }

    public Schedule(int day, int month, int year) throws IllegalArgumentException {
        if (day < 0 || month < 0 || month > 12 || year < 0) {
            throw new IllegalArgumentException("Invalid date");
        }
        this.day = day;
        this.month = month;
        this.year = year;
        this.date = LocalDate.of(year, month, day);
        this.animalTreatmentTasks = new TreeMap<Integer, ArrayList<AnimalTreatments>>();
        this.otherTaskCage = new ArrayList<OtherTaskCageCleaning>();
        this.finalSchedule = "";

        this.otherTaskFeeding = new HashMap<String, ArrayList<Task>>();
        for (AnimalEnum animal : AnimalEnum.values()) {
            otherTaskFeeding.put(animal.name().toLowerCase(), new ArrayList<Task>());
        }

        this.schedule = new HashMap<Integer, ScheduleSlot>();
        for (int i = 0; i < 24; i++) {
            schedule.put(i, new ScheduleSlot());
        }
    }

    public void readSql() throws SQLException {
        try {
            dbConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();
        }
        createTasks();
        dbConnection.close();

    }

    private void createTasks() {
        ArrayList<Animal> animals = new ArrayList<Animal>();
        ArrayList<MedicalTask> medicalTasks = new ArrayList<MedicalTask>();

        try {
            // Getting the animals from the database and putting them into an arraylist
            Statement statement = dbConnection.createStatement();
            ResultSet queryResult = statement.executeQuery("SELECT * FROM ANIMALS");
            while (queryResult.next()) {

                int animalID = queryResult.getInt("AnimalID");
                String animalNickname = queryResult.getString("AnimalNickname");
                String animalSpecies = queryResult.getString("AnimalSpecies");

                String activityPattern = AnimalEnum.ActivityPatternGetter(animalSpecies);
                Animal animalObj = new Animal(animalID, animalSpecies, animalNickname, activityPattern);
                animals.add(animalObj);
            }

            // Getting the medical tasks from the database and putting them into an
            // arraylist
            String nickname = "";
            queryResult = statement.executeQuery("SELECT * FROM TASKS");
            while (queryResult.next()) {

                int taskID = queryResult.getInt("TaskID");
                String taskDescription = queryResult.getString("Description");
                int taskDuration = queryResult.getInt("Duration");
                int taskMaxWindow = queryResult.getInt("MaxWindow");

                /* This is done to find the animal nickname and add it to the medical task */
                for (Animal iterAnimal : animals) {
                    if (iterAnimal.animalIdGetter() == taskID) {
                        nickname = iterAnimal.nicknameGetter();
                        break;
                    }
                }
                MedicalTask medicalTaskObj = new MedicalTask(taskDescription, taskDuration, taskMaxWindow, taskID,
                        nickname);
                medicalTasks.add(medicalTaskObj);
            }

            /*
             * Getting the medical tasks from the database and connecting animals and tasks
             * to add to animalTreatmentTasks arraylist
             */
            queryResult = statement.executeQuery("SELECT * FROM TREATMENTS");
            while (queryResult.next()) {

                int animalID = queryResult.getInt("AnimalID");
                int taskID = queryResult.getInt("TaskID");
                int taskStartHour = queryResult.getInt("StartHour");

                // to find the animal object
                Animal treatmentAnimal = null; // this is a pointer to the animal object?
                for (Animal iterAnimal : animals) {
                    if (iterAnimal.animalIdGetter() == animalID) {
                        treatmentAnimal = iterAnimal;
                        break;
                    }
                }

                // to find the medical task object
                MedicalTask treatmentTask = null;
                for (MedicalTask iterMedicalTask : medicalTasks) {
                    if (iterMedicalTask.idTaskGetter() == taskID) {
                        treatmentTask = iterMedicalTask;
                        break;
                    }
                }

                if (treatmentAnimal == null || treatmentTask == null) {
                    throw new Exception("Animal or medical task not found");
                }

                AnimalTreatments treatment = new AnimalTreatments(treatmentTask, treatmentAnimal, taskStartHour);
                int treatmentStartHour = treatment.startHourGetter();
                if (animalTreatmentTasks.containsKey(treatmentStartHour)) {
                    animalTreatmentTasks.get(treatmentStartHour).add(treatment);
                } else {
                    ArrayList<AnimalTreatments> newTreatmentList = new ArrayList<AnimalTreatments>();
                    newTreatmentList.add(treatment);
                    animalTreatmentTasks.put(treatmentStartHour, newTreatmentList);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (Exception generalException) {
            generalException.printStackTrace();
        }

        Set<Integer> orphansTaskIDs = new HashSet<Integer>();
        for (ArrayList<AnimalTreatments> iTreatmentList : animalTreatmentTasks.values()) {
            for (AnimalTreatments iTreatment : iTreatmentList) {
                if (iTreatment.MedicalTaskGetter().idTaskGetter() == 1) {
                    orphansTaskIDs.add(iTreatment.AnimalTreatedGetter().animalIdGetter());
                }
            }
        }

        for (Animal iterAnimal : animals) {
            String animalSpecies = iterAnimal.speciesGetter();
            String activityPattern = iterAnimal.ActivityPatternGetter();

            int duration = AnimalEnum.CleaningCageDurationGetter(animalSpecies);
            OtherTaskCageCleaning cageCleaning = null;
            try {
                cageCleaning = new OtherTaskCageCleaning("Cage Cleaning", duration, -1,
                        iterAnimal.nicknameGetter());

                this.otherTaskCage.add(cageCleaning);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            if (orphansTaskIDs.contains(iterAnimal.animalIdGetter())) {
                continue;
            }

            OtherTaskFeedingTask feeding = null;
            int maxWindow = -1;
            int feedingDuration = AnimalEnum.FeedingPrepDurationGetter(animalSpecies);
            feedingDuration += AnimalEnum.FeedingDurationGetter(animalSpecies);

            if (activityPattern.equalsIgnoreCase("nocturnal")) {
                maxWindow = 3;
            } else if (activityPattern.equalsIgnoreCase("diurnal")) {
                maxWindow = 11;
            } else if (activityPattern.equalsIgnoreCase("crepuscular")) {
                maxWindow = 22;
            }

            try {
                feeding = new OtherTaskFeedingTask("Feeding", feedingDuration,
                        maxWindow, iterAnimal.nicknameGetter(), iterAnimal.speciesGetter());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            this.otherTaskFeeding.get(iterAnimal.speciesGetter()).add(feeding);

        }

    }

    public void calculateEfficient() {
        String result = "Schedule for " + this.date.toString() + "\n\n";

        for (int startHour : schedule.keySet()) {
            ScheduleSlot timeslot = schedule.get(startHour);

            if (timeslot.taskGetter().isEmpty()) {
                continue;
            }

            result += startHour + ":00";

            // Adding Backup volunteer tag
            if (timeslot.backupVolunteerGetter()) {
                result += " [+ backup volunteer]\n";
            } else {
                result += "\n";
            }

            result += tasksFormat(timeslot);

            result += "\n";

        }
        this.finalSchedule = result;

    }



    public void makeEfficientSchedule() throws IllegalArgumentException {
        for (int startHour : animalTreatmentTasks.keySet()) {
            ArrayList<AnimalTreatments> treatments = animalTreatmentTasks.get(startHour);
            int nextTime = startHour;
            int iCurrentTreat = 0;
            int count = 0;
            ScheduleSlot timeslot = schedule.get(startHour);
            ScheduleSlot nextTimeslot = schedule.get(nextTime);

            for (AnimalTreatments iterTreatment : treatments) {
                int duration = iterTreatment.MedicalTaskGetter().durationGetter();

                int timeDifference = nextTimeslot.timeAvaliableGetter() - duration;
                if (timeDifference < 0) {
                    if (count == 1) {
                        this.invalidTreatmentsList = treatments;
                        this.invalidTreatmentsListIndex = iCurrentTreat;
                        clearSchedule();
                        throw new IllegalArgumentException(
                                "There are too many medical tasks to fit across two hours at "
                                        + (startHour + count) + ":00 . Please move some tasks.");
                    } else {
                        count++;
                    }

                    timeslot.taskAdder(iterTreatment.MedicalTaskGetter());

                    nextTimeslot.timeAvailableSetter(0);
                    nextTimeslot.backupVolunteerSetter(true);

                    nextTime++;
                    nextTimeslot = schedule.get(nextTime);
                    try {
                        nextTimeslot.timeAvailableAdder(timeDifference);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        timeslot.taskAdder(iterTreatment.MedicalTaskGetter());
                        nextTimeslot.timeAvailableAdder(-duration);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }

                }
                iCurrentTreat++;
            }
        }

        addOtherTaskFeeding();

    }

    private void addOtherTaskFeeding() throws IllegalArgumentException {
        for (String animal : this.otherTaskFeeding.keySet()) {
            ArrayList<Task> otherTaskFeedingList = this.otherTaskFeeding.get(animal);
            if (otherTaskFeedingList.isEmpty()) {
                continue;
            }

            int maxWindow = otherTaskFeedingList.get(0).MaxWindowGetter();
            int startHour = maxWindow - 3;
            ScheduleSlot timeslot = schedule.get(startHour);

            int timeRemaining = timeslot.timeAvaliableGetter();
            int feedingDuration = AnimalEnum.FeedingDurationGetter(animal);
            int prepDuration = AnimalEnum.FeedingPrepDurationGetter(animal);
            int minTimeFeeding = prepDuration + feedingDuration;

            while (startHour < maxWindow) {
                if (timeRemaining >= minTimeFeeding) {
                    int tasksToGroup = (timeRemaining - prepDuration) / feedingDuration;
                    timeRemaining -= (prepDuration);
                    for (int i = 0; i < tasksToGroup; i++) {
                        timeslot.taskAdder(otherTaskFeedingList.get(0));
                        otherTaskFeedingList.remove(0);
                        timeRemaining -= feedingDuration;
                        if (otherTaskFeedingList.isEmpty()) {
                            break;
                        }
                    }
                    timeslot.timeAvailableSetter(timeRemaining);
                    if (otherTaskFeedingList.isEmpty()) {
                        break;
                    }
                }

                startHour++;
                if (startHour == maxWindow) {
                    throw new IllegalArgumentException("There isn't room for all the feeding tasks");
                }
                timeslot = schedule.get(startHour);
                timeRemaining = timeslot.timeAvaliableGetter();
            }
        }
        int startHour = 0;
        for (OtherTaskCageCleaning cageTask : this.otherTaskCage) {
            // int timeTaken = cageTask
            while (startHour < 24) {

                ScheduleSlot timeslot = schedule.get(startHour);
                if (timeslot.timeAvaliableGetter() >= cageTask.durationGetter()) {
                    timeslot.taskAdder(cageTask);
                    try {
                        timeslot.timeAvailableAdder(-cageTask.durationGetter());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                startHour++;
            }
        }
    }

    public void moveAnimalTreatmentTasks(AnimalTreatments treatment, int startHour) {
        int oldStartHour = treatment.startHourGetter();
        animalTreatmentTasks.get(oldStartHour).remove(treatment);

        treatment.startHourSetter(startHour);
        animalTreatmentTasks.get(startHour).add(treatment);
    }

    public void addTask(Task task, int startHour) {
        schedule.get(startHour).taskAdder(task);
    }

    public void addTreatment(AnimalTreatments treatment, int startHour) {
        animalTreatmentTasks.get(startHour).add(treatment);
    }

    public void clearSchedule() {
        this.schedule = new HashMap<Integer, ScheduleSlot>();
        for (int i = 0; i < 24; i++) {
            schedule.put(i, new ScheduleSlot());
        }
    }

    public String getFinalSchedule() {
        return this.finalSchedule;
    }

    public TreeMap<Integer, ArrayList<AnimalTreatments>> getanimalTreatmentTasks() {
        return this.animalTreatmentTasks;
    }

    public int getDay() {
        return this.day;
    }

    public int getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    public ArrayList<AnimalTreatments> getTreatmentList() {
        ArrayList<AnimalTreatments> allTreatments = new ArrayList<>();
        for (ArrayList<AnimalTreatments> iterTreatmentList : animalTreatmentTasks.values()) {
            for (AnimalTreatments iterTreatment : iterTreatmentList) {
                allTreatments.add(iterTreatment);
            }
        }
        return allTreatments;
    }

    private String tasksFormat(ScheduleSlot timeslot) {
        HashMap<String, ArrayList<Object>> feedingCount = new HashMap<String, ArrayList<Object>>();
        String result = "";
        String cageResult = "";

        for (Task task : timeslot.taskGetter()) {

            if (task instanceof OtherTaskFeedingTask) {
                String species = ((OtherTaskFeedingTask) task).animalSpeciesGetter();

                ArrayList<Object> feedingInfo;
                if (feedingCount.containsKey(species)) {
                    feedingInfo = feedingCount
                            .get(((OtherTaskFeedingTask) task).animalSpeciesGetter());
                    int count = (Integer) feedingInfo.get(0); // get the current count
                    count++; // increment the count by 1
                    feedingInfo.set(0, count); // update the count in the ArrayList
                    String nicknames = (String) feedingInfo.get(1);
                    feedingInfo.remove(1);
                    feedingInfo.add(nicknames + ", " + task.nicknameGetter());
                } else {
                    feedingInfo = new ArrayList<Object>();
                    feedingInfo.add(1);
                    feedingInfo.add(task.nicknameGetter());
                }
                feedingCount.put(species, feedingInfo);

            } else if (task instanceof MedicalTask) {
                /* Adding medical tasks */
                result += "* " + task.descriptionGetter() + " (" + task.nicknameGetter() + ")\n";
            } else if (task instanceof OtherTaskCageCleaning) {
                /* Adding cage cleaning tasks */
                cageResult += "* " + task.descriptionGetter() + " (" + task.nicknameGetter() + ")\n";
            } else {
                System.out.println("Error: Unknown task type");
            }
        }

        /* Adding feeding animals */
        for (String animalSpecies : feedingCount.keySet()) {
            ArrayList<Object> feedingInfo = feedingCount.get(animalSpecies);
            int count = (Integer) feedingInfo.get(0);
            String nicknames = (String) feedingInfo.get(1);
            result += "* Feeding - " + animalSpecies + " (" + count + ": " + nicknames + ")\n";
        }

        result += cageResult;
        return result;
    }

    public LocalDate getDate() {
        return this.date;
    }
}
