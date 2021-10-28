package ir.kitgroup.salein.Adapters;




import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.Fragments.MobileView.SplashScreenFragment;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.models.ModelCompany;

@AndroidEntryPoint

public class CompanyAdapterList extends RecyclerView.Adapter<CompanyAdapterList.viewHolder> {


    @Inject
    private  Double ScreenSize;

    private List<ModelCompany> list ;
    private Activity context;
    private int fontSize = 0;
    private int type= 2;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(ModelCompany modelCompany,Boolean check);
    }

    private ClickItem clickItem;


    public CompanyAdapterList(Activity context, List<ModelCompany> list,int type) {
        this.context = context;
        this.list = list;
        this.type=type;

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ScreenSize>= 7) {
            fontSize = 13;
        } else {
            fontSize = 12;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final ModelCompany company = list.get(position);

        if (type==1)
            holder.checkBox.setVisibility(View.GONE);

        holder.tvTitleCompany.setText(company.NameCompany);
        holder.tvDescriptionCompany.setText(company.DESC);
        holder.ivCompany.setImageResource(company.ICON);
        holder.checkBox.setChecked(company.Check);


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    clickItem.onRowClick(company,isChecked);


            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type==1)
                    clickItem.onRowClick(company,true);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitleCompany;
        private TextView tvDescriptionCompany;
        private CircularImageView ivCompany;
        private CheckBox checkBox;


        public viewHolder(View itemView) {
            super(itemView);

            tvTitleCompany = itemView.findViewById(R.id.tvTitleCompany);
            tvDescriptionCompany = itemView.findViewById(R.id.tvDescriptionCompany);
            ivCompany= itemView.findViewById(R.id.ivCompany);
            checkBox= itemView.findViewById(R.id.checkBoxCompany);
            tvTitleCompany.setTextSize(fontSize);
            tvDescriptionCompany.setTextSize(fontSize);





        }
    }


}








