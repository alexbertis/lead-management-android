package com.community.jboss.leadmanagement.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by carbonyl on 09/12/2017.
 */
@Entity
public class Tag {
    @PrimaryKey @NonNull
    private final String id;
    @NonNull
    private String tag;

    public Tag(@NonNull String id, @NonNull String tag) {
        this.id = id;
        this.tag = tag;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    @NonNull
    public String getId() {
        return id;
    }
}
