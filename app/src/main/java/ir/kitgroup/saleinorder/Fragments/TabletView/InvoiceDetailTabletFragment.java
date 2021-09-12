package ir.kitgroup.saleinorder.Fragments.TabletView;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ir.kitgroup.saleinorder.Adapters.InvoiceDetailTabletAdapter;
import ir.kitgroup.saleinorder.DataBase.InvoiceDetail;
import ir.kitgroup.saleinorder.DataBase.Setting;
import ir.kitgroup.saleinorder.DataBase.User;
import ir.kitgroup.saleinorder.classes.CustomProgress;
import ir.kitgroup.saleinorder.Util.Utilities;
import ir.kitgroup.saleinorder.DataBase.Invoice;
import ir.kitgroup.saleinorder.DataBase.OrderType;
import ir.kitgroup.saleinorder.DataBase.Product;

import ir.kitgroup.saleinorder.databinding.FragmentInvoiceDetailBinding;
import ir.kitgroup.saleinorder.Util.Util;

public class InvoiceDetailTabletFragment extends Fragment {


    private FragmentInvoiceDetailBinding binding;


    private CustomProgress customProgress;

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");



    private String maxSales = "0";


    private String userName;
    private String passWord;


    private ArrayList<InvoiceDetail> invoiceDetailList = new ArrayList<>();
    private double sumPrice = 0;
    private double sumPurePrice = 0;
    private double sumDiscount = 0;


    private String Inv_GUID = "";

    private List<InvoiceDetail> invDetails;



    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentInvoiceDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        customProgress = CustomProgress.getInstance();

        invoiceDetailList=new ArrayList<>();
        List<Setting> setting = Select.from(Setting.class).list();
        if (setting.size() > 0)
            maxSales = setting.get(0).MAX_SALE;

        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;






        Bundle bundle = getArguments();
        String type = bundle.getString("type");  //1 seen   //2 Edit
        Inv_GUID = bundle.getString("Inv_GUID");
        String Ord_TYPE = bundle.getString("Ord_TYPE");
        String Tbl_GUID = bundle.getString("Tbl_GUID");
        String Acc_NAME = bundle.getString("Acc_NAME");
        String Acc_GUID = bundle.getString("Acc_GUID");
        String status = bundle.getString("status");
        boolean edit = bundle.getBoolean("EDIT");



        sumPrice = 0;
        sumPurePrice = 0;

       /* if (status != null && status.equals("0"))
            binding.btnResend.setVisibility(View.VISIBLE);
        binding.btnResend.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("Inv_GUID", Inv_GUID);
            bundle1.putString("Tbl_GUID", "");
            bundle1.putString("Acc_NAME", Acc_NAME);
            bundle1.putString("Acc_GUID", Acc_GUID);
            bundle1.putString("Ord_TYPE", "");
            if (edit)
                bundle1.putBoolean("EDIT", true);

            bundle1.putString("Sum_PURE_PRICE", String.valueOf(sumPurePrice));
            bundle1.putString("Sum_PRICE", String.valueOf(sumPrice));

            PaymentMobileFragment paymentFragment = new PaymentMobileFragment();
            paymentFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, paymentFragment, "PaymentFragment").addToBackStack("PaymentF").commit();
        });*/




        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();



