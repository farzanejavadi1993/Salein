package ir.kitgroup.salein.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
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

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;


import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.classes.Constant;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Product;
import ir.kitgroup.salein.DataBase.User;

import ir.kitgroup.salein.Fragments.ShowDetailFragment;

import ir.kitgroup.salein.models.ModelLog;
import ir.kitgroup.salein.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.kitgroup.salein.Util.Util.AllProduct;


public class ProductAdapter1 extends RecyclerView.Adapter<ProductAdapter1.viewHolder> {

    private final Context context;

    private final CustomProgress customProgress;

    private final List<Product> productsList;

    private final String maxSale;

    private final String Inv_GUID;

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


    public ProductAdapter1(Context context, List<Product> productsList, String maxSale, String Inv_GUID) {
        this.context = context;
        this.productsList = productsList;
        this.maxSale = maxSale;
        this.Inv_GUID = Inv_GUID;
        customProgress = CustomProgress.getInstance();
        df = new DecimalFormat();


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
                return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_products_item_tablet, parent,
                        false));
            } else {
                fontSize = 11;
                fontLargeSize = 12;
                return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_products_item_mobile, parent,
                        false));
            }
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


            String yourFilePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + "SaleIn" + "/" + productsList.get(holder.getAdapterPosition()).I.toUpperCase() + ".jpg";
            File file = new File(yourFilePath);

            if (file.exists()) {


                Bitmap image = null;

                try {
                    image = BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                holder.productImage.setImageBitmap(image);


            }
            else {
                try {
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                    switch (pInfo.packageName){
                        case "ir.kitgroup.salein":
                            holder.productImage.setImageResource(R.drawable.application_logo1);
                            break;

                        case "ir.kitgroup.saleintop":
                            holder.productImage.setImageResource(R.drawable.top_png);

                            break;


                        case "ir.kitgroup.saleinmeat":
                            holder.productImage.setImageResource(R.drawable.meat_png);
                            break;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }


            holder.productOldPrice.setTextSize(fontLargeSize);
            holder.productDiscountPercent.setTextSize(fontLargeSize);
            holder.Line.setTextSize(fontSize);
            holder.productPrice.setTextSize(fontLargeSize);
            holder.edtDesc.setTextSize(fontSize);
            holder.ProductAmountTxt.setTextSize(fontSize);
            //  holder.unit.setTextSize(fontSize);


        /*    final int newColor = context.getResources().getColor(R.color.purple_500);
            holder.ivEdit.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);*/


            holder.productName.setText(productsList.get(holder.getAdapterPosition()).getPRDNAME());

            if (productsList.get(holder.getAdapterPosition()).PERC_DIS != 0.0) {
                if (productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1() > 0) {
                    holder.productDiscountPercent.setVisibility(View.VISIBLE);
                    holder.productOldPrice.setVisibility(View.VISIBLE);
                    holder.Line.setVisibility(View.VISIBLE);

                    holder.productDiscountPercent.setText(format.format(productsList.get(holder.getAdapterPosition()).PERC_DIS) + "%");
                    holder.productOldPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()));
                    holder.Line.setText("------------");
                    double discountPrice = productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1() * (productsList.get(holder.getAdapterPosition()).PERC_DIS / 100);
                    double newPrice = productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1() - discountPrice;
                    holder.productPrice.setText(format.format(newPrice) + " ریال ");
                }

            } else {
                if (productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1() > 0) {
                    holder.productDiscountPercent.setVisibility(View.GONE);
                    holder.productOldPrice.setVisibility(View.GONE);
                    holder.Line.setVisibility(View.GONE);
                    holder.productPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()) + " ریال ");
                }

            }


            holder.tab = 0;
            holder.productImage.setOnClickListener(view -> {
                holder.tab++;
                if (holder.tab == 2) {
                    holder.tab = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", productsList.get(holder.getAdapterPosition()).I);
                    ShowDetailFragment showDetailFragment = new ShowDetailFragment();
                    showDetailFragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, showDetailFragment, "ShowDetailFragment").addToBackStack("ShowDetailF").commit();
                }

            });


            ArrayList<Product> resultPrd = new ArrayList<>(AllProduct);
            CollectionUtils.filter(resultPrd, r -> r.I.equals(productsList.get(holder.getAdapterPosition()).I));

            if (resultPrd.size() > 0) {
                if (resultPrd.get(0).getAmount() > 0) {
                    holder.ivMinus.setVisibility(View.VISIBLE);

                    holder.ProductAmountTxt.setVisibility(View.VISIBLE);
                } else {
                    holder.ivMinus.setVisibility(View.GONE);
                    holder.ProductAmountTxt.setVisibility(View.GONE);
                }
            }


            if (productsList.get(holder.getAdapterPosition()).descItem != null) {
                if (resultPrd.size() > 0)
                    holder.edtDesc.setText(resultPrd.get(0).descItem);

            } else
                holder.edtDesc.setText("");

            holder.ivMax.setOnClickListener(view -> {


                doAction(
                        holder.textWatcher,
                        holder.ProductAmountTxt,
                        holder.ivMinus,
                        Select.from(User.class).first().userName,
                        Select.from(User.class).first().passWord,
                        maxSale,
                        productsList.get(holder.getAdapterPosition()).getPRDUID(),
                        "",
                        1


                );
                //clickItem.onClick();


            });

            holder.ivMinus.setOnClickListener(v -> {

                doAction(
                        holder.textWatcher,
                        holder.ProductAmountTxt,
                        holder.ivMinus,
                        Select.from(User.class).first().userName,
                        Select.from(User.class).first().passWord,
                        maxSale,
                        productsList.get(holder.getAdapterPosition()).getPRDUID(),
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

                        doAction(
                                holder.textWatcher,
                                holder.ProductAmountTxt,
                                holder.ivMinus,
                                Select.from(User.class).first().userName,
                                Select.from(User.class).first().passWord,
                                maxSale,
                                productsList.get(holder.getAdapterPosition()).getPRDUID(),
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
                if (productsList.get(holder.getAdapterPosition()).AMOUNT != null) {
                    descriptionItem.onDesc(productsList.get(holder.getAdapterPosition()).I, productsList.get(holder.getAdapterPosition()).AMOUNT);
                } else {
                    Toast.makeText(context, " برای کالا  مقداروارد کنید", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }


    static class viewHolder extends RecyclerView.ViewHolder {

        private int tab = 0;
        private int sizeGroup = 0;


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


        private final ImageView ivMinus;
        private final ImageView ivMax;
        private TextWatcher textWatcher;


        public viewHolder(View itemView) {
            super(itemView);

            RelativeLayout cardView = itemView.findViewById(R.id.order_recycle_item_product_layout);
            cardEdit = itemView.findViewById(R.id.card_edit);

            edtDesc = itemView.findViewById(R.id.edt_description_temp);


            productName = itemView.findViewById(R.id.order_recycle_item_product_name);
            unit = itemView.findViewById(R.id.unit);


            productPrice = itemView.findViewById(R.id.order_recycle_item_product_price);
            productOldPrice = itemView.findViewById(R.id.order_recycle_item_product_old_price);
            productDiscountPercent = itemView.findViewById(R.id.order_recycle_item_product_discountPercent);
            Line = itemView.findViewById(R.id.order_recycle_item_product_line);


            productImage = itemView.findViewById(R.id.order_recycle_item_product_img);
            ProductAmountTxt = itemView.findViewById(R.id.order_recycle_item_product_txt_amount);


            ivMinus = itemView.findViewById(R.id.iv_minus);
            ivMax = itemView.findViewById(R.id.iv_max);


            if (LauncherActivity.screenInches >= 7 && productImage != null) {
                if (LauncherActivity.screenInches >= 7) {
                    productName.setTextSize(14);
                    int width;
                    if (LauncherActivity.width > LauncherActivity.height)
                        width = LauncherActivity.height;
                    else
                        width = LauncherActivity.width;


                    cardView.getLayoutParams().width = (int) (width / 3.22);
                    cardView.getLayoutParams().height = (int) (width / 3.22);
                  /*if (sizeGroup<= 1) {
                        cardView.getLayoutParams().width = (int) (height / 2.7);
                    } else {
                        cardView.getLayoutParams().width = (int) (height / 2.95);
                    }*/

                    // cardView.getLayoutParams().height = (int) (height / 2.95);

                  /*  productImage.getLayoutParams().width = (int) (height / 3.5);
                    productImage.getLayoutParams().height = (int) (height /3.5);*/


                }


            }


        }
    }

    private void getMaxSales(TextWatcher textWatcher, EditText ProductAmountTxt, ImageView ivMinus, String userName, String pass, String Prd_GUID, String s, int MinOrPlus) {
        customProgress.showProgress(context, "در حال دریافت مانده کالا..", false);

        try {

            Call<String> call = App.api.getMaxSales(userName, pass, Prd_GUID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    customProgress.hideProgress();

                    int remain = -1000000000;
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
                        CollectionUtils.filter(resultProduct, r -> r.getPRDUID().equals(Prd_GUID));

                        if (remain <= 0) {
                            AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount(0.0);
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
                            Toast.makeText(context, "این کالا موجود نمی باشد", Toast.LENGTH_SHORT).show();
                            customProgress.hideProgress();
                            return;
                        }


                        if (resultProduct.size() > 0) {
                            double amount = 0;
                            if (MinOrPlus == 1)
                                amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() + 1;


                            else if (MinOrPlus == 2) {
                                if (AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() >= 1)
                                    amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() - 1;
                                else
                                    return;


                            } else {
                                try {
                                    amount = Float.parseFloat(s);

                                } catch (Exception ignored) {

                                }
                            }


                            AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);

                            if (Integer.parseInt(response.body()) - amount < 0) {
                                Toast.makeText(context, "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();
                                AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).setAmount((double) remain);
                               // if (MinOrPlus != 3) {
                                    ProductAmountTxt.removeTextChangedListener(textWatcher);
                                    ProductAmountTxt.setText(df.format(remain));
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

                                customProgress.hideProgress();


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


    private void doAction(TextWatcher textWatcher, EditText ProductAmountTxt, ImageView ivMinus, String userName, String passWord, String maxSales, String Prd_GUID, String s, int MinOrPlus) {

        if (maxSales.equals("1")) {
            getMaxSales(textWatcher, ProductAmountTxt, ivMinus, userName, passWord, Prd_GUID, s, MinOrPlus);
        } else {
            ArrayList<Product> resultProduct = new ArrayList<>(AllProduct);
            CollectionUtils.filter(resultProduct, r -> r.getPRDUID().equals(Prd_GUID));

            if (resultProduct.size() > 0) {
                double amount = 0;
                if (MinOrPlus == 1)
                    amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() + 1;
                else if (MinOrPlus == 2) {
                    if (AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() >= 1)
                        amount = AllProduct.get(AllProduct.indexOf(resultProduct.get(0))).getAmount() - 1;

                    else
                        return;

                } else {
                    try {
                        amount = Float.parseFloat(s);
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

}





