package ir.kitgroup.saleinfingilkabab.ui.companies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import ir.kitgroup.saleinfingilkabab.DataBase.Company;
import ir.kitgroup.saleinfingilkabab.R;
import ir.kitgroup.saleinfingilkabab.classes.Util;



public class CompanyAdapterList extends RecyclerView.Adapter<CompanyAdapterList.viewHolder> {
    private final List<Company> list ;

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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {
        final Company company = list.get(position);
        if (type==1)
            holder.favoriteCompany.setVisibility(View.GONE);

        Picasso.get()
                .load(Util.Main_Url_IMAGE +"/GetCompanyImage?id=" +
                        company.getI()+"&width=120&height=120")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(holder.ivCompany);

        holder.tvTitleCompany.setText(company.getN());

        holder.tvDescriptionCompany.setText(company.getDesc());

        if (company.Parent!=null && company.Parent)
            holder.flesh.setVisibility(View.VISIBLE);
        else
            holder.flesh.setVisibility(View.GONE);

            holder.view.setVisibility(View.VISIBLE);


        if (!company.getPi().equals(""))
            holder.cardSalein.setBackgroundResource(R.color.launcher_linear_order_header_color);
        else
            holder.cardSalein.setBackgroundResource(R.color.white);


        holder.cardSalein.requestLayout();




        holder.itemView.setOnClickListener(v -> {
            if (!company.click){
                company.click =true;
                clickItem.onRowClick(company,company.Parent!=null ? company.Parent : false,holder.getAdapterPosition(),false);

            }else {
                company.click =false;
                clickItem.onRowClick(company,company.Parent!=null ? company.Parent : false,holder.getAdapterPosition(),true);

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
        private final RelativeLayout cardSalein;
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

        }
    }
}








