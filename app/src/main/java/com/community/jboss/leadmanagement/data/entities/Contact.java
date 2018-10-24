package com.community.jboss.leadmanagement.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * Created by carbonyl on 09/12/2017.
 */
@Entity
public class Contact {
    @PrimaryKey @NonNull
    private final String id;
    private String name;

    @Ignore
    public Contact(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public Contact(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
