package ir.kitgroup.saleinmeat.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import ir.kitgroup.saleinmeat.Adapters.DescriptionAdapter;
import ir.kitgroup.saleinmeat.Adapters.InvoiceDetailMobileAdapter;
import ir.kitgroup.saleinmeat.DataBase.Account;

import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.classes.CustomProgress;
import ir.kitgroup.saleinmeat.classes.Utilities;

import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;

import ir.kitgroup.saleinmeat.DataBase.User;


import ir.kitgroup.saleinmeat.models.Company;
import ir.kitgroup.saleinmeat.models.Description;
import ir.kitgroup.saleinmeat.models.ModelDesc;

import ir.kitgroup.saleinmeat.models.ModelInvoice;
import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.databinding.FragmentInvoiceDetailMobileBinding;
import ir.kitgroup.saleinmeat.models.ModelProduct;
import ir.kitgroup.saleinmeat.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@AndroidEntryPoint

public class InVoiceDetailMobileFragment extends Fragment {

    //region Parameter

    @Inject
   Double ScreenSize;

    @Inject
    Company company;
    private FragmentInvoiceDetailMobileBinding binding;
    private CustomProgress customProgress;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    private String userName;
    private String passWord;
    private String Inv_GUID;
    private String Ord_TYPE;
    private String Tbl_GUID = "";
    private Boolean Seen =false;
    private boolean edit = false;
    private String type;

    private double sumPrice;
    private double sumPurePrice;
    private double sumDiscounts;
    private double sumDiscountsInvoiceRial=0.0;


    private List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetailMobileAdapter invoiceDetailAdapter;


    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private  ImageView ivIconSync;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync




    //region Variable Dialog
    private Dialog dialogDelete;
    private int imageIconDialog = R.drawable.saleinorder_png;
    //endregion Variable Dialog


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private DescriptionAdapter descriptionAdapter;
    private ArrayList<Description> descriptionList;
    private String GuidInv;
    //endregion Variable Dialog Description


    private String status;
    private Boolean invFinal;
    private double sumTransport = 0.0;
    private String maxSales = "0";
    private List<InvoiceDetail> invDetails = new ArrayList<>();


    private String Acc_NAME;
    private String Acc_GUID;
    private int counter = 0;


    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentInvoiceDetailMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        customProgress = CustomProgress.getInstance();


        descriptionList = new ArrayList<>();
        invoiceDetailList = new ArrayList<>();
        maxSales = sharedPreferences.getString("maxSale", "0");


        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;


        //region Configuration Text Size
        int fontSize;
        int fontLargeSize;
        if (ScreenSize  >= 7) {
            fontSize = 13;
            fontLargeSize = 14;
        } else {
            fontSize = 11;
            fontLargeSize = 12;
        }


        binding.tvNameCustomer.setTextSize(fontSize);
        binding.txtDate.setTextSize(fontSize);
        binding.txtTableNumber.setTextSize(fontSize);


        binding.orderListPurePriceTv.setTextSize(fontSize);
        binding.tvSumPurePrice.setTextSize(fontLargeSize);
        binding.orderListSumPriceTv.setTextSize(fontLargeSize);
        binding.tvSumPrice.setTextSize(fontLargeSize);
        binding.orderListSumDiscountTv.setTextSize(fontLargeSize);
        binding.tvSumDiscount.setTextSize(fontLargeSize);
        binding.tvSumTransport.setTextSize(fontLargeSize);
        binding.txtSumTransport.setTextSize(fontLargeSize);


        binding.btnContinue.setTextSize(fontSize);
        binding.btnDelete.setTextSize(fontSize);
        binding.btnEdit.setTextSize(fontSize);
        binding.txtDeleteAll.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Cast Dialog Delete

        try {
            switch (company.nameCompany) {


                case "ir.kitgroup.saleintop":

                    imageIconDialog = R.drawable.top_png;


                    break;


                case "ir.kitgroup.saleinmeat":

                    imageIconDialog = R.drawable.meat_png;


                    break;

                case "ir.kitgroup.saleinnoon":

                    imageIconDialog = R.drawable.noon;


                    break;
            }
        } catch (Exception ignore) {

        }


