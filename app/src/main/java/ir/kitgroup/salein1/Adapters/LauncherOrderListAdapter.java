package ir.kitgroup.salein1.Adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.collections4.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.salein1.MainActivity;
import ir.kitgroup.salein1.R;


public class LauncherOrderListAdapter extends RecyclerView.Adapter<LauncherOrderListAdapter.viewHolder> {

    private List<Invoicedetail> orderDetailList = new ArrayList<>();
    private Context context;
    private int fontSize=0;
    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");

    public interface DeleteItem {
        void onRowDelete(String GUID);
    }
    private DeleteItem deleteItem;
    public void deleteItemListener(DeleteItem deleteItem){
        this.deleteItem=deleteItem;
    }



    public interface EditAmountItem {
        void onEditAmountRow(String GUID,String s,String Price);
    }
    private EditAmountItem editAmountItem;
    public void editAmountItemListener(EditAmountItem editAmountItem){
        this.editAmountItem=editAmountItem;
    }

    public LauncherOrderListAdapter(Context context, List<Invoicedetail> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (MainActivity.screenInches >= 7) {
            fontSize=13;
        }
        else {
            fontSize=12;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.launcher_order_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {

        GradientDrawable bgLight = new GradientDrawable();
        bgLight.setColor(0xFFFFFFFF);

        GradientDrawable bgDark = new GradientDrawable();
        bgDark.setColor(0xFFE1E3E8);



        if (position % 2 == 0) {
            holder.cardBackground.setBackground(bgDark);
        } else {
            holder.cardBackground.setBackground(bgLight);
        }

        final Invoicedetail invoicedetail = orderDetailList.get(position);
        holder.mainGUID= invoicedetail.PRD_UID;
        holder.row.setText(String.valueOf(holder.getAdapterPosition()+1));

        holder.txtDiscount.setText(format.format(Double.parseDouble(invoicedetail.INV_DET_PERCENT_DISCOUNT)));
        holder.txtSumDiscount.setText(format.format(Double.parseDouble(invoicedetail.INV_DET_DISCOUNT)));

        ArrayList<Product> rst = new ArrayList<>();
        rst.addAll(LauncherOrganizationFragment.AllProduct);
        CollectionUtils.filter(rst, r -> r.getPRDUID().equals(invoicedetail.PRD_UID));

        if (rst.size()>0){
            holder.nameProduct.setText(rst.get(0).getPRDNAME());

        }

        holder.sumPriceProduct.setText(format.format(Float.parseFloat(invoicedetail.INV_DET_TOTAL_AMOUNT)));
        holder.countProduct.setText(format.format(Double.parseDouble(invoicedetail.INV_DET_QUANTITY)));



    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private String mainGUID;
        private TextView row;
        private TextView nameProduct;
        private TextView countProduct;
        private TextView sumPriceProduct;
        private TextView txtSumDiscount;
        private TextView txtDiscount;
        private LinearLayout cardBackground;


        public viewHolder(View itemView) {
            super(itemView);


            cardBackground=itemView.findViewById(R.id.launcher_order_list_item_background_item);
            row=itemView.findViewById(R.id.launcher_item_recycle_order_row);
            nameProduct=itemView.findViewById(R.id.launcher_item_recycle_order_name_product);
            countProduct=itemView.findViewById(R.id.launcher_item_recycle_order_count_product);
            sumPriceProduct=itemView.findViewById(R.id.launcher_item_recycle_order_sumPrice);
            txtSumDiscount=itemView.findViewById(R.id.launcher_order_header_sum_discount_item);
            txtDiscount=itemView.findViewById(R.id.launcher_order_header_discount_item);


            row.setTextSize(fontSize);
            nameProduct.setTextSize(fontSize);
            countProduct.setTextSize(fontSize);
            sumPriceProduct.setTextSize(fontSize);
//            row.setWidth((int) (MainActivity.width / 2 * .075));
//            nameProduct.setWidth((int) (MainActivity.width / 2 * .575));
//            countProduct.setWidth((int) (MainActivity.width / 2 * .175));
//            sumPriceProduct.setWidth((int) (MainActivity.width / 2 * .175));



        }
    }



}








