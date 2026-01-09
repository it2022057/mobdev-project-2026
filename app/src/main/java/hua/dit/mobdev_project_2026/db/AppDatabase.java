package hua.dit.mobdev_project_2026.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * AppDatabase
 * <p>
 * This abstract class defines the main Room database of the application.
 * It acts as the central access point for persistent data storage
 * and exposes Data Access Objects (DAOs) for database operations.
 * </p>
 * <p>
 * The database is configured using Room annotations.
 * </p>
 */
@Database(entities = {Task.class, Status.class}, version = 1, exportSchema = false)
@TypeConverters({MyConverters.class})  // Registers my custom converter (e.g., Date <-> Long)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    public abstract StatusDao statusDao();

}
