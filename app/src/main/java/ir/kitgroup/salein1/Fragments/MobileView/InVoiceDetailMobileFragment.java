package ir.kitgroup.salein1.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import ir.kitgroup.salein1.Activities.Classes.LauncherActivity;

import ir.kitgroup.salein1.Adapters.DescriptionAdapter;
import ir.kitgroup.salein1.Adapters.InvoiceDetailAdapter;
import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.Classes.CustomProgress;
import ir.kitgroup.salein1.Classes.Utilities;

import ir.kitgroup.salein1.DataBase.Invoice;
import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.OrderType;
import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.DataBase.Setting;
import ir.kitgroup.salein1.DataBase.Tables;
import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.TabletView.OrderFragment;

import ir.kitgroup.salein1.Models.Description;

import ir.kitgroup.salein1.Models.ModelDesc;
import ir.kitgroup.salein1.Models.ModelLog;
import ir.kitgroup.salein1.R;
import ir.kitgroup.salein1.Util.Util;
import ir.kitgroup.salein1.databinding.FragmentInvoiceDetailMobileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InVoiceDetailMobileFragment extends Fragment {

    //region Parameter
    private FragmentInvoiceDetailMobileBinding binding;
    private String maxSales = "0";
    private String userName;
    private String passWord;
    private String Inv_GUID;

    private double sumPrice;
    private double sumPurePrice;

    public static ArrayList<Invoicedetail> invoiceDetailList = new ArrayList<>();
    public InvoiceDetailAdapter invoiceDetailAdapter;

    private CustomProgress customProgress;

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");

    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    public static ArrayList<Description> descriptionList = new ArrayList<>();
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv = "";
    //endregion Variable Dialog Description


    private int type;//1 edit && continue    //2 seen

    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentInvoiceDetailMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


     /*   Account acc = Select.from(Account.class).where("I ='" + ord.ACC_CLB_UID + "'").first();
        binding.orderNameCustomer.removeTextChangedListener(textWatcherAcc);
        binding.orderNameCustomer.setText(acc.getACCCLBNAME());
        binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);
        Acc_GUID = acc.getACCCLBUID();
        binding.orderNameCustomer.setEnabled(false);*/


        Bundle bundle = getArguments();
        String type = bundle.getString("type");  //1 seen   //2 Edit
        Inv_GUID = bundle.getString("Inv_GUID");
        String Tbl_GUID = bundle.getString("Tbl_GUID");
        String Ord_TYPE = bundle.getString("Ord_TYPE");
        String Acc_NAME = bundle.getString("Acc_NAME");
        String Acc_GUID = bundle.getString("Acc_GUID");
        String status = bundle.getString("status");

        if (status != null && status.equals("0"))
            binding.btnResend.setVisibility(View.VISIBLE);


        binding.btnResend.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("Inv_GUID", Inv_GUID);
            bundle1.putString("Tbl_GUID", Tbl_GUID);


            bundle1.putString("Acc_NAME", Acc_NAME);
            bundle1.putString("Acc_GUID", Acc_GUID);

            bundle1.putString("DESC", binding.edtDescription.getText().toString());

            bundle1.putString("Ord_TYPE", "");

            bundle1.putString("Sum_PRICE", String.valueOf(sumPurePrice));
            PaymentMobileFragment paymentFragment = new PaymentMobileFragment();
            paymentFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, paymentFragment, "PaymentFragment").addToBackStack("PaymentF").commit();
        });


        customProgress = CustomProgress.getInstance();


        if (App.mode == 2) {
            binding.tvNameCustomer.setVisibility(View.GONE);
            binding.txtTableNumber.setVisibility(View.GONE);
            binding.txtTypeOrder.setVisibility(View.GONE);
        }
        List<Setting> setting = Select.from(Setting.class).list();
        if (setting.size() > 0)
            maxSales = setting.get(0).MAX_SALE;


        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;


        //region Cast DialogDescription
        dialogDescription = new Dialog(getActivity());
        dialogDescription.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDescription.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDescription.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialogDescription.setContentView(R.layout.dialog_description);
        dialogDescription.setCancelable(false);
        edtDescriptionItem = dialogDescription.findViewById(R.id.edt_description);
        MaterialButton btnRegisterDescription = dialogDescription.findViewById(R.id.btn_register_description);
        RecyclerView recyclerDescription = dialogDescription.findViewById(R.id.recyclerView_description);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 3) {
            @Override
            protected boolean isLayoutRTL() {
                return true;
            }
        };
        recyclerDescription.setLayoutManager(gridLayoutManager1);
        descriptionAdapter = new DescriptionAdapter(getActivity(), descriptionList);
        recyclerDescription.setAdapter(descriptionAdapter);


        descriptionAdapter.setOnClickItemListener((desc, click) -> {
            if (click) {

                edtDescriptionItem.setText(edtDescriptionItem.getText().toString() + "   " + "'" + desc + "'");
            } else {
                if (edtDescriptionItem.getText().toString().contains("'" + desc + "'"))

                    edtDescriptionItem.setText(edtDescriptionItem.getText().toString().replace("   " + "'" + desc + "'", ""));

            }


        });


        btnRegisterDescription.setOnClickListener(v -> {
            ArrayList<Product> resultPrd = new ArrayList<>();
            if (LauncherActivity.screenInches < 7) {
                resultPrd.addAll(Util.AllProduct);
            }
            ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
            CollectionUtils.filter(result, r -> r.INV_DET_UID.equals(GuidInv));
            if (result.size() > 0) {
                invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_DESCRIBTION = edtDescriptionItem.getText().toString();

                CollectionUtils.filter(resultPrd, r -> r.I.equals(result.get(0).PRD_UID));
            }
            invoiceDetailAdapter.notifyDataSetChanged();


            if (resultPrd.size() > 0) {
                if (resultPrd.get(0).descItem == null)
                    resultPrd.get(0).descItem = edtDescriptionItem.getText().toString();
                else
                    resultPrd.get(0).descItem = edtDescriptionItem.getText().toString() + " " + resultPrd.get(0).descItem;


                MainOrderMobileFragment.productAdapter.notifyDataSetChanged();
            }
            dialogDescription.dismiss();
        });
        //endregion Cast DialogDescription

        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");
        //seen
        if (type.equals("1")) {

            binding.layoutContinue.setVisibility(View.GONE);
            binding.layoutEditDelete.setVisibility(View.VISIBLE);
            Invoice invoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
            if (invoice != null) {
                binding.edtDescription.setText(invoice.INV_DESCRIBTION);


                Tables tbl1 = Select.from(Tables.class).where("I ='" + invoice.TBL_UID + "'").first();
                if (tbl1 != null)
                    binding.txtTableNumber.setText("شماره میز : " + tbl1.N);


                Utilities.SolarCalendar sc = util.new SolarCalendar(invoice.INV_DUE_DATE);
                binding.txtDate.setText((sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year);


                OrderType orderType = Select.from(OrderType.class).where("c ='" + invoice.INV_TYPE_ORDER + "'").first();
                if (orderType != null) {
                    binding.txtTypeOrder.setText(orderType.getN());
                }


                if (App.mode == 1)
                    binding.tvNameCustomer.setText(invoice.Acc_name);


            }


        }


        //edit
        else {
            Tables tbl1 = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
            if (tbl1 != null)
                binding.txtTableNumber.setText("شماره میز : " + tbl1.N);


            Calendar calendar = Calendar.getInstance();
            Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
            String text2 = (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year;
            binding.txtDate.setText(text2);


            OrderType ordTY = Select.from(OrderType.class).where("c ='" + Ord_TYPE + "'").first();

            if (ordTY != null)
                binding.txtTypeOrder.setText(ordTY.getN());


        }


        //region CONFIGURATION DATA INVOICE_DETAIL


        invoiceDetailList.clear();
        if (type.equals("1"))
            invoiceDetailList.addAll(Select.from(Invoicedetail.class).where("INVUID ='" + Inv_GUID + "'").list());

        else
            invoiceDetailList.addAll(MainOrderMobileFragment.invoiceDetailList);


        sumPrice = 0;
        sumPurePrice = 0;


        for (int i = 0; i < invoiceDetailList.size(); i++) {
            sumPrice = sumPrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_QUANTITY)
                    * Float.parseFloat(invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT);
            sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
        }

        binding.sumPriceTxt.setText(format.format(sumPrice) + " ریال ");
        binding.purePriceTxt.setText(format.format(sumPurePrice) + " ریال ");


        invoiceDetailAdapter = new InvoiceDetailAdapter(invoiceDetailList, type);


        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setHasFixedSize(false);
        binding.recyclerDetailInvoice.setAdapter(invoiceDetailAdapter);
        binding.recyclerDetailInvoice.setNestedScrollingEnabled(false);


        invoiceDetailAdapter.editAmountItemListener((GUID, s, Price, discountPercent) -> {
            if (!s.equals("")) {
                if (maxSales.equals("1")) {
                    getMaxSales(userName, passWord, GUID, Price, discountPercent, GUID, s);
                } else {
                    ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
                    CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                    if (result.size() > 0) {
                        double amount = 0.0;
                        if (!s.equals("")) {
                            amount = Double.parseDouble(s);

                        }
                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);

                        MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);

                        double sumprice = (amount * Float.parseFloat(Price));
                        double discountPrice = sumprice * discountPercent;
                        double totalPrice = sumprice - discountPrice;


                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);

                        MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);

                        ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                        CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID));
                        if (resultPrd.size() > 0) {
                            Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);
                            if (MainOrderMobileFragment.productList.contains(resultPrd.get(0))) {
                                MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultPrd.get(0)));
                            }

                        }
                        sumPrice = 0;
                        sumPurePrice = 0;


                        for (int i = 0; i < invoiceDetailList.size(); i++) {
                            sumPrice = sumPrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_QUANTITY)
                                    * Float.parseFloat(invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT);
                            sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
                        }

                        binding.sumPriceTxt.setText(format.format(sumPrice) + " ریال ");
                        binding.purePriceTxt.setText(format.format(sumPurePrice) + " ریال ");
                    }
                }
            } else {
                ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                if (result.size() > 0) {
                    double amount = 0.0;
                    if (!s.equals("")) {
                        amount = Double.parseDouble(s);

                    }

                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);

                    MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);
                    double sumprice = (amount * Float.parseFloat(Price));
                    double discountPrice = sumprice * discountPercent;
                    double totalPrice = sumprice - discountPrice;


                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);

                    MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);

                    ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                    CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID));
                    if (resultPrd.size() > 0) {
                        Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);

                        if (MainOrderMobileFragment.productList.contains(resultPrd.get(0))) {
                            MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultPrd.get(0)));
                        }


                    }
                    sumPrice = 0;
                    sumPurePrice = 0;


                    for (int i = 0; i < invoiceDetailList.size(); i++) {
                        sumPrice = sumPrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_QUANTITY)
                                * Float.parseFloat(invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT);
                        sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
                    }

                    binding.sumPriceTxt.setText(format.format(sumPrice) + " ریال ");
                    binding.purePriceTxt.setText(format.format(sumPurePrice) + " ریال ");
                    customProgress.hideProgress();

                }
            }

        });


        invoiceDetailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();


            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                sumPrice = 0;
                sumPurePrice = 0;


                MainOrderMobileFragment.invoiceDetailList.remove(invoiceDetailList.get(positionStart));

                invoiceDetailList.remove(invoiceDetailList.get(positionStart));


                for (int i = 0; i < invoiceDetailList.size(); i++) {
                    sumPrice = sumPrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_QUANTITY)
                            * Float.parseFloat(invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT);
                    sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
                }


                binding.sumPriceTxt.setText(format.format(sumPrice) + " ریال ");
                binding.purePriceTxt.setText(format.format(sumPurePrice) + " ریال ");
                //tvSumPriceHeader.setText(format.format(sumPurePrice) + " ریال ");


            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);


            }
        });


        invoiceDetailAdapter.onDescriptionItem((GUIDPrd, GUIDInv, description) -> {

            edtDescriptionItem.setText(description);
            descriptionList.clear();
            GuidInv = GUIDInv;
            getDescription(userName, passWord, GUIDPrd);
        });


        edtDescriptionItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    for (int i = 0; i < descriptionList.size(); i++) {
                        descriptionList.get(i).Click = false;
                    }
                    descriptionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //endregion CONFIGURATION DATA INVOICE_DETAIL


        binding.btnContinue.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("Inv_GUID", Inv_GUID);
            bundle1.putString("Tbl_GUID", Tbl_GUID);


            bundle1.putString("Acc_NAME", Acc_NAME);
            bundle1.putString("Acc_GUID", Acc_GUID);

            bundle1.putString("DESC", binding.edtDescription.getText().toString());
            if (App.mode == 2)
                bundle1.putString("Ord_TYPE", "");
            else
                bundle1.putString("Ord_TYPE", Ord_TYPE);
            bundle1.putString("Sum_PRICE", String.valueOf(sumPurePrice));
            PaymentMobileFragment paymentFragment = new PaymentMobileFragment();
            paymentFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, paymentFragment, "PaymentFragment").addToBackStack("PaymentF").commit();
        });

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
                    } catch (Exception ignored) {

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
            discount, String GUID, String s) {


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
                        if (message != 1) {
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (remain != -1000000000) {

                        ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
                        CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                        if (result.size() > 0) {
                            double amount = 0.0;
                            if (!s.equals("")) {
                                amount = Double.parseDouble(s);
                                if (Integer.parseInt(response.body()) - amount < 0) {
                                    Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = "0";

                                    OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = "0";

                                    invoiceDetailAdapter.notifyDataSetChanged();
                                    customProgress.hideProgress();
                                    return;
                                }
                            }
                            invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);

                            OrderFragment.invoiceDetailList.get(OrderFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);
                            Double sumprice = (amount * Float.parseFloat(Price));
                            Double discountPrice = sumprice * discount;
                            Double totalPrice = sumprice - discountPrice;


                            invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);

                            MainOrderMobileFragment.invoiceDetailList.get(MainOrderMobileFragment.invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);

                            ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                            //  CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID) && r.getPRDREMAIN()>0 && r.getPRDPRICEPERUNIT1()>0);
                            CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID));
                            if (resultPrd.size() > 0) {
                                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);

                                if (MainOrderMobileFragment.productList.contains(resultPrd.get(0))) {
                                    MainOrderMobileFragment.productAdapter.notifyItemChanged(MainOrderMobileFragment.productList.indexOf(resultPrd.get(0)));
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
