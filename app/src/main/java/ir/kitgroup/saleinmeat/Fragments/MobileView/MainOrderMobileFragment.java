package ir.kitgroup.saleinmeat.Fragments.MobileView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;

import ir.kitgroup.saleinmeat.Adapters.AccountAdapter;
import ir.kitgroup.saleinmeat.Adapters.DescriptionAdapter;

import ir.kitgroup.saleinmeat.Adapters.ProductAdapter1;
import ir.kitgroup.saleinmeat.Adapters.ProductLevel1Adapter;
import ir.kitgroup.saleinmeat.Adapters.ProductLevel2Adapter;
import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.classes.CustomProgress;
import ir.kitgroup.saleinmeat.classes.PaginationScrollListener;
import ir.kitgroup.saleinmeat.classes.RecyclerViewLoadMoreScroll;

import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.Invoice;

import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.DataBase.OrderType;
import ir.kitgroup.saleinmeat.DataBase.Product;
import ir.kitgroup.saleinmeat.DataBase.ProductGroupLevel1;
import ir.kitgroup.saleinmeat.DataBase.ProductGroupLevel2;
import ir.kitgroup.saleinmeat.DataBase.Setting;
import ir.kitgroup.saleinmeat.DataBase.User;

import ir.kitgroup.saleinmeat.models.Description;

import ir.kitgroup.saleinmeat.models.ModelAccount;
import ir.kitgroup.saleinmeat.models.ModelDesc;
import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.models.ModelProduct;
import ir.kitgroup.saleinmeat.models.ModelSetting;
import ir.kitgroup.saleinmeat.models.ModelTypeOrder;
import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.Util.Util;
import ir.kitgroup.saleinmeat.databinding.FragmentMobileOrderMainBinding;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.min;

public class MainOrderMobileFragment extends Fragment implements Filterable {

    //region Parameter

    private FragmentMobileOrderMainBinding binding;

    private SharedPreferences sharedPreferences;


    private String error;

    private CustomProgress customProgress;
    private String Ord_TYPE;
    private String Tbl_GUID;
    private String Inv_GUID;
    private String Inv_GUID_ORG;

    private String userName;
    private String passWord;


    private TextWatcher textWatcherProduct;
    private final Object mLock = new Object();
    private ArrayList<Product> mOriginalValues;
    private ArrayList<Product> emptyListProduct;
    private int emptyListProductFlag = 0;


    private ArrayList<ProductGroupLevel1> AllProductLevel1;
    private ArrayList<ProductGroupLevel1> productLevel1List;
    private ProductLevel1Adapter productLevel1Adapter;


    private ArrayList<ProductGroupLevel2> AllProductLevel2;
    private ArrayList<ProductGroupLevel2> productLevel2List;
    private ProductLevel2Adapter productLevel2Adapter;


    private ArrayList<Product> productList;
    private ArrayList<Product> productListData;
    private ArrayList<Product> allProductActive;

    private ProductAdapter1 productAdapter;
    private boolean isLastPage;
    private boolean isLoading;
    private int currentPage;
    private int totalPage;


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private ArrayList<Description> descriptionList;
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv;
    //endregion Variable Dialog Description


    //region Dialog
    private Dialog dialog;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog


    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private float sumPrice = 0;
    private float sumPurePrice = 0;


    private TextWatcher textWatcherAcc;
    private AccountAdapter accAdapter;
    private ArrayList<Account> accList;


    //region Variable DialogAddAccount
    private List<Account> accountsList;
    private Dialog dialogAddAccount;
    private EditText edtNameUser;
    private EditText edtAddressUser;
    private EditText edtMobileUser;
    private int gender;
    //endregion Variable DialogAddAccount

    private String Acc_GUID = "";
    private String Acc_NAME = "";

    private int counter;


    private Boolean firstSync = false;
    private Boolean showView = false;

    private int imageLogo;
    private int imgIconDialog;
    private int imgBackground = 0;
    private String nameCompany;

    private Integer time = 5;
    private String maxSales = "0";

    private String task = "2";


    private Dialog dialogUpdate;
    private TextView textUpdate;
    private MaterialButton btnNo;

    private String link;


    //endregion Parameter

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        showView = false;

