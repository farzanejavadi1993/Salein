package ir.kitgroup.saleindemo.ui.companies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;



public class CompanyAdapterList extends RecyclerView.Adapter<CompanyAdapterList.viewHolder> {
    private final List<Company> list ;
    private int fontSize = 0;
    private final int type;//1 from Company    2 from MyCompany

    //region Interface
    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }
    public interface ClickItem {
        void onRowClick(Company modelCompany,Boolean parent,int index,boolean delete);
    }
    private ClickItem clickItem;
    //endregion Interface

    public CompanyAdapterList(List<Company> list, int type) {
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

        Picasso.get()
                .load("http://api.kitgroup.ir/GetCompanyImage?id=" +
                        company.I+"&width=120&height=120")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(holder.ivCompany);

        holder.tvTitleCompany.setText(company.N);

        holder.tvDescriptionCompany.setText(company.DESC);

        if (company.Parent!=null && company.Parent)
            holder.flesh.setVisibility(View.VISIBLE);
        else
            holder.flesh.setVisibility(View.GONE);

        if (!company.PI.equals("")) {
            holder.cardSalein.setRadius(0);
            holder.view.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.cardSalein.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
        }else {
            holder.cardSalein.setRadius(50);
            holder.view.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.cardSalein.getLayoutParams();
            layoutParams.setMargins(50, 15, 50, 15);
        }
        holder.cardSalein.requestLayout();

        if (company.Open)
            holder.flesh.setRotation(270);
        else
            holder.flesh.setRotation(90);


        holder.itemView.setOnClickListener(v -> {
            if (!company.Open){
                company.Open=true;
                clickItem.onRowClick(company,company.Parent!=null ? company.Parent : false,holder.getAdapterPosition(),false);
                holder.flesh.setRotation(270);
            }else {
                company.Open=false;
                clickItem.onRowClick(company,company.Parent!=null ? company.Parent : false,holder.getAdapterPosition(),true);
                holder.flesh.setRotation(90);
            }
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
        private final ImageView flesh;
        private final MaterialCardView cardSalein;
        private final View view;

        public viewHolder(View itemView) {
            super(itemView);
            tvTitleCompany = itemView.findViewById(R.id.tvTitleCompany);
            cardSalein = itemView.findViewById(R.id.cardSalein);
            view = itemView.findViewById(R.id.view);
            tvDescriptionCompany = itemView.findViewById(R.id.tvDescriptionCompany);
            ivCompany= itemView.findViewById(R.id.ivCompany);
            favoriteCompany = itemView.findViewById(R.id.favoriteCompany);
            flesh = itemView.findViewById(R.id.flesh);
            tvTitleCompany.setTextSize(fontSize);
            tvDescriptionCompany.setTextSize(fontSize);
        }
    }
}








