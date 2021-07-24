package ir.mjahanbazi.databaseusingroom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MJTime.class}, version = 1, exportSchema = false)
public abstract class MJTimeDatabase extends RoomDatabase {
    public abstract MJTimeDao getMJTimeDao();

    private static volatile MJTimeDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseExecuter = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MJTimeDatabase getDB(final Context context) {
        if (instance == null) {
            synchronized (MJTimeDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            MJTimeDatabase.class, "time_database")
                            .addCallback(callback)
                            .build();
                }
            }
        }
        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecuter.execute(() -> {
                MJTimeDao dao = instance.getMJTimeDao();
                dao.deleteAll();
                MJTime time = new MJTime(System.currentTimeMillis() + "");
                dao.insert(time);

            });
        }
    };
}
