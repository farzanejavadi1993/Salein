package ir.kitgroup.saleinkonj.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import java.util.List;


import ir.kitgroup.saleinkonj.DataBase.InvoiceDetail;

import ir.kitgroup.saleinkonj.DataBase.Tables;
import ir.kitgroup.saleinkonj.R;
import ir.kitgroup.saleinkonj.classes.Util;


public class TableAdapter extends RecyclerView.Adapter<TableAdapter.viewHolder> {

    private final List<Tables> tableList;

    private final Activity context;


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(boolean Organization,String Name, boolean Reserve, String Tbl_Guid, String Inv_Guid);
    }

    private ClickItem clickItem;


    private ShowDialog showDialog;

    public interface ShowDialog {
        void onShow(String Inv_GUID, int position, boolean type);
    }

    public void OnclickShowDialog(ShowDialog showDialog) {
        this.showDialog = showDialog;
    }


    public TableAdapter(Activity context, List<Tables> tableList) {
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


        if (table.ACT) {
            holder.tvStatus.setText("میز مشغول است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.red_table));
            holder.ivDelete.setVisibility(View.GONE);

        } else if (table.RSV) {
            holder.tvStatus.setText("میز رزرو شده است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.blue_table));
            holder.ivDelete.setVisibility(View.GONE);
        } else if (Select.from(InvoiceDetail.class).where("INVUID ='" + tableList.get(position).I + "'").list().size() > 0
                && table.GO == null
        ) {
            holder.tvStatus.setText("سفارش ذخیره شده");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.yellow_table));
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setImageResource(R.drawable.ic_delete);

        } else if (!table.ACT && !table.RSV && table.GO == null) {
            Tables tb = Select.from(Tables.class).where("I ='" + table.I + "'").first();
            if (tb != null)
                Tables.delete(tb);
            holder.tvStatus.setText("میز خالی است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.green_table));
            holder.ivDelete.setVisibility(View.GONE);


        } else {
            holder.tableName.setText( table.N);
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.orange_light));
            holder.tvCapacity.setText(table.GO);
            holder.tvStatus.setText(table.DATE != null ? table.DATE : "");
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setImageResource(R.drawable.ic_close);
        }


        holder.ivDelete.setOnClickListener(v -> showDialog.onShow(tableList.get(position).I, position, table.GO != null && table.C != null));


        holder.itemView.setOnClickListener(view -> {
            if (table.GO != null) {
                //getOut
                clickItem.onRowClick(false,table.N, true, table.I, table.INVID);

            } else if (!table.ACT && !table.RSV) {
                //vacant
                clickItem.onRowClick(true,table.N, false, table.I, table.I);

            } else if (table.RSV) {
                Toast.makeText(context, "میز رزرو شده است", Toast.LENGTH_SHORT).show();
            } else {
                Tables tb = Select.from(Tables.class).where("I ='" + table.I + "'").first();
                if (tb == null) {
                    Toast.makeText(context, "دسترسی به سفارش امکان پذیر نمی باشد.", Toast.LENGTH_SHORT).show();
                } else {
                    clickItem.onRowClick(true,table.N, true, table.I, tb.INVID);
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
            ivDelete = itemView.findViewById(R.id.ivDelete);

            tableName = itemView.findViewById(R.id.tv_number_table);
            tvCapacity = itemView.findViewById(R.id.tv_capacity_table);
            tvStatus = itemView.findViewById(R.id.tv_status_table);

            double height ;
            if (Util.screenSize >= 7) {
                 /*  double height;
                   if (Util.width > Util.height)
                       height = Util.width / 2;
                   else*/
                height = Util.height / 2;
                rlTable.getLayoutParams().width = (int) (height / 3.3);
                rlTable.getLayoutParams().height = (int) (height / 4.2);


            } else {

                height = Util.width / 2;
                rlTable.getLayoutParams().width = (int) (height / 1.2);
                rlTable.getLayoutParams().height = (int) (height / 1.7);

            }


        }
    }


}





