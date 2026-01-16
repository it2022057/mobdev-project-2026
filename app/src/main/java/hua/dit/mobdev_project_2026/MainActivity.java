package hua.dit.mobdev_project_2026;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import hua.dit.mobdev_project_2026.bg.MyWorker;
import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.Status;
import hua.dit.mobdev_project_2026.db.StatusDao;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG, "on-create()...");

        // Get the application-wide singleton instance
        final MySingleton mySingleton = MySingleton.getInstance(getApplicationContext());
        // Executor used to run tasks off the UI thread
        final Executor executor = mySingleton.getExecutorService();

        // Periodic background work: Runs every 1 hour anytime
        // from minute 45 to minute 60 (15 min flex) to update task status
        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(MyWorker.class,
                        1, TimeUnit.HOURS,
                        15, TimeUnit.MINUTES)
                        .build();

        // Enqueue unique periodic work
        final String workUID = "TASK-PERIODIC-CHECK-123";
        final ExistingPeriodicWorkPolicy workPolicy = ExistingPeriodicWorkPolicy.KEEP;
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(workUID, workPolicy, workRequest);

        // Every time the app starts, we need to initialize the database with predefined status values
        executor.execute(() -> {
            // DB
            AppDatabase db = mySingleton.getDb();
            // Status Data
            StatusDao statusDao = db.statusDao();
            if (statusDao.getAllStatus().isEmpty()) {
                final List<Status> statusList = new ArrayList<>();
                statusList.add(new Status("RECORDED"));
                statusList.add(new Status("IN_PROGRESS"));
                statusList.add(new Status("EXPIRED"));
                statusList.add(new Status("COMPLETED"));

                List<Long> statusIds = statusDao.insertAll(statusList);
                Log.i(TAG, "Status Data - statusIds: " + statusIds.size() + " :: " + statusIds);
            }
        });

        // See Tasks Button Listener
        Button tasks_button = findViewById(R.id.main_activity_button1);
        tasks_button.setOnClickListener((v) -> {
            Log.d(TAG, "Pressed the See tasks button");
            Intent intent = new Intent(MainActivity.this, ViewTasksActivity.class);
            startActivity(intent);
            Log.i(TAG, "Going to view all the non-completed tasks");
        }); // End of tasks_button.setOnClickListener(...)

        // App Config Button Listener
        ImageButton app_config_button = findViewById(R.id.main_activity_button2);
        app_config_button.setOnClickListener((v) -> {
            Log.d(TAG, "Pressed the Configuration button");
            Intent intent2 = new Intent(MainActivity.this, ConfigActivity.class);
            startActivity(intent2);
            Log.i(TAG, "Going to the app's configuration page");
        }); // End of app_config_button.setOnClickListener(...)

        // Test DB Content Provider Button Listener
        ImageButton test_db_content_provider_button = findViewById(R.id.main_activity_button3);
        test_db_content_provider_button.setOnClickListener((v) -> {
            Log.d(TAG, "Pressed the Database button");
            executor.execute(() -> {
                // Insert Task using Content Provider
                ContentValues cv = new ContentValues();
                cv.put("shortName", "Cryptography Exam");
                cv.put("briefDescription", "I need to do well in the exam to pass the course");
                cv.put("difficulty", 7);
                cv.put("startTime", "09:00");
                cv.put("duration", 3);
                cv.put("location", "Harokopio University");

                // Insert into content://AUTHORITY/task
                Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URI, cv);
                // The returned URI also contains the autogenerated id of the new row (e.g. content://AUTHORITY/task/<id>)
                Log.i(TAG, "Content Provider - Data Inserted ! uri= " + uri);

                // Query Data using Content Provider for URI pattern with more than one row
                Cursor cursor_dir = getContentResolver().query(MyContentProvider.CONTENT_URI, null, null, null, null);
                Log.i(TAG, "Content Provider for URI pattern with more than one row - Query Response:");
                if (cursor_dir != null) {
                    cursor_dir.moveToPosition(-1);
                    // Iterate over all rows
                    while (cursor_dir.moveToNext()) {
                        int id = cursor_dir.getInt(0);
                        String shortName = cursor_dir.getString(1);
                        String status = cursor_dir.getString(2);
                        Log.i(TAG, "Task with id " + id + ": " + shortName + ", " + status);
                    }
                    cursor_dir.close();
                }

                // Query Data using Content Provider for URI pattern with a single row
                assert uri != null;
                // Only returns one row
                Cursor cursor_item = getContentResolver().query(uri, null, null, null, null);

                Log.i(TAG, "Content Provider for URI pattern with a single row - Query Response:");
                if (cursor_item != null) {
                    cursor_item.moveToPosition(-1);
                    while (cursor_item.moveToNext()) {
                        int id = cursor_item.getInt(0);
                        String shortName = cursor_item.getString(1);
                        String status = cursor_item.getString(2);
                        Log.i(TAG, "Task with id " + id + ": " + shortName + ", " + status);
                    }
                    cursor_item.close();
                }

                // Update Data using Content Provider
                cv.put("difficulty", 8);
                cv.put("statusName", "IN_PROGRESS");
                cv.put("location", "Harokopio University, Thiseos 70");

                // Update content://.../task/<id>
                int rows_updated = getContentResolver().update(uri, cv, null, null);
                Log.i(TAG, "Rows Updated: " + rows_updated);
                Log.i(TAG, "Content Provider - Task with id " + uri.getLastPathSegment() + " Updated ! uri= " + uri);

                // Delete Data using Content Provider
                int rows_deleted = getContentResolver().delete(uri, null, null);
                Log.i(TAG, "Rows Deleted: " + rows_deleted);
                Log.i(TAG, "Content Provider - Task with id " + uri.getLastPathSegment() + " Deleted ! uri= " + uri);
            });
        }); // End of test_db_content_provider_button.setOnClickListener(...)

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on-destroy()");
    }

}