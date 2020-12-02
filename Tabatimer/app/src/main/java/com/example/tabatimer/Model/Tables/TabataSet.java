package com.example.tabatimer.Model.Tables;

import androidx.room.Entity;

@Entity
public class TabataSet extends TabataBase{
    public String title;
    public String colour;
    public Integer index;

    public TabataSet(String title, String colour, Integer index) {
        super();
        this.title = title;
        this.colour = colour == "" ? "#FFFFFF" : colour ;
        this.index = index;
    }
}
