package com.makespace;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MakeSpaceScheduler {
    
    private static class RoomInternal {

        private String name;
        private int capacity;
        private boolean[] schedule;

        RoomInternal(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
            this.schedule = new boolean[96]; // Represents each 15-minute slot in a day
        }
    }

    private static final LocalTime[][] BUFFER_TIMES = {
        {LocalTime.of(9, 0), LocalTime.of(9, 15)},
        {LocalTime.of(13, 15), LocalTime.of(13, 45)},
        {LocalTime.of(18, 45), LocalTime.of(19, 0)}
    };

    private List<RoomInternal> rooms;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public MakeSpaceScheduler(){
        initializeRooms();
    }

    private void initializeRooms(){
        rooms = new ArrayList<>();
        rooms.add(new RoomInternal("C-Cave", 3));
        rooms.add(new RoomInternal("D-Tower", 7));
        rooms.add(new RoomInternal("G-Mansion", 20));
        Collections.sort(rooms, Comparator.comparingInt(r -> r.capacity));
    }

    private int timeToSlot(LocalTime time) {
        return time.getHour() * 4 + time.getMinute() / 15;
    }

    private  boolean isValidTimeInterval(LocalTime time){
        return time.getMinute() % 15 == 0;
    }

    private boolean overlapsWithBuffer(LocalTime starTime, LocalTime endTime){
        for (LocalTime[] buffer : BUFFER_TIMES){
            LocalTime bufferStart = buffer[0];
            LocalTime bufferEnd = buffer[1];

            if (starTime.isBefore(bufferEnd) && endTime.isAfter(bufferStart)){
                return true;
            }
        }
        return false;
    }

    private boolean isRoomAvailable(RoomInternal room, int startSlot, int endSlot){
        for (int i = startSlot; i < endSlot; i++){
            if (room.schedule[i]){
                return false;
            }
        }
        return true;
    }

    private void bookRoom(RoomInternal room, int startSlot, int endSlot){
        for (int i = startSlot; i < endSlot; i++){
            room.schedule[i] = true;
        }
    }

    private RoomInternal findOptimalRoom(int requiredCapacity, int startSlot, int endSlot){
        for (RoomInternal room : rooms){
            if (room.capacity >= requiredCapacity && isRoomAvailable(room, startSlot, endSlot)){
                return room;
            }
        }
        return null;
    }

    public String processBooking(String startTimeStr, String endTimeStr, int personCapacity){
        if (personCapacity < 2 || personCapacity > 20){
            return "NO_VACANT_ROOM";
        }

        try {
            LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
            LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

            if (!isValidTimeInterval(startTime) || !isValidTimeInterval(endTime)){
                return "INCORRECT_INPUT";
            }

            if (!endTime.isAfter(startTime)) {
                return "INCORRECT_INPUT";
            }

            if (overlapsWithBuffer(startTime, endTime)){
                return "NO_VACANT_ROOM";
            }

            int startSlot = timeToSlot(startTime);
            int endSlot = timeToSlot(endTime);

            RoomInternal optimalRoom = findOptimalRoom(personCapacity, startSlot, endSlot);

            if (optimalRoom != null){
                bookRoom(optimalRoom, startSlot, endSlot);
                return optimalRoom.name;
            } else {
                return "NO_VACANT_ROOM";
            }

        } catch (DateTimeParseException e){
            return "INCORRECT_INPUT";
        }
    }

    public String processVacancy(String startTimeStr, String endTimeStr){
        try {
            LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
            LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

            if (!isValidTimeInterval(startTime) || !isValidTimeInterval(endTime)){
                return "INCORRECT_INPUT";
            }

            if (!endTime.isAfter(startTime)) {
                return "INCORRECT_INPUT";
            }

            int startSlot = timeToSlot(startTime);
            int endSlot = timeToSlot(endTime);

            List<String> availableRooms = new ArrayList<>();

            for (RoomInternal room : rooms){
                if (isRoomAvailable(room, startSlot, endSlot)){
                    availableRooms.add(room.name);
                }
            }

            if (availableRooms.isEmpty()){
                return "NO_VACANT_ROOM";
            } else {
                return String.join(",", availableRooms);
            }

        } catch (DateTimeParseException e){
            return "INCORRECT_INPUT";
        }
    }

    public String processInput(String input){
        String[] parts = input.split("\\s+");

        if (parts.length < 1) {
            return "INCORRECT_INPUT";
        }

        String command = parts[0].toUpperCase();
        
        try{
            switch (command) {
                case "BOOK":
                    if (parts.length != 4) {
                        return "INCORRECT_INPUT";
                    }
                    int capacity = Integer.parseInt(parts[3]);
                    return processBooking(parts[1], parts[2], capacity);
                case "VACANCY":
                    if (parts.length != 3) {
                        return "INCORRECT_INPUT";
                    }
                    return processVacancy(parts[1], parts[2]);

                default:
                    return "INCORRECT_INPUT";
            }
        } catch (NumberFormatException e){
            return "INCORRECT_INPUT";
        }
    }

    public List<String> getAllRoomNames(){
        List<String> roomNames = new ArrayList<>();
        for (RoomInternal room : rooms){
            roomNames.add(room.name + " (" + room.capacity + " persons)");
        }
        return roomNames;
    }

    public List<String> getBufferTimes(){
        List<String> bufferTimes = new ArrayList<>();
        for (LocalTime[] buffer : BUFFER_TIMES){
            bufferTimes.add(buffer[0].toString() + " - " + buffer[1].toString());
        }
        return bufferTimes;
    }

    public void testRoomAllocation() {
        System.out.println("=== Testing Room Allocation ===");

        // Clear any existing bookings
        initializeRooms();

        // Test 1: 3 people should get C-Cave
        String result1 = processBooking("10:00", "11:00", 3);
        System.out.println("Test 1 - 3 people @ 10-11: " + result1 +
                " (Expected: C-Cave, Got: " + result1 + ")");

        // Test 2: Another 3 people at same time should get D-Tower (C-Cave booked)
        String result2 = processBooking("10:00", "11:00", 3);
        System.out.println("Test 2 - 3 people @ 10-11: " + result2 +
                " (Expected: D-Tower, Got: " + result2 + ")");

        // Test 3: 2 people at different time should get C-Cave
        String result3 = processBooking("11:00", "12:00", 2);
        System.out.println("Test 3 - 2 people @ 11-12: " + result3 +
                " (Expected: C-Cave, Got: " + result3 + ")");
    }
}
