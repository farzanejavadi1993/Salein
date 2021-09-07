package ir.kitgroup.order.Fragments.TabletView;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.appcompat.widget.PopupMenu;

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


import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;

import java.util.Objects;
import java.util.UUID;

import ir.kitgroup.order.Activities.Classes.LauncherActivity;
import ir.kitgroup.order.Adapters.AccountAdapter;
import ir.kitgroup.order.Adapters.DescriptionAdapter;
import ir.kitgroup.order.Adapters.InvoiceDetailMobileAdapter;
import ir.kitgroup.order.Adapters.ProductAdapter1;
import ir.kitgroup.order.Adapters.ProductLevel1Adapter;

import ir.kitgroup.order.Adapters.ProductLevel2Adapter;
import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.classes.CustomProgress;

import ir.kitgroup.order.classes.RecyclerViewLoadMoreScroll;

import ir.kitgroup.order.DataBase.Account;
import ir.kitgroup.order.DataBase.Invoice;
import ir.kitgroup.order.DataBase.OrderType;
import ir.kitgroup.order.DataBase.ProductGroupLevel1;
import ir.kitgroup.order.DataBase.ProductGroupLevel2;

import ir.kitgroup.order.DataBase.InvoiceDetail;
import ir.kitgroup.order.DataBase.Product;
import ir.kitgroup.order.DataBase.Setting;
import ir.kitgroup.order.DataBase.Tables;
import ir.kitgroup.order.DataBase.User;

import ir.kitgroup.order.databinding.OrderFragmentBinding;
import ir.kitgroup.order.models.Description;
import ir.kitgroup.order.models.ModelAccount;
import ir.kitgroup.order.models.ModelDesc;
import ir.kitgroup.order.models.ModelLog;

import ir.kitgroup.order.R;
import ir.kitgroup.order.Util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.min;


public class OrderFragment extends Fragment implements Filterable {

    //region Variable

    private OrderFragmentBinding binding;
    private String Ord_TYPE = "";
    private String Acc_GUID = "";
    private String Tbl_GUID = "";
    private String Inv_GUID;

    private String userName = "";
    private String passWord = "";
    private String numberPos = "";


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
    public int sizeGroupList = 0;


    private final ArrayList<ProductGroupLevel2> AllProductLevel2 = new ArrayList<>();
    private ArrayList<ProductGroupLevel2> productLevel2List = new ArrayList<>();
    private ProductLevel2Adapter productLevel2Adapter;


    private RecyclerViewLoadMoreScroll scrollListener;
    private final ArrayList<Product> productList = new ArrayList<>();
    private final ArrayList<Product> productListData = new ArrayList<>();
    private final ArrayList<Product> allProductActive = new ArrayList<>();


    private ProductAdapter1 productAdapter;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int totalPage;
    private String maxSales = "0";


    private CustomProgress customProgress;


    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private ArrayList<Description> descriptionList = new ArrayList<>();
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv = "";


    //region Variable DialogAddAccount
    private final List<Account> accountsList = new ArrayList<>();
    private Dialog dialogAddAccount;
    private EditText edtNameUser;
    private EditText edtAddressUser;
    private EditText edtMobileUser;
    private int gender;
    //endregion Variable DialogAddAccount


    private final ArrayList<InvoiceDetail> invoiceDetailList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private InvoiceDetailMobileAdapter invoiceDetailAdapter;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private Double sumPurePrice = 0.0;


    private Dialog dialog;


    //endregion Variable


    //region Override Method


    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        binding = OrderFragmentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        customProgress = CustomProgress.getInstance();


        Bundle bundle = getArguments();
        assert bundle != null;
        Tbl_GUID = bundle.getString("Tbl_GUID");
        Inv_GUID = bundle.getString("Inv_GUID");
        Ord_TYPE = bundle.getString("Ord_TYPE");

        OrderType ordTY = Select.from(OrderType.class).where("c ='" + Ord_TYPE + "'").first();
        if (ordTY != null)
            binding.txtTypeOrder.setText(ordTY.getN());


