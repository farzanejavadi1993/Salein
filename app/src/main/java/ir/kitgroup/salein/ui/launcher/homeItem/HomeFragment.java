package ir.kitgroup.salein.ui.launcher.homeItem;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.Activities.LauncherActivity;
import ir.kitgroup.salein.BR;

import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Salein;
import ir.kitgroup.salein.DataBase.Product;

import ir.kitgroup.salein.DataBase.Unit;
import ir.kitgroup.salein.adapter.UniversalAdapter2;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.classes.dialog.DialogInstance;
import ir.kitgroup.salein.databinding.HomeFragmentBinding;
import ir.kitgroup.salein.models.CustomTab;
import ir.kitgroup.salein.Connect.CompanyViewModel;
import ir.kitgroup.salein.classes.CustomProgress;

import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.Setting;
import ir.kitgroup.salein.models.Description;
import ir.kitgroup.salein.models.ProductLevel1;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.models.ProductLevel2;
import ir.kitgroup.salein.ui.launcher.searchItem.SearchProductFragmentDirections;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;

    private HomeFragmentBinding binding;

    private CompanyViewModel myViewModel;


    private ArrayList<ProductLevel1> productLevel1List;
    private ProductLevel1Adapter productLevel1Adapter;


    private ArrayList<ProductLevel2> productLevel2List;
    private ProductLevel2Adapter productLevel2Adapter;


    private ArrayList<CustomTab> customTabList;
    private CustomTabAdapter customTabAdapter;


    private ArrayList<ir.kitgroup.salein.models.Product> productList;
    private UniversalAdapter2 productAdapter;


    //    private TextView txtError;
