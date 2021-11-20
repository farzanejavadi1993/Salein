package ir.kitgroup.saleinmeat.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
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

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import ir.kitgroup.saleinmeat.Activities.LauncherActivity;
import ir.kitgroup.saleinmeat.Adapters.DescriptionAdapter;
import ir.kitgroup.saleinmeat.Adapters.InvoiceDetailMobileAdapter;
import ir.kitgroup.saleinmeat.Connect.API;
import ir.kitgroup.saleinmeat.DataBase.Account;

import ir.kitgroup.saleinmeat.DataBase.Tables;

import ir.kitgroup.saleinmeat.classes.ConfigRetrofit;
import ir.kitgroup.saleinmeat.classes.CustomProgress;
import ir.kitgroup.saleinmeat.classes.Util;
import ir.kitgroup.saleinmeat.classes.Utilities;

import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;


import ir.kitgroup.saleinmeat.models.Company;
import ir.kitgroup.saleinmeat.models.Description;

import ir.kitgroup.saleinmeat.models.ModelDesc;

import ir.kitgroup.saleinmeat.models.ModelInvoice;
import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.databinding.FragmentInvoiceDetailMobileBinding;
import ir.kitgroup.saleinmeat.models.ModelProduct;
import ir.kitgroup.saleinmeat.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint

public class InVoiceDetailFragment extends Fragment {

    //region Parameter


    @Inject
    Company company;

    @Inject
    API api;

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentInvoiceDetailMobileBinding binding;
    private CustomProgress customProgress;
    private  CompositeDisposable compositeDisposable;


    private String type;
    private Boolean Seen = false;
    private String Inv_GUID;
    private String Tbl_GUID = "";
    private String Tbl_NAME = "";
    private String Ord_TYPE;
    private boolean EDIT = false;
    private boolean setADR1 = false;


    private double sumPrice;
    private double sumPurePrice;
    private double sumDiscounts;


    private List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetailMobileAdapter invoiceDetailAdapter;


    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private ImageView ivIconSync;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync


    //region Variable Dialog
    private Dialog dialogDelete;

    //endregion Variable Dialog


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private DescriptionAdapter descriptionAdapter;
    private ArrayList<Description> descriptionList;
    private String GuidInv;
    //endregion Variable Dialog Description


    private String status;
    private Boolean invFinal;
    private double sumTransport = 0.0;
    private String maxSales = "0";
    private String Transport_GUID;
    private List<InvoiceDetail> invDetails = new ArrayList<>();


    private String Acc_NAME;
    private String Acc_GUID;
    private int counter=0;


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


