package ir.kitgroup.saleindemo.ui.launcher.homeItem;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.models.ProductLevel2;

public class ProductLevel2Adapter extends RecyclerView.Adapter<ProductLevel2Adapter.viewHolder> {


    private final List<ProductLevel2> subGroupList ;
    private final Activity context;

    public interface ClickItem {
        void onRowClick(String GUID);
    }

    private ClickItem clickItem;

    public void SetOnItemClickListener(ClickItem clickItem){
        this.clickItem=clickItem;
    }



    public ProductLevel2Adapter(Activity context, List<ProductLevel2> subGroupList) {
        this.context = context;
        this.subGroupList = subGroupList;

    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_subgroup_item_mobile, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final ProductLevel2 subGroup = subGroupList.get(position);
        holder.GUID=subGroup.getI();
        holder.subGroupName.setText(subGroup.getN() );


      //  setAnimation(holder.itemView);



        if (subGroup.Click ){

                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.order_item_recycle_group__card_background));
                holder.subGroupName.setTextColor(context.getResources().getColor(R.color.white));

        }else if (!subGroup.Click ){

                holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_subgroup_mobile));
                holder.subGroupName.setTextColor(context.getResources().getColor(R.color.medium_color));



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
    protected void setAnimation(View viewToAnimate) {

        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(new Random().nextInt(1000));//to make duration random number between [0,501)
        viewToAnimate.startAnimation(anim);


    }

}






