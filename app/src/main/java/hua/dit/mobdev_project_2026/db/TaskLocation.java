package hua.dit.mobdev_project_2026.db;

import androidx.room.ColumnInfo;

public class TaskLocation {

    public String street;

    public String state;

    public String city;

    @ColumnInfo(name = "post_code")
    public int postCode;
}
