package ir.kitgroup.saleinOrder.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ir.kitgroup.saleinOrder.models.Invoice;
import ir.kitgroup.saleinOrder.R;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.viewHolder> {


    private final List<Invoice> list;
    private final Context context;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    private final int type;

    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(int type, String Inv_GUID);
    }

    private ClickItem clickItem;


    public OrderListAdapter(Context context, List<Invoice> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;


    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item_mobile, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {


        Invoice invoice = list.get(holder.getAdapterPosition());

        if (invoice.invFinalStatusControl) {
            holder.tvStatus.setText("آماده سازی برای ارسال");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
        }
       else if (invoice.INV_SYNC.equals("*")) {
            holder.tvStatus.setText("ارسال شده");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green_table));
        } else if (invoice.INV_SYNC.equals("-")) {
            holder.tvStatus.setText("در انتظار بررسی اپراتور");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.blue600));
        } else {
            holder.tvStatus.setText("ناموفق");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red_table));
        }


        holder.tvTotalPrice.setText(format.format((invoice.INV_EXTENDED_AMOUNT)));


        //تاریخ تحویل
        if (invoice.INV_DUE_DATE_PERSIAN != null)
            holder.tvDeliveryDateOrder.setText(invoice.INV_DUE_DATE_PERSIAN + "  " + invoice.INV_DUE_TIME);


        //تاریخ ارسال
        holder.tvDateOrder.setText(invoice.INV_DATE_PERSIAN);


        holder.itemView.setOnClickListener(view -> {

            clickItem.onRowClick(type, invoice.INV_UID);

        });


        holder.btnShow.setOnClickListener(v -> clickItem.onRowClick(type, invoice.INV_UID));

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTotalPrice;
        private final TextView tvDateOrder;
        private final TextView tvStatus;
        private final TextView tvDeliveryDateOrder;
        private final MaterialButton btnShow;


        public viewHolder(View itemView) {
            super(itemView);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);

            tvDateOrder = itemView.findViewById(R.id.tv_date_order);
            tvDeliveryDateOrder = itemView.findViewById(R.id.tv_date_delivery_order);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnShow = itemView.findViewById(R.id.btn_show);


        }
    }

    private Date stringToDate(String aDate) {


        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }

}





