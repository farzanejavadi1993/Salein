package ir.kitgroup.salein1.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
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

import ir.kitgroup.salein1.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.Classes.Constant;
import ir.kitgroup.salein1.Classes.CustomProgress;
import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.MobileView.MainOrderMobileFragment;
import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.salein1.Fragments.TabletView.OrderFragment;
import ir.kitgroup.salein1.Fragments.ShowDetailFragment;

import ir.kitgroup.salein1.Models.ModelLog;
import ir.kitgroup.salein1.R;
import ir.kitgroup.salein1.Util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductAdapter1 extends RecyclerView.Adapter<ProductAdapter1.viewHolder> {

    private final Context context;

    private CustomProgress customProgress;

    private final List<Product> productsList ;

    private final String maxSale;

    private final DecimalFormat df;

    private  final DecimalFormat format = new DecimalFormat("#,###,###,###");


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


    public ProductAdapter1(Context context, List<Product> productsList,String maxSale) {
        this.context = context;
        this.productsList = productsList;
        this.maxSale=maxSale;
        customProgress=CustomProgress.getInstance();

        df = new DecimalFormat();
        //df.setMaximumFractionDigits(2);


    }


    public void addLoadingView() {
        //add loading item
        new Handler().post(() -> {
            productsList.add(null);
            notifyItemInserted(productsList.size() - 1);
        });
    }

    public void removeLoadingView() {
        //Remove loading item
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

                return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycle_products_item_tablet, parent,
                        false));
            } else {

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
            holder.itemClick = false;


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


            } else {


                holder.productImage.setImageResource(R.drawable.application_logo1);


            }

            final int newColor = context.getResources().getColor(R.color.purple_500);
            holder.ivEdit.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);




            holder.productName.setText(productsList.get(holder.getAdapterPosition()).getPRDNAME());


            if (productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1() > 0) {
                holder.productPrice.setText(format.format(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()) + " ریال ");
            }













            holder.tab = 0;
            holder.productImage.setOnClickListener(view -> {
                holder.tab++;
                if (holder.tab == 3) {
                    holder.tab = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", productsList.get(holder.getAdapterPosition()).I);
                    ShowDetailFragment showDetailFragment = new ShowDetailFragment();
                    showDetailFragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, showDetailFragment, "ShowDetailFragment").addToBackStack("ShowDetailF").commit();
                }

            });

            if (productsList.get(holder.getAdapterPosition()).descItem != null)
                holder.edtDesc.setText(productsList.get(holder.getAdapterPosition()).descItem);
            else
                holder.edtDesc.setText("");




            if (productsList.get(holder.getAdapterPosition()).getAmount() > 0) {

                holder.ivMinus.setVisibility(View.VISIBLE);
                holder.ProductAmountTxt.setVisibility(View.VISIBLE);
            } else {
                holder.ivMinus.setVisibility(View.GONE);
                holder.ProductAmountTxt.setVisibility(View.GONE);
            }

            holder.ivMax.setOnClickListener(view -> {


                    holder.itemClick = true;
                    doAction(
                            holder.textWatcher,
                            holder.ProductAmountTxt,
                            holder.ivMinus,
                            holder.getAdapterPosition(),
                            Select.from(User.class).first().userName,
                            Select.from(User.class).first().passWord,
                            maxSale,
                            String.valueOf(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()),
                            productsList.get(holder.getAdapterPosition()).PERC_DIS / 100,
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
                            holder.getAdapterPosition(),
                            Select.from(User.class).first().userName,
                            Select.from(User.class).first().passWord,
                            maxSale,
                            String.valueOf(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()),
                            productsList.get(holder.getAdapterPosition()).PERC_DIS / 100,
                            productsList.get(holder.getAdapterPosition()).getPRDUID(),
                            "",
                            2

                    );
                    //clickItem.onClick(productsList.get(holder.getAdapterPosition()).getPRDUID(), String.valueOf(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()), productsList.get(holder.getAdapterPosition()).PERC_DIS / 100,"", 2);

            });


            if (holder.textWatcher==null){
                holder.textWatcher=new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
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

                            doAction(
                                    holder.textWatcher,
                                    holder.ProductAmountTxt,
                                    holder.ivMinus,
                                    holder.getAdapterPosition(),
                                    Select.from(User.class).first().userName,
                                    Select.from(User.class).first().passWord,
                                    maxSale,
                                    String.valueOf(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()),
                                    productsList.get(holder.getAdapterPosition()).PERC_DIS / 100,
                                    productsList.get(holder.getAdapterPosition()).getPRDUID(),
                                    s.toString(),
                                    3

                            );
                           // clickItem.onClick(productsList.get(holder.getAdapterPosition()).getPRDUID(), String.valueOf(productsList.get(holder.getAdapterPosition()).getPRDPRICEPERUNIT1()), productsList.get(holder.getAdapterPosition()).PERC_DIS / 100,s.toString(), 3);




                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };
            }


            holder.ProductAmountTxt.removeTextChangedListener(holder.textWatcher);
            holder.ProductAmountTxt.setText(df.format(productsList.get(holder.getAdapterPosition()).getAmount()));
            holder.ProductAmountTxt.addTextChangedListener(holder.textWatcher);






            holder.ivEdit.setOnClickListener(v -> {
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


        private boolean itemClick = false;
        private final TextView productName;
        private final TextView productPrice;
        private final EditText ProductAmountTxt;

        private final EditText edtDesc;
        private final ImageView ivEdit;
        private final RoundedImageView productImage;


        private final ImageView ivMinus;
        private final ImageView ivMax;
        private TextWatcher textWatcher;


        public viewHolder(View itemView) {
            super(itemView);

            RelativeLayout cardView = itemView.findViewById(R.id.order_recycle_item_product_layout);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            edtDesc = itemView.findViewById(R.id.edt_description_temp);


            productName = itemView.findViewById(R.id.order_recycle_item_product_name);


            productPrice = itemView.findViewById(R.id.order_recycle_item_product_price);


            productImage = itemView.findViewById(R.id.order_recycle_item_product_img);
            ProductAmountTxt = itemView.findViewById(R.id.order_recycle_item_product_txt_amount);



            ivMinus = itemView.findViewById(R.id.iv_minus);
            ivMax = itemView.findViewById(R.id.iv_max);


            if (LauncherActivity.screenInches >= 7 && productImage != null) {
                if (LauncherActivity.screenInches >= 7) {
                    int height;
                    if (LauncherActivity.width > LauncherActivity.height)
                        height = LauncherActivity.width / 2;
                    else
                        height = LauncherActivity.height / 2;

                    if (OrderFragment.productLevel1List.size() <= 1) {
                        cardView.getLayoutParams().width = (int) (height / 2.7);
                    } else {
                        cardView.getLayoutParams().width = (int) (height / 2.95);
                    }
                    cardView.getLayoutParams().height = (int) (height / 2.95);


                }


            }


        }
    }
    private void getMaxSales(TextWatcher textWatcher,EditText ProductAmountTxt,ImageView ivMinus,String userName, String pass, String Price, Double
            discount, String GuidProduct,String s, int MinOrPlus) {
        customProgress.showProgress(context,  "دریافت مانده",false);

        try {

            Call<String> call = App.api.getMaxSales(userName, pass, GuidProduct);

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

                        if (Integer.parseInt(response.body()) <= 0) {
                            Toast.makeText(context, "این کالا موجود نمی باشد", Toast.LENGTH_SHORT).show();
                            customProgress.hideProgress();
                            return;
                        }
                        ArrayList<Product> resultProduct = new ArrayList<>(Util.AllProduct);
                        CollectionUtils.filter(resultProduct, r -> r.getPRDUID().equals(GuidProduct));

                        if (resultProduct.size() > 0) {
                            double amount = 0;
                            if (MinOrPlus == 1)
                                amount = Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() + 1;

                            else if (MinOrPlus == 2) {
                                if (Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() >= 1)
                                    amount = Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() - 1;

                                else
                                    return;

                            }
                            else {
                                try {
                                    amount=Float.parseFloat(s);
                                }catch (Exception ignored){

                                }
                            }


                            if (Integer.parseInt(response.body()) - amount < 0) {
                                Toast.makeText(context, "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();
                                customProgress.hideProgress();

                                return;
                            }
                            Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);


                            ArrayList<Invoicedetail> result = new ArrayList<>(MainOrderMobileFragment.invoiceDetailList);
                            CollectionUtils.filter(result, r -> r.PRD_UID.equals(GuidProduct));
                            //edit
                            if (result.size() > 0) {


                                if (amount == 0) {
                                    MainOrderMobileFragment.invoiceDetailList.remove(result.get(0));

                                    if (MinOrPlus!=3) {
                                        ProductAmountTxt.removeTextChangedListener(textWatcher);
                                        ProductAmountTxt.setText("0");
                                        ProductAmountTxt.addTextChangedListener(textWatcher);
                                        ivMinus.setVisibility(View.GONE);
                                        ProductAmountTxt.setVisibility(View.GONE);
                                    }


                                    clickItem.onClick();
                                    return;
                                }

                                Double sumprice = (amount * Float.parseFloat(Price));
                                Double discountPrice = sumprice * discount;
                                Double totalPrice = sumprice - discountPrice;
                                MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);
                                MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                                MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_DISCOUNT = String.valueOf(discountPrice);

                                if (MinOrPlus!=3){
                                    ProductAmountTxt.removeTextChangedListener(textWatcher);
                                    ProductAmountTxt.setText(df.format(amount));
                                    ProductAmountTxt.addTextChangedListener(textWatcher);
                                }


                                ivMinus.setVisibility(View.VISIBLE);
                                ProductAmountTxt.setVisibility(View.VISIBLE);



                            }
                            else {
                                Invoicedetail invoicedetail = new Invoicedetail();
                                invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                                invoicedetail.INV_UID = MainOrderMobileFragment.Inv_GUID;
                                invoicedetail.INV_DET_QUANTITY = String.valueOf(amount);
                                invoicedetail.INV_DET_PRICE_PER_UNIT = Price;
                                invoicedetail.INV_DET_DISCOUNT = "0";
                                Double sumprice = (amount * Float.parseFloat(Price));
                                Double discountPrice = sumprice * discount;
                                Double totalPrice = sumprice - discountPrice;
                                invoicedetail.INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                invoicedetail.INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                                invoicedetail.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                                invoicedetail.INV_DET_TAX = "0";
                                invoicedetail.INV_DET_STATUS = true;
                                invoicedetail.PRD_UID = GuidProduct;
                                invoicedetail.INV_DET_TAX_VALUE = "0";
                                invoicedetail.INV_DET_DESCRIBTION = "";
                                MainOrderMobileFragment.invoiceDetailList.add(invoicedetail);


                                ProductAmountTxt.removeTextChangedListener(textWatcher);
                                ProductAmountTxt.setText(df.format(amount));
                                ProductAmountTxt.addTextChangedListener(textWatcher);
                                ivMinus.setVisibility(View.VISIBLE);
                                ProductAmountTxt.setVisibility(View.VISIBLE);

                            }


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


    private void doAction(TextWatcher textWatcher,EditText ProductAmountTxt,ImageView ivMinus,int position,String userName, String passWord, String maxSales, String Price, double discount, String Inv_GUID, String  s, int MinOrPlus){

        if (maxSales.equals("1")) {
            getMaxSales(textWatcher,ProductAmountTxt,ivMinus,userName, passWord, Price, discount, Inv_GUID,s, MinOrPlus);
        }
        else {
            ArrayList<Product> resultProduct = new ArrayList<>(Util.AllProduct);
            CollectionUtils.filter(resultProduct, r -> r.getPRDUID().equals(Inv_GUID));

            if (resultProduct.size() > 0) {
                double amount = 0;
                if (MinOrPlus == 1)
                    amount = Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() + 1;
                else if (MinOrPlus == 2) {
                    if (Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() >= 1)
                        amount = Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() - 1;

                    else
                        return;

                }else {
                    try {
                        amount=Float.parseFloat(s);
                    }catch (Exception e){

                    }
                }


                Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);


                ArrayList<Invoicedetail> result = new ArrayList<>(MainOrderMobileFragment.invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(Inv_GUID));
                //edit
                if (result.size() > 0) {
                    if (amount == 0) {
                        MainOrderMobileFragment.invoiceDetailList.remove(result.get(0));

                       if (MinOrPlus!=3) {
                           ProductAmountTxt.removeTextChangedListener(textWatcher);

                           ProductAmountTxt.setText("0");
                           ProductAmountTxt.addTextChangedListener(textWatcher);
                           ivMinus.setVisibility(View.GONE);
                           ProductAmountTxt.setVisibility(View.GONE);
                       }

                       clickItem.onClick();
                       return;
                    }

                    Double sumprice = (amount * Float.parseFloat(Price));
                    Double discountPrice = sumprice * discount;
                    Double totalPrice = sumprice - discountPrice;
                    MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);
                    MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                    MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                    MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_DISCOUNT = String.valueOf(discountPrice);
                   if (MinOrPlus!=3){
                       ProductAmountTxt.removeTextChangedListener(textWatcher);
                       ProductAmountTxt.setText(df.format(amount));
                       ProductAmountTxt.addTextChangedListener(textWatcher);
                   }


                    ivMinus.setVisibility(View.VISIBLE);
                    ProductAmountTxt.setVisibility(View.VISIBLE);



                } else {
                    Invoicedetail invoicedetail = new Invoicedetail();
                    invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                    invoicedetail.INV_UID = MainOrderMobileFragment.Inv_GUID;
                    invoicedetail.INV_DET_QUANTITY = String.valueOf(amount);
                    invoicedetail.INV_DET_PRICE_PER_UNIT = Price;
                    invoicedetail.INV_DET_DISCOUNT = "0";
                    Double sumprice = (amount * Float.parseFloat(Price));
                    Double discountPrice = sumprice * discount;
                    Double totalPrice = sumprice - discountPrice;
                    invoicedetail.INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                    invoicedetail.INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                    invoicedetail.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                    invoicedetail.INV_DET_TAX = "0";
                    invoicedetail.INV_DET_STATUS = true;
                    invoicedetail.PRD_UID = Inv_GUID;
                    invoicedetail.INV_DET_TAX_VALUE = "0";
                    invoicedetail.INV_DET_DESCRIBTION = "";
                    MainOrderMobileFragment.invoiceDetailList.add(invoicedetail);

                    ProductAmountTxt.removeTextChangedListener(textWatcher);

                    ProductAmountTxt.setText(df.format(amount));
                    ProductAmountTxt.addTextChangedListener(textWatcher);
                    ivMinus.setVisibility(View.VISIBLE);
                    ProductAmountTxt.setVisibility(View.VISIBLE);

                    clickItem.onClick();

                }



            }
        }
    }

}





