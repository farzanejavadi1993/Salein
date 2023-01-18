package ir.kitgroup.saleinfingilkabab.ui.companies.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ir.kitgroup.saleinfingilkabab.R;
import ir.kitgroup.saleinfingilkabab.models.ProductLevel1;


public class ProductLevel1TestAdapter extends RecyclerView.Adapter<ProductLevel1TestAdapter.viewHolder> {
    private final List<ProductLevel1> productGroupLevel1s;
    private final Activity context;

    public interface ClickItem {
        void onRowClick(String GUID);
    }

    private ClickItem clickItem;

    public void SetOnItemClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


    public ProductLevel1TestAdapter(Activity context, List<ProductLevel1> groupList) {
        this.context = context;
        this.productGroupLevel1s = groupList;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_level_item, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final ProductLevel1 productGroupLevel1 = productGroupLevel1s.get(position);

        holder.tvGroupLevel.setText(productGroupLevel1.getN());

        holder.itemView.setOnClickListener(view -> {
            clickItem.onRowClick(productGroupLevel1s.get(position).getI());
        });
    }
    @Override
    public int getItemCount() {
        return productGroupLevel1s.size();
    }
    static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGroupLevel;


        public viewHolder(View itemView) {
            super(itemView);

            tvGroupLevel = itemView.findViewById(R.id.tvGroupLevel);
        }
    }
}