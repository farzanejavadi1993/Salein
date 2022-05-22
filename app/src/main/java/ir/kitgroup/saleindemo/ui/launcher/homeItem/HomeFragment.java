package ir.kitgroup.saleindemo.ui.launcher.homeItem;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import ir.kitgroup.saleindemo.databinding.FragmentMobileOrderMainBinding;
import ir.kitgroup.saleindemo.models.CustomTab;
import ir.kitgroup.saleindemo.ui.companies.AllCompanyFragment;
import ir.kitgroup.saleindemo.Connect.API;
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.classes.CustomProgress;
import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;
import ir.kitgroup.saleindemo.classes.PaginationScrollListener;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.DataBase.Company;

import ir.kitgroup.saleindemo.models.Setting;
import ir.kitgroup.saleindemo.models.Description;
import ir.kitgroup.saleindemo.models.Product;
import ir.kitgroup.saleindemo.models.ProductLevel1;
import ir.kitgroup.saleindemo.R;

import ir.kitgroup.saleindemo.models.ProductLevel2;

import static java.lang.Math.min;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    //region Parameter

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    API api;


    private MyViewModel myViewModel;
    private FragmentMobileOrderMainBinding binding;
    private Boolean disableAccount = false;
    private Boolean filterError = false;
    private Company company;
    private CustomProgress customProgress;
    private String Inv_GUID = "";
    private Boolean Seen = false;
    private Boolean EDIT = false;



    private ArrayList<ProductLevel1> productLevel1List;
    private ProductLevel1Adapter productLevel1Adapter;


    private ArrayList<ProductLevel2> productLevel2List;
    private ProductLevel2Adapter productLevel2Adapter;


    private ArrayList<CustomTab> customTabList;
    private CustomTabAdapter customTabAdapter;


    private ArrayList<Product> productList;
    private ArrayList<String> closeDayList;
    private ArrayList<Product> productListData;
    private ProductAdapter productAdapter;
    private boolean isLastPage;
    private boolean isLoading;
    private int currentPage = 1;
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



    //region Variable DialogUpdate
    private Dialog dialogUpdate;
    private TextView textUpdate;
    private MaterialButton btnNo;
    //endregion Variable DialogUpdate



    private String maxSales = "0";

    private String Transport_GUID = "";
    private String linkUpdate = "";

    public int counter1 = 0;
    private String GuidProductLvl2 = "";
    private int keyCustomTab =0;

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
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);

            if (Util.getPackageName(getActivity()).equals("ir.kitgroup.saleinmeat")) {
                Glide.with(this).asGif().load(Uri.parse("file:///android_asset/donyavi.gif")).into(binding.animationView);
                binding.mainBackground.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            }

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            customProgress = CustomProgress.getInstance();


            sharedPreferences.edit().putString("FNM", "main").apply();
            sharedPreferences.edit().putBoolean("vip", false).apply();
            sharedPreferences.edit().putBoolean("discount", false).apply();

            company = Select.from(Company.class).first();


            //region First Value Parameter


            counter1 = 0;
            descriptionList = new ArrayList<>();
            productLevel1List = new ArrayList<>();
            productLevel2List = new ArrayList<>();
            customTabList = new ArrayList<>();

            closeDayList = new ArrayList<>();
            productList = new ArrayList<>();
            productListData = new ArrayList<>();


            if (productAdapter != null) {
                productAdapter.notifyDataSetChanged();
                productLevel1Adapter.notifyDataSetChanged();
                productLevel2Adapter.notifyDataSetChanged();
                customTabAdapter.notifyDataSetChanged();
            }
            //endregion First Value Parameter


           //region Get Bundle
            Bundle bundle = getArguments();

         /*   Inv_GUID = bundle.getString("Inv_GUID");
            EDIT = bundle.getBoolean("EDIT");//when order need EDIT
            Seen = bundle.getBoolean("Seen");*/
            Transport_GUID = sharedPreferences.getString("Transport_GUID", "");




            //region Create Order
            if (Inv_GUID.equals("")) {
                String name;
                name = company.INSK_ID.split("ir.kitgroup.")[1];

                if (name.equals("salein"))
                    Inv_GUID = sharedPreferences.getString(name, "");

                if (Inv_GUID.equals("")) {
                    Inv_GUID = UUID.randomUUID().toString();
                    sharedPreferences.edit().putString(name, Inv_GUID).apply();

                }
                sharedPreferences.edit().putString("Inv_GUID", Inv_GUID).apply();

            } else {
                if (company.mode == 1)
                    sharedPreferences.edit().putString("Inv_GUID", Inv_GUID).apply();
            }


            List<InvoiceDetail> invDetailses = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetailses.size() > 0) {
                CollectionUtils.filter(invDetailses, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
                counter1 = invDetailses.size();
            }
           // if (counter1 == 0)
              //  ((LauncherActivity) getActivity()).setClearCounterOrder();
          //  else
             //   ((LauncherActivity) getActivity()).setCounterOrder(counter1);
          //  ((LauncherActivity) getActivity()).setInVisibiltyItem(!EDIT);
           // ((LauncherActivity) getActivity()).setMainOrderFragment(this);
          //  ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
            //endregion Create Order

            //endregion Get Bundle*/

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
            binding.tvNameStore.setText(company.N);
            //endregion Set Icon And Title

            Users acc = Select.from(Users.class).first();



            //region Action BtnStore
            binding.btnStore.setOnClickListener(v -> {
                if (!company.PI.equals("")) {
                    Bundle bundleCompany = new Bundle();
                    bundleCompany.putString("ParentId", company.PI);
                    AllCompanyFragment companyFragment = new AllCompanyFragment();
                    companyFragment.setArguments(bundleCompany);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, companyFragment, "companyFragment").addToBackStack("companyF").commit();
                }
            });
            //endregion Action BtnStore





            //region Configuration Client Application

                binding.layoutAddressBranch.setVisibility(View.VISIBLE);
                binding.layoutAccount.setVisibility(View.GONE);


            //endregion Configuration Client Application




            //region Cast Variable Dialog Sync
            dialogSync = new Dialog(getActivity());
            dialogSync.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogSync.setContentView(R.layout.custom_dialog);
            dialogSync.setCancelable(false);

            textMessageDialog = dialogSync.findViewById(R.id.tv_message);

            btnOkDialog = dialogSync.findViewById(R.id.btn_ok);
            btnNoDialog = dialogSync.findViewById(R.id.btn_cancel);
            btnNoDialog.setOnClickListener(v -> {
                dialogSync.dismiss();
                if (disableAccount) {
                    getActivity().finish();
                    return;
                } else if (filterError)
                    return;


                if (company.mode == 2 && !Util.getPackageName(getActivity()).equals("ir.kitgroup.salein"))
                    getActivity().finish();
                else
                    getActivity().getSupportFragmentManager().popBackStack();
            });


            btnOkDialog.setOnClickListener(v -> {
                dialogSync.dismiss();
                binding.animationView.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.VISIBLE);
                productList.clear();
                productListData.clear();
                productLevel1List.clear();
                productLevel2List.clear();
                productAdapter.notifyDataSetChanged();
                myViewModel.getProductLevel1(company.USER, company.PASS);
                if (company.mode == 2)
                    myViewModel.getInquiryAccount(company.USER, company.PASS, acc.getM());
                myViewModel.getUnit(company.USER, company.PASS);
                myViewModel.getSetting(company.USER, company.PASS);
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
                binding.animationView.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
                isLastPage = false;
                isLoading = false;
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
                myViewModel.getProductLevel2(company.USER, company.PASS, GUID);
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
                binding.animationView.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.VISIBLE);
                productList.clear();

                productAdapter.notifyDataSetChanged();
                binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
                isLastPage = false;
                isLoading = false;
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
                GuidProductLvl2 = GUID;
                myViewModel.getSettingPrice(company.USER, company.PASS);
                //endregion Full ProductList Because This Item ProductLevel1 Is True
            });

            //endregion Click Item ProductLevel2

            //endregion CONFIGURATION DATA PRODUCT_LEVEL_2



            //region CONFIGURATION DATA CUSTOM_TAB
            customTabAdapter = new CustomTabAdapter(getActivity(), customTabList);

            LinearLayoutManager managerCustom = new LinearLayoutManager(getContext());
            managerCustom.setOrientation(LinearLayoutManager.HORIZONTAL);
            managerCustom.setReverseLayout(true);
            binding.orderRecyclerViewCustomTab.setLayoutManager(managerCustom);
            binding.orderRecyclerViewCustomTab.setScrollingTouchSlop(View.FOCUS_LEFT);
            binding.orderRecyclerViewCustomTab.setAdapter(customTabAdapter);


            //region Click Item CustomTab
            customTabAdapter.SetOnItemClickListener(key -> {
                binding.animationView.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.VISIBLE);
                productList.clear();
                productAdapter.notifyDataSetChanged();
                binding.orderRecyclerViewProduct.post(() -> binding.orderRecyclerViewProduct.scrollToPosition(0));
                isLastPage = false;
                isLoading = false;
                currentPage = 1;
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
                CollectionUtils.filter(resCustomTab, r -> r.getT()==key);

                if (customTabList.size() > 0) {
                    customTabList.get(customTabList.indexOf(resCustomTab.get(0))).Click = true;
                }
                //endregion Click New Item

                customTabAdapter.notifyDataSetChanged();


                //region Full ProductList Because This Item ProductLevel1 Is True
                keyCustomTab=key;
              myViewModel.getSettingPrice(company.USER,company.PASS);
                //endregion Full ProductList Because This Item ProductLevel1 Is True
            });

            //endregion Click Item CustomTab

            //endregion CONFIGURATION DATA CUSTOM_TAB


            //region CONFIGURATION DATA PRODUCT
            productAdapter = new ProductAdapter(getActivity(), productList,company,sharedPreferences);
            productAdapter.setInv_GUID(Inv_GUID);
            productAdapter.setType(Seen);
           productAdapter.setApi(api);
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
            productAdapter.setOnClickListener((Prd_UID) -> {
                List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

                if (invDetails.size() > 0) {
                    int counter = invDetails.size();
                    counter1 = counter;
                   // ((LauncherActivity) getActivity()).setCounterOrder(counter);
                } else {
                  //  ((LauncherActivity) getActivity()).setClearCounterOrder();
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
                        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات", true);
                        myViewModel.getDescription(company.USER, company.PASS, GUID);

                    }
                } else {
                    Toast.makeText(getActivity(), "برای نوشتن توضیحات برای کالا مقدار ثبت کنید.", Toast.LENGTH_SHORT).show();
                }
            });


            //endregion CONFIGURATION DATA PRODUCT


            binding.ivFilter.setOnClickListener(v -> {
                FilterFragment filterFragment = new FilterFragment();
                if (binding.orderRecyclerViewProductLevel2.getVisibility() == View.GONE) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putBoolean("filter", true);
                    filterFragment.setArguments(bundle1);
                }
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, filterFragment, "FilterFragment").addToBackStack("FilterF").commit();
            });


        } catch (Exception e) {
            Toast.makeText(getActivity(), e + "", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);


        binding.progressbar.setVisibility(View.VISIBLE);
        sharedPreferences.edit().putBoolean("discount", false).apply();
        sharedPreferences.edit().putBoolean("vip", false).apply();
        productList.clear();
        productListData.clear();
        productLevel1List.clear();
        productLevel2List.clear();
        if (productAdapter != null && productLevel1Adapter != null && productLevel2Adapter != null) {
            productAdapter.notifyDataSetChanged();
            productLevel1Adapter.notifyDataSetChanged();
            productLevel2Adapter.notifyDataSetChanged();
        }

        binding.orderTxtError.setText("");
        binding.ivFilter.setImageResource(R.drawable.ic_filter);
        binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
        binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);

        myViewModel.getSetting(company.USER, company.PASS);
        myViewModel.getUnit(company.USER, company.PASS);
        if (company.mode == 2)
            myViewModel.getInquiryAccount(company.USER, company.PASS, Select.from(Users.class).first().getM());



        myViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            myViewModel.getResultSetting().setValue(null);
            List<Setting> settingsList = new ArrayList<>(result);

            if (settingsList.size() > 0) {



                //region menu
                sharedPreferences.edit().putString("menu", settingsList.get(0).MENU != null && !settingsList.get(0).MENU .equals("")? settingsList.get(0).MENU : "0").apply();
                String menu = sharedPreferences.getString("menu", "0");
                if (!menu.equals("0") && company.mode==2) {

                    myViewModel.getCustomTab(company.USER, company.PASS);
                    binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
                    binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
                    binding.orderRecyclerViewCustomTab.setVisibility(View.VISIBLE);


                }
                else
                {
                    myViewModel.getProductLevel1(company.USER, company.PASS);
                    binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
                    binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);
                    binding.orderRecyclerViewCustomTab.setVisibility(View.GONE);
                }
                //endregion menu

                //region  closeDayList
                String CloseDay = settingsList.get(0).CLOSE_DAY;
                sharedPreferences.edit().putString("close_day", CloseDay).apply();
                closeDayList.clear();
                if (!CloseDay.equals("")) {
                    closeDayList = new ArrayList<>(Arrays.asList(CloseDay.split(",")));
                }
                productAdapter.setCloseListDate(closeDayList);
                //endregion  closeDayList




                //region updateApp
                String update = settingsList.get(0).UPDATE_APP;
                try {
                    linkUpdate = settingsList.get(0).LINK_UPDATE;
                    sharedPreferences.edit().putString("update_link", settingsList.get(0).LINK_UPDATE).apply();
                } catch (Exception ignored) {}

                String NewVersion = settingsList.get(0).VERSION_APP;
                String AppVersion = "";
                try {
                    AppVersion = appVersion();
                } catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }

                if (company.mode == 2 && update.equals("3") && !AppVersion.equals(NewVersion)) {
                    textUpdate.setText("آپدیت جدید از برنامه موجود است.برای ادامه دادن  برنامه را از بازار آپدیت کنید.");
                    btnNo.setVisibility(View.GONE);
                    dialogUpdate.setCancelable(false);
                    dialogUpdate.show();
                    ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);
                    InvoiceDetail.deleteAll(InvoiceDetail.class);
                } else if (company.mode == 2 && update.equals("2") && !AppVersion.equals(NewVersion)) {
                    textUpdate.setText("آپدیت جدید از برنامه موجود است.برای بهبود عملکرد  برنامه را  از بازار آپدیت کنید.");
                    btnNo.setVisibility(View.VISIBLE);
                    dialogUpdate.setCancelable(true);
                    ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);
                    InvoiceDetail.deleteAll(InvoiceDetail.class);
                    dialogUpdate.show();
                }
                //endregion updateApp


                //region Default Account
                sharedPreferences.edit().putString("Default_ACCOUNT", settingsList.get(0).DEFAULT_CUSTOMER).apply();

                //endregion Default Account


                if (settingsList.get(0).LINK_PAYMENT != null)
                    sharedPreferences.edit().putString("payment_link", settingsList.get(0).LINK_PAYMENT).apply();
                sharedPreferences.edit().putString("coff", settingsList.get(0).COEF != null ? settingsList.get(0).COEF : "0").apply();
                sharedPreferences.edit().putString("Transport_GUID", settingsList.get(0).PEYK).apply();

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

            binding.orderRecyclerViewProductLevel1.setVisibility(View.VISIBLE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.VISIBLE);
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
                myViewModel.getProductLevel2(company.USER, company.PASS, productLevel1List.get(0).getI());
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
                myViewModel.getSettingPrice(company.USER, company.PASS);
                //endregion Full ProductList Because First Item ProductLevel2 Is True

            } else {


                binding.orderTxtError.setText("هیچ زیرگروهی برای این گروه کالایی وجود ندارد.");
                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.GONE);
                binding.animationView.setVisibility(View.GONE);
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
                myViewModel.getSettingPrice(company.USER, company.PASS);
                //endregion Full ProductList Because First Item ProductLevel2 Is True

            }
            else {
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
            ArrayList<Product> resultPrd_ = new ArrayList<>(result);
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

        });
        myViewModel.getResultSettingPrice().observe(getViewLifecycleOwner(), result -> {
            customProgress.hideProgress();
            if (result == null)
                return;
            myViewModel.getResultSettingPrice().setValue(null);
            sharedPreferences.edit().putString("priceProduct", result.get(0).DEFAULT_PRICE_INVOICE).apply();
            maxSales = result.get(0).MAX_SALE;
            sharedPreferences.edit().putString("maxSale", maxSales).apply();

            String menu=sharedPreferences.getString("menu","");
            if (!menu.equals("0"))
                myViewModel.getProductCustomTab(company.USER, company.PASS,keyCustomTab);

            else
                myViewModel.getListProduct(company.USER, company.PASS, GuidProductLvl2);


        });
        myViewModel.getResultListProduct().observe(getViewLifecycleOwner(), result -> {

            binding.progressbar.setVisibility(View.GONE);
            binding.animationView.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;

            myViewModel.getResultListProduct().setValue(null);
            binding.orderTxtError.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
            ArrayList<Product> resultPrd_ = new ArrayList<>(result);
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
            filterError = false;
            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts() &&
                    !i.getI().equalsIgnoreCase(Transport_GUID));


            productListData.clear();
            productListData.addAll(result);
            if (result.size() > 0)
                for (int i = 0; i < 18; i++) {
                    if (result.size() > i)
                        productList.add(result.get(i));
                }


            if (productList.size() == 0) {

                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست ، برای مشاهده کامل کالاها فیلترها را حذف کنید.");
            }


           productAdapter.setMaxSale(maxSales);
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
            filterError = false;
            binding.orderRecyclerViewProductLevel1.setVisibility(View.GONE);
            binding.orderRecyclerViewProductLevel2.setVisibility(View.GONE);
            productList.clear();

            CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts() && !i.getI().equalsIgnoreCase(Transport_GUID));


            productListData.clear();
            productListData.addAll(result);
            if (result.size() > 0)
                for (int i = 0; i < 18; i++) {
                    if (result.size() > i)
                        productList.add(result.get(i));
                }


            if (productList.size() == 0) {

                binding.orderTxtError.setVisibility(View.VISIBLE);
                binding.orderTxtError.setText("هیچ کالایی موجود نیست ، برای مشاهده کامل کالاها فیلترها را حذف کنید.");
            }

         productAdapter.setMaxSale(maxSales);
            productAdapter.notifyDataSetChanged();
            binding.progressbar.setVisibility(View.GONE);
            binding.animationView.setVisibility(View.GONE);

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.pro.setVisibility(View.GONE);
            binding.progressbar.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;
            // myViewModel.getResultMessage().setValue(null);
            if (result.getCode() == -4) {

                sharedPreferences.edit().putBoolean("vip", false).apply();
                sharedPreferences.edit().putBoolean("discount", false).apply();
            }

            disableAccount = sharedPreferences.getBoolean("disableAccount", false);
            if (disableAccount) {
                productList.clear();
                productLevel2List.clear();
                productLevel1List.clear();
                productListData.clear();
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
    public void onDestroy() {
        super.onDestroy();

    }


    private void showError(String error, int type) {


        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        if (type == 0)
            btnOkDialog.setVisibility(View.GONE);
        else if (type == 1)
            btnOkDialog.setVisibility(View.VISIBLE);
        else {
            btnOkDialog.setVisibility(View.GONE);
            dialogSync.setCancelable(false);
        }

        dialogSync.show();
        customProgress.hideProgress();

    }

    @SuppressLint("NotifyDataSetChanged")
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


                    items.addAll(productListData.subList(start, end + 1));

                    if (currentPage != 2)
                        productAdapter.removeLoadingView();


                    if (productListData.size() - end < pageSize)
                        items.addAll(productListData.subList(end, productListData.size()));

                    for (int i = 0; i < items.size(); i++) {
                        if (!productList.contains(items.get(i))) {
                            productList.add(items.get(i));
                        }
                    }


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

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }

    public Bundle getBundle(boolean SetARD1) {
        Bundle bundle = new Bundle();
        bundle.putString("Inv_GUID", Inv_GUID);
        bundle.putBoolean("EDIT", EDIT);
        bundle.putBoolean("Seen", Seen);


        return bundle;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshProductList() {
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EDIT = false;
        binding = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(int type) {
        binding.ivFilter.setImageResource(R.drawable.ic_filter);
        binding.animationView.setVisibility(View.VISIBLE);
        binding.progressbar.setVisibility(View.VISIBLE);
        productList.clear();
        productListData.clear();
        productLevel1List.clear();
        productLevel2List.clear();
        productAdapter.notifyDataSetChanged();
        if (type == 1)
            myViewModel.getDiscountProduct(company.USER, company.PASS);
        else if (type == 2)
            myViewModel.getVipProduct(company.USER, company.PASS, Select.from(Users.class).first().I);
        else
            myViewModel.getProductLevel1(company.USER, company.PASS);

    }

    public void setBundle(Bundle bundle) {
        this.Inv_GUID = bundle.getString("Inv_GUID");
        this.EDIT = bundle.getBoolean("EDIT");
        this.Seen = bundle.getBoolean("Seen");
    }

}