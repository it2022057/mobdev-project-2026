package hua.dit.mobdev_project_2026;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.MyConverters;
import hua.dit.mobdev_project_2026.db.StatusDao;
import hua.dit.mobdev_project_2026.db.Task;
import hua.dit.mobdev_project_2026.db.TaskDao;
import hua.dit.mobdev_project_2026.db.TaskWithStatus;

public class NewTaskActivity extends AppCompatActivity {

    private static final String TAG = "NewTaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_task), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG, "on-create()...");

        // All the input provided by the user in order to create a new task
        final TextInputEditText short_name_input = findViewById(R.id.short_name_input);
        final TextInputEditText brief_description_input = findViewById(R.id.brief_description_input);
        final TextInputEditText difficulty_input = findViewById(R.id.difficulty_input);
        final TextInputEditText start_time_input = findViewById(R.id.start_time_input);
        final TextInputEditText duration_input = findViewById(R.id.duration_input);
        final TextInputEditText location_input = findViewById(R.id.location_input);

        // Start time Button Listener (TimePicker pop_up)
        start_time_input.setOnClickListener((v) -> {
            // Show TimePickerDialog and Handle "OK" (time set) button
            new TimePickerDialogFragment().show(getSupportFragmentManager(), "Start Time");
            // Listen for the result and update the UI
            getSupportFragmentManager().setFragmentResultListener(
                    TimePickerDialogFragment.RESULT_KEY,
                    this,
                    (requestKey, bundle) -> {
                        int hour = bundle.getInt(TimePickerDialogFragment.HOUR_KEY);
                        int minute = bundle.getInt(TimePickerDialogFragment.MINUTE_KEY);

                        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                        start_time_input.setText(time);
                    }
            );
        }); // End of start_time_input.setOnClickListener(...)

        // Save new task Button Listener
        Button save_button = findViewById(R.id.new_task_activity_save_button);
        save_button.setOnClickListener((v) -> {
            Log.d(TAG, "New task save button pressed");

            boolean ok = true;

            // Task values
            String short_name = getTextSafe(short_name_input);
            String brief_description = getTextSafe(brief_description_input);
            int difficulty = getIntSafe(difficulty_input, 10);
            String start_time = getTextSafe(start_time_input);
            int duration = getIntSafe(duration_input, 24);
            String location = getTextSafe(location_input);

        /* Start of validation check... */

            // Short name validation check
            if (short_name.isEmpty()) {
                // Do not let the UI go back in the view tasks page
                ok = false;
                // Show toast message on screen
                Toast.makeText(getApplicationContext(), "Short name NOT specified yet !", Toast.LENGTH_SHORT).show();
            }
            // Brief description validation check
            if (brief_description.isEmpty()) {
                ok = false;
                Toast.makeText(getApplicationContext(), "Name NOT specified yet !", Toast.LENGTH_SHORT).show();
            }
            // Difficulty validation check
            // Empty input
            if (difficulty == -1) {
                ok = false;
                Toast.makeText(getApplicationContext(), "Difficulty input INVALID !", Toast.LENGTH_SHORT).show();
                // Exceeded value 10
            } else if (difficulty == -3) {
                ok = false;
                Toast.makeText(getApplicationContext(), "Difficulty must not be > 10 !", Toast.LENGTH_SHORT).show();
            }
            // Start time validation check
            if (start_time.isEmpty()) {
                ok = false;
                Toast.makeText(getApplicationContext(), "Start time NOT specified yet !", Toast.LENGTH_SHORT).show();
            }
            // Duration validation check
            switch (duration) {
                // Empty input
                case -1:
                    ok = false;
                    Toast.makeText(getApplicationContext(), "Duration input INVALID !", Toast.LENGTH_SHORT).show();
                    break;
                // Not positive
                case -2:
                    ok = false;
                    Toast.makeText(getApplicationContext(), "Duration NOT positive !", Toast.LENGTH_SHORT).show();
                    break;
                // Exceeded value 24 (24 hours -> 1 day)
                case -3:
                    ok = false;
                    Toast.makeText(getApplicationContext(), "Duration should not be > 24 (one-day) !", Toast.LENGTH_SHORT).show();
                    break;
            }
        /* ...End of validation check */

            // If there was at least one error, do not lose time and recourses to open the DB
            if (!ok) {
                Log.i(TAG, "Failed to insert the new task in the DB");
                return;
            }

            // Dictate UI thread to update UI using a Handler
            Handler handler = new Handler(Looper.getMainLooper());

            // DB work in background thread
            new Thread(() -> {
                // DB
                AppDatabase db = MySingleton.getInstance(getApplicationContext()).getDb();
                // DAOs
                TaskDao taskDao = db.taskDao();
                StatusDao statusDao = db.statusDao();

                // Default status for a newly created Task is "RECORDED".
                long status_id = statusDao.getStatus("RECORDED").getId();
                // Creates a new task object to insert later on in the DB
                Task task = new Task(short_name, brief_description, difficulty, new Date(), new MyConverters().stringToTime(start_time), duration, status_id, location);

                // INSERT
                long task_id = taskDao.insertTask(task);
                Log.i(TAG, "Data successfully Stored !");
                Log.i(TAG, "New Task ID: " + task_id);
                // Show ALL ...
                List<Task> taskList = taskDao.getAllTasks();
                Log.i(TAG, "Data successfully Retrieved ! --> Size: " + taskList.size() + " :: " + taskList);
                List<TaskWithStatus> taskWithStatuses = taskDao.getTaskWithStatusList();
                Log.i(TAG, taskWithStatuses.size() + " :: " + taskWithStatuses);

                // This change should be made by the MAIN thread
                handler.post(() -> {
                    Toast.makeText(getApplicationContext(), "Task with id: " + task_id + ", saved successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewTaskActivity.this, ViewTasksActivity.class);
                    startActivity(intent);
                    Log.i(TAG, "Back to view tasks page");
                });
            }).start();
        }); // End of save_button.setOnClickListener(...)

        // Cancel Button Listener
        Button cancel_button = findViewById(R.id.new_task_activity_cancel_button);
        cancel_button.setOnClickListener((v) -> {
            Log.d(TAG, "New task cancel button pressed");
            Intent intent2 = new Intent(NewTaskActivity.this, ViewTasksActivity.class);
            startActivity(intent2);
            Log.i(TAG, "Back to view tasks page");
        }); // End of cancel_button.setOnClickListener(...)
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "on-destroy()...Closing the db !");
        MySingleton.getInstance(getApplicationContext()).close();
        super.onDestroy();
    }


    /**
     * Ensures the text from TextInputEditText is safe.
     * <p>
     * Rules:
     * <p> - Empty / null → invalid
     *
     * @return a text safe string, <p> or an empty one
     */
    private String getTextSafe(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    /**
     * Normalizes a positive integer from TextInputEditText.
     * <p>
     * Rules:
     * <p> - Empty / null → invalid
     * <p> - Must be >= 1
     * <p> - Removes leading zeros
     * <p> - Optional max value
     *
     * @return normalized int if valid, <p> or -1 if empty||exception, <p> or -2 if not positive <p> or -3 if it exceeds the max value
     */
    private int getIntSafe(TextInputEditText input, int maxValue) {
        String raw = getTextSafe(input);
        if (raw.isEmpty()) return -1;

        int value;
        try {
            value = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return -1;
        }

        // Only check for positive integer in the Duration input, because Difficulty input can be 0
        if (Objects.requireNonNull(input.getHint()).toString().trim().equals("Duration")) {
            if (value <= 0) return -2;
        }
        if (maxValue > 0 && value > maxValue) return -3;

        return value;
    }

}