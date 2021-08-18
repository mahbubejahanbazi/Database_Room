package ir.mjahanbazi.databaseusingroom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimeDao {
    @Query("select * from table_time order by current_time asc ;")
    LiveData<List<Time>> getAllTime();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Time currentTime);

    @Query("UPDATE table_time SET `current_time` = :currentTime WHERE id =:id")
    void update(long id, String  currentTime);

    @Delete
    void delete(Time currentTime);

    @Query("delete from table_time ")
    void deleteAll();
}
