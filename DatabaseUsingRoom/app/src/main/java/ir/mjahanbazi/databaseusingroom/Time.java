package ir.mjahanbazi.databaseusingroom;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_time")
public class Time {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private long id = 0;

    @ColumnInfo(name = "current_time")
    private String currentTime;

    public Time(@NonNull String currentTime) {
        this.currentTime = currentTime;
    }

    @NonNull
    public String getCurrentTime() {
        return currentTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
