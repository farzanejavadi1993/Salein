package ir.kitgroup.saleinjam.Adapters;





import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;


import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.classes.Util;
import ir.kitgroup.saleinjam.models.Config;


public class CompanyAdapterList extends RecyclerView.Adapter<CompanyAdapterList.viewHolder> {

    private final List<Company> list ;
    private int fontSize = 0;
    private final int type;
    private final Config config;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(Company modelCompany,Boolean check);
    }

    private ClickItem clickItem;


    public CompanyAdapterList(List<Company> list, int type, Config config) {
        this.list = list;
        this.type=type;
        this.config=config;

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
        holder.tvDescriptionCompany.setText(company.DESC);


        Picasso.get()
                .load("http://" + config.IP1 + "/GetCompanyImage?id=" +
                        company.I+"&width=100&height=100")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(holder.ivCompany);


       ArrayList<Company> companies=new ArrayList<>(list);
       CollectionUtils.filter(companies, r -> company.I.equals(r.PI));
       if (companies.size()>0)
           holder.flesh.setVisibility(View.VISIBLE);


        ArrayList<Company> companies1=new ArrayList<>(list);
        CollectionUtils.filter(companies1, r -> !r.PI.equals("") && r.PI.equals(company.I));
        if (companies1.size()>0)
            holder.cardSalein.setVisibility(View.GONE);


//        ArrayList<Company> companies1=new ArrayList<>(list);
//        CollectionUtils.filter(companies1, r -> company.PI.equals(r.I));
//        if (companies1.size()>0)
//            holder.itemView.setVisibility(View.GONE);







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
        private final ImageView flesh;
        private final MaterialCardView cardSalein;


        public viewHolder(View itemView) {
            super(itemView);

            tvTitleCompany = itemView.findViewById(R.id.tvTitleCompany);
            cardSalein = itemView.findViewById(R.id.cardSalein);
            tvDescriptionCompany = itemView.findViewById(R.id.tvDescriptionCompany);
            ivCompany= itemView.findViewById(R.id.ivCompany);
            favoriteCompany = itemView.findViewById(R.id.favoriteCompany);
            flesh = itemView.findViewById(R.id.flesh);
            tvTitleCompany.setTextSize(fontSize);
            tvDescriptionCompany.setTextSize(fontSize);






        }
    }


}








