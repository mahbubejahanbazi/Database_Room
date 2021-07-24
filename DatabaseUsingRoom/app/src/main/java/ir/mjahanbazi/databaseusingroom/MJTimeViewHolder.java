package ir.mjahanbazi.databaseusingroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class MJTimeViewHolder extends RecyclerView.ViewHolder {
    private final TextView time;

    public MJTimeViewHolder(@NonNull View itemView) {
        super(itemView);
        this.time = itemView.findViewById(R.id.recyclerview_item_textView);
    }

    public void bind(String str) {
        time.setText(str);
    }

    public static MJTimeViewHolder create(ViewGroup viewParent) {
        View view = LayoutInflater.from(viewParent.getContext()).
                inflate(R.layout.recyclerview_item, viewParent, false);
        return new MJTimeViewHolder(view);
    }

}
