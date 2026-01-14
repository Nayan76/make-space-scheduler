package com.makespace;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class MakeSpaceGUI {

    private JFrame frame;
    private JTextArea outputArea;
    private MakeSpaceScheduler scheduler;
    private JComboBox<String> hourComboStart, minuteComboStart;
    private JComboBox<String> hourComboEnd, minuteComboEnd;
    private JSpinner capacitySpinner;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MakeSpaceGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // FIXED: Constructor now calls initialize() method
    public MakeSpaceGUI() {
        scheduler = new MakeSpaceScheduler();
        initialize(); // THIS LINE WAS MISSING!
    }

    private void initialize() {
        frame = new JFrame("Make Space Ltd. - Meeting Room Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout(10, 10));

        // Add padding around the frame
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createHeaderPanel();
        createInputPanel();
        createOutputPanel();
        createRoomInfoPanel();
        createBufferTimePanel();

        frame.setVisible(true);
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));

        JLabel titleLabel = new JLabel("Make Space Ltd. - Meeting Room Booking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(headerPanel, BorderLayout.NORTH);
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "Meeting Booking",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Start Time:"), gbc);

        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hourComboStart = new JComboBox<>(getHourOptions());
        minuteComboStart = new JComboBox<>(new String[] { "00", "15", "30", "45" });
        startTimePanel.add(hourComboStart);
        startTimePanel.add(new JLabel(":"));
        startTimePanel.add(minuteComboStart);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(startTimePanel, gbc);

        // End Time
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("End Time:"), gbc);

        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hourComboEnd = new JComboBox<>(getHourOptions());
        minuteComboEnd = new JComboBox<>(new String[] { "00", "15", "30", "45" });
        endTimePanel.add(hourComboEnd);
        endTimePanel.add(new JLabel(":"));
        endTimePanel.add(minuteComboEnd);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(endTimePanel, gbc);

        // Capacity
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Number of People:"), gbc);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 2, 20, 1);
        capacitySpinner = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(capacitySpinner, "#");
        capacitySpinner.setEditor(editor);
        capacitySpinner.setPreferredSize(new Dimension(100, 30));

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(capacitySpinner, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton bookButton = createStyledButton("Book Meeting Room", new Color(46, 204, 113));
        bookButton.addActionListener(e -> bookMeeting());

        JButton vacancyButton = createStyledButton("Check Vacancy", new Color(52, 152, 219));
        vacancyButton.addActionListener(e -> checkVacancy());

        JButton clearButton = createStyledButton("Clear", new Color(231, 76, 60));
        clearButton.addActionListener(e -> clearOutput());

        buttonPanel.add(bookButton);
        buttonPanel.add(vacancyButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        inputPanel.add(buttonPanel, gbc);

        frame.add(inputPanel, BorderLayout.WEST);
    }

    private void createOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
                "Output",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBackground(new Color(236, 240, 241));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        outputPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(outputPanel, BorderLayout.CENTER);
    }

    private void createRoomInfoPanel() {
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new GridLayout(3, 1, 5, 5));
        roomPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 2),
                "Available Rooms",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)));

        // C-Cave
        JPanel cavePanel = createRoomCard("C-Cave", "Capacity: 3 people", new Color(52, 152, 219));
        // D-Tower
        JPanel towerPanel = createRoomCard("D-Tower", "Capacity: 7 people", new Color(46, 204, 113));
        // G-Mansion
        JPanel mansionPanel = createRoomCard("G-Mansion", "Capacity: 20 people", new Color(155, 89, 182));

        roomPanel.add(cavePanel);
        roomPanel.add(towerPanel);
        roomPanel.add(mansionPanel);

        frame.add(roomPanel, BorderLayout.EAST);
    }

    private void createBufferTimePanel() {
        JPanel bufferPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        bufferPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                "Buffer Times (Unavailable)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(231, 76, 60)));
        bufferPanel.setBackground(new Color(252, 228, 236));

        String[] bufferTimes = { "09:00 - 09:15", "13:15 - 13:45", "18:45 - 19:00" };

        for (String time : bufferTimes) {
            JLabel timeLabel = new JLabel(time, SwingConstants.CENTER);
            timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            timeLabel.setForeground(new Color(231, 76, 60));
            timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            bufferPanel.add(timeLabel);
        }

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(bufferPanel, BorderLayout.EAST);
        frame.add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createRoomCard(String roomName, String capacity, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setBackground(new Color(255, 255, 255));

        JLabel nameLabel = new JLabel(roomName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(color);

        JLabel capacityLabel = new JLabel(capacity);
        capacityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(capacityLabel, BorderLayout.CENTER);

        return card;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private String[] getHourOptions() {
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        return hours;
    }

    private void bookMeeting() {
        String startTime = hourComboStart.getSelectedItem() + ":" + minuteComboStart.getSelectedItem();
        String endTime = hourComboEnd.getSelectedItem() + ":" + minuteComboEnd.getSelectedItem();
        int capacity = (Integer) capacitySpinner.getValue();

        String result = scheduler.processInput("BOOK " + startTime + " " + endTime + " " + capacity);

        outputArea.append("=== BOOKING REQUEST ===\n");
        outputArea.append("Time: " + startTime + " to " + endTime + "\n");
        outputArea.append("People: " + capacity + "\n");
        outputArea.append("Result: " + result + "\n");
        outputArea.append("=====================\n\n");

        if (result.startsWith("C-") || result.startsWith("D-") || result.startsWith("G-")) {
            showSuccessDialog("Booking Successful!", "Room " + result + " has been booked!");
        } else if (result.equals("NO_VACANT_ROOM")) {
            showErrorDialog("No Vacant Room", "No rooms available for the requested time/capacity.");
        }
    }

    private void checkVacancy() {
        String startTime = hourComboStart.getSelectedItem() + ":" + minuteComboStart.getSelectedItem();
        String endTime = hourComboEnd.getSelectedItem() + ":" + minuteComboEnd.getSelectedItem();

        String result = scheduler.processInput("VACANCY " + startTime + " " + endTime);

        outputArea.append("=== VACANCY CHECK ===\n");
        outputArea.append("Time: " + startTime + " to " + endTime + "\n");
        outputArea.append("Available Rooms: " + result + "\n");
        outputArea.append("=====================\n\n");

        if (result.equals("NO_VACANT_ROOM")) {
            showInfoDialog("No Vacancy", "No rooms available for the requested time period.");
        }
    }

    private void clearOutput() {
        outputArea.setText("");
        hourComboStart.setSelectedIndex(0);
        minuteComboStart.setSelectedIndex(0);
        hourComboEnd.setSelectedIndex(1);
        minuteComboEnd.setSelectedIndex(0);
        capacitySpinner.setValue(2);
    }

    private void showSuccessDialog(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title,
                JOptionPane.WARNING_MESSAGE);
    }
}