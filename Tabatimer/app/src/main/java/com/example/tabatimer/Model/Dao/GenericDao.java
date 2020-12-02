package com.example.tabatimer.Model.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.tabatimer.Model.Tables.TabataBase;
import java.lang.reflect.ParameterizedType;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class GenericDao<T> {

    @Insert(onConflict = REPLACE)
    public abstract void insert(T obj);

    @Delete
    public abstract void delete(T obj);

    @Delete
    public abstract void reset(List<T> obj);

    @Update
    public abstract void update(T obj);

    @Update
    public abstract void update(List<T> obj);

    public T getById(Integer id) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "Select * from " + getTableName() + " where id = " + id.toString());
        return doGet(query);
    }

    public List<T> getAll() {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "Select * from " + getTableName()
        );
        return doGetAll(query);
    }

    public String getTableName() {

        Class c = (Class)
                ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        // tableName = StringUtil.toSnakeCase(clazz.getSimpleName());
        String tableName = c.getSimpleName();
        return tableName;
    }

    @RawQuery
    protected abstract T doGet(SupportSQLiteQuery query);

    @RawQuery
    protected abstract List<T> doGetAll(SupportSQLiteQuery query);
}

