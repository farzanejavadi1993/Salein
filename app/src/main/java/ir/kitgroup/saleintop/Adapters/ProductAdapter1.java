package ir.kitgroup.saleintop.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import ir.kitgroup.saleintop.Connect.API;
import ir.kitgroup.saleintop.DataBase.Unit;
import ir.kitgroup.saleintop.classes.Util;

import ir.kitgroup.saleintop.DataBase.InvoiceDetail;

import ir.kitgroup.saleintop.Fragments.ShowDetailFragment;

import ir.kitgroup.saleintop.DataBase.Company;
import ir.kitgroup.saleintop.models.Config;
import ir.kitgroup.saleintop.models.ModelLog;
import ir.kitgroup.saleintop.R;
import ir.kitgroup.saleintop.models.Product;


public class ProductAdapter1 extends RecyclerView.Adapter<ProductAdapter1.viewHolder> {

    private final Activity context;

    private final Company company;

    private final Config config;

    private final SharedPreferences sharedPreferences;

    private CompositeDisposable compositeDisposable;


    private final List<Product> productsList;

    private String maxSale;

    private Boolean Seen = false;

    private final String Inv_GUID;
    private String Tbl_GUID;

    private final DecimalFormat df;

    private int fontSize = 0;
    private int fontLargeSize = 0;


    private API api;

    private List<Unit> unitList;

