package ir.kitgroup.saleinmeat.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.os.Bundle;

import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;


import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.UUID;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;

import ir.kitgroup.saleinmeat.Adapters.AccountAdapter;
import ir.kitgroup.saleinmeat.Adapters.DescriptionAdapter;

import ir.kitgroup.saleinmeat.Adapters.ProductAdapter1;
import ir.kitgroup.saleinmeat.Adapters.ProductLevel1Adapter;
import ir.kitgroup.saleinmeat.Adapters.ProductLevel2Adapter;
import ir.kitgroup.saleinmeat.DataBase.OrderType;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.classes.CustomProgress;


import ir.kitgroup.saleinmeat.DataBase.Account;

import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;

import ir.kitgroup.saleinmeat.DataBase.Setting;
import ir.kitgroup.saleinmeat.DataBase.User;

import ir.kitgroup.saleinmeat.classes.PaginationScrollListener;
import ir.kitgroup.saleinmeat.classes.RecyclerViewLoadMoreScroll;
import ir.kitgroup.saleinmeat.models.Description;

import ir.kitgroup.saleinmeat.models.ModelAccount;
import ir.kitgroup.saleinmeat.models.ModelDesc;
import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.models.ModelProduct;
import ir.kitgroup.saleinmeat.models.ModelProductLevel1;
import ir.kitgroup.saleinmeat.models.ModelProductLevel2;
import ir.kitgroup.saleinmeat.models.ModelTypeOrder;
import ir.kitgroup.saleinmeat.models.Product;
import ir.kitgroup.saleinmeat.models.ProductLevel1;
import ir.kitgroup.saleinmeat.models.ModelSetting;
import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.Util.Util;
import ir.kitgroup.saleinmeat.databinding.FragmentMobileOrderMainBinding;


import ir.kitgroup.saleinmeat.models.ProductLevel2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.min;


public class MainOrderMobileFragment extends Fragment {

    //region Parameter
   // private final CompositeDisposable disposables = new CompositeDisposable();

    CompositeDisposable compositeDisposable = new CompositeDisposable();
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
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync


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

    private Boolean showView = false;


    private int imageLogo;
    private int imgIconDialog;
    private int imgBackground = 0;
    private String nameCompany;

    private String maxSales = "0";

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


        showView = false;

        binding = FragmentMobileOrderMainBinding.inflate(getLayoutInflater());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        customProgress = CustomProgress.getInstance();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        //region Configuration Text Size
        int fontSize;
        if (LauncherActivity.screenInches >= 7)
            fontSize = 14;
        else
            fontSize = 12;


        binding.edtSearchProduct.setTextSize(fontSize);
        binding.edtNameCustomer.setTextSize(fontSize);
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
                //imgBackground = R.drawable.donyavi_pas;
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
                        binding.edtNameCustomer.setHint("نام مشترک");
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
                binding.edtNameCustomer.removeTextChangedListener(textWatcherAcc);
                binding.edtNameCustomer.setText(name);
                binding.accountRecyclerView.setVisibility(View.GONE);
                binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);
            });


            binding.btnAddAccount.setOnClickListener(v -> {
                edtNameUser.setText("");
                edtAddressUser.setText("");
                edtMobileUser.setText("");
                dialogAddAccount.show();
            });


            binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);
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

        isLastPage = false;
        isLoading = false;
        currentPage = 1;

        error = "";

        Ord_TYPE = "";
        Tbl_GUID = "";


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

        if (Inv_GUID.equals("")) {
            Inv_GUID =sharedPreferences.getString("Inv_GUID","");
            if (Inv_GUID.equals("")) {
                Inv_GUID = UUID.randomUUID().toString();
                sharedPreferences.edit().putString("Inv_GUID", Inv_GUID).apply();
            }
            counter= Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID+ "'").list().size();

            if (counter==0)
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
            else
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);
        } else {
            binding.bottomNavigationViewLinear.getMenu().getItem(0).setVisible(false);
        }


        //endregion Create Order


        //region Cast Variable Dialog
        dialogSync = new Dialog(getActivity());
        dialogSync.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSync.setContentView(R.layout.custom_dialog);
        dialogSync.setCancelable(false);

        textMessageDialog = dialogSync.findViewById(R.id.tv_message);
        ImageView ivIcon = dialogSync.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imgIconDialog);
        btnOkDialog = dialogSync.findViewById(R.id.btn_ok);
        btnNoDialog = dialogSync.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            getActivity().finish();
        });


        btnOkDialog.setOnClickListener(v -> dialogSync.dismiss());

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
                CollectionUtils.filter(resultPrd, r -> r.getI().equals(invDetail.PRD_UID));
                //  }

            }

