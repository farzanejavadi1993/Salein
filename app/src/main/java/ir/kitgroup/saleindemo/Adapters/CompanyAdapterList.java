package ir.kitgroup.saleindemo.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.github.siyamed.shapeimageview.CircularImageView;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.DataBase.Company;


public class CompanyAdapterList extends RecyclerView.Adapter<CompanyAdapterList.viewHolder> {

    private final List<Company> list ;
    private int fontSize = 0;
    private final int type;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(Company modelCompany,Boolean check);
    }

    private ClickItem clickItem;


    public CompanyAdapterList( List<Company> list,int type) {
        this.list = list;
        this.type=type;

    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (Util.screenSize >= 7) {
            fontSize = 13;
        } else {
            fontSize = 12;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {
        final Company company = list.get(position);

        if (type==1)
            holder.favoriteCompany.setVisibility(View.GONE);

        holder.tvTitleCompany.setText(company.N);
        holder.tvDescriptionCompany.setText(company.Description);
        holder.ivCompany.setImageResource(company.imageLogo);





        holder.itemView.setOnClickListener(v -> {

                clickItem.onRowClick(company,true);
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {


        private final  TextView tvTitleCompany;
        private final  TextView tvDescriptionCompany;
        private final  CircularImageView ivCompany;
        private final ImageView favoriteCompany;


        public viewHolder(View itemView) {
            super(itemView);

            tvTitleCompany = itemView.findViewById(R.id.tvTitleCompany);
            tvDescriptionCompany = itemView.findViewById(R.id.tvDescriptionCompany);
            ivCompany= itemView.findViewById(R.id.ivCompany);
            favoriteCompany = itemView.findViewById(R.id.favoriteCompany);
            tvTitleCompany.setTextSize(fontSize);
            tvDescriptionCompany.setTextSize(fontSize);





        }
    }


}








