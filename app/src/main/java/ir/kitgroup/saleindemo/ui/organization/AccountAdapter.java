package ir.kitgroup.saleindemo.ui.organization;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.kitgroup.saleindemo.DataBase.User;
import ir.kitgroup.saleindemo.R;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.viewHolder>  {

    private List<User> accountList ;
    private Context context;


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


    public interface ClickItem {
        void onRowClick(User account);
    }

    private ClickItem clickItem;


    public AccountAdapter(Context context, List<User> accountList) {
        this.context = context;
        this.accountList = accountList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item_recycle, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final User account = accountList.get(position);
        holder.GUID = account.getACCCLBUID();
        holder.accountName.setText(account.getACCCLBNAME() + "-" + account.getM());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickItem.onRowClick(account);


            }
        });


    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

     class viewHolder extends RecyclerView.ViewHolder {

        private String GUID;
        private TextView rowNumber;
        private TextView accountName;


        public viewHolder(View itemView) {
            super(itemView);

            accountName = itemView.findViewById(R.id.account_item_recycle_name);
            rowNumber = itemView.findViewById(R.id.account_item_recyclerview_row);
        }


    }


}





