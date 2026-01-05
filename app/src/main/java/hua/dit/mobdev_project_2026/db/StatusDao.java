package hua.dit.mobdev_project_2026.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StatusDao {

    @Insert
    public void insertStatus(Status status);

    @Insert
    public List<Long> insertAll(List<Status> status);

    @Delete
    public void deleteStatus(Status status);

    @Query("SELECT * FROM status")
    public List<Status> getAllStatus();

    @Query("SELECT * FROM status WHERE name = :statusName")
    public Status getStatus(String statusName);

    @Query("SELECT name FROM status WHERE id = :statusId")
    public String getStatusNameById(long statusId);

}
