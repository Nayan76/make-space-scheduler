package com.makespace;

public class TestScheduler {
    public static void main(String[] args) {
        System.out.println("=== Make Space Scheduler Unit Tests ===");
        System.out.println();

        MakeSpaceScheduler scheduler = new MakeSpaceScheduler();

        // Run the allocation test
        scheduler.testRoomAllocation();

        System.out.println("\n=== Additional Tests ===");

        // Test buffer times
        System.out.println("\nTest 4: Buffer time (09:00-09:15)");
        String bufferTest = scheduler.processInput("BOOK 09:00 10:00 5");
        System.out.println("Result: " + bufferTest + " (Expected: NO_VACANT_ROOM)");

        // Test invalid capacity
        System.out.println("\nTest 5: Invalid capacity (25 people)");
        String capacityTest = scheduler.processInput("BOOK 10:00 11:00 25");
        System.out.println("Result: " + capacityTest + " (Expected: NO_VACANT_ROOM)");

        // Test vacancy
        System.out.println("\nTest 6: Vacancy check");
        String vacancy = scheduler.processInput("VACANCY 14:00 15:00");
        System.out.println("Vacancy at 14-15: " + vacancy);

        // Test optimal room selection
        System.out.println("\nTest 7: Optimal room selection");
        scheduler = new MakeSpaceScheduler(); // Fresh scheduler
        String room1 = scheduler.processInput("BOOK 15:00 16:00 4");
        System.out.println("4 people: " + room1 + " (Expected: D-Tower)");

        String room2 = scheduler.processInput("BOOK 15:00 16:00 2");
        System.out.println("2 people: " + room2 + " (Expected: C-Cave)");

        String room3 = scheduler.processInput("BOOK 15:00 16:00 15");
        System.out.println("15 people: " + room3 + " (Expected: G-Mansion)");
    }
}
