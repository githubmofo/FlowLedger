package com.example.flowledger.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String color;
    public String icon;
    
    public Category(String name, String color, String icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
}
