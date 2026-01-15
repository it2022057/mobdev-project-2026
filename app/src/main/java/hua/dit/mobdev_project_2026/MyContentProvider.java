package hua.dit.mobdev_project_2026;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;

import hua.dit.mobdev_project_2026.db.AppDatabase;
import hua.dit.mobdev_project_2026.db.MyConverters;
import hua.dit.mobdev_project_2026.db.Status;
import hua.dit.mobdev_project_2026.db.Task;

public class MyContentProvider extends ContentProvider {

    private static final String TAG = "MyContentProvider";

    /* Content Provider URI */
    private static final String MY_PROVIDER = "hua.dit.mobdev_project_2026.my_provider";
    private static final String CONTENT_URI_STR = "content://" + MY_PROVIDER + "/task";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STR);

    /* Helper - UriMatcher */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int URI_CODE_1 = 1;
    private static final int URI_CODE_2 = 2;
    static {
        uriMatcher.addURI(MY_PROVIDER, "task", URI_CODE_1);
        uriMatcher.addURI(MY_PROVIDER, "task/#", URI_CODE_2);
    }

    private AppDatabase db;

    public MyContentProvider() {
    }

    @Override
    public boolean onCreate() {
        db = MySingleton.getInstance(getContext()).getDb();
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "Insert Data: uri=" + uri + " , values=" + values);
        if (uriMatcher.match(uri) == URI_CODE_1) {
            String shortName = values.getAsString("shortName");
            String briefDescription = values.getAsString("briefDescription");
            int difficulty = values.getAsInteger("difficulty");
            String startTime = values.getAsString("startTime");
            int duration = values.getAsInteger("duration");
            String statusName = values.getAsString("statusName");
            String location = values.getAsString("location");

            // Find Status ID
            Status status = db.statusDao().getStatus(statusName);
            if (status == null)
                throw new RuntimeException("statusName = " + statusName + " not found !");

            Task task = new Task(shortName, briefDescription, difficulty, new Date(), new MyConverters().stringToTime(startTime), duration, status.getId(), location);

            // Insert Data and Return Row URI
            long task_id = db.taskDao().insertTask(task);
            Log.i(TAG, "Insert Data: NEW Task ID: " + task_id);
            return ContentUris.withAppendedId(CONTENT_URI, task_id);
        }

        throw new RuntimeException("Insert Method - Not supported URI: " + uri);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Query Data: uri=" + uri + " ...");
        // Select Fields, Where Conditions and Order Instructions are being ignored
        switch (uriMatcher.match(uri)) {
            case URI_CODE_1:
                return db.taskDao().getTaskWithStatusCursor();
            case URI_CODE_2:
                long row = ContentUris.parseId(uri);
                return db.taskDao().getTaskWithStatusByIdCursor(row);
            default:
                throw new RuntimeException("Query Method - Not supported URI: " + uri);
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri) ) {
            case URI_CODE_1:
                return "vnd.android.cursor.dir/task";
            case URI_CODE_2:
                return "vnd.android.cursor.item/task";
            default:
                throw new RuntimeException("Get Type Method - Not supported URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
