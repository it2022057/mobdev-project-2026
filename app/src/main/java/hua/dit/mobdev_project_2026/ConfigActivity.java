package hua.dit.mobdev_project_2026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = "ConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.config), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Widgets
        final EditText default_duration = findViewById(R.id.config_activity_duration_input);
        final EditText default_difficulty = findViewById(R.id.config_activity_difficulty_input);

        // Update Widgets with default duration & difficulty (if any) from Shared Preferences
        final SharedPreferences sp = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        // If values are not set yet (-1), keep the EditTexts empty ("")
        default_duration.setText(sp.getInt("default_duration", -1) == -1 ? "" : String.valueOf(sp.getInt("default_duration", -1)));
        default_difficulty.setText(sp.getInt("default_difficulty", -1) == -1 ? "" : String.valueOf(sp.getInt("default_difficulty", -1)));

        // Save Button Listener
        final Button save_config_button = findViewById(R.id.config_activity_button);
        save_config_button.setOnClickListener((v) -> {
            Log.d(TAG, "Pressed Save");
            String tmp = default_duration.getText().toString().trim(); // value of Duration EditText

            // Get new Duration
            int new_default_duration = tmp.isEmpty() ? -1 : Integer.parseInt(tmp); // if the user didn't type anything put a non valid value (-1)

            tmp = default_difficulty.getText().toString().trim(); // value of Difficulty EditText

            // Get new Difficulty
            int new_default_difficulty = tmp.isEmpty() ? -1 : Integer.parseInt(tmp); // if the user didn't type anything put a non valid value (-1)

            // Store new Duration and Difficulty in Shared Preferences
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("default_duration", new_default_duration);
            editor.putInt("default_difficulty", new_default_difficulty);
            editor.apply();
            Log.d(TAG, "Updated the Shared Preferences of the app");

            // Go back
            Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
            startActivity(intent);
            Log.i(TAG, "Going back to MainActivity");
        }); // End of save_config_button.setOnClickListener(..)
    }
}