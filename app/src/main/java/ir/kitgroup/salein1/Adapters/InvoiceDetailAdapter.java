package ir.kitgroup.salein1.Adapters;


import android.annotation.SuppressLint;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;



import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.Product;

import ir.kitgroup.salein1.Fragments.MobileView.InVoiceDetailMobileFragment;
import ir.kitgroup.salein1.Fragments.MobileView.MainOrderMobileFragment;
import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;

import ir.kitgroup.salein1.Fragments.MobileView.MainOrderMobileFragment;

import ir.kitgroup.salein1.R;
import ir.kitgroup.salein1.Util.Util;


public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailAdapter.viewHolder> {

    private final List<Invoicedetail> orderDetailList ;

    private final String type;//1 seen      //2 edit
    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private final DecimalFormat df;


    public interface DecriptionItem {
        void onRowDescription(String GUIDPrd, String GUIDInv, String description);
    }

    private DecriptionItem decriptionItem;

    public void onDescriptionItem(DecriptionItem decriptionItem) {
        this.decriptionItem = decriptionItem;
    }


    public interface EditAmountItem {
        void onEditAmountRow(String GUID, String s, String Price, double discountPercent);
    }

    private EditAmountItem editAmountItem;

    public void editAmountItemListener(EditAmountItem editAmountItem) {
        this.editAmountItem = editAmountItem;
    }

    public InvoiceDetailAdapter( List<Invoicedetail> orderDetailList,String type) {

        this.orderDetailList = orderDetailList;
        this.type=type;
        df = new DecimalFormat();


    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item_recycle, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {
        final Invoicedetail invoicedetail = orderDetailList.get(position);


        if (type.equals("1")){
            holder.imgDelete.setImageBitmap(null);
            holder.imgDescription.setImageBitmap(null);
            holder.edtDescription.setEnabled(false);
            holder.edtAmount.setEnabled(false);
        }
        holder.rst = new ArrayList<>();

        holder.rst.addAll(Util.AllProduct);
        CollectionUtils.filter(holder.rst, r -> r.getPRDUID().equals(invoicedetail.PRD_UID));

        if (holder.rst.size() > 0) {
            holder.discountPercent = holder.rst.get(0).PERC_DIS;


            if (holder.discountPercent != 0.0) {
                holder.discount.setText(format.format(holder.rst.get(0).PERC_DIS) + "%");

            } else {
                holder.discount.setText("");
            }


            holder.nameFood.setText(holder.rst.get(0).getPRDNAME());
            holder.priceFood.setText(format.format(holder.rst.get(0).getPRDPRICEPERUNIT1()));
            holder.Price = String.valueOf(holder.rst.get(0).getPRDPRICEPERUNIT1());
        }
        if (holder.textWatcher == null) {
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String s = LauncherOrganizationFragment.toEnglishNumber(charSequence.toString());
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
                    }
                    Double sumprice = (amount * Float.parseFloat(holder.Price));
                    Double discountPrice = sumprice * holder.discountPercent / 100;
                    Double totalPrice = sumprice - discountPrice;
                    holder.sumPriceFood.setText(format.format(totalPrice));
                    editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, s, holder.Price, holder.discountPercent / 100);


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };


        }
        holder.imgDescription.setOnClickListener(v -> decriptionItem.onRowDescription(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_DESCRIBTION));



        if (invoicedetail.INV_DET_DESCRIBTION != null) {
            holder.edtDescription.setText(invoicedetail.INV_DET_DESCRIBTION);

        }


        holder.sumPriceFood.setText(format.format(Float.parseFloat(invoicedetail.INV_DET_TOTAL_AMOUNT)));

        holder.edtAmount.removeTextChangedListener(holder.textWatcher);
        if (invoicedetail.INV_DET_QUANTITY.equals("0")) {
            holder.edtAmount.setText("");
        } else {
            holder.edtAmount.setText(df.format(Double.parseDouble(invoicedetail.INV_DET_QUANTITY)));
        }

        holder.edtAmount.addTextChangedListener(holder.textWatcher);


        holder.imgDelete.setOnClickListener(view -> {

            holder.imgDelete.setVisibility(View.GONE);
            holder.imgDelete.setEnabled(false);

            ArrayList<Invoicedetail> result = new ArrayList<>(InVoiceDetailMobileFragment.invoiceDetailList);

            CollectionUtils.filter(result, r -> r.PRD_UID.equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
            if (result.size() > 0) {
                ArrayList<Product> resultPrd_ = new ArrayList<>(Util.AllProduct);

                CollectionUtils.filter(resultPrd_, r -> r.getPRDUID().equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
                if (resultPrd_.size() > 0) {
                    Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd_.get(0))).setAmount(0.0);

                        if (MainOrderMobileFragment.productList.contains(resultPrd_.get(0))) {
                            MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultPrd_.get(0)));
                        }


                }

                    notifyItemRemoved(InVoiceDetailMobileFragment.invoiceDetailList.indexOf(result.get(0)));




                    ArrayList<Product> allResultpr = new ArrayList<>(Util.AllProduct);

                    CollectionUtils.filter(allResultpr, r -> r.I.equals(result.get(0).PRD_UID));
                    if (allResultpr.size() > 0) {
                        if (allResultpr.get(0).descItem != null) {

                            Util.AllProduct.get(Util.AllProduct.indexOf(allResultpr.get(0))).descItem = "";

                                MainOrderMobileFragment.productAdapter.notifyDataSetChanged();
                        }


                    }


            }

            holder.imgDelete.setEnabled(true);
            holder.imgDelete.setVisibility(View.VISIBLE);
        });


    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private ArrayList<Product> rst;

        private final EditText edtDescription;


        private final TextView discount;
        private Double discountPercent;
        private String Price;
        private TextWatcher textWatcher;

        private final TextView nameFood;
        private final TextView priceFood;
        private final TextView sumPriceFood;
        private final ImageView imgDelete;
        private final ImageView imgDescription;
        private final EditText edtAmount;


        public viewHolder(View itemView) {
            super(itemView);


            nameFood = itemView.findViewById(R.id.order_list_item_recycle_txt_name);
            discount = itemView.findViewById(R.id.order_list_item_recycle_txt_discount);
            priceFood = itemView.findViewById(R.id.order_list_item_recycle_txt_price);
            sumPriceFood = itemView.findViewById(R.id.order_list_item_recycle_txt_sumPrice);
            imgDelete = itemView.findViewById(R.id.order_list_item_recycle_img_delete);
            imgDescription = itemView.findViewById(R.id.iv_description);
            edtAmount = itemView.findViewById(R.id.order_list_item_recycle_editText);
            edtDescription = itemView.findViewById(R.id.edt_description);





        }
    }


}