//            if (resultPrd.size() > 0) {
//
//                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).descItem = edtDescriptionItem.getText().toString();
//                productAdapter.notifyDataSetChanged();
//            }

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


        btnNo.setOnClickListener(v -> dialogUpdate.dismiss());


        btnOk.setOnClickListener(v -> {
            dialogUpdate.dismiss();
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


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
            binding.edtSearchProduct.removeTextChangedListener(textWatcherProduct);
            binding.edtSearchProduct.setText("");
            binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);


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
            binding.edtSearchProduct.removeTextChangedListener(textWatcherProduct);
            binding.edtSearchProduct.setText("");
            binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);

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
        textWatcherProduct = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // searchProduct(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);
        //endregion Cast Product Configuration

        productAdapter = new ProductAdapter1(getActivity(), productList, Inv_GUID);
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

                counter = invDetails.size();
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);
                if (App.mode == 1)
                    binding.btnRegisterOrder.setVisibility(View.VISIBLE);
            } else
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



        return binding.getRoot();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        productList.clear();
        descriptionList.clear();
        productLevel1List.clear();
        productAdapter = null;
    }


    private void getProductLevel1() {
        try {

            Call<String> call = App.api.getProductLevel1("saleinkit_api", userName, passWord);


            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeModelProductLevel1 = new TypeToken<ModelProductLevel1>() {
                    }.getType();
                    ModelProductLevel1 iDs = gson.fromJson(response.body(), typeModelProductLevel1);

                    productLevel1List.addAll(iDs.getProductLevel1());
                    if (productLevel1List.size() == 1)
                        binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
                    productLevel1Adapter.notifyDataSetChanged();


                    if (productLevel1List.size() > 0) {
                        productLevel1List.get(0).Click = true;
                        getProductLevel2(productLevel1List.get(0).getI());
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

    private void getProductLevel2(String GuidPrdLvl1) {
        try {

            Call<String> call = App.api.getProductLevel2("saleinkit_api", userName, passWord, GuidPrdLvl1);


            call.enqueue(new Callback<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeModelProduct2 = new TypeToken<ModelProductLevel2>() {
                    }.getType();
                    ModelProductLevel2 iDs = gson.fromJson(response.body(), typeModelProduct2);

                    productLevel2List.clear();
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
                        ArrayList<ProductLevel2> rst = new ArrayList<>(productLevel2List);
                        CollectionUtils.filter(rst, r -> r.getI().equals(GuidPrdLvl1));

                        if (rst.size() > 0) {
                            binding.orderTxtError.setText("هیچ زیر دسته ای برای دسته" + rst.get(0).getN() + " وجود ندارد.");
                        }

                        productList.clear();
                        productAdapter.notifyDataSetChanged();
                        productLevel2List.clear();
                        productLevel2Adapter.notifyDataSetChanged();
                    }

                    productLevel2Adapter.notifyDataSetChanged();


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




    private void getProduct1(String GuidPrdLvl2) {
        compositeDisposable.add(
                App.api.getProduct1("saleinkit_api", userName, passWord, GuidPrdLvl2)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeModelProduct = new TypeToken<ModelProduct>() {
                            }.getType();
                            ModelProduct iDs = gson.fromJson(jsonElement, typeModelProduct);

                            productList.clear();

                           /* ArrayList<Product> list1 = new ArrayList<>(iDs.getProductList());
                            CollectionUtils.filter(list1, r -> r.getN().equals("توزیع"));

                            if (list1.size()>0)
                                sharedPreferences.edit().putString("transportId",list1.get(0).getI()).apply();*/


                            CollectionUtils.filter(iDs.getProductList(), r -> !r.getN().equals("توزیع")&&r.getPrice()>0 &&  r.getSts());



                            ArrayList<Product> list=new ArrayList<>(Util.AllProduct);
                            if (iDs.getProductList().size()>0)
                                CollectionUtils.filter(list, l -> l.getI().equals(iDs.getProductList().get(0).getI()));
                            if (list.size()==0)
                                Util.AllProduct.addAll(iDs.getProductList());



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
                            for (int i=0;i<18;i++){
                                if (productListData.size()>i)
                                    productList.add(resultPrd_.get(i));
                            }



                            productAdapter.setMaxSale(maxSales);
                            productAdapter.notifyDataSetChanged();


                            binding.progressbar.setVisibility(View.GONE);



                        }, throwable -> {


                        })
        );
    }



    private void getSettingPrice(String GUID) {


        try {

            Call<String> call = App.api.getSetting(userName, passWord);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelSetting>() {
                    }.getType();
                    ModelSetting iDs = gson.fromJson(response.body(), typeIDs);

                    sharedPreferences.edit().putString("priceProduct", iDs.getSettings().get(0).DEFAULT_PRICE_INVOICE).apply();
                    maxSales=iDs.getSettings().get(0).MAX_SALE;
                    sharedPreferences.edit().putString("maxSale", maxSales).apply();
                    getProduct1(GUID);


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

    private void showError(String error) {

        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
        customProgress.hideProgress();

    }


    public void setHomeBottomBar() {
        binding.bottomNavigationViewLinear.setSelectedItemId(R.id.homee);
    }

    public void setHomeBottomBarAndClearBadge() {
        binding.bottomNavigationViewLinear.setSelectedItemId(R.id.homee);
        binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
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


    @Override
    public void onResume() {
        super.onResume();

        /*if (showView) {
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
            Integer time = 0;
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

        }*/
    }

    @Override
    public void onPause() {
        super.onPause();

        /*if (!showView) {
            showView = true;
            Date dateOld = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sharedPreferences.edit().putString("timeOld", dateFormats.format(dateOld)).apply();


        }*/
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
                        binding.edtNameCustomer.removeTextChangedListener(textWatcherAcc);
                        binding.edtNameCustomer.setText(accountsList.get(0).N);
                        binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);
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



    private Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
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


                        List<Setting> settingsList = new ArrayList<>(iDs.getSettings());
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
                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    error = error + "\n" + "خطای تایم اوت در دریافت تنظیمات";
                     showError(error);


                }
            });


        } catch (NetworkOnMainThreadException ex) {


            //error = error + "\n" + "خطا در اتصال به سرور برای دریافت تنطیمات";
            // showError(error);
        }
    }




}
