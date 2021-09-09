package ir.kitgroup.salein.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Locale;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.Util.Utilities;
import ir.kitgroup.salein.DataBase.Invoice;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Tables;

import ir.kitgroup.salein.R;


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

        if (table.N.equals("بیرون بر")) {
            holder.iv_bicycle.setVisibility(View.VISIBLE);
            holder.iv_bicycle.setImageResource(R.drawable.ic_bys);
            Invoice invoice = Select.from(Invoice.class).where("TBLUID ='" + table.I + "'").first();
            if (invoice != null) {
                Utilities util = new Utilities();
                Locale loc = new Locale("en_US");
                Utilities.SolarCalendar sc = util.new SolarCalendar(invoice.INV_DUE_DATE);
                String date = (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year;
                holder.tvStatus.setText(date);


                holder.tableName.setText(invoice.Acc_name);
                holder.tvCapacity.setText("سفارش بیرون بر");
            }

            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.orange_light));

        } else {
            holder.iv_bicycle.setVisibility(View.GONE);
            holder.tableName.setVisibility(View.VISIBLE);
            holder.tableName.setText("میز شماره : " + table.N);
            holder.tvCapacity.setText("تعداد صندلی میز : " + table.CC);


        }


        if (!table.N.equals("بیرون بر") && !table.ACT && !table.RSV) {
            holder.iv_bicycle.setVisibility(View.GONE);
            holder.tvStatus.setText("میز خالی است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.green_table));

            try {
                Invoice ord3 = Select.from(Invoice.class).where("TBLUID ='" + table.I + "'").first();
                String guid = ord3.INV_UID;
                ord3.delete();
                for (InvoiceDetail ordDetail : Select.from(InvoiceDetail.class).where("INVUID = '" + guid + "'").list()) {
                    InvoiceDetail.deleteInTx(ordDetail);
                }
            } catch (Exception ignored) {

            }


        } else if (!table.N.equals("بیرون بر") && table.RSV) {
            holder.iv_bicycle.setVisibility(View.GONE);
            holder.tvStatus.setText("میز رزرو شده است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.blue_table));


        } else if (!table.N.equals("بیرون بر")) {
            holder.iv_bicycle.setVisibility(View.GONE);
            holder.tvStatus.setText("میز مشغول است.");
            holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.red_table));


        }


        holder.itemView.setOnClickListener(view -> {

            if (!table.ACT && !table.RSV) {
                //vacant
                clickItem.onRowClick(table.N, false, table.I);

            } else if (table.RSV) {
                clickItem.onRowClick("میز رزرو شده است", true, table.I);
            } else {
                clickItem.onRowClick("میز مشغول است.", true, table.I);
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
        private final ImageView iv_bicycle;


        public viewHolder(View itemView) {
            super(itemView);

            rlTable = itemView.findViewById(R.id.rl_table);
            iv_bicycle = itemView.findViewById(R.id.iv_table);
            tableName = itemView.findViewById(R.id.tv_number_table);
            tvCapacity = itemView.findViewById(R.id.tv_capacity_table);
            tvStatus = itemView.findViewById(R.id.tv_status_table);


            if (App.mode == 1) {
                if (LauncherActivity.screenInches >= 7) {
                    int height;
                    if (LauncherActivity.width > LauncherActivity.height)
                        height = LauncherActivity.width / 2;
                    else
                        height = LauncherActivity.height / 2;
                    rlTable.getLayoutParams().width = (int) (height / 3.3);
                    rlTable.getLayoutParams().height = (int) (height / 4.2);


                } else {

                    int width;
                    if (LauncherActivity.width > LauncherActivity.height)
                        width = LauncherActivity.height / 2;
                    else
                        width = LauncherActivity.width / 2;

                    rlTable.getLayoutParams().width = (int) (width / 1.3);
                    rlTable.getLayoutParams().height = (int) (width / 1.8);

                }
            }


        }
    }


}





