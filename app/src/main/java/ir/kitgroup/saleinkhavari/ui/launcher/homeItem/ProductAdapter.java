package ir.kitgroup.saleinkhavari.ui.launcher.homeItem;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
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
import java.util.Calendar;
import java.util.List;

import java.util.UUID;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinkhavari.Connect.CompanyAPI;
import ir.kitgroup.saleinkhavari.DataBase.Unit;
import ir.kitgroup.saleinkhavari.classes.Util;

import ir.kitgroup.saleinkhavari.DataBase.InvoiceDetail;

import ir.kitgroup.saleinkhavari.DataBase.Company;

import ir.kitgroup.saleinkhavari.models.ModelLog;
import ir.kitgroup.saleinkhavari.R;
import ir.kitgroup.saleinkhavari.models.Product;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder> {

    //region Parameter
    private final SharedPreferences sharedPreferences;
    private final CompanyAPI api;
    private final CompositeDisposable compositeDisposable;

    private final Company company;
    private final Activity activity;
    private final String defaultCoff;

    private final List<Product> productsList;

    private final String checkRemainProduct;

    private final String Inv_GUID;

    private final DecimalFormat df = new DecimalFormat();

    private final List<Unit> unitList;
    private List<String> closeDateList;
    private String valueOfDay;

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    //endregion Parameter


    //region InterFaceClickItemProduct
    public interface ClickItem {
        void onClick(String Prd_UID);
    }

    private ClickItem clickItem;

    public void setOnClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }
    //endregion InterFaceClickItemProduct


    //region InterfaceClickDescription
    public interface Descriptions {
        void onDesc(String GUID, double amount);
    }

    private Descriptions descriptionItem;

    public void setOnDescriptionItem(Descriptions descriptionItem) {
        this.descriptionItem = descriptionItem;
    }
    //endregion InterfaceClickDescription


    //region InterFaceClickImage
    public interface ClickImage {
        void onClick(String Prd_UID);
    }

    private ClickImage clickImage;

    public void setOnClickImageListener(ClickImage clickImage) {
        this.clickImage = clickImage;
    }
    //endregion InterFaceClickImage

    public ProductAdapter(Activity activity, List<Product> productsList, SharedPreferences sharedPreferences, CompanyAPI api) {

        this.activity = activity;
        this.productsList = productsList;
        this.sharedPreferences = sharedPreferences;
        this.api = api;
        this.checkRemainProduct = sharedPreferences.getString("maxSale", "0");
        this.Inv_GUID = sharedPreferences.getString("Inv_GUID", "");
        this.company = Select.from(Company.class).first();

        compositeDisposable = new CompositeDisposable();

        unitList = Select.from(Unit.class).list();

        defaultCoff = sharedPreferences.getString("coff", "0");

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
    }


    //region Override Method
    @Override
    public int getItemCount() {
        return productsList == null ? 0 : productsList.size();
    }

    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_products_item_mobile, parent,
                false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {

        if (productsList.get(position) != null) {

            holder.error.setText("");
            holder.progressBar.setVisibility(View.GONE);
            holder.ivMax.setEnabled(true);
            holder.ivMinus.setEnabled(true);

            holder.ProductAmountTxt.setEnabled(closeDateList == null || closeDateList.size() <= 0 || !closeDateList.contains(valueOfDay));

            holder.ProductAmountTxt.clearFocus();

            String ip = company.getIp1();

            Picasso.get()
                    .load("http://" + ip + "/GetImage?productId=" + productsList
                            .get(holder.getAdapterPosition()).getI() + "&width=200&height=200")
                    .error(R.drawable.loading)
                    .placeholder(R.drawable.loading)
                    .into(holder.productImage);


            InvoiceDetail ivDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "' AND PRDUID ='" + productsList.get(holder.getAdapterPosition()).getI() + "'").first();


            String name=productsList.get(holder.getAdapterPosition()).getN().trim();
            holder.productName.setText(name);



            if (productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) > 0) {
                holder.productPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences)) + " ریال ");
                holder.productDiscountPercent.setText("");
                holder.productOldPrice.setText("");
                holder.layoutDiscount.setVisibility(View.GONE);
                holder.productDiscountPercent.setVisibility(View.GONE);
                holder.productOldPrice.setVisibility(View.GONE);

                if (productsList.get(holder.getAdapterPosition()).getPercDis() != 0.0) {
                    holder.layoutDiscount.setVisibility(View.VISIBLE);
                    holder.productDiscountPercent.setVisibility(View.VISIBLE);
                    holder.productOldPrice.setVisibility(View.VISIBLE);
                    holder.productOldPrice.setPaintFlags(holder.productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.productDiscountPercent.setText(format.format(productsList.get(holder.getAdapterPosition()).getPercDis()) + "%");
                    holder.productOldPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences)));
                    double discountPrice = productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) * (productsList.get(position).getPercDis() / 100);
                    double newPrice = productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) - discountPrice;
                    holder.productPrice.setText(format.format(newPrice) + " ریال ");
                }
            }


            ArrayList<Unit> units = new ArrayList<>(unitList);
            CollectionUtils.filter(units, u -> u.getUomUid().equals(productsList.get(holder.getAdapterPosition()).UM1));
            if (units.size() > 0)
                holder.unit.setText(units.get(0).getUomName());
            else
                holder.unit.setText("");


            holder.tab = 0;
            holder.productImage.setOnClickListener(view -> {
                holder.tab++;
                if (holder.tab == 2) {
                    holder.tab = 0;
                    clickImage.onClick(productsList.get(holder.getAdapterPosition()).getI());
                }
            });

            double amount;
            if (ivDetail != null && ivDetail.INV_DET_QUANTITY != null)
                amount = ivDetail.INV_DET_QUANTITY;
            else
                amount = 0.0;
            if (checkRemainProduct.equals("1"))
                getMaxSale(holder.layoutAmount, holder.error, productsList.get(holder.getAdapterPosition()).getI(), amount);

            productsList.get(holder.getAdapterPosition()).setAmount(amount);
            if (amount > 0) {
                holder.ivMinus.setVisibility(View.VISIBLE);
                holder.ProductAmountTxt.setVisibility(View.VISIBLE);
            } else {
                holder.ivMinus.setVisibility(View.GONE);
                holder.ProductAmountTxt.setVisibility(View.GONE);
            }


            String description;
            if (ivDetail != null && ivDetail.INV_DET_DESCRIBTION != null)
                description = ivDetail.INV_DET_DESCRIBTION;
            else
                description = "";
            holder.edtDesc.setText(description);


            holder.ivMax.setOnClickListener(view -> {
                if (closeDateList != null && closeDateList.size() > 0 && closeDateList.contains(valueOfDay)) {
                    Toasty.warning(activity, "فروشگاه تعطیل می باشد.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                holder.ProductAmountTxt.setCursorVisible(false);

                if (checkRemainProduct.equals("1"))
                    holder.progressBar.setVisibility(View.VISIBLE);

                holder.ivMax.setEnabled(false);
                if (holder.getAdapterPosition() < productsList.size())
                    doAction(holder.getAdapterPosition(),
                            "",
                            1);
            });


            holder.ivMinus.setOnClickListener(v -> {
                if (closeDateList != null && closeDateList.size() > 0 && closeDateList.contains(valueOfDay)) {
                    Toasty.warning(activity, "فروشگاه تعطیل می باشد.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (checkRemainProduct.equals("1"))
                    holder.progressBar.setVisibility(View.VISIBLE);

                holder.ivMinus.setEnabled(false);

                holder.ProductAmountTxt.setCursorVisible(false);
                if (holder.getAdapterPosition() < productsList.size())
                    doAction(holder.getAdapterPosition(),
                            "",
                            2);

            });


            holder.ProductAmountTxt.setOnFocusChangeListener((view1, b) -> {
                holder.ProductAmountTxt.setCursorVisible(true);
            });


            if (holder.textWatcher1 == null) {
                holder.textWatcher1 = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        if (!charSequence.toString().isEmpty())
                            holder.ProductAmountTxt.setCursorVisible(true);

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

                        if (holder.getAdapterPosition() < productsList.size())
                            doAction(
                                    holder.getAdapterPosition(),
                                    s,
                                    3
                            );}
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
            }

            holder.ProductAmountTxt.removeTextChangedListener(holder.textWatcher1);
            holder.ProductAmountTxt.setText(df.format(amount));
            holder.ProductAmountTxt.addTextChangedListener(holder.textWatcher1);
            holder.ProductAmountTxt.setSelection(holder.ProductAmountTxt.getText().toString().length());

            holder.cardEdit.setOnClickListener(v -> {
                if (productsList.get(holder.getAdapterPosition()).getAmount() != null) {
                    descriptionItem.onDesc(productsList.get(holder.getAdapterPosition()).getI(), productsList.get(holder.getAdapterPosition()).getAmount());
                } else
                    showAlert("برای کالا مقدار وارد کنید.");
            });
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private int tab = 0;

        private final TextView productName;
        private final TextView unit;
        private final TextView error;


        private final TextView productPrice;
        private final TextView productOldPrice;
        private final TextView productDiscountPercent;

        private final EditText ProductAmountTxt;
        private final TextView edtDesc;
        private final RelativeLayout cardEdit;
        private final RoundedImageView productImage;
        private final RelativeLayout layoutAmount;

        private final ImageView ivMinus;
        private final ImageView ivMax;
        private final ProgressBar progressBar;
        private TextWatcher textWatcher1;
        private final RelativeLayout layoutDiscount;


        public viewHolder(View itemView) {
            super(itemView);
            cardEdit = itemView.findViewById(R.id.card_edit);

            edtDesc = itemView.findViewById(R.id.edt_description_temp);
            layoutDiscount = itemView.findViewById(R.id.layout_discount);

            productName = itemView.findViewById(R.id.order_recycle_item_product_name);
            unit = itemView.findViewById(R.id.unit);
            error = itemView.findViewById(R.id.error);

            productPrice = itemView.findViewById(R.id.order_recycle_item_product_price);
            productOldPrice = itemView.findViewById(R.id.order_recycle_item_product_old_price);
            productDiscountPercent = itemView.findViewById(R.id.order_recycle_item_product_discountPercent);

            productImage = itemView.findViewById(R.id.order_recycle_item_product_img);
            layoutAmount = itemView.findViewById(R.id.layoutAmount);
            ProductAmountTxt = itemView.findViewById(R.id.order_recycle_item_product_txt_amount);

            ivMinus = itemView.findViewById(R.id.iv_minus);
            ivMax = itemView.findViewById(R.id.iv_max);
            progressBar = itemView.findViewById(R.id.progress);

        }
    }
    //endregion Override Method


    //region Custom Method
    public void setCloseListDate(ArrayList<String> closeDateList) {
        this.closeDateList = closeDateList;
    }

    private void getMaxSales(int position, String s, int MinOrPlus) {
        String Prd_GUID = productsList.get(position).getI();
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

                                        double currentAmount = productsList.get(position).getAmount();
                                        double remain;
                                        try {
                                            assert jsonElement != null;
                                            remain = Integer.parseInt(jsonElement);
                                        } catch (Exception e) {
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);
                                            assert iDs != null;
                                            String description = iDs.getLogs().get(0).getDescription();
                                            Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
                                            return;
                                        }


                                        InvoiceDetail invDetails = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "' AND PRDUID ='" + Prd_GUID + "'").first();


                                        double mainCoef1 = productsList.get(position).getCoef1();
                                        double coef1 = mainCoef1;
                                        double coef2 = productsList.get(position).getCoef2();

                                        if (defaultCoff.equals("1")) {
                                            coef2 = mainCoef1;
                                        } else if (defaultCoff.equals("2")) {
                                            coef1 = coef2;
                                        }

                                        double amount ;

                                        if (remain <= 0) {
                                            productsList.get(position).setAmount(0.0);
                                            showAlert("ناموجود");
                                            amount = 0.0;
                                        }


                                        else if (MinOrPlus == 1) {
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
                                                productsList.get(position).setAmount(0.0);
                                                amount = 0.0;
                                            }

                                        }


                                        productsList.get(position).setAmount(amount);
                                        if (Integer.parseInt(jsonElement) - amount < 0) {
                                            showAlert("مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + jsonElement);


                                            if (remain % mainCoef1 != 0)
                                                remain = 0.0;

                                            productsList.get(position).setAmount(remain);

                                            if (invDetails != null) {

                                                invDetails.INV_DET_QUANTITY = remain;
                                                if (remain != 0.0)
                                                    invDetails.update();
                                                else
                                                    invDetails.delete();

                                                notifyItemChanged(position);
                                                clickItem.onClick(Prd_GUID);

                                            }
                                            return;
                                        }

                                        //region Edit InvoiceDetail
                                        if (invDetails != null) {

                                            if (amount == 0) {
                                                invDetails.delete();
                                                notifyItemChanged(position);
                                                clickItem.onClick(Prd_GUID);
                                                return;
                                            }

                                            invDetails.INV_DET_QUANTITY = amount;
                                            invDetails.update();
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
            showAlert( "خطا در دریافت اطلاعات مانده کالا");
            notifyItemChanged(position);
        }
    }


    private void getMaxSale(RelativeLayout layoutAmount, TextView txtError, String Prd_GUID, double amount) {
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
                                            if ((remain <= 0.0 && txtError.getText().toString().equals(""))) {
                                                txtError.setText("ناموجود");
                                                layoutAmount.setVisibility(View.GONE);

                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("PRDUID ='" + Prd_GUID + "'").first();
                                                if (invoiceDetail != null) {
                                                    InvoiceDetail.deleteInTx(invoiceDetail);
                                                    setAmountToProductItem(Prd_GUID,0.0);
                                                }
                                            }

                                            else if (remain < amount) {
                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("PRDUID ='" + Prd_GUID + "'").first();
                                                if (invoiceDetail != null) {
                                                    invoiceDetail.INV_DET_QUANTITY = remain;
                                                    invoiceDetail.update();
                                                    setAmountToProductItem(Prd_GUID,amount);
                                                }

                                            }

                                            else {
                                                txtError.setText("");
                                                layoutAmount.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        catch (Exception e) {
                                            Gson gson = new Gson();
                                            Type typeIDs = new TypeToken<ModelLog>() {
                                            }.getType();
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                                            assert iDs != null;
                                            int message = iDs.getLogs().get(0).getMessage();
                                            String description = iDs.getLogs().get(0).getDescription();
                                            if (message != 1)
                                                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    , throwable -> {

                                    })
            );
        } catch (Exception ignored) {
        }
    }


    private void setAmountToProductItem(String id,double amount){
        ArrayList<Product> resPrd=new ArrayList<>(productsList);
        CollectionUtils.filter(resPrd,r->r.getI().equals(id));
        if (resPrd.size()>0) {
            productsList.get(productsList.indexOf(resPrd.get(0))).setAmount(amount);
            notifyItemChanged(productsList.indexOf(resPrd.get(0)));
        }
    }
    private void doAction(int position, String s, int MinOrPlus) {

        double currentAmount = productsList.get(position).getAmount();

        if (checkRemainProduct.equals("1"))
            getMaxSales(position, s, MinOrPlus);

        else {
            double amount ;
            double mainCoef1 = productsList.get(position).getCoef1();
            double coef1 = mainCoef1;
            double coef2 = productsList.get(position).getCoef2();

            if (defaultCoff.equals("1"))
                coef2 = mainCoef1;
            else if (defaultCoff.equals("2"))
                coef1 = coef2;


            //region MaxAmount
            if (MinOrPlus == 1) {
                if (currentAmount > 0.0 && coef2 != 0.0)
                    coef1 = coef2;
                amount = currentAmount + coef1;
            }
            //endregion MaxAmount


            //region MinAmount
            else if (MinOrPlus == 2) {
                if (currentAmount > coef1 && coef2 != 0.0)
                    amount = currentAmount - coef2;
                else if (currentAmount >= coef1)
                    amount = currentAmount - coef1;

                else
                    return;
            }
            //endregion MinAmount


            //region EditAmount
            else {
                amount = Float.parseFloat(s.equals("") ? "0" : s);
                if (
                        (coef2 != 0.0 && amount < coef1)
                                || (coef2 == 0 && amount % coef1 != 0)
                                || (coef2 == 0 && amount % coef1 == 0 && amount < coef1)
                ) {

                    showAlert(" مقدار وارد شده باید ضریبی از " + mainCoef1 + " باشد.");
                    amount = 0;
                }

            }
            //endregion EditAmount


            productsList.get(position).setAmount(amount);

            String Prd_GUID = productsList.get(position).getI();
            InvoiceDetail invDetails = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "' AND PRDUID ='" + Prd_GUID + "'").first();

            //region Edit InvoiceDetail
            if (invDetails != null) {
                if (amount == 0) {
                    invDetails.delete();

                    notifyItemChanged(position);
                    clickItem.onClick(Prd_GUID);
                    return;
                }

                if (invDetails != null) {
                    invDetails.INV_DET_QUANTITY = amount;
                    invDetails.update();
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
    }

    private void showAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("بستن", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

       setStyleTextAlert(alertDialog);
    }
    private void setStyleTextAlert(AlertDialog alert){
        Typeface face = Typeface.createFromAsset(activity.getAssets(), "iransans.ttf");

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTypeface(face);
        textView.setTextColor(activity.getResources().getColor(R.color.medium_color));
        textView.setTextSize(13);
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.color_accent));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.color_accent));

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(face);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(face);

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(12);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(12);
    }
    //endregion Custom Method

}