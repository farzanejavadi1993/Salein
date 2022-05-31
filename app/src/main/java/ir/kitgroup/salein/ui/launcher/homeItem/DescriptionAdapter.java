package ir.kitgroup.salein.ui.launcher.homeItem;


import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.salein.models.Description;
import ir.kitgroup.salein.R;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.viewHolder> {

    private List<Description> list = new ArrayList<>();
    private Activity context;


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(String desc, boolean choose,int position);
    }

    private ClickItem clickItem;


        public DescriptionAdapter(Activity context, List<Description> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
//            if(LauncherActivity.screenInches>=7)
//                OrderFragment.descriptionList.get(holder.getAdapterPosition()).Click = true;
//            else


                holder.tableName.setTextColor(context.getResources().getColor(R.color.white));
                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.launcher_item_recycle_table_reserve_card_background));
                clickItem.onRowClick(list.get(position).DSC, true,holder.getAdapterPosition());
            }
            else {
               /* if(LauncherActivity.screenInches>=7)
                OrderFragment.descriptionList.get(holder.getAdapterPosition()).Click = false;
                else*/
                  //  MainOrderMobileFragment.descriptionList.get(holder.getAdapterPosition()).Click = false;

                holder.tableName.setTextColor(context.getResources().getColor(R.color.black));
                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.launcher_item_recycle_table_card_background));
                clickItem.onRowClick(list.get(position).DSC, false,holder.getAdapterPosition());
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



        }
    }


}







