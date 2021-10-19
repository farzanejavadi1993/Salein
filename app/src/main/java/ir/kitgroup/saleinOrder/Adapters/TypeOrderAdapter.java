package ir.kitgroup.saleinOrder.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;

import java.util.List;


import ir.kitgroup.saleinOrder.models.OrderType;

import ir.kitgroup.saleinOrder.R;


public class TypeOrderAdapter extends RecyclerView.Adapter<TypeOrderAdapter.viewHolder> {

    private final List<OrderType> list;
    private final Context context;


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(Integer code, Integer ty);
    }

    private ClickItem clickItem;


    public TypeOrderAdapter(Context context, List<OrderType> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_type_order, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final OrderType orderType = list.get(position);


        holder.tvOrderType.setText(orderType.getN());



        if (!list.get(position).Click){
            holder.cardOrderType.setBackgroundColor(context.getResources().getColor(R.color.orange_light));

        }else {
            holder.cardOrderType.setBackgroundColor(context.getResources().getColor(R.color.orange_dark));
        }


        list.get(position).Click=false;

        holder.cardOrderType.setOnClickListener(view -> {

            if (!list.get(position).Click){

                notifyDataSetChanged();


                if (holder.longClick>=1) {
                    holder.longClick=0;
                    holder.cardOrderType.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
                    clickItem.onRowClick(0, 0);
                } else {

                    //go to order fragment
                    clickItem.onRowClick(list.get(position).getC(), list.get(position).getTy());

                }
            }


            else {
                //get order
                clickItem.onRowClick(list.get(position).getC(), 100);
                notifyDataSetChanged();


            }







        });

        holder.cardOrderType.setOnLongClickListener(v -> {
            for(int i=0 ; i< list.size() ;i++){
                list.get(i).Click=false;
            }
            list.get(position).Click=true;
            holder.longClick++;

            return false;
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {


        private final RelativeLayout cardOrderType;
        private final TextView tvOrderType;
        private int longClick = 0;



        public viewHolder(View itemView) {
            super(itemView);


            cardOrderType = itemView.findViewById(R.id.btnOrderType);
            tvOrderType = itemView.findViewById(R.id.tv_order_type);


        }
    }


}