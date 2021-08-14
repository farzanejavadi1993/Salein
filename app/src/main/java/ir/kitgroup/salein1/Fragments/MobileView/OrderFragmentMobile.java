package ir.kitgroup.salein1.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.pm.ActivityInfo;
import android.graphics.Color;


import android.graphics.drawable.ColorDrawable;

import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
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


import java.util.List;

import java.util.Objects;
import java.util.UUID;

import ir.kitgroup.salein1.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein1.Adapters.AccountAdapter;
import ir.kitgroup.salein1.Adapters.DescriptionAdapter;
import ir.kitgroup.salein1.Adapters.InvoiceDetailAdapter;
import ir.kitgroup.salein1.Adapters.ProductAdapter1;
import ir.kitgroup.salein1.Adapters.ProductLevel1Adapter;

import ir.kitgroup.salein1.Adapters.ProductLevel2Adapter;
import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.Classes.CustomProgress;

import ir.kitgroup.salein1.Classes.PaginationScrollListener;
import ir.kitgroup.salein1.Classes.RecyclerViewLoadMoreScroll;

import ir.kitgroup.salein1.DataBase.Account;
import ir.kitgroup.salein1.DataBase.Invoice;

import ir.kitgroup.salein1.DataBase.ProductGroupLevel1;
import ir.kitgroup.salein1.DataBase.ProductGroupLevel2;

import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.DataBase.Setting;

import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.MobileView.InVoiceDetailMobileFragment;
import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;

import ir.kitgroup.salein1.Models.Description;
import ir.kitgroup.salein1.Models.ModelAccount;
import ir.kitgroup.salein1.Models.ModelDesc;
import ir.kitgroup.salein1.Models.ModelLog;

import ir.kitgroup.salein1.R;
import ir.kitgroup.salein1.databinding.FragmentOrderMobileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.min;


public class OrderFragmentMobile extends Fragment implements Filterable {


    //region Parameter
    private FragmentOrderMobileBinding binding;
    private CustomProgress customProgress;
    private String Ord_TYPE = "";
    private String Acc_GUID = "";
    private String Tbl_GUID = "";
    private String Inv_GUID;

    private String userName = "";
    private String passWord = "";


    private TextWatcher textWatcherAcc;
    private AccountAdapter accAdapter;
    private final ArrayList<Account> accList = new ArrayList<>();


    private TextWatcher textWatcherProduct;
    private final Object mLock = new Object();
    private ArrayList<Product> mOriginalValues;
    private final ArrayList<Product> emptyListProduct = new ArrayList<>();
    private int emptyListProductFlag = 0;


    private final ArrayList<ProductGroupLevel1> AllProductLevel1 = new ArrayList<>();
    private final ArrayList<ProductGroupLevel1> productLevel1List = new ArrayList<>();
    private ProductLevel1Adapter productLevel1Adapter;


    private final ArrayList<ProductGroupLevel2> AllProductLevel2 = new ArrayList<>();
    private final ArrayList<ProductGroupLevel2> productLevel2List = new ArrayList<>();
    private ProductLevel2Adapter productLevel2Adapter;


    public static ArrayList<Product> productList = new ArrayList<>();
    private final ArrayList<Product> productListData = new ArrayList<>();
    private final ArrayList<Product> allProductActive = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static ProductAdapter1 productAdapter;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int totalPage;

    private String maxSales = "0";


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    public static ArrayList<Description> descriptionList = new ArrayList<>();
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv = "";
    //endregion Variable Dialog Description


    //region Variable DialogAddAccount
    private final List<Account> accountsList = new ArrayList<>();
    private Dialog dialogAddAccount;
    private EditText edtNameUser;
    private EditText edtAddressUser;
    private EditText edtMobileUser;
    private int gender;
    //endregion Variable DialogAddAccount


    public static  ArrayList<Invoicedetail> invoiceDetailList = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    public static InvoiceDetailAdapter invoiceDetailAdapter;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private float sumPrice = 0;
    private float sumPurePrice = 0;

    //endregion Parameter


