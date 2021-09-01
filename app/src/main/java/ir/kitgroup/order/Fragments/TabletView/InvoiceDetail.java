package ir.kitgroup.order.Fragments.TabletView;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import ir.kitgroup.order.Adapters.DescriptionAdapter;
import ir.kitgroup.order.Adapters.InvoiceDetailAdapter;
import ir.kitgroup.order.Adapters.InvoiceDetailLargeAdapter;
import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.classes.CustomProgress;
import ir.kitgroup.order.classes.Utilities;
import ir.kitgroup.order.DataBase.Invoice;
import ir.kitgroup.order.DataBase.OrderType;
import ir.kitgroup.order.DataBase.Product;
import ir.kitgroup.order.MainActivity;
import ir.kitgroup.order.models.Description;
import ir.kitgroup.order.models.ModelDesc;
import ir.kitgroup.order.models.ModelLog;
import ir.kitgroup.order.R;
import ir.kitgroup.order.Util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceDetail extends Fragment {

    private CustomProgress customProgress;

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private RecyclerView recyclerView;
    public static ArrayList<ir.kitgroup.order.DataBase.InvoiceDetail> invoiceDetailList = new ArrayList<>();
    public InvoiceDetailAdapter invoiceDetailAdapter;
    private String maxSales = "0";
    private EditText edtDescriptionItem;


    private String userName;
    private String passWord;


    public ArrayList<ir.kitgroup.order.DataBase.InvoiceDetail> invoiceDetailLargeList = new ArrayList<>();
    private float sumPrice = 0;
    private TextView sumPriceTxt;
    private float sumPurePrice = 0;
    private TextView sumPurePriceTxt;


    private TextView tvTitle;
    private String factorGID = "";
    private String type = "";

    private TextView tvPurePrice;
    private TextView tvSumDiscount;
    private TextView tvTotalPrice;


    private EditText edtDescription;


    private Dialog dialogDescription;
    public static ArrayList<Description> descriptionList = new ArrayList<>();
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv = "";


    private LinearLayout rlBottom;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        Bundle bundle = getArguments();
        type = bundle.getString("type");
        factorGID = bundle.getString("FGUID");
        String TGID = bundle.getString("TGID");
        String ACCNM = bundle.getString("ACCNM");
        String ACCGID = bundle.getString("ACCGID");
        String OTYPE = bundle.getString("OTYPE");


        customProgress = CustomProgress.getInstance();


        //region Tablet
        int fontSize = 0;
        View view = null;
        if (MainActivity.screenInches >= 7) {
            fontSize = 13;
            view = inflater.inflate(R.layout.fragment_invoice_detail, container, false);
            TextView tvRow = view.findViewById(R.id.tv_row_order);
            TextView tvName = view.findViewById(R.id.tv_name_order);
            TextView edtAmount = view.findViewById(R.id.tv_amount_order);
            TextView tvPrice = view.findViewById(R.id.tv_price_order);
            TextView tvDiscountPercent = view.findViewById(R.id.tv_discount_percent_order);
            TextView tvSumDiscountPrice = view.findViewById(R.id.tv_discount_price_order);
            TextView tvSumPrice = view.findViewById(R.id.tv_sum_price_order);
            TextView tvSumPurePrice = view.findViewById(R.id.tv_sum_pure_price_order);
            recyclerView = view.findViewById(R.id.recycler_invoice_detail);
            TextView tvDate = view.findViewById(R.id.tvDate);
            TextView tvNameCustomer = view.findViewById(R.id.tvNameCustomer);
            TextView tvTypeOrder = view.findViewById(R.id.tvTypeOrder);

            tvTotalPrice = view.findViewById(R.id.launcher_order_sumPrice_txt);
            tvPurePrice = view.findViewById(R.id.launcher_order_totalPrice_txt);
            tvSumDiscount = view.findViewById(R.id.launcher_order_discount_txt);
            rlBottom = view.findViewById(R.id.rl_bottom);
            edtDescription = view.findViewById(R.id.edt_description_order);


            tvRow.setTextSize(fontSize);
            tvName.setTextSize(fontSize);
            edtAmount.setTextSize(fontSize);
            tvPrice.setTextSize(fontSize);
            tvSumDiscountPrice.setTextSize(fontSize);
            tvDiscountPercent.setTextSize(fontSize);
            tvSumPrice.setTextSize(fontSize);
            tvSumPurePrice.setTextSize(fontSize);


            if (type.equals("1")) {
                //  if (MainActivity.screenInches>=7)
                //  rlBottom.setVisibility(View.VISIBLE);
                Invoice invoice = Select.from(Invoice.class).where("INVUID ='" + factorGID + "'").first();
                if (invoice != null) {


                    String text1 = "نام مشترک : " + invoice.Acc_name;
                    SpannableString ss1 = new SpannableString(text1);
                    StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
                    ss1.setSpan(boldSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvNameCustomer.setText(ss1);


                    Utilities util = new Utilities();
                    Locale loc = new Locale("en_US");
                    Utilities.SolarCalendar sc = util.new SolarCalendar(invoice.INV_DUE_DATE);


                    String text2 = "تاریخ ایجاد : " + (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + ((sc.strMonth)) + "\t" + (sc.year);
                    SpannableString ss2 = new SpannableString(text2);
                    StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
                    ss2.setSpan(boldSpan2, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvDate.setText(ss2);


                    OrderType orderType = Select.from(OrderType.class).where("c ='" + invoice.INV_TYPE_ORDER + "'").first();
                    String text3 = "";
                    if (orderType != null) {
                        text3 = "نوع سفارش : " + orderType.getN();
                    }

                    SpannableString ss3 = new SpannableString(text3);
                    StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
                    ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvTypeOrder.setText(ss3);
                }


            } else {
//                String text1 = "نام مشترک : ";
//                String nameCustomer = bundle.getString("ACCNM");
//                if (!nameCustomer.equals("نام مشترک"))
//                    text1 = "نام مشترک : " + nameCustomer;
//                SpannableString ss1 = new SpannableString(text1);
//                StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
//                ss1.setSpan(boldSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                tvNameCustomer.setText(ss1);


                Calendar calendar = Calendar.getInstance();
                Utilities util = new Utilities();
                Locale loc = new Locale("en_US");
                Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
                String text2 = "تاریخ ایجاد : " + (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (String.valueOf(sc.strMonth)) + "\t" + String.valueOf(sc.year);
                SpannableString ss2 = new SpannableString(text2);
                StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
                ss2.setSpan(boldSpan2, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvDate.setText(ss2);


                String orderType = bundle.getString("OTYPE");
                OrderType ordTY = Select.from(OrderType.class).where("c ='" + orderType + "'").first();
                String text3 = "نوع سفارش : " + " ";

                if (ordTY != null)
                    text3 = "نوع سفارش : " + ordTY.getN();

                SpannableString ss3 = new SpannableString(text3);
                StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
                ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvTypeOrder.setText(ss3);


            }


            invoiceDetailLargeList.clear();
            if (type.equals("1"))
                invoiceDetailLargeList.addAll(Select.from(ir.kitgroup.order.DataBase.InvoiceDetail.class).where("INVUID ='" + factorGID + "'").list());
            else if (MainActivity.screenInches >= 7)
                invoiceDetailLargeList.addAll(OrderFragment.invoiceDetailList);
//            else
//                invoiceDetailLargeList.addAll(MainOrderMobileFragment.invoiceDetailList);
            InvoiceDetailLargeAdapter invoiceDetailLargeAdapter = new InvoiceDetailLargeAdapter(getContext(), invoiceDetailLargeList, type);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(invoiceDetailLargeAdapter);
            recyclerView.setHasFixedSize(false);


            recyclerView.post(() -> recyclerView.smoothScrollToPosition(invoiceDetailLargeList.size() - 1));


            invoiceDetailLargeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();

                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);

                    invoiceDetailLargeList.remove(invoiceDetailLargeList.get(positionStart));

                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);


                }
            });

            invoiceDetailLargeAdapter.SetOnEditItem(new InvoiceDetailLargeAdapter.EditItem() {
                @Override
                public void onRowEdit() {

                    float sumPrice = 0;
                    float sumPurePrice = 0;
                    double sumDiscountPrice = 0;


                    for (int i = 0; i < invoiceDetailLargeList.size(); i++) {
                        sumPrice = (float) (sumPrice + invoiceDetailLargeList.get(i).INV_DET_QUANTITY
                                * Float.parseFloat(invoiceDetailLargeList.get(i).INV_DET_PRICE_PER_UNIT));
                        sumDiscountPrice = sumDiscountPrice + Double.parseDouble(invoiceDetailLargeList.get(i).INV_DET_DISCOUNT);
                        sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailLargeList.get(i).INV_DET_TOTAL_AMOUNT);
                    }


                    String text1 = "جمع فاکتور : " + format.format(sumPrice) + " ریال ";
                    SpannableString ss1 = new SpannableString(text1);
                    StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
                    ss1.setSpan(boldSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvTotalPrice.setText(ss1);


                    String text2 = "جمع تخفیف : " + format.format(sumDiscountPrice) + " ریال ";
                    SpannableString ss2 = new SpannableString(text2);
                    StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
                    ss2.setSpan(boldSpan2, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvSumDiscount.setText(ss2);


                    String text3 = "جمع خالص : " + format.format(sumPurePrice) + " ریال ";
                    SpannableString ss3 = new SpannableString(text3);
                    StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
                    ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvPurePrice.setText(ss3);


                }
            });


            ShowData(invoiceDetailLargeList);
        }
        //endregion Tablet


        //region Mobile


        //endregion Mobile

        return view;

    }

    @Override
    public void onDestroy() {

        if (type.equals("2")) {
            if (MainActivity.screenInches >= 7) {
                OrderFragment.invoiceDetailAdapter.notifyDataSetChanged();
                OrderFragment.descriptionFactor = edtDescription.getText().toString();
            }


        }

        super.onDestroy();
    }


    public void ShowData(ArrayList<ir.kitgroup.order.DataBase.InvoiceDetail> list) {


        double sumPrice = 0;
        double sumDiscount = 0;
        double sumPurePrice = 0;

        for (int i = 0; i < list.size(); i++) {
            sumPrice = sumPrice + (list.get(i).INV_DET_QUANTITY * Double.parseDouble(list.get(i).INV_DET_PRICE_PER_UNIT));
            sumDiscount = sumDiscount + Double.parseDouble(list.get(i).INV_DET_DISCOUNT);
            sumPurePrice = sumPurePrice + Double.parseDouble(list.get(i).INV_DET_TOTAL_AMOUNT);
        }


        String text1 = "جمع فاکتور : " + format.format(sumPrice);
        SpannableString ss1 = new SpannableString(text1);
        StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
        ss1.setSpan(boldSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTotalPrice.setText(ss1);


        String text2 = "جمع تخفیف : " + format.format(sumDiscount);
        SpannableString ss2 = new SpannableString(text2);
        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        ss2.setSpan(boldSpan2, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSumDiscount.setText(ss2);


        String text3 = "جمع خالص : " + format.format(sumPurePrice);
        SpannableString ss3 = new SpannableString(text3);
        StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
        ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPurePrice.setText(ss3);


    }


    private void getDescription(String userName, String pass, String id) {

        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات...", false);

        try {

            Call<String> call = App.api.getDescription(userName, pass, id);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelDesc>() {
                    }.getType();
                    ModelDesc iDs = null;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {

                    }

                    descriptionList.clear();
                    if (iDs != null)
                        descriptionList.addAll(iDs.getDescriptions());


                    for (int i = 0; i < descriptionList.size(); i++) {
                        if (edtDescriptionItem.getText().toString().contains("'" + descriptionList.get(i).DSC + "'")) {
                            descriptionList.get(i).Click = true;
                        }

                    }

                    descriptionAdapter.notifyDataSetChanged();
                    customProgress.hideProgress();

                    dialogDescription.show();


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }

    private void getMaxSales(String userName, String pass, String id, String Price, Double
            discount, String GUID, String TYPE, String s, boolean MinOrPlus) {


        try {

            Call<String> call = App.api.getMaxSales(userName, pass, id);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    customProgress.hideProgress();

                    int remain = -1000000000;
                    try {
                        remain = Integer.parseInt(response.body());
                    } catch (Exception e) {
                        Gson gson = new Gson();
                        Type typeIDs = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                        int message = iDs.getLogs().get(0).getMessage();
                        String description = iDs.getLogs().get(0).getDescription();
                        if (message == 1) {
                        } else if (message == 2) {
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        } else if (message == 3) {
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (remain != -1000000000) {
                        if (TYPE.equals("1")) {
                            if (Integer.parseInt(response.body()) <= 0) {
                                Toast.makeText(getActivity(), "این کالا موجود نمی باشد", Toast.LENGTH_SHORT).show();
                                customProgress.hideProgress();
                                return;
                            }
                            ArrayList<Product> resultProduct = new ArrayList<>();
                            resultProduct.addAll(Util.AllProduct);
                            CollectionUtils.filter(resultProduct, r -> r.getPRDUID().equals(GUID));

                            if (resultProduct.size() > 0) {
                                double amount = 0.0;
                                if (MinOrPlus)
                                    amount = Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() + 1;

                                else {
                                    if (Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() >= 1)
                                        amount = Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).getAmount() - 1;

                                    else
                                        return;

                                }


                                if (Integer.parseInt(response.body()) - amount < 0) {
                                    Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();
                                    customProgress.hideProgress();

                                    return;
                                }
                                Util.AllProduct.get(Util.AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);
                                //productAdapter.notifyItemChanged(productList.indexOf(resultProduct.get(0)));

                                ArrayList<ir.kitgroup.order.DataBase.InvoiceDetail> result = new ArrayList<>();
                                result.addAll(invoiceDetailList);
                                CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                                //edit
                                if (result.size() > 0) {
                                    Double sumprice = (amount * Float.parseFloat(Price));
                                    Double discountPrice = sumprice * discount;
                                    Double totalPrice = sumprice - discountPrice;
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_PERCENT_DISCOUNT = discount * 100;
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_DISCOUNT = String.valueOf(discountPrice);

                                    if (MainActivity.screenInches >= 7) {
                                        OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;
                                        OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                        OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_PERCENT_DISCOUNT = discount * 100;
                                        OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_DISCOUNT = String.valueOf(discountPrice);
                                        invoiceDetailAdapter.notifyItemChanged(invoiceDetailList.indexOf(result.get(0)));
                                    }/*else {
                                        MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;
                                        MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                        MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_PERCENT_DISCOUNT = discount * 100;
                                        MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_DISCOUNT = String.valueOf(discountPrice);
                                        invoiceDetailAdapter.notifyItemChanged(invoiceDetailList.indexOf(result.get(0)));
                                    }*/

                                } else {
                                    ir.kitgroup.order.DataBase.InvoiceDetail invoicedetail = new ir.kitgroup.order.DataBase.InvoiceDetail();
                                    invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                                    invoicedetail.INV_UID = factorGID;
                                    invoicedetail.INV_DET_QUANTITY = amount;
                                    invoicedetail.INV_DET_PRICE_PER_UNIT = Price;
                                    invoicedetail.INV_DET_DISCOUNT = "0";
                                    Double sumprice = (amount * Float.parseFloat(Price));
                                    Double discountPrice = sumprice * discount;
                                    Double totalPrice = sumprice - discountPrice;
                                    invoicedetail.INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                    invoicedetail.INV_DET_PERCENT_DISCOUNT = discount * 100;
                                    invoicedetail.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                                    invoicedetail.INV_DET_TAX = "0";
                                    invoicedetail.INV_DET_STATUS = true;
                                    invoicedetail.PRD_UID = GUID;
                                    invoicedetail.INV_DET_TAX_VALUE = "0";
                                    invoicedetail.INV_DET_DESCRIBTION = "";
                                    invoiceDetailList.add(invoicedetail);
                                    invoiceDetailAdapter.notifyDataSetChanged();

                                }

                                if (MainActivity.screenInches >= 7) {

                                    if (invoiceDetailList.size() > 0) {
                                        recyclerView.post(() -> recyclerView.smoothScrollToPosition(invoiceDetailList.size() - 1));

                                    }
                                }
                                if (MainActivity.screenInches >= 7)
                                    OrderFragment.productAdapter.notifyItemChanged(OrderFragment.productList.indexOf(resultProduct.get(0)));
                               /* else
                                    MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultProduct.get(0)));*/
                            }


                        } else {
                            ArrayList<ir.kitgroup.order.DataBase.InvoiceDetail> result = new ArrayList<>();
                            result.addAll(invoiceDetailList);
                            CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                            if (result.size() > 0) {
                                Double amount = 0.0;
                                if (!s.equals("")) {
                                    amount = Double.parseDouble(s);
                                    if (Integer.parseInt(response.body()) - amount < 0) {
                                        Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();
                                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = 0.0;
                                        if (MainActivity.screenInches >= 7)
                                            OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = "0";
                                        else
                                            OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = "0";

                                        invoiceDetailAdapter.notifyDataSetChanged();
                                        customProgress.hideProgress();
                                        return;
                                    }
                                }
                                invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;
                                if (MainActivity.screenInches >= 7)
                                    OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;
                                else
                                    OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;
                                Double sumprice = (amount * Float.parseFloat(Price));
                                Double discountPrice = sumprice * discount;
                                Double totalPrice = sumprice - discountPrice;


                                invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                if (MainActivity.screenInches >= 7)
                                    OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
//                                else
//                                    MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);

                                ArrayList<Product> resultPrd = new ArrayList<>();
                                resultPrd.addAll(Util.AllProduct);
                                //  CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID) && r.getPRDREMAIN()>0 && r.getPRDPRICEPERUNIT1()>0);
                                CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID));
                                if (resultPrd.size() > 0) {
                                    Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);
                                    if (MainActivity.screenInches >= 7) {
                                        if (OrderFragment.productList.indexOf(resultPrd.get(0)) >= 0) {
                                            OrderFragment.productAdapter.notifyItemChanged(OrderFragment.productList.indexOf(resultPrd.get(0)));
                                        }
                                    } else {
//                                        if (MainOrderMobileFragment.productList.indexOf(resultPrd.get(0)) >= 0) {
//                                            MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultPrd.get(0)));
//                                        }
                                    }
                                }

                            }
                        }
                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();


                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }


}
