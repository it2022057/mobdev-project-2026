package hua.dit.mobdev_project_2026.bg;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import hua.dit.mobdev_project_2026.MySingleton;
import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.StatusDao;
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
        AppDatabase db = MySingleton.getInstance(getApplicationContext()).getDb();
        TaskDao taskDao = db.taskDao();
        StatusDao statusDao = db.statusDao();

        // 1) Calculate "start of today" (midnight) -> delete older tasks
//        long startOfTodayMillis = getStartOfTodayMillis();
//
//        // Delete tasks from previous days (adjust column name!)
//        taskDao.deleteTasksBefore(startOfTodayMillis);
//
//        // 2) Load tasks that are not completed
//        List<Task> tasks = taskDao.getAllNotCompletedTasks(); // you implement this query
//
//        // Preload status IDs (avoid repeated queries)
//        long recordedId = statusDao.getStatus("RECORDED").getId();
//        long inProgressId = statusDao.getStatus("IN-PROGRESS").getId();
//        long expiredId = statusDao.getStatus("EXPIRED").getId();
//
//        long now = System.currentTimeMillis();
//
//        for (Task t : tasks) {
//            long startMillis = computeTaskStartMillis(t);         // date + time
//            long endMillis = startMillis + t.getDuration() * 60L * 60L * 1000L; // duration in hours
//
//            long newStatusId;
//            if (now < startMillis) {
//                newStatusId = recordedId;
//            } else if (now < endMillis) {
//                newStatusId = inProgressId;
//            } else {
//                newStatusId = expiredId;
//            }
//
//            if (t.getStatusId() != newStatusId) {
//                taskDao.updateTaskStatus(t.getId(), newStatusId);
//            }
//        }
//
//        return Result.success();
//    }
//
//    private long getStartOfTodayMillis() {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTimeInMillis();
//    }
//
//    /**
//     * Build start datetime millis using:
//     * - task date (stored as long millis, usually contains the day)
//     * - task start time (java.sql.Time)
//     */
//    private long computeTaskStartMillis(Task t) {
//        Calendar day = Calendar.getInstance();
//        day.setTimeInMillis(t.getDateValue().getTime()); // if getDateValue() returns Date
//
//        Calendar time = Calendar.getInstance();
//        time.setTimeInMillis(t.getStartTime().getTime()); // java.sql.Time internally has date=1970-01-01
//
//        day.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
//        day.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
//        day.set(Calendar.SECOND, 0);
//        day.set(Calendar.MILLISECOND, 0);
//
//        return day.getTimeInMillis();
//    }

    }
}
