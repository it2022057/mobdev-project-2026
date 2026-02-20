package hua.dit.mobdev_project_2026.db;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    public Long insertTask(Task task);

    @Delete
    public int deleteTask(Task task);

    @Query("DELETE FROM task WHERE date < :startOfToday")
    public int deleteTasksBeforeToday(long startOfToday);

    @Query("SELECT * from task")
    public List<Task> getAllTasks();

    @Query("SELECT * FROM task WHERE id = :taskId")
    public Task getTaskById(long taskId);

    @Query("SELECT t.id, t.short_name, s.name AS status FROM task t LEFT JOIN status s ON s.id = t.status_id  WHERE t.id = :id")
    public Cursor getTaskWithStatusByIdCursor(long id);

    @Update
    public int updateTask(Task task);

    @Query("UPDATE task SET status_id = :newStatusId WHERE id = :taskId")
    public void updateTaskStatus(long taskId, long newStatusId);

    @Query("SELECT t.id, t.short_name, s.name AS status FROM task t, status s WHERE (t.status_id = s.id) AND (t.status_id != (SELECT id FROM status WHERE name = 'COMPLETED')) ORDER BY CASE WHEN t.status_id = (SELECT id FROM status WHERE name = 'EXPIRED') THEN 1 WHEN t.status_id = (SELECT id FROM status WHERE name = 'IN_PROGRESS') THEN 2 ELSE 3 END")
    public List<TaskWithStatus> getNonCompletedTasks();

    @Query("SELECT t.*, s.name AS status FROM task t, status s WHERE (t.status_id = s.id) AND (t.status_id != (SELECT id FROM status WHERE name = 'COMPLETED')) ORDER BY CASE WHEN t.status_id = (SELECT id FROM status WHERE name = 'EXPIRED') THEN 1 WHEN t.status_id = (SELECT id FROM status WHERE name = 'IN_PROGRESS') THEN 2 ELSE 3 END")
    public Cursor getNonCompletedTasksCursor();

    @Query("SELECT t.id, t.short_name, s.name AS status FROM task t, status s WHERE t.status_id = s.id")
    public List<TaskWithStatus> getTaskWithStatusList();

    @Query("SELECT t.id, t.short_name, s.name AS status FROM task t, status s WHERE t.status_id = s.id")
    public Cursor getTaskWithStatusCursor();

    @Query("SELECT * FROM task WHERE status_id = :statusId ")
    public List<Task> getTasksByStatus(long statusId);


}
