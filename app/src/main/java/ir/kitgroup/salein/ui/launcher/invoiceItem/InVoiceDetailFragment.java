package ir.kitgroup.salein.ui.launcher.invoiceItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.salein.Activities.LauncherActivity;
import ir.kitgroup.salein.Connect.CompanyAPI;
import ir.kitgroup.salein.Connect.CompanyViewModel;
import ir.kitgroup.salein.DataBase.Product;
import ir.kitgroup.salein.classes.dialog.DialogInstance;
import ir.kitgroup.salein.ui.launcher.homeItem.DescriptionAdapter;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.classes.Utilities;

import ir.kitgroup.salein.DataBase.InvoiceDetail;


import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.Description;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.databinding.FragmentInvoiceDetailMobileBinding;

@AndroidEntryPoint

public class InVoiceDetailFragment extends Fragment {

    //region Parameter

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    CompanyAPI api;


    private FragmentInvoiceDetailMobileBinding binding;
    private CompanyViewModel myViewModel;
    private String Prd_UID;
    private String sWord;
    private String type = "2";
    private String Inv_GUID = "";

    private double sumPrice;
    private double sumPurePrice;
    private double sumDiscounts;
    private List<InvoiceDetail> invDetails;
    private List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetailAdapter invoiceDetailAdapter;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");

    //region Dialog Sync
    private Dialog dialogRequestAgain;
    //endregion Dialog Sync

    //region Variable Dialog
    private Dialog dialogDelete;
    //endregion Variable Dialog

    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private DescriptionAdapter descriptionAdapter;
    private ArrayList<Description> descriptionList;
    private String GuidInv;
    //endregion Variable Dialog Description

    private int counter = 0;
    private Company company;
    private String userName;
    private String passWord;
    private CustomProgress customProgress;
    private ArrayList<String> closeDayList;
    private String valueOfDay;
    private String checkRemainProduct = "0";
    private String Transport_GUID = "";
    private double sumTransport = 0.0;
    private Integer invoiceStatus;
    private DialogInstance dialogInstance;
    //endregion Parameter


