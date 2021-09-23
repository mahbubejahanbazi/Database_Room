# Room Database
This simple application load list of records from database, in addition you can update, delete and insert information easily 

- Add item: by cliclicking on add icon
- Update: by cicking on each list item
- Delete: by swipe right or left each list item

## Tech Stack

Java
<p align="left">
   <a href="https://developer.android.com/training/data-storage/room">Room Database</a> 
</p>

<p align="left">
   <a href="https://developer.android.com/topic/libraries/architecture/livedata">LiveData</a> 
</p>


<p align="left">
   <a href="https://developer.android.com/jetpack/guide">MVVM Architecture</a> 
</p>

<p align="center">
  <img src="https://github.com/mahbubejahanbazi/database_room/blob/main/images/insert.jpg" />
</p>

<p align="center">
  <img src="https://github.com/mahbubejahanbazi/database_room/blob/main/images/update.jpg" />
</p>
## Source code

TimeEntity.java
```java
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_time")
public class TimeEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private long id = 0;

    @ColumnInfo(name = "current_time")
    private String currentTime;

    public TimeEntity(@NonNull String currentTime) {
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
```
TimeDao.java
```java
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
    LiveData<List<TimeEntity>> getAllTime();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TimeEntity currentTime);

    @Query("UPDATE table_time SET `current_time` = :currentTime WHERE id =:id")
    void update(long id, String  currentTime);

    @Delete
    void delete(TimeEntity currentTime);

    @Query("delete from table_time ")
    void deleteAll();
}
```
TimeRepository.java
```java
import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TimeRepository {
    private TimeDao dao;

    public TimeRepository(Application application) {
        TimeDatabase db = TimeDatabase.getDB(application);
        dao = db.getTimeDao();
    }

    LiveData<List<TimeEntity>> getAllTime() throws ExecutionException, InterruptedException {
        Callable<LiveData<List<TimeEntity>>> callable = new Callable<LiveData<List<TimeEntity>>>() {
            @Override
            public LiveData<List<TimeEntity>> call() throws Exception {
                return dao.getAllTime();
            }
        };
        Future<LiveData<List<TimeEntity>>> furure = TimeDatabase.databaseExecuter.submit(callable);
        return furure.get();
    }

    void insert(TimeEntity currentTime) {
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

    void delete(TimeEntity currentTime) {
        TimeDatabase.databaseExecuter.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(currentTime);
            }
        });
    }
}
```
TimeDatabase.java
```java
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

@Database(entities = {TimeEntity.class}, version = 1, exportSchema = false)
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
                dao.insert(new TimeEntity(formatDate));

            });
        }
    };
}
```
TimeViewModel.java
```java
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TimeViewModel extends AndroidViewModel {
    private TimeRepository repository;
    private LiveData<List<TimeEntity>> allTime;

    public TimeViewModel(Application application) {
        super(application);
        repository = new TimeRepository(application);
        try {
            allTime = repository.getAllTime();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<TimeEntity>> getAllTime() {
        return allTime;
    }

    public void insert(TimeEntity currentTime) {
        repository.insert(currentTime);
    }

    public void update(long id, String currentTime) {
        repository.update(id, currentTime);
    }

    public void delete(TimeEntity currentTime) {
        repository.delete(currentTime);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
```
TimeAdapter.java
```java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeAdapter extends ListAdapter<TimeEntity, TimeViewHolder> {
    TimeViewModel viewModel;

    protected TimeAdapter(@NonNull DiffUtil.ItemCallback<TimeEntity> diffCallback, TimeViewModel viewModel) {
        super(diffCallback);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recyclerview_item, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeEntity item = getItem(position);
                String pattern = "EEEE dd MMMM - hh: mm: ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String format = dateFormat.format(time);
                viewModel.update(item.getId(), format);
                Toast.makeText(view.getContext(), "Updating " +
                        item.getCurrentTime(), Toast.LENGTH_SHORT).show();
            }
        });
        TimeEntity currentTime = getItem(position);
        holder.bind(currentTime.getCurrentTime());
    }

    public TimeEntity getTimeAtPosition(int position) {
        return getItem(position);
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<TimeEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull TimeEntity oldItem, @NonNull TimeEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TimeEntity oldItem, @NonNull TimeEntity newItem) {
            return oldItem.getCurrentTime().equals(newItem.getCurrentTime());
        }
    }
}

class TimeViewHolder extends RecyclerView.ViewHolder {
    private final TextView time;

    public TimeViewHolder(@NonNull View itemView) {
        super(itemView);
        this.time = itemView.findViewById(R.id.recyclerview_item_textView);
    }

    public void bind(String str) {
        time.setText(str);
    }

}
```
MainActivity.java
```java
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static TimeViewModel timeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeViewModel = new ViewModelProvider(this, ViewModelProvider.
                AndroidViewModelFactory.getInstance(this.getApplication())).
                get(TimeViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.activity_main_recyclerview);
        TimeAdapter adapter = new TimeAdapter(new TimeAdapter.DiffCallback(), timeViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        timeViewModel.getAllTime().observe(this, new Observer<List<TimeEntity>>() {
            @Override
            public void onChanged(List<TimeEntity> times) {
                adapter.submitList(times);
                recyclerView.scrollToPosition(times.size() - 1);
            }
        });
        FloatingActionButton add = findViewById(R.id.activity_main_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pattern = "EEEE dd MMMM - hh: mm: ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                Calendar calendar = Calendar.getInstance();
                Date time = calendar.getTime();
                String formatDate = dateFormat.format(time);
                TimeEntity time1 = new TimeEntity(formatDate);
                timeViewModel.insert(time1);
                Toast.makeText(view.getContext(), "inserting " +
                        time1.getCurrentTime(), Toast.LENGTH_SHORT).show();
            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        TimeEntity time = adapter.getTimeAtPosition(position);
                        Toast.makeText(MainActivity.this, "Deleting " +
                                time.getCurrentTime(), Toast.LENGTH_SHORT).show();
                        timeViewModel.delete(time);
                    }
                });
        helper.attachToRecyclerView(recyclerView);
    }

}
```
## Contact

mjahanbazi@protonmail.com