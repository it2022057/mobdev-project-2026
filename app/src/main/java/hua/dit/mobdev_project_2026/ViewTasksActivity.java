package hua.dit.mobdev_project_2026;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ViewTasksActivity extends AppCompatActivity {

    private static final String TAG = "ViewTasksActivity";

    private static final int REQUEST_CODE = 123;


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
        ImageButton download_button = findViewById(R.id.view_tasks_download_button);
        download_button.setOnClickListener((v) -> {
            Log.d(TAG, "Download button pressed");

            // Create a Text File in a public Folder (Share it with other Apps) - part 1
            // Create an Intent
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_TITLE, "tasks.html");
            startActivityForResult(intent, REQUEST_CODE);
        }); // End of download_button.setOnClickListener(...)

        // New Task Button Listener
        ImageButton new_task_button = findViewById(R.id.view_tasks_add_button);
        new_task_button.setOnClickListener((v) -> {
            Log.d(TAG, "New task button pressed");
            Intent intent2 = new Intent(ViewTasksActivity.this, NewTaskActivity.class);
            startActivity(intent2);
            Log.i(TAG, "Going to a new page to add a new task");
        }); // End of new_task_button.setOnClickListener(...)
    }

    // Create a Text File in a public Folder (Share it with other Apps) - part 2
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(resultCode, resultCode, resultData);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            if (resultData != null) {
                final Uri uri = resultData.getData();
                // Perform operations on the document using its URI.

                new Thread(() -> {
                    /* TODO: Write the task data and not simple plain text messages*/
                    try {
                        final String txt_msg = "This is ANOTHER simple message\nstored in the file created";
                        OutputStream os = getContentResolver().openOutputStream(uri);
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                        bw.write(txt_msg);
                        bw.flush();
                        bw.close();
                        Log.i(TAG, "File successfully created !");
                    } catch (Throwable t) {
                        throw new RuntimeException("File processing problem", t);
                    }
                }).start();
            }
        }
    } // END OF onActivityResult(..)

}