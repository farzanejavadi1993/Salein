package ir.kitgroup.saleinkhavari.ui.launcher.homeItem;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import ir.kitgroup.saleinkhavari.R;
import ir.kitgroup.saleinkhavari.models.CustomTab;


public class CustomTabAdapter extends RecyclerView.Adapter<CustomTabAdapter.viewHolder> {
    private final List<CustomTab> customTabs ;
    private final Activity context;

    public interface ClickItem {
        void onRowClick(int key);
    }
    private ClickItem clickItem;
    public void SetOnItemClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public CustomTabAdapter(Activity context, List<CustomTab> customTabs) {
        this.context = context;
        this.customTabs = customTabs;
    }


    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_group_item1, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final CustomTab customTab = customTabs.get(position);

        holder.txtNameTab.setText(customTab.getTn());

        if (customTab.Click) {
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.order_item_recycle_group__card_background));
            holder.txtNameTab.setTextColor(context.getResources().getColor(R.color.white));

        } else {
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_subgroup_mobile));
            holder.txtNameTab.setTextColor(context.getResources().getColor(R.color.black_l));
        }

        holder.itemView.setOnClickListener(view -> clickItem.onRowClick(customTabs.get(position).getT()));
    }


    @Override
    public int getItemCount() {
        return customTabs.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNameTab;
        private final RelativeLayout cardView;

        public viewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.order_item_recycle_card);
            txtNameTab = itemView.findViewById(R.id.order_item_recycle_group_txt_name);
        }
    }

}