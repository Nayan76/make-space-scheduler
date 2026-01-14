package com.makespace.models;

import java.time.LocalTime;

public class Booking {
    private LocalTime startTime;
    private LocalTime endTime;
    private int personCapacity;
    private Room room;

    public Booking(LocalTime startTime, LocalTime endTime, int personCapacity, Room room) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.personCapacity = personCapacity;
        this.room = room;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getPersonCapacity() {
        return personCapacity;
    }

    public Room getRoom() {
        return room;
    }
}
