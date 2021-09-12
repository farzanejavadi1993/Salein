package ir.kitgroup.saleinorder.Adapters;


import android.annotation.SuppressLint;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import ir.kitgroup.saleinorder.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinorder.DataBase.InvoiceDetail;
import ir.kitgroup.saleinorder.DataBase.Product;

import ir.kitgroup.saleinorder.Fragments.TabletView.OrderFragment;
import ir.kitgroup.saleinorder.R;
import ir.kitgroup.saleinorder.Util.Util;


public class InvoiceDetailTabletAdapter extends RecyclerView.Adapter<InvoiceDetailTabletAdapter.viewHolder> {


    private List<InvoiceDetail> invoicedetails = new ArrayList<>();
    private Context context;
    private int fontSize = 0;
    private String type;
    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");


    public interface DeleteItem {
        void onDelete(String GUID);
    }

    private InvoiceDetailMobileAdapter.DeleteItem deleteItem;

    public void setOnDeleteListener(InvoiceDetailMobileAdapter.DeleteItem deleteItem) {
        this.deleteItem = deleteItem;
    }


    public InvoiceDetailTabletAdapter(Context context, List<InvoiceDetail> orderDetailList, String type) {
        this.context = context;
        this.invoicedetails = orderDetailList;
        this.type = type;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {

        InvoiceDetail invoicedetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invoicedetails.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();


        GradientDrawable bgLight = new GradientDrawable();
        bgLight.setColor(0xFFECE4E4);

        GradientDrawable bgDark = new GradientDrawable();
        bgDark.setColor(0xFFEEEEEE);


        if (type.equals("1")) {
            holder.ivDelete.setImageBitmap(null);
            holder.edtAmount.setEnabled(false);
            holder.tvDescription.setEnabled(false);
        } else {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.edtAmount.setEnabled(true);
            holder.tvDescription.setEnabled(true);
        }


        holder.rst = new ArrayList<>();
        holder.rst.addAll(Util.AllProduct);
        CollectionUtils.filter(holder.rst, r -> r.getPRDUID().equals(invoicedetail.PRD_UID));

        holder.tvRow.setText(String.valueOf(holder.getAdapterPosition() + 1));
        if (holder.rst.size() > 0) {


            if (holder.rst.get(0).PERC_DIS != 0.0) {

                holder.tvDiscountPercent.setText(format.format(holder.rst.get(0).PERC_DIS) + "%");
                holder.tvDiscountPercent.setTextColor(context.getResources().getColor(R.color.invite_friend_color));


            } else {
                holder.tvDiscountPercent.setText("0");
                holder.tvDiscountPercent.setTextColor(context.getResources().getColor(R.color.medium_color));
            }


            holder.tvName.setText(holder.rst.get(0).getPRDNAME());
            holder.tvPrice.setText(format.format(holder.rst.get(0).getPRDPRICEPERUNIT1()));

            double totalPrice = invoicedetail.INV_DET_QUANTITY * holder.rst.get(0).getPRDPRICEPERUNIT1();

            Double discountPrice = 0.0;
            try {
                discountPrice = totalPrice * holder.rst.get(0).PERC_DIS / 100;
            } catch (Exception e) {

            }
            double purePrice = totalPrice - discountPrice;

            holder.tvSumDiscountPrice.setText(format.format(discountPrice));
            holder.tvSumPrice.setText(format.format(totalPrice));
            holder.tvSumPurePrice.setText(format.format(purePrice));

        }
        if (holder.textWatcher == null) {
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String s = Util.toEnglishNumber(charSequence.toString());
                    Double amount = 0.0;
                    if (!s.equals("")) {
                        amount = Double.parseDouble(s);
                    }
                    Double sumprice = (amount * holder.rst.get(0).getPRDPRICEPERUNIT1());
                    holder.tvSumPrice.setText(format.format(sumprice));

                    Double discountPrice = sumprice * holder.rst.get(0).PERC_DIS / 100;
                    Double totalPrice = sumprice - discountPrice;
                    holder.tvSumPurePrice.setText(format.format(totalPrice));

                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invoicedetail.INV_DET_UID + "'").first();
                    invoiceDetail.INV_DET_QUANTITY = amount;
                    invoiceDetail.update();
                    ArrayList<Product> prdResult = new ArrayList<>();
                    prdResult.addAll(Util.AllProduct);
                    CollectionUtils.filter(prdResult, r -> r.I.equals(invoiceDetail.PRD_UID));
                    if (prdResult.size() > 0)
                        Util.AllProduct.get(Util.AllProduct.indexOf(prdResult.get(0))).setAmount(amount);


                    LauncherActivity mainActivity = (LauncherActivity) holder.itemView.getContext();
                    Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag("OrderFragment");
                    if (fragment instanceof OrderFragment) {
                        OrderFragment fgf = (OrderFragment) fragment;
                        fgf.invoiceDetailAdapter.notifyDataSetChanged();
                        fgf.productAdapter.notifyDataSetChanged();

                    }


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
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invoicedetail.INV_DET_UID + "'").first();
                    invoiceDetail.INV_DET_DESCRIBTION = charSequence.toString();
                    invoiceDetail.update();
                    LauncherActivity mainActivity = (LauncherActivity) holder.itemView.getContext();
                    Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag("OrderFragment");
                    if (fragment instanceof OrderFragment) {
                        OrderFragment fgf = (OrderFragment) fragment;
                        fgf.invoiceDetailAdapter.notifyDataSetChanged();


                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };


        }

        holder.tvDescription.addTextChangedListener(holder.textWatcherDescription);


        if (invoicedetail.INV_DET_DESCRIBTION != null && !invoicedetail.INV_DET_DESCRIBTION.equals("")) {
            holder.rlDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.removeTextChangedListener(holder.textWatcherDescription);
            holder.tvDescription.setText(invoicedetail.INV_DET_DESCRIBTION);
            holder.tvDescription.addTextChangedListener(holder.textWatcherDescription);

        } else {
            if (type.equals("1"))
                holder.rlDescription.setVisibility(View.GONE);
            holder.tvDescription.removeTextChangedListener(holder.textWatcherDescription);
            holder.tvDescription.setText("");
            holder.tvDescription.addTextChangedListener(holder.textWatcherDescription);

        }


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
                result.addAll(invoicedetails);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoicedetails.get(holder.getAdapterPosition()).PRD_UID));
                if (result.size() > 0) {
                    ArrayList<Product> resultPrd_ = new ArrayList<>();
                    resultPrd_.addAll(Util.AllProduct);

                    CollectionUtils.filter(resultPrd_, r -> r.getPRDUID().equals(invoicedetails.get(holder.getAdapterPosition()).PRD_UID));
                    if (resultPrd_.size() > 0) {
                        Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd_.get(0))).setAmount(0.0);


                    }


                }


                LauncherActivity mainActivity = (LauncherActivity) holder.itemView.getContext();
                Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag("OrderFragment");
                if (fragment instanceof OrderFragment) {
                    OrderFragment fgf = (OrderFragment) fragment;
                    fgf.invoiceDetailList.remove(fgf.invoiceDetailList.get(holder.getAdapterPosition()));
                    fgf.invoiceDetailAdapter.notifyDataSetChanged();
                    fgf.productAdapter.notifyDataSetChanged();

                }

                notifyItemRemoved(holder.getAdapterPosition());


                holder.ivDelete.setEnabled(true);
                holder.ivDelete.setVisibility(View.VISIBLE);

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








