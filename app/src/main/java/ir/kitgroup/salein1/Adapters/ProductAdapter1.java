package ir.kitgroup.salein1.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import java.util.List;

import ir.kitgroup.salein1.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein1.Classes.Constant;
import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.Fragments.OrderFragment;
import ir.kitgroup.salein1.Fragments.ShowDetailFragment;

import ir.kitgroup.salein1.R;


public class ProductAdapter1 extends RecyclerView.Adapter<ProductAdapter1.viewHolder> {

    private final Context context;

    private final List<Product> productsList ;

    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");


    public interface ClickItem {
        void onClick(String GUID, String Price, Double discount, boolean type);
        //TRUE     PLUS AMOUNT
        //FALSE     MINUS AMOUNT
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


        final Product products = productsList.get(position);

        if (products != null) {
            holder.itemClick = false;


            String yourFilePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + "SaleIn" + "/" + products.I.toUpperCase() + ".jpg";
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


            holder.GUID = products.getPRDUID();

            holder.productName.setText(products.getPRDNAME());


            if (products.getPRDPRICEPERUNIT1() > 0) {
                holder.productPrice.setText(format.format(products.getPRDPRICEPERUNIT1()) + " ریال ");
            }


            holder.ProductAmountTxt.setText(format.format(products.getAmount()));

            if (products.getAmount() > 0) {

                holder.ivMinus.setVisibility(View.VISIBLE);
                holder.ProductAmountTxt.setVisibility(View.VISIBLE);
            } else {
                holder.ivMinus.setVisibility(View.GONE);
                holder.ProductAmountTxt.setVisibility(View.GONE);
            }
            holder.ivMax.setOnClickListener(view -> {

                if (!holder.itemClick) {


                    holder.itemClick = true;
                    clickItem.onClick(holder.GUID, String.valueOf(products.getPRDPRICEPERUNIT1()), products.PERC_DIS / 100, true);
                }

            });

            holder.tab = 0;
            holder.productImage.setOnClickListener(view -> {
                holder.tab++;
                if (holder.tab == 3) {
                    holder.tab = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", products.I);
                    ShowDetailFragment showDetailFragment = new ShowDetailFragment();
                    showDetailFragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, showDetailFragment, "ShowDetailFragment").addToBackStack("ShowDetailF").commit();
                }

            });

            if (products.descItem != null)
                holder.edtDesc.setText(products.descItem);
            else
                holder.edtDesc.setText("");


            holder.ivMinus.setOnClickListener(v -> {
                if (!holder.itemClick) {
                    holder.itemClick = true;
                    clickItem.onClick(holder.GUID, String.valueOf(products.getPRDPRICEPERUNIT1()), products.PERC_DIS / 100, false);
                }
            });

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
        private String GUID;

        private boolean itemClick = false;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView ProductAmountTxt;

        private final EditText edtDesc;
        private final ImageView ivEdit;
        private final RoundedImageView productImage;


        private final ImageView ivMinus;
        private final ImageView ivMax;


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


}





