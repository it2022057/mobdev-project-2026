package hua.dit.mobdev_project_2026.dialog;

import android.Manifest;
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

public class RationaleDialog extends DialogFragment {
    private static final String TAG = "RationaleDialog";

    // The activity that creates an instance of this dialog fragment must
    // implement this interface to receive event callbacks. Each method passes
    // the DialogFragment in case the host needs to query it
    public interface RationaleDialogListener  {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNeutralClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    RationaleDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the RationaleDialogListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RationaleDialogListener so you can send events to the host
            listener = (RationaleDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, so throw exception
            throw new ClassCastException(context + " must implement RationaleDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create Alert Dialog using AlertDialog Builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.rationale_dialog_title)
                .setMessage(R.string.rationale_dialog_message)
                .setPositiveButton(R.string.rationale_dialog_button_enable, (d, w) -> {
                    Log.d(TAG, "Enable");

                    // Send the positive button event back to the host activity.
                    listener.onDialogPositiveClick(RationaleDialog.this);
                })
                .setNeutralButton(R.string.rationale_dialog_button_no_thanks, (d, w) -> {
                    Log.d(TAG, "No thanks");

                    // Send the neutral button event back to the host activity.
                    listener.onDialogNeutralClick(RationaleDialog.this);
                })
                .setNegativeButton(R.string.rationale_dialog_button_do_not_ask_again, (d,v)->{
                    Log.d(TAG, "Don't ask again");

                    // Send the negative button event back to the host activity.
                    listener.onDialogNegativeClick(RationaleDialog.this);
                });

        return builder.create();
    }
}
