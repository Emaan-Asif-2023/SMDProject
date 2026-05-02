package com.example.project;

public class Booking {
    private int id;
    private String checkIn;
    private String checkOut;
    private int personId; // Links to Person.java
    private int roomId;   // Links to Room.java

    private String hotelName;
    private String roomNumber;
    private String roomType;

    public Booking() {}

    public Booking(String checkIn, String checkOut, int personId, int roomId) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.personId = personId;
        this.roomId = roomId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
}