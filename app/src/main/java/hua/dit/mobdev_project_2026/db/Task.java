package hua.dit.mobdev_project_2026.db;

import androidx.annotation.IntRange;
import androidx.annotation.Size;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Status.class,          // Parent table
                parentColumns = {"id"},         // Primary key in Status
                childColumns = {"status_id"})   // Foreign key in Task
        }, indices = { @Index("status_id") } )
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "short_name")
    @Size(max = 20)
    private String shortName;

    @ColumnInfo
    @Size(max = 150)
    private String description;

    @ColumnInfo
    @IntRange(from = 0, to = 10)
    private int difficulty;

    @ColumnInfo(name = "date")
    private Date dateValue;

    @ColumnInfo(name = "start_time")
    private Time startTime;

    @ColumnInfo
    @IntRange(from = 1, to = 24)
    private int duration;

    @ColumnInfo(name = "status_id")
    private long statusId;

    @ColumnInfo
    private String location;

    @Ignore
    private boolean isNotified = false;

    // Constructor
    public Task(String shortName, String description, int difficulty, Date dateValue, Time startTime, int duration, long statusId, String location) {
        this.shortName = shortName;
        this.description = description;
        this.difficulty = difficulty;
        this.dateValue = dateValue;
        this.startTime = startTime;
        this.duration = duration;
        this.statusId = statusId;
        this.location = location;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getStatusId() {
        return statusId;
    }

    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", difficulty=" + difficulty +
                ", dateValue=" + dateValue +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", statusId=" + statusId +
                ", location='" + location + '\'' +
                '}';
    }
}
