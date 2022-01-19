package ir.kitgroup.saleinjam.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinjam.Activities.LauncherActivity;
import ir.kitgroup.saleinjam.Adapters.DescriptionAdapter;
import ir.kitgroup.saleinjam.Adapters.ProductAdapter1;
import ir.kitgroup.saleinjam.Connect.API;
import ir.kitgroup.saleinjam.DataBase.InvoiceDetail;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.classes.ConfigRetrofit;
import ir.kitgroup.saleinjam.classes.CustomProgress;
import ir.kitgroup.saleinjam.classes.ServerConfig;
import ir.kitgroup.saleinjam.classes.Util;
import ir.kitgroup.saleinjam.databinding.FragmentSearchProductBinding;
import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.models.Config;
import ir.kitgroup.saleinjam.models.Description;
import ir.kitgroup.saleinjam.models.ModelDesc;
import ir.kitgroup.saleinjam.models.ModelProduct;
import ir.kitgroup.saleinjam.models.Product;

@AndroidEntryPoint
public class SearchProductFragment extends Fragment {

    @Inject
    Config config;

    @Inject
    SharedPreferences sharedPreferences;
    private FragmentSearchProductBinding binding;
    private API api;
    private Company company;
    private String Transport_GUID = "";
    private ProductAdapter1 productAdapter;
    private String Inv_GUID = "";
    private ArrayList<Product> productList;
    private String maxSales = "0";
    private CustomProgress customProgress;
    private CompositeDisposable compositeDisposable;
    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private final ArrayList<Description> descriptionList = new ArrayList<>();
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv;
    //endregion Variable Dialog Description
    private ServerConfig serverConfig;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentSearchProductBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customProgress = CustomProgress.getInstance();
        compositeDisposable = new CompositeDisposable();
        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");

        Bundle bundle = getArguments();
        Boolean seen = bundle.getBoolean("Seen");
        Inv_GUID = bundle.getString("Inv_GUID");
        String tbl_GUID = bundle.getString("Tbl_GUID");
        maxSales = sharedPreferences.getString("maxSale", "0");
        productList = new ArrayList<>();

        company = Select.from(Company.class).first();
        if (company.mode == 2)
            api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/", false, 30).create(API.class);
        else {
         new AsyncTask() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @SuppressLint("StaticFieldLeak")
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    api = ConfigRetrofit.getRetrofit("http://" + serverConfig.URL1 + "/api/REST/", false, 30).create(API.class);
                    productAdapter.setApi(api);
                    productAdapter.notifyDataSetChanged();
                }
                @SuppressLint("StaticFieldLeak")
                @Override
                protected Object doInBackground(Object[] params) {
                    serverConfig = new ServerConfig(company.IP1, company.IP2);
                    return 0;
                }
            }.execute(0);
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

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.BASELINE);
        recyclerDescription.setLayoutManager(flexboxLayoutManager);
        descriptionAdapter = new DescriptionAdapter(getActivity(), descriptionList);
        recyclerDescription.setAdapter(descriptionAdapter);
        binding.txtError.setText("کالای مورد نظر خود را جستجو کنید");
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
                invDetail.update(); }
            productAdapter.notifyDataSetChanged();
            Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");
            if (frg instanceof MainOrderFragment) {
                MainOrderFragment fgf = (MainOrderFragment) frg;
                fgf.refreshProductList();
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
                } }
                @Override
            public void afterTextChanged(Editable s) {}
        });
        //endregion Cast DialogDescription

        int fontSize;
        if (Util.screenSize >= 7)
            fontSize = 14;
        else
            fontSize = 12;
        binding.edtSearchProduct.setTextSize(fontSize);
        TextWatcher textWatcherProduct = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    productList.clear();
                    productAdapter.notifyDataSetChanged();
                    binding.txtError.setText("کالای مورد نظر خود را جستجو کنید");
                } else {
                    compositeDisposable.clear();
                   if (s.toString().length()>=2)
                    getSearchProduct(Util.toEnglishNumber(s.toString()),s.toString().length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);
        productAdapter = new ProductAdapter1(getActivity(), productList, company, sharedPreferences, Inv_GUID, config);
        productAdapter.setTbl_GUID(tbl_GUID);
        productAdapter.setType(seen);
        productAdapter.setApi(api);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.recyclerView.setAdapter(productAdapter);

        productAdapter.setOnClickListener((Prd_UID) -> {
            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            CollectionUtils.filter(invDetails, i -> !i.PRD_UID.toLowerCase().equals(Transport_GUID.toLowerCase()));
            int counter = 0;
            if (invDetails.size() > 0) {
                counter = invDetails.size();
            }
            if (counter == 0)
                ((LauncherActivity) getActivity()).setClearCounterOrder();
            else
                ((LauncherActivity) getActivity()).setCounterOrder(counter);

            Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");

            if (frg instanceof MainOrderFragment) {
                MainOrderFragment fgf = (MainOrderFragment) frg;
                fgf.refreshProductList();
                fgf.counter1 = counter;
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
    }

    @SuppressLint("SetTextI18n")
    private void getSearchProduct(String s,int length) {
        try {
            compositeDisposable.add(
                    api.getSearchProduct("saleinkit_api", company.USER, company.PASS, s)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        productList.clear();
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
                                        if (s.length()==length) {
                                            productList.clear();
                                            CollectionUtils.filter(iDs.getProductList(), i -> i.getPrice(sharedPreferences) > 0);
                                            if (iDs!=null && iDs.getProductList()!=null &&  iDs.getProductList().size() > 0) {
                                                CollectionUtils.filter(iDs.getProductList(), i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
                                                productList.addAll(iDs.getProductList());
                                            }
                                            if (productList.size() > 0)
                                                binding.txtError.setText("");
                                            else
                                                binding.txtError.setText("کالایی یافت نشد");
                                            productAdapter.setMaxSale(maxSales);
                                            productAdapter.notifyDataSetChanged();
                                        }
                                        }
                                    , throwable -> {}));
        } catch (Exception ignored) {}
    }
    private void getDescription(String id) {
        if (!networkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "خطا در اتصال به اینترنت", Toast.LENGTH_SHORT).show();
            return;
        }
        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات...", false);
        try {
            compositeDisposable.add(
                    api.getDescription1(company.USER, company.PASS, id)
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
                            }));
        } catch (Exception e) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت توضیحات", Toast.LENGTH_SHORT).show(); }
    }
    private Boolean networkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear(); }
}
