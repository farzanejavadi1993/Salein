package ir.kitgroup.salein.ui.launcher.searchItem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.salein.Activities.LauncherActivity;
import ir.kitgroup.salein.ui.launcher.homeItem.DescriptionAdapter;
import ir.kitgroup.salein.ui.launcher.homeItem.ProductAdapter;
import ir.kitgroup.salein.Connect.CompanyAPI;
import ir.kitgroup.salein.Connect.CompanyViewModel;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.classes.Util;

import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.databinding.FragmentSearchProductBinding;

import ir.kitgroup.salein.models.Description;
import ir.kitgroup.salein.models.Product;


@AndroidEntryPoint
public class SearchProductFragment extends Fragment {


    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    CompanyAPI api;

    private String sWord = "";
    private CompanyViewModel myViewModel;
    private FragmentSearchProductBinding binding;
    private Company company;
    private String userName;
    private String passWord;

    private String Transport_GUID = "";//It Is GUID Of Transport Or GUID Of Row Transport In Order Form That Get From Server And Save In Local With SharedPreferences
    private String Inv_GUID = "";//It Is GUID Of Order Form
    private ArrayList<String> closeDayList;//This Variable Is For Get holidays From Server
    private ProductAdapter productAdapter;

    private ArrayList<Product> productList;
    private String maxSales = "0";//It Is For Check Inventory
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


        //region Config
        productList = new ArrayList<>();
        closeDayList = new ArrayList<>();

        customProgress = CustomProgress.getInstance();
        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");
        Inv_GUID = sharedPreferences.getString("Inv_GUID", "");
        maxSales = sharedPreferences.getString("maxSale", "0");
        String CloseDay = sharedPreferences.getString("close_day", "");
        closeDayList.clear();
        if (!CloseDay.equals("")) {
            closeDayList = new ArrayList<>(Arrays.asList(CloseDay.split(",")));
        }
        company = Select.from(Company.class).first();

        userName = company.getUser();
        passWord = company.getPass();
        //endregion Config


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

        //region config SearchView
        try {
            LinearLayout linearLayout1 = (LinearLayout) binding.searchView.getChildAt(0);
            LinearLayout linearLayout2;
            try {
                linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
            } catch (Exception ignore) {
                linearLayout2 = (LinearLayout) linearLayout1.getChildAt(0);
            }

            LinearLayout linearLayout3;
            try {
                linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
            } catch (Exception ignore) {
                linearLayout3 = (LinearLayout) linearLayout2.getChildAt(0);
            }


            AutoCompleteTextView autoComplete;
            try {
                autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
            } catch (Exception ignore) {
                autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(1);
            }
            autoComplete.setTextSize(12);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                autoComplete.setTextColor(getActivity().getColor(R.color.medium_color));
            }
            Typeface iranSansBold = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
            autoComplete.setTypeface(iranSansBold);
        } catch (Exception ignore) {
        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    binding.searchView.clearFocus();
                } catch (Exception ignored) {
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                try {
                    productList.clear();
                    productAdapter.notifyDataSetChanged();

                    if (newText.length() != 1 || newText.trim().equals("")) {
                        sWord = newText;
                        binding.pro.setVisibility(View.VISIBLE);
                        myViewModel.getSearchProduct(userName, passWord, Util.toEnglishNumber(newText));
                    }
                } catch (Exception ignored) {}


                return false;
            }
        });

        //endregion config SearchView


        //region Config RecyclerView And Its Adapter
        productAdapter = new ProductAdapter(getActivity(), productList, sharedPreferences, closeDayList, api,2);
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
        productAdapter.setOnClickImageListener(new ProductAdapter.ClickImage() {
            @Override
            public void onClick(String Prd_UID) {
                NavDirections action = SearchProductFragmentDirections.actionGoToShowDetailFragment(Prd_UID);
                Navigation.findNavController(binding.getRoot()).navigate(action);

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
                    customProgress.showProgress(getActivity(), "در حال دریافت اطلاعات از سرور", false);
                    myViewModel.getDescription(userName, passWord, GUID);
                }
            } else {
                Toast.makeText(getActivity(), "برای نوشتن توضیحات برای کالا مقدار ثبت کنید.", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion Config RecyclerView And Its Adapter

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        myViewModel.getResultSearchProduct().observe(getViewLifecycleOwner(), result -> {

            productList.clear();
            productAdapter.notifyDataSetChanged();
            if (result == null) {
                binding.pro.setVisibility(View.GONE);
                return;

            }


            if (sWord.length() >= 2 && result != null && result.size() > 0) {
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

            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
