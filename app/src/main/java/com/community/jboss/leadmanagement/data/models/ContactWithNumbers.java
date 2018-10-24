package com.community.jboss.leadmanagement.data.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.community.jboss.leadmanagement.data.entities.Contact;
import com.community.jboss.leadmanagement.data.entities.ContactNumber;

import java.util.List;

public class ContactWithNumbers {
    @Embedded
    public Contact contact;

    @Relation(parentColumn = "id", entityColumn = "contactId", entity = ContactNumber.class)
    public List<ContactNumber> contactNumberList;
}
