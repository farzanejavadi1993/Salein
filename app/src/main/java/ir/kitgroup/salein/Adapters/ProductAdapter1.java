package ir.kitgroup.salein.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;


import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.classes.Constant;
import ir.kitgroup.salein.DataBase.InvoiceDetail;

import ir.kitgroup.salein.DataBase.User;

import ir.kitgroup.salein.Fragments.ShowDetailFragment;

import ir.kitgroup.salein.models.ModelLog;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.kitgroup.salein.Util.Util.AllProduct;


public class ProductAdapter1 extends RecyclerView.Adapter<ProductAdapter1.viewHolder> {

    private final Context context;


    private final List<Product> productsList;

    private String maxSale;

    private String Inv_GUID;

    private final DecimalFormat df;

    private int fontSize = 0;
    private int fontLargeSize = 0;

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


    public ProductAdapter1(Context context, List<Product> productsList) {
        this.context = context;
        this.productsList = productsList;

        df = new DecimalFormat();


    }

    public void setMaxSale(String MaxSale) {
        this.maxSale = MaxSale;
    }

    public void setInv_GUID(String inv_guid) {
        this.Inv_GUID = inv_guid;
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
        return productsList.get(position) == null ? Constant.VIEW_TYPE_LOADING : Constant.VIEW_TYPE_ITEM;
    }


