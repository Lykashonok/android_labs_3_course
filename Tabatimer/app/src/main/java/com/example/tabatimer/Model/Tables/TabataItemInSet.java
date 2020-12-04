package com.example.tabatimer.Model.Tables;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(foreignKeys = {
                @ForeignKey(
                        entity = TabataSet.class,
                        parentColumns = "id",
                        childColumns = "id_tabata_set",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                        entity = TabataItem.class,
                        parentColumns = "id",
                        childColumns = "id_tabata_item",
                        onDelete = ForeignKey.CASCADE),
        },
        indices = {@Index("id_tabata_set"), @Index("id_tabata_item")})
public class TabataItemInSet extends TabataBase{
    public Integer id_tabata_item;
    public Integer id_tabata_set;
    public Integer index;
    public Long duration = (long)0;

    public TabataItemInSet( Integer id_tabata_set, Integer id_tabata_item, Integer index) {
        super();
        this.id_tabata_item = id_tabata_item;
        this.id_tabata_set = id_tabata_set;
        this.index = index;
    }
    public TabataItemInSet(TabataItemInSet iis) {
        this.id_tabata_item = iis.id_tabata_item;
        this.id_tabata_set = iis.id_tabata_set;
    }
}
