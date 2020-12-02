package com.example.tabatimer.Model.Tables;

import androidx.room.Entity;

@Entity
public class TabataSetting extends TabataBase{
    public String key;
    public String value;

    public TabataSetting(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }
}
