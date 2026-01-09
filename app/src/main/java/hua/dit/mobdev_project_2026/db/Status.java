package hua.dit.mobdev_project_2026.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Status {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo
    // i.e. "Recorded", "In progress", "Completed", "Expired"
    private String name;

    // Constructor
    public Status(@NonNull String name) {
        this.name = name;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
