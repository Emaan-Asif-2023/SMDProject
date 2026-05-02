package com.example.project;

public class Hotel {
    private int id;
    private String name;
    private String location;
    private String description;
    private int imageResId; // NEW: Stores R.drawable.your_image_name

    // Empty constructor required for creation
    public Hotel() {}

    // Updated constructor
    public Hotel(String name, String location, String description, int imageResId) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}