//    private EditText productAmountTxt;
//    private ImageView ivMax;
//    private ImageView ivMinus;
    private ProgressBar progressBarCheckRemainProduct;


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescription;
    private ArrayList<Description> descriptionList;
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv;
    //endregion Variable Dialog Description


    private Dialog dialogRequestAgain;


    private String checkRemainProduct = "0";//For Check Inventory If Its Amount ==1  Default Amount iS 0

    private String GuidProductLvl2 = "";//It Is GUID Of ProductLevel2 Item For Get Product By Using This GUID

    private String Transport_GUID = "";

    private int keyCustomTab = 0;//It Is Number Of CustomTab Item For Get Product By Using This Number

    private String Inv_GUID = "";

    private Salein appInfo;
    private Company company;
    private String userName;
    private String passWord;
    private Boolean disableAccount;

    private CustomProgress customProgress;
    private DecimalFormat format;
    private DecimalFormat df;
    private String defaultCoff;

    private String valueOfCloseDay;
    private ArrayList<String> closeDayList;

    DialogInstance dialogInstance;
    int pageMain = 1;

    private String sWord = "";

    MaterialButton btnOk;
    MaterialButton btnNo;
    TextView tvMessage;

    //endregion Parameter


    //region Override Method
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        resetFilter();
        createOrderForm();
        setCounterForAmountOfOrder();
        castDialogRequestAgain();
        castDialogDescription();
        castRecyclerViewProductLevel1();
        castRecyclerViewProductLevel2();
        castRecyclerViewCustomProduct();
        castRecyclerViewProduct();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        binding.progressbar.setVisibility(View.VISIBLE);
        binding.animationView.setVisibility(View.VISIBLE);

        resetData();

        binding.orderTxtError.setText("");
        binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
        binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);

        myViewModel.getSetting(userName, passWord);
        myViewModel.getUnit(userName, passWord);
        myViewModel.getInquiryAccount(userName, passWord, Select.from(Account.class).first().getM());


        myViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            myViewModel.getResultSetting().setValue(null);

            List<Setting> settingsList = new ArrayList<>(result);
            if (settingsList.size() > 0) {

                //region  closeDayList
                String CloseDay = settingsList.get(0).CLOSE_DAY;
                sharedPreferences.edit().putString("close_day", CloseDay).apply();
                closeDayList.clear();
                if (!CloseDay.equals("")) {
                    closeDayList = new ArrayList<>(Arrays.asList(CloseDay.split(",")));
                }
                //endregion  closeDayList

                sharedPreferences.edit().putString("Default_ACCOUNT", settingsList.get(0).DEFAULT_CUSTOMER).apply();

                sharedPreferences.edit().putString("payment_link", settingsList.get(0).LINK_PAYMENT != null ? settingsList.get(0).LINK_PAYMENT : "").apply();

                sharedPreferences.edit().putString("coff", settingsList.get(0).COEF != null ? settingsList.get(0).COEF : "0").apply();

                sharedPreferences.edit().putString("Transport_GUID", settingsList.get(0).PEYK != null ? settingsList.get(0).PEYK : "").apply();

                //region menu
                sharedPreferences.edit().putString("menu",
                        settingsList.get(0).MENU != null && !settingsList.get(0).MENU.equals("")
                                ? settingsList.get(0).MENU
                                : "0").apply();

                String menu = sharedPreferences.getString("menu", "0");

                if (!menu.equals("0")) {
                    myViewModel.getCustomTab(userName, passWord);
                    binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
                    binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
                    binding.orderRecyclerViewCustomTab.setVisibility(View.VISIBLE);
                } else {
                    if (sharedPreferences.getBoolean("discount", false))
                        myViewModel.getDiscountProduct(userName, passWord);

                    else if (sharedPreferences.getBoolean("vip", false))
                        myViewModel.getVipProduct(userName, passWord, Select.from(Account.class).first().getI());

                    else
                        myViewModel.getProductLevel1(userName, passWord);

                    binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
                    binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);
                    binding.orderRecyclerViewCustomTab.setVisibility(View.GONE);
                }
                //endregion menu
            }
        });
        myViewModel.getResultProductLevel1().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultProductLevel1().setValue(null);

            binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);
            binding.orderTxtError.setVisibility(View.GONE);

            productLevel1List.clear();
            productLevel1List.addAll(result);

            CollectionUtils.filter(result, ProductLevel1::getSts);
            if (productLevel1List.size() == 1)
                binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);

            productLevel1Adapter.notifyDataSetChanged();


            if (productLevel1List.size() > 0) {
                productLevel1List.get(0).Click = true;
                myViewModel.getProductLevel2(userName, passWord, productLevel1List.get(0).getI());
            } else {
                binding.progressbar.setVisibility(View.GONE);
                binding.animationView.setVisibility(View.GONE);
                binding.orderTxtError.setText("هیچ گروهی از کالاها موجود نیست");
                binding.orderTxtError.setVisibility(View.VISIBLE);
            }

        });
        myViewModel.getResultProductLevel2().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultProductLevel2().setValue(null);

            binding.orderTxtError.setVisibility(View.GONE);
            productLevel2List.clear();
            CollectionUtils.filter(result, ProductLevel2::getSts);
            productLevel2List.addAll(result);

            if (productLevel2List.size() > 0) {
                ArrayList<ProductLevel2> list = new ArrayList<>(productLevel2List);
                CollectionUtils.filter(list, l -> l.getTkn() != 0);

                if (list.size() > 0) {
                    try {
                        ArrayList<ProductLevel2> fulList = new ArrayList<>(list);
                        for (int i = 0; i < fulList.size(); i++) {
                            for (int j = 0; j < list.size(); j++) {
                                ArrayList<ProductLevel2> res = new ArrayList<>(list);
                                int finalJ = j;
                                CollectionUtils.filter(res, r -> r.getTkn() < list.get(finalJ).getTkn());
                                if (res.size() == 1) {
                                    j = list.size();
                                    int index = productLevel2List.indexOf(res.get(0));//old position
                                    if (productLevel2List.size() > i) {
                                        ProductLevel2 itemProductGroupLevel2 = productLevel2List.get(i);
                                        if (index != i) {
                                            productLevel2List.set(i, productLevel2List.get(productLevel2List.indexOf(res.get(0))));
                                            productLevel2List.set(index, itemProductGroupLevel2);
                                        }
                                    }
                                    list.remove(res.get(0));
                                }
                            }

                        }
                    } catch (Exception ignored) {
                    }
                }

                if (productLevel2List.size() > 0) {
                    productLevel2List.get(0).Click = true;
                }

                //region Full ProductList Because First Item ProductLevel2 Is True
                GuidProductLvl2 = productLevel2List.get(0).getI();
                myViewModel.getSettingPrice(userName, passWord);
                //endregion Full ProductList Because First Item ProductLevel2 Is True
            } else {
                binding.progressbar.setVisibility(View.GONE);
                binding.animationView.setVisibility(View.GONE);
                binding.orderTxtError.setText("هیچ زیرگروهی برای این گروه کالایی وجود ندارد.");
                binding.orderTxtError.setVisibility(View.VISIBLE);
                productList.clear();
                productAdapter.notifyDataSetChanged();
                productLevel2List.clear();
            }
            productLevel2Adapter.notifyDataSetChanged();
        });
        myViewModel.getResultSettingPrice().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultSettingPrice().setValue(null);

            customProgress.hideProgress();
            sharedPreferences.edit().putString("priceProduct", result.get(0).DEFAULT_PRICE_INVOICE).apply();
            checkRemainProduct = result.get(0).MAX_SALE;
            sharedPreferences.edit().putString("maxSale", checkRemainProduct).apply();


            String menu = sharedPreferences.getString("menu", "");
            if (!menu.equals("0"))
                myViewModel.getProductCustomTab(userName, passWord, keyCustomTab);
            else
                myViewModel.getListProduct(userName, passWord, GuidProductLvl2);
        });
        myViewModel.getResultListProduct().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultListProduct().setValue(null);

            binding.progressbar.setVisibility(View.GONE);
            binding.animationView.setVisibility(View.GONE);
            customProgress.hideProgress();
            binding.orderTxtError.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
            ArrayList<ir.kitgroup.salein.models.Product> resultPrd_ = new ArrayList<>(result);
            ArrayList<ir.kitgroup.salein.models.Product> listPrd = new ArrayList<>(resultPrd_);
            CollectionUtils.filter(listPrd, l -> l.getKey() != 0);
            if (listPrd.size() > 0)
                for (int i = 0; i < listPrd.size(); i++) {
                    int position = listPrd.get(i).getKey() - 1;//new position
                    int index = resultPrd_.indexOf(listPrd.get(i));//old position
                    if (resultPrd_.size() > position) {
                        ir.kitgroup.salein.models.Product itemProduct = resultPrd_.get(position);
                        if (index != position) {
                            resultPrd_.set(position, resultPrd_.get(resultPrd_.indexOf(listPrd.get(i))));
                            resultPrd_.set(index, itemProduct);

                        }
                    }
                }

            productList.clear();
            productList.addAll(resultPrd_);


            if (productList.size() == 0) {
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست");
            }
            productAdapter.notifyDataSetChanged();
        });
        myViewModel.geResultCustomTab().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.geResultCustomTab().setValue(null);
            binding.orderTxtError.setVisibility(View.GONE);
            customTabList.clear();
            customTabList.addAll(result);


            if (customTabList.size() > 0) {

                if (customTabList.size() > 0) {
                    customTabList.get(0).Click = true;
                }

                //region Full ProductList Because First Item ProductLevel2 Is True
                keyCustomTab = customTabList.get(0).getT();
                myViewModel.getSettingPrice(userName, passWord);
                //endregion Full ProductList Because First Item ProductLevel2 Is True

            } else {
                binding.orderTxtError.setText("هیچ منویی وجود ندارد.");
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.GONE);
                binding.animationView.setVisibility(View.GONE);
                productList.clear();
                productAdapter.notifyDataSetChanged();
                customTabList.clear();

            }
            customTabAdapter.notifyDataSetChanged();
        });
        myViewModel.getResultProductCustomTab().observe(getViewLifecycleOwner(), result -> {

            binding.progressbar.setVisibility(View.GONE);
            binding.animationView.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;

            myViewModel.getResultListProduct().setValue(null);
            binding.orderTxtError.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
            ArrayList<ir.kitgroup.salein.models.Product> resultPrd_ = new ArrayList<>(result);
            ArrayList<ir.kitgroup.salein.models.Product> listPrd = new ArrayList<>(resultPrd_);
            CollectionUtils.filter(listPrd, l -> l.getKey() != 0);
            if (listPrd.size() > 0)
                for (int i = 0; i < listPrd.size(); i++) {
                    int position = listPrd.get(i).getKey() - 1;//new position
                    int index = resultPrd_.indexOf(listPrd.get(i));//old position
                    if (resultPrd_.size() > position) {
                        ir.kitgroup.salein.models.Product itemProduct = resultPrd_.get(position);
                        if (index != position) {
                            resultPrd_.set(position, resultPrd_.get(resultPrd_.indexOf(listPrd.get(i))));
                            resultPrd_.set(index, itemProduct);

                        }
                    }
                }


            productList.clear();
            productList.addAll(resultPrd_);


            if (productList.size() == 0) {
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست");
            }
            productAdapter.notifyDataSetChanged();

        });
        myViewModel.getResultDescription().observe(getViewLifecycleOwner(), result -> {
            customProgress.hideProgress();
            if (result == null)
                return;
            myViewModel.getResultDescription().setValue(null);
            descriptionList.clear();
            descriptionList.addAll(result);

            for (int i = 0; i < descriptionList.size(); i++) {
                if (edtDescription.getText().toString().contains("'" + descriptionList.get(i).DSC + "'")) {
                    descriptionList.get(i).Click = true;
                }

            }
            descriptionAdapter.notifyDataSetChanged();

            dialogDescription.show();
        });
        myViewModel.getResultDiscountProduct().observe(getViewLifecycleOwner(), result -> {
            binding.ivFilter.setImageResource(R.drawable.ic_filter_active);
            customProgress.hideProgress();
            binding.progressbar.setVisibility(View.GONE);
            binding.animationView.setVisibility(View.GONE);
            if (result == null)
                return;
            myViewModel.getResultDiscountProduct().setValue(null);

            binding.orderTxtError.setVisibility(View.GONE);
            sharedPreferences.edit().putBoolean("discount", true).apply();

            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts() &&
                    !i.getI().equalsIgnoreCase(Transport_GUID));


            productList.clear();
            productList.addAll(result);


            if (productList.size() == 0) {

                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست ، برای مشاهده کامل کالاها فیلترها را حذف کنید.");
            }


            // productAdapter.setMaxSale(maxSales);
            productAdapter.notifyDataSetChanged();


        });
        myViewModel.getResultVipProduct().observe(getViewLifecycleOwner(), result -> {
            binding.ivFilter.setImageResource(R.drawable.ic_filter_active);
            customProgress.hideProgress();
            binding.progressbar.setVisibility(View.GONE);
            binding.animationView.setVisibility(View.GONE);
            if (result == null)
                return;

            binding.orderTxtError.setVisibility(View.GONE);
            myViewModel.getResultVipProduct().setValue(null);
            sharedPreferences.edit().putBoolean("vip", true).apply();

            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts() && !i.getI().equalsIgnoreCase(Transport_GUID));


            productList.clear();
            productList.addAll(result);
            if (result.size() > 0)
                for (int i = 0; i < 18; i++) {
                    if (result.size() > i)
                        productList.add(result.get(i));
                }


            if (productList.size() == 0) {

                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست ، برای مشاهده کامل کالاها فیلترها را حذف کنید.");
            }

            productAdapter.notifyDataSetChanged();
            binding.progressbar.setVisibility(View.GONE);
            binding.animationView.setVisibility(View.GONE);

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;


            binding.progressbar.setVisibility(View.GONE);
            customProgress.hideProgress();

            if (result.getCode() == -4) {
                binding.ivFilter.setImageResource(R.drawable.ic_filter_active);
                sharedPreferences.edit().putBoolean("vip", false).apply();
                sharedPreferences.edit().putBoolean("discount", false).apply();
            }

            disableAccount = sharedPreferences.getBoolean("disableAccount", false);
            if (disableAccount) {
                productList.clear();
                productLevel2List.clear();
                productLevel1List.clear();
                productAdapter.notifyDataSetChanged();
                productLevel1Adapter.notifyDataSetChanged();
                productLevel2Adapter.notifyDataSetChanged();
                showError(result.getName(), 0);
                return;
            }
            if (result.getCode() == -1) {
                showError(result.getName(), 1);
            }


        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //endregion Override Method


    //region Custom Method
    private void showError(String error, int type) {
        tvMessage.setText(error);
        btnNo.setText("بستن");
        btnOk.setText("سینک مجدد");
        dialogRequestAgain.dismiss();
        if (type == 0)
            btnOk.setVisibility(View.GONE);
        else if (type == 1)
            btnOk.setVisibility(View.VISIBLE);
        else {
            btnOk.setVisibility(View.GONE);
            dialogRequestAgain.setCancelable(false);
        }

        dialogRequestAgain.show();
        customProgress.hideProgress();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void showData() {
        resetFilter();
        binding.progressbar.setVisibility(View.VISIBLE);
        binding.animationView.setVisibility(View.VISIBLE);
        resetData();
        binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
        binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);
        myViewModel.getSetting(userName, passWord);
        myViewModel.getUnit(userName, passWord);
        myViewModel.getInquiryAccount(userName, passWord, Select.from(Account.class).first().getM());


    }

    private void getValueOfCloseDay() {
        Calendar calendar = Calendar.getInstance();
        switch (calendar.getTime().getDay()) {

            case 0:
                valueOfCloseDay = "1";
                break;
            case 1:
                valueOfCloseDay = "2";
                break;
            case 2:
                valueOfCloseDay = "3";
                break;
            case 3:
                valueOfCloseDay = "4";
                break;
            case 4:
                valueOfCloseDay = "5";
                break;
            case 5:
                valueOfCloseDay = "6";
                break;
            case 6:
                valueOfCloseDay = "0";
                break;
        }

    }

    private void init() {
        Inv_GUID = "";

        format = new DecimalFormat("#,###,###,###");
        df = new DecimalFormat();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Product.deleteAll(Product.class);

        customProgress = CustomProgress.getInstance();

        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");

        company = Select.from(Company.class).first();
        userName = company.getUser();
        passWord = company.getPass();
        appInfo = Select.from(Salein.class).first();

        binding.tvNameStore.setText(company.getN());

        Glide.with(this).asGif().load(Uri.parse(appInfo.getGif_url())).into(binding.animationView);

        productLevel1List = new ArrayList<>();
        productLevel2List = new ArrayList<>();
        customTabList = new ArrayList<>();
        productList = new ArrayList<>();
        descriptionList = new ArrayList<>();
        closeDayList = new ArrayList<>();

        dialogInstance = DialogInstance.getInstance();

        defaultCoff = sharedPreferences.getString("coff", "0");

        binding.ivFilter.setOnClickListener(v -> {
            boolean filter = binding.orderRecyclerViewProductLevel2.getVisibility() == View.GONE;
            NavDirections action = HomeFragmentDirections.actionGoToFilterFragment(filter);
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });

        binding.btnCompanyBranches.setOnClickListener(view1 -> {
            if (company.Parent != null) {
                NavDirections action = HomeFragmentDirections.actionGoToAllCompanyFragment(company.getPi());
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
        });
    }

    private void resetFilter() {
        sharedPreferences.edit().putBoolean("vip", false).apply();
        sharedPreferences.edit().putBoolean("discount", false).apply();
        binding.ivFilter.setImageResource(R.drawable.ic_filter);
    }

    private void createOrderForm() {
        try {
            Inv_GUID = HomeFragmentArgs.fromBundle(getArguments()).getInvGUID();
            getArguments().clear();
        } catch (Exception ignored) {
            Inv_GUID = "";
        }


        if (Inv_GUID.equals("")) {
            ((LauncherActivity) getActivity()).setShowProfileItem(true);


            Inv_GUID = sharedPreferences.getString(company.getI(), "");

            if (Inv_GUID.equals("")) {
                Inv_GUID = UUID.randomUUID().toString();
                sharedPreferences.edit().putString(company.getI(), Inv_GUID).apply();
            }
        } else {
            ((LauncherActivity) getActivity()).setShowProfileItem(false);
        }

        //Save GUID Order Form To Use In App
        sharedPreferences.edit().putString("Inv_GUID", Inv_GUID).apply();
    }

    private void setCounterForAmountOfOrder() {
        Inv_GUID = sharedPreferences.getString("Inv_GUID", "");
        int counter = 0;
        List<InvoiceDetail> invDetailses = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
        if (invDetailses.size() > 0) {
            CollectionUtils.filter(invDetailses, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
            counter = invDetailses.size();
        }

        if (counter == 0)
            ((LauncherActivity) getActivity()).setClearCounterOrder();
        else
            ((LauncherActivity) getActivity()).setCounterOrder(counter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void castDialogRequestAgain() {
        Account account = Select.from(Account.class).first();

        dialogRequestAgain = dialogInstance.dialog(getActivity(), false, R.layout.custom_dialog);

        MaterialButton btnOk = dialogRequestAgain.findViewById(R.id.btn_ok);

        MaterialButton btnNo = dialogRequestAgain.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(view -> {
            dialogRequestAgain.dismiss();

            resetFilter();
            resetData();
            binding.animationView.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);


            myViewModel.getInquiryAccount(userName, passWord, account.getM());
            myViewModel.getUnit(userName, passWord);
            myViewModel.getSetting(userName, passWord);


           /* String menu = sharedPreferences.getString("menu", "0");
            if (!menu.equals("0")) {
                myViewModel.getCustomTab(userName, passWord);
                binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
                binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
                binding.orderRecyclerViewCustomTab.setVisibility(View.VISIBLE);
            }

            else {
                myViewModel.getProductLevel1(userName, passWord);
                binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
                binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);
                binding.orderRecyclerViewCustomTab.setVisibility(View.GONE);
            }*/

        });

        btnNo.setOnClickListener(view -> {
            dialogRequestAgain.dismiss();

            if (disableAccount) {
                if (!appInfo.getSalein())
                    getActivity().finish();
                else
                    Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void castDialogDescription() {
        DialogInstance dialogInstance = DialogInstance.getInstance();

        dialogDescription = dialogInstance.dialog(getActivity(), false, R.layout.dialog_description);

        edtDescription = dialogDescription.findViewById(R.id.edt_description);
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
                String description = edtDescription.getText().toString();
                edtDescription.setText(description + "   " + "'" + desc + "'");
            } else {
                descriptionList.get(position).Click = false;
                if (edtDescription.getText().toString().contains("'" + desc + "'"))

                    edtDescription.setText(edtDescription.getText().toString().replace("   " + "'" + desc + "'", ""));
            }
        });

        btnRegisterDescription.setOnClickListener(v -> {
            InvoiceDetail invDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + GuidInv + "'").first();
            if (invDetail != null) {
                invDetail.INV_DET_DESCRIBTION = edtDescription.getText().toString();
                invDetail.update();
            }
            productAdapter.notifyDataSetChanged();
            dialogDescription.dismiss();
        });

        edtDescription.addTextChangedListener(new TextWatcher() {
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private void castRecyclerViewProductLevel1() {

        productLevel1Adapter = new ProductLevel1Adapter(getActivity(), productLevel1List);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);
        binding.orderRecyclerViewProductLevel1.setLayoutManager(manager);
        binding.orderRecyclerViewProductLevel1.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProductLevel1.setAdapter(productLevel1Adapter);


        productLevel1Adapter.SetOnItemClickListener(GUID -> {
            myViewModel.clear();
            binding.animationView.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
            pageMain = 1;
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
            productAdapter.notifyDataSetChanged();

            productLevel2List.clear();
            productLevel2Adapter.notifyDataSetChanged();

            //region Full ProductLevel2List Because This Item ProductLevel1 Is True
            myViewModel.getProductLevel2(userName, passWord, GUID);
            //endregion Full ProductLevel2List Because This Item ProductLevel1 Is True
        });
        //endregion CONFIGURATION DATA PRODUCT_LEVEL1
    }

    @SuppressLint("NotifyDataSetChanged")
    private void castRecyclerViewProductLevel2() {

        productLevel2Adapter = new ProductLevel2Adapter(getActivity(), productLevel2List);
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager2.setReverseLayout(true);
        binding.orderRecyclerViewProductLevel2.setLayoutManager(manager2);
        binding.orderRecyclerViewProductLevel2.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProductLevel2.setAdapter(productLevel2Adapter);


        productLevel2Adapter.SetOnItemClickListener(GUID -> {
            myViewModel.clear();
            binding.animationView.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);

            productList.clear();
            productAdapter.notifyDataSetChanged();

            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
            pageMain = 1;
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

            //regionFull ProductList Because This Item ProductLevel1 Is True
            GuidProductLvl2 = GUID;
            myViewModel.getSettingPrice(userName, passWord);
            //endregionFull ProductList Because This Item ProductLevel1 Is True
        });
        //endregion CONFIGURATION DATA PRODUCT_LEVEL_2
    }

    @SuppressLint("NotifyDataSetChanged")
    private void castRecyclerViewCustomProduct() {

        customTabAdapter = new CustomTabAdapter(getActivity(), customTabList);
        LinearLayoutManager managerCustom = new LinearLayoutManager(getContext());
        managerCustom.setOrientation(LinearLayoutManager.HORIZONTAL);
        managerCustom.setReverseLayout(true);
        binding.orderRecyclerViewCustomTab.setLayoutManager(managerCustom);
        binding.orderRecyclerViewCustomTab.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewCustomTab.setAdapter(customTabAdapter);


        customTabAdapter.SetOnItemClickListener(key -> {
            binding.animationView.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
            productList.clear();
            productAdapter.notifyDataSetChanged();
            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
            pageMain = 1;
            binding.orderTxtError.setText("");


            //region UnClick Old Item
            ArrayList<CustomTab> resultCustomTab = new ArrayList<>(customTabList);
            CollectionUtils.filter(resultCustomTab, r -> r.Click);
            if (resultCustomTab.size() > 0 && customTabList.size() > 0) {
                customTabList.get(customTabList.indexOf(resultCustomTab.get(0))).Click = false;
            }
            //endregion UnClick Old Item


            //region Click New Item
            ArrayList<CustomTab> resCustomTab = new ArrayList<>(customTabList);
            CollectionUtils.filter(resCustomTab, r -> r.getT() == key);
            if (customTabList.size() > 0) {
                customTabList.get(customTabList.indexOf(resCustomTab.get(0))).Click = true;
            }
            //endregion Click New Item


            customTabAdapter.notifyDataSetChanged();

            //region Full ProductList Because This Item ProductLevel1 Is True
            keyCustomTab = key;
            myViewModel.getSettingPrice(userName, passWord);
            //endregion Full ProductList Because This Item ProductLevel1 Is True
        });

        //endregion Click Item CustomTab


    }

    @SuppressLint("SetTextI18n")
    private void castRecyclerViewProduct() {
        productAdapter = new UniversalAdapter2(R.layout.order_recycle_products_item_mobile, productList, BR.product);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.orderRecyclerViewProduct.setLayoutManager(linearLayoutManager);
        binding.orderRecyclerViewProduct.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProduct.setAdapter(productAdapter);


        productAdapter.setOnItemBindListener((binAdapter, position) -> {
            if (position < productList.size()) {
                View viewAdapter = binAdapter.getRoot();
                getValueOfCloseDay();


                TextView txtError = viewAdapter.findViewById(R.id.error);
                EditText productAmountTxt = viewAdapter.findViewById(R.id.order_recycle_item_product_txt_amount);
                RelativeLayout cardEdit = viewAdapter.findViewById(R.id.card_edit);
                TextView edtDesc = viewAdapter.findViewById(R.id.edt_description_temp);
                RelativeLayout layoutDiscount = viewAdapter.findViewById(R.id.layout_discount);
                TextView unit = viewAdapter.findViewById(R.id.unit);
                TextView productPrice = viewAdapter.findViewById(R.id.order_recycle_item_product_price);
                TextView productOldPrice = viewAdapter.findViewById(R.id.order_recycle_item_product_old_price);
                TextView productDiscountPercent = viewAdapter.findViewById(R.id.order_recycle_item_product_discountPercent);
                RoundedImageView productImage = viewAdapter.findViewById(R.id.order_recycle_item_product_img);
                ImageView ivMinus = viewAdapter.findViewById(R.id.iv_minus);
                ImageView ivMax = viewAdapter.findViewById(R.id.iv_max);
                progressBarCheckRemainProduct = viewAdapter.findViewById(R.id.progress);


                txtError.setText("");

                productAmountTxt.setEnabled(closeDayList == null || closeDayList.size() <= 0 || !closeDayList.contains(valueOfCloseDay));


                InvoiceDetail ivDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "' AND PRDUID ='" + productList.get(position).getI() + "'").first();


                List<Unit> unitList = Select.from(Unit.class).list();
                ArrayList<Unit> units = new ArrayList<>(unitList);
                CollectionUtils.filter(units, u -> u.getUomUid().equals(productList.get(position).UM1));
                if (units.size() > 0)
                    unit.setText(units.get(0).getUomName());
                else
                    unit.setText("");


                if (productList.get(position).getPrice(sharedPreferences) > 0) {
                    productPrice.setText(format.format(productList.get(position).getPrice(sharedPreferences)) + " ریال ");
                    productDiscountPercent.setText("");
                    productOldPrice.setText("");
                    layoutDiscount.setVisibility(View.GONE);
                    productDiscountPercent.setVisibility(View.GONE);
                    productOldPrice.setVisibility(View.GONE);

                    if (productList.get(position).getPercDis() != 0.0) {
                        layoutDiscount.setVisibility(View.VISIBLE);
                        productDiscountPercent.setVisibility(View.VISIBLE);
                        productOldPrice.setVisibility(View.VISIBLE);
                        productOldPrice.setPaintFlags(productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        productDiscountPercent.setText(format.format(productList.get(position).getPercDis()) + "%");
                        productOldPrice.setText(format.format(productList.get(position).getPrice(sharedPreferences)));
                        double discountPrice = productList.get(position).getPrice(sharedPreferences) * (productList.get(position).getPercDis() / 100);
                        double newPrice = productList.get(position).getPrice(sharedPreferences) - discountPrice;
                        productPrice.setText(format.format(newPrice) + " ریال ");
                    }
                }


                AtomicInteger tab = new AtomicInteger();
                productImage.setOnClickListener(view1 -> {
                    tab.getAndIncrement();
                    if (tab.get() == 2) {
                        tab.set(0);
                        NavDirections action = SearchProductFragmentDirections.actionGoToShowDetailFragment(productList.get(position).getI());
                        Navigation.findNavController(binding.getRoot()).navigate(action);
                    }
                });


                double amount;
                if (ivDetail != null && ivDetail.INV_DET_QUANTITY != null)
                    amount = ivDetail.INV_DET_QUANTITY;
                else
                    amount = 0.0;


                if (amount > 0) {
                    ivMinus.setVisibility(View.VISIBLE);
                    productAmountTxt.setVisibility(View.VISIBLE);
                }
                else {
                    ivMinus.setVisibility(View.GONE);
                    productAmountTxt.setVisibility(View.GONE);
                }


                String description;
                if (ivDetail != null && ivDetail.INV_DET_DESCRIBTION != null)
                    description = ivDetail.INV_DET_DESCRIBTION;
                else
                    description = "";
                edtDesc.setText(description);


                productAmountTxt.setOnFocusChangeListener((view1, b) -> {
                   // productAmountTxt = viewAdapter.findViewById(R.id.order_recycle_item_product_txt_amount);
                    productAmountTxt.setCursorVisible(true);
                });
                productAmountTxt.setText(df.format(amount));


                if (checkRemainProduct.equals("1"))
                    myViewModel.getMaxSale(userName, passWord, productList.get(position), position, "remain");


                ivMax.setOnClickListener(v -> {
                    if (closeDayList.size() > 0 && closeDayList.contains(valueOfCloseDay)) {
                        showAlertDialog("فروشگاه تعطیل می باشد.");
                        return;
                    }

                   /* productAmountTxt = viewAdapter.findViewById(R.id.order_recycle_item_product_txt_amount);
                    ivMax = viewAdapter.findViewById(R.id.iv_max);
                    ivMinus = viewAdapter.findViewById(R.id.iv_minus);
                    txtError = viewAdapter.findViewById(R.id.error);
                    progressBarCheckRemainProduct = viewAdapter.findViewById(R.id.progress);*/
                    txtError.setText("");

                    if (checkRemainProduct.equals("1"))
                        myViewModel.getMaxSale(userName, passWord, productList.get(position), position, "max");
                    else {
                        productAmountTxt.setCursorVisible(false);
                        calculate(position, "max");
                    }

                });

                ivMinus.setOnClickListener(v -> {
                    if (closeDayList.size() > 0 && closeDayList.contains(valueOfCloseDay)) {
                        showAlertDialog("فروشگاه تعطیل می باشد.");
                        return;
                    }
                  /*  productAmountTxt = viewAdapter.findViewById(R.id.order_recycle_item_product_txt_amount);
                    ivMax = viewAdapter.findViewById(R.id.iv_max);
                    ivMinus = viewAdapter.findViewById(R.id.iv_minus);
                    txtError = viewAdapter.findViewById(R.id.error);
                    progressBarCheckRemainProduct = viewAdapter.findViewById(R.id.progress);*/
                    txtError.setText("");

                    if (checkRemainProduct.equals("1"))
                        myViewModel.getMaxSale(userName, passWord, productList.get(position), position, "min");
                    else {
                        productAmountTxt.setCursorVisible(false);
                        calculate(position, "min");
                    }


                });

                cardEdit.setOnClickListener(v -> {
                    String idProduct = productList.get(position).getI();

                    List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                    ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetails);
                    CollectionUtils.filter(result, r -> r.PRD_UID.equals(idProduct));

                    if (result.size() > 0 && result.get(0).getQuantity() > 0) {
                        edtDescription.setText(result.get(0).INV_DET_DESCRIBTION);
                        descriptionList.clear();
                        GuidInv = result.get(0).INV_DET_UID;
                        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات", true);
                        myViewModel.getDescription(userName, passWord, idProduct);
                        return;
                    }
                    showAlertDialog("برای نوشتن توضیحات برای کالا مقدار ثبت کنید.");
                });


                myViewModel.getResultMaxSale().observe(getViewLifecycleOwner(), result -> {
                    if (result == null)
                        return;
                    myViewModel.getResultMaxSale().setValue(null);


                    productAmountTxt.setText("");
                    ivMinus.setVisibility(View.GONE);
                    productAmountTxt.setVisibility(View.GONE);


                    double remain = result.getRe();
                    String type = result.getOperate();


                    progressBarCheckRemainProduct.setVisibility(View.GONE);
                    ArrayList<ir.kitgroup.salein.models.Product> resPr = new ArrayList<>(productList);
                    CollectionUtils.filter(resPr, r -> r.getI().equals(result.getI()));

                    if (remain <= 0 && resPr.size() >= 0) {
                        productList.get(productList.indexOf(resPr.get(0))).setAmount(0.0);
                        ivMax.setVisibility(View.GONE);
                        txtError.setText("ناموجود");
                    }
                    else
                        ivMax.setVisibility(View.VISIBLE);


                    if (resPr.size() > 0)
                        calculate(productList.indexOf(resPr.get(0)), type);
                });
            }

        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void resetData() {
        productLevel1List.clear();
        productLevel2List.clear();
        customTabList.clear();
        productList.clear();

        productLevel1Adapter.notifyDataSetChanged();
        productLevel2Adapter.notifyDataSetChanged();
        customTabAdapter.notifyDataSetChanged();
        productAdapter.notifyDataSetChanged();
    }

    private void calculate(int position, String type) {


        List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list();
        ArrayList<InvoiceDetail> result = new ArrayList<>(invDetails);
        CollectionUtils.filter(result, r -> r.PRD_UID.equals(productList.get(position).getI()));


        double amount = result.size() > 0 && result.get(0).getQuantity() != null ? result.get(0).getQuantity() : 0;
        double mainCoef1 = productList.get(position).getCoef1();

        double coef1 = productList.get(position).getCoef1();
        double coef2 = productList.get(position).getCoef2();

        if (defaultCoff.equals("1")) {
            coef2 = productList.get(position).getCoef1();
        } else if (defaultCoff.equals("2")) {
            coef1 = productList.get(position).getCoef2();
        }


        if (type.equals("max")) {
            if (amount > 0.0 && coef2 != 0.0)
                coef1 = coef2;
            amount = amount + coef1;
        } else if (type.equals("min")) {
            if (amount > coef1 && coef2 != 0.0)
                amount = amount - coef2;
            else if (amount >= coef1)
                amount = amount - coef1;
            else
                return;
        } else if (type.equals("")) {
            try {
                amount = Float.parseFloat(sWord);
                if (
                        (coef2 != 0.0 && amount < coef1)
                                || (coef2 == 0 && amount % coef1 != 0)
                                || (coef2 == 0 && amount % coef1 == 0 && amount < coef1)
                ) {


                    showAlertDialog(" مقدار وارد شده باید ضریبی از " + mainCoef1 + " باشد.");
                    amount = 0;
                   /* productAmountTxt.setText("");


                    ivMinus.setVisibility(View.GONE);
                    productAmountTxt.setVisibility(View.GONE);*/
                }
            } catch (Exception ignored) {
            }
        }


        if (checkRemainProduct.equals("1")) {
            double remain = productList.get(position).getRe();
            if (remain - amount < 0) {
                showAlertDialog("مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + format.format(remain));

                if (remain % mainCoef1 != 0)
                    remain = 0;

              /*  productAmountTxt.setText(df.format(remain));
                productAmountTxt.setCursorVisible(false);*/

                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    if (invoiceDetail != null) {
                        invoiceDetail.INV_DET_QUANTITY = remain;
                        if (remain != 0.0)
                            invoiceDetail.update();
                        else
                            invoiceDetail.delete();

                        setCounterForAmountOfOrder();
                    }
                }

                productAdapter.notifyItemChanged(position);
                progressBarCheckRemainProduct.setVisibility(View.GONE);

                return;
            }
        }


        //edit
        if (result.size() > 0) {
            InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
            if (amount == 0) {

                if (invoiceDetail != null)
                    invoiceDetail.delete();

                productAdapter.notifyItemChanged(position);
              /*  if (!type.equals("")) {
                    productAmountTxt.setText("");
                    ivMinus.setVisibility(View.GONE);
                    productAmountTxt.setVisibility(View.GONE);
                }
                productAmountTxt.setCursorVisible(false);*/
                setCounterForAmountOfOrder();
                return;
            }
            if (invoiceDetail != null) {
                invoiceDetail.INV_DET_QUANTITY = amount;
                invoiceDetail.update();
            }
        }

        //Create
        else {
            if (amount != 0.0) {
                InvoiceDetail invoicedetail = new InvoiceDetail();
                invoicedetail.INV_DET_UID = UUID.randomUUID().toString();
                invoicedetail.INV_UID = Inv_GUID;
                invoicedetail.INV_DET_QUANTITY = amount;
                invoicedetail.PRD_UID = productList.get(position).getI();
                invoicedetail.save();
            }
        }


       /* if (!type.equals(""))
            productAmountTxt.setText(df.format(amount));*/


        setCounterForAmountOfOrder();
        productAdapter.notifyItemChanged(position);
//        if (amount > 0) {
//            ivMinus.setVisibility(View.VISIBLE);
//            productAmountTxt.setVisibility(View.VISIBLE);
//        }
    }

    private void showAlertDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage(message).setPositiveButton("بستن", (dialog, which) -> {
            dialog.dismiss();
        }).show();
        TextView textView = alertDialog.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
        textView.setTypeface(face);
        textView.setTextColor(getActivity().getResources().getColor(R.color.red_table));
        textView.setTextSize(13);
    }

    //endregion Custom Method
}