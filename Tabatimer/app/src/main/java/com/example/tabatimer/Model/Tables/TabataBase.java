package com.example.tabatimer.Model.Tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;


@Entity
public class TabataBase {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public Date createdAt;

    public TabataBase() {
        this.createdAt = new Date(System.currentTimeMillis());
    }
}
