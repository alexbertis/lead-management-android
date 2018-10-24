package com.community.jboss.leadmanagement.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import com.community.jboss.leadmanagement.data.entities.Contact;
import com.community.jboss.leadmanagement.data.entities.ContactTagJoin;
import com.community.jboss.leadmanagement.data.entities.Tag;

import java.util.List;

@Dao
public interface ContactTagJoinDao {
    @Insert
    void insert(ContactTagJoin... contactTagJoins);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM contact INNER JOIN contacttagjoin ON contact.id = contacttagjoin.contactId WHERE contacttagjoin.tagId = :tagId")
    List<Contact> getContactsForTag(String tagId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM tag INNER JOIN contacttagjoin ON tag.id = contacttagjoin.tagId WHERE contacttagjoin.contactId = :contactId")
    List<Tag> getTagsForContact(String contactId);
}
