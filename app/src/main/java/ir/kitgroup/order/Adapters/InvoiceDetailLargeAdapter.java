package ir.kitgroup.order.Adapters;




import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.collections4.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import ir.kitgroup.order.Activities.Classes.LauncherActivity;
import ir.kitgroup.order.DataBase.InvoiceDetail;
import ir.kitgroup.order.DataBase.Product;
import ir.kitgroup.order.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.order.Fragments.TabletView.OrderFragment;

import ir.kitgroup.order.R;
import ir.kitgroup.order.Util.Util;


public class InvoiceDetailLargeAdapter extends RecyclerView.Adapter<InvoiceDetailLargeAdapter.viewHolder> {


    private List<InvoiceDetail> invoicedetails = new ArrayList<>();
    private Context context;
    private int fontSize = 0;
    private String type;
    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");



    public void SetOnEditItem(EditItem editItem) {
        this.editItem = editItem;
    }

    public interface EditItem {
        void onRowEdit();
    }

    private EditItem editItem;



    public InvoiceDetailLargeAdapter(Context context, List<InvoiceDetail> orderDetailList, String type) {
        this.context = context;
        this.invoicedetails = orderDetailList;
        this.type=type;

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (LauncherActivity.screenInches >= 7) {
            fontSize = 13;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_detail, parent, false);


        } else {
            fontSize = 12;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_detail_mobile, parent, false);


        }
         return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final InvoiceDetail invoicedetail = invoicedetails.get(position);

        GradientDrawable bgLight = new GradientDrawable();
        bgLight.setColor(0xFFECE4E4);

        GradientDrawable bgDark = new GradientDrawable();
        bgDark.setColor(0xFFEEEEEE);



        if (type.equals("1")){
            holder.ivDelete.setImageBitmap(null);
            holder.edtAmount.setEnabled(false);
            holder.tvDescription.setEnabled(false);
        }else {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.edtAmount.setEnabled(true);
            holder.tvDescription.setEnabled(true);
        }



       holder.rst = new ArrayList<>();
        holder.rst.addAll(Util.AllProduct);
        CollectionUtils.filter(holder.rst, r -> r.getPRDUID().equals(invoicedetail.PRD_UID));

        holder.tvRow.setText(String.valueOf(holder.getAdapterPosition()+1));
        if (holder.rst.size() > 0) {


            if (holder.rst.get(0).PERC_DIS!= 0.0) {

                holder.tvDiscountPercent .setText(format.format(holder.rst.get(0).PERC_DIS) + "%");
                holder.tvDiscountPercent.setTextColor(context.getResources().getColor(R.color.invite_friend_color));


            } else {
                holder.tvDiscountPercent.setText("0");
                holder.tvDiscountPercent.setTextColor(context.getResources().getColor(R.color.medium_color));
            }


            holder.tvName.setText(holder.rst.get(0).getPRDNAME());
            holder.tvPrice.setText(format.format(holder.rst.get(0).getPRDPRICEPERUNIT1()));

        }
        if (holder.textWatcher == null) {
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String s = LauncherOrganizationFragment.toEnglishNumber(charSequence.toString());
                    Double amount = 0.0;
                    if (!s.equals("")) {
                        amount = Double.parseDouble(s);
                    }
                    Double sumprice = (amount * holder.rst.get(0).getPRDPRICEPERUNIT1());
                    holder.tvSumPrice.setText(format.format(sumprice));

                    Double discountPrice = sumprice * holder.rst.get(0).PERC_DIS / 100;
                    Double totalPrice = sumprice - discountPrice;
                    holder.tvSumPurePrice.setText(format.format(totalPrice));

                    if (LauncherActivity.screenInches>=7){
                        OrderFragment.invoiceDetailList.get(holder.getAdapterPosition()).INV_DET_QUANTITY=amount;
                        OrderFragment.invoiceDetailList.get(holder.getAdapterPosition()).INV_DET_TOTAL_AMOUNT=String.valueOf(totalPrice);
                        OrderFragment.invoiceDetailList.get(holder.getAdapterPosition()).INV_DET_DISCOUNT=String.valueOf(discountPrice);
                        OrderFragment.invoiceDetailAdapter.notifyDataSetChanged();
                    }





                    ArrayList<Product> resultPrd = new ArrayList<>();
                    resultPrd.addAll(Util.AllProduct);
                    CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(invoicedetail.PRD_UID));
                    if (resultPrd.size() > 0) {
                        Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);
                       if (LauncherActivity.screenInches>=7){
                           if (OrderFragment.productList.indexOf(resultPrd.get(0)) >= 0) {
                               OrderFragment.productAdapter.notifyItemChanged(OrderFragment.productList.indexOf(resultPrd.get(0)));
                           }
                       }else {
                           /*if (MainOrderMobileFragment.productList.indexOf(resultPrd.get(0)) >= 0) {
                               MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultPrd.get(0)));
                           }*/
                       }

                    }




                    editItem.onRowEdit();











                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };


        }

        if (holder.textWatcherDescription == null) {
            holder.textWatcherDescription = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (LauncherActivity.screenInches>=7){
                        OrderFragment.invoiceDetailList.get(holder.getAdapterPosition()).INV_DET_DESCRIBTION= charSequence.toString();
                    }/*else {
                        MainOrderMobileFragment.invoiceDetailList.get(holder.getAdapterPosition()).INV_DET_DESCRIBTION= charSequence.toString();
                    }*/


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };


        }

        holder.tvDescription.addTextChangedListener(holder.textWatcherDescription);


        if (invoicedetail.INV_DET_DESCRIBTION != null && !invoicedetail.INV_DET_DESCRIBTION.equals("") ) {
            holder.rlDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.removeTextChangedListener(holder.textWatcherDescription);
            holder.tvDescription.setText(invoicedetail.INV_DET_DESCRIBTION);
            holder.tvDescription.addTextChangedListener(holder.textWatcherDescription);

        }else {
            if (type.equals("1"))
            holder.rlDescription.setVisibility(View.GONE);
            holder.tvDescription.removeTextChangedListener(holder.textWatcherDescription);
            holder.tvDescription.setText("");
            holder.tvDescription.addTextChangedListener(holder.textWatcherDescription);

        }






        double sumPrice1=Float.parseFloat(invoicedetail.INV_DET_PRICE_PER_UNIT)*invoicedetail.INV_DET_QUANTITY;
        Double discountPrice =.0;
        try {
             discountPrice = sumPrice1 * holder.rst.get(0).PERC_DIS / 100;
        }catch (Exception e){

        }

        holder.tvSumDiscountPrice.setText(format.format(discountPrice));
        holder.tvSumPrice.setText(format.format(sumPrice1));
        holder.tvSumPurePrice.setText(format.format(Float.parseFloat(invoicedetail.INV_DET_TOTAL_AMOUNT)));

        holder.edtAmount.removeTextChangedListener(holder.textWatcher);
        if (invoicedetail.INV_DET_QUANTITY == 0.0) {
            holder.edtAmount.setText("");
        } else {
            holder.edtAmount.setText(format.format(invoicedetail.INV_DET_QUANTITY));
        }

        holder.edtAmount.addTextChangedListener(holder.textWatcher);


        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivDelete.setVisibility(View.GONE);
                holder.ivDelete.setEnabled(false);
                ArrayList<InvoiceDetail> result = new ArrayList<>();
                if (LauncherActivity.screenInches>=7){
                    result.addAll(OrderFragment.invoiceDetailList);
                }/*else {
                    result.addAll(MainOrderMobileFragment.invoiceDetailList);
                }*/

                CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoicedetails.get(holder.getAdapterPosition()).PRD_UID));
                if (result.size() > 0) {
                    ArrayList<Product> resultPrd_ = new ArrayList<>();
                    resultPrd_.addAll(Util.AllProduct);

                    CollectionUtils.filter(resultPrd_, r -> r.getPRDUID().equals(invoicedetails.get(holder.getAdapterPosition()).PRD_UID));
                    if (resultPrd_.size() > 0) {
                        Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd_.get(0))).setAmount(0.0);
                       if (LauncherActivity.screenInches>=7){
                           if (OrderFragment.productList.indexOf(resultPrd_.get(0)) >= 0) {
                               OrderFragment.productAdapter.notifyItemChanged(OrderFragment.productList.indexOf(resultPrd_.get(0)));
                           }
                       }/*else {
                           if (MainOrderMobileFragment.productList.indexOf(resultPrd_.get(0)) >= 0) {
                               MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultPrd_.get(0)));
                           }
                       }*/

                    }
                    if (LauncherActivity.screenInches>=7)
                    OrderFragment.invoiceDetailAdapter.notifyItemRemoved(OrderFragment.invoiceDetailList.indexOf(result.get(0)));


                }
                notifyItemRemoved(holder.getAdapterPosition());
                //invoicedetails.remove(holder.getAdapterPosition());

                holder.ivDelete.setEnabled(true);
                holder.ivDelete.setVisibility(View.VISIBLE);
                editItem.onRowEdit();
            }
        });


        if (position % 2 == 0) {
            holder.card.setBackground(bgDark);
        } else {
            holder.card.setBackground(bgLight);
        }

    }

    @Override
    public int getItemCount() {
        return invoicedetails.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {



        private ArrayList<Product> rst;
        private TextWatcher textWatcher;
        private TextWatcher textWatcherDescription;
        private TextView tvRow;
        private LinearLayout card;
        private TextView tvName;
        private EditText edtAmount;
        private TextView tvPrice;
        private TextView tvDiscountPercent;
        private TextView tvSumDiscountPrice;
        private TextView tvSumPrice;
        private TextView tvSumPurePrice;
        private EditText tvDescription;
        private ImageView ivDelete;
        private RelativeLayout rlDescription;





        public viewHolder(View itemView) {
            super(itemView);


            card = itemView.findViewById(R.id.card_detail_invoice);
            tvRow = itemView.findViewById(R.id.tv_row_order);
            tvName = itemView.findViewById(R.id.tv_name_order);
            edtAmount = itemView.findViewById(R.id.tv_amount_order);
            tvPrice = itemView.findViewById(R.id.tv_price_order);
            tvDiscountPercent = itemView.findViewById(R.id.tv_discount_percent_order);
            tvSumDiscountPrice = itemView.findViewById(R.id.tv_discount_price_order);
            tvSumPrice = itemView.findViewById(R.id.tv_sum_price_order);
            tvSumPurePrice = itemView.findViewById(R.id.tv_sum_pure_price_order);
            tvDescription = itemView.findViewById(R.id.tv_description_order);
            ivDelete = itemView.findViewById(R.id.v_delete_order);

            rlDescription = itemView.findViewById(R.id.rl_description_order);

            tvRow.setTextSize(fontSize);
            tvName.setTextSize(fontSize);
            edtAmount.setTextSize(fontSize);
            tvPrice.setTextSize(fontSize);
            tvSumDiscountPrice.setTextSize(fontSize);
            tvDiscountPercent.setTextSize(fontSize);
            tvSumPrice.setTextSize(fontSize);
            tvSumPurePrice.setTextSize(fontSize);
            tvDescription.setTextSize(fontSize);






        }
    }




}








