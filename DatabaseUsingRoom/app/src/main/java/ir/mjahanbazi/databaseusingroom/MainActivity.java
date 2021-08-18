package ir.mjahanbazi.databaseusingroom;

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
        timeViewModel.getAllTime().observe(this, new Observer<List<Time>>() {
            @Override
            public void onChanged(List<Time> times) {
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
                Time mJTime = new Time(formatDate);
                timeViewModel.insert(mJTime);
                Toast.makeText(view.getContext(), "inserting " +
                        mJTime.getCurrentTime(), Toast.LENGTH_SHORT).show();
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
                        Time time = adapter.getTimeAtPosition(position);
                        Toast.makeText(MainActivity.this, "Deleting " +
                                time.getCurrentTime(), Toast.LENGTH_SHORT).show();
                        timeViewModel.delete(time);
                    }
                });
        helper.attachToRecyclerView(recyclerView);
    }

}
