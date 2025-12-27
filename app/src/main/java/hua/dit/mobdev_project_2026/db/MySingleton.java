package hua.dit.mobdev_project_2026.db;

import android.content.Context;

import androidx.room.Room;

public class MySingleton {


    private static volatile MySingleton instance;

    private final AppDatabase db;

    private MySingleton(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "app-database.sqlite").build();
    }

    public AppDatabase getDb() {
        return db;
    }

    public static MySingleton getInstance(Context context) {
        if (instance == null) {
            synchronized (MySingleton.class) {
                if (instance == null) {
                    instance = new MySingleton(context);
                }
            }
        }
        return instance;
    }

}
