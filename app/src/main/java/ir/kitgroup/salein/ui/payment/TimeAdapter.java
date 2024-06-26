package ir.kitgroup.salein.ui.payment;




import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;


import java.util.List;

import ir.kitgroup.salein.R;


public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.viewHolder> {
    private final List<String> list;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }
    public interface ClickItem {
        void onRowClick(String Name);
    }
    private ClickItem clickItem;


    public TimeAdapter( List<String> list) {
        this.list = list;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {

        String time = list.get(position);

        holder.tvTime.setText(time);

        holder.itemView.setOnClickListener(view -> {
            clickItem.onRowClick(time);
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTime;

        public viewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}