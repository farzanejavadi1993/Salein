package ir.kitgroup.saleinjam.Adapters;


import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import ir.kitgroup.saleinjam.DataBase.InvoiceDetail;


import ir.kitgroup.saleinjam.Fragments.MainOrderFragment;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.classes.Util;
import ir.kitgroup.saleinjam.models.Product;


public class InvoiceDetailMobileAdapter extends RecyclerView.Adapter<InvoiceDetailMobileAdapter.viewHolder> {


    private final List<InvoiceDetail> orderDetailList;

    private SharedPreferences sharedPreferences;
    private final String type;//1 seen      //2 edit
    private final Boolean Seen; //when Type 1 but Edit
    private final Activity contex;

    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");

    private int fontSize = 12;
    private int fontLargeSize = 13;

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


    public InvoiceDetailMobileAdapter(Activity context, List<InvoiceDetail> orderDetailList, String type, boolean Seen, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.orderDetailList = orderDetailList;
        this.type = type;
        this.Seen = Seen;
        this.contex = context;
        df = new DecimalFormat();


    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (Util.screenSize >= 7) {
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


        try {


        InvoiceDetail invoicedetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();


        holder.name.setTextSize(fontSize);
        holder.price.setTextSize(fontSize);
        holder.edtAmount.setTextSize(fontLargeSize);
        holder.edtDescription.setTextSize(fontSize);
        holder.sumPrice.setTextSize(fontLargeSize);

        if (type.equals("1")) {
            holder.imgDelete.setImageBitmap(null);
            holder.edtDescription.setEnabled(false);
            holder.imgDescription.setEnabled(false);
            holder.edtAmount.setEnabled(false);
            holder.ivMinus.setVisibility(View.GONE);
            holder.ivMax.setVisibility(View.GONE);
            holder.layoutAmount.setBackground(contex.getResources().getDrawable(R.drawable.background_edittext));
        }


        List<Product> prd1 = Select.from(Product.class).where("I ='" + orderDetailList.get(holder.getAdapterPosition()).PRD_UID + "'").list();


        if (prd1.size() > 0) {

            if (type.equals("2")){
               if (prd1.get(0).getPercDis() != 0.0)
                   holder.discount.setText(format.format(prd1.get(0).getPercDis()) + "%");
               else
                   holder.discount.setText("");
            }

            else if ((type.equals("1") || Seen) && invoicedetail.INV_DET_PERCENT_DISCOUNT != null ) {
                if (invoicedetail.INV_DET_PERCENT_DISCOUNT != 0.0)
                    holder.discount.setText(format.format(invoicedetail.INV_DET_PERCENT_DISCOUNT) + "%");
                else
                    holder.discount.setText("");
            }

            holder.name.setText(holder.getAdapterPosition() + 1 + "_" + prd1.get(0).getN());
            holder.price.setText(format.format(prd1.get(0).getPrice(sharedPreferences)));


            double sumprice = (invoicedetail.INV_DET_QUANTITY * prd1.get(0).getPrice(sharedPreferences));
            holder.sumPrice.setText(format.format(sumprice));


        } else {
            holder.discount.setText("");
            holder.name.setText("");
            holder.price.setText("");
            holder.sumPrice.setText("");
        }


        if (holder.textWatcher == null) {
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    holder.edtAmount.setCursorVisible(true);
                    String s = Util.toEnglishNumber(charSequence.toString());
                    s = s.contains("٫") ? s.replace("٫", ".") : s;
                    s = s.contains(",") ? s.replace(",", "") : s;

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
                      Product resultPrd1= Select.from(Product.class).where("I ='" + orderDetailList.get(holder.getAdapterPosition()).PRD_UID + "'").first();



                        InvoiceDetail ivDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();

                      if (holder.getAdapterPosition() < orderDetailList.size()) {

                            amount = Double.parseDouble(s);


                            double mainCoef = 1.0;
                            double coef1 = 1.0;
                            double coef2 = 0.0;
                            if (resultPrd1!=null) {
                                mainCoef = resultPrd1.getCoef1();
                                coef1 = resultPrd1.getCoef1();
                                coef2 = resultPrd1.getCoef1();

                            }


                            if ( (coef2 != 0.0 && amount < coef1)
                                    || (coef2 == 0 && amount % coef1 != 0)
                                    || (coef2 == 0 && amount % coef1 == 0 && amount < coef1)){


                                AlertDialog alertDialog = new AlertDialog.Builder(contex)
                                        .setMessage(" مقدار انتخاب شده باید ضریبی از" + mainCoef + "باشد  ")
                                        .setPositiveButton("بستن", (dialog, which) -> {
                                            dialog.dismiss();
                                        })
                                        .show();

                                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                                Typeface face = Typeface.createFromAsset(contex.getAssets(), "iransans.ttf");
                                textView.setTypeface(face);
                                textView.setTextColor(contex.getResources().getColor(R.color.medium_color));
                                textView.setTextSize(13);


                               /* holder.edtAmount.removeTextChangedListener(holder.textWatcher);
                                holder.edtAmount.setText("");
                                holder.edtAmount.addTextChangedListener(holder.textWatcher);

                                double sumprice = (0 * resultPrd1.getPrice(sharedPreferences));
                                holder.sumPrice.setText(format.format(sumprice));*/

                                double discountPercent ;
                                if (type.equals("1") || Seen)
                                    discountPercent = ivDtl.INV_DET_PERCENT_DISCOUNT;
                                else
                                    discountPercent = resultPrd1.getPercDis();
                                editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, "0", resultPrd1.getPrice(sharedPreferences), discountPercent / 100);
                                return;
                            }
                        }
                       /* double sumprice = (amount * resultPrd1.getPrice(sharedPreferences));
                        holder.sumPrice.setText(format.format(sumprice));*/

                        double discountPercent ;
                        if (type.equals("1") || Seen)
                            discountPercent = ivDtl.INV_DET_PERCENT_DISCOUNT;
                        else
                            discountPercent = resultPrd1.getPercDis();


                        editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, s, resultPrd1.getPrice(sharedPreferences), discountPercent / 100);
                    }


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };


        }
        holder.imgDescription.setOnClickListener(v -> {
            InvoiceDetail inDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();
            if (inDtl!=null && inDtl.INV_DET_QUANTITY>0.0){
                decriptionItem.onRowDescription(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_DESCRIBTION);
            }else {
                Toast.makeText(contex, "برای کالا مقدار وارد کنید.", Toast.LENGTH_SHORT).show();
            }

                }

        );


        holder.edtAmount.removeTextChangedListener(holder.textWatcher);
        if (invoicedetail.INV_DET_QUANTITY.equals("0")) {
            holder.edtAmount.setText("");
        } else {
            holder.edtAmount.setText(df.format(invoicedetail.INV_DET_QUANTITY));
        }

        holder.edtAmount.addTextChangedListener(holder.textWatcher);

        if (invoicedetail.INV_DET_DESCRIBTION != null) {
            holder.edtDescription.setText(invoicedetail.INV_DET_DESCRIBTION);

        } else {
            holder.edtDescription.setText("");
        }

        holder.imgDelete.setOnClickListener(view -> {

            try {
                if (orderDetailList.size() > holder.getAdapterPosition()){


                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.imgDelete.setVisibility(View.GONE);
                    holder.imgDelete.setEnabled(false);


                    ArrayList<InvoiceDetail> result = new ArrayList<>(orderDetailList);
                    CollectionUtils.filter(result, r -> r.PRD_UID.equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
                    if (result.size() > 0) {


                        notifyItemRemoved(orderDetailList.indexOf(result.get(0)));
                     Fragment frg = ((FragmentActivity) contex).getSupportFragmentManager().findFragmentByTag("MainOrderFragment");
                       if (frg instanceof MainOrderFragment) {
                           MainOrderFragment fgf = (MainOrderFragment) frg;
                           fgf.refreshProductList();
                           fgf.counter1 = fgf.counter1 - 1;

                       }
                        holder.imgDelete.setEnabled(true);
                        holder.imgDelete.setVisibility(View.VISIBLE);
                    }

                    holder.progressBar.setVisibility(View.GONE);

                }
            }catch (Exception ignored){}

        });

            holder.edtAmount.setOnClickListener(v -> {

                        holder.edtAmount.setCursorVisible(true);


                    }
            );

        holder.ivMax.setOnClickListener(v -> {
            try {
                if (holder.getAdapterPosition() < orderDetailList.size()){

                    holder.edtAmount.setCursorVisible(false);
                    holder.ivMax.setEnabled(false);
                    InvoiceDetail invDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();

                    Product resultPrd1= Select.from(Product.class).where("I ='" + orderDetailList.get(holder.getAdapterPosition()).PRD_UID + "'").first();


                    double coef1 = 1.0;
                    double coef2;
                    if (resultPrd1!=null) {
                        coef1 = resultPrd1.getCoef1();
                        coef2 = resultPrd1.getCoef2();
                        if (invDtl.INV_DET_QUANTITY>0.0 &&  coef2 != 0.0)
                            coef1 = coef2;
                    }
                    double amount = invDtl.INV_DET_QUANTITY + coef1;
                /*holder.edtAmount.removeTextChangedListener(holder.textWatcher);
                holder.edtAmount.setText(format.format(amount));
                holder.edtAmount.addTextChangedListener(holder.textWatcher);

                double sumprice = (amount * prd1.get(0).getPrice(sharedPreferences));
                holder.sumPrice.setText(format.format(sumprice));*/

                    double discountPercent;
                    if (type.equals("1") || Seen)
                        discountPercent = invDtl.INV_DET_PERCENT_DISCOUNT;
                    else
                        discountPercent = resultPrd1.getPercDis();


                    editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, String.valueOf(amount), resultPrd1.getPrice(sharedPreferences), discountPercent / 100);
                    holder.ivMax.setEnabled(true);
                }
            }catch (Exception ignored){}


        });

        holder.ivMinus.setOnClickListener(v -> {
            try {
                if (holder.getAdapterPosition() < orderDetailList.size()){
                    holder.ivMinus.setEnabled(false);
                    holder.edtAmount.setCursorVisible(false);
                    Product resultPrd1= Select.from(Product.class).where("I ='" + orderDetailList.get(holder.getAdapterPosition()).PRD_UID + "'").first();

                    InvoiceDetail ivDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();

                    double coef1 = 1;
                    double coef2 = 0.0;
                    if (resultPrd1!=null) {
                        coef1 = resultPrd1.getCoef1();
                        coef2 = resultPrd1.getCoef2();

                    }



                    double amount = 0.0;

                    if (ivDtl.INV_DET_QUANTITY > coef1 && coef2 != 0.0 && ivDtl.INV_DET_QUANTITY<coef1+coef2){
                        amount = 0.0;
                    }
                    else if (ivDtl.INV_DET_QUANTITY > coef1 && coef2 != 0.0)
                        amount = ivDtl.INV_DET_QUANTITY - coef2;
                    else if (ivDtl.INV_DET_QUANTITY >= coef1)
                        amount = ivDtl.INV_DET_QUANTITY - coef1;





//                holder.edtAmount.removeTextChangedListener(holder.textWatcher);
//                holder.edtAmount.setText(format.format(amount));
//                holder.edtAmount.addTextChangedListener(holder.textWatcher);
//                double sumprice = (amount * prd1.get(0).getPrice(sharedPreferences));
//                holder.sumPrice.setText(format.format(sumprice));
                    double discountPercent;
                    if (type.equals("1") || Seen)
                        discountPercent = ivDtl.INV_DET_PERCENT_DISCOUNT;
                    else
                        discountPercent = resultPrd1.getPercDis();

                    editAmountItem.onEditAmountRow(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, String.valueOf(amount), resultPrd1.getPrice(sharedPreferences), discountPercent / 100);

                    holder.ivMinus.setEnabled(true);
                }
            }catch (Exception ignored){}




        });
        }catch (Exception ignored){

        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {


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
        private final ProgressBar progressBar;


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
            progressBar = itemView.findViewById(R.id.progress);


        }
    }







}







