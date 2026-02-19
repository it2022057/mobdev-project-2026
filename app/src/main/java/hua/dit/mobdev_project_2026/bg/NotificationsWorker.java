package hua.dit.mobdev_project_2026.bg;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationsWorker extends Worker {

    public NotificationsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
