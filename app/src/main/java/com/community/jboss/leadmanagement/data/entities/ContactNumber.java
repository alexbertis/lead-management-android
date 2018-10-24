package com.community.jboss.leadmanagement.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * Created by carbonyl on 09/12/2017.
 */
@Entity(foreignKeys = @ForeignKey(
        entity = Contact.class,
        parentColumns = "id",
        childColumns = "contactId",
        onDelete = ForeignKey.CASCADE
))
public class ContactNumber {
    @PrimaryKey @NonNull
    private final String id;
    @NonNull
    private final String contactId;
    @NonNull
    private String number;

    @Ignore
    public ContactNumber(@NonNull String number, @NonNull String contactId) {
        this(UUID.randomUUID().toString(), number, contactId);
    }

    public ContactNumber(@NonNull String id,
                         @NonNull String number,
                         @NonNull String contactId) {
        this.id = id;
        this.number = number;
        this.contactId = contactId;
    }

    @NonNull
    public String getNumber() {
        return number;
    }

    public void setNumber(@NonNull String number) {
        this.number = number;
    }

    @NonNull
    public String getContactId() {
        return contactId;
    }

    @NonNull
    public String getId() {
        return id;
    }
}
