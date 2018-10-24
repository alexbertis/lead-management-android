package com.community.jboss.leadmanagement.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.community.jboss.leadmanagement.data.entities.Contact;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Update
    void update(Contact contact);

    @Query("SELECT * FROM contact WHERE id = :id")
    Contact getContact(String id);

    @Query("SELECT * FROM contact")
    LiveData<List<Contact>> getContacts();
}
