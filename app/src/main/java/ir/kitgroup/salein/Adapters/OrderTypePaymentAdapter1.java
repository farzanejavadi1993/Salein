package ir.kitgroup.salein.Adapters;



import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.models.OrderType;


import ir.kitgroup.salein.Fragments.MobileView.SplashScreenFragment;
import ir.kitgroup.salein.R;


public class OrderTypePaymentAdapter1 extends RecyclerView.Adapter<OrderTypePaymentAdapter1.viewHolder> {

    private  final List<OrderType> list;
    private final Activity context;

    @Inject
    Double ScreenSize;
    private int fontSize=0;
    public interface ClickItem {
        void onRowClick(String GUID,Integer code);
    }

    private ClickItem clickItem;

    public void setOnClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


    public OrderTypePaymentAdapter1(Activity context, List<OrderType> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (ScreenSize >=7)
            fontSize=13;
        else
            fontSize=11;
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_subgroup_item_mobile, parent, false));


    }


    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
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







