package ir.kitgroup.salein.ui.companies.test;

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
import ir.kitgroup.salein.models.ProductLevel2;

public class ProductLevel2TestAdapter extends RecyclerView.Adapter<ProductLevel2TestAdapter.viewHolder> {
    private final List<ProductLevel2> subGroupList;
    private final Activity context;


    public interface ClickItem {
        void onRowClick(String GUID);
    }

    private ClickItem clickItem;

    public void SetOnItemClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public ProductLevel2TestAdapter(Activity context, List<ProductLevel2> subGroupList) {
        this.context = context;
        this.subGroupList = subGroupList;
    }


    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_level_item, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final ProductLevel2 subGroup = subGroupList.get(position);
        holder.GUID = subGroup.getI();
        holder.tvGroupLevel.setText(subGroup.getN());


        holder.itemView.setOnClickListener(view -> clickItem.onRowClick(holder.GUID));
    }

    @Override
    public int getItemCount() {
        return subGroupList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {
        private String GUID;
        private final TextView tvGroupLevel;

        public viewHolder(View itemView) {
            super(itemView);
            tvGroupLevel = itemView.findViewById(R.id.tvGroupLevel);
        }
    }

}