        try {

        customProgress = CustomProgress.getInstance();
            compositeDisposable = new CompositeDisposable();

        if (!Util.RetrofitValue) {
            ConfigRetrofit configRetrofit = new ConfigRetrofit();
            String name = sharedPreferences.getString("CN", "");
            company=null;
            api=null;
            company = configRetrofit.getCompany(name);
            api = configRetrofit.getRetrofit(company.baseUrl).create(API.class);

        }

        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

        descriptionList = new ArrayList<>();
        invoiceDetailList = new ArrayList<>();
        maxSales = sharedPreferences.getString("maxSale", "0");
        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");


        //region Configuration Text Size
        int fontSize;
        int fontLargeSize;
        if (Util.screenSize >= 7) {
            fontSize = 13;
            fontLargeSize = 14;
        } else {
            fontSize = 11;
            fontLargeSize = 12;
        }


        binding.tvNameCustomer.setTextSize(fontSize);
        binding.txtDate.setTextSize(fontSize);
        binding.txtTableNumber.setTextSize(fontSize);


        binding.orderListPurePriceTv.setTextSize(fontSize);
        binding.tvSumPurePrice.setTextSize(fontLargeSize);
        binding.orderListSumPriceTv.setTextSize(fontLargeSize);
        binding.tvSumPrice.setTextSize(fontLargeSize);
        binding.orderListSumDiscountTv.setTextSize(fontLargeSize);
        binding.tvSumDiscount.setTextSize(fontLargeSize);
        binding.tvSumTransport.setTextSize(fontLargeSize);
        binding.txtSumTransport.setTextSize(fontLargeSize);


        binding.btnContinue.setTextSize(fontSize);
        binding.btnDelete.setTextSize(fontSize);
        binding.btnEdit.setTextSize(fontSize);
        binding.txtDeleteAll.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Cast Dialog Delete


        dialogDelete = new Dialog(getActivity());
        dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.setContentView(R.layout.custom_dialog);
        dialogDelete.setCancelable(false);

        TextView textExit = dialogDelete.findViewById(R.id.tv_message);
        textExit.setText("آیا مایل به حذف کامل سبد خرید هستید؟");
        ImageView ivIcon = dialogDelete.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(company.imageDialog);

        MaterialButton btnOk = dialogDelete.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialogDelete.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> dialogDelete.dismiss());
        btnOk.setOnClickListener(v -> {
            dialogDelete.dismiss();
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }
            invoiceDetailList.clear();
            invoiceDetailAdapter.notifyDataSetChanged();



           Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");

            if (frg instanceof MainOrderFragment) {
                MainOrderFragment fgf = (MainOrderFragment) frg;
                fgf.refreshProductList();
            }

        });
        //endregion Cast Dialog Delete


        //region Get Bundle


        Bundle bundle = getArguments();
        type = bundle.getString("type");//1 seen   //2 Edit
        Ord_TYPE = bundle.getString("Ord_TYPE");
        Tbl_GUID = bundle.getString("Tbl_GUID");
        Tbl_NAME = bundle.getString("Tbl_NAME");
        Inv_GUID = bundle.getString("Inv_GUID");

        Acc_NAME = bundle.getString("Acc_NAME");
        Acc_GUID = bundle.getString("Acc_GUID");
        EDIT = bundle.getBoolean("EDIT");//when order need EDIT
        Seen = bundle.getBoolean("Seen");
        setADR1 = bundle.getBoolean("setADR1");


        binding.tvNameCustomer.setText("(" + Acc_NAME + " _ " + Tbl_NAME + ")");


        //endregion Get Bundle


        //region Configuration Client Application
        if (company.mode == 2) {
            binding.tvNameCustomer.setVisibility(View.GONE);
            binding.txtTableNumber.setVisibility(View.GONE);

        }
        //endregion Configuration Client Application


        sumPrice = 0;
        sumPurePrice = 0;
        sumDiscounts = 0;


        //region Cast Variable Dialog Sync
        dialogSync = new Dialog(getActivity());
        dialogSync.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSync.setContentView(R.layout.custom_dialog);
        dialogSync.setCancelable(false);

        textMessageDialog = dialogSync.findViewById(R.id.tv_message);
        ivIconSync = dialogSync.findViewById(R.id.iv_icon);

        btnOkDialog = dialogSync.findViewById(R.id.btn_ok);
        btnNoDialog = dialogSync.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> dialogSync.dismiss());


        btnOkDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            if (type.equals("1"))
                getInvoice();
            else {
                invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                if (invDetails.size() == 0)
                    binding.progressBar.setVisibility(View.GONE);

                else {
                    counter = 0;

                    for (int i = 0; i < invDetails.size(); i++) {
                        getProduct(invDetails.get(i).PRD_UID);
                    }
                }
            }

        });

        //endregion Cast Variable Dialog Sync


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


        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.BASELINE);
        recyclerDescription.setLayoutManager(flexboxLayoutManager);
        recyclerDescription.setLayoutManager(flexboxLayoutManager);
        descriptionAdapter = new DescriptionAdapter(getActivity(), descriptionList);
        recyclerDescription.setAdapter(descriptionAdapter);


        descriptionAdapter.setOnClickItemListener((desc, click, position) -> {
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

            InvoiceDetail invDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + GuidInv + "'").first();

            if (invDetail != null) {
                invDetail.INV_DET_DESCRIBTION = edtDescriptionItem.getText().toString();
                invDetail.update();


            }


            invoiceDetailAdapter.notifyDataSetChanged();
            dialogDescription.dismiss();
        });
        //endregion Cast DialogDescription


        //region Set Parameter Toolbar
        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");

        //region Seen Order After Send It
        if (type.equals("1")) {
            binding.txtDeleteAll.setVisibility(View.GONE);
            binding.layoutContinue.setVisibility(View.GONE);

        }
        //endregion Seen Order After Send It


        //region See Order During Work
        else {

            Calendar calendar = Calendar.getInstance();
            Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
            String text2 = (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year;
            binding.txtDate.setText(text2);


            if (company.mode == 1) {
                Account acc = Select.from(Account.class).first();
                if (acc != null)
                    binding.tvNameCustomer.setText("(" + acc.N + " _ " + Tbl_NAME + ")");
                else
                    binding.tvNameCustomer.setText("(" + "مشتری پیش فرض" + " _ " + Tbl_NAME + ")");
            }

        }
        //endregion See Order During Work


        //endregion Set Parameter Toolbar


        //region CONFIGURATION DATA INVOICE_DETAIL

        invoiceDetailList.clear();


        if (type.equals("1") && !EDIT)
            binding.tvSumPurePrice.setText(format.format(sumPurePrice + sumTransport) + " ریال ");
        else
            binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");


        binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
        binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");


        invoiceDetailAdapter = new InvoiceDetailMobileAdapter(getActivity(), invoiceDetailList, type, Seen, sharedPreferences);
        binding.recyclerDetailInvoice.setAdapter(invoiceDetailAdapter);
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setHasFixedSize(false);
        binding.recyclerDetailInvoice.setNestedScrollingEnabled(false);


        invoiceDetailAdapter.editAmountItemListener((Prd_GUID, s, Price, discountPercent) -> {

            if (maxSales.equals("1")) {
                getMaxSales(Prd_GUID, s);
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


                    invoiceDetailAdapter.notifyDataSetChanged();

                }
            }


        });


        invoiceDetailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                sumPrice = 0;
                sumPurePrice = 0;
                sumDiscounts = 0;

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

                if (invoiceDetails.size() > 0) {
                    CollectionUtils.filter(invoiceDetails, i -> !i.PRD_UID.toLowerCase().equals(Transport_GUID.toLowerCase()));
                }


                for (int i = 0; i < invoiceDetails.size(); i++) {
                    ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();
                    if (product != null) {
                        double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));

                        double discountPrice;
                        if (type.equals("1") || Seen)
                            discountPrice = sumprice * (invoiceDetails.get(i).INV_DET_PERCENT_DISCOUNT / 100);

                        else
                            discountPrice = sumprice * (product.getPercDis() / 100);
                        double totalPrice = sumprice - discountPrice;

                        sumPrice = sumPrice + (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));
                        sumPurePrice = sumPurePrice + totalPrice;
                        sumDiscounts = sumDiscounts + discountPrice;

                    }

                }


                binding.tvSumPurePrice.setText(format.format(sumPurePrice + sumTransport) + " ریال ");
                binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
                binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                sumPrice = 0;
                sumPurePrice = 0;
                sumDiscounts = 0;


                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoiceDetailList.get(positionStart).PRD_UID));
                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    if (invoiceDetail != null)
                        invoiceDetail.delete();
                }

                invoiceDetailList.remove(invoiceDetailList.get(positionStart));


                for (int i = 0; i < invoiceDetailList.size(); i++) {


                    ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + invoiceDetailList.get(i).PRD_UID + "'").first();
                    if (product != null) {
                        double sumprice = (invoiceDetailList.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));
                        double discountPrice = sumprice * (product.getPercDis() / 100);
                        double totalPrice = sumprice - discountPrice;

                        sumPrice = sumPrice + (invoiceDetailList.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));
                        sumPurePrice = sumPurePrice + totalPrice;
                        sumDiscounts = sumDiscounts + discountPrice;

                    }

                }


                binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");
                binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
                binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");


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
            getDescription(GUIDPrd);
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


        //region Action BtnDelete
        binding.btnDelete.setOnClickListener(v -> {

            if (status.equals("-"))
                getDeleteInvoice(Inv_GUID);
        });

        //endregion Action BtnDelete


        //region Action BtnEdit
        binding.btnEdit.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
            String tblGuid;
            if (tb != null && tb.GO != null)
                tblGuid = "";
            else
                tblGuid = Tbl_GUID;


        // getActivity().getSupportFragmentManager().popBackStack();
            Bundle bundleMainOrder = new Bundle();
            bundleMainOrder.putSerializable("key", (Serializable) invoiceDetailList);
            bundleMainOrder.putString("Inv_GUID", Inv_GUID);
            bundleMainOrder.putString("Tbl_GUID", tblGuid);
            bundleMainOrder.putString("Tbl_NAME", Tbl_NAME);
            bundleMainOrder.putString("Ord_TYPE", Ord_TYPE);
            bundleMainOrder.putString("Acc_GUID", Acc_GUID);
            bundleMainOrder.putString("Acc_NAME", Acc_NAME);
            bundleMainOrder.putBoolean("EDIT", true);
            bundleMainOrder.putBoolean("Seen", true);
            MainOrderFragment mainOrderFragment = new MainOrderFragment();
            mainOrderFragment.setArguments(bundleMainOrder);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();


        });
        //endregion Action BtnEdit


        //region Action BtnContinue
        binding.btnContinue.setOnClickListener(v -> {
            binding.btnContinue.setEnabled(false);
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            String ordTy = "";
            if (company.mode != 2)
                ordTy = Ord_TYPE;


            boolean edit = false;
            if (EDIT)
                edit = true;


            Bundle bundlePayment = new Bundle();
            bundlePayment.putString("Inv_GUID", Inv_GUID);
            bundlePayment.putString("Tbl_GUID", Tbl_GUID);
            bundlePayment.putString("Tbl_NAME", Tbl_NAME);
            bundlePayment.putString("Ord_TYPE", ordTy);
            bundlePayment.putString("Acc_GUID", Acc_GUID);
            bundlePayment.putString("Acc_NAME", Acc_NAME);
            bundlePayment.putString("Sum_PRICE", String.valueOf(sumPurePrice));
            bundlePayment.putBoolean("EDIT", edit);
            bundlePayment.putBoolean("Seen", Seen);
            bundlePayment.putBoolean("setADR1", setADR1);

            PaymentMobileFragment paymentMobileFragment = new PaymentMobileFragment();
            paymentMobileFragment.setArguments(bundlePayment);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, paymentMobileFragment, "PaymentMobileFragment").addToBackStack("PaymentMobileF").commit();
            binding.btnContinue.setEnabled(true);
        });
        //endregion Action BtnContinue


        binding.txtDeleteAll.setOnClickListener(v -> {
            if (invoiceDetailList.size() > 0)
                dialogDelete.show();
        });


        if (type.equals("1"))
            getInvoice();
        else {
            invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() == 0)
                binding.progressBar.setVisibility(View.GONE);

            else {
                counter = 0;
                for (int i = 0; i < invDetails.size(); i++) {
                    getProduct(invDetails.get(i).PRD_UID);
                }
            }
        }


        }catch (Exception ignore){
            Toast.makeText(getActivity(), "Invoice", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDescription(String id) {

        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات...", true);
        try {
            compositeDisposable.add(
                    api.getDescription1(company.userName, company.passWord, id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelDesc>() {
                                }.getType();
                                ModelDesc iDs = null;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
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


                            }, throwable -> {
                                customProgress.hideProgress();
                                Toast.makeText(getActivity(), "خطای تایم اوت در دریافت توضیحات", Toast.LENGTH_SHORT).show();

                            })
            );
        } catch (Exception e) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت توضیحات", Toast.LENGTH_SHORT).show();
        }


    }


    private void getMaxSales(String Prd_GUID, String s) {


        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        try {
            compositeDisposable.add(
                    api.getMaxSales(company.userName, company.passWord, Prd_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                        int remain = -1000000000;
                                        try {
                                            remain = Integer.parseInt(jsonElement);
                                        } catch (Exception e) {
                                            Gson gson = new Gson();
                                            Type typeIDs = new TypeToken<ModelLog>() {
                                            }.getType();
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                                            int message = iDs.getLogs().get(0).getMessage();
                                            String description = iDs.getLogs().get(0).getDescription();
                                            if (message != 1) {
                                                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        if (remain != -1000000000) {


                                            ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);


                                            CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));

                                            if (result.size() > 0) {
                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                                                double amount = 0.0;
                                                if (!s.equals("")) {
                                                    amount = Double.parseDouble(s);
                                                    if (remain - amount < 0) {
                                                        Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + jsonElement, Toast.LENGTH_SHORT).show();

                                                        if (invoiceDetail != null) {
                                                            invoiceDetail.INV_DET_QUANTITY = 0.0;
                                                            invoiceDetail.update();

                                                        }

                                                        invoiceDetailAdapter.notifyDataSetChanged();

                                                        return;
                                                    }
                                                }

                                                if (invoiceDetail != null) {
                                                    invoiceDetail.INV_DET_QUANTITY = amount;
                                                    invoiceDetail.update();

                                                }
                                                invoiceDetailAdapter.notifyDataSetChanged();

                                            }

                                        }

                                    }
                                    , throwable -> Toast.makeText(getContext(), "خطا در دریافت مانده کالا", Toast.LENGTH_SHORT).show())
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در دریافت مانده کالا", Toast.LENGTH_SHORT).show();

        }


    }


    @SuppressLint("SetTextI18n")
    private void getProduct(String Guid) {

        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        try {

            compositeDisposable.add(
                    api.getProduct(company.userName, company.passWord, Guid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                counter = counter + 1;
                                Gson gson = new Gson();
                                Type typeModelProduct = new TypeToken<ModelProduct>() {
                                }.getType();


                                ModelProduct iDs = null;


                                try {
                                    iDs = gson.fromJson(jsonElement, typeModelProduct);
                                } catch (Exception ignore) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }


                                if (iDs != null) {


                                    ArrayList<Product> list1 = new ArrayList<>(iDs.getProductList());
                                    if (list1.size() > 0) {
                                        ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + list1.get(0).getI() + "'").first();

                                        if (product != null)
                                            product.update();
                                        else
                                            ir.kitgroup.saleinmeat.DataBase.Product.saveInTx(list1.get(0));

                                    }

                                    if (counter == invDetails.size()) {
                                        invoiceDetailList.clear();


                                        if (invDetails.size() > 0) {
                                            ArrayList<InvoiceDetail> invDtls = new ArrayList<>(invDetails);
                                            CollectionUtils.filter(invDtls, i -> i.PRD_UID.toLowerCase().equals(Transport_GUID.toLowerCase()));
                                            if (invDtls.size() > 0) {
                                                if (type.equals("1")) {
                                                    sumTransport = Double.parseDouble(invDtls.get(0).INV_DET_TOTAL_AMOUNT);
                                                    binding.tvSumTransport.setText(format.format(sumTransport) + " ریال ");
                                                    binding.layoutTransport.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            CollectionUtils.filter(invDetails, i -> !i.PRD_UID.toLowerCase().equals(Transport_GUID.toLowerCase()));
                                        }


                                        invoiceDetailList.addAll(invDetails);
                                        invoiceDetailAdapter.notifyDataSetChanged();
                                        binding.progressBar.setVisibility(View.GONE);
                                        counter = 0;
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "لیست دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }

                            }, throwable -> {


                            })
            );
        } catch (Exception e) {
            Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }

    }


    @SuppressLint("SetTextI18n")
    private void getInvoice() {

        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    api.getInvoice1(company.userName, company.passWord, Inv_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelInvoice>() {
                                }.getType();
                                ModelInvoice iDs = null;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception ignored) {
                                    Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }

                                if (iDs != null) {

                                    String name = company.namePackage.split("ir.kitgroup.")[1];
                                    String invGuid = sharedPreferences.getString(name, "");

                                    List<InvoiceDetail> list = Select.from(InvoiceDetail.class).list();
                                    CollectionUtils.filter(list, l -> l.INV_UID != null && !l.INV_UID.equals(invGuid));
                                    for (int i = 0; i < list.size(); i++) {
                                        InvoiceDetail.deleteInTx(list.get(i));
                                    }


                                    InvoiceDetail.saveInTx(iDs.getInvoiceDetail());

                                    if (iDs.getInvoice().size() == 0) {
                                        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                                    } else {


                                        status = iDs.getInvoice().get(0).INV_SYNC;
                                        invFinal = iDs.getInvoice().get(0).invFinalStatusControl;
                                        Ord_TYPE = String.valueOf(iDs.getInvoice().get(0).INV_TYPE_ORDER);
                                        //   sumDiscountsInvoiceRial = iDs.getInvoice().get(0).INV_TOTAL_DISCOUNT;


                                        if (company.mode == 1) {
                                            Acc_NAME = iDs.getInvoice().get(0).accClbName;
                                            Acc_GUID = iDs.getInvoice().get(0).ACC_CLB_UID;
                                        }


                                        binding.tvNameCustomer.setText("(" + Acc_NAME + " _ " + Tbl_NAME + ")");

                                        if (status != null && (status.equals("*") || invFinal)) {
                                            binding.layoutEditDelete.setVisibility(View.GONE);
                                        } else if (status != null && status.equals("-")) {
                                            binding.layoutEditDelete.setVisibility(View.VISIBLE);

                                        }


                                        if (iDs.getInvoice().get(0).INV_DUE_DATE_PERSIAN != null)
                                            binding.txtDate.setText(iDs.getInvoice().get(0).INV_DUE_DATE_PERSIAN);


                                        CollectionUtils.filter(iDs.getInvoiceDetail(), i -> !i.PRD_UID.toLowerCase().equals(Transport_GUID.toLowerCase()));
                                        invDetails.clear();
                                        invDetails.addAll(iDs.getInvoiceDetail());


                                    }


                                    if (invDetails.size() == 0)
                                        binding.progressBar.setVisibility(View.GONE);
                                    else
                                        for (int i = 0; i < invDetails.size(); i++) {
                                            getProduct(invDetails.get(i).PRD_UID);
                                        }


                                } else {
                                    Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);


                                }
                                binding.progressBar.setVisibility(View.GONE);

                            }, throwable -> {
                                Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }

    }

    private void getDeleteInvoice(String Inv_GUID) {

        customProgress.showProgress(getActivity(), "در حال دریافت تغییرات فاکتور...", false);

        try {


            Call<String> call = api.getDeleteInvoice(company.userName, company.passWord, Inv_GUID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    if (iDs != null) {
                        int message = iDs.getLogs().get(0).getMessage();
                        if (message == 4) {
                            List<InvoiceDetail> invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                            for (int i = 0; i < invoiceDetail.size(); i++) {
                                ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + invoiceDetail.get(i).PRD_UID + "'").first();
                                if (product != null) {
                                    product.delete();
                                }
                                InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                            }

                            Fragment frg;
                            if (company.mode == 2) {
                                getActivity().getSupportFragmentManager().popBackStack();
                                frg = getActivity().getSupportFragmentManager().findFragmentByTag("OrderListFragment");

                            } else {
                                Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                                if (tb != null && tb.GO != null)
                                    Tables.delete(tb);

                                frg = getActivity().getSupportFragmentManager().findFragmentByTag("LauncherFragment");
                            }


                            FragmentManager ft = getActivity().getSupportFragmentManager();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                ft.beginTransaction().detach(frg).commitNow();
                                ft.beginTransaction().attach(frg).commitNow();

                            } else {

                                ft.beginTransaction().detach(frg).attach(frg).commit();
                            }


                        }
                    } else {
                        Toast.makeText(getActivity(), "خطایی در سرور رخ داد", Toast.LENGTH_SHORT).show();
                    }


                    customProgress.hideProgress();

                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (
                NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }





    private Boolean networkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowErrorConnection() {
        binding.progressBar.setVisibility(View.GONE);
        textMessageDialog.setText("خطا در اتصال به اینترنت");
        ivIconSync.setImageResource(R.drawable.ic_wifi);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
        customProgress.hideProgress();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (type.equals("1")) {
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" +
                    Inv_GUID + "'").list();
            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }
        }
        compositeDisposable.dispose();
       binding=null;
    }



    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

}