        dialogDelete = new Dialog(getActivity());
        dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.setContentView(R.layout.custom_dialog);
        dialogDelete.setCancelable(false);

        TextView textExit = dialogDelete.findViewById(R.id.tv_message);
        textExit.setText("آیا مایل به حذف کامل سبد خرید هستید؟");
        ImageView ivIcon = dialogDelete.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imageIconDialog);

        MaterialButton btnOk = dialogDelete.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialogDelete.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> {
            dialogDelete.dismiss();

        });
        btnOk.setOnClickListener(v -> {
            dialogDelete.dismiss();
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }
            invoiceDetailList.clear();
            invoiceDetailAdapter.notifyDataSetChanged();

        });
        //endregion Cast Dialog Delete


        //region Get Bundle
        Bundle bundle = getArguments();
        type = bundle.getString("type");  //1 seen   //2 Edit
        Inv_GUID = bundle.getString("Inv_GUID");
        Tbl_GUID = bundle.getString("Tbl_GUID");
        Ord_TYPE = bundle.getString("Ord_TYPE");
        try {
            Acc_NAME = bundle.getString("Acc_NAME");
            Seen = bundle.getBoolean("Seen");

                binding.tvNameCustomer.setText(Acc_NAME);


        } catch (Exception ignore) {
        }

        edit = bundle.getBoolean("EDIT");
        //endregion Get Bundle


        //region Configuration Client Application
        if (company.mode == 2) {
            binding.tvNameCustomer.setVisibility(View.GONE);
            binding.txtTableNumber.setVisibility(View.GONE);

        }
        //endregion Configuration Client Application


        sumPrice = 0;
        sumPurePrice = 0;
        sumDiscounts = 0;


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
        });


        btnOkDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            if (type.equals("1"))
                getInvoice();
            else {
                invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                if (invDetails.size() == 0)
                    binding.progressBar.setVisibility(View.GONE);

                else {
                    counter = 0;
                    for (int i = 0; i < invDetails.size(); i++) {
                        getProduct(invDetails.get(i).PRD_UID, i);
                    }
                }
            }

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
        //endregion Cast DialogDescription


        //region Set Parameter Toolbar
        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");

        //region Seen Order After Send It
        if (type.equals("1")) {
            binding.txtDeleteAll.setVisibility(View.GONE);
            binding.layoutContinue.setVisibility(View.GONE);

        }
        //endregion Seen Order After Send It


        //region See Order During Work
        else {

            Calendar calendar = Calendar.getInstance();
            Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
            String text2 = (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year;
            binding.txtDate.setText(text2);


            if (company.mode  == 1) {
                Account acc = Select.from(Account.class).first();


                    binding.tvNameCustomer.setText(acc != null ? acc.N :"");
            }


        }
        //endregion See Order During Work


        //endregion Set Parameter Toolbar


        //region CONFIGURATION DATA INVOICE_DETAIL

        invoiceDetailList.clear();


        if (type.equals("1") )
            binding.tvSumPurePrice.setText(format.format(sumPurePrice + sumTransport) + " ریال ");
        else
            binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");

        binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
        binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");


        invoiceDetailAdapter = new InvoiceDetailMobileAdapter(getActivity(), invoiceDetailList, type,Seen);
        binding.recyclerDetailInvoice.setAdapter(invoiceDetailAdapter);
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setHasFixedSize(false);
        binding.recyclerDetailInvoice.setNestedScrollingEnabled(false);


        invoiceDetailAdapter.editAmountItemListener((Prd_GUID, s, Price, discountPercent) -> {

            if (maxSales.equals("1")) {
                getMaxSales(userName, passWord, Prd_GUID, s);
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


                    invoiceDetailAdapter.notifyDataSetChanged();

                }
            }


        });


        invoiceDetailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                sumPrice = 0;
                sumPurePrice = 0;
                sumDiscounts = 0;

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                for (int i = 0; i < invoiceDetails.size(); i++) {
                    if (invoiceDetails.get(i).INV_DET_DESCRIBTION != null && invoiceDetails.get(i).INV_DET_DESCRIBTION.equals("توزیع")) {
                        invoiceDetails.remove(invoiceDetails.get(i));
                    }
                }
                for (int i = 0; i < invoiceDetails.size(); i++) {
                    ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();
                    if (product != null) {
                        double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice());

                        double discountPrice=0.0;
                        if (type.equals("1") || Seen)
                            discountPrice = sumprice * (invoiceDetails.get(i).INV_DET_PERCENT_DISCOUNT/ 100);

                        else
                            discountPrice = sumprice * (product.getPercDis() / 100);
                        double totalPrice = sumprice - discountPrice;

                        sumPrice = sumPrice + (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice());
                        sumPurePrice = sumPurePrice + totalPrice;
                        sumDiscounts = sumDiscounts + discountPrice;

                    }

                }


                binding.tvSumPurePrice.setText(format.format(sumPurePrice + sumTransport) + " ریال ");
                binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
                binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                sumPrice = 0;
                sumPurePrice = 0;
                sumDiscounts = 0;


                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoiceDetailList.get(positionStart).PRD_UID));
                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    if (invoiceDetail != null)
                        invoiceDetail.delete();
                }

                invoiceDetailList.remove(invoiceDetailList.get(positionStart));


                for (int i = 0; i < invoiceDetailList.size(); i++) {


                    ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + invoiceDetailList.get(i).PRD_UID + "'").first();
                    if (product != null) {
                        double sumprice = (invoiceDetailList.get(i).INV_DET_QUANTITY * product.getPrice());
                        double discountPrice = sumprice * (product.getPercDis() / 100);
                        double totalPrice = sumprice - discountPrice;

                        sumPrice = sumPrice + (invoiceDetailList.get(i).INV_DET_QUANTITY * product.getPrice());
                        sumPurePrice = sumPurePrice + totalPrice;
                        sumDiscounts = sumDiscounts + discountPrice;

                    }

                }


                binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");
                binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
                binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");


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


        //endregion CONFIGURATION DATA INVOICE_DETAIL


        //region Action BtnDelete
        binding.btnDelete.setOnClickListener(v -> {

            if (status.equals("-"))
                getDeleteInvoice(userName, passWord, Inv_GUID);
        });

        //endregion Action BtnDelete


        //region Action BtnEdit
        binding.btnEdit.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            bundle.putString("Ord_TYPE", Ord_TYPE);
            Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
            if (tb != null && tb.INVID != null && tb.I.equals(tb.INVID))
                bundle.putString("Tbl_GUID", "");
            else
                bundle.putString("Tbl_GUID", Tbl_GUID);

            bundle.putString("Inv_GUID", Inv_GUID);
            bundle.putBoolean("Seen", true);
            bundle.putString("Acc_NAME", Acc_NAME);
            bundle.putString("Acc_GUID", Acc_GUID);


            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
            mainOrderMobileFragment.setArguments(bundle);
            FragmentTransaction replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileFX");
            replaceFragment.commit();
        });
        //endregion Action BtnEdit


        //region Action BtnContinue
        binding.btnContinue.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("Inv_GUID", Inv_GUID);
            bundle1.putString("Tbl_GUID", Tbl_GUID);
            bundle1.putString("Acc_NAME", Acc_NAME);
            bundle1.putString("Acc_GUID", Acc_GUID);


            if (company.mode == 2)
                bundle1.putString("Ord_TYPE", "");
            else
                bundle1.putString("Ord_TYPE", Ord_TYPE);

            if (edit)
                bundle1.putBoolean("EDIT", true);

            bundle1.putString("Sum_PURE_PRICE", String.valueOf(sumPurePrice));
            bundle1.putString("Sum_PRICE", String.valueOf(sumPrice));
            bundle1.putBoolean("Seen", Seen);
            PaymentMobileFragment paymentFragment = new PaymentMobileFragment();
            paymentFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, paymentFragment, "PaymentFragment").addToBackStack("PaymentF").commit();
        });
        //endregion Action BtnContinue


        binding.txtDeleteAll.setOnClickListener(v -> {
            if (invoiceDetailList.size() > 0)
                dialogDelete.show();
        });


        if (type.equals("1"))
            getInvoice();
        else {
            invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() == 0)
                binding.progressBar.setVisibility(View.GONE);

            else {
                counter = 0;
                for (int i = 0; i < invDetails.size(); i++) {
                    getProduct(invDetails.get(i).PRD_UID, i);
                }
            }
        }


    }

    private void getDescription(String userName, String pass, String id) {

        if (!isNetworkAvailable(getActivity())){
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }
        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات...", true);
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


    private void getMaxSales(String userName, String pass, String Prd_GUID, String s) {


        if (!isNetworkAvailable(getActivity())){
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }
        try {
            compositeDisposable.add(
                    App.api.getMaxSales(userName, pass, Prd_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                        int remain = -1000000000;
                                        try {
                                            remain = Integer.parseInt(jsonElement);
                                        } catch (Exception e) {
                                            Gson gson = new Gson();
                                            Type typeIDs = new TypeToken<ModelLog>() {
                                            }.getType();
                                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                                            int message = iDs.getLogs().get(0).getMessage();
                                            String description = iDs.getLogs().get(0).getDescription();
                                            if (message != 1) {
                                                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        if (remain != -1000000000) {


                                            ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);


                                            CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));

                                            if (result.size() > 0) {
                                                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                                                double amount = 0.0;
                                                if (!s.equals("")) {
                                                    amount = Double.parseDouble(s);
                                                    if (remain - amount < 0) {
                                                        Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + jsonElement, Toast.LENGTH_SHORT).show();

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
                                                    invoiceDetail.update();

                                                }
                                                invoiceDetailAdapter.notifyDataSetChanged();

                                            }

                                        }

                                    }
                                    , throwable -> {
                                        Toast.makeText(getContext(), "خطا در دریافت مانده کالا", Toast.LENGTH_SHORT).show();

                                    })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در دریافت مانده کالا", Toast.LENGTH_SHORT).show();

        }


    }


    @SuppressLint("SetTextI18n")
    private void getProduct(String Guid, int i) {

        if (!isNetworkAvailable(getActivity())){
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        try {

            compositeDisposable.add(
                    App.api.getProduct(userName, passWord, Guid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                counter = counter + 1;
                                Gson gson = new Gson();
                                Type typeModelProduct = new TypeToken<ModelProduct>() {
                                }.getType();


                                ModelProduct iDs = null;


                                try {
                                    iDs = gson.fromJson(jsonElement, typeModelProduct);
                                } catch (Exception ignore) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }


                                if (iDs != null) {


                                    ArrayList<Product> list1 = new ArrayList<>(iDs.getProductList());
                                    if (list1.size() > 0) {
                                        ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + list1.get(0).getI() + "'").first();

                                        if (product != null)
                                            product.update();
                                        else
                                            ir.kitgroup.saleinmeat.DataBase.Product.saveInTx(list1.get(0));

                                    }

                                    if (counter == invDetails.size()) {
                                        invoiceDetailList.clear();

                                        for (int j = 0; j < invDetails.size(); j++) {
                                            if (invDetails.get(j).INV_DET_DESCRIBTION != null && invDetails.get(j).INV_DET_DESCRIBTION.equals("توزیع")) {

                                                if (type.equals("1")) {
                                                    sumTransport = Double.parseDouble(invDetails.get(j).INV_DET_TOTAL_AMOUNT);
                                                    binding.tvSumTransport.setText(format.format(sumTransport) + " ریال ");
                                                    binding.layoutTransport.setVisibility(View.VISIBLE);
                                                }

                                                invDetails.remove(invDetails.get(j));


                                            }
                                        }

                                        invoiceDetailList.addAll(invDetails);
                                        invoiceDetailAdapter.notifyDataSetChanged();
                                        binding.progressBar.setVisibility(View.GONE);
                                        counter = 0;
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "لیست دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }

                            }, throwable -> {


                            })
            );
        } catch (Exception e) {
            Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }

    }


    @SuppressLint("SetTextI18n")
    private void getInvoice() {

        if (!isNetworkAvailable(getActivity())){
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    App.api.getInvoice1(userName, passWord, Inv_GUID)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelInvoice>() {
                                }.getType();
                                ModelInvoice iDs = null;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception ignored) {
                                    Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }

                                if (iDs != null) {

                                    InvoiceDetail.saveInTx(iDs.getInvoiceDetail());

                                    if (iDs.getInvoice().size() == 0) {
                                        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                                    } else {


                                        status = iDs.getInvoice().get(0).INV_SYNC;
                                        invFinal = iDs.getInvoice().get(0).invFinalStatusControl;
                                        Ord_TYPE = String.valueOf(iDs.getInvoice().get(0).INV_TYPE_ORDER);
                                        sumDiscountsInvoiceRial = iDs.getInvoice().get(0).INV_TOTAL_DISCOUNT;


                                        if (company.mode  == 1) {
                                            Acc_NAME = iDs.getInvoice().get(0).accClbName;
                                            Acc_GUID = iDs.getInvoice().get(0).ACC_CLB_UID;
                                        }


                                            binding.tvNameCustomer.setText(Acc_NAME);

                                        if (status != null && (status.equals("*") || invFinal)) {
                                            binding.layoutEditDelete.setVisibility(View.GONE);
                                        } else if (status != null && status.equals("-")) {
                                            binding.layoutEditDelete.setVisibility(View.VISIBLE);

                                        }


                                        if (iDs.getInvoice().get(0).INV_DUE_DATE_PERSIAN != null)
                                            binding.txtDate.setText(iDs.getInvoice().get(0).INV_DUE_DATE_PERSIAN);


                                        invDetails.addAll(iDs.getInvoiceDetail());


                                    }


                                    if (invDetails.size() == 0)
                                        binding.progressBar.setVisibility(View.GONE);
                                    else
                                        for (int i = 0; i < invDetails.size(); i++) {
                                            getProduct(invDetails.get(i).PRD_UID, i);
                                        }


                                } else {
                                    Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);


                                }
                                binding.progressBar.setVisibility(View.GONE);

                            }, throwable -> {
                                Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }

    }

    private void getDeleteInvoice(String userName, String pass, String Inv_GUID) {

        customProgress.showProgress(getActivity(), "در حال دریافت تغییرات فاکتور...", false);

        try {


            Call<String> call = App.api.getDeleteInvoice(userName, pass, Inv_GUID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    if (iDs != null) {
                        int message = iDs.getLogs().get(0).getMessage();
                        if (message == 4) {
                            List<InvoiceDetail> invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                            for (int i = 0; i < invoiceDetail.size(); i++) {
                                ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + invoiceDetail.get(i).PRD_UID + "'").first();
                                if (product != null) {
                                    product.delete();
                                }
                                InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                            }


                            if (company.mode  == 2) {
                                getActivity().getSupportFragmentManager().popBackStack();
                                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("OrderListFragment");
                                FragmentManager ft = getActivity().getSupportFragmentManager();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    ft.beginTransaction().detach(frg).commitNow();
                                    ft.beginTransaction().attach(frg).commitNow();

                                } else {

                                    ft.beginTransaction().detach(frg).attach(frg).commit();
                                }
                            } else {
                                Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                                if (tb != null && tb.GO != null)
                                    Tables.delete(tb);

                                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("LauncherFragment");
                                FragmentManager ft = getActivity().getSupportFragmentManager();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    ft.beginTransaction().detach(frg).commitNow();
                                    ft.beginTransaction().attach(frg).commitNow();

                                } else {

                                    ft.beginTransaction().detach(frg).attach(frg).commit();
                                }
                            }

                        }
                    } else {
                        Toast.makeText(getActivity(), "خطایی در سرور رخ داد", Toast.LENGTH_SHORT).show();
                    }


                    customProgress.hideProgress();

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }


    @Override
    public void onDestroy() {
        if (type.equals("1")) {
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" +
                    Inv_GUID + "'").list();
            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }
        }


        super.onDestroy();
    }

    public List<InvoiceDetail> getInvoiceDetail() {
        return invDetails;
    }
    private   boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowErrorConnection(String error) {
        binding.progressBar.setVisibility(View.GONE);
        textMessageDialog.setText(error);
        ivIconSync.setImageResource(R.drawable.ic_wifi);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
        customProgress.hideProgress();

    }
}

