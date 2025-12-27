package hua.dit.mobdev_project_2026.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Task.class, Status.class}, version = 1, exportSchema = false)
@TypeConverters({MyConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    public abstract StatusDao statusDao();

}