    @Override
    public @NotNull viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        if (viewType == Constant.VIEW_TYPE_ITEM) {
            if (LauncherActivity.screenInches >= 7) {
                fontSize = 13;
                fontLargeSize = 14;

            } else {
                fontSize = 11;
                fontLargeSize = 12;

            }
            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_products_item_mobile, parent,
                    false));
        } else if (viewType == Constant.VIEW_TYPE_LOADING) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_loading, parent, false);
            return new viewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final @NotNull viewHolder holder, final int position) {


        if (productsList.get(holder.getAdapterPosition()) != null) {

            ir.kitgroup.salein.DataBase.Product product1 = Select.from(ir.kitgroup.salein.DataBase.Product.class)
                    .where("I ='" + productsList.get(position).getI() + "'").first();



            if (product1==null || product1.Url == null || product1.Url.equals("")) {

                    switch (LauncherActivity.name) {
                        case "ir.kitgroup.salein":

                            holder.productImage.setImageResource(R.drawable.white);
                            holder.productImage1.setImageResource(R.drawable.logo1);
                            break;

                        case "ir.kitgroup.saleintop":
                            holder.productImage.setImageResource(R.drawable.white);
                            holder.productImage1.setImageResource(R.drawable.top_png);


                            break;


                        case "ir.kitgroup.saleinmeat":

                            holder.productImage.setImageResource(R.drawable.white);
                            holder.productImage1.setImageResource(R.drawable.meat_png);
                            break;


                        case "ir.kitgroup.saleinnoon":


                            holder.productImage.setImageResource(R.drawable.noon);
                            break;
                    }

                }
            else if (product1!=null &&!product1.Url.equals("") && product1.Url != null) {

                    holder.productImage1.setImageBitmap(null);
                    byte[] decodedString = Base64.decode(product1.Url, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                            decodedString.length);

                    holder.productImage.setImageBitmap(decodedByte);
                }




           /* ir.kitgroup.salein.DataBase.Product product = Select.from(ir.kitgroup.salein.DataBase.Product.class).where(" I  = '" + productsList.get(position).getI() + "'").first();
            if (product == null || (product != null && product.Url == null))
                try {

                    getImage1(productsList.get(position).getI(), holder.productImage, holder.productImage1, holder.getAdapterPosition());
                }
                 catch (Exception e) {

                    int p = 0;

                }*/
           /*  if (Util.URLS.size()>position){
                Picasso.get()
                        .load(Util.URLS.get(position))
                        .resize(50, 50)
                        .centerCrop()
                        .into(holder.productImage);
            }else {
                switch (LauncherActivity.name) {
                    case "ir.kitgroup.salein":

                        holder.productImage.setImageResource(R.drawable.white);
                        holder.productImage1.setImageResource(R.drawable.logo1);
                        break;

                    case "ir.kitgroup.saleintop":
                        holder.productImage.setImageResource(R.drawable.white);
                        holder.productImage1.setImageResource(R.drawable.top_png);


                        break;


                    case "ir.kitgroup.saleinmeat":

                        holder.productImage.setImageResource(R.drawable.white);
                        holder.productImage1.setImageResource(R.drawable.meat_png);
                        break;


                    case "ir.kitgroup.saleinnoon":


                        holder.productImage.setImageResource(R.drawable.noon);
                        break;
                }
            }*/
           //Glide.with(context).load(productsList.get(holder.getAdapterPosition()).Url)
          //        .skipMemoryCache(false)
          //        .diskCacheStrategy(DiskCacheStrategy.ALL)
          //        .transition(DrawableTransitionOptions.withCrossFade())
          //        .into(holder.productImage);

            holder.productOldPrice.setTextSize(fontLargeSize);
            holder.productDiscountPercent.setTextSize(fontLargeSize);
            holder.Line.setTextSize(fontSize);
            holder.productPrice.setTextSize(fontLargeSize);
            holder.edtDesc.setTextSize(fontSize);
            holder.ProductAmountTxt.setTextSize(fontSize);
            holder.unit.setTextSize(fontSize);


        /*    final int newColor = context.getResources().getColor(R.color.purple_500);
            holder.ivEdit.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);*/


            holder.productName.setText(productsList.get(holder.getAdapterPosition()).getN());

            if (productsList.get(holder.getAdapterPosition()).getPercDis() != 0.0) {
                if (productsList.get(holder.getAdapterPosition()).getPrice() > 0) {
                    holder.layoutDiscount.setVisibility(View.VISIBLE);
                    holder.productDiscountPercent.setVisibility(View.VISIBLE);
                    holder.productOldPrice.setVisibility(View.VISIBLE);
                    holder.Line.setVisibility(View.VISIBLE);

                    holder.productDiscountPercent.setText(format.format(productsList.get(holder.getAdapterPosition()).getPercDis()) + "%");
                    holder.productOldPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice()));
                    holder.Line.setText("------------");
                    double discountPrice = productsList.get(holder.getAdapterPosition()).getPrice() * (productsList.get(holder.getAdapterPosition()).getPercDis() / 100);
                    double newPrice = productsList.get(holder.getAdapterPosition()).getPrice() - discountPrice;
                    holder.productPrice.setText(format.format(newPrice) + " ریال ");
                }

            } else {

                if (productsList.get(holder.getAdapterPosition()).getPrice() > 0) {

                    holder.productDiscountPercent.setVisibility(View.GONE);
                    holder.layoutDiscount.setVisibility(View.GONE);
                    holder.productOldPrice.setVisibility(View.GONE);
                    holder.Line.setVisibility(View.GONE);
                    holder.productPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPrice()) + " ریال ");
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
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, showDetailFragment, "ShowDetailFragment").addToBackStack("ShowDetailF").commit();
                }

            });


            ArrayList<Product> resultPrd = new ArrayList<>(AllProduct);
            CollectionUtils.filter(resultPrd, r -> r.getI().equals(productsList.get(holder.getAdapterPosition()).getI()));


            if (resultPrd.size() > 0) {

                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "' AND PRDUID ='" + resultPrd.get(0).getI() + "'").first();

                if (invoiceDetail != null && invoiceDetail.INV_DET_QUANTITY != null)
                    resultPrd.get(0).setAmount(invoiceDetail.INV_DET_QUANTITY);
                else
                    resultPrd.get(0).setAmount(0.0);
                if (resultPrd.get(0).getAmount() > 0) {
                    holder.ivMinus.setVisibility(View.VISIBLE);
                    holder.ProductAmountTxt.setVisibility(View.VISIBLE);
                    holder.unit.setVisibility(View.VISIBLE);


                } else {
                    holder.ivMinus.setVisibility(View.GONE);
                    holder.ProductAmountTxt.setVisibility(View.GONE);
                    holder.unit.setVisibility(View.GONE);

                }
            }


           /* if (productsList.get(holder.getAdapterPosition()).descItem != null) {
                if (resultPrd.size() > 0)
                    holder.edtDesc.setText(resultPrd.get(0).descItem);

            } else
                holder.edtDesc.setText("");*/

            holder.ivMax.setOnClickListener(view -> {

                doAction(holder.getAdapterPosition(),
                        holder.progressBar,
                        holder.textWatcher,
                        holder.ProductAmountTxt,
                        holder.unit,
                        holder.ivMinus,
                        Select.from(User.class).first().userName,
                        Select.from(User.class).first().passWord,
                        maxSale,
                        productsList.get(position).getI(),
                        "",
                        1


                );
                //clickItem.onClick();


            });

            holder.ivMinus.setOnClickListener(v -> {

                doAction(holder.getAdapterPosition(),
                        holder.progressBar,
                        holder.textWatcher,
                        holder.ProductAmountTxt,
                        holder.unit,
                        holder.ivMinus,
                        Select.from(User.class).first().userName,
                        Select.from(User.class).first().passWord,
                        maxSale,
                        productsList.get(position).getI(),
                        "",
                        2

                );
                //clickItem.onClick(productsList.get(holder.getAdapterPosition()).getPRDUID(), String.valueOf(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()), productsList.get(holder.getAdapterPosition()).PERC_DIS / 100,"", 2);

            });


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

                        doAction(holder.getAdapterPosition(),
                                holder.progressBar,
                                holder.textWatcher,
                                holder.ProductAmountTxt,
                                holder.unit,
                                holder.ivMinus,
                                Select.from(User.class).first().userName,
                                Select.from(User.class).first().passWord,
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
            if (resultPrd.size() > 0) {
                holder.ProductAmountTxt.setText(df.format(resultPrd.get(0).getAmount()));

            }

            holder.ProductAmountTxt.addTextChangedListener(holder.textWatcher);


            holder.cardEdit.setOnClickListener(v -> {
                if (productsList.get(holder.getAdapterPosition()).getAmount() != null) {
                    descriptionItem.onDesc(productsList.get(holder.getAdapterPosition()).getI(), productsList.get(holder.getAdapterPosition()).getAmount());
                } else {
                    Toast.makeText(context, " برای کالا  مقداروارد کنید", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }


    static class viewHolder extends RecyclerView.ViewHolder {

        private int tab = 0;
        private final int sizeGroup = 0;
        private final TextView productName;
        private final TextView unit;
        private final TextView productPrice;
        private final TextView productOldPrice;
        private final TextView productDiscountPercent;
        private final TextView Line;
        private final EditText ProductAmountTxt;
        private final TextView edtDesc;
        private final RelativeLayout cardEdit;
        private final RoundedImageView productImage;
        private final ImageView productImage1;
        private final ImageView ivMinus;
        private final ImageView ivMax;
        private final ProgressBar progressBar;
        private TextWatcher textWatcher;
        private final RelativeLayout layoutDiscount;


        public viewHolder(View itemView) {
            super(itemView);

            RelativeLayout cardView = itemView.findViewById(R.id.order_recycle_item_product_layout);
            cardEdit = itemView.findViewById(R.id.card_edit);

            edtDesc = itemView.findViewById(R.id.edt_description_temp);
            layoutDiscount = itemView.findViewById(R.id.layout_discount);


            productName = itemView.findViewById(R.id.order_recycle_item_product_name);
            unit = itemView.findViewById(R.id.unit);


            productPrice = itemView.findViewById(R.id.order_recycle_item_product_price);
            productOldPrice = itemView.findViewById(R.id.order_recycle_item_product_old_price);
            productDiscountPercent = itemView.findViewById(R.id.order_recycle_item_product_discountPercent);
            Line = itemView.findViewById(R.id.order_recycle_item_product_line);


            productImage = itemView.findViewById(R.id.order_recycle_item_product_img);
            productImage1 = itemView.findViewById(R.id.order_recycle_item_product_img1);
            ProductAmountTxt = itemView.findViewById(R.id.order_recycle_item_product_txt_amount);


            ivMinus = itemView.findViewById(R.id.iv_minus);
            ivMax = itemView.findViewById(R.id.iv_max);
            progressBar = itemView.findViewById(R.id.progress);


        }
    }

    private void getMaxSales(int position, ProgressBar progressBar, TextWatcher textWatcher, EditText ProductAmountTxt, TextView unit, ImageView ivMinus, String userName, String pass, String Prd_GUID, String s, int MinOrPlus) {

        progressBar.setVisibility(View.VISIBLE);
        double aPlus = productsList.get(position).getCoef();
        if (aPlus == 0)
            aPlus = 1;
        try {

            Call<String> call = App.api.getMaxSales(userName, pass, Prd_GUID);

            double finalAPlus = aPlus;

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    progressBar.setVisibility(View.GONE);

                    double remain = -1000000000;
                    try {
                        assert response.body() != null;
                        remain = Integer.parseInt(response.body());
                    } catch (Exception e) {
                        Gson gson = new Gson();
                        Type typeIDs = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs = gson.fromJson(response.body(), typeIDs);

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


                        ArrayList<Product> resultProduct = new ArrayList<>(AllProduct);
                        CollectionUtils.filter(resultProduct, r -> r.getI().equals(Prd_GUID));

                        if (remain <= 0) {
                            AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount(0.0);
                            if (MinOrPlus != 3) {
                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                ProductAmountTxt.setText("0");
                                unit.setVisibility(View.GONE);
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
                            Toast.makeText(context, "این کالا موجود نمی باشد", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (resultProduct.size() > 0) {
                            double amount = 0;
                            if (MinOrPlus == 1)
                                amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() + finalAPlus;


                            else if (MinOrPlus == 2) {
                                if (AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() >= finalAPlus)
                                    amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() - finalAPlus;
                                else
                                    return;


                            } else {
                                try {
                                    amount = Float.parseFloat(s);
                                    if (amount % finalAPlus != 0) {
                                        Toast.makeText(context, " مقدار وارد شده باید ضریبی از " + finalAPlus + " باشد ", Toast.LENGTH_SHORT).show();
                                        ProductAmountTxt.removeTextChangedListener(textWatcher);
                                        ProductAmountTxt.setText("0");
                                        unit.setVisibility(View.GONE);
                                        ProductAmountTxt.addTextChangedListener(textWatcher);
                                        ivMinus.setVisibility(View.GONE);
                                        ProductAmountTxt.setVisibility(View.GONE);
                                        AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount(0.0);
                                        return;
                                    }


                                } catch (Exception ignored) {

                                }
                            }


                            AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);

                            if (Integer.parseInt(response.body()) - amount < 0) {
                                Toast.makeText(context, "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();

                                if (remain % finalAPlus != 0) {

                                    remain = (int) (remain / finalAPlus) * finalAPlus;

                                }

                                AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount((double) remain);


                                // if (MinOrPlus != 3) {
                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                ProductAmountTxt.setText(df.format(remain));
                                unit.setVisibility(View.VISIBLE);


                                ProductAmountTxt.addTextChangedListener(textWatcher);
                                // }

                                if (resultInvoice.size() > 0) {
                                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + resultInvoice.get(0).INV_DET_UID + "'").first();
                                    if (invoiceDetail != null) {
                                        invoiceDetail.INV_DET_QUANTITY = (double) remain;
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
                                        unit.setVisibility(View.GONE);
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
                                    unit.setVisibility(View.VISIBLE);
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
                                invoicedetail.save();


                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                ProductAmountTxt.setText(df.format(amount));
                                unit.setVisibility(View.VISIBLE);
                                ProductAmountTxt.addTextChangedListener(textWatcher);

                            }
                            ivMinus.setVisibility(View.VISIBLE);
                            ProductAmountTxt.setVisibility(View.VISIBLE);


                            clickItem.onClick();
                        }


                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(context, "خطا در دریافت اطلاعات مانده کالا" + t.toString(), Toast.LENGTH_SHORT).show();


                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(context, "خطا در دریافت اطلاعات مانده کالا" + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }

    private void doAction(int position, ProgressBar progressBar, TextWatcher textWatcher, EditText ProductAmountTxt, TextView unit, ImageView ivMinus, String userName, String passWord, String maxSales, String Prd_GUID, String s, int MinOrPlus) {

        if (position < 0)
            return;
        if (maxSales.equals("1")) {
            getMaxSales(position, progressBar, textWatcher, ProductAmountTxt, unit, ivMinus, userName, passWord, Prd_GUID, s, MinOrPlus);
        } else {
            double aPlus = productsList.get(position).getCoef();
            if (aPlus == 0)
                aPlus = 1;
            ArrayList<Product> resultProduct = new ArrayList<>(AllProduct);
            CollectionUtils.filter(resultProduct, r -> r.getI().equals(Prd_GUID));

            if (resultProduct.size() > 0) {
                double amount = 0;
                if (MinOrPlus == 1) {

                    amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() + aPlus;
                } else if (MinOrPlus == 2) {
                    if (AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() >= aPlus)
                        amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() - aPlus;

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
                            unit.setVisibility(View.GONE);
                            ProductAmountTxt.addTextChangedListener(textWatcher);
                            ivMinus.setVisibility(View.GONE);
                            ProductAmountTxt.setVisibility(View.GONE);
                        }
                    } catch (Exception ignored) {

                    }
                }


                AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);

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
                            unit.setVisibility(View.GONE);
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
                        unit.setVisibility(View.VISIBLE);
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
                    unit.setVisibility(View.VISIBLE);
                    ProductAmountTxt.addTextChangedListener(textWatcher);
                    clickItem.onClick();
                }

                ivMinus.setVisibility(View.VISIBLE);
                ProductAmountTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getImage1(String prd_uid, ImageView productImage, ImageView productImage1, int count) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                App.api.getImage1(prd_uid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {


                            ir.kitgroup.salein.DataBase.Product product;
                            product = Select.from(ir.kitgroup.salein.DataBase.Product.class).where(" I  = '" + prd_uid + "'").first();

                            if (product == null)
                                product = new ir.kitgroup.salein.DataBase.Product();

                            product.Url = jsonElement.replace("data:image/png;base64,", "");
                            product.I = prd_uid;
                            product.save();

                            if (productsList.get(count).getI().equals(prd_uid)) {
                                productImage1.setImageBitmap(null);
                                byte[] decodedString = Base64.decode(jsonElement.replace("data:image/png;base64,", ""), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                                        decodedString.length);

                                productImage.setImageBitmap(decodedByte);
                            }


                        }, throwable -> {

                            int p = 0;
                        })
        );
    }


}