        binding = FragmentMobileOrderMainBinding.inflate(getLayoutInflater());
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        customProgress = CustomProgress.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!LauncherActivity.name.equals("ir.kitgroup.salein"))
        firstSync = sharedPreferences.getBoolean("firstSync", false);

        if (firstSync)
            task = "1";
        else
            task = "2";

        //region Configuration Text Size
        int fontSize;
        if (LauncherActivity.screenInches >= 7)
            fontSize = 14;
        else
            fontSize = 12;


        binding.edtSearchProduct.setTextSize(fontSize);
        binding.nameCustomer.setTextSize(fontSize);
        binding.orderListTvRegister.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Set Icon And Title
        switch (LauncherActivity.name) {
            case "ir.kitgroup.salein":
                nameCompany = "سالین دمو";
                imageLogo = R.drawable.salein;
                imgIconDialog = R.drawable.saleinicon128;


                break;

            case "ir.kitgroup.saleintop":
                nameCompany = " تاپ کباب";
                imageLogo = R.drawable.top;
                imgBackground = R.drawable.top_pas;
                imgIconDialog = R.drawable.top_png;

                break;


            case "ir.kitgroup.saleinmeat":
                nameCompany = " گوشت دنیوی";
                imageLogo = R.drawable.goosht;
                imgBackground = R.drawable.donyavi_pas;
                imgIconDialog = R.drawable.meat_png;
                link = "https://b2n.ir/b37054";

                break;


            case "ir.kitgroup.saleinnoon":
                nameCompany = "کافه نون";
                imageLogo = R.drawable.noon;
                imgIconDialog = R.drawable.noon;

                break;
        }
        binding.ivIconCompany.setImageResource(imageLogo);
        binding.tvCompany.setText(nameCompany);
        if (imgBackground != 0)
            binding.image.setImageResource(imgBackground);
        //endregion Set Icon And Title


        //region Delete Invoice And InvoiceDetail is Not Necessary
        for (Invoice invoice : Select.from(Invoice.class).where("INVTOTALAMOUNT ='" + null + "'").list()) {
            Tables tb = Select.from(Tables.class).where("I ='" + invoice.TBL_UID + "'").first();
            if (tb == null || tb.SV == null || !tb.SV) {
                Invoice.deleteInTx(invoice);
                for (InvoiceDetail invoiceDetail : Select.from(InvoiceDetail.class).where("INVUID ='" + invoice.INV_UID + "'").list()) {

                    InvoiceDetail.deleteInTx(invoiceDetail);
                }
            }
        }
        //endregion Delete Invoice And InvoiceDetail is Not Necessary


        //region Set Amount 0.0 For Products
        ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
        CollectionUtils.filter(prdResult, p -> p.getAmount() > 0);
        if (prdResult.size() > 0) {
            for (int i = 0; i < prdResult.size(); i++) {
                Util.AllProduct.get(Util.AllProduct.indexOf(prdResult.get(i))).AMOUNT = 0.0;
            }
        }
        //endregion Set Amount 0.0 For Products


        //region Configuration Organization Application
        if (App.mode == 1) {
            binding.defineCompany.setVisibility(View.GONE);
            binding.layoutAccount.setVisibility(View.VISIBLE);
            binding.layoutSearchProduct.setVisibility(View.VISIBLE);
            binding.bottomNavigationViewLinear.setVisibility(View.GONE);

            //region Cast DialogAddAcc
            dialogAddAccount = new Dialog(getActivity());
            dialogAddAccount.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAddAccount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogAddAccount.setContentView(R.layout.dialog_add_account);
            dialogAddAccount.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dialogAddAccount.setCancelable(false);


            ImageView imgCloseAddDialog = dialogAddAccount.findViewById(R.id.iv_close_add_dialog);
            edtNameUser = dialogAddAccount.findViewById(R.id.edt_name_account);
            edtMobileUser = dialogAddAccount.findViewById(R.id.edt_mobile_account);
            edtAddressUser = dialogAddAccount.findViewById(R.id.edt_address_account);
            RadioButton radioMan = dialogAddAccount.findViewById(R.id.radioMan);
            RadioButton radioWoman = dialogAddAccount.findViewById(R.id.radioWoman);
            MaterialButton btnRegisterAccount = dialogAddAccount.findViewById(R.id.btn_register_account);
            radioMan.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    gender = 0;
                }
            });
            radioWoman.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    gender = 1;
                }
            });

            imgCloseAddDialog.setOnClickListener(v -> dialogAddAccount.dismiss());


            btnRegisterAccount.setOnClickListener(v -> {
                if (edtNameUser.getText().toString().equals("") || edtMobileUser.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "لطفا فیلد نام مشتری و شماره موبایل مشتری را پر کنید.", Toast.LENGTH_SHORT).show();
                } else if (!edtMobileUser.getText().toString().equals("1") && (edtMobileUser.getText().toString().length() < 11 || edtMobileUser.getText().toString().length() > 11)) {
                    Toast.makeText(getActivity(), "شماره موبایل صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                } else {

                    Account account = new Account();
                    account.I = UUID.randomUUID().toString();
                    account.N = edtNameUser.getText().toString();
                    account.M = edtMobileUser.getText().toString();
                    account.ADR = edtAddressUser.getText().toString();
                    account.S = String.valueOf(gender);
                    accountsList.clear();
                    accountsList.add(account);
                    addAccount(userName, passWord, accountsList);

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

                    Acc_GUID = "";
                    Acc_NAME = "";

                    if (s.toString().isEmpty()) {
                        binding.accountRecyclerView.setVisibility(View.GONE);
                        binding.nameCustomer.setHint("نام مشترک");
                    } else {

                        getAccountSearch(s.toString());
                    }


                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            accAdapter.setOnClickItemListener((GUID, name) -> {
                Account.deleteAll(Account.class);
                Account account = new Account();
                account.I = GUID;
                account.N = name;
                Account.saveInTx(account);
                Acc_GUID = GUID;
                Acc_NAME = name;
                binding.nameCustomer.removeTextChangedListener(textWatcherAcc);
                binding.nameCustomer.setText(name);
                binding.accountRecyclerView.setVisibility(View.GONE);
                binding.nameCustomer.addTextChangedListener(textWatcherAcc);
            });


            binding.btnAddAccount.setOnClickListener(v -> {
                edtNameUser.setText("");
                edtAddressUser.setText("");
                edtMobileUser.setText("");
                dialogAddAccount.show();
            });


            binding.nameCustomer.addTextChangedListener(textWatcherAcc);
        }
        //endregion Configuration Organization Application


        //region Configuration Client Application

        else {

            binding.defineCompany.setVisibility(View.VISIBLE);
            binding.layoutAccount.setVisibility(View.GONE);
            binding.layoutSearchProduct.setVisibility(View.VISIBLE);
            binding.btnRegisterOrder.setVisibility(View.GONE);

            Acc_NAME = Select.from(Account.class).first().N;
            Acc_GUID = Select.from(Account.class).first().I;

            binding.bottomNavigationViewLinear.setSelectedItemId(R.id.homee);
            binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setBackgroundColor(getActivity().getResources().getColor(R.color.red_table));
            binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
            binding.bottomNavigationViewLinear.setOnNavigationItemSelectedListener(item -> {

                List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                //region Delete Item Transport For Show Counter
                if (invDetails.size() > 0) {
                    for (int i = 0; i < invDetails.size(); i++) {
                        if (invDetails.get(i).INV_DET_DESCRIBTION != null && invDetails.get(i).INV_DET_DESCRIBTION.equals("توزیع")) {
                            invDetails.remove(invDetails.get(i));
                        }
                    }
                    counter = invDetails.size();
                    binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);
                    if (App.mode == 1)
                        binding.btnRegisterOrder.setVisibility(View.VISIBLE);
                }
                //endregion Delete Item Transport For Show Counter

                else
                    binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();


                //region Delete Layout In Fragment When Going To MainOrderFragment For Buy Not Edit
                int size = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                if (LauncherActivity.name.equals("ir.kitgroup.salein") && size > 0)
                    size = size - 1;

                if (Inv_GUID_ORG.equals("")) {
                    for (int i = 1; i <= size; i++) {
                        getFragmentManager().popBackStack();
                    }
                }

                //endregion Delete Layout In Fragment When Going To MainOrderFragment For Buy Not Edit


                switch (item.getItemId()) {
                    case R.id.homee:


                        if (!Inv_GUID_ORG.equals("")) {
                            getFragmentManager().popBackStack();
                        }
                        productAdapter.notifyDataSetChanged();
                        return true;


                    case R.id.orders:
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "2");//go to InVoiceDetailMobileFragment for register order first time
                        bundle.putString("Inv_GUID", Inv_GUID);
                        bundle.putString("Tbl_GUID", Tbl_GUID);
                        bundle.putString("Ord_TYPE", Ord_TYPE);
                        bundle.putString("Acc_Name", Acc_NAME);
                        bundle.putString("Acc_GUID", Acc_GUID);
                        if (!Inv_GUID_ORG.equals(""))
                            bundle.putBoolean("EDIT", true);

                        InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
                        inVoiceDetailFragmentMobile.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailF").commit();

                        return true;


                    case R.id.profile:

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_mobile, new SettingFragment()).addToBackStack("SettingF").commit();
                        return true;
                }


                return false;
            });


        }

        //endregion Configuration Client Application


        //region First Value Parameter
        counter = 0;
        Inv_GUID = "";
        Inv_GUID_ORG = "";

        emptyListProduct = new ArrayList<>();
        allProductActive = new ArrayList<>();
        descriptionList = new ArrayList<>();
        accountsList = new ArrayList<>();

        AllProductLevel1 = new ArrayList<>();
        productLevel1List = new ArrayList<>();


        AllProductLevel2 = new ArrayList<>();
        productLevel2List = new ArrayList<>();


        productList = new ArrayList<>();
        productListData = new ArrayList<>();


        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
            productLevel1Adapter.notifyDataSetChanged();
            productLevel2Adapter.notifyDataSetChanged();
        }

        isLastPage = false;
        isLoading = false;
        currentPage = 1;

        error = "";

        Ord_TYPE = "";
        Tbl_GUID = "";


        maxSales = "0";
        List<Setting> setting = Select.from(Setting.class).list();
        if (setting.size() > 0)
            maxSales = setting.get(0).MAX_SALE;


        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;


        //endregion First Value Parameter


        //region Get Bundle
        Bundle bnd = getArguments();
        assert bnd != null;
        Ord_TYPE = bnd.getString("Ord_TYPE");
        Tbl_GUID = bnd.getString("Tbl_GUID");
        Inv_GUID = bnd.getString("Inv_GUID");
        Inv_GUID_ORG = bnd.getString("Inv_GUID");
        //endregion Get Bundle


        //region Create Order


        //region Delete Invoice And Invoice Detail Not Necessary exception NotSuccessfulOrder
        List<Invoice> invoicese = Select.from(Invoice.class).list();
        CollectionUtils.filter(invoicese, i -> i.INV_SYNC.equals("@") && i.INV_EXTENDED_AMOUNT == null);
        if (invoicese.size() > 0) {
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).list();
            CollectionUtils.filter(invoiceDetails, i -> i.INV_UID.equals(invoicese.get(0).INV_UID));
            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }
            for (int i = 0; i < invoicese.size(); i++) {
                Invoice.delete(invoicese.get(i));
            }
        }
        //endregion Delete Invoice And Invoice Detail Not Necessary exception NotSuccessfulOrder


        if (Inv_GUID.equals("")) {
            Inv_GUID = UUID.randomUUID().toString();
            Invoice order = new Invoice();
            order.INV_UID = Inv_GUID;
            order.INV_SYNC = "@";
            Invoice.save(order);


        } else {
            binding.bottomNavigationViewLinear.getMenu().getItem(0).setVisible(false);
        }


        //endregion Create Order


        //region Cast Variable Dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textMessageDialog = dialog.findViewById(R.id.tv_message);
        ImageView ivIcon = dialog.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imgIconDialog);
        btnOkDialog = dialog.findViewById(R.id.btn_ok);
        btnNoDialog = dialog.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> dialog.dismiss());


        btnOkDialog.setOnClickListener(v -> {
            dialog.dismiss();

            getProduct(task);

        });


        //endregion Cast Variable Dialog


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
            ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
            InvoiceDetail invDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + GuidInv + "'").first();
            if (invDetail != null) {
                invDetail.INV_DET_DESCRIBTION = edtDescriptionItem.getText().toString();
                invDetail.update();
                /*   if (LauncherActivity.screenInches < 7) {*/
                resultPrd.addAll(Util.AllProduct);
                CollectionUtils.filter(resultPrd, r -> r.I.equals(invDetail.PRD_UID));
                //  }

            }

            if (resultPrd.size() > 0) {

                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).descItem = edtDescriptionItem.getText().toString();
                productAdapter.notifyDataSetChanged();
            }

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
            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
            isLastPage = false;
            currentPage = 1;
            binding.orderTxtError.setText("");
            binding.edtSearchProduct.removeTextChangedListener(textWatcherProduct);
            binding.edtSearchProduct.setText("");
            binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);


            //region UnClick Old Item ProductLevel1 And Item ProductLevel2
            ArrayList<ProductGroupLevel1> resultPrdGrp1 = new ArrayList<>(productLevel1List);
            CollectionUtils.filter(resultPrdGrp1, r -> r.Click);
            if (resultPrdGrp1.size() > 0) {
                productLevel1List.get(productLevel1List.indexOf(resultPrdGrp1.get(0))).Click = false;
            }
            ArrayList<ProductGroupLevel2> resultPrdGrp2 = new ArrayList<>(AllProductLevel2);
            CollectionUtils.filter(resultPrdGrp2, r -> r.Click);
            if (resultPrdGrp2.size() > 0 && productLevel2List.size() > 0) {
                productLevel2List.get(productLevel2List.indexOf(resultPrdGrp2.get(0))).Click = false;
            }
            //endregion UnClick Old Item ProductLevel1 And Item ProductLevel2


            //region Click New Item ProductLevel1
            ArrayList<ProductGroupLevel1> resultPrdGroup1_ = new ArrayList<>(productLevel1List);
            CollectionUtils.filter(resultPrdGroup1_, r -> r.getPRDLVLUID().equals(GUID));
            if (resultPrdGroup1_.size() > 0) {
                productLevel1List.get(productLevel1List.indexOf(resultPrdGroup1_.get(0))).Click = true;
            }
            //endregion Click New Item ProductLevel1


            productLevel1Adapter.notifyDataSetChanged();


            //region Full ProductLevel2List Because This Item ProductLevel1 Is True
            ArrayList<ProductGroupLevel2> resultPrdGrp2_ = new ArrayList<>(AllProductLevel2);
            CollectionUtils.filter(resultPrdGrp2_, r -> r.getPRDLVLPARENTUID().equals(GUID));
            if (resultPrdGrp2_.size() > 0) {


                ArrayList<ProductGroupLevel2> tempPrdLvl2 = new ArrayList<>();


                for (int i = 0; i < resultPrdGrp2_.size(); i++) {
                    int finalI = i;
                    ArrayList<Product> tempPrd = new ArrayList<>(Util.AllProduct);
                    CollectionUtils.filter(tempPrd, tp -> tp.getPRDLVLUID2().equals(resultPrdGrp2_.get(finalI).getPRDLVLUID()) && tp.getPRDPRICEPERUNIT1() > 0 && tp.STS);
                    if (tempPrd.size() > 0)

                        tempPrdLvl2.add(resultPrdGrp2_.get(i));
                }


                productLevel2List.clear();
                productLevel2List.addAll(tempPrdLvl2);

                ArrayList<ProductGroupLevel2> list = new ArrayList<>(productLevel2List);
                CollectionUtils.filter(list, l -> l.TKN != 0);
                if (list.size() > 0)
                    for (int i = 0; i < list.size(); i++) {
                        int position = list.get(i).TKN - 1;//new position
                        int index = productLevel2List.indexOf(list.get(i));//old position
                        if (productLevel2List.size() > position) {
                            ProductGroupLevel2 itemProductGroupLevel2 = productLevel2List.get(position);
                            if (index != position) {
                                productLevel2List.set(position, productLevel2List.get(productLevel2List.indexOf(list.get(i))));
                                productLevel2List.set(index, itemProductGroupLevel2);

                            }
                        }

                    }


                if (tempPrdLvl2.size() > 0) {
                    productLevel2List.get(0).Click = true;

                }

                productLevel2Adapter.notifyDataSetChanged();


                //region Full ProductList Because First Item ProductLevel2 Is True
                ArrayList<Product> resultPrd_ = new ArrayList<>(Util.AllProduct);
                CollectionUtils.filter(resultPrd_, r -> r.getPRDLVLUID2().equals(productLevel2List.get(0).getPRDLVLUID()) && r.getPRDPRICEPERUNIT1() > 0 && r.STS);

                if (resultPrd_.size() == 0) {
                    ArrayList<ProductGroupLevel1> rst = new ArrayList<>(AllProductLevel1);
                    CollectionUtils.filter(rst, r -> r.getPRDLVLUID().equals(productLevel2List.get(0).getPRDLVLPARENTUID()));

                    if (rst.size() > 0) {
                        binding.orderTxtError.setText("هیچ نوع از " + productLevel2List.get(0).getPRDLVLNAME() + " " + "در دسته " + rst.get(0).getPRDLVLNAME() + " " + "موجود نمی باشد. ");

                    }
                }

                productList.clear();
                productListData.clear();


                ArrayList<Product> listPrd = new ArrayList<>(resultPrd_);
                CollectionUtils.filter(listPrd, l -> l.KEY != 0);
                if (listPrd.size() > 0)
                    for (int i = 0; i < listPrd.size(); i++) {
                        int position = listPrd.get(i).KEY - 1;//new position
                        int index = resultPrd_.indexOf(listPrd.get(i));//old position
                        if (resultPrd_.size() > position) {
                            Product itemProduct = resultPrd_.get(position);
                            if (index != position) {
                                resultPrd_.set(position, resultPrd_.get(resultPrd_.indexOf(listPrd.get(i))));
                                resultPrd_.set(index, itemProduct);

                            }
                        }
                    }

                productListData.addAll(resultPrd_);

                for (int i = 0; i < 18; i++) {
                    if (resultPrd_.size() > i) {
                        productList.add(resultPrd_.get(i));

                    }

                }


                productAdapter.notifyDataSetChanged();
                //endregion Full ProductList Because First Item ProductLevel2 Is True

            } else {
                ArrayList<ProductGroupLevel1> rst = new ArrayList<>(AllProductLevel1);
                CollectionUtils.filter(rst, r -> r.getPRDLVLUID().equals(GUID));

                if (rst.size() > 0) {
                    binding.orderTxtError.setText("هیچ زیر دسته ای برای دسته " + rst.get(0).getPRDLVLNAME() + " وجود ندارد.");

                }

                productList.clear();
                productAdapter.notifyDataSetChanged();
                productLevel2List.clear();
                productLevel2Adapter.notifyDataSetChanged();
            }
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

            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
            isLastPage = false;
            currentPage = 1;
            binding.orderTxtError.setText("");
            binding.edtSearchProduct.removeTextChangedListener(textWatcherProduct);
            binding.edtSearchProduct.setText("");
            binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);

            //region UnClick Old Item
            ArrayList<ProductGroupLevel2> resultProductGroupLevel2 = new ArrayList<>(AllProductLevel2);
            CollectionUtils.filter(resultProductGroupLevel2, r -> r.Click);

            if (resultProductGroupLevel2.size() > 0 && productLevel2List.size() > 0) {

                productLevel2List.get(productLevel2List.indexOf(resultProductGroupLevel2.get(0))).Click = false;
            }
            //endregion UnClick Old Item


            //region Click New Item
            ArrayList<ProductGroupLevel2> resProductGroupLevel2 = new ArrayList<>(productLevel2List);
            CollectionUtils.filter(resProductGroupLevel2, r -> r.getPRDLVLUID().equals(GUID));

            if (resProductGroupLevel2.size() > 0) {

                productLevel2List.get(productLevel2List.indexOf(resProductGroupLevel2.get(0))).Click = true;
            }
            //endregion Click New Item

            productLevel2Adapter.notifyDataSetChanged();

            //region Full ProductList Because This Item ProductLevel1 Is True
            ArrayList<Product> rstProduct = new ArrayList<>(Util.AllProduct);
            CollectionUtils.filter(rstProduct, r -> r.getPRDLVLUID2().equals(GUID) && r.getPRDPRICEPERUNIT1() > 0 && r.STS);
            if (rstProduct.size() == 0) {
                ArrayList<ProductGroupLevel1> rstProductGroupLevel1s = new ArrayList<>();
                ArrayList<ProductGroupLevel2> rstProductGroupLevel2s = new ArrayList<>(AllProductLevel2);
                CollectionUtils.filter(rstProductGroupLevel2s, r -> r.getPRDLVLUID().equals(GUID));

                if (rstProductGroupLevel2s.size() > 0) {
                    rstProductGroupLevel1s.addAll(AllProductLevel1);
                    CollectionUtils.filter(rstProductGroupLevel1s, r -> r.getPRDLVLUID().equals(rstProductGroupLevel2s.get(0).getPRDLVLPARENTUID()));

                }
                if (rstProductGroupLevel1s.size() > 0) {
                    binding.orderTxtError.setText("هیچ نوع از " + rstProductGroupLevel2s.get(0).getPRDLVLNAME() + " " + "در دسته " + rstProductGroupLevel1s.get(0).getPRDLVLNAME() + " " + "موجود نمی باشد. ");
                }
            }

            productList.clear();
            productListData.clear();

            ArrayList<Product> listPrd = new ArrayList<>(rstProduct);
            CollectionUtils.filter(listPrd, l -> l.KEY != 0);

            if (listPrd.size() > 0) {

                for (int i = 0; i < listPrd.size(); i++) {

                    int position = listPrd.get(i).KEY - 1;//new position
                    int index = rstProduct.indexOf(listPrd.get(i));//old position

                    if (rstProduct.size() > position) {

                        Product itemProduct = rstProduct.get(position);

                        if (index != position) {

                            rstProduct.set(position, rstProduct.get(rstProduct.indexOf(listPrd.get(i))));
                            rstProduct.set(index, itemProduct);
                        }
                    }
                }
            }

            productListData.addAll(rstProduct);

            for (int i = 0; i < 18; i++) {

                if (rstProduct.size() > i) {

                    productList.add(rstProduct.get(i));
                }
            }

            productAdapter.notifyDataSetChanged();
            //endregion Full ProductList Because This Item ProductLevel1 Is True
        });

        //endregion Click Item ProductLevel2

        //endregion CONFIGURATION DATA PRODUCT_LEVEL_2


        //region CONFIGURATION DATA PRODUCT

        //region Cast Product Configuration
        textWatcherProduct = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchProduct(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);
        //endregion Cast Product Configuration

        productAdapter = new ProductAdapter1(getActivity(), productList, maxSales, Inv_GUID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        binding.orderRecyclerViewProduct.setLayoutManager(linearLayoutManager);
        binding.orderRecyclerViewProduct.setScrollingTouchSlop(View.FOCUS_LEFT);
        RecyclerViewLoadMoreScroll scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
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


        binding.orderRecyclerViewProduct.addOnScrollListener(scrollListener);
        binding.orderRecyclerViewProduct.setAdapter(productAdapter);
        productAdapter.setOnClickListener(() -> {
            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() > 0) {
                //  binding.itemInvoiceDetail.setBadgeText(String.valueOf(invDetails.size()));
                counter = invDetails.size();
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);
                if (App.mode == 1)
                    binding.btnRegisterOrder.setVisibility(View.VISIBLE);
            } else
                // binding.itemInvoiceDetail.setBadgeText(null);
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();

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
                    getDescription(userName, passWord, GUID);

                }
            } else {
                Toast.makeText(getActivity(), "برای نوشتن توضیحات برای کالا مقدار ثبت کنید.", Toast.LENGTH_SHORT).show();
            }
        });

        productAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();


            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);


            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                sumPrice = 0;
                sumPurePrice = 0;

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                for (int i = 0; i < invoiceDetails.size(); i++) {
                    sumPrice = (float) (sumPrice + invoiceDetails.get(i).INV_DET_QUANTITY
                            * Float.parseFloat(invoiceDetails.get(i).INV_DET_PRICE_PER_UNIT));
                    sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetails.get(i).INV_DET_TOTAL_AMOUNT);
                }


                binding.orderListTvRegister.setText("تکمیل سفارش " + "(" + format.format(sumPurePrice) + " ریال " + ")");


            }
        });

        //endregion CONFIGURATION DATA PRODUCT


        //region Action BtnRegister
        binding.btnRegisterOrder.setOnClickListener(view1 -> {

            if (App.mode == 1 && Acc_GUID.equals("")) {

                Toast.makeText(getActivity(), "مشتری را انتخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            if (invDetails.size() == 0) {

                Toast.makeText(getActivity(), "هیچ سفارشی ندارید", Toast.LENGTH_SHORT).show();
            } else {

                Bundle bundle = new Bundle();
                bundle.putString("type", "2");//go to InVoiceDetailMobileFragment for register order first time
                bundle.putString("Inv_GUID", Inv_GUID);
                bundle.putString("Tbl_GUID", Tbl_GUID);
                bundle.putString("Ord_TYPE", Ord_TYPE);
                bundle.putString("Acc_Name", Acc_NAME);
                bundle.putString("Acc_GUID", Acc_GUID);
                if (!Inv_GUID_ORG.equals(""))
                    bundle.putBoolean("EDIT", true);


                InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
                inVoiceDetailFragmentMobile.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobile").commit();


            }

        });
        //endregion Action BtnRegister


        //region Cast Dialog Update
        dialogUpdate = new Dialog(getActivity());
        dialogUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogUpdate.setContentView(R.layout.custom_dialog);


        textUpdate = dialogUpdate.findViewById(R.id.tv_message);
        ivIcon = dialogUpdate.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imgIconDialog);


        MaterialButton btnOk = dialogUpdate.findViewById(R.id.btn_ok);
        btnOk.setText("آپدیت");
        btnNo = dialogUpdate.findViewById(R.id.btn_cancel);
        btnNo.setText("بعدا");


        btnNo.setOnClickListener(v -> {
            dialogUpdate.dismiss();

        });


        btnOk.setOnClickListener(v -> {
            dialogUpdate.dismiss();
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


        });
        //endregion Cast Dialog Update

        getProduct(task);

        return binding.getRoot();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (emptyListProductFlag == 0) {
                    emptyListProductFlag = 1;
                    emptyListProduct.clear();
                    emptyListProduct.addAll(productListData);
                }

                FilterResults filterResults = new FilterResults();
                ArrayList<Product> tempList = new ArrayList<>();

                if (mOriginalValues == null) {
                    synchronized (mLock) {

                        mOriginalValues = new ArrayList<>(allProductActive);
                    }
                }

                if (!constraint.toString().isEmpty()) {

                    String[] tempSearch = constraint.toString().trim().split("");


                    int searchSize = tempSearch.length;
                  /*  for (String searchItem : tempSearch) {
                       ArrayList<Product> result = new ArrayList<>(mOriginalValues);
                        CollectionUtils.filter(result, r -> r.N.contains(searchItem));
                        if (result.size() > 0) {
                            counter++;
                        }

                        if (counter == searchSize)
                            tempList.clear();
                        tempList.addAll(result);
                    }*/

                    for (Product item : mOriginalValues) {

                        int counter = 0;
                        for (String searchItem : tempSearch) {

                            if (item.N.contains(searchItem)) {
                                counter++;
                            }
                        }

                        if (counter == searchSize)
                            tempList.add(item);
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();


                } else {
                    synchronized (mLock) {
                        emptyListProductFlag = 0;
                        productList.clear();
                        productListData.clear();
                        productListData.addAll(emptyListProduct);
                        emptyListProduct.clear();
                        tempList = new ArrayList<>();
                        isLastPage = false;
                        currentPage = 1;
                        for (int i = 0; i < productListData.size() - 1; i++) {

                            tempList.add(productListData.get(i));

                        }

                    }

                }
                filterResults.values = tempList;
                filterResults.count = tempList.size();

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {


                ArrayList<Product> tempResult = (ArrayList<Product>) results.values;
                productList.clear();

                if (tempResult != null) {
                    productListData.clear();
                    ArrayList<Product> listPrd = new ArrayList<>(tempResult);
                    CollectionUtils.filter(listPrd, l -> l.KEY != 0);
                    if (listPrd.size() > 0)
                        for (int i = 0; i < listPrd.size(); i++) {
                            int position = listPrd.get(i).KEY - 1;//new position
                            int index = tempResult.indexOf(listPrd.get(i));//old position
                            if (tempResult.size() > position) {
                                Product itemProduct = tempResult.get(position);
                                if (index != position) {
                                    tempResult.set(position, tempResult.get(tempResult.indexOf(listPrd.get(i))));
                                    tempResult.set(index, itemProduct);

                                }
                            }
                        }


                    productListData.addAll(tempResult);
                }
                for (int i = 0; i < 18; i++) {
                    assert tempResult != null;
                    if (tempResult.size() > i) {
                        productList.add(tempResult.get(i));

                    }
                }


                productAdapter.notifyDataSetChanged();

            }
        };
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        productList.clear();
        descriptionList.clear();
        productLevel1List.clear();
        productListData.clear();

        productAdapter = null;
    }


    @SuppressLint("StaticFieldLeak")
    private void getProducts() {

        new AsyncTask() {


            @Override
            protected void onPreExecute() {


                super.onPreExecute();
            }

            @SuppressLint({"SetTextI18n", "StaticFieldLeak"})
            @Override
            protected void onPostExecute(Object o) {


                if (Util.AllProduct.size() == 0) {
                    Util.AllProduct.addAll(Select.from(Product.class).list());
                    ArrayList<Product> prdList = new ArrayList<>(Util.AllProduct);
                    CollectionUtils.filter(prdList, p -> p.N.contains("توزیع"));
                    if (prdList.size() > 0)
                        Util.TransportId = prdList.get(0).I;

                    CollectionUtils.filter(Util.AllProduct, p -> !p.N.contains("توزیع"));
                }

                if (AllProductLevel1.size() == 0) {
                    AllProductLevel1.addAll(Select.from(ProductGroupLevel1.class).list());
                }

                if (AllProductLevel2.size() == 0) {
                    AllProductLevel2.addAll(Select.from(ProductGroupLevel2.class).list());
                }

                productLevel1List.clear();
                for (int i = 0; i < AllProductLevel1.size(); i++) {

                    ArrayList<ProductGroupLevel2> resultPrdGrp2 = new ArrayList<>(AllProductLevel2);
                    int finalI = i;
                    CollectionUtils.filter(resultPrdGrp2, r -> r.getPRDLVLPARENTUID().equals(AllProductLevel1.get(finalI).getPRDLVLUID()));


                    if (resultPrdGrp2.size() > 0) {

                        ArrayList<ProductGroupLevel2> tempPrdLvl2 = new ArrayList<>();


                        for (int j = 0; j < resultPrdGrp2.size(); j++) {
                            ArrayList<Product> tempPrd = new ArrayList<>(Util.AllProduct);
                            int finalJ = j;
                            CollectionUtils.filter(tempPrd, tp -> tp.getPRDLVLUID2().equals(resultPrdGrp2.get(finalJ).getPRDLVLUID()) && tp.getPRDPRICEPERUNIT1() > 0 && tp.STS);
                            if (tempPrd.size() > 0)

                                tempPrdLvl2.add(resultPrdGrp2.get(j));
                        }


                        if (tempPrdLvl2.size() > 0) {
                            productLevel1List.add(AllProductLevel1.get(i));
                        }

                    }


                }
                if (productLevel1List.size() <= 1) {
                    binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE
                    );
                }
                productLevel1Adapter.notifyDataSetChanged();
                ArrayList<ProductGroupLevel1> rst1 = new ArrayList<>(productLevel1List);
                CollectionUtils.filter(rst1, r -> r.Click);
                if (rst1.size() > 0) {
                    for (int i = 0; i < rst1.size(); i++) {
                        productLevel1List.get(productLevel1List.indexOf(rst1.get(i))).Click = false;
                    }
                }
                if (productLevel1List.size() > 0)
                    productLevel1List.get(0).Click = true;

                //region full ProductLevel2List because First Item ProductLevel1 Is True


                ArrayList<ProductGroupLevel2> resultPrdGrp2 = new ArrayList<>(AllProductLevel2);
                if (productLevel1List.size() > 0)
                    CollectionUtils.filter(resultPrdGrp2, r -> r.getPRDLVLPARENTUID().equals(productLevel1List.get(0).getPRDLVLUID()));
                productLevel2List.clear();


                if (resultPrdGrp2.size() > 0) {
                    for (int i = 0; i < resultPrdGrp2.size(); i++) {
                        ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                        int finalI = i;

                        CollectionUtils.filter(resultPrd, r -> r.getPRDLVLUID2().equals(resultPrdGrp2.get(finalI).getPRDLVLUID()) && r.getPRDPRICEPERUNIT1() > 0 && r.STS);
                        if (resultPrd.size() > 0) {
                            productLevel2List.add(resultPrdGrp2.get(i));
                        }
                    }


                    ArrayList<ProductGroupLevel2> list = new ArrayList<>();
                    list.addAll(productLevel2List);
                    CollectionUtils.filter(list, l -> l.TKN != 0);
                    if (list.size() > 0)
                        for (int i = 0; i < list.size(); i++) {
                            int position = list.get(i).TKN - 1;//new position
                            int index = productLevel2List.indexOf(list.get(i));//old position
                            if (productLevel2List.size() > position) {
                                ProductGroupLevel2 itemProductGroupLevel2 = productLevel2List.get(position);

                                if (index != position) {
                                    productLevel2List.set(position, productLevel2List.get(productLevel2List.indexOf(list.get(i))));
                                    productLevel2List.set(index, itemProductGroupLevel2);

                                }
                            }

                        }
                    ArrayList<ProductGroupLevel2> rst2 = new ArrayList<>(productLevel2List);
                    CollectionUtils.filter(rst2, r -> r.Click);
                    if (rst2.size() > 0) {
                        for (int i = 0; i < rst2.size(); i++) {
                            productLevel2List.get(productLevel2List.indexOf(rst2.get(i))).Click = false;
                        }
                    }
                    if (productLevel2List.size() > 0)
                        productLevel2List.get(0).Click = true;
                    productLevel2Adapter.notifyDataSetChanged();
                    ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                    CollectionUtils.filter(resultPrd, r -> r.getPRDLVLUID2().equals(productLevel2List.get(0).getPRDLVLUID()) && r.getPRDPRICEPERUNIT1() > 0 && r.STS);


                    if (resultPrd.size() == 0) {

                        ArrayList<ProductGroupLevel1> rst = new ArrayList<>(AllProductLevel1);
                        if (productLevel2List.size() > 0)
                            CollectionUtils.filter(rst, r -> r.getPRDLVLUID().equals(productLevel2List.get(0).getPRDLVLPARENTUID()));

                        if (rst.size() > 0) {
                            if (productLevel2List.size() > 0)
                                binding.orderTxtError.setText("هیچ نوع از " + productLevel2List.get(0).getPRDLVLNAME() + " " + "در دسته " + rst.get(0).getPRDLVLNAME() + " " + "موجود نمی باشد. ");

                        }
                    }


                    //change by me
                    // AllProductLevel2.clear();
                    //AllProductLevel2.addAll(productLevel2List);


                    ArrayList<Product> resultPrdAc = new ArrayList<>(Util.AllProduct);
                    CollectionUtils.filter(resultPrdAc, r -> r.getPRDPRICEPERUNIT1() > 0 && r.STS);
                    allProductActive.clear();
                    allProductActive.addAll(resultPrdAc);
                    productListData.clear();

                    ArrayList<Product> listPrd = new ArrayList<>(resultPrd);
                    CollectionUtils.filter(listPrd, l -> l.KEY != 0);
                    if (listPrd.size() > 0)
                        for (int i = 0; i < listPrd.size(); i++) {
                            int position = listPrd.get(i).KEY - 1;//new position
                            int index = resultPrd.indexOf(listPrd.get(i));//old position
                            if (resultPrd.size() > position) {
                                Product itemProduct = resultPrd.get(position);
                                if (index != position) {
                                    resultPrd.set(position, resultPrd.get(resultPrd.indexOf(listPrd.get(i))));
                                    resultPrd.set(index, itemProduct);

                                }
                            }
                        }

                    productListData.addAll(resultPrd);


                    productList.clear();
                    for (int i = 0; i < 18; i++) {
                        if (resultPrd.size() > i) {
                            productList.add(resultPrd.get(i));

                        }
                    }


                    // productList.addAll(resultPrd);

                    //edit Order
                    if (!Inv_GUID_ORG.equals("")) {
                        ArrayList<Product> result = new ArrayList<>(Util.AllProduct);
                        CollectionUtils.filter(result, R -> R.getAmount() > 0);

                        if (result.size() > 0) {
                            Util.AllProduct.get(Util.AllProduct.indexOf(result.get(0))).AMOUNT = 0.0;
                        }


                        Invoice ord = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                        List<InvoiceDetail> invoicedetails = Select.from(InvoiceDetail.class).where("INVUID = '" + ord.INV_UID + "'").list();
                        if (invoicedetails.size() > 0) {
                            // binding.itemInvoiceDetail.setBadgeText(String.valueOf(invoicedetails.size()));

                            for (int i = 0; i < invoicedetails.size(); i++) {
                                if (invoicedetails.get(i).INV_DET_DESCRIBTION != null && invoicedetails.get(i).INV_DET_DESCRIBTION.equals("توزیع")) {

                                    invoicedetails.remove(invoicedetails.get(i));

                                }
                            }
                            counter = invoicedetails.size();
                            binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);
                        } else
                            // binding.itemInvoiceDetail.setBadgeText(null);

                            binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();


                        for (int i = 0; i < invoicedetails.size(); i++) {

                            ArrayList<Product> resultProducts = new ArrayList<>(Util.AllProduct);
                            int finalI = i;

                            //  List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

                            CollectionUtils.filter(resultProducts, r -> r.getPRDUID().equals(invoicedetails.get(finalI).PRD_UID) && r.STS);
                            if (resultProducts.size() > 0) {
                                Double amount = (Util.AllProduct.get(Util.AllProduct.indexOf(resultProducts.get(0))).getAmount() + invoicedetails.get(finalI).INV_DET_QUANTITY);
                                Util.AllProduct.get(Util.AllProduct.indexOf(resultProducts.get(0))).setAmount(amount);
                                // productAdapter.notifyItemChanged(productList.indexOf(resultProducts.get(0)));


                                if (App.mode == 1)
                                    binding.btnRegisterOrder.setVisibility(View.VISIBLE);


                            }
                        }


                    }
                    productAdapter.notifyDataSetChanged();


                } else {

                    ArrayList<ProductGroupLevel1> rst = new ArrayList<>(AllProductLevel1);
                    CollectionUtils.filter(rst, r -> r.getPRDLVLUID().equals(productLevel1List.get(0).getPRDLVLUID()));

                    if (rst.size() > 0) {
                        binding.orderTxtError.setText("هیچ زیر دسته ای برای دسته " + rst.get(0).getPRDLVLNAME() + " وجود ندارد.");

                    }


                    productList.clear();
                    productAdapter.notifyDataSetChanged();
                    productLevel2List.clear();
                    productLevel2Adapter.notifyDataSetChanged();
                }
                if (productLevel1List.size() <= 1) {
                    binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
                }

                //endregion full ProductLevel2List because First Item ProductLevel1 Is True

                if (customProgress.isShow) {
                    customProgress.hideProgress();
                    customProgress.hideProgress();
                }
                //  Toast.makeText(getActivity(), count + "", Toast.LENGTH_LONG).show();

               // for (int j = 0; j < productLevel2List.size(); j++) {

                    ArrayList<Product> resultProduct = new ArrayList<>();
                    resultProduct.addAll(Util.AllProduct);
                   // int finalJ = j;
                    CollectionUtils.filter(resultProduct, r -> r.getPRDPRICEPERUNIT1() > 0 && r.STS);
                    for (int i = 0; i < resultProduct.size(); i++) {
                        if (resultProduct.get(i).Url == null || resultProduct.get(i).Url.equals("")) {
                            getImage(resultProduct.get(i).I);

                        /*Call<String>    call = App.api.getImage(resultProduct.get(i).I);
                        Response<String> connect= null;
                        try {
                            connect = call.execute();
                            // count+=connect.body().length();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Product product = Select.from(Product.class)
                                    .where(" I  = '" + resultProduct.get(i).I + "'").first();
                            product.Url = connect.body().replace("data:image/png;base64,", "");
                            Product.save(product);
                            productAdapter.notifyDataSetChanged();
                        } catch (Exception ignored) {
                        }*/
                        }
                    }

               // }

                super.onPostExecute(o);
            }


            @Override
            protected Object doInBackground(Object[] params) {

                return 0;
            }
        }.execute(0);


    }

    private int count;

    private void loadMore() {


        int pageSize = 18;
        isLoading = true;
//        if (productListData.size() != 0) ;
        final ArrayList<Product> items = new ArrayList<>();
        totalPage = (productListData.size() + pageSize - 1) / pageSize;

        if (totalPage > 1) { ///////////////////

            int start = ((currentPage * pageSize) - (pageSize - 1)) - 1;
            int end = (min((start + 1) + pageSize - 1, productListData.size())) - 1;

            new Handler().postDelayed(() -> {
                try {

                    items.addAll(productListData.subList(start, end));
                    if (currentPage != 1) productAdapter.removeLoadingView();
                    productList.addAll(items);


                    productAdapter.notifyDataSetChanged();

                    // check weather is last page or not
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


    private void searchProduct(String search) {


        if (binding.orderRecyclerViewProduct.getAdapter() != null) {
            getFilter().filter(search);
        }

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
                    Toast.makeText(getActivity(), "خطای تایم اوت در دریافت توضیحات" + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت توضیحات" + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }


    private void getProduct(String task) {
        String date=sharedPreferences.getString("date","");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date d;
        try {
             d = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
            d = Calendar.getInstance().getTime();
            ;
        }



        binding.progressbar.setVisibility(View.GONE);
        error ="";
        customProgress.showProgress(getActivity(), "در حال دریافت اطلاعات", false);

        String yourFilePath = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + "SaleIn";
        File file = new File(yourFilePath);
        deleteDirectory(file);
        try {

            Call<String> call = App.api.getProduct("saleinkit_api", userName, passWord, task,dateFormat.format(d));


            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelProduct>() {
                    }.getType();


                    ModelProduct iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception ignore) {

                        error = error + "\n" + "مدل دریافت شده از کالاها نا معتبر است";
                        showError(error);
                        return;
                    }


                    if (iDs == null) {
                        error = error + "\n" + "لیست دریافت شده از کالاها نا معتبر می باشد";
                        showError(error);
                    } else {

                        if (LauncherActivity.name.equals("ir.kitgroup.salein")){
                            Product.deleteAll(Product.class);
                            ProductGroupLevel1.deleteAll(ProductGroupLevel1.class);
                            ProductGroupLevel2.deleteAll(ProductGroupLevel2.class);
                        }

                        List<Product> products = iDs.getProductList();


                        if (!firstSync)
                            Product.saveInTx(products);
                        else
                            for (Product product : products) {

                                product.update();
                            }

                        if (products.size() > 0) {
                            Util.AllProduct.clear();
                        }


                        List<ProductGroupLevel2> productGroupLevel2s = iDs.getProductLevel2List();

                        if (!firstSync)
                            ProductGroupLevel2.saveInTx(productGroupLevel2s);
                        else
                            for (ProductGroupLevel2 productGroupLevel2 : productGroupLevel2s) {
                                productGroupLevel2.update();
                            }

                        if (productGroupLevel2s.size() > 0) {
                            AllProductLevel2.clear();
                        }


                        if (!firstSync)
                            ProductGroupLevel2.saveInTx(productGroupLevel2s);
                        else
                            for (ProductGroupLevel2 productGroupLevel2 : productGroupLevel2s) {
                                productGroupLevel2.update();
                            }

                        if (productGroupLevel2s.size() > 0) {
                            AllProductLevel2.clear();
                        }




                        /* for (int i = 0; i < products.size(); i++) {
                            Product product = new Product();
                            product.I = products.get(i).I;
                            product.STS = products.get(i).STS;
                            product.PID2 = products.get(i).PID2;
                            product.PID1 = products.get(i).PID1;
                            product.N = products.get(i).N;
                            product.KEY = products.get(i).KEY;
                            product.DES = products.get(i).DES;
                            product.NIP = products.get(i).NIP;
                            product.PU1 = products.get(i).PU1;
                            product.PU2 = products.get(i).PU2;
                            product.PU3 = products.get(i).PU3;
                            product.PU4 = products.get(i).PU4;
                            product.PU5 = products.get(i).PU5;
                            product.PERC_DIS = products.get(i).PERC_DIS;

                            if (!products.get(i).IMG.equals("0"))
                                SaveImageToStorage(StringToImage(products.get(i).IMG), products.get(i).I, getActivity());

                            if (firstSync) {
                                Product.saveInTx(products);

                            }
                            else {
                                product.update();
                            }

                        }*/
                        /*    CollectionUtils.filter(products1, p -> !p.IMG.equals("0"));
                        if (products1.size() > 0) {
                            for (int i = 0; i < products1.size(); i++) {
                                SaveImageToStorage(StringToImage(products1.get(i).IMG), products1.get(i).I, Objects.requireNonNull(getActivity()));
                            }
                        }
*/
                        /*   AllProductLevel1.clear();
                        if (iDs.getProductLevel1List() != null)
                            AllProductLevel1.addAll(iDs.getProductLevel1List());

                        List<ProductGroupLevel1> productGroupLevel1s = iDs.getProductLevel1List();

                        if (!firstSync)
                            ProductGroupLevel1.saveInTx(productGroupLevel1s);
                        else
                            for (ProductGroupLevel1 PrdGroupLevel1 : productGroupLevel1s) {
                                PrdGroupLevel1.update();
                            }


                            if ()*/
                        /*            AllProductLevel2.clear();
                        if (iDs.getProductLevel2List() != null)
                            AllProductLevel2.addAll(iDs.getProductLevel2List());
                        List<ProductGroupLevel2> productGroupLevel2s = iDs.getProductLevel2List();
                        if (!firstSync)
                            ProductGroupLevel2.saveInTx(productGroupLevel2s);
                        else
                            for (ProductGroupLevel2 prdGroupLevel2 : productGroupLevel2s) {
                                prdGroupLevel2.update();
                            }*/


                        sharedPreferences.edit().putBoolean("firstSync", true).apply();

                        getTypeOrder();


                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    error = error + "\n" + "فروشگاه تعطیل می باشد...لطفا در زمان دیگری مراجعه کنید.";
                    showError(error);

                }
            });


        } catch (NetworkOnMainThreadException ex) {

            error = error + "\n" + "خطا در اتصال به سرور برای دریافت کالاها";
            showError(error);
        }


    }

    private void getSetting() {


        try {

            Call<String> call = App.api.getSetting(userName, passWord);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelSetting>() {
                    }.getType();

                    ModelSetting iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {
                        error = error + "\n" + "مدل دریافت شده از تنظیمات نا معتبر است";
                        showError(error);
                        return;
                    }

                    if (iDs == null) {
                        error = error + "\n" + "لیست دریافت شده از تنظیمات نا معتبر می باشد";
                        showError(error);
                    } else {

                        Setting.deleteAll(Setting.class);
                        List<Setting> settingsList = new ArrayList<>(iDs.getSettings());
                        Setting.saveInTx(settingsList);
                        maxSales = settingsList.get(0).MAX_SALE;

                        String Update = settingsList.get(0).UPDATE_APP;
                        String NewVersion = settingsList.get(0).VERSION_APP;
                        String AppVersion = "";
                        try {
                            AppVersion = appVersion();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (Update.equals("3") && !AppVersion.equals(NewVersion)) {
                            textUpdate.setText("آپدیت جدید از برنامه موجود است.برای ادامه دادن  برنامه را آپدیت کنید.");
                            btnNo.setVisibility(View.GONE);
                            dialogUpdate.setCancelable(false);
                            dialogUpdate.show();
                        } else if (Update.equals("2") && !AppVersion.equals(NewVersion)) {
                            textUpdate.setText("آپدیت جدید از برنامه موجود است.برای بهبود عملکرد  برنامه را آپدیت کنید.");
                            btnNo.setVisibility(View.VISIBLE);
                            dialogUpdate.setCancelable(true);
                            dialogUpdate.show();
                        }


                        sharedPreferences.edit().putString("priceProduct", iDs.getSettings().get(0).DEFAULT_PRICE_INVOICE).apply();
                        Date date = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                        sharedPreferences.edit().putString("date",dateFormats.format(date)).apply();
                        getProducts();

                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    error = error + "\n" + "خطای تایم اوت در دریافت تنظیمات";
                    showError(error);


                }
            });


        } catch (NetworkOnMainThreadException ex) {


            error = error + "\n" + "خطا در اتصال به سرور برای دریافت تنطیمات";
            showError(error);
        }


    }

    private void getTypeOrder() {


        try {

            Call<String> call = App.api.getOrderType(userName, passWord);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelTypeOrder>() {
                    }.getType();

                    ModelTypeOrder iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {


                        error = error + "\n" + "مدل دریافت شده از نوع سفارش نا معتبر است";
                        showError(error);
                        return;
                    }

                    if (iDs == null) {

                        error = error + "\n" + "لیست دریافت شده از نوع سفارش نا معتبر می باشد";
                        showError(error);
                    } else {
                        OrderType.deleteAll(OrderType.class);
                        OrderType.saveInTx(iDs.getOrderTypes());
                        getSetting();


                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    error = error + "\n" + "خطای تایم اوت در دریافت نوع سفارش";
                    showError(error);
                }
            });


        } catch (NetworkOnMainThreadException ex) {

            error = error + "\n" + "خطا در اتصال به سرور برای دریافت نوع سفارش";
            showError(error);
        }


    }

    private Bitmap StringToImage(String image) {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void showError(String error) {

        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialog.dismiss();
        dialog.show();
        customProgress.hideProgress();

    }

    private void SaveImageToStorage(String bitmapImage, String ID, Context context) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        File destination = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SaleIn");
        File file = new File(destination, ID.toUpperCase());

        FileOutputStream fo = null;
        try {
            if (!destination.exists()) {
                destination.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
                fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null)
                    fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class JsonObjectAccount {

        public List<Account> Account;

    }

    private void addAccount(String userName, String pass, List<Account> accounts) {


        try {
            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            Call<String> call = App.api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "");
            customProgress.showProgress(getContext(), "در حال ثبت مشتری در سرور...", false);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    assert iDs != null;
                    int message = iDs.getLogs().get(0).getMessage();
                    String description = iDs.getLogs().get(0).getDescription();
                    if (message == 1) {
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        dialogAddAccount.dismiss();
                        customProgress.hideProgress();
                        Acc_GUID = accountsList.get(0).I;
                        Acc_NAME = accountsList.get(0).N;
                        binding.nameCustomer.removeTextChangedListener(textWatcherAcc);
                        binding.nameCustomer.setText(accountsList.get(0).N);
                        binding.nameCustomer.addTextChangedListener(textWatcherAcc);
                        accountsList.clear();


                    } else {
                        customProgress.hideProgress();
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری" + t.toString(), Toast.LENGTH_SHORT).show();
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در ثبت مشتری" + ex.toString(), Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();
        }


    }

    private void getAccountSearch(String word) {


        try {

            // customProgress.showProgress(getContext(),"در حال پیدا کردن مشتری",false);
            Call<String> call = App.api.getAccountSearch("saleinkit_api", userName, passWord, word);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();

                    Type typeIDs = new TypeToken<ModelAccount>() {
                    }.getType();
                    ModelAccount iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است", Toast.LENGTH_SHORT).show();
                        customProgress.hideProgress();
                        return;

                    }

                    assert iDs != null;
                    if (iDs.getAccountList() == null) {
                        Type typeIDs0 = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs0 = gson.fromJson(response.body(), typeIDs0);

                        if (iDs0.getLogs() != null) {

                            String description = iDs0.getLogs().get(0).getDescription();
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                            customProgress.hideProgress();
                            return;

                        } else {
                            Toast.makeText(getActivity(), "لیست دریافت شده از مشتریان نا معتبر می باشد", Toast.LENGTH_SHORT).show();
                            customProgress.hideProgress();
                            return;
                        }

                    } else {

                        accList.clear();
                        accList.addAll(iDs.getAccountList());
                        accAdapter.notifyDataSetChanged();
                        binding.accountRecyclerView.setVisibility(View.VISIBLE);
                        customProgress.hideProgress();


                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(), "خطای تایم اوت در دریافت مشتریان", Toast.LENGTH_SHORT).show();

                    customProgress.hideProgress();
                }
            });


        } catch (NetworkOnMainThreadException ignored) {
            Toast.makeText(getActivity(), "خطا در اتصال به سرور برای دریافت مشتریان", Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 88) {

            if (grantResults.length <= 0 || (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {

                Toast.makeText(getActivity(), "برای مشاهده تصاویر کالاها لطفا دسترسی ها را کامل بدهید", Toast.LENGTH_LONG).show();

            }
        }

        getProduct(task);


    }


    private void deleteDirectory(File file) {
        if (file.isDirectory()) {


            if (Objects.requireNonNull(file.list()).length == 0) {

                file.delete();


            } else {
                String[] files = file.list();

                assert files != null;
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    deleteDirectory(fileDelete);
                }

                if (Objects.requireNonNull(file.list()).length == 0) {
                    file.delete();
                }
            }


        } else {
            file.delete();
        }
    }

    private void isAllPermissionGranted() {

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
        ) {
            getProduct(task);


        } else {

            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, 88);
        }
    }


    public void setHomeBottomBar() {
        binding.bottomNavigationViewLinear.setSelectedItemId(R.id.homee);
    }

    public void setHomeBottomBarAndClearBadge() {
        binding.bottomNavigationViewLinear.setSelectedItemId(R.id.homee);
        binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (showView) {
            String dateOld = sharedPreferences.getString("timeOld", "");
            Date oldDate;
            Date dateNow = Calendar.getInstance().getTime();
            if (!dateOld.equals("")) {
                oldDate = stringToDate(dateOld, "dd/MM/yyyy HH:mm:ss");
            } else {
                oldDate = dateNow;
            }


            long diff = dateNow.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            if (time != 0 && minutes > time) {
                customProgress.hideProgress();
                productList.clear();
                productLevel1List.clear();
                productLevel2List.clear();
                productLevel1Adapter.notifyDataSetChanged();
                productLevel2Adapter.notifyDataSetChanged();
                productAdapter.notifyDataSetChanged();
                binding.progressbar.setVisibility(View.VISIBLE);
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!showView) {
            showView = true;
            Date dateOld = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sharedPreferences.edit().putString("timeOld", dateFormats.format(dateOld)).apply();


        }
    }


    private Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }


    private void getImage(final String Prd_GUID) {

        try {

            Call<String> call = App.api.getImage(Prd_GUID);

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {


                        Product product = Select.from(Product.class)
                                .where(" I  = '" + Prd_GUID + "'").first();
                        product.Url = response.body()
                                .replace("data:image/png;base64,", "");
                        Product.save(product);
                        productAdapter.notifyDataSetChanged();
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    // Toast.makeText(getActivity(), "خطا در دریافت تصویر کالا" +
                    // t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            //Toast.makeText(getActivity(), "خطا در دریافت تصویر کالا" + ex.toString(),
            // Toast.LENGTH_SHORT).show();
        }
    }


    /*private void getImage(String Prd_GUID) {

        try {

            ArrayList<Product> arrayList = new ArrayList<>(AllProduct);
            CollectionUtils.filter(arrayList, a -> a.I.equals(Prd_GUID));
            if (arrayList.size() > 0 && arrayList.get(0).Url == null) {

                Call<String> call = App.api.getImage(Prd_GUID);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {


                        CollectionUtils.filter(arrayList, a -> a.I.equals(Prd_GUID));
                        if (arrayList.size() > 0)

                            AllProduct.get(AllProduct.indexOf(arrayList.get(0))).Url = response.body();

                         SaveImageToStorage(response.body(), arrayList.get(0).I, getActivity());

                        productAdapter.notifyDataSetChanged();



                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Toast.makeText(getActivity(), "خطا در دریافت تصویر کالا" + t.toString(), Toast.LENGTH_SHORT).show();


                    }
                });

            }


        } catch (NetworkOnMainThreadException ex) {

            //  Toast.makeText(getActivity(), "خطا در دریافت تصویر کالا" + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }*/

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }
}
