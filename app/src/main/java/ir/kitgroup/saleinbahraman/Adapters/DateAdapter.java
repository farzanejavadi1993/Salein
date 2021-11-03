
package ir.kitgroup.saleinbahraman.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.kitgroup.saleinbahraman.R;
import ir.kitgroup.saleinbahraman.classes.Utilities;
import ir.kitgroup.saleinbahraman.models.ModelDate;


public class DateAdapter extends RecyclerView.Adapter<DateAdapter.viewHolder> {

    private final List<ModelDate> list;
    private final Context context;


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(Date date,int position);
    }

    private ClickItem clickItem;


    public DateAdapter(Context context, List<ModelDate> list) {
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

        ModelDate modelDate = list.get(position);
        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");
        Utilities.SolarCalendar sc;

        sc = util.new SolarCalendar(modelDate.date);
        holder.tvDay.setText(sc.strWeekDay);
        holder.tvDate.setText(String.format(loc, "%02d", sc.date) + "\t" + sc.strMonth + "\t" + (sc.year));

        holder.itemView.setOnClickListener(view -> {
            ArrayList<ModelDate> arrayList=new ArrayList<>(list);
            CollectionUtils.filter(arrayList,a->a.Click);

                    if(arrayList.size()>0)
                        list.get(list.indexOf(arrayList.get(0))).Click=false;


            list.get(position).Click=true;

         notifyDataSetChanged();
         clickItem.onRowClick(modelDate.date,position);
        });

        if (modelDate.Click)
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.blue300));
        else
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.stroke_color));



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDay;
        private final TextView tvDate;
        private final MaterialCardView cardView;
        private  Boolean click=false;


        public viewHolder(View itemView) {
            super(itemView);

            tvDay = itemView.findViewById(R.id.txt_day);
            tvDate = itemView.findViewById(R.id.txt_date);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }


}








