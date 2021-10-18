package ir.kitgroup.saleinOrder.Adapters;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import java.util.List;

import ir.kitgroup.saleinOrder.Fragments.MobileView.SplashScreenFragment;
import ir.kitgroup.saleinOrder.classes.App;
import ir.kitgroup.saleinOrder.DataBase.InvoiceDetail;
import ir.kitgroup.saleinOrder.DataBase.Tables;

import ir.kitgroup.saleinOrder.R;


public class TableAdapter extends RecyclerView.Adapter<TableAdapter.viewHolder> {

    private final List<Tables> tableList;
    private final Context context;



    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(String Name, boolean Reserve, String Guid);
    }

    private ClickItem clickItem;




    private ShowDialog showDialog;

    public interface ShowDialog {
        void onShow(String Inv_GUID,int position,boolean type);
    }

    public void OnclickShowDialog(ShowDialog showDialog) {
        this.showDialog = showDialog;
    }


    public TableAdapter(Context context, List<Tables> tableList) {
        this.context = context;
        this.tableList = tableList;
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_table, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {

        Tables table = tableList.get(position);



        holder.tableName.setText("میز شماره : " + table.N);
        holder.tvCapacity.setText("تعداد صندلی میز : " + table.CC);
        if (Select.from(InvoiceDetail.class).where("INVUID ='" + tableList.get(position).I + "'").list().size() > 0) {
            holder.tvStatus.setText("سفارش ذخیره شده");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.yellow_table));
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setImageResource(R.drawable.ic_delete);

        } else if (table.RSV) {
            holder.tvStatus.setText("میز رزرو شده است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.blue_table));
            holder.ivDelete.setVisibility(View.GONE);
        }
        else if (table.ACT) {
            holder.tvStatus.setText("میز مشغول است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.red_table));
            holder.ivDelete.setVisibility(View.GONE);

        } else if (!table.ACT && !table.RSV && table.GO==null) {

           Tables tb= Select.from(Tables.class).where("I ='" + table.I+ "'").first();
           if (tb!=null)
             Tables.delete(tb);
            holder.tvStatus.setText("میز خالی است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.green_table));
            holder.ivDelete.setVisibility(View.GONE);


        }else {
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
            holder.tableName.setText("سفارش بیرون بر");
            holder.tvCapacity.setText(table.GO);
            holder.tvStatus.setText(table.DATE!=null?table.DATE:"");
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setImageResource(R.drawable.ic_close);
        }



        holder.ivDelete.setOnClickListener(v -> {

            if (holder.tableName.getText().toString().equals("سفارش بیرون بر"))
        showDialog.onShow(tableList.get(position).I,position,true);
            else
                showDialog.onShow(tableList.get(position).I,position,false);

        });




        holder.itemView.setOnClickListener(view -> {
          if (!table.ACT && !table.RSV) {
                //vacant
                clickItem.onRowClick(table.N, false, table.I);

            } else if (table.RSV) {
              Toast.makeText(context, "میز رزرو شده است", Toast.LENGTH_SHORT).show();
            } else {
              Tables tb=Select.from(Tables.class).where("I ='"+table.I+"'").first();
              if (tb==null){
                  Toast.makeText(context, "دسترسی به سفارش امکان پذیر نمی باشد.", Toast.LENGTH_SHORT).show();
                  return;
              }else {
                  clickItem.onRowClick("میز مشغول است.", true, tb.INVID);
              }

            }

        });


    }

    @Override
    public int getItemCount() {

        return tableList.size();

    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout rlTable;
        private final TextView tableName;
        private final TextView tvCapacity;
        private final TextView tvStatus;
        private final ImageView ivDelete;



        public viewHolder(View itemView) {
            super(itemView);









            rlTable = itemView.findViewById(R.id.rl_table);
            ivDelete= itemView.findViewById(R.id.ivDelete);

            tableName = itemView.findViewById(R.id.tv_number_table);
            tvCapacity = itemView.findViewById(R.id.tv_capacity_table);
            tvStatus = itemView.findViewById(R.id.tv_status_table);


            if (App.mode == 1) {
                if (SplashScreenFragment.screenInches >= 7) {
                    int height;
                    if (SplashScreenFragment.width > SplashScreenFragment.height)
                        height = SplashScreenFragment.width / 2;
                    else
                        height = SplashScreenFragment.height / 2;
                    rlTable.getLayoutParams().width = (int) (height / 3.3);
                    rlTable.getLayoutParams().height = (int) (height / 4.2);


                } else {

                    int width;
                    if (SplashScreenFragment.width > SplashScreenFragment.height)
                        width = SplashScreenFragment.height / 2;
                    else
                        width = SplashScreenFragment.width / 2;

                    rlTable.getLayoutParams().width = (int) (width /1.2);
                    rlTable.getLayoutParams().height = (int) (width/1.7);

                }
            }


        }
    }


}





