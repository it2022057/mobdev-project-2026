package hua.dit.mobdev_project_2026;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "TimePickerDialogFrag";
    public static final String RESULT_KEY = "start_time_result";
    public static final String HOUR_KEY = "hour";
    public static final String MINUTE_KEY = "minute";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it.
        TimePickerDialog.OnTimeSetListener listener = this;
        boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());
        return new TimePickerDialog(getActivity(), listener, hour, minute, is24HourFormat);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Log.i(TAG, "Selected Time: hourOfDay= " + hourOfDay + ":" + minute);

        // Package result in a Bundle
        Bundle result = new Bundle();
        result.putInt(HOUR_KEY, hourOfDay);
        result.putInt(MINUTE_KEY, minute);

        // Send result back to NewTaskActivity
        getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
    }
}
