package hua.dit.mobdev_project_2026;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.MyConverters;
import hua.dit.mobdev_project_2026.db.TaskDao;
import hua.dit.mobdev_project_2026.db.TaskWithStatus;
import hua.dit.mobdev_project_2026.list.MyTaskAdapter;

public class ViewTasksActivity extends AppCompatActivity {

    private static final String TAG = "ViewTasksActivity";

    private static final int REQUEST_CODE = 123;

    private AppDatabase db;
    private MyTaskAdapter myTaskAdapter;

    private MySingleton mySingleton;
    private Executor executor;
    private Handler handler;

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

        // Get the application-wide singleton instance
        mySingleton = MySingleton.getInstance(getApplicationContext());
        // Executor used to run tasks off the UI thread
        executor = mySingleton.getExecutorService();
        // Handler used to safely post UI updates from background threads
        handler = mySingleton.getHandler();

        // Create a custom adapter for the recycler view, with a callback every time the user clicks one task
        // First we pass an empty list to set the adapter immediately and avoid Log warnings
        myTaskAdapter = new MyTaskAdapter(new ArrayList<>(), taskId -> {
            Intent intent = new Intent(ViewTasksActivity.this, TaskDetailsActivity.class);
            intent.putExtra("TASK_ID", taskId);
            startActivity(intent);
            Log.i(TAG, "Callback activated...Going to view the selected task with id: " + taskId);
        });

        // Prepare Recycler View
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Attach custom adapter to the recycler view
        recyclerView.setAdapter(myTaskAdapter);

        // DB work in background thread
        executor.execute(() -> {
            // DB
            db = mySingleton.getDb();
            // DAO
            TaskDao taskDao = db.taskDao();

            // Gets all the non completed tasks with the appropriate order,
            // so that the «urgent» tasks (e.g., tasks «expired») appear on the top of their screen
            List<TaskWithStatus> nonCompletedTasks = taskDao.getNonCompletedTasks();
            Log.i(TAG, "NonCompletedTasks size = " + nonCompletedTasks.size());

            // This change should be made by the MAIN thread
            handler.post(() -> {
                myTaskAdapter.setTaskList(nonCompletedTasks);

                if (nonCompletedTasks.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No Urgent Tasks !", Toast.LENGTH_LONG).show();
                }
            });
        });

        // Export Tasks Button Listener
        ImageButton download_button = findViewById(R.id.view_tasks_download_button);
        download_button.setOnClickListener((v) -> {
            Log.d(TAG, "Download button pressed");

            // Create a Text File in a public Folder (Share it with other Apps) - part 1
            // Create an Intent
            Intent intent2 = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent2.addCategory(Intent.CATEGORY_OPENABLE);
            // HTML file (not plaintext)
            intent2.setType("text/html");
            // Add a title
            intent2.putExtra(Intent.EXTRA_TITLE, "tasks.html");
            startActivityForResult(intent2, REQUEST_CODE);
        }); // End of download_button.setOnClickListener(...)

