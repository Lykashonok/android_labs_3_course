package com.example.tabatimer.Model.Dao;

import androidx.room.Dao;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;

import java.util.Collections;
import java.util.List;

@Dao
public abstract class TabataItemInSetDao extends GenericDao<TabataItemInSet>  {

    public List<TabataItemInSet> getAll(int indexOfSet) {
        if (indexOfSet != 0) {
            SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                    "Select * from " + getTableName() + " where id_tabata_set = " + indexOfSet
            );
            return doGetAll(query);
        } else {
            return Collections.emptyList();
        }
    }
}
