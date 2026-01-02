package hua.dit.mobdev_project_2026.db;

import androidx.room.TypeConverter;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyConverters {

    @TypeConverter
    public Long dateToLong(Date date) {
        return (date != null) ? date.getTime() : null;
    }

    @TypeConverter
    public Date longToDate(Long value) {
        return (value != null) ? new Date(value) : null;
    }

    @TypeConverter
    public String timeToString(Time time) {
        return (time != null) ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time) : null;
    }

    @TypeConverter
    public Time stringToTime(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return new Time(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(value).getTime());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format: " + value);
        }
    }

}
