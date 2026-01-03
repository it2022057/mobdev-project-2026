package hua.dit.mobdev_project_2026.db;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    public Long insertTask(Task task);

    @Update
    public void updateTask(Task task);

    @Query("DELETE FROM task WHERE id = :taskId")
    public int deleteTaskById(int taskId);

    @Query("SELECT * from task")
    public List<Task> getAllTasks();

    @Query("SELECT * FROM task WHERE id = :taskId")
    public Task getTaskById(int taskId);

    @Query("SELECT * FROM task WHERE short_name = :name")
    public Task findTaskByName(String name);

    @Query("SELECT t.id, t.short_name, s.name AS status FROM task t, status s WHERE (t.status_id = s.id) AND (t.status_id != (SELECT id FROM status WHERE name = 'COMPLETED')) ORDER BY CASE WHEN t.status_id = (SELECT id FROM status WHERE name = 'EXPIRED') THEN 1 WHEN t.status_id = (SELECT id FROM status WHERE name = 'IN_PROGRESS') THEN 2 ELSE 3 END")
    public List<TaskWithStatus> getNonCompletedTasks();

    @Query("SELECT t.id, t.short_name, s.name AS status FROM task t, status s WHERE t.status_id = s.id")
    public List<TaskWithStatus> getTaskWithStatusList();

    @Query("SELECT t.id, t.short_name, s.name AS status FROM task t, status s WHERE t.status_id = s.id")
    public Cursor getTaskWithStatusCursor();

}
