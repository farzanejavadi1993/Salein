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


import ir.kitgroup.saleinOrder.Fragments.MobileView.SplashScreenFragment;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.models.ProductLevel2;


public class ProductLevel2Adapter extends RecyclerView.Adapter<ProductLevel2Adapter.viewHolder> {

    private final List<ProductLevel2> subGroupList ;
    private final Context context;
    private  int fontSize=0;
    public interface ClickItem {
        void onRowClick(String GUID);
    }

    private ClickItem clickItem;

    public void SetOnItemClickListener(ClickItem clickItem){
        this.clickItem=clickItem;
    }



    public ProductLevel2Adapter(Context context, List<ProductLevel2> subGroupList) {
        this.context = context;
        this.subGroupList = subGroupList;

    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {


     /*   if (LauncherActivity.screenInches >= 7){

            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_subgroup_item_tablet, parent, false));

        }else {*/

        if (SplashScreenFragment.screenInches>=7)
            fontSize=13;
        else
            fontSize=11;
            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_subgroup_item_mobile, parent, false));
       // }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final ProductLevel2 subGroup = subGroupList.get(position);
        holder.GUID=subGroup.getI();
        holder.subGroupName.setText(subGroup.getN() );



        holder.subGroupName.setTextSize(fontSize);

        if (subGroup.Click ){
           /* if (LauncherActivity.screenInches>=7)
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.order_item_recycle_subgroup_card_background));
            else {*/
                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.order_item_recycle_group__card_background));
                holder.subGroupName.setTextColor(context.getResources().getColor(R.color.white));
           // }
        }else if (!subGroup.Click ){
         /*   if (LauncherActivity.screenInches>=7)
                holder.cardView.setBackground(null);
            else {*/
                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_subgroup_mobile));
                holder.subGroupName.setTextColor(context.getResources().getColor(R.color.medium_color));
           // }


        }




        holder.itemView.setOnClickListener(view -> clickItem.onRowClick(holder.GUID));




    }

    @Override
    public int getItemCount() {
        return subGroupList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private String GUID;
        private final TextView subGroupName;
        private final MaterialCardView cardView;


        public viewHolder(View itemView) {
            super(itemView);

            cardView=itemView.findViewById(R.id.order_item_recycle_card);
            subGroupName=itemView.findViewById(R.id.order_item_recycle_subgroup_txt_name);







        }
    }


}






