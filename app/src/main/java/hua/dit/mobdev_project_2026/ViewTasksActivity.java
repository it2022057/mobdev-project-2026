package hua.dit.mobdev_project_2026;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewTasksActivity extends AppCompatActivity {

    private static final String TAG = "ViewTasksActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_tasks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_tasks), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG, "on-create()...");

        // Export Tasks Button Listener
        Button download_button = findViewById(R.id.view_tasks_download_button);
        download_button.setOnClickListener((v) -> {
            Log.d(TAG, "Download button pressed");
        }); // End of download_button.setOnClickListener(...)

        // New Task Button Listener
        Button new_task_button = findViewById(R.id.view_tasks_add_button);
        new_task_button.setOnClickListener((v) -> {
            Log.d(TAG, "New task button pressed");
            Intent intent = new Intent(ViewTasksActivity.this, NewTaskActivity.class);
            startActivity(intent);
            Log.i(TAG, "Going to a new page to add a new task");
        }); // End of new_task_button.setOnClickListener(...)
    }
}