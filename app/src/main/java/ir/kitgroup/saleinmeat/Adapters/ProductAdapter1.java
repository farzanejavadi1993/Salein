package ir.kitgroup.saleinmeat.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
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
import java.util.List;

import java.util.UUID;



import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinmeat.Connect.API;
import ir.kitgroup.saleinmeat.DataBase.Unit;
import ir.kitgroup.saleinmeat.classes.Util;

import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;

import ir.kitgroup.saleinmeat.Fragments.ShowDetailFragment;

import ir.kitgroup.saleinmeat.models.Company;
import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.models.Product;


public class ProductAdapter1 extends RecyclerView.Adapter<ProductAdapter1.viewHolder> {

    private final Activity context;

    private final Company company;

    private final SharedPreferences sharedPreferences;

    private  CompositeDisposable compositeDisposable ;


    private final List<Product> productsList;

    private String maxSale;

    private Boolean Seen = false;

    private final String Inv_GUID;
    private String Tbl_GUID;

    private final DecimalFormat df;

    private int fontSize = 0;
    private int fontLargeSize = 0;


    private final API api;

    private  List<Unit> unitList;


    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    public interface ClickItem {
        void onClick();
        //1     PLUS AMOUNT
        //2     MINUS AMOUNT
        //3     Edit AMOUNT
    }

    private ClickItem clickItem;

