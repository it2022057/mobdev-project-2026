package hua.dit.mobdev_project_2026;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.MyConverters;
import hua.dit.mobdev_project_2026.db.StatusDao;
import hua.dit.mobdev_project_2026.db.Task;
import hua.dit.mobdev_project_2026.db.TaskDao;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TaskDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.task_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG, "on-create()...");

        // // Find views of all the task details
        final TextView id_text = findViewById(R.id.task_details_activity_id);
        final TextView short_name_text = findViewById(R.id.task_details_activity_name);
        final TextView brief_description_text = findViewById(R.id.task_details_activity_description);
        final TextView difficulty_text = findViewById(R.id.task_details_activity_difficulty);
        final TextView date_text = findViewById(R.id.task_details_activity_date);
        final TextView start_time_text = findViewById(R.id.task_details_activity_start_time);
        final TextView duration_text = findViewById(R.id.task_details_activity_duration);
        final TextView location_text = findViewById(R.id.task_details_activity_location);
        final TextView status_text = findViewById(R.id.task_details_activity_status);

        // Get the id of the task selected (clicked)
        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        Log.d(TAG, "taskId = " + taskId);
        if (taskId == -1) return;

        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            // DB
            AppDatabase db = MySingleton.getInstance(getApplicationContext()).getDb();
            // DAO's
            TaskDao taskDao = db.taskDao();
            StatusDao statusDao = db.statusDao();

            Task task = taskDao.getTaskById(taskId);
            String status = statusDao.getStatusNameById(task.getStatusId());

            handler.post(() -> {
                // Fill UI dynamically with the appropriate task values
                id_text.setText(String.valueOf(task.getId()));
                short_name_text.setText(task.getShortName());
                brief_description_text.setText(task.getDescription());
                difficulty_text.setText(String.valueOf(task.getDifficulty()));
                // Format Date nicely
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                date_text.setText(sdf.format(task.getDateValue()));
                start_time_text.setText(new MyConverters().timeToString(task.getStartTime()));
                duration_text.setText(String.valueOf(task.getDuration()));
                location_text.setText(task.getLocation()); // it can't be null because we made sure in NewTaskActivity
                status_text.setText(status);
            });
        }).start();

        // Navigate to Location Button Listener
        Button navigate_button = findViewById(R.id.task_details_activity_button_navigate);
        navigate_button.setOnClickListener((v) -> {
            Log.d(TAG, "Pressed the navigate button");
        }); // End of navigate_button.setOnClickListener(...)
    }
}