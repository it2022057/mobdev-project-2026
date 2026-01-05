package hua.dit.mobdev_project_2026;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.text.SimpleDateFormat;
import java.util.Locale;

import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.MyConverters;
import hua.dit.mobdev_project_2026.db.StatusDao;
import hua.dit.mobdev_project_2026.db.Task;
import hua.dit.mobdev_project_2026.db.TaskDao;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TaskDetailsActivity";

    private AppDatabase db;

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

        final FloatingActionButton navigate_button = findViewById(R.id.task_details_activity_button_navigate);

        // Get the id of the task selected (clicked)
        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        Log.d(TAG, "taskId = " + taskId);
        if (taskId == -1) return;

        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            // DB
            db = MySingleton.getInstance(getApplicationContext()).getDb();
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
                // If location is not specified, then ...
                if (task.getLocation().isEmpty()) {
                    // Hide the navigate button
                    navigate_button.setVisibility(View.GONE);
                    location_text.setText((CharSequence) "(not specified)");
                } else {
                    location_text.setText(task.getLocation());
                }
                status_text.setText(status);
            });
        }).start();

        // Navigate to Location Button Listener
        navigate_button.setOnClickListener((v) -> {
            Log.d(TAG, "Pressed the navigate button");

            // Specified location
            String location_str = location_text.getText().toString().trim();

            // Show Location on Google Maps
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri location_uri = Uri.parse("geo:0,0?q="+location_str);
            intent.setData(location_uri);
            startActivity(intent);
        }); // End of navigate_button.setOnClickListener(...)

        // Mark as Completed Button Listener
        Button mark_as_completed = findViewById(R.id.task_details_activity_mark_as_completed);
        mark_as_completed.setOnClickListener((v) -> {
            Log.i(TAG, "Mark as completed button pressed !");
            new Thread(() -> {
                // DB
                db = MySingleton.getInstance(getApplicationContext()).getDb();
                // DAO's
                TaskDao taskDao = db.taskDao();
                StatusDao statusDao = db.statusDao();

                long newStatusId = statusDao.getStatus("COMPLETED").getId();
                taskDao.updateTaskStatus(taskId, newStatusId);

                handler.post(() -> {
                    // Hide the completed button because there is no reason to press it again
                    mark_as_completed.setVisibility(View.GONE);
                    Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
                });
            }).start();
        }); // End of mark_as_completed.setOnClickListener(...)

    }

    @Override
    protected void onResume(){
        super.onResume();
        /* TODO: Make onSaveConfigurationChange to not let the button appear again after it got hidden
        *        and make onResume() change the status text to Completed */
        Log.d(TAG, "on-resume()...");
        // put your code here...
    }
}