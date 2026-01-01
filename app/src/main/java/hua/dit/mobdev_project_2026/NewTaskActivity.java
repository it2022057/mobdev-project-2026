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

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.Task;
import hua.dit.mobdev_project_2026.db.TaskDao;

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
            /* TODO: 1. Save the new task in the database
                     2. Make a Toast.LENGTH_SHORT for successful save*/
//            new Thread(()->{
//                Log.d(TAG, "New task save button pressed");
//
//                // DB
//                AppDatabase db = MySingleton.getInstance(getApplicationContext()).getDb();
//                TaskDao taskDao = db.taskDao();
//                // INSERT
//
//                Log.i(TAG, "Data successfully Stored !");
//                Log.i(TAG, "Data successfully Retrieved ! - " + myTableObjlist.size());
//                Log.i(TAG, "List:: " + myTableObjlist);
//
//                // Note: The above code cannot be executed in main thread !
//
//            }).start();
            Intent intent = new Intent(NewTaskActivity.this, ViewTasksActivity.class);
            startActivity(intent);
            Log.i(TAG, "Back to view tasks page");
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
}