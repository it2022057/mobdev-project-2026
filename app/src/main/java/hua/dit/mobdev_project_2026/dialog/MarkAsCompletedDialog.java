package hua.dit.mobdev_project_2026.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import hua.dit.mobdev_project_2026.R;

public class MarkAsCompletedDialog extends DialogFragment {

    private static final String TAG = "MarkAsCompletedDialog";

    // The activity that creates an instance of this dialog fragment must
    // implement this interface to receive event callbacks. Each method passes
    // the DialogFragment in case the host needs to query it
    public interface MarkAsCompletedDialogListener  {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    MarkAsCompletedDialogListener  listener;

    // Override the Fragment.onAttach() method to instantiate the MarkAsCompletedDialogListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MarkAsCompletedDialogListener so you can send events to the host
            listener = (MarkAsCompletedDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, so throw exception
            throw new ClassCastException(context + " must implement MarkAsCompletedDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Convert XML to View Java Object
        View view = getLayoutInflater().inflate(R.layout.mark_as_completed_dialog_layout, null);
        // Create Alert Dialog using AlertDialog Builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .setPositiveButton(R.string.mark_as_completed_dialog_button_yes, (d,v)->{
                    Log.d(TAG, "YES");

                    // Send the positive button event back to the host activity.
                    listener.onDialogPositiveClick(MarkAsCompletedDialog.this);
                })
                .setNegativeButton(R.string.mark_as_completed_dialog_button_no, (d,v)->{
                    Log.d(TAG, "NO");

                    // Send the negative button event back to the host activity.
                    listener.onDialogNegativeClick(MarkAsCompletedDialog.this);
                });

        return builder.create();
    }

}
