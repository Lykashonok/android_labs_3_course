package com.example.tabatimer.Model.Dao;

import androidx.room.Dao;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.Model.Tables.TabataSetting;

@Dao
public abstract class
TabataSettingDao extends GenericDao<TabataSetting>  {
    public void setSetting(String key, String value) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "Select * from " + getTableName() + " where key = '" + key + "'"
        );
        TabataSetting setting = doGet(query);
        setting.value = value;
        update(setting);
    }

    public String getSetting(String key) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "Select * from " + getTableName() + " where key = '" + key + "'"
        );
        return doGet(query).value;
    }
}
