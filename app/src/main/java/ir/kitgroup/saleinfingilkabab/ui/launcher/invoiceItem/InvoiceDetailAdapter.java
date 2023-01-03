package ir.kitgroup.saleinfingilkabab.ui.launcher.invoiceItem;

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


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinfingilkabab.Connect.CompanyAPI;
import ir.kitgroup.saleinfingilkabab.DataBase.Company;
import ir.kitgroup.saleinfingilkabab.DataBase.InvoiceDetail;


import ir.kitgroup.saleinfingilkabab.R;
import ir.kitgroup.saleinfingilkabab.classes.Util;
import ir.kitgroup.saleinfingilkabab.models.ModelLog;
import ir.kitgroup.saleinfingilkabab.models.Product;

public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailAdapter.viewHolder> {

    //region Parameter
    private final List<InvoiceDetail> orderDetailList;
    private final SharedPreferences sharedPreferences;
    private final String type;//1 seen      //2 edit
    private final Activity contex;
    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");
    private final DecimalFormat df;
    private final CompanyAPI api;
    private final CompositeDisposable compositeDisposable;
    private List<String> closeDateList;
    private String valueOfDay;
    private final String defaultCoff;
    private final Company company;
    private final String checkRemainProduct;
    String Inv_GUID;


    //region DescriptionInterface
    public interface DescriptionItem {
        void onRowDescription(String GUIDPrd, String GUIDInv, String description);
    }

    private DescriptionItem descriptionItem;

    public void onDescriptionItem(DescriptionItem decriptionItem) {
        this.descriptionItem = decriptionItem;
    }
    //endregion DescriptionInterface


    //region InterFaceClickItemProduct
    public interface ClickItem {
        void onClick(String Prd_UID);
    }

    private ClickItem clickItem;

    public void setOnClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }
    //endregion InterFaceClickItemProduct

    //endregion Parameter

    public InvoiceDetailAdapter(Activity context, List<InvoiceDetail> orderDetailList, String type, SharedPreferences sharedPreferences, CompanyAPI api, String Inv_GUID) {
        this.sharedPreferences = sharedPreferences;
        this.orderDetailList = orderDetailList;
        this.type = type;
        this.contex = context;
        df = new DecimalFormat();
        company = Select.from(Company.class).first();

        this.api = api;
        compositeDisposable = new CompositeDisposable();
        closeDateList = new ArrayList<>();
        getCloseDay();

        defaultCoff = sharedPreferences.getString("coff", "0");

        this.checkRemainProduct = sharedPreferences.getString("maxSale", "");
        Calendar calendar = Calendar.getInstance();
        switch (calendar.getTime().getDay()) {

            case 0:
                valueOfDay = "1";
                break;
            case 1:
                valueOfDay = "2";
                break;
            case 2:
                valueOfDay = "3";
                break;
            case 3:
                valueOfDay = "4";
                break;
            case 4:
                valueOfDay = "5";
                break;
            case 5:
                valueOfDay = "6";
                break;
            case 6:
                valueOfDay = "0";
                break;
        }

        this.Inv_GUID = Inv_GUID;
    }

    //region Override Method
    @Override
    public @NotNull viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_list_item_recycle, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {
        InvoiceDetail invoicedetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();

        List<Product> prd1 = Select.from(Product.class).where("I ='" + orderDetailList.get(holder.getAdapterPosition()).PRD_UID + "'").list();

        holder.txtError.setText("");
        holder.progressBar.setVisibility(View.GONE);
        holder.ivMax.setEnabled(true);
        holder.ivMinus.setEnabled(true);


        if (type.equals("1")) {
            holder.imgDelete.setImageBitmap(null);
            holder.edtDescription.setEnabled(false);
            holder.imgDescription.setEnabled(false);
            holder.edtAmount.setEnabled(false);
            holder.ivMinus.setVisibility(View.GONE);
            holder.ivMax.setVisibility(View.GONE);
            holder.layoutAmount.setBackground(contex.getResources().getDrawable(R.drawable.background_edittext));
        }

        if (prd1.size() > 0) {
            Picasso.get()
                    .load("http://" + company.getIp1() + "/GetImage?productId=" + prd1.get(0).getI() + "&width=200&height=200")
                    .error(R.drawable.loading)
                    .placeholder(R.drawable.loading)
                    .into(holder.imageView);


            double discount;

            if (type.equals("2")) {
                if (prd1.get(0).getPercDis() != 0.0)
                    discount = prd1.get(0).getPercDis();
                else
                    discount = 0;
            } else {
                if (invoicedetail.INV_DET_PERCENT_DISCOUNT != 0.0)
                    discount = invoicedetail.INV_DET_PERCENT_DISCOUNT;
                else
                    discount = 0;
            }

            if (discount > 0)
                holder.discount.setText(format.format(discount) + "%");
            else
                holder.discount.setText("");


            holder.name.setText(holder.getAdapterPosition() + 1 + "_" + prd1.get(0).getN());

            holder.price.setText(format.format(prd1.get(0).getPrice(sharedPreferences)));

            double sumprice = (invoicedetail.INV_DET_QUANTITY * prd1.get(0).getPrice(sharedPreferences));
            holder.sumPrice.setText(format.format(sumprice));
        }

        holder.imgDescription.setOnClickListener(v -> {
            InvoiceDetail inDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID + "'").first();
            if (inDtl != null && inDtl.INV_DET_QUANTITY > 0.0) {
                descriptionItem.onRowDescription(orderDetailList.get(holder.getAdapterPosition()).PRD_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_UID, orderDetailList.get(holder.getAdapterPosition()).INV_DET_DESCRIBTION);
            } else {
                showAlert("برای کالا مقدار وارد کنید.");
            }
        });

        holder.edtDescription.setText(Objects.requireNonNullElse(invoicedetail.INV_DET_DESCRIBTION, ""));

        holder.imgDelete.setOnClickListener(view -> {

            try {
                if (orderDetailList.size() > holder.getAdapterPosition()) {


                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.imgDelete.setVisibility(View.GONE);
                    holder.imgDelete.setEnabled(false);


                    ArrayList<InvoiceDetail> result = new ArrayList<>(orderDetailList);
                    CollectionUtils.filter(result, r -> r.PRD_UID.equals(orderDetailList.get(holder.getAdapterPosition()).PRD_UID));
                    if (result.size() > 0) {


                        notifyItemRemoved(orderDetailList.indexOf(result.get(0)));
                        holder.imgDelete.setEnabled(true);
                        holder.imgDelete.setVisibility(View.VISIBLE);
                    }

                    holder.progressBar.setVisibility(View.GONE);

                }
            } catch (Exception ignored) {
            }

        });

        holder.ivMax.setOnClickListener(view -> {
            if (closeDateList != null && closeDateList.size() > 0 && closeDateList.contains(valueOfDay)) {
                Toasty.warning(contex, "فروشگاه تعطیل می باشد.", Toast.LENGTH_SHORT, true).show();
                return;
            }
            holder.edtAmount.setCursorVisible(false);
            holder.ivMinus.setEnabled(false);

            if (checkRemainProduct.equals("1"))
                holder.progressBar.setVisibility(View.VISIBLE);

            if (holder.getAdapterPosition() < orderDetailList.size())
                doAction(holder.getAdapterPosition(),
                        "",
                        1);
        });

        holder.ivMinus.setOnClickListener(v -> {
            if (closeDateList != null && closeDateList.size() > 0 && closeDateList.contains(valueOfDay)) {
                Toasty.warning(contex, "فروشگاه تعطیل می باشد.", Toast.LENGTH_SHORT, true).show();
                return;
            }

            holder.edtAmount.setCursorVisible(false);
            holder.ivMinus.setEnabled(false);

            if (checkRemainProduct.equals("1"))
                holder.progressBar.setVisibility(View.VISIBLE);

            if (holder.getAdapterPosition() < orderDetailList.size())
                if (holder.getAdapterPosition() < orderDetailList.size())
                    doAction(holder.getAdapterPosition(),
                            "",
                            2);
        });

        holder.edtAmount.setOnFocusChangeListener((view1, b) -> {
            holder.edtAmount.setCursorVisible(true);
        });

        if (holder.textWatcher == null) {
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if (!charSequence.toString().isEmpty())
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

                    if (checkRemainProduct.equals("1"))
                        holder.progressBar.setVisibility(View.VISIBLE);

                    if (holder.getAdapterPosition() < orderDetailList.size())
                        doAction(holder.getAdapterPosition(), s, 3);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }

        holder.edtAmount.removeTextChangedListener(holder.textWatcher);

        holder.edtAmount.removeTextChangedListener(holder.textWatcher);

        if (invoicedetail.INV_DET_QUANTITY.equals("0")) {
            holder.edtAmount.setText("");
        } else {
            holder.edtAmount.setText(df.format(invoicedetail.INV_DET_QUANTITY));
        }

        holder.edtAmount.addTextChangedListener(holder.textWatcher);

        holder.edtAmount.setSelection(holder.edtAmount.getText().toString().length());

    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }
    //endregion Override Method

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
        private final TextView txtError;
        private final RoundedImageView imageView;

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
            txtError = itemView.findViewById(R.id.txtError);
            imageView = itemView.findViewById(R.id.order_list_item_recycle_img);
        }
    }

    private void showAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(contex)
                .setMessage(message)
                .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                .show();

        TextView textView = alertDialog.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(contex.getAssets(), "iransans.ttf");
        textView.setTypeface(face);
        textView.setTextColor(contex.getResources().getColor(R.color.red_table));
        textView.setTextSize(13);
    }

    private void doAction(int position, String s, int MinOrPlus) {

        String Prd_GUID = orderDetailList.get(position).PRD_UID;
        String INV_DET_UID = orderDetailList.get(position).INV_DET_UID;

        InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + INV_DET_UID + "'").first();
        Product product = Select.from(Product.class).where("I ='" + Prd_GUID + "'").first();

        double currentAmount = invoiceDetail.getQuantity();


        if (checkRemainProduct.equals("1"))
            getMaxSales(position, s, MinOrPlus);

        else {
            double newAmount;

            double mainCoef1 = product.getCoef1();

            double coef1 = mainCoef1;
            double coef2 = product.getCoef2();

            if (defaultCoff.equals("1"))
                coef2 = mainCoef1;
            else if (defaultCoff.equals("2"))
                coef1 = coef2;


            //region MaxAmount
            if (MinOrPlus == 1) {
                if (currentAmount > 0.0 && coef2 != 0.0)
                    coef1 = coef2;
                newAmount = currentAmount + coef1;
            }
            //endregion MaxAmount


            //region MinAmount
            else if (MinOrPlus == 2) {
                if (currentAmount > coef1 && coef2 != 0.0)
                    newAmount = currentAmount - coef2;
                else if (currentAmount >= coef1)
                    newAmount = currentAmount - coef1;

                else
                    return;
            }
            //endregion MinAmount


            //region EditAmount
            else {
                newAmount = Float.parseFloat(s.equals("") ? "0" : s);
                if (
                        (coef2 != 0.0 && newAmount < coef1)
                                || (coef2 == 0 && newAmount % coef1 != 0)
                                || (coef2 == 0 && newAmount % coef1 == 0 && newAmount < coef1)
                ) {

                    showAlert(" مقدار وارد شده باید ضریبی از " + mainCoef1 + " باشد.");
                    newAmount = 0;

                }
            }
            //endregion EditAmount


            //region Edit InvoiceDetail
            if (invoiceDetail != null) {
                invoiceDetail.INV_DET_QUANTITY = newAmount;
                invoiceDetail.update();
            }
            //endregion Edit InvoiceDetail


            //region Create InvoiceDetail
            else {
                if (newAmount != 0.0) {
                    InvoiceDetail invoicedetail = new InvoiceDetail();
                    invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                    invoicedetail.INV_UID = Inv_GUID;
                    invoicedetail.INV_DET_QUANTITY = newAmount;
                    invoicedetail.PRD_UID = Prd_GUID;
                    invoicedetail.save();
                }

            }
            //endregion Create InvoiceDetail

            clickItem.onClick(Prd_GUID);
            notifyItemChanged(position);
        }
    }

    private void getMaxSales(int position, String s, int MinOrPlus) {
        String Prd_GUID = orderDetailList.get(position).PRD_UID;

        Gson gson = new Gson();
        Type typeIDs = new TypeToken<ModelLog>() {
        }.getType();

        try {
            compositeDisposable.add(
                    api.getMaxSales(company.getUser(), company.getPass(), Prd_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                        double remain;
                                        try {
                                            assert jsonElement != null;
                                            remain = Integer.parseInt(jsonElement);
                                        } catch (Exception e) {
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);
                                            assert iDs != null;
                                            String description = iDs.getLogs().get(0).getDescription();
                                            Toast.makeText(contex, description, Toast.LENGTH_SHORT).show();
                                            return;
                                        }


                                        String INV_DET_UID = orderDetailList.get(position).INV_DET_UID;

                                        InvoiceDetail resultInvoice = Select.from(InvoiceDetail.class).where("INVDETUID ='" + INV_DET_UID + "'").first();

                                        Product product = Select.from(Product.class).where("I ='" + Prd_GUID + "'").first();

                                        double currentAmount = resultInvoice.getQuantity();

                                        double mainCoef1 = product.getCoef1();
                                        double coef1 = mainCoef1;
                                        double coef2 = product.getCoef2();

                                        if (defaultCoff.equals("1")) {
                                            coef2 = mainCoef1;
                                        } else if (defaultCoff.equals("2")) {
                                            coef1 = coef2;
                                        }

                                        double amount;

                                        if (remain <= 0) {
                                            showAlert("ناموجود");
                                            amount = 0.0;
                                        } else if (MinOrPlus == 1) {
                                            if (currentAmount > 0.0 && coef2 != 0.0)
                                                coef1 = coef2;
                                            amount = currentAmount + coef1;
                                        } else if (MinOrPlus == 2) {
                                            if (currentAmount > coef1 && coef2 != 0.0 && currentAmount < coef1 + coef2) {
                                                amount = 0.0;
                                            } else if (currentAmount > coef1 && coef2 != 0.0)
                                                amount = currentAmount - coef2;
                                            else if (currentAmount >= coef1)
                                                amount = currentAmount - coef1;
                                            else
                                                return;
                                        } else {
                                            amount = Float.parseFloat(s.equals("") ? "0" : s);
                                            if (
                                                    (coef2 != 0.0 && amount < coef1)
                                                            || (coef2 == 0 && amount % coef1 != 0)
                                                            || (coef2 == 0 && amount % coef1 == 0 && amount < coef1)
                                            ) {

                                                showAlert(" مقدار وارد شده باید ضریبی از " + mainCoef1 + " باشد ");
                                                amount = 0.0;
                                            }

                                        }


                                        if (Integer.parseInt(jsonElement) - amount < 0) {
                                            showAlert("مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + jsonElement);

                                            if (remain % mainCoef1 != 0)
                                                remain = 0.0;

                                            if (resultInvoice != null) {
                                                resultInvoice.INV_DET_QUANTITY = remain;
                                                resultInvoice.update();

                                                clickItem.onClick(Prd_GUID);
                                                notifyItemChanged(position);
                                            }

                                            return;
                                        }
                                        //region Edit InvoiceDetail
                                        if (resultInvoice != null) {
                                            if (resultInvoice != null) {
                                                resultInvoice.INV_DET_QUANTITY = amount;
                                                resultInvoice.update();
                                            }
                                        }
                                        //endregion Edit InvoiceDetail


                                        //region Create InvoiceDetail
                                        else {
                                            if (amount != 0.0) {
                                                InvoiceDetail invoicedetail = new InvoiceDetail();
                                                invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                                                invoicedetail.INV_UID = Inv_GUID;
                                                invoicedetail.INV_DET_QUANTITY = amount;
                                                invoicedetail.PRD_UID = Prd_GUID;
                                                invoicedetail.save();
                                            }
                                        }
                                        //endregion Create InvoiceDetail

                                        notifyItemChanged(position);
                                        clickItem.onClick(Prd_GUID);
                                    }
                                    , throwable -> {
                                        showAlert("خطا در دریافت اطلاعات مانده کالا");
                                        notifyItemChanged(position);

                                    }));
        } catch (Exception e) {
            showAlert("خطا در دریافت اطلاعات مانده کالا");
            notifyItemChanged(position);

        }
    }

    private void getCloseDay() {
        String CloseDay = sharedPreferences.getString("close_day", "");
        closeDateList.clear();
        if (!CloseDay.equals("")) {
            closeDateList = new ArrayList<>(Arrays.asList(CloseDay.split(",")));
        }
    }

}