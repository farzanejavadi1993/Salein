package ir.kitgroup.saleindemo.ui.launcher.homeItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.models.ProductLevel2;
public class ProductLevel2Adapter extends RecyclerView.Adapter<ProductLevel2Adapter.viewHolder> {
    private final List<ProductLevel2> subGroupList;
    private final Activity context;


    public interface ClickItem {
        void onRowClick(String GUID);
    }

    private ClickItem clickItem;

    public void SetOnItemClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
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
        holder.GUID = subGroup.getI();
        holder.subGroupName.setText(subGroup.getN());

        holder.animationView.setAnimation("animation.json");
        holder.animationView.loop(true);
        holder.animationView.setSpeed(2f);
        holder.animationView.playAnimation();

        if (subGroup.Click) {

            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.order_item_recycle_group__card_background));
            holder.subGroupName.setTextColor(context.getResources().getColor(R.color.color_text_screen));
            holder.animationView.setVisibility(View.VISIBLE);

        } else if (!subGroup.Click) {
            holder.animationView.setVisibility(View.GONE);
            holder.cardView.setBackground(null);
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
        private final LottieAnimationView animationView;

        public viewHolder(View itemView) {
            super(itemView);
            animationView = itemView.findViewById(R.id.animation_lottie);
            cardView = itemView.findViewById(R.id.order_item_recycle_card);
            subGroupName = itemView.findViewById(R.id.order_item_recycle_subgroup_txt_name);
        }
    }

}