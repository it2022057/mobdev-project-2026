package hua.dit.mobdev_project_2026.bg;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hua.dit.mobdev_project_2026.MySingleton;
import hua.dit.mobdev_project_2026.R;
import hua.dit.mobdev_project_2026.TaskDetailsActivity;
import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.StatusDao;
import hua.dit.mobdev_project_2026.db.Task;
import hua.dit.mobdev_project_2026.db.TaskDao;
import hua.dit.mobdev_project_2026.receiver.NotificationReceiver;

public class NotificationsWorker extends Worker {

    private static final String TAG = "NotificationsWorker";

    public NotificationsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // A task that should be executed in the background - no UI update is necessary

        Log.d(TAG, "doWork() started at " + new Date());

        // Access the Room database through a Singleton to avoid creating multiple DB instances
        AppDatabase db = MySingleton.getInstance(getApplicationContext()).getDb();
        // DAO's
        TaskDao taskDao = db.taskDao();
        StatusDao statusDao = db.statusDao();

        // Get the time right now
        long now = System.currentTimeMillis();
        // Next 30 minutes
        long windowEnd = now + 30L * 60L * 1000L;
        // Preload RECORDED ID
        long recordedId = statusDao.getStatus("RECORDED").getId();

        // Gets all the tasks with status = RECORDED
        List<Task> tasks = taskDao.getTasksByStatus(recordedId);

        for (Task task : tasks) {
            if (task.isNotified()) continue;

            // start = start time of the task (stored as Date in the entity)
            long start = getStartTimeMillis(task.getStartTime(), task.getDateValue());

            // In case the periodic work that updates the task's statuses did not already run
            if (now >= start) continue;

            if (start <= windowEnd) {
                int taskId = Math.toIntExact(task.getId());

                // Create a dismiss intent for the action button
                Intent dismissIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
                dismissIntent.setAction("Dismiss");
                dismissIntent.putExtra("TASK_ID", taskId);
                PendingIntent dismissPendingIntent =
                        PendingIntent.getBroadcast(getApplicationContext(), taskId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
                intent.putExtra("TASK_ID", taskId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Channel-1")
                        .setSmallIcon(R.drawable.ic_task_notification)
                        .setContentTitle("Reminder")
                        .setContentText("Task '" + task.getShortName() + "'" + " is about to begin")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that fires when the user taps the notification.
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_expired, "Dismiss", dismissPendingIntent);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return Result.failure();
                }
                NotificationManagerCompat.from(getApplicationContext()).notify(taskId, builder.build());

                // Mark as notified so it won't repeat (avoid spam)
                task.setNotified(true);
            }
        }
        return Result.success();
    }

    /**
     * Combines a Date (day) and a Time (hour:minute) into
     * a single timestamp in milliseconds
     * <p>
     * Returns: full timestamp (date + time) in milliseconds
     * </p>
     */
    private long getStartTimeMillis(Time start_time, Date date) {
        Calendar cal = Calendar.getInstance();

        // Set the task date (year, month, day)
        cal.setTime(date);

        // Extract hour/minute from the start time
        Calendar t = Calendar.getInstance();
        t.setTime(start_time);

        cal.set(Calendar.HOUR_OF_DAY, t.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, t.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Now it is a real “today” timestamp (e.g. 2026-09-01 19:38)
        // and we can get the milliseconds since 1970
        return cal.getTimeInMillis();
    }
}
