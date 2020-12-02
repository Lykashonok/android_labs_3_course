package com.example.tabatimer.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.tabatimer.Model.Dao.GenericDao;
import com.example.tabatimer.Model.Dao.TabataItemDao;
import com.example.tabatimer.Model.Dao.TabataItemInSetDao;
import com.example.tabatimer.Model.Dao.TabataSetDao;
import com.example.tabatimer.Model.Dao.TabataSettingDao;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.Model.Tables.TabataSetting;

@Database(entities = {
        TabataItem.class,
        TabataItemInSet.class,
        TabataSet.class,
        TabataSetting.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DB extends RoomDatabase {
    private static DB database;
    private static String DATABASE_NAME = "db";

    public synchronized static DB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), DB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract TabataItemDao tabataItemDao();
    public abstract TabataItemInSetDao tabataItemInSetDao();
    public abstract TabataSetDao tabataSetDao();
    public abstract TabataSettingDao tabataSettingDao();
}
