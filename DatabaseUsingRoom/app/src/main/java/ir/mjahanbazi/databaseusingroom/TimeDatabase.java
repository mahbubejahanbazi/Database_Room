package ir.mjahanbazi.databaseusingroom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Time.class}, version = 1, exportSchema = false)
public abstract class TimeDatabase extends RoomDatabase {
    public abstract TimeDao getTimeDao();

    private static volatile TimeDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseExecuter = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static TimeDatabase getDB(final Context context) {
        if (instance == null) {
            synchronized (TimeDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            TimeDatabase.class, "time_database")
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
                TimeDao dao = instance.getTimeDao();
                dao.deleteAll();
                String pattern = "EEEE dd MMMM - hh: mm: ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String formatDate = dateFormat.format(time);
                dao.insert(new Time(formatDate));

            });
        }
    };
}
