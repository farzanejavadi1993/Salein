package ir.kitgroup.salein.ui.launcher.moreItem.orders;


import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import java.text.DecimalFormat;

import java.util.List;
import java.util.Objects;


import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.models.Invoice;
import ir.kitgroup.salein.R;



public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.viewHolder> {


    private final List<Invoice> list;
    private final Activity context;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(String description,int type, String Inv_GUID);
    }

    private ClickItem clickItem;


    public OrderListAdapter(Activity context, List<Invoice> list) {
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
//1 در انتظار تایید
//2 تایید شده
//3 در حال ارسال
//4 تحویل شده

        if (invoice.INV_STEP==3) {
            holder.tvStatus.setText("آماده سازی برای ارسال");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
        }
       else if (invoice.INV_STEP==4) {
            holder.tvStatus.setText("ارسال شده");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else if (invoice.INV_STEP==1) {
            holder.tvStatus.setText("در انتظار بررسی اپراتور");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.blue600));
        } else if (invoice.INV_STEP==2){
            holder.tvStatus.setText("تایید شده");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.pink));
        }


        holder.tvTotalPrice.setText(format.format((invoice.INV_EXTENDED_AMOUNT)) + " ریال ");

       holder.edtDescription.setText("");
        //تاریخ تحویل
        if (invoice.INV_DUE_DATE_PERSIAN != null)
            holder.tvDeliveryDateOrder.setText(invoice.INV_DUE_TIME +"_"+invoice.INV_DUE_DATE_PERSIAN );


        //تاریخ ارسال
        holder.tvDateOrder.setText(invoice.INV_DATE_PERSIAN);


        if (invoice.invNumber==null)
            holder.tvNumberFactor.setVisibility(View.GONE);
        else
            holder.tvNumberFactor.setVisibility(View.VISIBLE);

        holder.tvNumberFactor.setText(invoice.invNumber!=null ?"شماره فیش : " + invoice.invNumber:"");


        if (invoice.INV_DEFAULT_ADDRESS==null)
            holder.layoutAddress.setVisibility(View.GONE);
        else {
            holder.layoutAddress.setVisibility(View.VISIBLE);

            Account account= Select.from(Account.class).first();
            if (account!=null&&invoice.INV_DEFAULT_ADDRESS.equals("1"))
                holder.tvAddress.setText(account.getAdr());
            else if (account!=null&&invoice.INV_DEFAULT_ADDRESS.equals("2"))
                holder.tvAddress.setText(account.getAdr2());
            else
                holder.layoutAddress.setVisibility(View.GONE);

        }



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

             setStyleTextAlert(alertDialog);

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
        private final TextView tvAddress;
        private final TextView tvNumberFactor;
        private final TextView tvDeliveryDateOrder;
        private final ImageView ivSend;
        private final EditText edtDescription;
        private final RelativeLayout layoutAddress;



        public viewHolder(View itemView) {
            super(itemView);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);

            tvDateOrder = itemView.findViewById(R.id.tv_date_order);
            layoutAddress = itemView.findViewById(R.id.layout_address);
            edtDescription = itemView.findViewById(R.id.edt_description);
            ivSend = itemView.findViewById(R.id.iv_send);
            tvDeliveryDateOrder = itemView.findViewById(R.id.tv_date_delivery_order);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvAddress = itemView.findViewById(R.id.txtAddress);
            tvNumberFactor = itemView.findViewById(R.id.tv_number_factor);



        }
    }


    private void setStyleTextAlert(AlertDialog alert){
        Typeface face = Typeface.createFromAsset(context.getAssets(), "iransans.ttf");

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTypeface(face);
        textView.setTextColor(context.getResources().getColor(R.color.medium_color));
        textView.setTextSize(13);
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.color_accent));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.color_accent));

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(face);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(face);

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(12);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(12);
    }




}





