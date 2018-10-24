package com.community.jboss.leadmanagement.data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.community.jboss.leadmanagement.data.entities.ContactNumber;

import java.util.List;

@Dao
public interface ContactNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContactNumber contactNumber);

    @Update
    void update(ContactNumber contactNumber);

    @Query("SELECT * FROM contactnumber WHERE number = :number")
    ContactNumber getContactNumber(String number);

    @Query("SELECT * FROM contactnumber WHERE contactId = :contactId")
    List<ContactNumber> getContactNumbers(String contactId);

    @Delete
    void delete(ContactNumber... contactNumber);
}