    //region Override Method


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentOrderMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }


    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        customProgress = CustomProgress.getInstance();


        for (Invoice invoice : Select.from(Invoice.class).where("INVTOTALAMOUNT ='" + null + "'").list()) {
            Invoice.deleteInTx(invoice);
        }


        Bundle bundle = getArguments();
        assert bundle != null;
        Tbl_GUID = bundle.getString("Tbl_GUID");
        Inv_GUID = bundle.getString("Inv_GUID");
        Ord_TYPE = bundle.getString("Ord_TYPE");


        List<Setting> setting = Select.from(Setting.class).list();
        if (setting.size() > 0)
            maxSales = setting.get(0).MAX_SALE;


        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;


        if (App.mode == 2) {
            binding.orderNameCustomer.setVisibility(View.GONE);
            binding.btnAddAccount.setVisibility(View.GONE);
        } else {

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

                    if (s.toString().isEmpty()) {
                        binding.accountRecyclerView.setVisibility(View.GONE);
                        binding.orderNameCustomer.setHint("نام مشترک");
                    } else {
                        binding.accountRecyclerView.setVisibility(View.VISIBLE);
                        getAccountSearch(s.toString());
                    }


                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            accAdapter.setOnClickItemListener((GUID, name) -> {
                Acc_GUID = GUID;
                binding.orderNameCustomer.removeTextChangedListener(textWatcherAcc);
                binding.orderNameCustomer.setText(name);
                binding.accountRecyclerView.setVisibility(View.GONE);
                binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);


            });
            binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);
        }


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
                String description = edtDescriptionItem.getText().toString();
                edtDescriptionItem.setText(description + "   " + "'" + desc + "'");
            } else {
                if (edtDescriptionItem.getText().toString().contains("'" + desc + "'"))

                    edtDescriptionItem.setText(edtDescriptionItem.getText().toString().replace("   " + "'" + desc + "'", ""));

            }


        });


        btnRegisterDescription.setOnClickListener(v -> {
            ArrayList<Product> resultpr = new ArrayList<>();
            if (LauncherActivity.screenInches < 7) {
                resultpr.addAll(LauncherOrganizationFragment.AllProduct);
            }
            ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
            CollectionUtils.filter(result, r -> r.INV_DET_UID.equals(GuidInv));
            if (result.size() > 0) {

                invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_DESCRIBTION = edtDescriptionItem.getText().toString();

                CollectionUtils.filter(resultpr, r -> r.I.equals(result.get(0).PRD_UID));
            }
            invoiceDetailAdapter.notifyDataSetChanged();


            if (resultpr.size() > 0) {
                if (resultpr.get(0).descItem == null)
                    resultpr.get(0).descItem = edtDescriptionItem.getText().toString();
                else
                    resultpr.get(0).descItem = edtDescriptionItem.getText().toString() + " " + resultpr.get(0).descItem;
                productAdapter.notifyDataSetChanged();
            }
            dialogDescription.dismiss();
        });
        //endregion Cast DialogDescription


        binding.btnAddAccount.setOnClickListener(v -> {
            edtNameUser.setText("");
            edtAddressUser.setText("");
            edtMobileUser.setText("");
            dialogAddAccount.show();
        });


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
                    ArrayList<Product> tempPrd = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                    CollectionUtils.filter(tempPrd, tp -> tp.getPRDLVLUID2().equals(resultPrdGrp2_.get(finalI).getPRDLVLUID()) && tp.getPRDPRICEPERUNIT1() > 0 && tp.STS);
                    if (tempPrd.size() > 0)

                        tempPrdLvl2.add(resultPrdGrp2_.get(i));
                }


                productLevel2List.clear();
                productLevel2List.addAll(tempPrdLvl2);

                if (tempPrdLvl2.size() > 0) {
                    productLevel2List.get(0).Click = true;

                }

                productLevel2Adapter.notifyDataSetChanged();


                //region Full ProductList Because First Item ProductLevel2 Is True
                ArrayList<Product> resultPrd_ = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
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
                productListData.addAll(resultPrd_);

                for (int i = 0; i < 18; i++) {
                    if (resultPrd_.size() > i)
                        productList.add(resultPrd_.get(i));
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
            ArrayList<Product> rstProduct = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
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

            productListData.addAll(rstProduct);

            for (int i = 0; i < 18; i++) {
                if (rstProduct.size() > i)
                    productList.add(rstProduct.get(i));
            }

            productAdapter.notifyDataSetChanged();
            //endregion Full ProductList Because This Item ProductLevel1 Is True
        });

        //endregion Click Item ProductLevel2

        //endregion CONFIGURATION DATA PRODUCT_LEVEL_2


        //region CONFIGURATION DATA PRODUCT
        productAdapter = new ProductAdapter1(getActivity(), productList);

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
        productAdapter.setOnClickListener((GUID, Price, discount, MinOrPlus) -> {

            if (maxSales.equals("1")) {
                getMaxSales(userName, passWord, GUID, Price, discount, GUID, MinOrPlus);
            }
            else {
                ArrayList<Product> resultProduct = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                CollectionUtils.filter(resultProduct, r -> r.getPRDUID().equals(GUID));

                if (resultProduct.size() > 0) {
                    double amount;
                    if (MinOrPlus)
                        amount = LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).getAmount() + 1;
                    else {
                        if (LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).getAmount() >= 1)
                            amount = LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).getAmount() - 1;

                        else
                            return;

                    }


                    LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);


                    ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
                    CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                    //edit
                    if (result.size() > 0) {
                        Double sumprice = (amount * Float.parseFloat(Price));
                        Double discountPrice = sumprice * discount;
                        Double totalPrice = sumprice - discountPrice;
                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);
                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_DISCOUNT = String.valueOf(discountPrice);

                        if (amount == 0) {
                            invoiceDetailAdapter.notifyItemRemoved(invoiceDetailList.indexOf(result.get(0)));


                        } else {
                            invoiceDetailAdapter.notifyItemChanged(invoiceDetailList.indexOf(result.get(0)));
                        }


                    } else {
                        Invoicedetail invoicedetail = new Invoicedetail();
                        invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                        invoicedetail.INV_UID = Inv_GUID;
                        invoicedetail.INV_DET_QUANTITY = String.valueOf(amount);
                        invoicedetail.INV_DET_PRICE_PER_UNIT = Price;
                        invoicedetail.INV_DET_DISCOUNT = "0";
                        Double sumprice = (amount * Float.parseFloat(Price));
                        Double discountPrice = sumprice * discount;
                        Double totalPrice = sumprice - discountPrice;
                        invoicedetail.INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                        invoicedetail.INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                        invoicedetail.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                        invoicedetail.INV_DET_TAX = "0";
                        invoicedetail.INV_DET_STATUS = true;
                        invoicedetail.PRD_UID = GUID;
                        invoicedetail.INV_DET_TAX_VALUE = "0";
                        invoicedetail.INV_DET_DESCRIBTION = "";
                        invoiceDetailList.add(invoicedetail);
                        invoiceDetailAdapter.notifyDataSetChanged();

                    }


                    binding.orderListBtnRegister.setVisibility(View.VISIBLE);


                    productAdapter.notifyItemChanged(productList.indexOf(resultProduct.get(0)));
                }
            }

            binding.orderListBtnRegister.setVisibility(View.VISIBLE);


        });


        productAdapter.setOnDescriptionItem((GUID, amount) -> {
            if (amount > 0) {
                ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
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


                for (int i = 0; i < invoiceDetailList.size(); i++) {
                    sumPrice = sumPrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_QUANTITY)
                            * Float.parseFloat(invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT);
                    sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
                }


                binding.orderListTvRegister.setText("تکمیل سفارش " + "(" + format.format(sumPurePrice) + " ریال " + ")");


            }
        });

        //endregion CONFIGURATION DATA PRODUCT


        invoiceDetailAdapter=new InvoiceDetailAdapter(invoiceDetailList,"2");

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


        ArrayList<Product> prdResult = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
        CollectionUtils.filter(prdResult, p -> p.getAmount() > 0);
        if (prdResult.size() > 0) {
            for (int i = 0; i < prdResult.size(); i++) {
                LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(prdResult.get(i))).AMOUNT = 0.0;
            }
        }


        //region Action BtnRegister
        binding.orderListBtnRegister.setOnClickListener(view1 -> {

            if (Acc_GUID.equals("")) {
                Toast.makeText(getActivity(), "مشترک مورد نظر را انتخاب کنید", Toast.LENGTH_SHORT).show();
            } else if (invoiceDetailList.size() == 0) {
                Toast.makeText(getActivity(), "هیچ سفارشی ندارید", Toast.LENGTH_SHORT).show();
            } else {

                Bundle bundle1 = new Bundle();
                bundle1.putString("type", "2");
                bundle1.putString("Inv_GUID", Inv_GUID);
                bundle1.putString("Tbl_GUID", Tbl_GUID);
                bundle1.putString("Acc_NAME", binding.orderNameCustomer.getText().toString());
                bundle1.putString("Acc_GUID", Acc_GUID);
                bundle1.putString("Ord_TYPE", Ord_TYPE);

                InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
                inVoiceDetailFragmentMobile.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobile").commit();


            }

        });
        //endregion Action BtnRegister


        getProduct();

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

                    int counter = 0;
                    int searchSize = tempSearch.length;
                    for (String searchItem : tempSearch) {
                        ArrayList<Product> result = new ArrayList<>(mOriginalValues);
                        CollectionUtils.filter(result, r -> r.N.contains(searchItem));
                        if (result.size() > 0) {
                            counter++;
                        }
                        if (counter == searchSize)
                            tempList.clear();
                        tempList.addAll(result);
                    }


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
                    productListData.addAll(tempResult);
                }
                for (int i = 0; i < 18; i++) {
                    assert tempResult != null;
                    if (tempResult.size() > i)
                        productList.add(tempResult.get(i));
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
        invoiceDetailAdapter = null;
        productAdapter = null;
    }

    //endregion Override Method

    //region Method




    public List<Product> getDataViews(int j, int size) {
        List<Product> dataViews = new ArrayList<>();
        for (int i = j; i < size; i++) {
            if (productListData.size() > i) {
                dataViews.add(productListData.get(i));

            } else {
                isLastPage = true;
            }
        }
        return dataViews;
    }


    @SuppressLint("StaticFieldLeak")
    private void getProduct() {
     new AsyncTask() {


            @Override
            protected void onPreExecute() {
                customProgress.showProgress(getActivity(), "در حال بارگزاری کالاها", false);
                super.onPreExecute();
            }

            @SuppressLint({"SetTextI18n", "StaticFieldLeak"})
            @Override
            protected void onPostExecute(Object o) {

                AllProductLevel1.clear();
                AllProductLevel1.addAll(Select.from(ProductGroupLevel1.class).list());

                AllProductLevel2.clear();
                AllProductLevel2.addAll(Select.from(ProductGroupLevel2.class).list());

                // LauncherFragment.AllProduct.clear();
                //LauncherFragment.AllProduct.addAll(Select.from(Product.class).list());


                productLevel1List.clear();

                for (int i = 0; i < AllProductLevel1.size(); i++) {

                    ArrayList<ProductGroupLevel2> resultPrdGrp2 = new ArrayList<>(AllProductLevel2);
                    int finalI = i;
                    CollectionUtils.filter(resultPrdGrp2, r -> r.getPRDLVLPARENTUID().equals(AllProductLevel1.get(finalI).getPRDLVLUID()));


                    if (resultPrdGrp2.size() > 0) {

                        ArrayList<ProductGroupLevel2> tempPrdLvl2 = new ArrayList<>();
                        ArrayList<Product> tempPrd = new ArrayList<>(LauncherOrganizationFragment.AllProduct);


                        for (int j = 0; j < resultPrdGrp2.size(); j++) {
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
                        ArrayList<Product> resultPrd = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                        int finalI = i;

                        CollectionUtils.filter(resultPrd, r -> r.getPRDLVLUID2().equals(resultPrdGrp2.get(finalI).getPRDLVLUID()) && r.getPRDPRICEPERUNIT1() > 0 && r.STS);
                        if (resultPrd.size() > 0) {
                            productLevel2List.add(resultPrdGrp2.get(i));
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
                    ArrayList<Product> resultPrd = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                    CollectionUtils.filter(resultPrd, r -> r.getPRDLVLUID2().equals(productLevel2List.get(0).getPRDLVLUID()) && r.getPRDPRICEPERUNIT1() > 0 && r.STS);
                    //CollectionUtils.filter(resultPrd, r -> r.getPRDLVLUID2().equals(productLevel2List.get(0).getPRDLVLUID()));

                    if (resultPrd.size() == 0) {

                        ArrayList<ProductGroupLevel1> rst = new ArrayList<>(AllProductLevel1);
                        CollectionUtils.filter(rst, r -> r.getPRDLVLUID().equals(productLevel2List.get(0).getPRDLVLPARENTUID()));

                        if (rst.size() > 0) {
                            binding.orderTxtError.setText("هیچ نوع از " + productLevel2List.get(0).getPRDLVLNAME() + " " + "در دسته " + rst.get(0).getPRDLVLNAME() + " " + "موجود نمی باشد. ");

                        }
                    }


                    ArrayList<Product> resultPrdAc = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                    CollectionUtils.filter(resultPrdAc, r -> r.getPRDPRICEPERUNIT1() > 0 && r.STS);
                    allProductActive.clear();
                    allProductActive.addAll(resultPrdAc);
                    productListData.clear();
                    productListData.addAll(resultPrd);
                    productList.clear();
                    for (int i = 0; i < 18; i++) {
                        if (resultPrd.size() > i)
                            productList.add(resultPrd.get(i));
                    }

                    // productList.addAll(resultPrd);
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


                //endregion full ProductLevel2List because First Item ProductLevel1 Is True

                //region Create Order

                //create Order
                if (Inv_GUID.equals("")) {

                    Inv_GUID = UUID.randomUUID().toString();
                    Invoice order = new Invoice();
                    order.INV_UID = Inv_GUID;
                    Invoice.save(order);


                }

                //edit Order
                else {
                    ArrayList<Product> result = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                    CollectionUtils.filter(result, R -> R.getAmount() > 0);

                    if (result.size() > 0) {
                        LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(result.get(0))).AMOUNT = 0.0;
                    }


                    Invoice ord = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                    List<Invoicedetail> invoicedetails = Select.from(Invoicedetail.class).where("INVUID = '" + ord.INV_UID + "'").list();
                    Account acc = Select.from(Account.class).where("I ='" + ord.ACC_CLB_UID + "'").first();
                    binding.orderNameCustomer.removeTextChangedListener(textWatcherAcc);
                    binding.orderNameCustomer.setText(acc.getACCCLBNAME());
                    binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);
                    Acc_GUID = acc.getACCCLBUID();
                    binding.orderNameCustomer.setEnabled(false);
                    invoiceDetailList.clear();
                    invoiceDetailList.addAll(invoicedetails);
                    invoiceDetailAdapter.notifyDataSetChanged();
                    for (int i = 0; i < invoicedetails.size(); i++) {
                        ArrayList<Product> resultProducts = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                        int finalI = i;
                        //CollectionUtils.filter(resultProducts, r -> r.getPRDUID().equals(orderDetails.get(finalI).MainGuid) && r.getPRDREMAIN()>0 && r.getPRDPRICEPERUNIT1()>0);
                        CollectionUtils.filter(resultProducts, r -> r.getPRDUID().equals(invoicedetails.get(finalI).PRD_UID) && r.STS);
                        if (resultProducts.size() > 0) {
                            Double amount = (LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProducts.get(0))).getAmount() + Double.parseDouble(invoicedetails.get(finalI).INV_DET_QUANTITY));
                            LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProducts.get(0))).setAmount(amount);
                            productAdapter.notifyItemChanged(productList.indexOf(resultProducts.get(0)));


                            binding.orderListBtnRegister.setVisibility(View.VISIBLE);


                        }
                    }


                }

                //endregion Create Order
                List<Setting> setting = Select.from(Setting.class).list();
                if (setting.size() > 0)

                    try {
                      /*  if (App.mode == 2) {
                            if (Select.from(User.class).first().ACCGID != null && !Select.from(User.class).first().ACCGID.equals(""))
                                Acc_GUID = Select.from(User.class).first().ACCGID;
                            else
                                Acc_GUID = setting.get(0).DEFAULT_CUSTOMER;

                        }*/
                    } catch (Exception e) {
                        Acc_GUID = "";
                    }

                customProgress.hideProgress();
                //  getAccount(userName, passWord);
                super.onPostExecute(o);
            }


            @Override
            protected Object doInBackground(Object[] params) {

                return 0;
            }
        }.execute(0);


    }


    public void loadMore() {


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

    private void getMaxSales(String userName, String pass, String id, String Price, Double
            discount, String GUID, boolean MinOrPlus) {



        try {

            Call<String> call = App.api.getMaxSales(userName, pass, id);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    customProgress.hideProgress();

                    int remain = -1000000000;
                    try {
                        assert response.body() != null;
                        remain = Integer.parseInt(response.body());
                    } catch (Exception e) {
                        Gson gson = new Gson();
                        Type typeIDs = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                        assert iDs != null;
                        int message = iDs.getLogs().get(0).getMessage();
                        String description = iDs.getLogs().get(0).getDescription();
                        if (message != 1)
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();


                    }

                    if (remain != -1000000000) {

                            if (Integer.parseInt(response.body()) <= 0) {
                                Toast.makeText(getActivity(), "این کالا موجود نمی باشد", Toast.LENGTH_SHORT).show();
                                customProgress.hideProgress();
                                return;
                            }
                            ArrayList<Product> resultProduct = new ArrayList<>(LauncherOrganizationFragment.AllProduct);
                            CollectionUtils.filter(resultProduct, r -> r.getPRDUID().equals(GUID));

                            if (resultProduct.size() > 0) {
                                double amount;
                                if (MinOrPlus)
                                    amount = LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).getAmount() + 1;

                                else {
                                    if (LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).getAmount() >= 1)
                                        amount = LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).getAmount() - 1;

                                    else
                                        return;

                                }


                                if (Integer.parseInt(response.body()) - amount < 0) {
                                    Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();
                                    customProgress.hideProgress();

                                    return;
                                }
                                LauncherOrganizationFragment.AllProduct.get(LauncherOrganizationFragment.AllProduct.indexOf(resultProduct.get(0))).setAmount(amount);
                                //productAdapter.notifyItemChanged(productList.indexOf(resultProduct.get(0)));

                                ArrayList<Invoicedetail> result = new ArrayList<>(invoiceDetailList);
                                CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                                //edit
                                if (result.size() > 0) {
                                    Double sumprice = (amount * Float.parseFloat(Price));
                                    Double discountPrice = sumprice * discount;
                                    Double totalPrice = sumprice - discountPrice;
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = String.valueOf(amount);
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                                    invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_DISCOUNT = String.valueOf(discountPrice);
                                    invoiceDetailAdapter.notifyItemChanged(invoiceDetailList.indexOf(result.get(0)));
                                } else {
                                    Invoicedetail invoicedetail = new Invoicedetail();
                                    invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                                    invoicedetail.INV_UID = Inv_GUID;
                                    invoicedetail.INV_DET_QUANTITY = String.valueOf(amount);
                                    invoicedetail.INV_DET_PRICE_PER_UNIT = Price;
                                    invoicedetail.INV_DET_DISCOUNT = "0";
                                    Double sumprice = (amount * Float.parseFloat(Price));
                                    Double discountPrice = sumprice * discount;
                                    Double totalPrice = sumprice - discountPrice;
                                    invoicedetail.INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);
                                    invoicedetail.INV_DET_PERCENT_DISCOUNT = String.valueOf(discount * 100);
                                    invoicedetail.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                                    invoicedetail.INV_DET_TAX = "0";
                                    invoicedetail.INV_DET_STATUS = true;
                                    invoicedetail.PRD_UID = GUID;
                                    invoicedetail.INV_DET_TAX_VALUE = "0";
                                    invoicedetail.INV_DET_DESCRIBTION = "";
                                    invoiceDetailList.add(invoicedetail);
                                    invoiceDetailAdapter.notifyDataSetChanged();

                                }


                                binding.orderListBtnRegister.setVisibility(View.VISIBLE);

                                productAdapter.notifyItemChanged(productList.indexOf(resultProduct.get(0)));
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

    public void searchProduct(String search) {


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

            Call<String> call = App.api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject));
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
                        binding.orderNameCustomer.removeTextChangedListener(textWatcherAcc);
                        binding.orderNameCustomer.setText(accountsList.get(0).N);
                        binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);
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

                        } else {
                            Toast.makeText(getActivity(), "لیست دریافت شده از مشتریان نا معتبر می باشد", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        accList.clear();
                        accList.addAll(iDs.getAccountList());
                        accAdapter.notifyDataSetChanged();



                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Toast.makeText(getActivity(), "خطای تایم اوت در دریافت مشتریان", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (NetworkOnMainThreadException ignored) {
            Toast.makeText(getActivity(), "خطا در اتصال به سرور برای دریافت مشتریان", Toast.LENGTH_SHORT).show();
        }


    }

}
