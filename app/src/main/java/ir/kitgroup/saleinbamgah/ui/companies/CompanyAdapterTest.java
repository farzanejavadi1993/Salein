package ir.kitgroup.saleinbamgah.ui.companies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import ir.kitgroup.saleinbamgah.DataBase.Company;
import ir.kitgroup.saleinbamgah.R;


public class CompanyAdapterTest extends RecyclerView.Adapter<CompanyAdapterTest.viewHolder> {
    private final List<Company> list;



    //region Interface
    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }
    public interface ClickItem {
        void onRowClick(Company modelCompany,Boolean parent,int index,boolean delete);
    }
    private ClickItem clickItem;
    //endregion Interface


    public CompanyAdapterTest(List<Company> list) {
        this.list = list;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company_test, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {
        final Company company = list.get(position);


        holder.tvNameCompany.setText(company.getN());



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


        public viewHolder(View itemView) {
            super(itemView);
            tvNameCompany = itemView.findViewById(R.id.tvNameCompanyTest);
        }
    }
}