        List<Setting> setting = Select.from(Setting.class).list();
        if (setting.size() > 0)
            maxSales = setting.get(0).MAX_SALE;


        // userName = Select.from(User.class).list().get(0).userName;
        // passWord = Select.from(User.class).list().get(0).passWord;

        userName = "admin";
        passWord = "123";


        //region Create Order

        //create Order
        if (Inv_GUID.equals("")) {
            Inv_GUID = UUID.randomUUID().toString();
            Invoice order = new Invoice();
            order.INV_UID = Inv_GUID;
            order.save(order);
        }

        //edit Order
        else {
            ArrayList<Product> result = new ArrayList<>(Util.AllProduct);
            CollectionUtils.filter(result, R -> R.getAmount() > 0);

            if (result.size() > 0) {
                Util.AllProduct.get(Util.AllProduct.indexOf(result.get(0))).AMOUNT = 0.0;
            }


            Invoice ord = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
            List<InvoiceDetail> invoicedetails = Select.from(InvoiceDetail.class).where("INVUID = '" + ord.INV_UID + "'").list();
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
                ArrayList<Product> resultProducts = new ArrayList<>(Util.AllProduct);
                int finalI = i;
                //CollectionUtils.filter(resultProducts, r -> r.getPRDUID().equals(orderDetails.get(finalI).MainGuid) && r.getPRDREMAIN()>0 && r.getPRDPRICEPERUNIT1()>0);
                CollectionUtils.filter(resultProducts, r -> r.getPRDUID().equals(invoicedetails.get(finalI).PRD_UID) && r.STS);
                if (resultProducts.size() > 0) {
                    Double amount = (Util.AllProduct.get(Util.AllProduct.indexOf(resultProducts.get(0))).getAmount() + invoicedetails.get(finalI).INV_DET_QUANTITY);
                    Util.AllProduct.get(Util.AllProduct.indexOf(resultProducts.get(0))).setAmount(amount);
                    productAdapter.notifyItemChanged(productList.indexOf(resultProducts.get(0)));

                    if (invoiceDetailList.size() > 0) {
                        binding.invoiceRecyclerView.post(() -> binding.invoiceRecyclerView.smoothScrollToPosition(invoiceDetailList.size() - 1));
                    }


                }
            }


        }
        //endregion Create Order


        if (App.mode == 2) {
            binding.rlAccount.setVisibility(View.GONE);
        } else {

            numberPos = Select.from(User.class).list().get(0).numberPos;
            if (numberPos != null && numberPos.equals(""))
                numberPos = "0";

            //region Configuration Account
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
            binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);


            accAdapter.setOnClickItemListener((GUID, name) -> {

                Acc_GUID = GUID;

                binding.orderNameCustomer.removeTextChangedListener(textWatcherAcc);
                binding.orderNameCustomer.setText(name);
                binding.accountRecyclerView.setVisibility(View.GONE);
                binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);


            });


            binding.btnAddAccount.setOnClickListener(v -> {
                edtNameUser.setText("");
                edtAddressUser.setText("");
                edtMobileUser.setText("");
                dialogAddAccount.show();
            });
            //endregion Configuration Account
        }


        //region Cast DialogSendOrder
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        RelativeLayout rlButtons = dialog.findViewById(R.id.layoutButtons);
        MaterialButton btnReturned = dialog.findViewById(R.id.btn_returned);
        rlButtons.setVisibility(View.GONE);
        btnReturned.setVisibility(View.VISIBLE);

        tvMessage.setText("سفارش با موفقیت ارسال شد");
        btnReturned.setOnClickListener(v -> {
            for (int i = 0; i < invoiceDetailList.size(); i++) {

                invoiceDetailList.get(i).ROW_NUMBER = i + 1;
            }

            if (Tbl_GUID.equals("")) {
                Tables tables = new Tables();
                tables.N = "بیرون بر";
                tables.ACT = true;
                if (!Ord_TYPE.equals(""))
                    tables.C = Integer.parseInt(Ord_TYPE);
                tables.RSV = false;
                tables.I = UUID.randomUUID().toString();
                Invoice inv = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
                if (inv != null) {
                    inv.TBL_UID = tables.I;
                    inv.save();
                }
                tables.save();
            } else {
                Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                if (tb != null)
                    tb.ACT = true;
                assert tb != null;
                tb.update();
            }

            // LauncherFragment.tableAdapter.notifyDataSetChanged();

            dialog.dismiss();
            assert getFragmentManager() != null;
            getFragmentManager().popBackStack();
            Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("LauncherFragment");
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            assert frg != null;
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();


        });

        //endregion Cast DialogSendOrder


        //region CONFIGURATION DATA INVOICE_DETAIL

        invoiceDetailAdapter = new InvoiceDetailMobileAdapter(invoiceDetailList, "2", Inv_GUID);


        binding.invoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.invoiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.invoiceRecyclerView.setHasFixedSize(false);
        binding.invoiceRecyclerView.setAdapter(invoiceDetailAdapter);
        binding.invoiceRecyclerView.setNestedScrollingEnabled(false);


        /*  invoiceDetailAdapter.editAmountItemListener((GUID, amountString, Price, discountPercent) -> {
                if (!amountString.equals("")) {
                    if (maxSales.equals("1")) {
                        getMaxSales(userName, passWord, GUID, Price, discountPercent, GUID, "2", amountString, 1);
                    } else {
                        ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                        CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                        if (result.size() > 0) {
                            double amount = 0.0;
                            if (!amountString.isEmpty()) {
                                amount = Double.parseDouble(amountString);

                            }
                            invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;

                            double sumprice = (amount * Float.parseFloat(Price));
                            double discountPrice = sumprice * discountPercent;
                            double totalPrice = sumprice - discountPrice;


                            invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);


                            ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                            CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID));
                            if (resultPrd.size() > 0) {
                                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);
                            */
        /*
                            if (productList.indexOf(resultPrd.get(0)) >= 0)
                                productAdapter.notifyItemChanged(productList.indexOf(resultPrd.get(0)));*/
        /*
                                if (productList.contains(resultPrd.get(0)))
                                    productAdapter.notifyItemChanged(productList.indexOf(resultPrd.get(0)));
                            }

                        }
                    }
                } else {
                    ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                    CollectionUtils.filter(result, r -> r.PRD_UID.equals(GUID));
                    if (result.size() > 0) {
                        double amount = 0.0;
                        try {
                            amount = Double.parseDouble(amountString);
                        } catch (Exception ignored) {

                        }


                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_QUANTITY = amount;

                        double sumprice = (amount * Float.parseFloat(Price));
                        double discountPrice = sumprice * discountPercent;
                        double totalPrice = sumprice - discountPrice;


                        invoiceDetailList.get(invoiceDetailList.indexOf(result.get(0))).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPrice);


                        ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                        CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(GUID));
                        if (resultPrd.size() > 0) {
                            Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);
                       */
        /* if (productList.indexOf(resultPrd.get(0)) >= 0)
                            productAdapter.notifyItemChanged(productList.indexOf(resultPrd.get(0)));*/
        /*
                            if (productList.contains(resultPrd.get(0)))
                                productAdapter.notifyItemChanged(productList.indexOf(resultPrd.get(0)));

                        }
                        customProgress.hideProgress();
                    }
                }

            });*/


        invoiceDetailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();


                sumPurePrice = 0.0;

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                for (int i = 0; i < invoiceDetails.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetails.get(finalI).PRD_UID));

                    if (prdResult.size() > 0) {
                        Double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        Double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                        Double purePrice = sumprice - discountPrice;

                        sumPurePrice = sumPurePrice + purePrice;

                    }

                }
                binding.txtPurePrice.setText(format.format(sumPurePrice) + " ریال ");

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                sumPurePrice = 0.0;

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                for (int i = 0; i < invoiceDetails.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetails.get(finalI).PRD_UID));

                    if (prdResult.size() > 0) {
                        Double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        Double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                        Double purePrice = sumprice - discountPrice;

                        sumPurePrice = sumPurePrice + purePrice;

                    }

                }
                binding.txtPurePrice.setText(format.format(sumPurePrice) + " ریال ");


            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                sumPurePrice = 0.0;

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                for (int i = 0; i < invoiceDetails.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetails.get(finalI).PRD_UID));

                    if (prdResult.size() > 0) {
                        Double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        Double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                        Double purePrice = sumprice - discountPrice;

                        sumPurePrice = sumPurePrice + purePrice;

                    }

                }
                binding.txtPurePrice.setText(format.format(sumPurePrice) + " ریال ");


            }
        });


        invoiceDetailAdapter.onDescriptionItem((GUIDPrd, GUIDInv, description) -> {
            edtDescriptionItem.setText(description);
            descriptionList.clear();
            GuidInv = GUIDInv;
            getDescription(userName, passWord, GUIDPrd);
        });
        //endregion CONFIGURATION DATA INVOICE_DETAIL


        //region Action BtnSeenInvoice
        binding.btnSeenDetailInvoice.setOnClickListener(v -> {
            Bundle bundle12 = new Bundle();
            bundle12.putString("type", "2");
            bundle12.putString("Inv_GUID",Inv_GUID);
            bundle12.putString("Acc_NAME", binding.orderNameCustomer.getText().toString());
            bundle12.putString("Ord_TYPE", Ord_TYPE);
            InvoiceDetailTabletFragment invoiceDetailFragment = new InvoiceDetailTabletFragment();
            invoiceDetailFragment.setArguments(bundle12);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, invoiceDetailFragment, "InvoiceDetailFragment").addToBackStack("InvoiceDetailF").commit();
        });


        //endregion Action BtnSeenInvoice


        //region Action BtnTypePayment
        binding.btnTypePayment.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(Objects.requireNonNull(getContext()), binding.btnTypePayment);

            popup.getMenuInflater()
                    .inflate(R.menu.menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                binding.txtTypePayment.setText(item.getTitle());
                binding.btnRegisterOrder.setEnabled(true);
                binding.btnRegisterOrder.setBackground(getActivity().getResources().getDrawable(R.drawable.order_sumprice_order_list_button_layout));

                return true;
            });

            popup.show();
        });
        //endregion Action BtnTypePayment


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
                resultpr.addAll(Util.AllProduct);
            }
            ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
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


        //region CONFIGURATION DATA PRODUCT_LEVEL1

        productLevel1Adapter = new ProductLevel1Adapter(getActivity(), productLevel1List);
        binding.orderRecyclerViewProductLevel1.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.orderRecyclerViewProductLevel1.setHasFixedSize(false);
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

                if (tempPrdLvl2.size() > 0) {
                    productLevel2List.get(0).Click = true;
                    binding.orderCardSubGroup.setVisibility(View.VISIBLE);
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
                    binding.orderCardSubGroup.setVisibility(View.GONE);
                }

                productList.clear();
                productAdapter.notifyDataSetChanged();
                productLevel2List.clear();
                productLevel2Adapter.notifyDataSetChanged();
            }
            //endregion Full ProductLevel2List Because This Item ProductLevel1 Is True
        });
        //endregion Click Item ProductLevel1


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

        //endregion CONFIGURATION DATA PRODUCT_LEVEL1


        //region CONFIGURATION DATA PRODUCT_LEVEL_2
        productLevel2Adapter = new ProductLevel2Adapter(getActivity(), productLevel2List);
        binding.orderRecyclerViewProductLevel2.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.orderRecyclerViewProductLevel2.setHasFixedSize(false);
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


        productAdapter = new ProductAdapter1(getActivity(), productList, maxSales, Inv_GUID);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3) {
            @Override
            protected boolean isLayoutRTL() {
                return true;
            }
        };


        binding.orderRecyclerViewProduct.setLayoutManager(gridLayoutManager);
        scrollListener = new RecyclerViewLoadMoreScroll(gridLayoutManager);
        scrollListener.setOnLoadMoreListener(() -> {

            int i = currentPage * 18;

            currentPage++;
            int size = currentPage * 18;
            LoadMoreData(i, size);
        });


        binding.orderRecyclerViewProduct.addOnScrollListener(scrollListener);
        binding.orderRecyclerViewProduct.setAdapter(productAdapter);


        productAdapter.setOnClickListener(() -> {
                    List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

                    if (invDetails.size() > 0) {
                        binding.btnSeenDetailInvoice.setVisibility(View.VISIBLE);
                    } else {
                        binding.btnSeenDetailInvoice.setVisibility(View.GONE);
                    }
                    invoiceDetailList.clear();
                    invoiceDetailList.addAll(invDetails);
                    invoiceDetailAdapter.notifyDataSetChanged();


                }
        );


        invoiceDetailAdapter.editAmountItemListener((Prd_GUID, s, Price, discountPercent) -> {

            if (maxSales.equals("1")) {
                getMaxSales(userName, passWord, Prd_GUID, s, Price, discountPercent);
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


                        sumPurePrice = 0.0;


                        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                        for (int i = 0; i < invoiceDetails.size(); i++) {

                            ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                            int finalI = i;
                            CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetails.get(finalI).PRD_UID));

                            if (prdResult.size() > 0) {
                                Double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                                Double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                                Double totalPrice = sumprice - discountPrice;


                                sumPurePrice = sumPurePrice + totalPrice;

                            }

                        }


                        binding.txtPurePrice.setText(format.format(sumPurePrice) + " ریال ");
                        invoiceDetailAdapter.notifyDataSetChanged();

                    }
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

                sumPurePrice = 0.0;


                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoiceDetailList.get(positionStart).PRD_UID));
                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    if (invoiceDetail != null)
                        invoiceDetail.delete();
                }

                invoiceDetailList.remove(invoiceDetailList.get(positionStart));


                for (int i = 0; i < invoiceDetailList.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetailList.get(finalI).PRD_UID));

                    if (prdResult.size() > 0) {
                        double sumprice = (invoiceDetailList.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                        double totalPrice = sumprice - discountPrice;


                        sumPurePrice = sumPurePrice + totalPrice;

                    }

                }


                binding.txtPurePrice.setText(format.format(sumPurePrice) + " ریال ");


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




        invoiceDetailAdapter.setOnDeleteListener(GUID -> {
            ArrayList<Product> rst = new ArrayList<>(Util.AllProduct);
            CollectionUtils.filter(rst,r->r.I.equals(GUID));
            productList.get(productList.indexOf(rst.get(0))).AMOUNT=0.0;
            productAdapter.notifyDataSetChanged();

        });
        productAdapter.setOnDescriptionItem((GUID, amount) -> {
            if (amount > 0) {
                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
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

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                sumPurePrice = 0.0;

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                for (int i = 0; i < invoiceDetails.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetails.get(finalI).PRD_UID));

                    if (prdResult.size() > 0) {
                        Double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        Double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                        Double purePrice = sumprice - discountPrice;

                        sumPurePrice = sumPurePrice + purePrice;

                    }

                }
                binding.txtPurePrice.setText(format.format(sumPurePrice) + " ریال ");


            }
        });

        //endregion CONFIGURATION DATA PRODUCT


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


        if (Tbl_GUID.equals(""))
            binding.txtNumberTable.setText("خارج سالن");
        else {
            Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
            if (tb != null)

                binding.txtNumberTable.setText("میز شماره " + tb.N);
        }


        ArrayList<Product> prstResult = new ArrayList<>(Util.AllProduct);
        CollectionUtils.filter(prstResult, p -> p.getAmount() > 0);
        if (prstResult.size() > 0) {
            for (int i = 0; i < prstResult.size(); i++) {
                Util.AllProduct.get(Util.AllProduct.indexOf(prstResult.get(i))).AMOUNT = 0.0;
            }
        }


        //region Action BtnRegister
        binding.btnRegisterOrder.setOnClickListener(view1 -> {

          /* Tables tb= Select.from(Tables.class).where("I ='" + tblGUID + "'").first();

            if (tb!=null && tb.RSV){
                Toast.makeText(getActivity(), "میز رزرو شده است", Toast.LENGTH_SHORT).show();
                return;
            }*/
            if (Acc_GUID.equals("")) {

                Toast.makeText(getActivity(), "مشترک مورد نظر را انتخاب کنید", Toast.LENGTH_SHORT).show();
            } else if (invoiceDetailList.size() == 0) {

                Toast.makeText(getActivity(), "هیچ سفارشی ندارید", Toast.LENGTH_SHORT).show();
            } else {

                Date date = Calendar.getInstance().getTime();

                float sumPrice = 0;
                float sumDiscount = 0;
                float sumPurePrice = 0;

                for (InvoiceDetail invoicedetail : Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list()) {
                    InvoiceDetail.deleteInTx(invoicedetail);
                }
                for (int i = 0; i < invoiceDetailList.size(); i++) {
                    sumPurePrice = sumPurePrice + Float.parseFloat(invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
                    sumPrice = (float) (sumPrice + (invoiceDetailList.get(i).INV_DET_QUANTITY * Float.parseFloat(invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT)));
                    invoiceDetailList.get(i).ROW_NUMBER = i + 1;
                    sumDiscount = sumDiscount + Float.parseFloat(invoiceDetailList.get(i).INV_DET_DISCOUNT);
                }
                InvoiceDetail.saveInTx(invoiceDetailList);
                Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                invoice.INV_UID = Inv_GUID;
                invoice.INV_TOTAL_AMOUNT = (double) sumPrice;//جمع فاکنور
                invoice.INV_TOTAL_DISCOUNT = 0.0;
                invoice.INV_PERCENT_DISCOUNT = 0.0;
                invoice.INV_DET_TOTAL_DISCOUNT = (double) sumDiscount;
                // invoice.INV_DESCRIBTION = descriptionFactor;
                invoice.INV_TOTAL_TAX = 0.0;
                invoice.INV_TOTAL_COST = 0.0;
                invoice.INV_EXTENDED_AMOUNT = (double) sumPurePrice;
                invoice.INV_DATE = date;
                invoice.INV_DUE_DATE = date;
                invoice.INV_STATUS = true;
                invoice.ACC_CLB_UID = Acc_GUID;
                invoice.TBL_UID = Tbl_GUID;
                invoice.INV_TYPE_ORDER = Integer.parseInt(Ord_TYPE);
                invoice.Acc_name = binding.orderNameCustomer.getText().toString();
                invoice.save();
                // LauncherFragment.factorGuid = factorGuid;


                List<Invoice> listInvoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").list();
                List<InvoiceDetail> invoiceDetailList = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                SendOrder(listInvoice, invoiceDetailList);


            }

        });
        //endregion Action BtnRegister


        getProduct();


        return view;
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

                if (!constraint.toString().equals("")) {

                    String[] tempSearch = constraint.toString().trim().split(" ");
                    //  mOriginalValues.clear();
                    //mOriginalValues.addAll(productListData);
                    // int length = mOriginalValues.size();
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

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }

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

    private static class JsonObject {
        public List<Invoice> Invoice;
        public List<ir.kitgroup.order.DataBase.InvoiceDetail> InvoiceDetail;
    }

    void SendOrder(List<Invoice> invoice, List<InvoiceDetail> invoiceDetail) {

        try {

            customProgress.showProgress(getActivity(), "در حال ارسال سفارش", false);
            JsonObject jsonObject = new JsonObject();
            jsonObject.Invoice = invoice;
            jsonObject.InvoiceDetail = invoiceDetail;
            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObject>() {
            }.getType();


            Call<String> call = App.api.PostData(userName, passWord,
                    gson.toJson(jsonObject, typeJsonObject),
                    "",
                    numberPos);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, final Response<String> response) {


                    customProgress.hideProgress();

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    assert iDs != null;
                    int message = iDs.getLogs().get(0).getMessage();
                    String description = iDs.getLogs().get(0).getDescription();
                    if (message == 1) {
                        // Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        dialog.show();
                    } else {
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در ارتباط" + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getActivity(), "خطا در ارتباط" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadMoreData(int i, int size) {

        productAdapter.addLoadingView();
        new Handler().postDelayed(() -> {
            List<Product> dataViews = getDataViews(i, size);
            productAdapter.removeLoadingView();
            productList.addAll(dataViews);
            productAdapter.notifyDataSetChanged();
            scrollListener.setLoaded();
        }, 2000);
    }

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
                        ArrayList<Product> tempPrd = new ArrayList<>(Util.AllProduct);


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
                    binding.orderCardGroup.setVisibility(View.GONE);
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

                    binding.orderCardSubGroup.setVisibility(View.VISIBLE);


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
                    //CollectionUtils.filter(resultPrd, r -> r.getPRDLVLUID2().equals(productLevel2List.get(0).getPRDLVLUID()));

                    if (resultPrd.size() == 0) {

                        ArrayList<ProductGroupLevel1> rst = new ArrayList<>(AllProductLevel1);
                        CollectionUtils.filter(rst, r -> r.getPRDLVLUID().equals(productLevel2List.get(0).getPRDLVLPARENTUID()));

                        if (rst.size() > 0) {
                            binding.orderTxtError.setText("هیچ نوع از " + productLevel2List.get(0).getPRDLVLNAME() + " " + "در دسته " + rst.get(0).getPRDLVLNAME() + " " + "موجود نمی باشد. ");

                        }
                    }


                    ArrayList<Product> resultPrdAc = new ArrayList<>(Util.AllProduct);
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
                        binding.orderCardSubGroup.setVisibility(View.GONE);
                    }


                    productList.clear();
                    productAdapter.notifyDataSetChanged();
                    productLevel2List.clear();
                    productLevel2Adapter.notifyDataSetChanged();
                }


                //endregion full ProductLevel2List because First Item ProductLevel1 Is True


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


    private void getMaxSales(String userName, String pass, String Prd_GUID, String s, double Price, double discount) {


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
                                        if (resultPrd1.size() > 0) {
                                            resultPrd1.get(0).AMOUNT = 0.0;
                                            productAdapter.notifyDataSetChanged();
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

                            productAdapter.notifyDataSetChanged();
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

    public void searchProduct(String search) {


        if (binding.orderRecyclerViewProduct.getAdapter() != null) {
            getFilter().filter(search);
        }

    }


    public void searchAccount(String search) {


        if (binding.accountRecyclerView.getAdapter() != null) {
            ((Filterable) binding.accountRecyclerView.getAdapter()).getFilter().filter(search);
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
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();

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
                        binding.orderNameCustomer.removeTextChangedListener(textWatcherAcc);
                        binding.orderNameCustomer.setText(accountsList.get(0).N);
                        binding.orderNameCustomer.addTextChangedListener(textWatcherAcc);
                        accountsList.clear();
                        getAccount(userName, pass);

                    } else {
                        customProgress.hideProgress();
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();
        }


    }

    void getAccount(String user, String pass) {

        try {

            customProgress.showProgress(getActivity(), "در حال بارگزاری مشتریان...", false);
            Call<String> call = App.api.getAccount("saleinkit_api", user, pass);

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
                        Toast.makeText(getActivity(), "در دریافت مشتری خطایی رخ داد..", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "خطایی رخ داده است.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        accList.clear();


                        //LauncherFragment.AllAccount.clear();
                        //LauncherFragment.AllAccount.addAll(iDs.getAccountList());
                        Toast.makeText(getActivity(), "بارگیری موفق", Toast.LENGTH_SHORT).show();

                    }

                    customProgress.hideProgress();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                    customProgress.hideProgress();
                }
            });


        } catch (NetworkOnMainThreadException EX) {

            Toast.makeText(getActivity(), EX.toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "در دریافت مشتری خطایی رخ داد..", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "خطایی رخ داده است.", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        accList.clear();

                        accList.addAll(iDs.getAccountList());
                        accAdapter.notifyDataSetChanged();

                        Toast.makeText(getActivity(), "بارگیری موفق", Toast.LENGTH_SHORT).show();

                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {


                }
            });


        } catch (NetworkOnMainThreadException ignored) {
        }


    }


}
