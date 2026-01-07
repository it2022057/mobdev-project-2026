package hua.dit.mobdev_project_2026.bg;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
        Log.d(TAG, "doWork() started at " + new Date());

        // A task that should be executed in the background - no UI update is necessary
        AppDatabase db = MySingleton.getInstance(getApplicationContext()).getDb();
        TaskDao taskDao = db.taskDao();
        StatusDao statusDao = db.statusDao();

        // 1) Calculate "start of today" (midnight) -> delete older tasks
        long startOfToday = getStartOfToday();

        // Delete tasks from previous days (adjust column name!)
        int totalDeleted = taskDao.deleteTasksBeforeToday(startOfToday);
        Log.d(TAG, "Deleted " + totalDeleted + " previous-day tasks !");

        // 2) Load all the today's tasks (yesterday's tasks were deleted)
        List<Task> tasks = taskDao.getAllTasks();

        // Preload status IDs (avoid repeated queries)
        long recordedId = statusDao.getStatus("RECORDED").getId();
        long inProgressId = statusDao.getStatus("IN_PROGRESS").getId();
        long expiredId = statusDao.getStatus("EXPIRED").getId();
        long completedId = statusDao.getStatus("COMPLETED").getId();

        // Get the time right now
        long now = System.currentTimeMillis();

        for (Task task : tasks) {
            // Do not change the status of Completed tasks
            if (task.getStatusId() == completedId) continue;

            long start = task.getStartTime().getTime();
            long end = start + task.getDuration() * 60L * 60L * 1000L; // hours to milliseconds

            long newStatusId;
            if (now > end) {
                newStatusId = expiredId;        // ended already -> expired
            } else if (now >= start) {
                newStatusId = inProgressId;     // started but not ended -> in progress
            } else {
                newStatusId = recordedId;       // not started yet -> recorded
            }

            if (task.getStatusId() != newStatusId) {
                taskDao.updateTaskStatus(task.getId(), newStatusId);
            }
        }

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

}
