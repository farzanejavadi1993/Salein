
package ir.kitgroup.saleinOrder.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;


import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.Util.Utilities;


public class DateAdapter extends RecyclerView.Adapter<DateAdapter.viewHolder> {

    private final List<Date> list;
    private final Context context;


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(Date date);
    }

    private ClickItem clickItem;


    public DateAdapter(Context context, List<Date> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {

        Date date = list.get(position);
        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");
        Utilities.SolarCalendar sc;

        sc = util.new SolarCalendar(date);
        holder.tvDay.setText(sc.strWeekDay);
        holder.tvDate.setText(String.format(loc, "%02d", sc.date) + "\t" + sc.strMonth + "\t" + (sc.year));

        holder.itemView.setOnClickListener(view -> {
            clickItem.onRowClick(date);
        });


    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDay;
        private final TextView tvDate;


        public viewHolder(View itemView) {
            super(itemView);

            tvDay = itemView.findViewById(R.id.txt_day);
            tvDate = itemView.findViewById(R.id.txt_date);
        }
    }


}








