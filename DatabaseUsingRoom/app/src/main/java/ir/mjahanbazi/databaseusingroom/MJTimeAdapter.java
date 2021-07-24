package ir.mjahanbazi.databaseusingroom;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MJTimeAdapter extends ListAdapter<MJTime, MJTimeViewHolder> {
    MJTimeViewModel viewModel;

    protected MJTimeAdapter(@NonNull DiffUtil.ItemCallback<MJTime> diffCallback, MJTimeViewModel viewModel) {
        super(diffCallback);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MJTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MJTimeViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MJTimeViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MJTime item = getItem(position);
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
        MJTime currentTime = getItem(position);
        holder.bind(currentTime.getCurrentTime());
    }

    public MJTime getTimeAtPosition(int position) {
        return getItem(position);
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<MJTime> {

        @Override
        public boolean areItemsTheSame(@NonNull MJTime oldItem, @NonNull MJTime newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MJTime oldItem, @NonNull MJTime newItem) {
            return oldItem.getCurrentTime().equals(newItem.getCurrentTime());
        }
    }
}
