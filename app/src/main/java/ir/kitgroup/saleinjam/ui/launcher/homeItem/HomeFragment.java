package ir.kitgroup.saleinjam.ui.launcher.homeItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinjam.Activities.LauncherActivity;
import ir.kitgroup.saleinjam.Connect.CompanyAPI;
import ir.kitgroup.saleinjam.DataBase.Account;
import ir.kitgroup.saleinjam.DataBase.Product;
import ir.kitgroup.saleinjam.classes.dialog.DialogInstance;
import ir.kitgroup.saleinjam.databinding.HomeFragmentBinding;
import ir.kitgroup.saleinjam.models.CustomTab;
import ir.kitgroup.saleinjam.Connect.CompanyViewModel;
import ir.kitgroup.saleinjam.classes.CustomProgress;
import ir.kitgroup.saleinjam.DataBase.InvoiceDetail;
import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.models.Setting;
import ir.kitgroup.saleinjam.models.Description;
import ir.kitgroup.saleinjam.models.ProductLevel1;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.models.ProductLevel2;


@AndroidEntryPoint
public class HomeFragment extends Fragment {
    //region Parameter

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    CompanyAPI api;

    private HomeFragmentBinding binding;
    private CompanyViewModel myViewModel;

    private final ArrayList<ProductLevel1> productLevel1List = new ArrayList<>();
    private ProductLevel1Adapter productLevel1Adapter;

    private final ArrayList<ProductLevel2> productLevel2List = new ArrayList<>();
    private ProductLevel2Adapter productLevel2Adapter;

    private final ArrayList<CustomTab> customTabList = new ArrayList<>();
    private CustomTabAdapter customTabAdapter;

    private final ArrayList<ir.kitgroup.saleinjam.models.Product> productList = new ArrayList<>();
    private ProductAdapter productAdapter;


    private ArrayList<String> closeDayList = new ArrayList<>();


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private ArrayList<Description> descriptionList;
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv;
    //endregion Variable Dialog Description





    private String checkRemainProduct = "0";
    private String Transport_GUID = "";
    private String GuidProductLvl2 = "";
    private Integer keyCustomTab = 0;
    private Boolean disableAccount = false;
    private String Inv_GUID = "";

    private Company company;
    private String userName;
    private String passWord;
    private CustomProgress customProgress;
    private Account account;


    private DialogInstance dialogInstance;

    private boolean vip = false;
    private boolean discount = false;

    //endregion Parameter


    //region Override Method
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
            createOrder();
            setNumberOfOrderRow();
            castDialogDescription();
            initRecyclerViewProductLevel1();
            initRecyclerViewProductLevel2();
            initRecyclerViewCustomProduct();
            initRecyclerViewProduct();

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        if (productList.size() == 0 || vip || discount) {
            clearData();
            getMainRequest();
        }