            if (type.equals("1")) {

                Invoice invoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
                if (invoice != null) {
                    String text1 = "نام مشترک : " + invoice.Acc_name;
                    SpannableString ss1 = new SpannableString(text1);
                    StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
                    ss1.setSpan(boldSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvNameCustomer.setText(ss1);


                    Utilities util = new Utilities();
                    Locale loc = new Locale("en_US");
                    Utilities.SolarCalendar sc = util.new SolarCalendar(invoice.INV_DUE_DATE);
                    String text2 = "تاریخ ایجاد : " + (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + ((sc.strMonth)) + "\t" + (sc.year);
                    SpannableString ss2 = new SpannableString(text2);
                    StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
                    ss2.setSpan(boldSpan2, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvDate.setText(ss2);


                    OrderType orderType = Select.from(OrderType.class).where("c ='" + invoice.INV_TYPE_ORDER + "'").first();
                    String text3 = "";
                    if (orderType != null) {
                        text3 = "نوع سفارش : " + orderType.getN();
                    }

                    SpannableString ss3 = new SpannableString(text3);
                    StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
                    ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvTypeOrder.setText(ss3);
                }


            }
            else {
                String text1 = "نام مشترک : ";
                String nameCustomer = bundle.getString("Acc_NAME");
                if (!nameCustomer.equals("نام مشترک"))
                    text1 = "نام مشترک : " + nameCustomer;
                SpannableString ss1 = new SpannableString(text1);
                StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
                ss1.setSpan(boldSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvNameCustomer.setText(ss1);


                Calendar calendar = Calendar.getInstance();
                Utilities util = new Utilities();
                Locale loc = new Locale("en_US");
                Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
                String text2 = "تاریخ ایجاد : " + (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (String.valueOf(sc.strMonth)) + "\t" + String.valueOf(sc.year);
                SpannableString ss2 = new SpannableString(text2);
                StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
                ss2.setSpan(boldSpan2, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvDate.setText(ss2);


                String orderType = bundle.getString("Ord_TYPE");
                OrderType ordTY = Select.from(OrderType.class).where("c ='" + orderType + "'").first();
                String text3 = "نوع سفارش : " + " ";

                if (ordTY != null)
                    text3 = "نوع سفارش : " + ordTY.getN();

                SpannableString ss3 = new SpannableString(text3);
                StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
                ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvTypeOrder.setText(ss3);


            }







        //region CONFIGURATION DATA INVOICE_DETAIL
        invoiceDetailList.clear();
        invoiceDetailList.addAll(invDetails);
        /*if (type.equals("1")) {
            getInVoice(userName, passWord, Inv_GUID);
        }*/

        for (int i = 0; i <invDetails.size(); i++) {

            ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
            int finalI = i;
            CollectionUtils.filter(prdResult, p->p.I.equals(invDetails.get(finalI).PRD_UID));

            if (prdResult.size()>0){
               double totalPrice = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
               double discountPrice = totalPrice * (prdResult.get(0).PERC_DIS/100);
               double purePrice = totalPrice - discountPrice;

                sumPrice =sumPrice + (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                sumPurePrice =sumPurePrice + purePrice;
                sumDiscount =sumDiscount  +discountPrice;

            }

        }




        binding.launcherOrderSumPriceTxt.setText(format.format(sumPrice) + " ریال ");
        binding.launcherOrderTotalPriceTxt.setText(format.format(sumPurePrice) + " ریال ");
        binding.launcherOrderDiscountTxt.setText(format.format(sumDiscount) + " ریال ");





        InvoiceDetailTabletAdapter invoiceDetailLargeAdapter = new InvoiceDetailTabletAdapter(getContext(), invoiceDetailList, type);
        binding.recyclerInvoiceDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerInvoiceDetail.setAdapter(invoiceDetailLargeAdapter);
        binding.recyclerInvoiceDetail.setHasFixedSize(false);




        if (invoiceDetailList.size()>0)
            binding.recyclerInvoiceDetail.post(() -> binding.recyclerInvoiceDetail.smoothScrollToPosition(invoiceDetailList.size() - 1));


        invoiceDetailLargeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);


                sumPrice = 0;
                sumPurePrice = 0;
                sumDiscount = 0;


                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoiceDetailList.get(positionStart).PRD_UID));
                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    if (invoiceDetail != null)
                        invoiceDetail.delete();
                }

                invoiceDetailList.remove(invoiceDetailList.get(positionStart));



                for (int i = 0; i <invoiceDetailList.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p->p.I.equals(invoiceDetailList.get(finalI).PRD_UID));

                    if (prdResult.size()>0){
                        double sumprice = (invoiceDetailList.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        double discountPrice = sumprice * (prdResult.get(0).PERC_DIS/100);
                        double totalPrice = sumprice - discountPrice;

                        sumPurePrice =sumPurePrice + (invoiceDetailList.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        sumPrice =sumPrice +totalPrice;
                        sumDiscount =sumDiscount +discountPrice;

                    }

                }



                binding.launcherOrderSumPriceTxt.setText(format.format(sumPrice) + " ریال ");
                binding.launcherOrderTotalPriceTxt.setText(format.format(sumPurePrice) + " ریال ");
                binding.launcherOrderDiscountTxt.setText(format.format(sumDiscount) + " ریال ");




            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);


            }
        });


/*        invoiceDetailLargeAdapter.SetOnEditItem(() -> {

            double sumPrice = 0;
            double sumPurePrice = 0;
            double sumDiscountPrice = 0;


            for (int i = 0; i < invoiceDetailList.size(); i++) {
                sumPrice = (float) (sumPrice + invoiceDetailList.get(i).INV_DET_QUANTITY
                        * Float.parseFloat(invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT));
                sumDiscountPrice = sumDiscountPrice + Double.parseDouble(invoiceDetailList.get(i).INV_DET_DISCOUNT);
                sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
            }


            String text1 = "جمع فاکتور : " + format.format(sumPrice) + " ریال ";
            SpannableString ss1 = new SpannableString(text1);
            StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
            ss1.setSpan(boldSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvSumPriceOrder.setText(ss1);


            String text2 = "جمع تخفیف : " + format.format(sumDiscountPrice) + " ریال ";
            SpannableString ss2 = new SpannableString(text2);
            StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
            ss2.setSpan(boldSpan2, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvDiscountPercentOrder.setText(ss2);


            String text3 = "جمع خالص : " + format.format(sumPurePrice) + " ریال ";
            SpannableString ss3 = new SpannableString(text3);
            StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
            ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvSumPurePriceOrder.setText(ss3);


        });*/






