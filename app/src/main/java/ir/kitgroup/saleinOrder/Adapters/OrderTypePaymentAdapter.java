package ir.kitgroup.saleinOrder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ir.kitgroup.saleinOrder.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinOrder.DataBase.OrderType;


import ir.kitgroup.saleinOrder.R;


public class OrderTypePaymentAdapter extends RecyclerView.Adapter<OrderTypePaymentAdapter.viewHolder> {

    private final List<OrderType> list;
    private final Context context;

    private int fontSize=0;
    public interface ClickItem {
        void onRowClick(String GUID,Integer code);
    }

    private ClickItem clickItem;

    public void setOnClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


    public OrderTypePaymentAdapter(Context context, List<OrderType> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (LauncherActivity.screenInches>=7)
            fontSize=13;
        else
            fontSize=11;
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_subgroup_item_mobile, parent, false));


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final OrderType orderType = list.get(position);

        holder.name.setText(orderType.getN());


        holder.name.setTextSize(fontSize);
        if (orderType.Click) {

                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.order_item_recycle_group__card_background));
                holder.name.setTextColor(context.getResources().getColor(R.color.white));

        } else if (!orderType.Click) {

                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_subgroup_mobile));
                holder.name.setTextColor(context.getResources().getColor(R.color.medium_color));



        }


        holder.itemView.setOnClickListener(view -> clickItem.onRowClick(orderType.getI(),orderType.getC()));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {


        private final TextView name;
        private final MaterialCardView cardView;


        public viewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.order_item_recycle_card);
            name = itemView.findViewById(R.id.order_item_recycle_subgroup_txt_name);


        }
    }


}






