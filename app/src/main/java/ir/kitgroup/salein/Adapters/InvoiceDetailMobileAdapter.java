package ir.kitgroup.salein.Adapters;


import android.annotation.SuppressLint;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Product;

import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;


public class InvoiceDetailMobileAdapter extends RecyclerView.Adapter<InvoiceDetailMobileAdapter.viewHolder> {

    private final List<InvoiceDetail> orderDetailList;

    private final String type;//1 seen      //2 edit
    private final Context contex;//1 seen      //2 edit

    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");

    private int fontSize = 0;
    private int fontLargeSize = 0;

    private final DecimalFormat df;


    public interface DecriptionItem {
        void onRowDescription(String GUIDPrd, String GUIDInv, String description);
    }

    private DecriptionItem decriptionItem;

    public void onDescriptionItem(DecriptionItem decriptionItem) {
        this.decriptionItem = decriptionItem;
    }


    public interface EditAmountItem {
        void onEditAmountRow(String GUID, String s, double Price, double discountPercent);
    }

    private EditAmountItem editAmountItem;

    public void editAmountItemListener(EditAmountItem editAmountItem) {
        this.editAmountItem = editAmountItem;
    }







    public InvoiceDetailMobileAdapter(Context context, List<InvoiceDetail> orderDetailList, String type) {

        this.orderDetailList = orderDetailList;
        this.type = type;
        this.contex = context;

        df = new DecimalFormat();


    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (LauncherActivity.screenInches >= 7) {
            fontSize = 13;
            fontLargeSize = 14;
        } else {
            fontSize = 11;
            fontLargeSize = 12;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_list_item_recycle, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {


        InvoiceDetail invoicedetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();


        holder.name.setTextSize(fontSize);
        holder.price.setTextSize(fontSize);
        holder.edtAmount.setTextSize(fontLargeSize);
        holder.edtDescription.setTextSize(fontSize);
        holder.sumPrice.setTextSize(fontLargeSize);

        if (type.equals("1")) {
            holder.imgDelete.setImageBitmap(null);
            holder.edtDescription.setEnabled(false);
            holder.edtAmount.setEnabled(false);
            holder.ivMinus.setVisibility(View.GONE);
            holder.ivMax.setVisibility(View.GONE);
            holder.layoutAmount.setBackground(contex.getResources().getDrawable(R.drawable.background_edittext));
        }

        holder.rst = new ArrayList<>();
        holder.rst.addAll(Util.AllProduct);
        CollectionUtils.filter(holder.rst, r -> r.getPRDUID().equals(invoicedetail.PRD_UID));

        if (holder.rst.size() > 0) {


            if (holder.rst.get(0).PERC_DIS != 0.0) {
                holder.discount.setText(format.format(holder.rst.get(0).PERC_DIS) + "%");

            } else {
                holder.discount.setText("");
            }


            holder.name.setText(holder.rst.get(0).getPRDNAME());
            holder.price.setText(format.format(holder.rst.get(0).getPRDPRICEPERUNIT1()));


            double sumprice = (invoicedetail.INV_DET_QUANTITY * holder.rst.get(0).getPRDPRICEPERUNIT1());
           /* double discountPrice = sumprice * (holder.rst.get(0).PERC_DIS / 100);
            double totalPrice = sumprice - discountPrice;*/
            holder.sumPrice.setText(format.format(sumprice));


        }
        if (holder.textWatcher == null) {
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String s = Util.toEnglishNumber(charSequence.toString());
                    s = s.contains("٫") ? s.replace("٫", ".") : s;

                    if (!s.isEmpty()) {
                        if (s.contains(".") &&
                                s.indexOf(".") == s.length() - 1) {
                            return;
                        } else if (s.contains("٫") &&
                                s.indexOf("٫") == s.length() - 1) {
                            return;
                        }

                    }
                    double amount = 0.0;
                    if (!s.equals("")) {
                         amount = Double.parseDouble(s);
                        ArrayList<Product> resultPrd1 = new ArrayList<>(Util.AllProduct);
      CollectionUtils.filter(resultPrd1, r -> r.I.equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
                        double aPlus=1;
                        if (resultPrd1.size()>0){
                            aPlus = resultPrd1.get(0).COEF;
                            if (aPlus == 0)
                                aPlus = 1;
                        }

                        if (amount%aPlus!=0){
                            Toast.makeText(contex,  " مقدار انتخاب شده باید ضریبی از" + aPlus + "باشد  ", Toast.LENGTH_SHORT).show();
                            holder.edtAmount.removeTextChangedListener(holder.textWatcher);
                            holder.edtAmount.setText("0");
                            holder.edtAmount.addTextChangedListener(holder.textWatcher);
                            Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd1.get(0))).AMOUNT=0.0;
                            double sumprice = (0 * holder.rst.get(0).getPRDPRICEPERUNIT1());

                            holder.sumPrice.setText(format.format(sumprice));
                            editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, "0", holder.rst.get(0).getPRDPRICEPERUNIT1(), holder.rst.get(0).PERC_DIS / 100);
                            return;
                        }
                    }
                    double sumprice = (amount * holder.rst.get(0).getPRDPRICEPERUNIT1());
                   /* Double discountPrice = sumprice * holder.rst.get(0).PERC_DIS / 100;
                    Double totalPrice = sumprice - discountPrice;*/
                    holder.sumPrice.setText(format.format(sumprice));
                    editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, s, holder.rst.get(0).getPRDPRICEPERUNIT1(), holder.rst.get(0).PERC_DIS / 100);


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };


        }
        holder.imgDescription.setOnClickListener(v -> decriptionItem.onRowDescription(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_DESCRIBTION));


        holder.edtAmount.removeTextChangedListener(holder.textWatcher);
        if (invoicedetail.INV_DET_QUANTITY.equals("0")) {
            holder.edtAmount.setText("");
        } else {
            holder.edtAmount.setText(df.format(invoicedetail.INV_DET_QUANTITY));
        }

        holder.edtAmount.addTextChangedListener(holder.textWatcher);

        if (invoicedetail.INV_DET_DESCRIBTION != null) {
            holder.edtDescription.setText(invoicedetail.INV_DET_DESCRIBTION);

        }else {
            holder.edtDescription.setText("");
        }

        holder.imgDelete.setOnClickListener(view -> {

            holder.imgDelete.setVisibility(View.GONE);
            holder.imgDelete.setEnabled(false);

            ArrayList<InvoiceDetail> result = new ArrayList<>(orderDetailList);
            CollectionUtils.filter(result, r -> r.PRD_UID.equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
            if (result.size() > 0) {
                ArrayList<Product> resultPrd_ = new ArrayList<>(Util.AllProduct);

                CollectionUtils.filter(resultPrd_, r -> r.getPRDUID().equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
                if (resultPrd_.size() > 0) {
                    Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd_.get(0))).setAmount(0.0);

                }

                notifyItemRemoved(orderDetailList.indexOf(result.get(0)));


                ArrayList<Product> allResultPr = new ArrayList<>(Util.AllProduct);

                CollectionUtils.filter(allResultPr, r -> r.I.equals(result.get(0).PRD_UID));
                if (allResultPr.size() > 0) {
                    if (allResultPr.get(0).descItem != null) {
                        Util.AllProduct.get(Util.AllProduct.indexOf(allResultPr.get(0))).descItem = "";
                    }


                }


            }

            holder.imgDelete.setEnabled(true);
            holder.imgDelete.setVisibility(View.VISIBLE);
     /*       if (LauncherActivity.screenInches >= 7)
                deleteItem.onDelete(holder.rst.get(0).I);*/
        });


        holder.ivMax.setOnClickListener(v -> {
            ArrayList<Product> resultPrd1 = new ArrayList<>(Util.AllProduct);
            CollectionUtils.filter(resultPrd1, r -> r.I.equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
            double aPlus=1;
            if (resultPrd1.size()>0){
                aPlus = resultPrd1.get(0).COEF;
                if (aPlus == 0)
                    aPlus = 1;
            }
            holder.edtAmount.removeTextChangedListener(holder.textWatcher);
            double amount = invoicedetail.INV_DET_QUANTITY + aPlus;
            holder.edtAmount.setText(format.format(amount));
            holder.edtAmount.addTextChangedListener(holder.textWatcher);

            double sumprice = (amount * holder.rst.get(0).getPRDPRICEPERUNIT1());
            holder.sumPrice.setText(format.format(sumprice));
            editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, String.valueOf(amount), holder.rst.get(0).getPRDPRICEPERUNIT1(), holder.rst.get(0).PERC_DIS / 100);

        });


        holder.ivMinus.setOnClickListener(v -> {
            ArrayList<Product> resultPrd1 = new ArrayList<>(Util.AllProduct);
            CollectionUtils.filter(resultPrd1, r -> r.I.equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
            double aPlus=1;
            if (resultPrd1.size()>0){
                 aPlus = resultPrd1.get(0).COEF;
                if (aPlus == 0)
                    aPlus = 1;
            }


            holder.edtAmount.removeTextChangedListener(holder.textWatcher);
            double amount=0.0;
            if (invoicedetail.INV_DET_QUANTITY>=aPlus) {
                 amount = invoicedetail.INV_DET_QUANTITY -aPlus;
            }
            holder.edtAmount.setText(format.format(amount));
            holder.edtAmount.addTextChangedListener(holder.textWatcher);

            double sumprice = (amount * holder.rst.get(0).getPRDPRICEPERUNIT1());
            holder.sumPrice.setText(format.format(sumprice));
            editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, String.valueOf(amount), holder.rst.get(0).getPRDPRICEPERUNIT1(), holder.rst.get(0).PERC_DIS / 100);
        });

    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private ArrayList<Product> rst;

        private final TextView edtDescription;


        private final TextView discount;


        private TextWatcher textWatcher;

        private final TextView name;
        private final TextView price;
        private final TextView sumPrice;
        private final ImageView imgDelete;
        private final RelativeLayout imgDescription;
        private final EditText edtAmount;
        private final ImageView ivMax;
        private final ImageView ivMinus;
        private final RelativeLayout layoutAmount;


        public viewHolder(View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.order_list_item_recycle_txt_name);
            discount = itemView.findViewById(R.id.order_list_item_recycle_txt_discount);
            price = itemView.findViewById(R.id.order_list_item_recycle_txt_price);
            sumPrice = itemView.findViewById(R.id.order_list_item_recycle_txt_sumPrice);
            imgDelete = itemView.findViewById(R.id.order_list_item_recycle_img_delete);
            imgDescription = itemView.findViewById(R.id.layout_description);
            edtAmount = itemView.findViewById(R.id.order_list_item_recycle_editText);
            edtDescription = itemView.findViewById(R.id.edt_description);
            ivMax = itemView.findViewById(R.id.iv_max_invoice);
            ivMinus = itemView.findViewById(R.id.iv_minus_invoice);
            layoutAmount = itemView.findViewById(R.id.layout_amount);


        }
    }


}