    private String fragmentName = "main";

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    public interface ClickItem {
        void onClick(String Prd_UID);
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


    public ProductAdapter1(Activity context, List<Product> productsList, Company company, SharedPreferences sharedPreferences, String Inv_GUID, Config config) {
        this.context = context;
        this.Inv_GUID = Inv_GUID;

        this.config = config;
        this.productsList = productsList;
        this.company = company;
        this.sharedPreferences = sharedPreferences;


        df = new DecimalFormat();
        compositeDisposable = new CompositeDisposable();
        unitList = Select.from(Unit.class).list();


    }

    public void setMaxSale(String MaxSale) {
        this.maxSale = MaxSale;
    }

    public void setType(Boolean Seen) {
        this.Seen = Seen;
    }

    public void setApi(API api) {
        this.api = api;
    }


    public void setTbl_GUID(String Tbl_GUID) {
        this.Tbl_GUID = Tbl_GUID;
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
            holder.error.setText("");



            fragmentName=sharedPreferences.getString("FNM","main");

            if (!fragmentName.equals("main"))
                holder.ProductAmountTxt.clearFocus();



            InvoiceDetail ivDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "' AND PRDUID ='" + productsList.get(holder.getAdapterPosition()).getI() + "'").first();


            String ip = company.IP1;


            Picasso.get()
                    .load("http://" + ip + "/GetImage?productId=" + productsList
                            .get(holder.getAdapterPosition()).getI() + "&width=200&height=200")
                    .error(config.imageIcon)
                    .placeholder(config.INSKU_ID.equals("ir.kitgroup.saleinmeat") ? R.drawable.donyavilaoding : R.drawable.loading)
                    .into(holder.productImage);


            holder.productOldPrice.setTextSize(13);
            holder.productDiscountPercent.setTextSize(fontLargeSize);
            holder.Line.setTextSize(fontSize);
            holder.productPrice.setTextSize(fontLargeSize);
            holder.edtDesc.setTextSize(fontSize);
            holder.ProductAmountTxt.setTextSize(fontSize);
            holder.unit.setTextSize(fontSize);


            ArrayList<Unit> units = new ArrayList<>(unitList);
            CollectionUtils.filter(units, u -> u.getUomUid().equals(productsList.get(holder.getAdapterPosition()).UM1));
            if (units.size() > 0)
                holder.unit.setText(units.get(0).getUomName());
            else
                holder.unit.setText("");


            holder.productName.setText(productsList.get(holder.getAdapterPosition()).getN());


            if (productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) > 0) {

                holder.productPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences)) + " ریال ");
                holder.productDiscountPercent.setText("");
                holder.productOldPrice.setText("");
                holder.layoutDiscount.setVisibility(View.GONE);
                holder.productDiscountPercent.setVisibility(View.GONE);
                holder.productOldPrice.setVisibility(View.GONE);
                holder.Line.setVisibility(View.GONE);
                if (productsList.get(holder.getAdapterPosition()).getPercDis() != 0.0) {
                    holder.layoutDiscount.setVisibility(View.VISIBLE);
                    holder.productDiscountPercent.setVisibility(View.VISIBLE);
                    holder.productOldPrice.setVisibility(View.VISIBLE);
                    holder.Line.setVisibility(View.VISIBLE);
                    holder.productDiscountPercent.setText(format.format(productsList.get(holder.getAdapterPosition()).getPercDis()) + "%");
                    holder.productOldPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences)));
                    holder.Line.setText("---------------");
                    double discountPrice = productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) * (productsList.get(position).getPercDis() / 100);
                    double newPrice = productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) - discountPrice;
                    holder.productPrice.setText(format.format(newPrice) + " ریال ");
                }


            }


            holder.tab = 0;
            holder.productImage.setOnClickListener(view -> {
                holder.tab++;
                if (holder.tab == 2) {
                    holder.tab = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", productsList.get(holder.getAdapterPosition()).getI());
                    ShowDetailFragment showDetailFragment = new ShowDetailFragment();
                    showDetailFragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, showDetailFragment, "ShowDetailFragment").addToBackStack("ShowDetailF").commit();
                }

            });


            double amount;
            String description;

            if (ivDetail != null && ivDetail.INV_DET_QUANTITY != null) {
                amount = ivDetail.INV_DET_QUANTITY;

                if (amount > 0 && Seen) {
                    if (ivDetail.INV_DET_PERCENT_DISCOUNT != null && ivDetail.INV_DET_PERCENT_DISCOUNT != 0.0) {
                        if (productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) > 0) {
                            holder.layoutDiscount.setVisibility(View.VISIBLE);
                            holder.productDiscountPercent.setVisibility(View.VISIBLE);
                            holder.productOldPrice.setVisibility(View.VISIBLE);
                            holder.Line.setVisibility(View.VISIBLE);

                            holder.productDiscountPercent.setText(format.format(ivDetail.INV_DET_PERCENT_DISCOUNT) + "%");
                            holder.productOldPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences)));
                            holder.Line.setText("------------");
                            double discountPrice = productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) * (ivDetail.INV_DET_PERCENT_DISCOUNT / 100);
                            double newPrice = productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) - discountPrice;
                            holder.productPrice.setText(format.format(newPrice) + " ریال ");
                        }
                    } else {
                        if (productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences) > 0) {
                            holder.productDiscountPercent.setVisibility(View.GONE);
                            holder.layoutDiscount.setVisibility(View.GONE);
                            holder.productOldPrice.setVisibility(View.GONE);
                            holder.Line.setVisibility(View.GONE);
                            holder.productPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice(sharedPreferences)) + " ریال ");
                        }
                    }
                }
            } else
                amount = 0.0;


            if (maxSale.equals("1"))
                getMaxSale(holder.layoutAmount, holder.error, productsList.get(holder.getAdapterPosition()).getI(), amount);


            if (ivDetail != null && ivDetail.INV_DET_DESCRIBTION != null)
                description = ivDetail.INV_DET_DESCRIBTION;
            else
                description = "";


            holder.edtDesc.setText(description);
            productsList.get(holder.getAdapterPosition()).setAmount(amount);


            if (amount > 0) {
                holder.ivMinus.setVisibility(View.VISIBLE);
                holder.ProductAmountTxt.setVisibility(View.VISIBLE);

            } else {
                holder.ivMinus.setVisibility(View.GONE);
                holder.ProductAmountTxt.setVisibility(View.GONE);

            }


            holder.ivMax.setOnClickListener(view ->
                    {


                        holder.ProductAmountTxt.setCursorVisible(false);

                        if (holder.getAdapterPosition() < productsList.size())
                            doAction(
                                    productsList.get(holder.getAdapterPosition()).getAmount(),
                                    holder.getAdapterPosition(),
                                    holder.error,
                                    holder.progressBar,
                                    holder.textWatcher1,
                                    holder.ProductAmountTxt,
                                    holder.ivMinus,
                                    company,
                                    maxSale,
                                    productsList.get(holder.getAdapterPosition()).getI(),
                                    "",
                                    1
                            );
                    }
            );


            holder.ivMinus.setOnClickListener(v ->
                    {

                        holder.ProductAmountTxt.setCursorVisible(false);
                        if (holder.getAdapterPosition() < productsList.size())
                            doAction(productsList.get(holder.getAdapterPosition()).getAmount(),
                                    holder.getAdapterPosition(),
                                    holder.error,
                                    holder.progressBar,
                                    holder.textWatcher1,
                                    holder.ProductAmountTxt,
                                    holder.ivMinus,
                                    company,
                                    maxSale,
                                    productsList.get(holder.getAdapterPosition()).getI(),
                                    "",
                                    2

                            );
                    }
            );


            holder.ProductAmountTxt.setOnFocusChangeListener((view1, b) -> {

                holder.ProductAmountTxt.setCursorVisible(true);
            });



            if (holder.textWatcher1 == null) {
                holder.textWatcher1 = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        int p = 0;
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

                            if (holder.getAdapterPosition() < productsList.size())
                                doAction(productsList.get(holder.getAdapterPosition()).getAmount(),
                                        holder.getAdapterPosition(),
                                        holder.error,
                                        holder.progressBar,
                                        holder.textWatcher1,
                                        holder.ProductAmountTxt,
                                        holder.ivMinus,
                                        company,
                                        maxSale,
                                        productsList.get(holder.getAdapterPosition()).getI(),
                                        s,
                                        3
                                );



                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int t = 0;
                    }
                };
            }


            holder.ProductAmountTxt.removeTextChangedListener(holder.textWatcher1);
            holder.ProductAmountTxt.setText(df.format(amount));
            holder.ProductAmountTxt.addTextChangedListener(holder.textWatcher1);


            holder.cardEdit.setOnClickListener(v -> {
                if (productsList.get(holder.getAdapterPosition()).getAmount() != null) {
                    descriptionItem.onDesc(productsList.get(holder.getAdapterPosition()).getI(), productsList.get(holder.getAdapterPosition()).getAmount());
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setMessage("برای کالا مقدار وارد کنید.")
                            .setPositiveButton("بستن", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();

                    TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                    Typeface face = Typeface.createFromAsset(context.getAssets(), "iransans.ttf");
                    textView.setTypeface(face);
                    textView.setTextColor(context.getResources().getColor(R.color.red_table));
                    textView.setTextSize(13);
                }
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
        private final TextView Line;
        private final EditText ProductAmountTxt;
        private final TextView edtDesc;
        private final RelativeLayout cardEdit;
        private final RoundedImageView productImage;
        private final ConstraintLayout layoutAmount;

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
        Gson gson = new Gson();
        Type typeIDs = new TypeToken<ModelLog>() {
        }.getType();


        progressBar.setVisibility(View.VISIBLE);


        try {
            compositeDisposable.add(
                    api.getMaxSales(company.USER, company.PASS, Prd_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        progressBar.setVisibility(View.GONE);

                                        double remain;
                                        try {
                                            assert jsonElement != null;
                                            remain = Integer.parseInt(jsonElement);
                                        } catch (Exception e) {
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);
                                            assert iDs != null;
                                            String description = iDs.getLogs().get(0).getDescription();
                                            Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                            return;
                                        }


                                        List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list();
                                        ArrayList<InvoiceDetail> resultInvoice = new ArrayList<>(invDetails);
                                        CollectionUtils.filter(resultInvoice, r -> r.PRD_UID.equals(Prd_GUID));


                                        double mainCoef1 = productsList.get(position).getCoef1();
                                        double coef1 = productsList.get(position).getCoef1();
                                        double coef2 = productsList.get(position).getCoef2();
                                        double amount = 0;

                                        if (remain <= 0) {
                                            productsList.get(position).setAmount(0.0);
                                            if (MinOrPlus != 3) {
                                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                ProductAmountTxt.setText("");

                                                ProductAmountTxt.addTextChangedListener(textWatcher);
                                                ivMinus.setVisibility(View.GONE);
                                                ProductAmountTxt.setVisibility(View.GONE);
                                            }


                                            error.setText("این کالا موجود نمی باشد");
                                            progressBar.setVisibility(View.GONE);
                                            amount = 0.0;

                                        }


                                        if (MinOrPlus == 1) {
                                            if (amount1 > 0.0 && coef2 != 0.0)
                                                coef1 = coef2;
                                            amount = amount1 + coef1;
                                        } else if (MinOrPlus == 2) {
                                            if (amount1 > coef1 && coef2 != 0.0 && amount1 < coef1 + coef2) {
                                                amount = 0.0;
                                            } else if (amount1 > coef1 && coef2 != 0.0)
                                                amount = amount1 - coef2;
                                            else if (amount1 >= coef1)
                                                amount = amount1 - coef1;

                                            else
                                                return;
                                        } else {
                                            try {
                                                amount = Float.parseFloat(s);
                                                if (
                                                        (coef2 != 0.0 && amount < coef1)
                                                                || (coef2 == 0 && amount % coef1 != 0)
                                                                || (coef2 == 0 && amount % coef1 == 0 && amount < coef1)
                                                ) {

                                                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                                                            .setMessage(" مقدار وارد شده باید ضریبی از " + mainCoef1 + " باشد ")
                                                            .setPositiveButton("بستن", (dialog, which) -> {
                                                                dialog.dismiss();
                                                            })
                                                            .show();

                                                    TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                                                    Typeface face = Typeface.createFromAsset(context.getAssets(), "iransans.ttf");
                                                    textView.setTypeface(face);
                                                    textView.setTextColor(context.getResources().getColor(R.color.medium_color));
                                                    textView.setTextSize(13);


                                                    ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                    ProductAmountTxt.setText("");
                                                    ProductAmountTxt.addTextChangedListener(textWatcher);

                                                    ivMinus.setVisibility(View.GONE);
                                                    ProductAmountTxt.setVisibility(View.GONE);
                                                    productsList.get(position).setAmount(0.0);
                                                    amount = 0.0;

                                                }


                                            } catch (Exception ignored) {
                                            }
                                        }


                                        productsList.get(position).setAmount(amount);

                                        if (Integer.parseInt(jsonElement) - amount < 0) {
                                            AlertDialog alertDialog = new AlertDialog.Builder(context)
                                                    .setMessage("مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + jsonElement)
                                                    .setPositiveButton("بستن", (dialog, which) -> {
                                                        dialog.dismiss();
                                                    })
                                                    .show();

                                            TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                                            Typeface face = Typeface.createFromAsset(context.getAssets(), "iransans.ttf");
                                            textView.setTypeface(face);
                                            textView.setTextColor(context.getResources().getColor(R.color.medium_color));
                                            textView.setTextSize(13);

                                            if (remain % mainCoef1 != 0)
                                                remain = 0.0;


                                            productsList.get(position).setAmount(remain);
                                            ProductAmountTxt.removeTextChangedListener(textWatcher);
                                            ProductAmountTxt.setText(df.format(remain));
                                            ProductAmountTxt.addTextChangedListener(textWatcher);
                                            ProductAmountTxt.setCursorVisible(false);


                                            if (resultInvoice.size() > 0) {
                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + resultInvoice.get(0).INV_DET_UID + "'").first();
                                                if (invoiceDetail != null) {
                                                    invoiceDetail.INV_DET_QUANTITY = remain;
                                                    if (remain != 0.0)
                                                        invoiceDetail.update();
                                                    else
                                                        invoiceDetail.delete();

                                                    clickItem.onClick(Prd_GUID);
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
                                                    ProductAmountTxt.setText("");
                                                    ProductAmountTxt.addTextChangedListener(textWatcher);
                                                    ivMinus.setVisibility(View.GONE);
                                                    ProductAmountTxt.setVisibility(View.GONE);
                                                }


                                                clickItem.onClick(Prd_GUID);
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
                                            if (amount != 0.0) {
                                                InvoiceDetail invoicedetail = new InvoiceDetail();
                                                invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                                                invoicedetail.INV_UID = Inv_GUID;
                                                invoicedetail.INV_DET_QUANTITY = amount;
                                                invoicedetail.PRD_UID = Prd_GUID;
                                                if (company.mode == 1)
                                                    invoicedetail.TBL = Tbl_GUID;
                                                invoicedetail.save();
                                            }

                                            if (MinOrPlus != 3) {
                                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                                ProductAmountTxt.setText(df.format(amount));
                                                ProductAmountTxt.addTextChangedListener(textWatcher);

                                            }
                                        }
                                        ivMinus.setVisibility(View.VISIBLE);
                                        ProductAmountTxt.setVisibility(View.VISIBLE);


                                        clickItem.onClick(Prd_GUID);


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


    private void getMaxSale(ConstraintLayout layoutAmount, TextView txtError, String Prd_GUID, double amount) {


        try {
            compositeDisposable.add(
                    api.getMaxSales(company.USER, company.PASS, Prd_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        double remain;

                                        try {
                                            assert jsonElement != null;
                                            remain = Integer.parseInt(jsonElement);
                                            if ((remain == 0.0 && txtError.getText().toString().equals(""))) {
                                                txtError.setText("این کالا موجود نمی باشد");
                                                layoutAmount.setVisibility(View.GONE);

                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("PRDUID ='" + Prd_GUID + "'").first();
                                                if (invoiceDetail != null) {
                                                    InvoiceDetail.deleteInTx(invoiceDetail);
                                                }


                                            } else if (remain > 0 && remain < amount) {
                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("PRDUID ='" + Prd_GUID + "'").first();
                                                if (invoiceDetail != null) {
                                                    invoiceDetail.INV_DET_QUANTITY = remain;
                                                    invoiceDetail.update();
                                                }
                                            } else {
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
            getMaxSales(amount1, position, error, progressBar, textWatcher, ProductAmountTxt, ivMinus, company, Prd_GUID, s, MinOrPlus);
        } else {
            double amount = 0.0;
            double mainCoef1 = productsList.get(position).getCoef1();
            double coef1 = productsList.get(position).getCoef1();
            double coef2 = productsList.get(position).getCoef2();


            //region PlusAmount
            if (MinOrPlus == 1) {
                if (amount1 > 0.0 && coef2 != 0.0)
                    coef1 = coef2;
                amount = amount1 + coef1;
            }
            //endregion PlusAmount

            //region MinAmount
            else if (MinOrPlus == 2) {
                if (amount1 > coef1 && coef2 != 0.0)
                    amount = amount1 - coef2;
                else if (amount1 >= coef1)
                    amount = amount1 - coef1;

                else
                    return;
            }
            //endregion MinAmount


            //region EditAmount
            else {
                try {
                    amount = Float.parseFloat(s);
                    if (
                            (coef2 != 0.0 && amount < coef1)
                                    || (coef2 == 0 && amount % coef1 != 0)
                                    || (coef2 == 0 && amount % coef1 == 0 && amount < coef1)
                    ) {


                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setMessage(" مقدار وارد شده باید ضریبی از " + mainCoef1 + " باشد.")
                                .setPositiveButton("بستن", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .show();

                        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                        Typeface face = Typeface.createFromAsset(context.getAssets(), "iransans.ttf");
                        textView.setTypeface(face);
                        textView.setTextColor(context.getResources().getColor(R.color.medium_color));
                        textView.setTextSize(13);

                        amount = 0;
                        ProductAmountTxt.removeTextChangedListener(textWatcher);
                        ProductAmountTxt.setText("");

                        ProductAmountTxt.addTextChangedListener(textWatcher);
                        ivMinus.setVisibility(View.GONE);
                        ProductAmountTxt.setVisibility(View.GONE);
                    }
                } catch (Exception ignored) {
                }
            }
            //endregion EditAmount


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
                        ProductAmountTxt.setText("");
                        ProductAmountTxt.addTextChangedListener(textWatcher);

                        ivMinus.setVisibility(View.GONE);
                        ProductAmountTxt.setVisibility(View.GONE);
                    }
                    ProductAmountTxt.setCursorVisible(false);
                    clickItem.onClick(Prd_GUID);
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
                clickItem.onClick(Prd_GUID);
            }

            //Create
            else {

                if (amount != 0.0) {
                    InvoiceDetail invoicedetail = new InvoiceDetail();
                    invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                    invoicedetail.INV_UID = Inv_GUID;
                    invoicedetail.INV_DET_QUANTITY = amount;
                    invoicedetail.PRD_UID = Prd_GUID;
                    invoicedetail.save();
                }
                if (MinOrPlus != 3) {
                    ProductAmountTxt.removeTextChangedListener(textWatcher);
                    ProductAmountTxt.setText(df.format(amount));
                    ProductAmountTxt.addTextChangedListener(textWatcher);

                }

                clickItem.onClick(Prd_GUID);
            }

            ivMinus.setVisibility(View.VISIBLE);
            ProductAmountTxt.setVisibility(View.VISIBLE);

        }
    }


}





