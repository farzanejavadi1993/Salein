package ir.kitgroup.salein.ui.launcher.homeItem;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import ir.kitgroup.salein.R;
import ir.kitgroup.salein.models.ProductLevel1;


public class ProductLevel1Adapter extends RecyclerView.Adapter<ProductLevel1Adapter.viewHolder> {
    private final List<ProductLevel1> productGroupLevel1s;
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


        if (productGroupLevel1.Click)
            holder.view.setBackground(context.getResources().getDrawable(R.drawable.background_view));
        else
            holder.view.setBackgroundColor(context.getResources().getColor(R.color.color_primary));



        holder.itemView.setOnClickListener(view -> {
            clickItem.onRowClick(productGroupLevel1s.get(position).getI());
        });
    }
    @Override
    public int getItemCount() {
        return productGroupLevel1s.size();
    }
    static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView groupName;
        private final View view;

        public viewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.viewGroup);
            groupName = itemView.findViewById(R.id.order_item_recycle_group_txt_name);
        }
    }
}