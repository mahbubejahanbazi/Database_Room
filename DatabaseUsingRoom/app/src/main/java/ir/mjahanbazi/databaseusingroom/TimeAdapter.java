package ir.mjahanbazi.databaseusingroom;

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

public class TimeAdapter extends ListAdapter<Time, TimeViewHolder> {
    TimeViewModel viewModel;

    protected TimeAdapter(@NonNull DiffUtil.ItemCallback<Time> diffCallback, TimeViewModel viewModel) {
        super(diffCallback);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TimeViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Time item = getItem(position);
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
        Time currentTime = getItem(position);
        holder.bind(currentTime.getCurrentTime());
    }

    public Time getTimeAtPosition(int position) {
        return getItem(position);
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<Time> {

        @Override
        public boolean areItemsTheSame(@NonNull Time oldItem, @NonNull Time newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Time oldItem, @NonNull Time newItem) {
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

    public static TimeViewHolder create(ViewGroup viewParent) {
        View view = LayoutInflater.from(viewParent.getContext()).
                inflate(R.layout.recyclerview_item, viewParent, false);
        return new TimeViewHolder(view);
    }

}
