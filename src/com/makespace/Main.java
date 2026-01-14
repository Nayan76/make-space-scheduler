package com.makespace;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // Run scheduler tests first
        System.out.println("========================================");
        System.out.println("Running Scheduler Tests");
        System.out.println("========================================");

        MakeSpaceScheduler scheduler = new MakeSpaceScheduler();
        scheduler.testRoomAllocation();

        System.out.println("\n========================================");
        System.out.println("Starting GUI Application");
        System.out.println("========================================");

        // Then launch the GUI
        SwingUtilities.invokeLater(() -> {
            try {
                new MakeSpaceGUI();
            } catch (Exception e) {
                System.err.println("Failed to launch GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
