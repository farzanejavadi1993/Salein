package ir.kitgroup.salein1.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.salein1.DataBase.Account;
import ir.kitgroup.salein1.R;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.viewHolder>  {

    private List<Account> accountList = new ArrayList<>();
    private Context context;
    private final Object mLock = new Object();
    private ArrayList<Account> mOriginalValues;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


   /* public void nullArray() {
        if (mOriginalValues != null)
            mOriginalValues = null;
    }*/

 /*   @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Account> tempList = new ArrayList();

                if (mOriginalValues == null) {
                    synchronized (mLock) {
                        mOriginalValues = new ArrayList(LauncherFragment.AllAccount);
                    }
                }

                if (!constraint.toString().equals("") && constraint != null && LauncherFragment.AllAccount != null) {

                    String[] tempSearch = constraint.toString().trim().split(" ");

                    int counter = 0;
                    int searchSize = tempSearch.length;
                   *//* for (String searchItem : tempSearch) {
                        ArrayList<Account> resultAcc= new ArrayList<>(mOriginalValues);
                        CollectionUtils.filter(resultAcc,r->r.getACCCLBNAME().contains(searchItem) || r.getM().contains(searchItem));
                       if(resultAcc.size()>0)
                           counter++;

                        if (counter == searchSize)
                            for (int i=0;i<20;i++){
                                if (resultAcc.size()>i){
                                    tempList.add(resultAcc.get(i));
                                }
                            }
//                            tempList.addAll(resultAcc);
                    }*//*

                    for (Account item : mOriginalValues) {

                        counter = 0;
                        for (String searchItem : tempSearch) {

                            if (item.getACCCLBNAME().contains(searchItem) ||
                                    item.getM().contains(searchItem) ||
                                    item.getM2().contains(searchItem)
                            ) {
                                counter++;
                            }
                        }

                        if (counter == searchSize)
                            tempList.add(item);
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                } else {
                    synchronized (mLock) {
                        tempList = new ArrayList(mOriginalValues);
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
             *//*   if(((String) contraint).charAt(0)==48){

                     for (int i=1;i<((String) contraint).split("0").length;i++){
                         contraint=(((String) contraint).split("0"))[i];
                     }
                }*//*
                if (MainActivity.screenInches>=7 && contraint.length()>4)
                *//*for (int i = 0; i < 10; i++) {
                    if (((ArrayList<Account>) results.values).size()>i)
                    accountList.add(((ArrayList<Account>) results.values).get(i));
                }*//* {
                    accountList = (ArrayList<Account>) results.values;
                    for (int i=20;i<accountList.size();i++){
                        if (accountList.size()>i)
                            accountList.remove(accountList.get(i));
                    }
                    notifyDataSetChanged();
                }else if (MainActivity.screenInches<7){
                    accountList = (ArrayList<Account>) results.values;
                    notifyDataSetChanged();
                }else {
                    accountList .clear();
                    notifyDataSetChanged();
                }
            }
        };
    }*/

    public interface ClickItem {
        void onRowClick(String GUID, String name);
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

                clickItem.onRowClick(account.getACCCLBUID(), account.getACCCLBNAME());


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





