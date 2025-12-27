package hua.dit.mobdev_project_2026.db;

import androidx.room.TypeConverter;

import java.util.Date;

public class MyConverters {

    @TypeConverter
    public  Long dateToLong(Date date) {
        return (date != null) ? date.getTime() : null;
    }

    @TypeConverter
    public Date longToDate(Long value) {
        return (value != null) ? new Date(value) : null;
    }

}
