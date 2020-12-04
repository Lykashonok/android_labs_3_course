package com.example.tabatimer.Model.Tables;

import androidx.room.Entity;

@Entity
public class TabataItem extends TabataBase{
    public String title;
    public String colour;
    public Long duration;

    public TabataItem(String title, String colour, Long duration) {
        super();
        this.title = title;
        this.colour = colour;
        this.duration = duration;
    }
}
