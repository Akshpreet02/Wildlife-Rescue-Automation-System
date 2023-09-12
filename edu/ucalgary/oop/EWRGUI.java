package edu.ucalgary.oop;

import javax.swing.*;
import java.awt.*;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class EWRGUI extends JFrame implements ActionListener {
    private JLabel headerLabel;
    private JLabel taskLabel;
    private JLabel startLabel;
    private JLabel dateLabel;

    private JTextField taskField;
    private JTextField startField;
    private JTextField dateField;

    private JButton calcButton;

    private JTextArea schedArea;

    private JComboBox<String> taskComboBox;

    public EWRGUI() {
        // Set the properties of the JFrame
        setTitle("Create An EWR Schedule");
        setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);


        // Create the components
        headerLabel = new JLabel("Create A Schedule", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 23));

        dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateField = new JTextField(10);

        calcButton = new JButton("Generate Schedule");
        calcButton.addActionListener(this);


        Font buttonFont = calcButton.getFont();
        calcButton.setFont(new Font(buttonFont.getName(), buttonFont.getStyle(), 16)); // Change the size (16) to the
                                                                                       // desired size

        schedArea = new JTextArea(20, 20);
        schedArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(calcButton);
        add(inputPanel, BorderLayout.NORTH);

        add(buttonPanel, BorderLayout.WEST);

        JScrollPane scrollPane = new JScrollPane(schedArea);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private String scheduleCalc(String dateStr, boolean backupVolunteerNeeded) throws IllegalArgumentException {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date entry.");
        }

        StringBuilder sb = new StringBuilder();
        int day = Integer.parseInt(dateStr.substring(8));
        int month = Integer.parseInt(dateStr.substring(5, 7));
        int year = Integer.parseInt(dateStr.substring(0, 4));
        Schedule schedule = new Schedule(day, month, year);
        try {
            schedule.readSql();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while (true) {

            try {
                schedule.makeEfficientSchedule();
                break;
            } catch (IllegalArgumentException e) {
                ArrayList<AnimalTreatments> invalidTreatmentsList = schedule.getInvalidTreatmentsList();
                int invalidIndex = schedule.getInvalidTreatmentsListIndex();

                for (int index = invalidIndex; index < invalidTreatmentsList.size(); index++) {
                    int maxWindow = invalidTreatmentsList.get(index).MedicalTaskGetter().MaxWindowGetter();
                    AnimalTreatments invalidTreat = invalidTreatmentsList.get(index);

                    String medTaskDescription = invalidTreat.MedicalTaskGetter().descriptionGetter();
                    String medTaskAnimal = invalidTreat.MedicalTaskGetter().nicknameGetter();
                    String userInstructions = "Treatment Description: " + medTaskDescription
                            + "\nAnimal Treated: " + medTaskAnimal
                            + "\nCant Generate\n Enter a different Start Hour";
                    String input = JOptionPane.showInputDialog(null, userInstructions);

                    try {
                        LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
                        if (!input.substring(3).equals("00")) {
                            throw new IllegalArgumentException(
                                    "minute format is not correct");
                        }
                    } catch (DateTimeParseException f) {
                        throw new IllegalArgumentException(
                                "start time is invalid");
                    }
                    int startHourChosen = Integer.parseInt(input.substring(0, 2));

                    while (true) {
                        if (startHourChosen >= maxWindow) {
                            JOptionPane.showMessageDialog(null,
                                    "start time invalid " + maxWindow + ":00");
                        } else {
                            schedule.moveAnimalTreatmentTasks(invalidTreat, startHourChosen);
                            break;
                        }
                    }

                }
            }
        }
        schedule.calculateEfficient();
        String result = schedule.getFinalSchedule();
        sb.append(result);

        // Check if backup volunteer is needed
        boolean backupVolunteerFound = result.contains("backup volunteer");
        if (backupVolunteerNeeded && backupVolunteerFound) {
            sb.append("\n\nBackup volunteer is required to complete this schedule. Please confirm their availability before continuing.");
            JOptionPane.showMessageDialog(null, "Backup volunteer is required to complete this schedule. Please confirm their availability before continuing.");
        }

        generateFile(sb.toString());
        return sb.toString();
    }



    private void generateFile(String schedule) {
        String fileName = "Final_Schedule" + System.currentTimeMillis() + ".txt";
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            writer.write(schedule);
            JOptionPane.showMessageDialog(this, "Schedule saved", "File Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            String date = dateField.getText();
            String schedule = scheduleCalc(date, rootPaneCheckingEnabled);

            schedArea.setText(schedule);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Create an instance of the ScheduleGUI class
        new EWRGUI();
    }

}
