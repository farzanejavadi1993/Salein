package ir.kitgroup.salein.Adapters;

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


import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.models.ProductLevel1;


public class ProductLevel1Adapter extends RecyclerView.Adapter<ProductLevel1Adapter.viewHolder> {

    private final List<ProductLevel1> productGroupLevel1s ;
    private final Activity context;
    private  int fontSize=0;

    public interface ClickItem {
        void onRowClick(String GUID);
    }

    private ClickItem clickItem;

    public void SetOnItemClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


    public ProductLevel1Adapter(Activity context, List<ProductLevel1> groupList) {
        this.context = context;
        this.productGroupLevel1s = groupList;

    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

       if (Util.screenSize >=7)
            fontSize=13;
        else
            fontSize=11;


            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_group_item1, parent, false));




    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final ProductLevel1 productGroupLevel1 = productGroupLevel1s.get(position);

        holder.groupName.setText(productGroupLevel1.getN());



        holder.groupName.setTextSize(fontSize);


        if (productGroupLevel1.Click) {
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.group_background));
            holder.groupName.setTextColor(context.getResources().getColor(R.color.white));

        } else {
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_subgroup_mobile));
            holder.groupName.setTextColor(context.getResources().getColor(R.color.black_l));

        }


        holder.itemView.setOnClickListener(view -> clickItem.onRowClick(productGroupLevel1s.get(position).getI()));


    }

    @Override
    public int getItemCount() {
        return productGroupLevel1s.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {



        private final TextView groupName;

        private final RelativeLayout cardView;


        public viewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.order_item_recycle_card);

            groupName = itemView.findViewById(R.id.order_item_recycle_group_txt_name);


        }
    }


}






