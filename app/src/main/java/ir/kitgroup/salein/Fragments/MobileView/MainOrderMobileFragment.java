package ir.kitgroup.salein.Fragments.MobileView;

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

import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
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


import java.lang.reflect.Type;
import java.text.DecimalFormat;

import java.util.ArrayList;

import java.util.List;

import java.util.UUID;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Activities.Classes.LauncherActivity;

import ir.kitgroup.salein.Adapters.AccountAdapter;
import ir.kitgroup.salein.Adapters.DescriptionAdapter;

import ir.kitgroup.salein.Adapters.ProductAdapter1;
import ir.kitgroup.salein.Adapters.ProductLevel1Adapter;
import ir.kitgroup.salein.Adapters.ProductLevel2Adapter;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.classes.CustomProgress;


import ir.kitgroup.salein.DataBase.Account;

import ir.kitgroup.salein.DataBase.InvoiceDetail;


import ir.kitgroup.salein.models.Setting;
import ir.kitgroup.salein.DataBase.User;

import ir.kitgroup.salein.classes.PaginationScrollListener;
import ir.kitgroup.salein.classes.RecyclerViewLoadMoreScroll;
import ir.kitgroup.salein.models.Description;

import ir.kitgroup.salein.models.ModelAccount;
import ir.kitgroup.salein.models.ModelDesc;
import ir.kitgroup.salein.models.ModelLog;
import ir.kitgroup.salein.models.ModelProduct;
import ir.kitgroup.salein.models.ModelProductLevel1;
import ir.kitgroup.salein.models.ModelProductLevel2;
import ir.kitgroup.salein.models.Product;
import ir.kitgroup.salein.models.ProductLevel1;
import ir.kitgroup.salein.models.ModelSetting;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.databinding.FragmentMobileOrderMainBinding;


import ir.kitgroup.salein.models.ProductLevel2;


import static java.lang.Math.min;


public class MainOrderMobileFragment extends Fragment {

    //region Parameter
    private FragmentMobileOrderMainBinding binding;

    private SharedPreferences sharedPreferences;
    private CustomProgress customProgress;

    private String Ord_TYPE;
    private String Tbl_GUID;
    private String Inv_GUID;
    private String Inv_GUID_ORG;
    private String Acc_GUID = "";
    private Boolean Seen = false;
    private String Acc_NAME = "";


    private String userName;
    private String passWord;


    private TextWatcher textWatcherProduct;

    private ArrayList<ProductLevel1> productLevel1List;
    private ProductLevel1Adapter productLevel1Adapter;


    private ArrayList<ProductLevel2> productLevel2List;
    private ProductLevel2Adapter productLevel2Adapter;


    private Boolean emptySearch = false;
    private ArrayList<Product> productList;
    private ArrayList<Product> productListData;
    private ProductAdapter1 productAdapter;
    private boolean isLastPage;
    private boolean isLoading;
    private int currentPage;
    private int totalPage;


    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String error;

    private int counter;


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


    private int imageLogoCopy = R.drawable.salein;
    private int imageLogo = R.drawable.salein;
    private int imgIconDialog = R.drawable.saleinorder_png;
    private int imgBackground = 0;
    private String nameCompany = "Salein Order";


    //region Variable DialogUpdate
    private Dialog dialogUpdate;
    private TextView textUpdate;
    private MaterialButton btnNo;
    private String linkUpdate;
    //endregion Variable DialogUpdate

    private String maxSales = "0";


    //endregion Parameter

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentMobileOrderMainBinding.inflate(getLayoutInflater());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        customProgress = CustomProgress.getInstance();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        //region Delete InvoiceDetail UnNecessary
        if (App.mode == 1) {
            List<InvoiceDetail> invDetail = Select.from(InvoiceDetail.class).where("TBL ='" + "" + "'").list();
            for (int i = 0; i < invDetail.size(); i++) {
                InvoiceDetail.delete(invDetail.get(i));
            }
        }

        //region Delete InvoiceDetail UnNecessary


