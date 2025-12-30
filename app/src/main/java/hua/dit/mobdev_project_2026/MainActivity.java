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
        Log.d(TAG, "on-destroy()...Closing the db !");
        MySingleton.getInstance(getApplicationContext()).close();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Changed state");
    }
}