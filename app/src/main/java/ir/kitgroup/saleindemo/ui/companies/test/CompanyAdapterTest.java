package ir.kitgroup.saleindemo.ui.companies.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.R;


public class CompanyAdapterTest extends RecyclerView.Adapter<CompanyAdapterTest.viewHolder> {
    private final List<Company> list;
    private final Context context;



    //region Interface
    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }
    public interface ClickItem {
        void onRowClick(Company modelCompany);
    }
    private ClickItem clickItem;
    //endregion Interface


    public CompanyAdapterTest(List<Company> list,Context context) {
        this.list = list;
        this.context=context;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company_test, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {
        final Company company = list.get(position);


        holder.tvNameCompany.setText(company.getN());
        holder.tvDescription.setText(company.getDesc());



        if (company.click){
            holder.layoutCompany.setBackground(context.getResources().getDrawable(R.drawable.background_active_company_item));
            holder.tvNameCompany.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvDescription.setTextColor(context.getResources().getColor(R.color.white));

        }

        else {
            holder.layoutCompany.setBackground(context.getResources().getDrawable(R.drawable.background_inactive_company_item));
            holder.tvNameCompany.setTextColor(context.getResources().getColor(R.color.color_primary_very_dark));
            holder.tvDescription.setTextColor(context.getResources().getColor(R.color.color_primary_very_dark));

        }

        holder.itemView.setOnClickListener(view -> clickItem.onRowClick(company));


/*        holder.itemView.setOnClickListener(v -> {
            if (!company.Open){
                company.Open=true;
                clickItem.onRowClick(company,company.Parent!=null ? company.Parent : false,holder.getAdapterPosition(),false);

            }else {
                company.Open=false;
                clickItem.onRowClick(company,company.Parent!=null ? company.Parent : false,holder.getAdapterPosition(),true);

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private final  TextView tvNameCompany;
        private final  TextView tvDescription;
        private final ConstraintLayout layoutCompany;
        private final  View ivView;


        public viewHolder(View itemView) {
            super(itemView);
            tvNameCompany = itemView.findViewById(R.id.tvNameCompanyTest);
            layoutCompany = itemView.findViewById(R.id.layoutCompany);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivView = itemView.findViewById(R.id.ivView);
        }
    }
}








