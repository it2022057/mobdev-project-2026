package hua.dit.mobdev_project_2026.db;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Status.class,
                parentColumns = {"id"},
                childColumns = {"status_id"})
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
    private int startTime; // TODO: Date or int ??? I will decide later on...

    @ColumnInfo
    @IntRange(from = 1)
    private int duration;

    @ColumnInfo(name = "status_id")
    private long statusId;

    @ColumnInfo
    @Nullable
    private String location; // TODO: Make it @Embedded with extra info (i.e. address, state, etc.) or leave it like that

    public Task(String shortName, String description, int difficulty, Date dateValue, int startTime, int duration, long statusId, @Nullable String location) {
        this.shortName = shortName;
        this.description = description;
        this.difficulty = difficulty;
        this.dateValue = dateValue;
        this.startTime = startTime;
        this.duration = duration;
        this.statusId = statusId;
        this.location = location;
    }

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

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
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

    @Nullable
    public String getLocation() {
        return location;
    }

    public void setLocation(@Nullable String location) {
        this.location = location;
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
