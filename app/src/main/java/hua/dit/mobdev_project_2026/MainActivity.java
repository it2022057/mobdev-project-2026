package hua.dit.mobdev_project_2026;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

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

        // Every time the app starts, we need to initialize the database with predefined status values
        new Thread(() -> {
            // DB
            AppDatabase db = MySingleton.getInstance(getApplicationContext()).getDb();
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
        }).start();

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
            Intent intent2 = new Intent(MainActivity.this, ConfigActivity.class);
            startActivity(intent2);
            Log.i(TAG, "Going to the app's configuration page");
        }); // End of app_config_button.setOnClickListener(...)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on-destroy()");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Changed state");
    }
}