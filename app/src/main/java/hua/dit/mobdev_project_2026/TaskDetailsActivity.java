package hua.dit.mobdev_project_2026;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Executor;

import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.MyConverters;
import hua.dit.mobdev_project_2026.db.StatusDao;
import hua.dit.mobdev_project_2026.db.Task;
import hua.dit.mobdev_project_2026.db.TaskDao;
import hua.dit.mobdev_project_2026.dialog.MarkAsCompletedDialog;

public class TaskDetailsActivity extends AppCompatActivity
                                 implements MarkAsCompletedDialog.MarkAsCompletedDialogListener {

    private static final String TAG = "TaskDetailsActivity";

    private static final String KEY_HIDDEN = "KEY_HIDDEN";

    private AppDatabase db;

    private TaskDao taskDao;

    private StatusDao statusDao;

    private boolean isMarkAsCompletedHidden = false;

    long taskId;

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

        // Get the application-wide singleton instance
        final MySingleton mySingleton = MySingleton.getInstance(getApplicationContext());
        // Executor used to run tasks off the UI thread
        final Executor executor = mySingleton.getExecutorService();
        // Handler used to safely post UI updates from background threads
        final Handler handler = mySingleton.getHandler();

        // Find views of all the task details
        final TextView id_text = findViewById(R.id.task_details_activity_id);
        final TextView short_name_text = findViewById(R.id.task_details_activity_name);
        final TextView brief_description_text = findViewById(R.id.task_details_activity_description);
        final TextView difficulty_text = findViewById(R.id.task_details_activity_difficulty);
        final TextView date_text = findViewById(R.id.task_details_activity_date);
        final TextView start_time_text = findViewById(R.id.task_details_activity_start_time);
        final TextView duration_text = findViewById(R.id.task_details_activity_duration);
        final TextView location_text = findViewById(R.id.task_details_activity_location);
        final TextView status_text = findViewById(R.id.task_details_activity_status);

        // Navigate and mark as completed buttons (with icons)
        FloatingActionButton navigate_button = findViewById(R.id.task_details_activity_button_navigate);
        FloatingActionButton mark_as_completed_button = findViewById(R.id.task_details_activity_mark_as_completed);

        // Get the id of the task selected (clicked)
        taskId = getIntent().getIntExtra("TASK_ID", -1);
        Log.d(TAG, "taskId = " + taskId);
        if (taskId == -1) return;

        // I want the mark_as_completed_button to stay hidden after a configuration change (rotation / theme change), instead of reappearing
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_HIDDEN)) {
            isMarkAsCompletedHidden = savedInstanceState.getBoolean(KEY_HIDDEN, false);
            Log.d(TAG, "LOADED if mark as Completed is hidden !");
        }
        // If it was hidden before the configuration change, hide it again
        if (isMarkAsCompletedHidden) {
            mark_as_completed_button.setVisibility(View.GONE);
        }

        // Load task details from DB in background thread
        executor.execute(() -> {
            // DB
            db = mySingleton.getDb();
            // DAOs
            taskDao = db.taskDao();
            statusDao = db.statusDao();

            Task task = taskDao.getTaskById(taskId);
            String status = statusDao.getStatusNameById(task.getStatusId());

            // Update UI on main thread
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
                    location_text.setText("(not specified)");
                } else {
                    location_text.setText(task.getLocation());
                }
                status_text.setText(status);
            });
        });

        // Open location in Google Maps Button Listener
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

        // Mark task as COMPLETED Button Listener
        mark_as_completed_button.setOnClickListener((v) -> {
            Log.i(TAG, "Mark as completed button pressed !");

            new MarkAsCompletedDialog().show(getSupportFragmentManager(), "MarkAsCompletedDialog");
        }); // End of mark_as_completed.setOnClickListener(...)

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Persist mark as completed button's hidden state
        outState.putBoolean(KEY_HIDDEN, isMarkAsCompletedHidden);
        Log.d(TAG, "Activity state (isMarkAsCompletedHidden) saved ...");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following
    // methods defined by the NoticeDialogFragment.NoticeDialogListener
    // interface.
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Get the application-wide singleton instance
        MySingleton mySingleton = MySingleton.getInstance(getApplicationContext());

        TextView status_text = findViewById(R.id.task_details_activity_status);
        FloatingActionButton mark_as_completed_button = findViewById(R.id.task_details_activity_mark_as_completed);

        // User presses YES
        mySingleton.getExecutorService().execute(() -> {
            // DAOs
            taskDao = db.taskDao();
            statusDao = db.statusDao();

            String newStatusName = "COMPLETED";
            long newStatusId = statusDao.getStatus(newStatusName).getId();
            taskDao.updateTaskStatus(taskId, newStatusId);

            mySingleton.getHandler().post(() -> {
                // Hide the completed button because there is no reason to press it again
                mark_as_completed_button.setVisibility(View.GONE);
                isMarkAsCompletedHidden = true;
                status_text.setText(newStatusName);
                Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User presses NO
        if (dialog.getDialog() != null) {
            dialog.getDialog().cancel();
        }
    }
}