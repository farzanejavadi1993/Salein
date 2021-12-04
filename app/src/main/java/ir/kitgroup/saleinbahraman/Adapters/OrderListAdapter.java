package ir.kitgroup.saleinbahraman.Adapters;


import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;


import java.text.DecimalFormat;

import java.util.List;
import java.util.Objects;

import ir.kitgroup.saleinbahraman.models.Invoice;
import ir.kitgroup.saleinbahraman.R;



public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.viewHolder> {


    private final List<Invoice> list;
    private final Context context;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(String description,int type, String Inv_GUID);
    }

    private ClickItem clickItem;


    public OrderListAdapter(Context context, List<Invoice> list) {
        this.context = context;
        this.list = list;



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


        holder.tvTotalPrice.setText(format.format((invoice.INV_EXTENDED_AMOUNT)) + " ریال ");


        //تاریخ تحویل
        if (invoice.INV_DUE_DATE_PERSIAN != null)
            holder.tvDeliveryDateOrder.setText(invoice.INV_DUE_TIME +"_"+invoice.INV_DUE_DATE_PERSIAN );


        //تاریخ ارسال
        holder.tvDateOrder.setText(invoice.INV_DATE_PERSIAN);


        Objects.requireNonNull(holder.itemView).setOnClickListener(view -> clickItem.onRowClick("",1, invoice.INV_UID));


        holder.ivSend.setOnClickListener(v ->{
            if (holder.edtDescription.getText().toString().equals(""))
            {
                AlertDialog alertDialog=  new AlertDialog.Builder(context)
                        .setMessage("هیچ نظری موجود نمی باشد")
                        .setPositiveButton("بستن", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();

                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                Typeface face=Typeface.createFromAsset(context.getAssets(), "iransans.ttf");
                textView.setTypeface(face);
                textView.setTextColor(context.getResources().getColor(R.color.red_table));
                textView.setTextSize(13);

                return;
            }

                    clickItem.onRowClick(holder.edtDescription.getText().toString(), 2, invoice.INV_UID);

    });

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
        private final ImageView ivSend;
        private final EditText edtDescription;



        public viewHolder(View itemView) {
            super(itemView);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);

            tvDateOrder = itemView.findViewById(R.id.tv_date_order);
            edtDescription = itemView.findViewById(R.id.edt_description);
            ivSend = itemView.findViewById(R.id.iv_send);
            tvDeliveryDateOrder = itemView.findViewById(R.id.tv_date_delivery_order);
            tvStatus = itemView.findViewById(R.id.tv_status);



        }
    }







}





