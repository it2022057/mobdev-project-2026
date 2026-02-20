package hua.dit.mobdev_project_2026.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        long taskId = intent.getLongExtra("TASK_ID", -1);

        Log.d(TAG, "onReceive - Notification with id " + taskId + " is dismissed");

        NotificationManagerCompat.from(context).cancel((int) taskId);
    }
}
