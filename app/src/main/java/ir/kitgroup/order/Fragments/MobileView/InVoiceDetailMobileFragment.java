package ir.kitgroup.order.Fragments.MobileView;

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
import androidx.fragment.app.FragmentTransaction;
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
import java.util.Objects;


import ir.kitgroup.order.Activities.Classes.LauncherActivity;

import ir.kitgroup.order.Adapters.DescriptionAdapter;
import ir.kitgroup.order.Adapters.InvoiceDetailMobileAdapter;
import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.classes.CustomProgress;
import ir.kitgroup.order.Util.Utilities;

import ir.kitgroup.order.DataBase.Invoice;
import ir.kitgroup.order.DataBase.InvoiceDetail;
import ir.kitgroup.order.DataBase.OrderType;
import ir.kitgroup.order.DataBase.Product;
import ir.kitgroup.order.DataBase.Setting;
import ir.kitgroup.order.DataBase.Tables;
import ir.kitgroup.order.DataBase.User;


import ir.kitgroup.order.models.Description;
import ir.kitgroup.order.models.ModelDesc;
import ir.kitgroup.order.models.ModelInvoice;
import ir.kitgroup.order.models.ModelLog;
import ir.kitgroup.order.R;
import ir.kitgroup.order.Util.Util;
import ir.kitgroup.order.databinding.FragmentInvoiceDetailMobileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InVoiceDetailMobileFragment extends Fragment {

    //region Parameter
    private FragmentInvoiceDetailMobileBinding binding;
    private String maxSales;
    private String userName;
    private String passWord;
    private String Inv_GUID;

    private double sumPrice;
    private double sumPurePrice;

    private List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetailMobileAdapter invoiceDetailAdapter;

    private CustomProgress customProgress;

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");

    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;

    private DescriptionAdapter descriptionAdapter;
    private ArrayList<Description> descriptionList;
    private String GuidInv;

   private List<InvoiceDetail> invDetails;


    //endregion Variable Dialog Description


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


        customProgress = CustomProgress.getInstance();

        descriptionList = new ArrayList<>();
        invoiceDetailList=new ArrayList<>();
        List<Setting> setting = Select.from(Setting.class).list();
        if (setting.size() > 0)
            maxSales = setting.get(0).MAX_SALE;

        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;


        if (App.mode == 2) {
            binding.tvNameCustomer.setVisibility(View.GONE);
            binding.txtTableNumber.setVisibility(View.GONE);
            binding.txtTypeOrder.setVisibility(View.GONE);
        }


        Bundle bundle = getArguments();
        String type = bundle.getString("type");  //1 seen   //2 Edit
        Inv_GUID = bundle.getString("Inv_GUID");
        String Tbl_GUID = bundle.getString("Tbl_GUID");
        String Ord_TYPE = bundle.getString("Ord_TYPE");
        String Acc_NAME = bundle.getString("Acc_NAME");
        String Acc_GUID = bundle.getString("Acc_GUID");
        String status = bundle.getString("status");
        boolean edit = bundle.getBoolean("EDIT");


        sumPrice = 0;
        sumPurePrice = 0;

        if (status != null && status.equals("0"))
            binding.btnResend.setVisibility(View.VISIBLE);


        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

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
        });


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


        descriptionAdapter.setOnClickItemListener((desc, click,position) -> {
            if (click) {
                descriptionList.get(position).Click = true;
                edtDescriptionItem.setText(edtDescriptionItem.getText().toString() + "   " + "'" + desc + "'");
            } else {
                descriptionList.get(position).Click = false;
                if (edtDescriptionItem.getText().toString().contains("'" + desc + "'"))

                    edtDescriptionItem.setText(edtDescriptionItem.getText().toString().replace("   " + "'" + desc + "'", ""));
            }
        });


        btnRegisterDescription.setOnClickListener(v -> {
            ArrayList<Product> resultPrd = new ArrayList<>();
            InvoiceDetail invDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + GuidInv + "'").first();

            if (invDetail != null) {
                invDetail.INV_DET_DESCRIBTION = edtDescriptionItem.getText().toString();
                invDetail.update();
                if (LauncherActivity.screenInches < 7) {
                    resultPrd.addAll(Util.AllProduct);
                    CollectionUtils.filter(resultPrd, r -> r.I.equals(invDetail.PRD_UID));
                }

            }

            if (resultPrd.size() > 0) {
                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).descItem = edtDescriptionItem.getText().toString();
            }


            invoiceDetailAdapter.notifyDataSetChanged();
            dialogDescription.dismiss();
        });
        //endregion Cast DialogDescription


        //region Set Parameter Toolbar
        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");
        //seen
        if (type.equals("1")) {

            binding.layoutContinue.setVisibility(View.GONE);
            binding.layoutEditDelete.setVisibility(View.VISIBLE);

            Invoice invoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
            if (invoice != null) {
                Tables tbl1 = Select.from(Tables.class).where("I ='" + invoice.TBL_UID + "'").first();

                if (tbl1 != null)
                    binding.txtTableNumber.setText("شماره میز : " + tbl1.N);


                OrderType orderType = Select.from(OrderType.class).where("c ='" + invoice.INV_TYPE_ORDER + "'").first();
                if (orderType != null) {
                    binding.txtTypeOrder.setText(orderType.getN());
                }


                if (App.mode == 1)
                    binding.tvNameCustomer.setText(invoice.Acc_name);
                if (invoice.INV_DUE_DATE!=null) {
                    Utilities.SolarCalendar sc = util.new SolarCalendar(invoice.INV_DUE_DATE);
                    binding.txtDate.setText(invoice.INV_DUE_DATE.getHours() + ":" + invoice.INV_DUE_DATE.getMinutes() + " " + (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year);

                }else {
                    if (invoice.INV_DUE_DATE1!=null)
                        binding.txtDate.setText(invoice.INV_DUE_DATE1);
                }

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


            if (App.mode == 1)
                binding.tvNameCustomer.setText(Acc_NAME);

        }
        //endregion Set Parameter Toolbar


        //region CONFIGURATION DATA INVOICE_DETAIL


        invoiceDetailList.clear();
        invoiceDetailList.addAll(invDetails);
        if (type.equals("1")) {
            getInVoice(userName, passWord, Inv_GUID);
        }

        for (int i = 0; i <invDetails.size(); i++) {

            ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
            int finalI = i;
            CollectionUtils.filter(prdResult, p->p.I.equals(invDetails.get(finalI).PRD_UID));

            if (prdResult.size()>0){
                Double sumprice = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                Double discountPrice = sumprice * (prdResult.get(0).PERC_DIS/100);
                Double totalPrice = sumprice - discountPrice;

                sumPurePrice =sumPurePrice + (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                sumPrice =sumPrice +totalPrice;

            }

        }




        binding.sumPriceTxt.setText(format.format(sumPrice) + " ریال ");
        binding.purePriceTxt.setText(format.format(sumPurePrice) + " ریال ");


        invoiceDetailAdapter = new InvoiceDetailMobileAdapter(invoiceDetailList, type, Inv_GUID);
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setHasFixedSize(false);
        binding.recyclerDetailInvoice.setAdapter(invoiceDetailAdapter);
        binding.recyclerDetailInvoice.setNestedScrollingEnabled(false);


        invoiceDetailAdapter.editAmountItemListener((Prd_GUID, s, Price, discountPercent) -> {

            if (maxSales.equals("1")) {
                getMaxSales(userName, passWord, Prd_GUID, s);
            } else {
                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));
                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    double amount = 0.0;
                    if (!s.equals(""))
                        amount = Double.parseDouble(s);

                    if (invoiceDetail != null) {
                        invoiceDetail.INV_DET_QUANTITY = amount;
                        invoiceDetail.update();
                    }


                    ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                    CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(Prd_GUID));
                    if (resultPrd.size() > 0) {
                        Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);

                    }
                    sumPrice = 0;
                    sumPurePrice = 0;



              List<InvoiceDetail> invoiceDetails= Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                    for (int i = 0; i <invoiceDetails.size(); i++) {

                        ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                        int finalI = i;
                        CollectionUtils.filter(prdResult, p->p.I.equals(invoiceDetails.get(finalI).PRD_UID));

                        if (prdResult.size()>0){
                            Double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                            Double discountPrice = sumprice * (prdResult.get(0).PERC_DIS/100);
                            Double totalPrice = sumprice - discountPrice;

                            sumPurePrice =sumPurePrice + (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                            sumPrice =sumPrice +totalPrice;

                        }

                    }

                    binding.sumPriceTxt.setText(format.format(sumPrice) + " ریال ");
                    binding.purePriceTxt.setText(format.format(sumPurePrice) + " ریال ");
                    invoiceDetailAdapter.notifyDataSetChanged();

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

                    }

                }


                binding.sumPriceTxt.setText(format.format(sumPrice) + " ریال ");
                binding.purePriceTxt.setText(format.format(sumPurePrice) + " ریال ");


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



        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeleteInvoice(userName,passWord,Inv_GUID);
            }
        });


        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invoiceDetailList.size() == 0) {
                    Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putString("Ord_TYPE", "");
                bundle.putString("Tbl_GUID", "");
                bundle.putString("Inv_GUID", Inv_GUID);
                MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                mainOrderMobileFragment.setArguments(bundle);
                FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment,"MainOrderMobileFragment")).addToBackStack("MainOrderMobileFX");
                replaceFragment.commit();
            }
        });
        binding.btnContinue.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("Inv_GUID", Inv_GUID);
            bundle1.putString("Tbl_GUID", Tbl_GUID);
            bundle1.putString("Acc_NAME", Acc_NAME);
            bundle1.putString("Acc_GUID", Acc_GUID);


            if (App.mode == 2)
                bundle1.putString("Ord_TYPE", "");
            else
                bundle1.putString("Ord_TYPE", Ord_TYPE);

            if (edit)
                bundle1.putBoolean("EDIT", true);
            bundle1.putString("Sum_PURE_PRICE", String.valueOf(sumPurePrice));
            bundle1.putString("Sum_PRICE", String.valueOf(sumPrice));
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

    private void getMaxSales(String userName, String pass, String Prd_GUID, String s) {


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


    }

    private void getInVoice(String userName, String pass, String Inv_GUID) {

        customProgress.showProgress(getActivity(), "در حال دریافت تغییرات فاکتور...", false);

        try {


            Call<String> call = App.api.getInvoice(userName, pass, Inv_GUID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {


                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelInvoice>() {
                    }.getType();
                    ModelInvoice iDs = null;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception ignored) {
                    }

                    if (iDs != null) {
                       Invoice inv=Select.from(Invoice.class).where("INVUID ='"+Inv_GUID+"'").first();
                        if (inv!=null){
                            inv.delete();
                            Invoice.saveInTx(iDs.getInvoice());
                        }
                        List<InvoiceDetail> invoiceDetail=Select.from(InvoiceDetail.class).where("INVUID ='"+Inv_GUID+"'").list();
                        for (int i=0;i<invoiceDetail.size();i++){
                            InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                        }


                        InvoiceDetail.saveInTx(iDs.getInvoiceDetail());



                        invoiceDetailList.clear();
                        invoiceDetailList.addAll(iDs.getInvoiceDetail());
                        invoiceDetailAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                    }
                    customProgress.hideProgress();

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }
    private void getDeleteInvoice(String userName, String pass, String Inv_GUID) {

        customProgress.showProgress(getActivity(), "در حال دریافت تغییرات فاکتور...", false);

        try {


            Call<String> call = App.api.getDeleteInvoice(userName, pass, Inv_GUID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                   if (iDs!=null){
                       int message = iDs.getLogs().get(0).getMessage();
                       String description = iDs.getLogs().get(0).getDescription();
                       if (message == 4) {


                           Invoice inv=Select.from(Invoice.class).where("INVUID ='"+Inv_GUID+"'").first();
                           if (inv!=null)
                               inv.delete();
                           List<InvoiceDetail> invoiceDetail=Select.from(InvoiceDetail.class).where("INVUID ='"+Inv_GUID+"'").list();
                           for (int i=0;i<invoiceDetail.size();i++){
                               InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                           }

                           assert getFragmentManager() != null;
                           getFragmentManager().popBackStack();
                           Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("OrderListFragment");
                           FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                           assert frg != null;
                           ft.detach(frg);
                           ft.attach(frg);
                           ft.commit();
                           Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                       } else {
                       }
                   }else {
                       Toast.makeText(getActivity(), "خطایی در سرور رخ داد", Toast.LENGTH_SHORT).show();
                   }


                    customProgress.hideProgress();

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }
}
