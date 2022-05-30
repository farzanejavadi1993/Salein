package ir.kitgroup.saleindemo.ui.launcher.homeItem;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;


import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.models.ProductLevel1;


public class ProductLevel1Adapter extends RecyclerView.Adapter<ProductLevel1Adapter.viewHolder> {

    private final List<ProductLevel1> productGroupLevel1s ;
    private final Activity context;


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




            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_group_item1, parent, false));




    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final ProductLevel1 productGroupLevel1 = productGroupLevel1s.get(position);

        holder.groupName.setText(productGroupLevel1.getN());

       // setAnimation(holder.itemView);

        holder.animationView.setAnimation("animation.json");
        holder.animationView.loop(true);
        holder.animationView.setSpeed(2f);

        holder.animationView.playAnimation();






        if (productGroupLevel1.Click) {
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.group_background));
            holder.animationView.setVisibility(View.VISIBLE);
        }else {
            holder.animationView.setVisibility(View.GONE);
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_subgroup_mobile));
        }


        holder.itemView.setOnClickListener(view -> {

            clickItem.onRowClick(productGroupLevel1s.get(position).getI());
                }

        );


    }

    @Override
    public int getItemCount() {
        return productGroupLevel1s.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {



        private final TextView groupName;

        private final RelativeLayout cardView;
       private final LottieAnimationView animationView;


        public viewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.order_item_recycle_card);

            groupName = itemView.findViewById(R.id.order_item_recycle_group_txt_name);
           animationView = itemView.findViewById(R.id.animation_lottie);


        }
    }


    protected void setAnimation(View viewToAnimate) {

            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(1000));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);


    }
}






