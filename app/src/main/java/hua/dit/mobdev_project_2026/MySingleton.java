package hua.dit.mobdev_project_2026;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hua.dit.mobdev_project_2026.db.AppDatabase;

public class MySingleton {


    private static volatile MySingleton instance;

    private final AppDatabase db;
    private final ExecutorService executorService;
    private final Handler handler;

    private MySingleton(Context context) {
        // DB
        db = Room.databaseBuilder(context, AppDatabase.class, "app-database.sqlite").build();
        // Executor - Threads Pool
        this.executorService =
                Executors.newFixedThreadPool(4);
        // Handler - Main Thread "TODOs" List
        this.handler =
                HandlerCompat.createAsync(Looper.getMainLooper());
    }

    public AppDatabase getDb() {
        return db;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Handler getHandler() {
        return handler;
    }

    public void close() {
        if (this.db != null) {
            db.close();
        }
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
