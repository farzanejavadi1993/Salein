package ir.kitgroup.saleinBahraman.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.saleinBahraman.DataBase.Account;
import ir.kitgroup.saleinBahraman.R;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.viewHolder>  {

    private List<Account> accountList ;
    private Context context;
    private final Object mLock = new Object();
    private ArrayList<Account> mOriginalValues;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }






    public interface ClickItem {
        void onRowClick(Account account);
    }

    private ClickItem clickItem;


    public AccountAdapter(Context context, List<Account> accountList) {
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
        final Account account = accountList.get(position);
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





