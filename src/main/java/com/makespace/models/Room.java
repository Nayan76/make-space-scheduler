package main.java.com.makespace.models;
import java.time.LocalTime;

public class Room {
    private String name;
    private int capacity;
    private boolean[] schedule;

    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.schedule = new boolean[96]; // Represents each hour of the day
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean[] getSchedule() {
        return schedule;
    }

    public boolean isSlotAvailable(int slot){
        return !schedule[slot];
    }

    public void bookSlot(int slot){
        schedule[slot] = true;
    }

    public void freeSlot(int slot){
        schedule[slot] = false;
    }
}