        //region Configuration Text Size
        int fontSize;
        if (Util.getSizeMobile(getActivity()).get(0) >= 7)
            fontSize = 14;
        else
            fontSize = 12;


        binding.edtSearchProduct.setTextSize(fontSize);
        binding.edtNameCustomer.setTextSize(fontSize);
        binding.orderListTvRegister.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Set Icon And Title
        try {
            switch (Util.getUser(getActivity()).name) {
                case "ir.kitgroup.salein":
                    nameCompany = "سالین دمو";
                    imageLogo = R.drawable.salein;
                    imgIconDialog = R.drawable.saleinicon128;
                    break;

                case "ir.kitgroup.saleintop":
                    nameCompany = " تاپ کباب";
                    imageLogo = R.drawable.top;
                    //  imgBackground = R.drawable.top_pas;
                    imgIconDialog = R.drawable.top_png;
                    break;


                case "ir.kitgroup.saleinmeat":
                    nameCompany = " گوشت دنیوی";
                    imageLogo = R.drawable.goosht;
                    //imgBackground = R.drawable.donyavi_pas;
                    imgIconDialog = R.drawable.meat_png;
                    linkUpdate = "https://b2n.ir/b37054";
                    break;


                case "ir.kitgroup.saleinnoon":
                    nameCompany = "کافه نون";
                    imageLogo = R.drawable.noon;
                    imgIconDialog = R.drawable.noon;
                    break;
            }
        } catch (Exception ignore) {

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
            edtNameUser = dialogAddAccount.findViewById(R.id.edt_address);
            edtMobileUser = dialogAddAccount.findViewById(R.id.edt_unit);
            edtAddressUser = dialogAddAccount.findViewById(R.id.edt_pelaque);
            RadioButton radioMan = dialogAddAccount.findViewById(R.id.radioMan);
            RadioButton radioWoman = dialogAddAccount.findViewById(R.id.radioWoman);
            MaterialButton btnRegisterAccount = dialogAddAccount.findViewById(R.id.btn_register_address);
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
                    } else if (s.toString().length() >=8) {

                        getAccountSearch1(s.toString(), 0);
                    }


                }

                @Override
                public void afterTextChanged(Editable s) {
                    int p = 0;
                }
            };


            accAdapter.setOnClickItemListener((account) -> {
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


                }
                //endregion Delete Item Transport For Show Counter
                else
                    binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();


                //region Delete Layout In Fragment When Going To MainOrderFragment For Buy Not Edit
                int size = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                if (Util.getUser(getActivity()).namePackage.equals("ir.kitgroup.salein") && size > 0)
                    size = size - 1;

                if (Inv_GUID_ORG.equals("")) {
                    for (int i = 1; i <= size; i++) {
                        getFragmentManager().popBackStack();
                    }
                }

                //endregion Delete Layout In Fragment When Going To MainOrderFragment For Buy Not Edit


                switch (item.getItemId()) {
                    case R.id.homee:

                        if (!Inv_GUID_ORG.equals("") && !Seen) {
                            getFragmentManager().popBackStack();
                        }
                        productAdapter.notifyDataSetChanged();
                        return true;


                    case R.id.orders:
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "2");//go to InVoiceDetailMobileFragment for register order first time
                        bundle.putString("Inv_GUID", Inv_GUID);
                        bundle.putBoolean("Seen", Seen);
                        bundle.putString("Tbl_GUID", Tbl_GUID);
                        bundle.putString("Ord_TYPE", Ord_TYPE);
                        bundle.putString("Acc_Name", Acc_NAME);
                        bundle.putString("Acc_GUID", Acc_GUID);
                        if (!Inv_GUID_ORG.equals("") && App.mode == 2)
                            bundle.putBoolean("EDIT", true);
                        else
                            bundle.putBoolean("EDIT", false);

                        InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
                        inVoiceDetailFragmentMobile.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailF").commit();

                        return true;


                    case R.id.profile:

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_mobile, new SettingFragment(),"SettingFragment").addToBackStack("SettingF").commit();
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
        try {
            Acc_NAME = bnd.getString("Acc_NAME");
            Acc_GUID = bnd.getString("Acc_GUID");
            Seen = bnd.getBoolean("Seen");
        } catch (Exception ignore) {
        }

        //endregion Get Bundle


        if (App.mode == 1 && !Inv_GUID_ORG.equals(Tbl_GUID)) {
            binding.edtNameCustomer.setEnabled(false);
            binding.edtNameCustomer.removeTextChangedListener(textWatcherAcc);
            binding.edtNameCustomer.setText(Acc_NAME);
            binding.accountRecyclerView.setVisibility(View.GONE);
            binding.edtNameCustomer.addTextChangedListener(textWatcherAcc);
            getAccountSearch1(Acc_NAME, 1);
        }


        //region Create Order

        if (Inv_GUID.equals("")) {
            String name;
            try {
                //Client
                name = Util.getUser(getActivity()).name.split("ir.kitgroup.")[1];
                Inv_GUID = sharedPreferences.getString(name, "");
                if (Inv_GUID.equals("")) {
                    Inv_GUID = UUID.randomUUID().toString();
                    sharedPreferences.edit().putString(name, Inv_GUID).apply();
                }
            } catch (Exception ignore) {
                Inv_GUID = UUID.randomUUID().toString();
            }


            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() > 0) {
                for (int i = 0; i < invDetails.size(); i++) {
                    if (invDetails.get(i).INV_DET_DESCRIBTION != null && invDetails.get(i).INV_DET_DESCRIBTION.equals("توزیع")) {
                        invDetails.remove(invDetails.get(i));
                    }
                }
                counter = invDetails.size();
            }


            if (counter == 0)
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
            else
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);
        } else {
            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() > 0) {
                for (int i = 0; i < invDetails.size(); i++) {
                    if (invDetails.get(i).INV_DET_DESCRIBTION != null && invDetails.get(i).INV_DET_DESCRIBTION.equals("توزیع")) {
                        invDetails.remove(invDetails.get(i));
                    }
                }
                counter = invDetails.size();
            }
            binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);
            binding.bottomNavigationViewLinear.getMenu().getItem(0).setVisible(false);
        }


        //endregion Create Order


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
            if (Util.getUser(getActivity()).namePackage.equals("ir.kitgroup.salein"))
                getFragmentManager().popBackStack();
            else
                getActivity().finish();


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
                bundle.putBoolean("Seen", Seen);
                bundle.putString("Inv_GUID", Inv_GUID);
                bundle.putString("Tbl_GUID", Tbl_GUID);
                bundle.putString("Ord_TYPE", Ord_TYPE);
                bundle.putString("Acc_Name", Acc_NAME);
                bundle.putString("Acc_GUID", Acc_GUID);
                if (!Inv_GUID_ORG.equals("") && App.mode == 2)
                    bundle.putBoolean("EDIT", true);
                else if ((App.mode == 1 && !Inv_GUID_ORG.equals(Tbl_GUID)))
                    bundle.putBoolean("EDIT", true);
                else
                    bundle.putBoolean("EDIT", false);


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
        ImageView ivIcon = dialogUpdate.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imgIconDialog);


        MaterialButton btnOk = dialogUpdate.findViewById(R.id.btn_ok);
        btnOk.setText("آپدیت");
        btnNo = dialogUpdate.findViewById(R.id.btn_cancel);
        btnNo.setText("بعدا");


        btnNo.setOnClickListener(v -> dialogUpdate.dismiss());


        btnOk.setOnClickListener(v -> {
            dialogUpdate.dismiss();
            Uri uri = Uri.parse(linkUpdate);
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

                if (s.toString().isEmpty()) {
                    emptySearch = true;
                    productList.clear();
                    productAdapter.notifyDataSetChanged();

                    if (productListData.size() > 0)
                        for (int i = 0; i < 18; i++) {
                            if (productListData.size() > i)
                                productList.add(productListData.get(i));
                        }
                    productAdapter.setMaxSale(maxSales);
                    productAdapter.notifyDataSetChanged();
                } else {
                    emptySearch = false;
                    getSearchProduct(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);
        //endregion Cast Product Configuration

        productAdapter = new ProductAdapter1(getActivity(), productList);
        productAdapter.setInv_GUID(Inv_GUID);
        productAdapter.setTbl_GUID(Tbl_GUID);
        productAdapter.setType(Seen);
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

    }


    private void getProductLevel1() {

        if (!isNetworkAvailable(getActivity())) {
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }

        try {
            compositeDisposable.add(
                    App.api.getProductLevel1("saleinkit_api", userName, passWord)
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
        if (!isNetworkAvailable(getActivity())) {
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }
        try {
            binding.progressbar.setVisibility(View.VISIBLE);
            compositeDisposable.add(
                    App.api.getProductLevel2("saleinkit_api", userName, passWord, GuidPrdLvl1)
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
                                        CollectionUtils.filter(iDs.getProductLevel2(), i -> i.getSts());
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
        if (!isNetworkAvailable(getActivity())) {
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }
        try {
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
                                        CollectionUtils.filter(iDs.getProductList(), r -> !r.getN().contains("توزیع") && r.getPrice() > 0 && r.getSts());


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
                    App.api.getSetting1(userName, passWord)
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
            error = "خطا در اتصال به سرور برای دریافت تنطیمات";
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }

    }

    private void getDescription(String userName, String pass, String id) {
        if (!isNetworkAvailable(getActivity())) {
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }

        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات...", false);

        try {
            compositeDisposable.add(
                    App.api.getDescription1(userName, pass, id)
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
        imageLogoCopy = imgIconDialog;
        ivIconSync.setImageResource(imageLogoCopy);
        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
        customProgress.hideProgress();

    }


    private void ShowErrorConnection(String error) {
        textMessageDialog.setText(error);
        ivIconSync.setImageResource(R.drawable.ic_wifi);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
        customProgress.hideProgress();

    }


    public void setHomeBottomBar() {
        if (Seen)
            getFragmentManager().popBackStack();
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


    private static class JsonObjectAccount {
        public List<Account> Account;
    }

    private void addAccount(String userName, String pass, List<Account> accounts) {

        if (!isNetworkAvailable(getActivity())) {
            ShowErrorConnection("خطا در اتصال به اینترنت");
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
                    App.api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "")
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
                                Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری" , Toast.LENGTH_SHORT).show();
                                customProgress.hideProgress();

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ثبت مشتری" , Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();
        }



    }

    private void getAccountSearch1(String word, int type) {
        try {
            compositeDisposable.add(
                    App.api.getAccountSearch1("saleinkit_api", userName, passWord, word)
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

                                assert iDs != null;
                                if (iDs.getAccountList() == null) {
                                    Type typeIDs0 = new TypeToken<ModelLog>() {
                                    }.getType();
                                    ModelLog iDs0 = gson.fromJson(jsonElement, typeIDs0);


                                } else {

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
                                customProgress.hideProgress();

                            }, throwable -> {


                            })
            );
        } catch (Exception e) {

        }
    }


    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }


    private void getSetting() {

        try {
            compositeDisposable.add(
                    App.api.getSetting1(userName, passWord)
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

    @SuppressLint("SetTextI18n")
    private void getSearchProduct(String s) {
        try {
            compositeDisposable.add(
                    App.api.getSearchProduct("saleinkit_api", userName, passWord, s)
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
                                            return;
                                        }

                                        if (iDs == null) {
                                            return;
                                        }

                                        if (!emptySearch) {
                                            productList.clear();

                                            CollectionUtils.filter(iDs.getProductList(), r -> !r.getN().contains("توزیع") && r.getPrice() > 0 && r.getSts());
                                            if (iDs.getProductList().size() > 0)
                                                for (int i = 0; i < 18; i++) {
                                                    if (iDs.getProductList().size() > i)
                                                        productList.add(iDs.getProductList().get(i));
                                                }
                                            productAdapter.setMaxSale(maxSales);
                                            productAdapter.notifyDataSetChanged();
                                        }


                                    }
                                    , throwable -> {
                                    })
            );
        } catch (Exception e) {
        }

    }

    private boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