        myViewModel.getResultMessage().setValue(null);
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            binding.progressbar.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result.getCode() == -4) {
                binding.ivFilter.setImageResource(R.drawable.ic_filter);
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
                binding.txtErrorSync.setText(result.getName());
                binding.cardErrorSync.setVisibility(View.VISIBLE);
                binding.cardRequsetAgain.setVisibility(View.GONE);
            }

            binding.txtErrorSync.setText(result.getName());
            binding.cardErrorSync.setVisibility(View.VISIBLE);

            if (result.getCode() == -1) {
                binding.cardRequsetAgain.setVisibility(View.VISIBLE);
            }


        });
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
                productAdapter.setCloseListDate(closeDayList);
                //endregion  closeDayList

                //region Default Account
                sharedPreferences.edit().putString("Default_ACCOUNT", settingsList.get(0).DEFAULT_CUSTOMER).apply();
                //endregion Default Account

                if (settingsList.get(0).LINK_PAYMENT != null)
                    sharedPreferences.edit().putString("payment_link", settingsList.get(0).LINK_PAYMENT).apply();

                sharedPreferences.edit().putString("coff", settingsList.get(0).COEF != null ? settingsList.get(0).COEF : "0").apply();

                sharedPreferences.edit().putString("Transport_GUID", settingsList.get(0).PEYK).apply();

                //region menu
                sharedPreferences.edit().putString("menu", settingsList.get(0).MENU != null && !settingsList.get(0).MENU.equals("") ? settingsList.get(0).MENU : "0").apply();

                sharedPreferences.edit().putString("priceProduct", result.get(0).DEFAULT_PRICE_INVOICE).apply();
                checkRemainProduct = result.get(0).MAX_SALE;
                sharedPreferences.edit().putString("maxSale", checkRemainProduct).apply();

                getRequestBaseMenu();
                //endregion menu


            }
        });
        myViewModel.getResultDescription().observe(getViewLifecycleOwner(), result -> {
            customProgress.hideProgress();
            if (result == null)
                return;
            myViewModel.getResultDescription().setValue(null);
            descriptionList.clear();
            descriptionList.addAll(result);
            for (int i = 0; i < descriptionList.size(); i++) {
                if (edtDescriptionItem.getText().toString().contains("'" + descriptionList.get(i).DSC + "'")) {
                    descriptionList.get(i).Click = true;
                }
            }

            descriptionAdapter.notifyDataSetChanged();
            dialogDescription.show();
        });
        myViewModel.getResultProductLevel1().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            binding.orderTxtError.setVisibility(View.GONE);
            myViewModel.getResultProductLevel1().setValue(null);
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
            //    binding.animationView.setVisibility(View.GONE);
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
                getSettingPrice();
                //endregion Full ProductList Because First Item ProductLevel2 Is True
            } else {
                binding.orderTxtError.setText("هیچ زیرگروهی برای این گروه کالایی وجود ندارد.");
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.GONE);
              //  binding.animationView.setVisibility(View.GONE);
                productList.clear();
                productAdapter.notifyDataSetChanged();
                productLevel2List.clear();
            }

            productLevel2Adapter.notifyDataSetChanged();
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
               getSettingPrice();
                //endregion Full ProductList Because First Item ProductLevel2 Is True

            } else {
                binding.orderTxtError.setText("هیچ منویی وجود ندارد.");
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.GONE);
              //  binding.animationView.setVisibility(View.GONE);
                productList.clear();
                productAdapter.notifyDataSetChanged();
                customTabList.clear();

            }

            customTabAdapter.notifyDataSetChanged();

        });
        myViewModel.getResultProductCustomTab().observe(getViewLifecycleOwner(), result -> {

            binding.progressbar.setVisibility(View.GONE);
          //  binding.animationView.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;

            myViewModel.getResultListProduct().setValue(null);
            binding.orderTxtError.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
            ArrayList<ir.kitgroup.saleinjam.models.Product> resultPrd_ = new ArrayList<>(result);
            ArrayList<ir.kitgroup.saleinjam.models.Product> listPrd = new ArrayList<>(resultPrd_);
            CollectionUtils.filter(listPrd, l -> l.getKey() != 0);
            if (listPrd.size() > 0)
                for (int i = 0; i < listPrd.size(); i++) {
                    int position = listPrd.get(i).getKey() - 1;//new position
                    int index = resultPrd_.indexOf(listPrd.get(i));//old position
                    if (resultPrd_.size() > position) {
                        ir.kitgroup.saleinjam.models.Product itemProduct = resultPrd_.get(position);
                        if (index != position) {
                            resultPrd_.set(position, resultPrd_.get(resultPrd_.indexOf(listPrd.get(i))));
                            resultPrd_.set(index, itemProduct);

                        }
                    }
                }


            productList.addAll(resultPrd_);
            if (productList.size() == 0) {
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست");
            }

            productAdapter.notifyDataSetChanged();

        });
        myViewModel.getResultListProduct().observe(getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
         //   binding.animationView.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;

            myViewModel.getResultListProduct().setValue(null);
            binding.orderTxtError.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
            ArrayList<ir.kitgroup.saleinjam.models.Product> resultPrd_ = new ArrayList<>(result);
            ArrayList<ir.kitgroup.saleinjam.models.Product> listPrd = new ArrayList<>(resultPrd_);
            CollectionUtils.filter(listPrd, l -> l.getKey() != 0);
            if (listPrd.size() > 0)
                for (int i = 0; i < listPrd.size(); i++) {
                    int position = listPrd.get(i).getKey() - 1;//new position
                    int index = resultPrd_.indexOf(listPrd.get(i));//old position
                    if (resultPrd_.size() > position) {
                        ir.kitgroup.saleinjam.models.Product itemProduct = resultPrd_.get(position);
                        if (index != position) {
                            resultPrd_.set(position, resultPrd_.get(resultPrd_.indexOf(listPrd.get(i))));
                            resultPrd_.set(index, itemProduct);
                        }
                    }
                }

            productList.addAll(resultPrd_);
            if (productList.size() == 0) {
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست");
            }
            productAdapter.notifyDataSetChanged();

        });
        myViewModel.getResultDiscountProduct().observe(getViewLifecycleOwner(), result -> {
            binding.ivFilter.setImageResource(R.drawable.ic_filter_active);
            customProgress.hideProgress();
            binding.progressbar.setVisibility(View.GONE);
           // binding.animationView.setVisibility(View.GONE);
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


            productList.addAll(result);
            if (productList.size() == 0) {

                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست ، برای مشاهده کامل کالاها فیلترها را حذف کنید.");
            }
            productAdapter.notifyDataSetChanged();


        });
        myViewModel.getResultVipProduct().observe(getViewLifecycleOwner(), result -> {
            binding.ivFilter.setImageResource(R.drawable.ic_filter_active);
            customProgress.hideProgress();
            binding.progressbar.setVisibility(View.GONE);
          //  binding.animationView.setVisibility(View.GONE);
            if (result == null)
                return;

            binding.orderTxtError.setVisibility(View.GONE);
            myViewModel.getResultVipProduct().setValue(null);
            sharedPreferences.edit().putBoolean("vip", true).apply();

            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts() && !i.getI().equalsIgnoreCase(Transport_GUID));


            productList.addAll(result);
            if (productList.size() == 0) {
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست ، برای مشاهده کامل کالاها فیلترها را حذف کنید.");
            }
            productAdapter.notifyDataSetChanged();
            binding.progressbar.setVisibility(View.GONE);
          //  binding.animationView.setVisibility(View.GONE);

        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //endregion Override Method


    //region Custom Method


    @SuppressLint("NotifyDataSetChanged")
    public void showData() {
        resetFilter();
        clearData();
        getMainRequest();
    }

    private void init() {
        binding.cardErrorSync.setVisibility(View.GONE);
        sharedPreferences.edit().putBoolean("loginSuccess",true).apply();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Product.deleteAll(Product.class);

        Inv_GUID = "";
        customProgress = CustomProgress.getInstance();

        company = Select.from(Company.class).first();
        userName = company.getUser();
        passWord = company.getPass();

        account = Select.from(Account.class).first();


        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");

        vip = sharedPreferences.getBoolean("vip", false);
        discount = sharedPreferences.getBoolean("discount", false);

        binding.tvNameStore.setText(company.getN());

        descriptionList = new ArrayList<>();


        dialogInstance = DialogInstance.getInstance();

        binding.ivFilter.setOnClickListener(v -> {
            boolean filter = binding.orderRecyclerViewProductLevel2.getVisibility() == View.GONE;
            NavDirections action = HomeFragmentDirections.actionGoToFilterFragment(filter);
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });

        binding.btnCompanyBranches.setOnClickListener(view1 -> {
            if (!company.getPi().equals("")) {
                NavDirections action = HomeFragmentDirections.actionGoToAllCompanyFragment(company.getPi());
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
        });

        if (vip || discount)
            binding.ivFilter.setImageResource(R.drawable.ic_filter_active);
        else
            binding.ivFilter.setImageResource(R.drawable.ic_filter);

        binding.cardRequsetAgain.setOnClickListener(view -> {

            binding.cardErrorSync.setVisibility(View.GONE);
            binding.progressbar.setVisibility(View.VISIBLE);
            resetFilter();
            clearData();
            getMainRequest();
        });

    }

    private void resetFilter() {
        sharedPreferences.edit().putBoolean("vip", false).apply();
        sharedPreferences.edit().putBoolean("discount", false).apply();
        vip=false;
        discount=false;
        binding.ivFilter.setImageResource(R.drawable.ic_filter);
    }

    private void createOrder() {
        try {
            Inv_GUID = HomeFragmentArgs.fromBundle(getArguments()).getInvGUID();
//            getArguments().clear();
        } catch (Exception ignored) {
            Inv_GUID = "";
        }

        if (Inv_GUID.equals("")) {
            ((LauncherActivity) getActivity()).setShowProfileItem(true);
            String name = company.getI();
            Inv_GUID = sharedPreferences.getString(name, "");

            if (Inv_GUID.equals("")) {
                Inv_GUID = UUID.randomUUID().toString();
                sharedPreferences.edit().putString(name, Inv_GUID).apply();
            }
        } else
            ((LauncherActivity) getActivity()).setShowProfileItem(false);

        sharedPreferences.edit().putString("Inv_GUID", Inv_GUID).apply();//Save GUID Order Form To Use In App
    }

    private void setNumberOfOrderRow() {
        int counter = 0;
        Inv_GUID = sharedPreferences.getString("Inv_GUID", "");
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
    private void clearData() {
        binding.orderTxtError.setText("");
        productLevel1List.clear();
        productLevel2List.clear();
        customTabList.clear();
        productList.clear();

        productLevel1Adapter.notifyDataSetChanged();
        productLevel2Adapter.notifyDataSetChanged();
        customTabAdapter.notifyDataSetChanged();
        productAdapter.notifyDataSetChanged();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void castDialogDescription() {
        dialogDescription = dialogInstance.dialog(getActivity(), false, R.layout.dialog_description);

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

            @SuppressLint("NotifyDataSetChanged")
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
    private void initRecyclerViewProductLevel1() {
        if (productLevel1List.size()==1)
            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);

        productLevel1Adapter = new ProductLevel1Adapter(getActivity(), productLevel1List);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);
        binding.orderRecyclerViewProductLevel1.setLayoutManager(manager);
        binding.orderRecyclerViewProductLevel1.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProductLevel1.setAdapter(productLevel1Adapter);

        productLevel1Adapter.SetOnItemClickListener(GUID -> {
            showProgressBar();
            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
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

            myViewModel.getProductLevel2(userName, passWord, GUID);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerViewProductLevel2() {
        productLevel2Adapter = new ProductLevel2Adapter(getActivity(), productLevel2List);
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager2.setReverseLayout(true);
        binding.orderRecyclerViewProductLevel2.setLayoutManager(manager2);
        binding.orderRecyclerViewProductLevel2.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProductLevel2.setAdapter(productLevel2Adapter);

        productLevel2Adapter.SetOnItemClickListener(GUID -> {
            showProgressBar();

            productList.clear();
            productAdapter.notifyDataSetChanged();

            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
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

            GuidProductLvl2 = GUID;
            getSettingPrice();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerViewCustomProduct() {
        customTabAdapter = new CustomTabAdapter(getActivity(), customTabList);
        LinearLayoutManager managerCustom = new LinearLayoutManager(getContext());
        managerCustom.setOrientation(LinearLayoutManager.HORIZONTAL);
        managerCustom.setReverseLayout(true);
        binding.orderRecyclerViewCustomTab.setLayoutManager(managerCustom);
        binding.orderRecyclerViewCustomTab.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewCustomTab.setAdapter(customTabAdapter);

        customTabAdapter.SetOnItemClickListener(key -> {
            showProgressBar();
            productList.clear();
            productAdapter.notifyDataSetChanged();
            binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
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

            keyCustomTab = key;
          getSettingPrice();
        });
    }

    private void initRecyclerViewProduct() {
        productAdapter = new ProductAdapter(getActivity(), productList, sharedPreferences, api);

        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 2) {
            @Override
            protected boolean isLayoutRTL() {
                return true;
            }
        };
        binding.orderRecyclerViewProduct.setLayoutManager(linearLayoutManager);
        binding.orderRecyclerViewProduct.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.orderRecyclerViewProduct.setAdapter(productAdapter);

        productAdapter.setOnClickListener((Prd_UID) -> {
            setNumberOfOrderRow();
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
                    customProgress.showProgress(getActivity(), "در حال دریافت توضیحات", true);
                    myViewModel.getDescription(userName, passWord, GUID);
                }
            } else
                showAlert();


        });


        productAdapter.setOnClickImageListener(Prd_UID -> {
            NavDirections action = HomeFragmentDirections.actionGoToShowDetailFragment(Prd_UID);
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });

    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("برای نوشتن توضیحات برای کالا مقدار ثبت کنید.")
                .setPositiveButton("بستن", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
      setStyleTextAlert(alertDialog);
    }


    private void setStyleTextAlert(AlertDialog alert){
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTypeface(face);
        textView.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
        textView.setTextSize(13);
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.color_accent));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.color_accent));

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(face);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(face);

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(12);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(12);
    }
    private void getMainRequest() {
        showProgressBar();
        myViewModel.getInquiryAccount(userName, passWord, account.getM());
        myViewModel.getUnit(userName, passWord);
        myViewModel.getSetting(userName, passWord);
    }

    private void getRequestBaseMenu() {
        String menu = sharedPreferences.getString("menu", "0");
        if (!menu.equals("0")) {
            myViewModel.getCustomTab(userName, passWord);
            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
            binding.orderRecyclerViewCustomTab.setVisibility(View.VISIBLE);
        } else {
            if (vip)
                myViewModel.getVipProduct(userName,passWord,account.getI());
            else if (discount)
                myViewModel.getDiscountProduct(userName,passWord);
            else
            myViewModel.getProductLevel1(userName, passWord);
            binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);
            binding.orderRecyclerViewCustomTab.setVisibility(View.GONE);
        }
    }


    private void showProgressBar() {
        binding.progressbar.setVisibility(View.VISIBLE);
      //  binding.animationView.setVisibility(View.VISIBLE);
    }

    private void getSettingPrice(){


        String menu = sharedPreferences.getString("menu", "");
        if (!menu.equals("0"))
            myViewModel.getProductCustomTab(userName, passWord, keyCustomTab);

        else
            myViewModel.getListProduct(userName, passWord, GuidProductLvl2);
    }
    //endregion Custom Method
}