package ir.kitgroup.salein1.Adapters;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.card.MaterialCardView;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.Classes.Utilities;
import ir.kitgroup.salein1.DataBase.Invoice;
import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.OrderType;
import ir.kitgroup.salein1.DataBase.Tables;
import ir.kitgroup.salein1.MainActivity;
import ir.kitgroup.salein1.R;


public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.viewHolder> {

    private List<Tables> tableList = new ArrayList<>();
    private List<Invoice> list = new ArrayList<>();
    private Context context;
    private int type;//1seen table   2seen invoice
    private int fontSize = 0;
    private  final DecimalFormat format = new DecimalFormat("#,###,###,###");


    public void setOnClickItemListener(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public interface ClickItem {
        void onRowClick(String Name, boolean Reserve, String Guid);
    }

    private ClickItem clickItem;


    public TableListAdapter(Context context, List<Tables> tableList,List<Invoice> list,int type) {
        this.context = context;
        this.tableList = tableList;
        this.list = list;
        this.type=type;


    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (MainActivity.screenInches >= 7) {
            fontSize = 13;
        } else {
            fontSize = 12;
        }


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_table, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {

        Tables table = null;
        Invoice invoice = null;




        if (type==1){
            table = tableList.get(position);
            if (table.N.equals("بیرون بر")) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setImageResource(R.drawable.ic_bys);
                holder.tableName.setText("");
                holder.tvCapacity.setText("سفارش بیرون بر");
                holder.tvStatus.setText("");

                Invoice ord3 = Select.from(Invoice.class).where("TBLUID ='" + table.I + "'").first();
                if (ord3!=null){

                }

                holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.orange_light));

            }
            else {
                holder.imageView.setVisibility(View.GONE);
                holder.tableName.setVisibility(View.VISIBLE);
                holder.tableName.setText("میز شماره : " + table.N);
                holder.tvCapacity.setText("تعداد صندلی میز : " + table.CC);


            }


            if (table.ACT != null && table.RSV != null) {
                if (!table.N.equals("empty") && !table.N.equals("بیرون بر") && !table.ACT && !table.RSV) {
                    holder.imageView.setVisibility(View.GONE);
                    holder.tvStatus.setText("میز خالی است.");
                    holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.green_table));



                } else if (!table.N.equals("empty") && !table.N.equals("بیرون بر") && table.RSV) {
                    holder.imageView.setVisibility(View.GONE);
                    holder.tvStatus.setText("میز رزرو شده است.");
                    holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.blue_table));




                } else if (!table.N.equals("empty") && !table.N.equals("بیرون بر") && table.ACT) {
                    holder.imageView.setVisibility(View.GONE);
                    holder.tvStatus.setText("میز مشغول است.");
                    holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.red_table));


                } else if (!table.N.equals("empty") && !table.N.equals("بیرون بر")) {
                    holder.imageView.setVisibility(View.GONE);
                    holder.tvStatus.setText("میز خالی است.");
                    holder.rlTable.setBackgroundColor(context.getResources().getColor(R.color.green_table));


                    try {
                        Invoice ord3 = Select.from(Invoice.class).where("TBLUID ='" + table.I + "'").first();
                        String guid = ord3.INV_UID;
                        ord3.delete();
                        for (Invoicedetail ordDetail : Select.from(Invoicedetail.class).where("INVUID = '" + guid + "'").list()) {
                            Invoicedetail.deleteInTx(ordDetail);
                        }
                    } catch (Exception e) {

                    }


                }


            }

        }else {
            invoice = list.get(position);
            boolean status=false;


            status=invoice.SendStatus;


            if (status)
                holder.tableName.setText("شماره فاکتور : " + (holder.getAdapterPosition()+1) +"  ("+   "موفقیت امیز" + ")");
                else
                holder.tableName.setText("شماره فاکتور : " + (holder.getAdapterPosition()+1)+"("+"ناموفق"+")");

            holder.tvCapacity.setText("جمع مبلغ : " + format.format(Double.parseDouble(invoice.INV_EXTENDED_AMOUNT)));


            Utilities util = new Utilities();
            Locale loc = new Locale("en_US");
            Utilities.SolarCalendar sc = util.new SolarCalendar(invoice.INV_DUE_DATE);
            String date=(sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (String.valueOf(sc.strMonth)) + "\t" + String.valueOf(sc.year);
            holder.tvStatus.setText("تاریخ فاکتور : " + date );
        }


        Tables finalTable = table;
        Invoice finalInvoice = invoice;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (type==1){
                    assert finalTable != null;
                    if (!finalTable.ACT && !finalTable.RSV) {
                        clickItem.onRowClick(finalTable.N, false, finalTable.I);

                    } else {
                        if (finalTable.RSV) {
                            clickItem.onRowClick("میز رزرو شده است", true, finalTable.I);

                        } else if (finalTable.ACT) {
                            clickItem.onRowClick("میز مشغول است.", true, finalTable.I);

                        } else {
                            clickItem.onRowClick(finalTable.N, true, finalTable.I);
                        }
                    }
                }else {
                    clickItem.onRowClick("", true, finalInvoice.INV_UID);
                }




            }
        });


    }

    @Override
    public int getItemCount() {
        if (type==1)
        return tableList.size();
        else
            return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView rlTable;

        private TextView tableName;
        private TextView tvCapacity;
        private TextView tvStatus;
        private ImageView imageView;


        public viewHolder(View itemView) {
            super(itemView);
            rlTable = itemView.findViewById(R.id.rl_table);

            imageView = itemView.findViewById(R.id.iv_table);
            tableName = itemView.findViewById(R.id.tv_number_table);
            tvCapacity = itemView.findViewById(R.id.tv_capacity_table);
            tvStatus = itemView.findViewById(R.id.tv_status_table);


           // tableName.setTextSize(fontSize);
            if (App.mode==1){
                if (MainActivity.screenInches >= 7) {
                    int height = 0;
                    if (MainActivity.width > MainActivity.height)
                        height = MainActivity.width / 2;
                    else
                        height = MainActivity.height / 2;
                    rlTable.getLayoutParams().width = (int) (height / 3.3);
                    rlTable.getLayoutParams().height = (int) (height / 4.2);


                } else {
                    int height = MainActivity.width;
                    rlTable.getLayoutParams().width = (int) (height / 2.5);
                    rlTable.getLayoutParams().height = (int) (height / 3.1);

                }
            }



        }
    }


}