    //region Override Method

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentInvoiceDetailMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        findValueOfDays();
        getBundle();
        getCloseDayList();
        castDialogDeleteInvoice();
        castDialogRequestAgain();
        castDialogDescription();
        setDataInToolbarElement();
        initRecyclerViewInvoiceDetail();

    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);


        if (type.equals("1")) {
            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getInvoice(userName, passWord, Inv_GUID);
        } else {
            invDetails.clear();
            invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            if (invDetails.size() == 0) {
                binding.progressBar.setVisibility(View.GONE);
                showAlert("هیچ سفارشی وجود ندارد.");
            } else {
                counter = 0;
                Product.deleteAll(Product.class);
                for (int i = 0; i < invDetails.size(); i++) {
                    myViewModel.getProduct(userName, passWord, invDetails.get(i).PRD_UID);
                }
            }
        }

        myViewModel.getResultInvoice().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultInvoice().setValue(null);


            if (result.getInvoice().size() == 0) {
                invDetails.clear();
                showAlert("این سفارش در سرور وجود ندارد.");
                binding.progressBar.setVisibility(View.GONE);
                return;
            }


            String name = company.getI();
            String invGuid = sharedPreferences.getString(name, "");


            List<InvoiceDetail> list = Select.from(InvoiceDetail.class).where("INVUID ='" + invGuid + "'").list();
            for (int i = 0; i < list.size(); i++) {
                InvoiceDetail.deleteInTx(list.get(i));
            }
            InvoiceDetail.saveInTx(result.getInvoiceDetail());


            if (result.getInvoice().get(0).INV_DESCRIBTION != null && !result.getInvoice().get(0).INV_DESCRIBTION.equals("")) {
                binding.edtDescription.setHint("توضیحات : " + result.getInvoice().get(0).INV_DESCRIBTION);
                binding.edtDescription.setVisibility(View.VISIBLE);
            } else
                binding.edtDescription.setVisibility(View.GONE);


            invoiceStatus = result.getInvoice().get(0).INV_STEP;
            if (invoiceStatus != null && invoiceStatus == 1)
                binding.layoutEditDelete.setVisibility(View.VISIBLE);
            else
                binding.layoutEditDelete.setVisibility(View.GONE);


            if (result.getInvoice().get(0).INV_DUE_DATE_PERSIAN != null)
                binding.txtDate.setText(result.getInvoice().get(0).INV_DUE_DATE_PERSIAN);


            CollectionUtils.filter(result.getInvoiceDetail(), i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
            invDetails.clear();
            invDetails.addAll(result.getInvoiceDetail());


            if (invDetails.size() == 0) {
                binding.progressBar.setVisibility(View.GONE);
                showAlert("هیچ ردیفی برای سفارش یافت نشد.");
                return;
            }

            if (invDetails.size() > 0) {
                Product.deleteAll(Product.class);
                for (int i = 0; i < invDetails.size(); i++) {
                    myViewModel.getProduct(userName, passWord, invDetails.get(i).PRD_UID);
                }
            }
        });
        myViewModel.getResultProduct().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultProduct().setValue(null);

            counter = counter + 1;

            ArrayList<ir.kitgroup.salein.models.Product> list1 = new ArrayList<>(result);
            if (list1.size() > 0)
                Product.saveInTx(list1.get(0));


            if (counter == invDetails.size()) {

                if (invDetails.size() > 0) {
                    ArrayList<InvoiceDetail> invDtls = new ArrayList<>(invDetails);
                    CollectionUtils.filter(invDtls, i -> i.PRD_UID.equalsIgnoreCase(Transport_GUID));

                    if (invDtls.size() > 0) {
                        if (type.equals("1")) {
                            sumTransport = Double.parseDouble(invDtls.get(0).INV_DET_TOTAL_AMOUNT);
                            binding.tvSumTransport.setText(format.format(sumTransport) + " ریال ");
                            binding.layoutTransport.setVisibility(View.VISIBLE);
                        }
                    }

                    CollectionUtils.filter(invDetails, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
                }

                invoiceDetailList.clear();
                invoiceDetailList.addAll(invDetails);
                invDetails.clear();
                invoiceDetailAdapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
                counter = 0;
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
            customProgress.hideProgress();

            dialogDescription.show();


        });
        myViewModel.getResultLog().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            myViewModel.getResultLog().setValue(null);

            int message = result.getLogs().get(0).getMessage();
            if (message == 4) {
                List<InvoiceDetail> invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                Product.deleteAll(Product.class);
                for (int i = 0; i < invoiceDetail.size(); i++) {
                    InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                }

                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
            customProgress.hideProgress();

        });
        myViewModel.getResultMaxSale().observe(getViewLifecycleOwner(), res -> {

            if (res == null)
                return;

            myViewModel.getResultMaxSale().setValue(null);

            int remain = res;
            ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
            CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_UID));


            if (result.size() > 0) {
                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                double amount = 0.0;
                if (!sWord.equals("")) {
                    amount = Double.parseDouble(sWord);
                    if (remain - amount < 0) {


                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setMessage("مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + remain)
                                .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                                .show();

                        TextView textView = alertDialog.findViewById(android.R.id.message);
                        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                        textView.setTypeface(face);
                        textView.setTextColor(getResources().getColor(R.color.medium_color));
                        textView.setTextSize(13);

                        if (invoiceDetail != null) {
                            invoiceDetail.INV_DET_QUANTITY = 0.0;
                            invoiceDetail.update();

                        }

                        invoiceDetailAdapter.notifyDataSetChanged();

                        return;
                    }
                }

                if (invoiceDetail != null) {
                    invoiceDetail.INV_DET_QUANTITY = amount;
                    ArrayList<InvoiceDetail> invDtls = new ArrayList<>(invoiceDetailList);
                    CollectionUtils.filter(invDtls, i -> i.INV_DET_UID.equals(invoiceDetail.INV_DET_UID));
                    if (invDtls.size() > 0)
                        invoiceDetailList.get(invoiceDetailList.indexOf(invDtls.get(0))).INV_DET_QUANTITY = amount;

                    invoiceDetail.update();


                }


                invoiceDetailAdapter.notifyDataSetChanged();


            }

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            customProgress.hideProgress();
            if (result == null)
                return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (type.equals("1")) {
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" +
                    Inv_GUID + "'").list();

            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }
        }
        binding = null;
    }


    //endregion Override Method


    //region Custom Method

    private void init() {
        Product.deleteAll(Product.class);
        customProgress = CustomProgress.getInstance();
        company = Select.from(Company.class).first();
        userName = company.getUser();
        passWord = company.getPass();


        invDetails = new ArrayList<>();
        invoiceDetailList = new ArrayList<>();
        descriptionList = new ArrayList<>();

        dialogInstance = DialogInstance.getInstance();

        checkRemainProduct = sharedPreferences.getString("maxSale", "0");
        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");

        sumPrice = 0;
        sumPurePrice = 0;
        sumDiscounts = 0;
        counter = 0;

        binding.btnDelete.setOnClickListener(v -> {
            if (invoiceStatus == 1) {
                binding.progressBar.setVisibility(View.VISIBLE);
                myViewModel.getDeleteInvoice(userName, passWord, Inv_GUID);
            }
        });

        binding.btnEdit.setOnClickListener(v -> {
            if (closeDayList.contains(valueOfDay)) {
                showAlert("فروشگاه تعطیل می باشد.");
                return;
            }
            if (invoiceDetailList.size() == 0) {
                showAlert("سفارشی وجود ندارد");
                return;
            }
            NavDirections action = InVoiceDetailFragmentDirections.actionGoToHomeFragment(Inv_GUID);
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });


        binding.btnContinue.setOnClickListener(v -> {
            if (closeDayList.contains(valueOfDay)) {
                showAlert("فروشگاه تعطیل می باشد.");
                return;
            }

            ArrayList<InvoiceDetail> invoiceDetails = new ArrayList<>(Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list());
            CollectionUtils.filter(invoiceDetails, i -> i.INV_DET_QUANTITY == 0.0);


            if (invoiceDetailList.size() == 0) {
                showAlert("سفارشی وجود ندارد");
                return;
            }
            else if (invoiceDetails.size() > 0) {
                showAlert("برای ردیف های سفارش مقدار وارد کنید و یا ردیف با مقدار صفر را حذف کنید .");
                return;
            }
            binding.btnContinue.setEnabled(false);
            NavDirections action = InVoiceDetailFragmentDirections.actionGoToPaymentFragment(false);
            Navigation.findNavController(binding.getRoot()).navigate(action);

        });

        binding.txtDeleteAll.setOnClickListener(v -> {
            if (invoiceDetailList.size() > 0)
                dialogDelete.show();
        });

        binding.ivBack.setOnClickListener(view1 -> Navigation.findNavController(binding.getRoot()).popBackStack());
    }

    private void findValueOfDays() {
        Calendar calendar1 = Calendar.getInstance();
        switch (calendar1.getTime().getDay()) {
            case 0:
                valueOfDay = "1";
                break;
            case 1:
                valueOfDay = "2";
                break;
            case 2:
                valueOfDay = "3";
                break;
            case 3:
                valueOfDay = "4";
                break;
            case 4:
                valueOfDay = "5";
                break;
            case 5:
                valueOfDay = "6";
                break;
            case 6:
                valueOfDay = "0";
                break;
        }
    }

    private void getBundle() {
        try {
            Inv_GUID = InVoiceDetailFragmentArgs.fromBundle(getArguments()).getInvGUID();
            type = InVoiceDetailFragmentArgs.fromBundle(getArguments()).getType();//1 Edit  //2 create

        } catch (Exception ignore) {
        }


        if (Inv_GUID.equals(""))
            Inv_GUID = sharedPreferences.getString("Inv_GUID", "");
    }

    private void getCloseDayList() {
        String CloseDay = sharedPreferences.getString("close_day", "");
        closeDayList = new ArrayList<>(); //This Variable Is For Get holidays From Server
        if (!CloseDay.equals("")) {
            closeDayList = new ArrayList<>(Arrays.asList(CloseDay.split(",")));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void castDialogDeleteInvoice() {
        dialogDelete = dialogInstance.dialog(getActivity(), false, R.layout.custom_dialog);

        TextView textExit = dialogDelete.findViewById(R.id.tv_message);
        textExit.setText("آیا مایل به حذف کامل سبد خرید هستید؟");
        MaterialButton btnOk = dialogDelete.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialogDelete.findViewById(R.id.btn_cancel);

        btnNo.setOnClickListener(v -> dialogDelete.dismiss());
        btnOk.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }

            invoiceDetailList.clear();
            invoiceDetailAdapter.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
            dialogDelete.dismiss();
        });
    }

    private void castDialogRequestAgain() {
        dialogRequestAgain = dialogInstance.dialog(getActivity(), false, R.layout.custom_dialog);

        MaterialButton btnOkDialog = dialogRequestAgain.findViewById(R.id.btn_ok);
        MaterialButton btnNoDialog = dialogRequestAgain.findViewById(R.id.btn_cancel);

        btnNoDialog.setOnClickListener(v -> dialogRequestAgain.dismiss());

        btnOkDialog.setOnClickListener(v -> {
            dialogRequestAgain.dismiss();
            binding.progressBar.setVisibility(View.VISIBLE);

            if (type.equals("1"))
                myViewModel.getInvoice(userName, passWord, Inv_GUID);

            else {
                invDetails.clear();
                invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

                if (invDetails.size() == 0) {
                    binding.progressBar.setVisibility(View.GONE);
                    showAlert("سفارشی وجود ندارد");

                } else {
                    counter = 0;
                    Product.deleteAll(Product.class);

                    for (int i = 0; i < invDetails.size(); i++) {
                        myViewModel.getProduct(userName, passWord, invDetails.get(i).PRD_UID);
                    }
                }
            }

        });

        //endregion Cast Variable Dialog Sync
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
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
        recyclerDescription.setLayoutManager(flexboxLayoutManager);
        descriptionAdapter = new DescriptionAdapter(getActivity(), descriptionList);
        recyclerDescription.setAdapter(descriptionAdapter);

        descriptionAdapter.setOnClickItemListener((desc, click, position) -> {
            if (click) {
                descriptionList.get(position).Click = true;
                edtDescriptionItem.setText(edtDescriptionItem.getText().toString() + "   " + "'" + desc + "'");
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

            invoiceDetailAdapter.notifyDataSetChanged();
            dialogDescription.dismiss();
        });
    }

    private void setDataInToolbarElement() {
        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");

        if (type.equals("1")) {
            binding.txtDeleteAll.setVisibility(View.GONE);
            binding.layoutContinue.setVisibility(View.GONE);
        } else {
            Calendar calendar = Calendar.getInstance();
            Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
            String text2 = (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year;
            binding.txtDate.setText(text2);
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void initRecyclerViewInvoiceDetail() {
        invoiceDetailList.clear();
        invoiceDetailAdapter = new InvoiceDetailAdapter(getActivity(), invoiceDetailList, type, sharedPreferences,api,Inv_GUID);
        binding.recyclerDetailInvoice.setAdapter(invoiceDetailAdapter);
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setHasFixedSize(false);
        binding.recyclerDetailInvoice.setNestedScrollingEnabled(false);

         invoiceDetailAdapter.setOnClickListener(Prd_UID ->
                 setNumberOfOrderRow()
         );

        invoiceDetailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                calculate(0, "");
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                calculate(positionStart, "remove");
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
            customProgress.showProgress(getActivity(), "در حال دریافت تضیحات", true);
            myViewModel.getDescription(userName, passWord, GUIDPrd);
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
    }


    private void showAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                .show();

        TextView textView = alertDialog.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
        textView.setTypeface(face);
        textView.setTextColor(getResources().getColor(R.color.red_table));
        textView.setTextSize(13);
    }


    @SuppressLint("SetTextI18n")
    private void calculate(int positionStart, String type) {
        sumPrice = 0;
        sumPurePrice = 0;
        sumDiscounts = 0;

        if (type.equals("remove")) {
            ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
            CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoiceDetailList.get(positionStart).PRD_UID));

            if (result.size() > 0) {
                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();

                if (invoiceDetail != null)
                    invoiceDetail.delete();
            }
            invoiceDetailList.remove(invoiceDetailList.get(positionStart));
        }


        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
        if (invoiceDetails.size() > 0)
            CollectionUtils.filter(invoiceDetails, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));


        for (int i = 0; i < invoiceDetails.size(); i++) {
            Product product = Select.from(Product.class).where("I ='" + invoiceDetails.get(i).PRD_UID + "'").first();

            if (product != null) {
                double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));
                double discountPrice;
                if (type.equals("1"))
                    discountPrice = sumprice * (invoiceDetails.get(i).INV_DET_PERCENT_DISCOUNT / 100);
                else
                    discountPrice = sumprice * (product.getPercDis() / 100);
                double totalPrice = sumprice - discountPrice;

                sumPrice = sumPrice + sumprice;
                sumPurePrice = sumPurePrice + totalPrice;
                sumDiscounts = sumDiscounts + discountPrice;
            }
        }

        binding.tvSumPurePrice.setText(format.format(sumPurePrice + sumTransport) + " ریال ");
        binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
        binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");
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

        calculate(0, "");
    }
    //endregion Custom Method


}

