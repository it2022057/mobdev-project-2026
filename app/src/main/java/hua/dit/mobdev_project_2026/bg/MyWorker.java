package hua.dit.mobdev_project_2026.bg;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hua.dit.mobdev_project_2026.MySingleton;
import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.StatusDao;
import hua.dit.mobdev_project_2026.db.Task;
import hua.dit.mobdev_project_2026.db.TaskDao;

public class MyWorker extends Worker {

    private static final String TAG = "MyWorker";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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

        // 1) Calculate "start of today" (midnight)
        long startOfToday = getStartOfToday();

        // Cleanup: delete tasks from previous days
        int totalDeleted = taskDao.deleteTasksBeforeToday(startOfToday);
        Log.d(TAG, "Deleted " + totalDeleted + " previous-day tasks !");

        // 2) Load today's tasks after cleanup
        List<Task> tasks = taskDao.getAllTasks();

        // Preload status IDs
        long recordedId = statusDao.getStatus("RECORDED").getId();
        long inProgressId = statusDao.getStatus("IN_PROGRESS").getId();
        long expiredId = statusDao.getStatus("EXPIRED").getId();
        long completedId = statusDao.getStatus("COMPLETED").getId();

        // Get the time right now
        long now = System.currentTimeMillis();

        // Iterate over all tasks and update their status based on their start time and duration
        for (Task task : tasks) {
            // Do not change the status of Completed tasks
            if (task.getStatusId() == completedId) continue;

            // start = start time of the task (stored as Date in the entity)
            long start = getStartTimeMillis(task.getStartTime(), task.getDateValue());
            // end = start time + duration (duration is in hours, so convert to milliseconds)
            long end = start + task.getDuration() * 60L * 60L * 1000L; // hours to milliseconds

            long newStatusId;
            // Determine the new status according to current time
            if (now > end) {
                newStatusId = expiredId;        // ended already -> expired
            } else if (now >= start) {
                newStatusId = inProgressId;     // started but not ended -> in progress
            } else {
                newStatusId = recordedId;       // not started yet -> recorded
            }

            // Reduce unnecessary writes and only update the DB if the status actually changes
            if (task.getStatusId() != newStatusId) {
                taskDao.updateTaskStatus(task.getId(), newStatusId);
            }
        }
        // Worker executed successfully
        return Result.success();
    }

    /**
     * Gets the start of the day (e.g 2026-01-07 00:00:00)
     * <p>
     * Returns: a time value of a Date's object in milliseconds
     */
    private long getStartOfToday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
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
