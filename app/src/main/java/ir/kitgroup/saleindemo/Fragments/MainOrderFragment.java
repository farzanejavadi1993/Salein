package ir.kitgroup.saleindemo.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import android.widget.EditText;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


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


import java.lang.reflect.Type;


import java.util.ArrayList;

import java.util.List;

import java.util.UUID;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


import ir.kitgroup.saleindemo.Activities.LauncherActivity;
import ir.kitgroup.saleindemo.Adapters.AccountAdapter;
import ir.kitgroup.saleindemo.Adapters.DescriptionAdapter;

import ir.kitgroup.saleindemo.Adapters.ProductAdapter1;
import ir.kitgroup.saleindemo.Adapters.ProductLevel1Adapter;
import ir.kitgroup.saleindemo.Adapters.ProductLevel2Adapter;
import ir.kitgroup.saleindemo.Connect.API;

import ir.kitgroup.saleindemo.DataBase.Unit;
import ir.kitgroup.saleindemo.classes.ConfigRetrofit;
import ir.kitgroup.saleindemo.classes.CustomProgress;


import ir.kitgroup.saleindemo.DataBase.Account;

import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;


import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.models.Company;
import ir.kitgroup.saleindemo.models.Config;
import ir.kitgroup.saleindemo.models.ModelUnit;
import ir.kitgroup.saleindemo.models.Setting;

import ir.kitgroup.saleindemo.classes.PaginationScrollListener;

import ir.kitgroup.saleindemo.models.Description;

import ir.kitgroup.saleindemo.models.ModelAccount;
import ir.kitgroup.saleindemo.models.ModelDesc;
import ir.kitgroup.saleindemo.models.ModelLog;
import ir.kitgroup.saleindemo.models.ModelProduct;
import ir.kitgroup.saleindemo.models.ModelProductLevel1;
import ir.kitgroup.saleindemo.models.ModelProductLevel2;
import ir.kitgroup.saleindemo.models.Product;
import ir.kitgroup.saleindemo.models.ProductLevel1;
import ir.kitgroup.saleindemo.models.ModelSetting;
import ir.kitgroup.saleindemo.R;

import ir.kitgroup.saleindemo.databinding.FragmentMobileOrderMainBinding;


import ir.kitgroup.saleindemo.models.ProductLevel2;


import static java.lang.Math.min;

@AndroidEntryPoint
public class MainOrderFragment extends Fragment {


    //region Parameter

    @Inject
    Company company;

    @Inject
    Config config;


    @Inject
    API api;

    @Inject
    SharedPreferences sharedPreferences;


    private FragmentMobileOrderMainBinding binding;

    private CustomProgress customProgress;

    private String Inv_GUID = "";
    private String Tbl_GUID = "";
    private String Tbl_NAME = "";
    private String Ord_TYPE = "";
    private String Acc_GUID = "";
    private String Acc_NAME = "";
    private Boolean Seen = false;
    private Boolean EDIT = false;
    boolean setARD1 = false;


    private ArrayList<ProductLevel1> productLevel1List;
    private ProductLevel1Adapter productLevel1Adapter;


    private ArrayList<ProductLevel2> productLevel2List;
    private ProductLevel2Adapter productLevel2Adapter;


    private ArrayList<Product> productList;
    private ArrayList<Product> productListData;
    private ProductAdapter1 productAdapter;
    private boolean isLastPage;
    private boolean isLoading;
    private int currentPage;
    private int totalPage;


    private CompositeDisposable compositeDisposable;
    private String error = "";


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private ArrayList<Description> descriptionList;
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv;
    //endregion Variable Dialog Description


    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private ImageView ivIconSync;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync


    private TextWatcher textWatcherAcc;
    private AccountAdapter accAdapter;
    private ArrayList<Account> accList;


    //region Variable DialogAddAccount
    private List<Account> accountsList;
    private Dialog dialogAddAccount;
    private EditText edtNameAccount;
    private EditText edtAddressAccount;
    private EditText edtMobileAccount;
    private int genderAccount;
    //endregion Variable DialogAddAccount


    //region Variable DialogUpdate
    private Dialog dialogUpdate;
    private TextView textUpdate;
    private MaterialButton btnNo;

    //endregion Variable DialogUpdate

    //region Dialog Address
    private Dialog dialogAddress;
    private RadioButton radioAddress1;
    private RadioButton radioAddress2;
    public int typeAddress = 0;


    private Double latitude1 = 0.0;
    private Double longitude1 = 0.0;

    private Double latitude2 = 0.0;
    private Double longitude2 = 0.0;


    //endregion Dialog Address


    private String maxSales = "0";
    private boolean chooseAccount = false;


    private String Transport_GUID = "";
    private String linkUpdate="";

    public int counter1 = 0;

    //  private List<InvoiceDetail> invoiceDetails;


    //endregion Parameter

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentMobileOrderMainBinding.inflate(getLayoutInflater());

        return binding.getRoot();


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        customProgress = CustomProgress.getInstance();
        compositeDisposable = new CompositeDisposable();

        if (!Util.RetrofitValue) {
            ConfigRetrofit configRetrofit = new ConfigRetrofit();
            String name = sharedPreferences.getString("CN", "");
            company = null;
            api = null;
            company = configRetrofit.getCompany(name);
            api = configRetrofit.getRetrofit(company.baseUrl).create(API.class);

        }

        getUnit();


        //region First Value Parameter

        counter1 = 0;

