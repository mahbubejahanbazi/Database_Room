package ir.mjahanbazi.databaseusingroom;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MJTimeRepository {
    private MJTimeDao dao;
    private LiveData<List<MJTime>> allTime;

    public MJTimeRepository(Application application) {
        MJTimeDatabase db = MJTimeDatabase.getDB(application);
        dao = db.getMJTimeDao();
        allTime = dao.getAllTime();
    }

    LiveData<List<MJTime>> getAllTime() {
        return allTime;
    }

    void insert(MJTime currentTime) {
        MJTimeDatabase.databaseExecuter.execute(() -> {
            dao.insert(currentTime);
        });
    }

    void update(long id, String currentTime) {
        MJTimeDatabase.databaseExecuter.execute(() -> {
            dao.update(id, currentTime);
        });
    }

    void deleteAll() {
        MJTimeDatabase.databaseExecuter.execute(() -> {
            dao.deleteAll();
        });
    }

    void delete(MJTime currentTime) {
        MJTimeDatabase.databaseExecuter.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(currentTime);
            }
        });
    }
}