        // New Task Button Listener
        ImageButton new_task_button = findViewById(R.id.view_tasks_add_button);
        new_task_button.setOnClickListener((v) -> {
            Log.d(TAG, "New task button pressed");
            Intent intent3 = new Intent(ViewTasksActivity.this, NewTaskActivity.class);
            startActivity(intent3);
            Log.i(TAG, "Going to a new page to add a new task");
        }); // End of new_task_button.setOnClickListener(...)
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "on-resume()...");

        executor.execute(() -> {
            // Gets all the non completed tasks again, so that the UI reloads
            List<TaskWithStatus> nonCompletedTasks = mySingleton.getDb().taskDao().getNonCompletedTasks();

            // This change should be made by the MAIN thread
            handler.post(() -> {
                // When we go back from the task details page, where we could mark the task as completed,
                // this activity calls onResume(), so we need to reload the UI to not show the completed task
                myTaskAdapter.setTaskList(nonCompletedTasks);
                myTaskAdapter.notifyDataSetChanged(); // used as a last resort...didn't know another efficient way to reload the UI
            });
        });
    }

    // Create a Text File in a public Folder (Share it with other Apps) - part 2
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(resultCode, resultCode, resultData);

        // Check that:
        // 1) This result corresponds to our request
        // 2) The user completed the action successfully
        // 3) We actually received data back
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null) {

            // The result data contains a URI for the document or directory that the user selected
            final Uri uri = resultData.getData();

            // Safety check: if for some reason URI is null, abort
            if (uri == null) return;

            // Create the base HTML content (doctype, head, styles, etc.)
            StringBuilder htmlData = getStringBuilder();

            // Perform operations on the document using its URI
            // Run database + file I/O work on a background thread
            executor.execute(() -> {
                // DB
                AppDatabase db = mySingleton.getDb();
                // DAO
                TaskDao taskDao = db.taskDao();
                // Query all NON-completed tasks using a Cursor
                Cursor c = taskDao.getNonCompletedTasksCursor();
                // Date formatter for better date output in the file (not long but string->dd/MM/yyyy HH:mm)
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                // Get column indices once
                int id_index = c.getColumnIndexOrThrow("id");
                int short_name_index = c.getColumnIndexOrThrow("short_name");
                int brief_description_index = c.getColumnIndexOrThrow("description");
                int difficulty_index = c.getColumnIndexOrThrow("difficulty");
                int date_index = c.getColumnIndexOrThrow("date");
                int start_time_index = c.getColumnIndexOrThrow("start_time");
                int duration_index = c.getColumnIndexOrThrow("duration");
                int location_index = c.getColumnIndexOrThrow("location");
                int status_index = c.getColumnIndexOrThrow("status");

                // Iterate over all rows of each task
                while (c.moveToNext()) {

                    // Read each column value from the current row
                    String id = c.getString(id_index);
                    String short_name = c.getString(short_name_index);
                    String brief_description = c.getString(brief_description_index);
                    String difficulty = c.getString(difficulty_index);
                    // Convert stored timestamp (long) into Date
                    long createdAtMillis = c.getLong(date_index);
                    Date created_at = new MyConverters().longToDate(createdAtMillis);
                    // and now make it human-readable
                    String date = sdf.format(created_at);
                    String start_time = c.getString(start_time_index);
                    String duration = c.getString(duration_index);
                    // Location may not be specified so avoid "null" in HTML
                    String location = c.getString(location_index).isEmpty() ? "(not specified)" : c.getString(location_index);
                    String status = c.getString(status_index);

                    // Append a table row (<tr>) with all task fields
                    htmlData.append("<tr>")
                            .append("<td>").append(id).append("</td>")
                            .append("<td>").append(short_name).append("</td>")
                            .append("<td>").append(brief_description).append("</td>")
                            .append("<td>").append(difficulty).append("</td>")
                            .append("<td>").append(date).append("</td>")
                            .append("<td>").append(start_time).append("</td>")
                            .append("<td>").append(duration).append("</td>")
                            .append("<td>").append(location).append("</td>")
                            .append("<td>").append(status).append("</td>")
                            .append("</tr>");
                }
                // Close the cursor to free resources
                c.close();
                // Close table, body, and HTML tags
                htmlData.append("\n\t</tbody>\n    </table>\n</body>\n</html>");

                try {
                    OutputStream os = getContentResolver().openOutputStream(uri);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                    // Write full HTML content to file
                    bw.write(htmlData.toString());
                    bw.flush();
                    bw.close();
                    Log.i(TAG, "File successfully created !");
                } catch (Throwable t) {
                    throw new RuntimeException("File processing problem", t);
                }
            });
        }
    } // END OF onActivityResult(..)

    // This method returns a StringBuilder with the opening HTML structure
    private static StringBuilder getStringBuilder() {
        String htmlContent =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "   <meta charset=\"UTF-8\">\n" +
                        "   <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "   <title>Non Completed Tasks</title>\n" +
                        "   <style>\n" +
                        "       body { font-family: Arial, sans-serif; padding: 16px; }\n" +
                        "       table { border-collapse: collapse; width: 100%; border: 2px solid #000; }\n" +
                        "       th, td { border: 2px solid #000; padding: 10px; text-align: left; }\n" +
                        "       th { background-color: #f5f5f5; }\n" +
                        "   </style>\n" +
                        "</head>\n\n" +
                        "<body>\n" +
                        "   <h1 align=\"center\">Non Completed Tasks</h1>\n" +
                        "   <table>\n" +
                        "       <thead>\n" +
                        "           <tr>\n" +
                        "               <th>ID</th>\n" +
                        "               <th>SHORT_NAME</th>\n" +
                        "               <th>DESCRIPTION</th>\n" +
                        "               <th>DIFFICULTY</th>\n" +
                        "               <th>CREATED_AT</th>\n" +
                        "               <th>START_TIME</th>\n" +
                        "               <th>DURATION</th>\n" +
                        "               <th>LOCATION</th>\n" +
                        "               <th>STATUS</th>\n" +
                        "           </tr>\n" +
                        "       </thead>\n\n" +
                        "       <tbody>\n";

        return new StringBuilder(htmlContent);
    }

}