package hua.dit.mobdev_project_2026.db;


import androidx.room.ColumnInfo;

public class TaskWithStatus {

    private int id;

    @ColumnInfo(name = "short_name")
    private String shortName;

    private String status;

    public TaskWithStatus(int id, String shortName, String status) {
        this.id = id;
        this.shortName = shortName;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskWithStatus{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
