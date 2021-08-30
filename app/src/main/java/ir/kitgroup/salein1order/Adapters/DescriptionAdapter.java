package ir.kitgroup.salein1order.Adapters;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.salein1order.Fragments.TabletView.OrderFragment;
import ir.kitgroup.salein1order.MainActivity;
import ir.kitgroup.salein1order.models.Description;
import ir.kitgroup.salein1order.R;


public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.viewHolder> {

    private List<Description> list = new ArrayList<>();
    private Context context;
    private int fontSize = 0;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(String desc, boolean choose);
    }

    private ClickItem clickItem;


    public DescriptionAdapter(Context context, List<Description> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (MainActivity.screenInches >= 7) {
            fontSize = 13;
        } else {
            fontSize = 12;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.launcher_recycle_table_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final Description description = list.get(position);


        holder.tableName.setText(description.DSC);
        holder.tableName.setTextColor(context.getResources().getColor(R.color.black));
        holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.launcher_item_recycle_table_card_background));


        if (description.Click) {
            holder.tableName.setTextColor(context.getResources().getColor(R.color.white));
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.launcher_item_recycle_table_reserve_card_background));
        } else {
            holder.tableName.setTextColor(context.getResources().getColor(R.color.black));
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.launcher_item_recycle_table_card_background));
        }


        holder.itemView.setOnClickListener(view -> {

            if (!description.Click) {
            if(MainActivity.screenInches>=7)
                OrderFragment.descriptionList.get(holder.getAdapterPosition()).Click = true;
            else

               // MainOrderMobileFragment.descriptionList.get(holder.getAdapterPosition()).Click = true;
                holder.tableName.setTextColor(context.getResources().getColor(R.color.white));
                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.launcher_item_recycle_table_reserve_card_background));
                clickItem.onRowClick(list.get(position).DSC, true);
            } else {
                if(MainActivity.screenInches>=7)
                OrderFragment.descriptionList.get(holder.getAdapterPosition()).Click = false;
                else
                  //  MainOrderMobileFragment.descriptionList.get(holder.getAdapterPosition()).Click = false;

                holder.tableName.setTextColor(context.getResources().getColor(R.color.black));
                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.launcher_item_recycle_table_card_background));
                clickItem.onRowClick(list.get(position).DSC, false);
            }


        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {


        private TextView tableName;
        private RelativeLayout cardView;


        public viewHolder(View itemView) {
            super(itemView);

            tableName = itemView.findViewById(R.id.launcher_recycle_table_txt_name);
            cardView = itemView.findViewById(R.id.launcher_recycle_table_card);
            tableName.setTextSize(fontSize);
            if (MainActivity.screenInches >= 7) {


            } else {

            }


        }
    }


}