        //endregion CONFIGURATION DATA INVOICE_DETAIL








//        invoiceDetailAdapter.editAmountItemListener((Prd_GUID, s, Price, discountPercent) -> {
        //
//
//            if (maxSales.equals("1")) {
//                getMaxSales(userName, passWord, Prd_GUID, s);
//            } else {
//                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
//                CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));
//                if (result.size() > 0) {
//                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
//                    double amount = 0.0;
//                    if (!s.equals(""))
//                        amount = Double.parseDouble(s);
//
//                    if (invoiceDetail != null) {
//                        invoiceDetail.INV_DET_QUANTITY = amount;
//                        invoiceDetail.update();
//                    }
//
//
//                    ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
//                    CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(Prd_GUID));
//                    if (resultPrd.size() > 0) {
//                        Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);
//
//                    }
//                    sumPrice = 0;
//                    sumPurePrice = 0;
//                    sumDiscount= 0;
//
//
//
//                    List<InvoiceDetail> invoiceDetails= Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
//                    for (int i = 0; i <invoiceDetails.size(); i++) {
//
//                        ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
//                        int finalI = i;
//                        CollectionUtils.filter(prdResult, p->p.I.equals(invoiceDetails.get(finalI).PRD_UID));
//
//                        if (prdResult.size()>0){
//                            double totalPrice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
//                            double discountPrice = totalPrice * (prdResult.get(0).PERC_DIS/100);
//                            double purePrice = totalPrice - discountPrice;
//
//                            sumPurePrice =sumPurePrice + (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
//                            sumPrice =sumPrice + purePrice;
//                            sumDiscount =sumDiscount + discountPrice;
//
//                        }
//
//                    }
//
//                    binding.launcherOrderSumPriceTxt.setText(format.format(sumPrice) + " ریال ");
//                    binding.launcherOrderTotalPriceTxt.setText(format.format(sumPurePrice) + " ریال ");
//                    binding.launcherOrderDiscountTxt.setText(format.format(sumDiscount) + " ریال ");
//                    invoiceDetailAdapter.notifyDataSetChanged();
//
//                }
//            }
//
//
//        });













        //    ShowData(invoiceDetailLargeList);



        return view;

    }

    @Override
    public void onDestroy() {

       /* if (type.equals("2")) {
         *//*   if (LauncherActivity.screenInches >= 7) {
                OrderFragment.invoiceDetailAdapter.notifyDataSetChanged();
                OrderFragment.descriptionFactor = edtDescription.getText().toString();
            }*//*


        }
*/
        super.onDestroy();
    }


    public void ShowData(ArrayList<ir.kitgroup.saleinorder.DataBase.InvoiceDetail> list) {


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
        binding.tvSumPriceOrder.setText(ss1);


        String text2 = "جمع تخفیف : " + format.format(sumDiscount);
        SpannableString ss2 = new SpannableString(text2);
        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        ss2.setSpan(boldSpan2, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvDiscountPriceOrder.setText(ss2);


        String text3 = "جمع خالص : " + format.format(sumPurePrice);
        SpannableString ss3 = new SpannableString(text3);
        StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
        ss3.setSpan(boldSpan3, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvSumPurePriceOrder.setText(ss3);


    }



/*    private void getMaxSales(String userName, String pass, String Prd_GUID, String s) {


        try {

            Call<String> call = App.api.getMaxSales(userName, pass, Prd_GUID);

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
                        if (message != 1) {
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (remain != -1000000000) {


                        ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                        ArrayList<Product> resultPrd1 = new ArrayList<>(Util.AllProduct);

                        CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));
                        CollectionUtils.filter(resultPrd1, r -> r.I.equals(Prd_GUID));
                        if (result.size() > 0) {
                            InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                            double amount = 0.0;
                            if (!s.equals("")) {
                                amount = Double.parseDouble(s);
                                if (remain - amount < 0) {
                                    Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();

                                    if (invoiceDetail != null) {
                                        invoiceDetail.INV_DET_QUANTITY = 0.0;
                                        invoiceDetail.update();
                                        if (resultPrd1.size()>0) {
                                            resultPrd1.get(0).AMOUNT = 0.0;
                                        }
                                    }

                                    invoiceDetailAdapter.notifyDataSetChanged();
                                    customProgress.hideProgress();
                                    return;
                                }
                            }

                            if (invoiceDetail != null) {
                                invoiceDetail.INV_DET_QUANTITY = amount;
                                invoiceDetail.update();

                            }

                            ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                            CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(Prd_GUID));
                            if (resultPrd.size() > 0) {
                                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);

                            }


                            invoiceDetailAdapter.notifyDataSetChanged();

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


    }*/


}