    public void setOnClickListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }


    public interface Descriptions {
        void onDesc(String GUID, double amount);

    }

    private Descriptions descriptionItem;

    public void setOnDescriptionItem(Descriptions descriptionItem) {
        this.descriptionItem = descriptionItem;
    }


    public ProductAdapter1(Activity context, List<Product> productsList, Company company, API api, SharedPreferences sharedPreferences, String Inv_GUID) {
        this.context = context;
        this.Inv_GUID = Inv_GUID;

        this.productsList = productsList;
        this.company = company;
        this.sharedPreferences = sharedPreferences;
        this.api = api;

        df = new DecimalFormat();
        compositeDisposable = new CompositeDisposable();
        unitList=Select.from(Unit.class).list();


    }

    public void setMaxSale(String MaxSale) {
        this.maxSale = MaxSale;
    }

    public void setType(Boolean Seen) {
        this.Seen = Seen;
    }


    public void setTbl_GUID(String Tbl_GUID) {
        this.Tbl_GUID = Tbl_GUID;
    }

    public void Add(ArrayList<Product> arrayList) {
        productsList.addAll(arrayList);
    }

    public void addLoadingView() {
        new Handler().post(() -> {
            productsList.add(null);
            notifyItemInserted(productsList.size() - 1);
        });
    }

    public void removeLoadingView() {
        productsList.remove(productsList.size() - 1);
        notifyItemRemoved(productsList.size());
    }

    @Override
    public int getItemCount() {
        return productsList == null ? 0 : productsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return productsList.get(position) == null ? Util.VIEW_TYPE_LOADING : Util.VIEW_TYPE_ITEM;
    }


    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        if (viewType == Util.VIEW_TYPE_ITEM) {
            if (Util.screenSize >= 7) {
                fontSize = 13;
                fontLargeSize = 14;

            } else {
                fontSize = 11;
                fontLargeSize = 12;

            }
            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_products_item_mobile, parent,
                    false));
        } else if (viewType == Util.VIEW_TYPE_LOADING) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_loading, parent, false);
            return new viewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {


        if (productsList.get(position) != null) {
           // holder.error.setText("");


            InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "' AND PRDUID ='" + productsList.get(position).getI() + "'").first();


            String ip = company.ipLocal;





            Picasso.get()
                    .load("http://" + ip + "/GetImage?productId=" + productsList
                            .get(position).getI()+"&width=200&height=200")
                    .error(company.imageLogo)
                    .placeholder(R.drawable.loading)
                    .into(holder.productImage);





            holder.productOldPrice.setTextSize(13);
            holder.productDiscountPercent.setTextSize(fontLargeSize);
            holder.Line.setTextSize(fontSize);
            holder.productPrice.setTextSize(fontLargeSize);
            holder.edtDesc.setTextSize(fontSize);
            holder.ProductAmountTxt.setTextSize(fontSize);
            holder.unit.setTextSize(fontSize);





            ArrayList<Unit> units=new ArrayList<>(unitList);
            CollectionUtils.filter(units,u->u.getUomUid().equals(productsList.get(position).UM1));
            if (units.size()>0)
                holder.unit.setText(units.get(0).getUomName());
            else
                holder.unit.setText("");


            holder.productName.setText(productsList.get(position).getN());



            if (productsList.get(position).getPrice(sharedPreferences) > 0) {

                holder.productPrice.setText(format.format(productsList.get(position).getPrice(sharedPreferences)) + " ریال ");

                if (productsList.get(position).getPercDis() != 0.0) {
                    holder.layoutDiscount.setVisibility(View.VISIBLE);
                    holder.productDiscountPercent.setVisibility(View.VISIBLE);
                    holder.productOldPrice.setVisibility(View.VISIBLE);
                    holder.Line.setVisibility(View.VISIBLE);
                    holder.productDiscountPercent.setText(format.format(productsList.get(position).getPercDis()) + "%");
                    holder.productOldPrice.setText(format.format(productsList.get(position).getPrice(sharedPreferences)));
                    holder.Line.setText("---------------");
                    double discountPrice = productsList.get(position).getPrice(sharedPreferences) * (productsList.get(position).getPercDis() / 100);
                    double newPrice = productsList.get(position).getPrice(sharedPreferences) - discountPrice;
                    holder.productPrice.setText(format.format(newPrice) + " ریال ");
                }




            }









            holder.tab = 0;
            holder.productImage.setOnClickListener(view -> {
                holder.tab++;
                if (holder.tab == 2) {
                    holder.tab = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", productsList.get(position).getI());
                    ShowDetailFragment showDetailFragment = new ShowDetailFragment();
                    showDetailFragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, showDetailFragment, "ShowDetailFragment").addToBackStack("ShowDetailF").commit();
                }

            });


            double amount;
            String description;

            if (invoiceDetail != null && invoiceDetail.INV_DET_QUANTITY != null) {
                amount = invoiceDetail.INV_DET_QUANTITY;

                if (amount > 0 && Seen) {
                    if (invoiceDetail.INV_DET_PERCENT_DISCOUNT != null && invoiceDetail.INV_DET_PERCENT_DISCOUNT != 0.0) {
                        if (productsList.get(position).getPrice(sharedPreferences) > 0) {
                            holder.layoutDiscount.setVisibility(View.VISIBLE);
                            holder.productDiscountPercent.setVisibility(View.VISIBLE);
                            holder.productOldPrice.setVisibility(View.VISIBLE);
                            holder.Line.setVisibility(View.VISIBLE);

                            holder.productDiscountPercent.setText(format.format(invoiceDetail.INV_DET_PERCENT_DISCOUNT) + "%");
                            holder.productOldPrice.setText(format.format(productsList.get(position).getPrice(sharedPreferences)));
                            holder.Line.setText("------------");
                            double discountPrice = productsList.get(position).getPrice(sharedPreferences) * (invoiceDetail.INV_DET_PERCENT_DISCOUNT / 100);
                            double newPrice = productsList.get(position).getPrice(sharedPreferences) - discountPrice;
                            holder.productPrice.setText(format.format(newPrice) + " ریال ");
                        }
                    } else {
                        if (productsList.get(position).getPrice(sharedPreferences) > 0) {

                            holder.productDiscountPercent.setVisibility(View.GONE);
                            holder.layoutDiscount.setVisibility(View.GONE);
                            holder.productOldPrice.setVisibility(View.GONE);
                            holder.Line.setVisibility(View.GONE);
                            holder.productPrice.setText(format.format(productsList.get(position).getPrice(sharedPreferences)) + " ریال ");
                        }
                    }
                }
            } else
                amount = 0.0;


            if (maxSale.equals("1"))
                getMaxSale(holder.layoutAmount, holder.error, productsList.get(position).getI(), amount);


            if (invoiceDetail != null && invoiceDetail.INV_DET_DESCRIBTION != null)
                description = invoiceDetail.INV_DET_DESCRIBTION;
            else
                description = "";


            holder.edtDesc.setText(description);
            productsList.get(position).setAmount(amount);


            if (amount > 0) {
                holder.ivMinus.setVisibility(View.VISIBLE);
                holder.ProductAmountTxt.setVisibility(View.VISIBLE);

            } else {
                holder.ivMinus.setVisibility(View.GONE);
                holder.ProductAmountTxt.setVisibility(View.GONE);

            }


            holder.ivMax.setOnClickListener(view -> doAction(
                    productsList.get(position).getAmount(),
                    position,
                    holder.error,
                    holder.progressBar,
                    holder.textWatcher,
                    holder.ProductAmountTxt,
                    holder.ivMinus,
                    company,
                    maxSale,
                    productsList.get(position).getI(),
                    "",
                    1
            ));


            holder.ivMinus.setOnClickListener(v -> doAction(productsList.get(position).getAmount(),
                    position,
                    holder.error,
                    holder.progressBar,
                    holder.textWatcher,
                    holder.ProductAmountTxt,
                    holder.ivMinus,
                    company,
                    maxSale,
                    productsList.get(position).getI(),
                    "",
                    2

            ));


            if (holder.textWatcher == null) {
                holder.textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
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

                        doAction(productsList.get(position).getAmount(),
                                position,
                                holder.error,
                                holder.progressBar,
                                holder.textWatcher,
                                holder.ProductAmountTxt,
                                holder.ivMinus,
                                company,
                                maxSale,
                                productsList.get(position).getI(),
                                s,
                                3

                        );


                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };
            }


            holder.ProductAmountTxt.removeTextChangedListener(holder.textWatcher);
            holder.ProductAmountTxt.setText(df.format(amount));


            holder.ProductAmountTxt.addTextChangedListener(holder.textWatcher);


            holder.cardEdit.setOnClickListener(v -> {
                if (productsList.get(position).getAmount() != null) {
                    descriptionItem.onDesc(productsList.get(position).getI(), productsList.get(position).getAmount());
                } else {
                    Toast.makeText(context, " برای کالا  مقداروارد کنید", Toast.LENGTH_SHORT).show();
                }
            });





        }


    }


    static class viewHolder extends RecyclerView.ViewHolder {

        private int tab = 0;

        private final TextView productName;
        private final TextView unit;
        private final TextView error;


        private final TextView productPrice;
        private final TextView productOldPrice;
        private final TextView productDiscountPercent;
        private final TextView Line;
        private final EditText ProductAmountTxt;
        private final TextView edtDesc;
        private final RelativeLayout cardEdit;
        private final RoundedImageView productImage;
        private final ConstraintLayout layoutAmount;

        private final ImageView ivMinus;
        private final ImageView ivMax;
        private final ProgressBar progressBar;
        private TextWatcher textWatcher;
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
            Line = itemView.findViewById(R.id.order_recycle_item_product_line);


            productImage = itemView.findViewById(R.id.order_recycle_item_product_img);
            layoutAmount = itemView.findViewById(R.id.layoutAmount);
            ProductAmountTxt = itemView.findViewById(R.id.order_recycle_item_product_txt_amount);


            ivMinus = itemView.findViewById(R.id.iv_minus);
            ivMax = itemView.findViewById(R.id.iv_max);
            progressBar = itemView.findViewById(R.id.progress);


        }
    }


    private void getMaxSales(double amount1, int position, TextView error, ProgressBar progressBar, TextWatcher textWatcher, EditText ProductAmountTxt, ImageView ivMinus, Company company, String Prd_GUID, String s, int MinOrPlus) {

        progressBar.setVisibility(View.VISIBLE);
        double aPlus = productsList.get(position).getCoef();
        if (aPlus == 0)
            aPlus = 1;
        double finalAPlus = aPlus;

        try {
            compositeDisposable.add(
                    api.getMaxSales(company.userName, company.passWord, Prd_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        progressBar.setVisibility(View.GONE);

                                        double remain = -1000000000;
                                        try {
                                            assert jsonElement != null;
                                            remain = Integer.parseInt(jsonElement);
                                        } catch (Exception e) {
                                            Gson gson = new Gson();
                                            Type typeIDs = new TypeToken<ModelLog>() {
                                            }.getType();
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                                            assert iDs != null;
                                            int message = iDs.getLogs().get(0).getMessage();
                                            String description = iDs.getLogs().get(0).getDescription();
                                            if (message != 1)
                                                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();


                                        }

                                        if (remain != -1000000000) {

                                            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list();
                                            ArrayList<InvoiceDetail> resultInvoice = new ArrayList<>(invDetails);
                                            CollectionUtils.filter(resultInvoice, r -> r.PRD_UID.equals(Prd_GUID));


                                            if (remain <= 0) {
                                                productsList.get(position).setAmount(0.0);
                                                if (MinOrPlus != 3) {
                                                    ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                    ProductAmountTxt.setText("0");

                                                    ProductAmountTxt.addTextChangedListener(textWatcher);
                                                    ivMinus.setVisibility(View.GONE);
                                                    ProductAmountTxt.setVisibility(View.GONE);
                                                }
                                                if (resultInvoice.size() > 0) {
                                                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + resultInvoice.get(0).INV_DET_UID + "'").first();
                                                    if (invoiceDetail != null) {
                                                        invoiceDetail.delete();
                                                        clickItem.onClick();
                                                    }
                                                }

                                                error.setText("این کالا موجود نمی باشد");
                                                progressBar.setVisibility(View.GONE);
                                                return;
                                            }


                                            double amount = 0;
                                            if (MinOrPlus == 1)
                                                amount = amount1 + finalAPlus;


                                            else if (MinOrPlus == 2) {
                                                if (amount1 >= finalAPlus)
                                                    amount = amount1 - finalAPlus;
                                                else
                                                    return;


                                            } else {
                                                try {
                                                    amount = Float.parseFloat(s);
                                                    if (amount % finalAPlus != 0) {
                                                        Toast.makeText(context, " مقدار وارد شده باید ضریبی از " + finalAPlus + " باشد ", Toast.LENGTH_SHORT).show();
                                                        ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                        ProductAmountTxt.setText("0");

                                                        ProductAmountTxt.addTextChangedListener(textWatcher);
                                                        ivMinus.setVisibility(View.GONE);
                                                        ProductAmountTxt.setVisibility(View.GONE);
                                                        productsList.get(position).setAmount(0.0);
                                                        return;
                                                    }


                                                } catch (Exception ignored) {

                                                }
                                            }


                                            productsList.get(position).setAmount(amount);

                                            if (Integer.parseInt(jsonElement) - amount < 0) {
                                                Toast.makeText(context, "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + jsonElement, Toast.LENGTH_SHORT).show();

                                                if (remain % finalAPlus != 0) {

                                                    remain = (int) (remain / finalAPlus) * finalAPlus;

                                                }

                                                productsList.get(position).setAmount(remain);


                                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                ProductAmountTxt.setText(df.format(remain));



                                                ProductAmountTxt.addTextChangedListener(textWatcher);


                                                if (resultInvoice.size() > 0) {
                                                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + resultInvoice.get(0).INV_DET_UID + "'").first();
                                                    if (invoiceDetail != null) {
                                                        invoiceDetail.INV_DET_QUANTITY = remain;
                                                        invoiceDetail.update();
                                                        clickItem.onClick();
                                                    }


                                                }

                                                progressBar.setVisibility(View.GONE);

                                                return;
                                            }


                                            //edit row
                                            if (resultInvoice.size() > 0) {
                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + resultInvoice.get(0).INV_DET_UID + "'").first();
                                                if (amount == 0) {
                                                    if (invoiceDetail != null)
                                                        invoiceDetail.delete();


                                                    if (MinOrPlus != 3) {
                                                        ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                        ProductAmountTxt.setText("0");

                                                        ProductAmountTxt.addTextChangedListener(textWatcher);
                                                        ivMinus.setVisibility(View.GONE);
                                                        ProductAmountTxt.setVisibility(View.GONE);
                                                    }


                                                    clickItem.onClick();
                                                    return;
                                                }


                                                if (invoiceDetail != null) {
                                                    invoiceDetail.INV_DET_QUANTITY = amount;
                                                    invoiceDetail.update();
                                                }

                                                if (MinOrPlus != 3) {
                                                    ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                    ProductAmountTxt.setText(df.format(amount));

                                                    ProductAmountTxt.addTextChangedListener(textWatcher);
                                                }

                                            }
                                            //create row
                                            else {
                                                InvoiceDetail invoicedetail = new InvoiceDetail();
                                                invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                                                invoicedetail.INV_UID = Inv_GUID;
                                                invoicedetail.INV_DET_QUANTITY = amount;
                                                invoicedetail.PRD_UID = Prd_GUID;
                                                if (company.mode == 1)
                                                    invoicedetail.TBL = Tbl_GUID;
                                                invoicedetail.save();


                                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                ProductAmountTxt.setText(df.format(amount));

                                                ProductAmountTxt.addTextChangedListener(textWatcher);

                                            }
                                            ivMinus.setVisibility(View.VISIBLE);
                                            ProductAmountTxt.setVisibility(View.VISIBLE);


                                            clickItem.onClick();


                                        }

                                    }
                                    , throwable -> {
                                        Toast.makeText(context, "خطا در دریافت اطلاعات مانده کالا", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);


                                    })
            );
        } catch (Exception e) {
            Toast.makeText(context, "خطا در دریافت اطلاعات مانده کالا", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }


    }


    private void getMaxSale(ConstraintLayout layoutAmount, TextView txtError, String Prd_GUID,double amount) {


        try {
            compositeDisposable.add(
                    api.getMaxSales(company.userName, company.passWord, Prd_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        double remain ;

                                        try {
                                            assert jsonElement != null;
                                            remain = Integer.parseInt(jsonElement);
                                            if ((remain == 0.0 && txtError.getText().toString().equals("")) ) {
                                                txtError.setText("این کالا موجود نمی باشد");
                                                layoutAmount.setVisibility(View.GONE);

                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("PRDUID ='" + Prd_GUID+ "'").first();
                                                if (invoiceDetail!=null){
                                                    InvoiceDetail.deleteInTx(invoiceDetail);
                                                }


                                            }
                                            else if(remain > 0 && remain < amount){
                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("PRDUID ='" + Prd_GUID+ "'").first();
                                                if (invoiceDetail!=null){
                                                    invoiceDetail.INV_DET_QUANTITY=remain;
                                                    invoiceDetail.update();
                                                }
                                            }
                                            else {
                                                txtError.setText("");
                                                layoutAmount.setVisibility(View.VISIBLE);
                                            }


                                        } catch (Exception e) {
                                            Gson gson = new Gson();
                                            Type typeIDs = new TypeToken<ModelLog>() {
                                            }.getType();
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);


                                            assert iDs != null;
                                            int message = iDs.getLogs().get(0).getMessage();
                                            String description = iDs.getLogs().get(0).getDescription();
                                            if (message != 1)
                                                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                    , throwable -> {


                                    })
            );
        } catch (Exception ignored) {

        }


    }


    private void doAction(double amount1, int position, TextView error, ProgressBar progressBar, TextWatcher textWatcher, EditText ProductAmountTxt, ImageView ivMinus, Company company, String maxSales, String Prd_GUID, String s, int MinOrPlus) {

        error.setText("");
        if (position < 0)
            return;
        if (maxSales.equals("1")) {
            getMaxSales(amount1, position, error, progressBar, textWatcher, ProductAmountTxt,  ivMinus, company, Prd_GUID, s, MinOrPlus);
        } else {
            double aPlus = productsList.get(position).getCoef();
            if (aPlus == 0)
                aPlus = 1;


            double amount = 0;
            if (MinOrPlus == 1) {

                amount = amount1 + aPlus;
            } else if (MinOrPlus == 2) {
                if (amount1 >= aPlus)
                    amount = amount1 - aPlus;

                else
                    return;

            } else {
                try {
                    amount = Float.parseFloat(s);
                    if (amount % aPlus != 0) {
                        Toast.makeText(context, " مقدار وارد شده باید ضریبی از " + aPlus + " باشد.", Toast.LENGTH_SHORT).show();
                        amount = 0;
                        ProductAmountTxt.removeTextChangedListener(textWatcher);
                        ProductAmountTxt.setText("0");

                        ProductAmountTxt.addTextChangedListener(textWatcher);
                        ivMinus.setVisibility(View.GONE);
                        ProductAmountTxt.setVisibility(View.GONE);
                    }
                } catch (Exception ignored) {

                }
            }

            productsList.get(position).setAmount(amount);
            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list();
            ArrayList<InvoiceDetail> result = new ArrayList<>(invDetails);
            CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));

            //edit
            if (result.size() > 0) {

                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();

                if (amount == 0) {

                    if (invoiceDetail != null)

                        invoiceDetail.delete();

                    if (MinOrPlus != 3) {

                        ProductAmountTxt.removeTextChangedListener(textWatcher);
                        ProductAmountTxt.setText("0");

                        ProductAmountTxt.addTextChangedListener(textWatcher);
                        ivMinus.setVisibility(View.GONE);
                        ProductAmountTxt.setVisibility(View.GONE);
                    }

                    clickItem.onClick();
                    return;
                }

                if (invoiceDetail != null) {

                    invoiceDetail.INV_DET_QUANTITY = amount;
                    invoiceDetail.update();
                }

                if (MinOrPlus != 3) {

                    ProductAmountTxt.removeTextChangedListener(textWatcher);
                    ProductAmountTxt.setText(df.format(amount));

                    ProductAmountTxt.addTextChangedListener(textWatcher);
                }
            }

            //Create
            else {

                InvoiceDetail invoicedetail = new InvoiceDetail();
                invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                invoicedetail.INV_UID = Inv_GUID;
                invoicedetail.INV_DET_QUANTITY = amount;
                invoicedetail.PRD_UID = Prd_GUID;

                invoicedetail.save();
                ProductAmountTxt.removeTextChangedListener(textWatcher);
                ProductAmountTxt.setText(df.format(amount));

                ProductAmountTxt.addTextChangedListener(textWatcher);
                clickItem.onClick();
            }

            ivMinus.setVisibility(View.VISIBLE);
            ProductAmountTxt.setVisibility(View.VISIBLE);

        }
    }


}





