package ir.kitgroup.saleindemo.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.lifecycle.ViewModelProvider;
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
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleindemo.Activities.LauncherActivity;
import ir.kitgroup.saleindemo.Adapters.DescriptionAdapter;
import ir.kitgroup.saleindemo.Adapters.ProductAdapter1;
import ir.kitgroup.saleindemo.Connect.API;
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.CustomProgress;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.databinding.FragmentSearchProductBinding;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.models.Config;
import ir.kitgroup.saleindemo.models.Description;
import ir.kitgroup.saleindemo.models.Product;

@AndroidEntryPoint
public class SearchProductFragment extends Fragment {

    @Inject
    Config config;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    API api;

    private String sWord = "";
    private MyViewModel myViewModel;
    private FragmentSearchProductBinding binding;
    private Company company;
    private String Transport_GUID = "";
    private ProductAdapter1 productAdapter;
    private String Inv_GUID = "";
    private ArrayList<Product> productList;
    private String maxSales = "0";
    private CustomProgress customProgress;

    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private final ArrayList<Description> descriptionList = new ArrayList<>();
    private DescriptionAdapter descriptionAdapter;
    private String GuidInv;
    //endregion Variable Dialog Description


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentSearchProductBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customProgress = CustomProgress.getInstance();

        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");
        Bundle bundle = getArguments();
        Boolean seen = bundle.getBoolean("Seen");
        Inv_GUID = bundle.getString("Inv_GUID");
        String tbl_GUID = bundle.getString("Tbl_GUID");
        maxSales = sharedPreferences.getString("maxSale", "0");
        productList = new ArrayList<>();

        company = Select.from(Company.class).first();

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

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productList.clear();
                productAdapter.notifyDataSetChanged();

                if (s.toString().length() >= 2 || s.toString().isEmpty() || s.toString().trim().equals("")) {
                    sWord=s.toString();
                    binding.pro.setVisibility(View.VISIBLE);
                    myViewModel.getSearchProduct(company.USER, company.PASS, Util.toEnglishNumber(s.toString()));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.edtSearchProduct.addTextChangedListener(textWatcherProduct);
        productAdapter = new ProductAdapter1(getActivity(), productList, company, sharedPreferences, config);

        productAdapter.setTbl_GUID(tbl_GUID);
        productAdapter.setInv_GUID(Inv_GUID);
        productAdapter.setType(seen);
        productAdapter.setApi(api);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.recyclerView.setAdapter(productAdapter);

        productAdapter.setOnClickListener((Prd_UID) -> {
            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            CollectionUtils.filter(invDetails, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
            int counter = 0;
            if (invDetails.size() > 0) {
                counter = invDetails.size();
            }
            if (counter == 0)
                ((LauncherActivity) getActivity()).setClearCounterOrder();
            else
                ((LauncherActivity) getActivity()).setCounterOrder(counter);


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
                    customProgress.showProgress(getActivity(), "در حال دریافت اطلاعات از سرور", false);
                    myViewModel.getDescription(company.USER, company.PASS, GUID);
                }
            } else {
                Toast.makeText(getActivity(), "برای نوشتن توضیحات برای کالا مقدار ثبت کنید.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getResultSearchProduct().observe(getViewLifecycleOwner(), result -> {

            productList.clear();
            productAdapter.notifyDataSetChanged();
            if (result == null) {
                binding.pro.setVisibility(View.GONE);
                return;

            }


            if (sWord.length()<2 && result != null && result.size() > 0) {
                CollectionUtils.filter(result, i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts() && !i.getI().equals(Transport_GUID));

                productList.addAll(result);
            }
            if (productList.size() > 0)
                binding.txtError.setText("");
            else
                binding.txtError.setText("کالایی یافت نشد");
            productAdapter.setMaxSale(maxSales);
            productAdapter.notifyDataSetChanged();
            binding.pro.setVisibility(View.GONE);

        });


        myViewModel.getResultDescription().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                binding.pro.setVisibility(View.GONE);
                customProgress.hideProgress();
                return;

            }
            myViewModel.getResultDescription().setValue(null);
            descriptionList.clear();
            descriptionList.addAll(result);
            for (int i = 0; i < descriptionList.size(); i++) {
                if (edtDescriptionItem.getText().toString().contains("'" + descriptionList.get(i).DSC + "'")) {
                    descriptionList.get(i).Click = true;
                }
            }
            descriptionAdapter.notifyDataSetChanged();
            customProgress.hideProgress();
            dialogDescription.show();
            binding.pro.setVisibility(View.GONE);
        });


        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            productList.clear();
            productAdapter.notifyDataSetChanged();
            binding.pro.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;
            myViewModel.getResultMessage().setValue(null);
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
