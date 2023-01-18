package ir.kitgroup.saleinkhavari.ui.payment;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ir.kitgroup.saleinkhavari.models.OrderType;


import ir.kitgroup.saleinkhavari.R;
public class OrderTypeOrderAdapter extends RecyclerView.Adapter<OrderTypeOrderAdapter.viewHolder> {
    private  final List<OrderType> list;
    private final Activity context;


    public interface ClickItem {
        void onRowClick(String GUID,Integer code);
    }
    private ClickItem clickItem;
    public void setOnClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


    public OrderTypeOrderAdapter(Activity context, List<OrderType> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_subgroup_item_mobile, parent, false));
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
        final OrderType orderType = list.get(position);
        holder.name.setText(orderType.getN());

        if (orderType.Click) {
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_bottom_low_radius));
            holder.name.setTextColor(context.getResources().getColor(R.color.color_text_screen));

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