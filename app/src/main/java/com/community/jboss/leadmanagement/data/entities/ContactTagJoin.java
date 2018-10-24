package com.community.jboss.leadmanagement.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.annotation.NonNull;

@Entity(primaryKeys = {
        "contactId",
        "tagId",
}, foreignKeys = {
        @ForeignKey(entity = Contact.class,
                    parentColumns = "id",
                    childColumns = "contactId"),
        @ForeignKey(entity = Tag.class,
                    parentColumns = "id",
                    childColumns = "tagId"),
})
public class ContactTagJoin {
    @NonNull
    private final String contactId;
    @NonNull
    private final String tagId;

    public ContactTagJoin(@NonNull String contactId, @NonNull String tagId) {
        this.contactId = contactId;
        this.tagId = tagId;
    }

    @NonNull
    public String getContactId() {
        return contactId;
    }

    @NonNull
    public String getTagId() {
        return tagId;
    }
}
