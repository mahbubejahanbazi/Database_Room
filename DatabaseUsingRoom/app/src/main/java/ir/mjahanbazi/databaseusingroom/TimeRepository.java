package ir.mjahanbazi.databaseusingroom;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TimeRepository {
    private TimeDao dao;
    private LiveData<List<Time>> allTime;

    public TimeRepository(Application application) {
        TimeDatabase db = TimeDatabase.getDB(application);
        dao = db.getTimeDao();
        allTime = dao.getAllTime();
    }

    LiveData<List<Time>> getAllTime() {
        return allTime;
    }

    void insert(Time currentTime) {
        TimeDatabase.databaseExecuter.execute(() -> {
            dao.insert(currentTime);
        });
    }

    void update(long id, String currentTime) {
        TimeDatabase.databaseExecuter.execute(() -> {
            dao.update(id, currentTime);
        });
    }

    void deleteAll() {
        TimeDatabase.databaseExecuter.execute(() -> {
            dao.deleteAll();
        });
    }

    void delete(Time currentTime) {
        TimeDatabase.databaseExecuter.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(currentTime);
            }
        });
    }
}
