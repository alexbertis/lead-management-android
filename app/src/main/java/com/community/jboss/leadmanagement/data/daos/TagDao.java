package com.community.jboss.leadmanagement.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.community.jboss.leadmanagement.data.entities.Tag;

import java.util.List;

@Dao
public interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Tag... tags);

    @Query("SELECT * FROM tag")
    List<Tag> getAllTags();
}