        descriptionList = new ArrayList<>();
        accountsList = new ArrayList<>();
        productLevel1List = new ArrayList<>();
        productLevel2List = new ArrayList<>();
        productList = new ArrayList<>();
        productListData = new ArrayList<>();


        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
            productLevel1Adapter.notifyDataSetChanged();
            productLevel2Adapter.notifyDataSetChanged();
        }


        //endregion First Value Parameter


        //region Get Bundle

        Bundle bundle = getArguments();
        Ord_TYPE = bundle.getString("Ord_TYPE");
        Tbl_GUID = bundle.getString("Tbl_GUID");
        Tbl_NAME = bundle.getString("Tbl_NAME");
        Inv_GUID = bundle.getString("Inv_GUID");

        Acc_NAME = bundle.getString("Acc_NAME");
        Acc_GUID = bundle.getString("Acc_GUID");
        EDIT = bundle.getBoolean("EDIT");//when order need EDIT
        Seen = bundle.getBoolean("Seen");
        // invoiceDetails = (List<InvoiceDetail>) bundle.getSerializable("key");

        setARD1 = bundle.getBoolean("setADR");

        if (!setARD1)
            setARD1 = bundle.getBoolean("setADR1");


        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");


        //region Create Order
        if (Inv_GUID.equals("")) {
            String name;


            name = company.namePackage.split("ir.kitgroup.")[1];
            Inv_GUID = sharedPreferences.getString(name, "");
            if (Inv_GUID.equals("")) {
                Inv_GUID = UUID.randomUUID().toString();
                sharedPreferences.edit().putString(name, Inv_GUID).apply();

            }


            if (!Inv_GUID.equals("") && name.equals("saleinOrder")) {
                Inv_GUID = UUID.randomUUID().toString();
            }

        }


        List<InvoiceDetail> invDetailses = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();


        if (invDetailses.size() > 0) {

            CollectionUtils.filter(invDetailses, i -> !i.PRD_UID.toLowerCase().equals(Transport_GUID.toLowerCase()));
            counter1 = invDetailses.size();


        }

        if (counter1 == 0)
            ((LauncherActivity) getActivity()).setClearCounterOrder();
        else
            ((LauncherActivity) getActivity()).setCounterOrder(counter1);


        ((LauncherActivity) getActivity()).setInVisibiltyItem(!EDIT);
        ((LauncherActivity) getActivity()).setMainOrderFragment(this);
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);


        //endregion Create Order


        //endregion Get Bundle


        //region Delete InvoiceDetail UnNecessary
        if (company.mode == 1) {
            List<InvoiceDetail> invDetail = Select.from(InvoiceDetail.class).where("TBL ='" + "" + "'").list();
            for (int i = 0; i < invDetail.size(); i++) {
                InvoiceDetail.delete(invDetail.get(i));
            }
        }
        //endregion Delete InvoiceDetail UnNecessary


        //region Configuration Text Size
        int fontSize;
        if (Util.screenSize >= 7)
            fontSize = 14;
        else
            fontSize = 12;


        binding.edtNameCustomer.setTextSize(fontSize);

        //endregion Configuration Text Size


        //region Set Icon And Title
        binding.tvNameStore.setText(company.nameCompany);
        //endregion Set Icon And Title


        Account acc = Select.from(Account.class).first();

        //region Cast DialogAddress

        dialogAddress = new Dialog(getActivity());
        dialogAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddress.setContentView(R.layout.dialog_address);
        dialogAddress.setCancelable(true);

        radioAddress1 = dialogAddress.findViewById(R.id.radioAddress1);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        MaterialButton btnNewAddress = dialogAddress.findViewById(R.id.btn_edit);


        if (acc != null && acc.ADR != null && !acc.ADR.equals("")) {
            String address;
            try {
                latitude1 = Double.parseDouble(acc.ADR.split("latitude")[1]);
                longitude1 = Double.parseDouble(acc.ADR.split("longitude")[0]);
                address = acc.ADR.replace(acc.ADR.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR.split("longitude")[0], "").replace("longitude", "");

            } catch (Exception e) {
                address = acc.ADR + "( نامعتبر )";
                latitude1 = 0.0;
                longitude1 = 0.0;
            }
            radioAddress1.setText(address);

        }
        if (acc != null && acc.ADR2 != null && !acc.ADR2.equals("")) {
            String address;
            try {
                latitude2 = Double.parseDouble(acc.ADR2.split("latitude")[1]);
                longitude2 = Double.parseDouble(acc.ADR2.split("longitude")[0]);
                address = acc.ADR2.replace(acc.ADR2.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR2.split("longitude")[0], "").replace("longitude", "");

            } catch (Exception e) {
                address = acc.ADR2 + "( نامعتبر )";
                latitude2 = 0.0;
                longitude2 = 0.0;
            }
            radioAddress2.setText(address);

        }


        radioAddress1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    latitude1 = Double.parseDouble(acc.ADR.split("latitude")[1]);
                    longitude1 = Double.parseDouble(acc.ADR.split("longitude")[0]);
                } catch (Exception ignored) {
                }

                if (latitude1 == 0.0 && longitude1 == 0.0) {
                    Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();
                    return;
                }


                typeAddress = 1;
                String address = radioAddress1.getText().toString();
                binding.tvAddress.setText(address);
                dialogAddress.dismiss();

            }
        });
        radioAddress2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                try {
                    latitude2 = Double.parseDouble(acc.ADR2.split("latitude")[1]);
                    longitude2 = Double.parseDouble(acc.ADR2.split("longitude")[0]);
                } catch (Exception ignored) {
                }
                if (latitude2 == 0.0 || longitude2 == 0.0) {
                    Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();
                    return;
                }


                typeAddress = 2;
                String address = radioAddress2.getText().toString();
                binding.tvAddress.setText(address);
                dialogAddress.dismiss();
            }
        });


        btnNewAddress.setOnClickListener(v -> {
            if (acc == null) {
                Toast.makeText(getActivity(), "مشتری نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            }
            dialogAddress.dismiss();


            Bundle bundleMap = new Bundle();
            bundleMap.putString("mobileNumber", "");
            bundleMap.putString("edit_address", "3");
            bundleMap.putString("type", "");
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundleMap);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").addToBackStack("MapF").commit();


        });


        //endregion Cast DialogAddress


        //region SetAddress


        if (acc != null && acc.ADR != null && !acc.ADR.equals("") && !setARD1) {
            String address = "نامشخص";
            try {
                typeAddress = 1;
                address = acc.ADR.replace(acc.ADR.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR.split("longitude")[0], "").replace("longitude", "");
            } catch (Exception ignored) {
            }
            binding.tvAddress.setText(address);
        } else if (acc != null && acc.ADR2 != null && !acc.ADR2.equals("")) {
            String address = "نامشخص";
            try {
                typeAddress = 2;
                address = acc.ADR2.replace(acc.ADR2.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR2.split("longitude")[0], "").replace("longitude", "");
            } catch (Exception ignored) {

            }
            binding.tvAddress.setText(address);
        } else {
            typeAddress = 0;
            binding.tvAddress.setText("نامشخص");
        }

        //endregion SetAddress


        //region Action BtnAddress
        binding.btnAddAddress.setOnClickListener(v -> {
            if (acc != null && acc.ADR != null && !acc.ADR.equals("") && acc.ADR2 != null && !acc.ADR2.equals(""))
                dialogAddress.show();
            else {
                Bundle bundleMap = new Bundle();
                bundleMap.putString("mobileNumber", "");
                bundleMap.putString("edit_address", "3");
                bundleMap.putString("type", "");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundleMap);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").addToBackStack("MapF").commit();


            }
        });
        //endregion Action BtnAddress


        //region Configuration Organization Application
        if (company.mode == 1) {
            binding.layoutAddressBranch.setVisibility(View.GONE);
            binding.layoutAccount.setVisibility(View.VISIBLE);


            //region Cast DialogAddAcc
            dialogAddAccount = new Dialog(getActivity());
            dialogAddAccount.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAddAccount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogAddAccount.setContentView(R.layout.dialog_add_account);
            dialogAddAccount.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialogAddAccount.setCancelable(false);


            ImageView imgCloseAddDialog = dialogAddAccount.findViewById(R.id.iv_close_add_dialog);
            edtNameAccount = dialogAddAccount.findViewById(R.id.edt_address);
            edtMobileAccount = dialogAddAccount.findViewById(R.id.edt_unit);
            edtAddressAccount = dialogAddAccount.findViewById(R.id.edt_pelaque);
            RadioButton radioMan = dialogAddAccount.findViewById(R.id.radioMan);
            RadioButton radioWoman = dialogAddAccount.findViewById(R.id.radioWoman);
            MaterialButton btnRegisterAccount = dialogAddAccount.findViewById(R.id.btn_register_address);
            radioMan.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    genderAccount = 0;
                }
            });
            radioWoman.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    genderAccount = 1;
                }
            });

            imgCloseAddDialog.setOnClickListener(v -> dialogAddAccount.dismiss());


            btnRegisterAccount.setOnClickListener(v -> {
                if (edtNameAccount.getText().toString().isEmpty() || edtMobileAccount.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "لطفا فیلد نام مشتری و شماره موبایل مشتری را پر کنید.", Toast.LENGTH_SHORT).show();

                } else if (!edtMobileAccount.getText().toString().equals("1") && (edtMobileAccount.getText().toString().length() < 11 || edtMobileAccount.getText().toString().length() > 11)) {
                    Toast.makeText(getActivity(), "شماره موبایل صحیح نمی باشد.", Toast.LENGTH_SHORT).show();

                } else {

                    Account account = new Account();
                    account.I = UUID.randomUUID().toString();
                    account.N = edtNameAccount.getText().toString();
                    account.M = edtMobileAccount.getText().toString();
                    account.ADR = edtAddressAccount.getText().toString();
                    account.S = String.valueOf(genderAccount);
                    accountsList.clear();
                    accountsList.add(account);
                    addAccount(accountsList);

                }
            });
            //endregion Cast DialogAddAcc


            accList = new ArrayList<>();
            accAdapter = new AccountAdapter(getActivity(), accList);
            binding.accountRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.accountRecyclerView.setAdapter(accAdapter);
            textWatcherAcc = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    chooseAccount = false;
                    Acc_GUID = "";
                    Acc_NAME = "";

                    if (s.toString().isEmpty()) {
                        binding.accountRecyclerView.setVisibility(View.GONE);

                        binding.edtNameCustomer.setHint("جستجو مشتری");
                    } else if (s.toString().length() > 3) {
                        getAccountSearch1(s.toString(), 0);
                    }


                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };


            accAdapter.setOnClickItemListener((account) -> {
                chooseAccount = true;
                Account.deleteAll(Account.class);
                Account.saveInTx(account);
                Acc_GUID = account.I;
                Acc_NAME = account.N;
                binding.edtNameCustomer.removeTextChangedListener(textWatcherAcc);
                binding.edtNameCustomer.setText(account.N);
                binding.accountRecyclerView.setVisibility(View.GONE);
                binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);
            });


            binding.btnAddAccount.setOnClickListener(v -> {
                edtNameAccount.setText("");
                edtAddressAccount.setText("");
                edtMobileAccount.setText("");
                dialogAddAccount.show();
            });


            binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);


        }
        //endregion Configuration Organization Application


        //region Configuration Client Application

        else {
            binding.layoutAddressBranch.setVisibility(View.VISIBLE);
            binding.layoutAccount.setVisibility(View.GONE);


            Acc_NAME = Select.from(Account.class).first().N;
            Acc_GUID = Select.from(Account.class).first().I;

        }

        //endregion Configuration Client Application


        if (company.mode == 1 && EDIT) {
            binding.edtNameCustomer.setEnabled(false);
            binding.edtNameCustomer.removeTextChangedListener(textWatcherAcc);
            binding.edtNameCustomer.setText(Acc_NAME);
            binding.accountRecyclerView.setVisibility(View.GONE);
            binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);
            getAccountSearch1(Acc_NAME, 1);
        }


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
        btnNoDialog.setOnClickListener(v -> {
            dialogSync.dismiss();

            if (company.mode == 2 && !config.packageName.equals("ir.kitgroup.salein"))
                getActivity().finish();

            else
                getActivity().getSupportFragmentManager().popBackStack();

        });


        btnOkDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            getProductLevel1();
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

        descriptionAdapter = new DescriptionAdapter(getActivity(), descriptionList);
        recyclerDescription.setAdapter(descriptionAdapter);


        descriptionAdapter.setOnClickItemListener((desc, click, position) -> {
            if (click) {
                descriptionList.get(position).Click = true;
                String description = edtDescriptionItem.getText().toString();
                edtDescriptionItem.setText(description + "   " + "'" + desc + "'");
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

            productAdapter.notifyDataSetChanged();

            dialogDescription.dismiss();
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
        //endregion Cast DialogDescription


        //region Cast Dialog Update
        dialogUpdate = new Dialog(getActivity());
        dialogUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogUpdate.setContentView(R.layout.custom_dialog);
        dialogUpdate.setCancelable(false);


        textUpdate = dialogUpdate.findViewById(R.id.tv_message);
        ImageView ivIcon = dialogUpdate.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(company.imageDialog);


        MaterialButton btnOk = dialogUpdate.findViewById(R.id.btn_ok);
        btnOk.setText("آپدیت");
        btnNo = dialogUpdate.findViewById(R.id.btn_cancel);
        btnNo.setText("بعدا");


        btnNo.setOnClickListener(v -> dialogUpdate.dismiss());


        btnOk.setOnClickListener(v -> {
            btnOk.setEnabled(false);

            if (!linkUpdate.equals("")) {
                btnOk.setEnabled(true);
                Uri uri = Uri.parse(linkUpdate);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }


        });
        //endregion Cast Dialog Update


        getProductLevel1();
        getSetting();

        //region CONFIGURATION DATA PRODUCT_LEVEL1
        productLevel1Adapter = new ProductLevel1Adapter(getActivity(), productLevel1List);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);
        binding.orderRecyclerViewProductLevel1.setLayoutManager(manager);
        binding.orderRecyclerViewProductLevel1.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProductLevel1.setAdapter(productLevel1Adapter);


        //region Click Item ProductLevel1
        productLevel1Adapter.SetOnItemClickListener(GUID -> {

            binding.progressbar.setVisibility(View.VISIBLE);
            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
            isLastPage = false;
            currentPage = 1;
            binding.orderTxtError.setText("");


            //region UnClick Old Item ProductLevel1 And Item ProductLevel2
            ArrayList<ProductLevel1> resultPrdGrp1 = new ArrayList<>(productLevel1List);
            CollectionUtils.filter(resultPrdGrp1, r -> r.Click);
            if (resultPrdGrp1.size() > 0) {
                productLevel1List.get(productLevel1List.indexOf(resultPrdGrp1.get(0))).Click = false;
            }
            //endregion UnClick Old Item ProductLevel1 And Item ProductLevel2


            //region Click New Item ProductLevel1
            ArrayList<ProductLevel1> resultPrdGroup1_ = new ArrayList<>(productLevel1List);
            CollectionUtils.filter(resultPrdGroup1_, r -> r.getI().equals(GUID));
            if (resultPrdGroup1_.size() > 0) {
                productLevel1List.get(productLevel1List.indexOf(resultPrdGroup1_.get(0))).Click = true;
            }
            //endregion Click New Item ProductLevel1


            productLevel1Adapter.notifyDataSetChanged();
            productList.clear();
            productListData.clear();
            productAdapter.notifyDataSetChanged();

            productLevel2List.clear();
            productLevel2Adapter.notifyDataSetChanged();


            //region Full ProductLevel2List Because This Item ProductLevel1 Is True
            getProductLevel2(GUID);
            //endregion Full ProductLevel2List Because This Item ProductLevel1 Is True
        });
        //endregion Click Item ProductLevel1

        //endregion CONFIGURATION DATA PRODUCT_LEVEL1

        //region CONFIGURATION DATA PRODUCT_LEVEL_2
        productLevel2Adapter = new ProductLevel2Adapter(getActivity(), productLevel2List);

        LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager2.setReverseLayout(true);
        binding.orderRecyclerViewProductLevel2.setLayoutManager(manager2);
        binding.orderRecyclerViewProductLevel2.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProductLevel2.setAdapter(productLevel2Adapter);


        //region Click Item ProductLevel2
        productLevel2Adapter.SetOnItemClickListener(GUID -> {

            binding.progressbar.setVisibility(View.VISIBLE);
            productList.clear();

            productAdapter.notifyDataSetChanged();
            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
            isLastPage = false;
            currentPage = 1;
            binding.orderTxtError.setText("");


            //region UnClick Old Item
            ArrayList<ProductLevel2> resultProductGroupLevel2 = new ArrayList<>(productLevel2List);
            CollectionUtils.filter(resultProductGroupLevel2, r -> r.Click);
            if (resultProductGroupLevel2.size() > 0 && productLevel2List.size() > 0) {
                productLevel2List.get(productLevel2List.indexOf(resultProductGroupLevel2.get(0))).Click = false;
            }
            //endregion UnClick Old Item


            //region Click New Item
            ArrayList<ProductLevel2> resProductGroupLevel2 = new ArrayList<>(productLevel2List);
            CollectionUtils.filter(resProductGroupLevel2, r -> r.getI().equals(GUID));

            if (resProductGroupLevel2.size() > 0) {
                productLevel2List.get(productLevel2List.indexOf(resProductGroupLevel2.get(0))).Click = true;
            }
            //endregion Click New Item

            productLevel2Adapter.notifyDataSetChanged();


            //region Full ProductList Because This Item ProductLevel1 Is True

            getSettingPrice(GUID);
            //endregion Full ProductList Because This Item ProductLevel1 Is True
        });

        //endregion Click Item ProductLevel2

        //endregion CONFIGURATION DATA PRODUCT_LEVEL_2

        //region CONFIGURATION DATA PRODUCT

        //region Cast Product Configuration


        //endregion Cast Product Configuration

        productAdapter = new ProductAdapter1(getActivity(), productList, company, api, sharedPreferences, Inv_GUID);
        // productAdapter.setInv_GUID(Inv_GUID);
        productAdapter.setTbl_GUID(Tbl_GUID);
        productAdapter.setType(Seen);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        binding.orderRecyclerViewProduct.setLayoutManager(linearLayoutManager);
        binding.orderRecyclerViewProduct.setScrollingTouchSlop(View.FOCUS_LEFT);


        binding.orderRecyclerViewProduct.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {

            @Override
            protected void loadMoreItems() {

                currentPage++;
                loadMore();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;

            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        binding.orderRecyclerViewProduct.setAdapter(productAdapter);


        productAdapter.setOnClickListener(() -> {
            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();


            if (invDetails.size() > 0) {
                int counter = invDetails.size();
                counter1 = counter;
                ((LauncherActivity) getActivity()).setCounterOrder(counter);


            } else {
                ((LauncherActivity) getActivity()).setClearCounterOrder();
            }


        });

        productAdapter.setOnDescriptionItem((GUID, amount) -> {
            if (amount > 0) {
                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetails);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));

                if (result.size() > 0) {
                    edtDescriptionItem.setText(result.get(0).INV_DET_DESCRIBTION);
                    descriptionList.clear();
                    GuidInv = result.get(0).INV_DET_UID;
                    getDescription(GUID);

                }
            } else {
                Toast.makeText(getActivity(), "برای نوشتن توضیحات برای کالا مقدار ثبت کنید.", Toast.LENGTH_SHORT).show();
            }
        });


        //endregion CONFIGURATION DATA PRODUCT


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    private void getProductLevel1() {

        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }

        try {
            compositeDisposable.add(
                    api.getProductLevel1("saleinkit_api", company.userName, company.passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                        Gson gson = new Gson();
                                        Type typeModelProductLevel1 = new TypeToken<ModelProductLevel1>() {
                                        }.getType();
                                        ModelProductLevel1 iDs;

                                        try {
                                            iDs = gson.fromJson(jsonElement, typeModelProductLevel1);
                                        } catch (Exception e) {
                                            error = "مدل دریافت شده از  گروه کالاها نا معتبر است";
                                            showError(error);
                                            binding.progressbar.setVisibility(View.GONE);
                                            return;
                                        }

                                        if (iDs == null) {
                                            error = "لیست دریافت شده از  گروه کالاها نا معتبر می باشد";
                                            showError(error);
                                            binding.progressbar.setVisibility(View.GONE);
                                            return;
                                        }
                                        productLevel1List.addAll(iDs.getProductLevel1());
                                        if (productLevel1List.size() == 1)
                                            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
                                        productLevel1Adapter.notifyDataSetChanged();


                                        if (productLevel1List.size() > 0) {
                                            productLevel1List.get(0).Click = true;
                                            getProductLevel2(productLevel1List.get(0).getI());
                                        } else {
                                            binding.progressbar.setVisibility(View.GONE);
                                            binding.orderTxtError.setText("هیچ گروهی از کالاها موجود نیست");
                                            binding.orderTxtError.setVisibility(View.VISIBLE);
                                        }

                                    }
                                    , throwable -> {
                                        error = "فروشگاه تعطیل می باشد.";
                                        showError(error);
                                        binding.progressbar.setVisibility(View.GONE);


                                    })
            );
        } catch (Exception e) {
            error = "خطا در اتصال به سرور برای دریافت گروه کالاها";
            showError(error);
            binding.progressbar.setVisibility(View.GONE);
        }


    }

    private void getProductLevel2(String GuidPrdLvl1) {
        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        try {
            binding.progressbar.setVisibility(View.VISIBLE);
            compositeDisposable.add(
                    api.getProductLevel2("saleinkit_api", company.userName, company.passWord, GuidPrdLvl1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        Gson gson = new Gson();
                                        Type typeModelProduct2 = new TypeToken<ModelProductLevel2>() {
                                        }.getType();
                                        ModelProductLevel2 iDs;


                                        try {
                                            iDs = gson.fromJson(jsonElement, typeModelProduct2);
                                        } catch (Exception e) {
                                            error = "مدل دریافت شده از زیر گروه کالاها نا معتبر است";
                                            showError(error);
                                            binding.progressbar.setVisibility(View.GONE);
                                            return;
                                        }

                                        if (iDs == null) {
                                            error = "لیست دریافت شده از زیر گروه کالاها نا معتبر می باشد";
                                            showError(error);
                                            binding.progressbar.setVisibility(View.GONE);
                                            return;
                                        }

                                        productLevel2List.clear();
                                        CollectionUtils.filter(iDs.getProductLevel2(), ProductLevel2::getSts);
                                        productLevel2List.addAll(iDs.getProductLevel2());


                                        if (productLevel2List.size() > 0) {
                                            ArrayList<ProductLevel2> list = new ArrayList<>(productLevel2List);
                                            CollectionUtils.filter(list, l -> l.getTkn() != 0);
                                            if (list.size() > 0)
                                                for (int i = 0; i < list.size(); i++) {
                                                    int position = list.get(i).getTkn() - 1;//new position
                                                    int index = productLevel2List.indexOf(list.get(i));//old position
                                                    if (productLevel2List.size() > position) {
                                                        ProductLevel2 itemProductGroupLevel2 = productLevel2List.get(position);
                                                        if (index != position) {
                                                            productLevel2List.set(position, productLevel2List.get(productLevel2List.indexOf(list.get(i))));
                                                            productLevel2List.set(index, itemProductGroupLevel2);

                                                        }
                                                    }

                                                }

                                            if (productLevel2List.size() > 0) {
                                                productLevel2List.get(0).Click = true;
                                            }

                                            productLevel2Adapter.notifyDataSetChanged();


                                            //region Full ProductList Because First Item ProductLevel2 Is True
                                            getSettingPrice(productLevel2List.get(0).getI());
                                            //endregion Full ProductList Because First Item ProductLevel2 Is True

                                        } else {


                                            binding.orderTxtError.setText("هیچ زیرگروهی برای این گروه کالایی وجود ندارد.");
                                            binding.orderTxtError.setVisibility(View.VISIBLE);
                                            binding.progressbar.setVisibility(View.GONE);
                                            productList.clear();
                                            productAdapter.notifyDataSetChanged();
                                            productLevel2List.clear();
                                            productLevel2Adapter.notifyDataSetChanged();
                                        }

                                        productLevel2Adapter.notifyDataSetChanged();

                                    }
                                    , throwable -> {
                                        error = "فروشگاه تعطیل می باشد.";
                                        showError(error);
                                        binding.progressbar.setVisibility(View.GONE);


                                    })
            );
        } catch (Exception e) {
            error = "خطا در اتصال به سرور برای دریافت زیر گروه کالاها.";
            showError(error);
            binding.progressbar.setVisibility(View.GONE);
        }


    }


    private void getProduct1(String GuidPrdLvl2) {
        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        try {
            compositeDisposable.add(
                    api.getProduct1("saleinkit_api", company.userName, company.passWord, GuidPrdLvl2)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        Gson gson = new Gson();
                                        Type typeModelProduct = new TypeToken<ModelProduct>() {
                                        }.getType();
                                        ModelProduct iDs;

                                        try {
                                            iDs = gson.fromJson(jsonElement, typeModelProduct);
                                        } catch (Exception e) {
                                            error = "مدل دریافت شده از کالاها نا معتبر است";
                                            showError(error);
                                            binding.progressbar.setVisibility(View.GONE);
                                            return;
                                        }

                                        if (iDs == null) {
                                            error = "لیست دریافت شده از کالاها نا معتبر می باشد";
                                            showError(error);
                                            binding.progressbar.setVisibility(View.GONE);
                                            return;
                                        }

                                        productList.clear();

                                        CollectionUtils.filter(iDs.getProductList(), i -> i.getPrice(sharedPreferences) > 0.0);
                                        ArrayList<Product> resultPrd_ = new ArrayList<>(iDs.getProductList());
                                        ArrayList<Product> listPrd = new ArrayList<>(resultPrd_);
                                        CollectionUtils.filter(listPrd, l -> l.getKey() != 0);
                                        if (listPrd.size() > 0)
                                            for (int i = 0; i < listPrd.size(); i++) {
                                                int position = listPrd.get(i).getKey() - 1;//new position
                                                int index = resultPrd_.indexOf(listPrd.get(i));//old position
                                                if (resultPrd_.size() > position) {
                                                    Product itemProduct = resultPrd_.get(position);
                                                    if (index != position) {
                                                        resultPrd_.set(position, resultPrd_.get(resultPrd_.indexOf(listPrd.get(i))));
                                                        resultPrd_.set(index, itemProduct);

                                                    }
                                                }
                                            }


                                        productListData.clear();
                                        productListData.addAll(resultPrd_);
                                        if (resultPrd_.size() > 0)
                                            for (int i = 0; i < 18; i++) {
                                                if (resultPrd_.size() > i)
                                                    productList.add(resultPrd_.get(i));
                                            }


                                        if (productList.size() == 0) {
                                            binding.orderTxtError.setVisibility(View.VISIBLE);
                                            binding.orderTxtError.setText("هیچ کالایی موجود نیست");
                                        }


                                        productAdapter.setMaxSale(maxSales);
                                        productAdapter.notifyDataSetChanged();


                                        binding.progressbar.setVisibility(View.GONE);


                                    }
                                    , throwable -> {
                                        error = "فروشگاه تعطیل می باشد.";
                                        showError(error);
                                        binding.progressbar.setVisibility(View.GONE);

                                    })
            );
        } catch (Exception e) {
            error = "خطا در اتصال به سرور برای دریافت کالاها";
            showError(error);
        }

    }


    private void getSettingPrice(String GUID) {

        try {
            compositeDisposable.add(
                    api.getSetting1(company.userName, company.passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelSetting>() {
                                }.getType();
                                ModelSetting iDs = null;


                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception ignore) {
                                }


                                if (iDs != null) {
                                    sharedPreferences.edit().putString("priceProduct", iDs.getSettings().get(0).DEFAULT_PRICE_INVOICE).apply();
                                    maxSales = iDs.getSettings().get(0).MAX_SALE;
                                    sharedPreferences.edit().putString("maxSale", maxSales).apply();

                                }
                                getProduct1(GUID);


                            }, throwable -> {

                            })
            );
        } catch (Exception e) {
            binding.progressbar.setVisibility(View.VISIBLE);
            error = "خطا در اتصال به سرور برای دریافت تنطیمات";
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }

    }


    private void getUnit() {
        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        try {
            compositeDisposable.add(
                    api.getUnitSync(company.userName, company.passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        Gson gson = new Gson();
                                        Type typeIDs = new TypeToken<ModelUnit>() {
                                        }.getType();
                                        ModelUnit iDs = null;


                                        try {
                                            iDs = gson.fromJson(jsonElement, typeIDs);
                                        } catch (Exception ignore) {
                                        }


                                        if (iDs != null && iDs.getUnit().size() > 0) {
                                            Unit.deleteAll(Unit.class);
                                            Unit.saveInTx(iDs.getUnit());

                                        }
                                    }
                                    , throwable -> {


                                    })
            );
        } catch (Exception ignored) {

        }

    }

    private void getDescription(String id) {
        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }

        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات...", false);

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


    private void showError(String error) {

        ivIconSync.setImageResource(company.imageDialog);
        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
        customProgress.hideProgress();

    }


    private void ShowErrorConnection() {
        textMessageDialog.setText("خطا در اتصال به اینترنت");
        ivIconSync.setImageResource(R.drawable.ic_wifi);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
        customProgress.hideProgress();

    }


    private void loadMore() {


        int pageSize = 18;
        isLoading = true;

        final ArrayList<Product> items = new ArrayList<>();
        totalPage = (productListData.size() + pageSize - 1) / pageSize;

        if (totalPage > 1) {

            int start = ((currentPage * pageSize) - (pageSize - 1)) - 1;
            int end = (min((start + 1) + pageSize - 1, productListData.size())) - 1;

            new Handler().postDelayed(() -> {
                try {

                    items.addAll(productListData.subList(start, end));
                    if (currentPage != 1) productAdapter.removeLoadingView();
                    productList.addAll(items);


                    productAdapter.notifyDataSetChanged();


                    if (currentPage < totalPage) {
                        productAdapter.addLoadingView();
                    } else {
                        isLastPage = true;
                    }

                    isLoading = false;
                } catch (Exception e) {

                    Toast.makeText(getActivity(), "لطفا صبر کنید...", Toast.LENGTH_SHORT).show();
                }

            }, 500);
        }
    }


    private static class JsonObjectAccount {
        public List<Account> Account;
    }

    private void addAccount(List<Account> accounts) {

        if (!networkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        try {
            customProgress.showProgress(getContext(), "در حال ثبت مشتری در سرور...", true);
            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            compositeDisposable.add(
                    api.addAccount(company.userName, company.passWord, gson.toJson(jsonObjectAcc, typeJsonObject), "")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                Gson gson1 = new Gson();
                                Type typeIDs = new TypeToken<ModelLog>() {
                                }.getType();
                                ModelLog iDs = gson1.fromJson(jsonElement, typeIDs);

                                assert iDs != null;
                                int message = iDs.getLogs().get(0).getMessage();
                                String description = iDs.getLogs().get(0).getDescription();
                                if (message == 1) {
                                    Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                    dialogAddAccount.dismiss();
                                    customProgress.hideProgress();
                                    Acc_GUID = accountsList.get(0).I;
                                    Acc_NAME = accountsList.get(0).N;
                                    binding.edtNameCustomer.removeTextChangedListener(textWatcherAcc);
                                    binding.edtNameCustomer.setText(accountsList.get(0).N);
                                    binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);
                                    accountsList.clear();


                                } else {
                                    customProgress.hideProgress();
                                    Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                }

                            }, throwable -> {
                                Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری", Toast.LENGTH_SHORT).show();
                                customProgress.hideProgress();

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ثبت مشتری", Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();
        }


    }

    private void getAccountSearch1(String word, int type) {
        try {
            compositeDisposable.add(
                    api.getAccountSearch1("saleinkit_api", company.userName, company.passWord, word)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();

                                Type typeIDs = new TypeToken<ModelAccount>() {
                                }.getType();
                                ModelAccount iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    return;

                                }

                                if (!chooseAccount) {
                                    assert iDs != null;
                                    if (iDs.getAccountList() != null) {
                                        if (type == 0) {
                                            accList.clear();
                                            accList.addAll(iDs.getAccountList());
                                            accAdapter.notifyDataSetChanged();
                                            binding.accountRecyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            Account.deleteAll(Account.class);
                                            accList.addAll(iDs.getAccountList());
                                            CollectionUtils.filter(accList, a -> a.I.equals(Acc_GUID));
                                            if (accList.size() > 0)
                                                Account.saveInTx(accList.get(0));
                                        }
                                    }
                                }

                                customProgress.hideProgress();

                            }, throwable -> {


                            })
            );
        } catch (Exception ignored) {

        }
    }


    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }


    private void getSetting() {

        try {
            compositeDisposable.add(
                    api.getSetting1(company.userName, company.passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelSetting>() {
                                }.getType();

                                ModelSetting iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    error = "مدل دریافت شده از تنظیمات نا معتبر است";
                                    showError(error);
                                    return;
                                }

                                if (iDs == null) {
                                    error = "لیست دریافت شده از تنظیمات نا معتبر می باشد";
                                    showError(error);
                                } else {


                                    List<Setting> settingsList = new ArrayList<>(iDs.getSettings());

                                    if (settingsList.size() > 0) {
                                        String Update = settingsList.get(0).UPDATE_APP;

                                        try {
                                            linkUpdate = settingsList.get(0).LINKUPDATE;
                                            if (linkUpdate==null)
                                                linkUpdate=company.linkUpdate;

                                        }catch (Exception ignored){
                                            linkUpdate=company.linkUpdate;
                                        }
                                        String NewVersion = settingsList.get(0).VERSION_APP;
                                        String AppVersion = "";
                                        try {
                                            AppVersion = appVersion();
                                        } catch (PackageManager.NameNotFoundException e) {
                                            e.printStackTrace();
                                        }


                                        sharedPreferences.edit().putString("Default_ACCOUNT", settingsList.get(0).DEFAULT_CUSTOMER).apply();
                                        sharedPreferences.edit().putString("Transport_GUID", settingsList.get(0).PEYK).apply();

                                        if (company.mode == 1) {
                                            Acc_GUID = settingsList.get(0).DEFAULT_CUSTOMER;
                                            Account account = new Account();
                                            account.I = Acc_GUID;
                                            Account.saveInTx(account);
                                            if (!EDIT)
                                                binding.edtNameCustomer.setHint("مشتری پیش فرض");
                                        }


                                        if (Update.equals("3") && !AppVersion.equals(NewVersion)) {
                                            textUpdate.setText("آپدیت جدید از برنامه موجود است.برای ادامه دادن  برنامه را از بازار آپدیت کنید.");
                                            btnNo.setVisibility(View.GONE);
                                            dialogUpdate.setCancelable(false);
                                            dialogUpdate.show();
                                        } else if (Update.equals("2") && !AppVersion.equals(NewVersion)) {
                                            textUpdate.setText("آپدیت جدید از برنامه موجود است.برای بهبود عملکرد  برنامه را  از بازار آپدیت کنید.");
                                            btnNo.setVisibility(View.VISIBLE);
                                            dialogUpdate.setCancelable(true);
                                            dialogUpdate.show();
                                        }





                                    }


                                }


                            }, throwable -> {
                                error = "خطای تایم اوت در دریافت تنظیمات";
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                            })
            );
        } catch (Exception e) {
            error = "خطا در اتصال به سرور برای دریافت تنطیمات";
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

        }


    }


    private Boolean networkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public Bundle reloadFragment(Boolean setAddress) {
        Bundle bundle = getArguments();
        bundle.putString("Inv_GUID", Inv_GUID);
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putBoolean("Seen", Seen);
        bundle.putString("Tbl_NAME", Tbl_NAME);
        bundle.putString("Ord_TYPE", Ord_TYPE);
        bundle.putString("Acc_NAME", Acc_NAME);
        bundle.putString("Acc_GUID", Acc_GUID);
        bundle.putBoolean("EDIT", EDIT);
        bundle.putBoolean("Seen", Seen);
        bundle.putBoolean("setARD1", setAddress);
        return bundle;
    }


    public Bundle getBundle(boolean SetARD1) {
        Bundle bundle = new Bundle();
        bundle.putString("Inv_GUID", Inv_GUID);
        bundle.putString("NAME", company.namePackage);
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putString("Tbl_NAME", Tbl_NAME);
        bundle.putString("Ord_TYPE", Ord_TYPE);
        bundle.putString("Acc_GUID", Acc_GUID);
        bundle.putString("Acc_NAME", Acc_NAME);
        bundle.putBoolean("EDIT", EDIT);
        bundle.putBoolean("Seen", Seen);


        if (!SetARD1) {
            if (typeAddress == 2)
                bundle.putBoolean("setADR1", true);
        } else
            bundle.putBoolean("setADR1", SetARD1);


        return bundle;
    }


    public void refreshProductList() {
        productAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        //setADR1 = false;
        super.onDestroyView();

        EDIT = false;
        compositeDisposable.dispose();
        binding = null;


    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }


}
