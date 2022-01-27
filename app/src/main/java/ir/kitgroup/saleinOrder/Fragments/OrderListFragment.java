package ir.kitgroup.saleinOrder.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Activities.LauncherActivity;
import ir.kitgroup.saleinOrder.Adapters.OrderListAdapter;

import ir.kitgroup.saleinOrder.Connect.API;
import ir.kitgroup.saleinOrder.DataBase.Account;
import ir.kitgroup.saleinOrder.DataBase.InvoiceDetail;

import ir.kitgroup.saleinOrder.classes.ConfigRetrofit;
import ir.kitgroup.saleinOrder.classes.CustomProgress;
import ir.kitgroup.saleinOrder.DataBase.Company;
import ir.kitgroup.saleinOrder.models.Invoice;


import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.classes.Util;

import ir.kitgroup.saleinOrder.classes.DateConverter;
import ir.kitgroup.saleinOrder.databinding.FragmentOrderListBinding;
import ir.kitgroup.saleinOrder.models.ModelInvoice;

import ir.kitgroup.saleinOrder.models.ModelLog;
import ir.kitgroup.saleinOrder.models.PaymentRecieptDetail;


@AndroidEntryPoint
public class OrderListFragment extends Fragment {


    private API api;

    private Company company;


    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Typeface typeface;

    //region Parameter
    private CustomProgress customProgress;
    private CompositeDisposable compositeDisposable;

    private FragmentOrderListBinding binding;

    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private ImageView ivIconSync;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync


    //endregion Parameter

    public OrderListAdapter orderListAdapter;
    private final ArrayList<Invoice> list = new ArrayList<>();


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentOrderListBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);
        compositeDisposable = new CompositeDisposable();
        customProgress = CustomProgress.getInstance();

        String accGUID = Select.from(Account.class).list().get(0).I;


            company = null;
            api = null;
            company = Select.from(Company.class).first();
        api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/",false,30).create(API.class);




        //region Calculate Date Always Product


        int dayAlways = 100;
        try {
            dayAlways = Integer.parseInt("700");
        } catch (Exception ignore) {
        }
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                new SimpleDateFormat("dd/MM/yyyy");
        Date newDate = Util.deleteDays(date, -dayAlways);
        String d = dateFormats.format(newDate);
        String[] da = d.split("/");
        DateConverter converter = new DateConverter();
        String datVip = converter.gregorianToPersian(Integer.parseInt(da[2]), Integer.parseInt(da[1]), Integer.parseInt(da[0]));
        //endregion Calculate Date Always Product


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
        btnNoDialog.setOnClickListener(v -> dialogSync.dismiss());


        btnOkDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            getAllInvoice1(accGUID, datVip);

        });

        //endregion Cast Variable Dialog Sync


        list.clear();


        orderListAdapter = new OrderListAdapter(getActivity(), list);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(orderListAdapter);
        binding.recycler.setHasFixedSize(false);
        orderListAdapter.setOnClickItemListener((description, type, Inv_GUID) -> {

            if (type == 1) {
                Bundle bundleOrder = new Bundle();
                bundleOrder.putString("Inv_GUID", Inv_GUID);
                bundleOrder.putString("Tbl_GUID", "");
                bundleOrder.putString("Tbl_NAME", "");
                bundleOrder.putString("Ord_TYPE", "");
                bundleOrder.putString("Acc_GUID", "");
                bundleOrder.putString("Acc_NAME", "");
                bundleOrder.putString("type", "1");

                InVoiceDetailFragment inVoiceDetailFragment = new InVoiceDetailFragment();
                inVoiceDetailFragment.setArguments(bundleOrder);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, inVoiceDetailFragment, "InVoiceDetailFragment").addToBackStack("InVoiceDetailFX").commit();
            } else {
                Invoice invoice = new Invoice();
                invoice.INV_UID = Inv_GUID;
                invoice.INV_DESCRIBTION = description;
                List<Invoice> listInvoice = new ArrayList<>();
                listInvoice.add(invoice);


                List<InvoiceDetail> invoiceDetailList = new ArrayList<>();
                List<PaymentRecieptDetail> clsPaymentRecieptDetails = new ArrayList<>();
                sendFeedBack(listInvoice, invoiceDetailList, clsPaymentRecieptDetails);
            }


        });


        binding.ivBack.setOnClickListener(v -> {
                    ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
        );

        getAllInvoice1(accGUID, datVip);


    }


    private void getAllInvoice1(String AccGuid, String date) {
        if (!isNetworkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }

        try {
            compositeDisposable.add(
                    api.getAllInvoice1(company.USER, company.PASS, AccGuid, date)
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
                                    binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
                                    binding.txtError.setVisibility(View.VISIBLE);
                                    binding.txtError.setText("دریافت آخرین اطلاعات ناموفق");
                                    binding.progressBar.setVisibility(View.GONE);
                                }

                                if (iDs != null) {

                                    list.clear();

                                    list.addAll(iDs.getInvoice());
                                    if (list.size() == 0) {
                                        binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
                                        binding.txtError.setVisibility(View.VISIBLE);
                                        binding.txtError.setText("هیچ سفارشی وجود ندارد");
                                    }
                                    orderListAdapter.notifyDataSetChanged();


                                    // InvoiceDetail.saveInTx(iDs.getInvoiceDetail());

                                } else {

                                    binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
                                    binding.txtError.setVisibility(View.VISIBLE);
                                    binding.txtError.setText("دریافت آخرین اطلاعات ناموفق");
                                    binding.progressBar.setVisibility(View.GONE);


                                }
                                binding.progressBar.setVisibility(View.GONE);


                            }, throwable -> {

                                binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
                                binding.txtError.setVisibility(View.VISIBLE);
                                binding.txtError.setText("خطا در دریافت اطلاعات فاکتور...");
                                binding.progressBar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {


            binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText("خطا در دریافت اطلاعات فاکتور...");
            binding.progressBar.setVisibility(View.GONE);
        }

    }

    private boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowErrorConnection() {
        binding.progressBar.setVisibility(View.GONE);
        textMessageDialog.setText("خطا در اتصال به اینترنت");
        ivIconSync.setImageResource(R.drawable.ic_wifi);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();


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
        compositeDisposable.clear();
    }


    private class JsonObject {
        public List<Invoice> Invoice;
        public List<ir.kitgroup.saleinOrder.DataBase.InvoiceDetail> InvoiceDetail;
        public List<ir.kitgroup.saleinOrder.models.PaymentRecieptDetail> PaymentRecieptDetail;
    }

    private void sendFeedBack(List<Invoice> invoice, List<InvoiceDetail> invoiceDetail, List<PaymentRecieptDetail> clsPaymentRecieptDetail) {

        if (!isNetworkAvailable(getActivity())) {
            ShowErrorConnection();
            return;
        }
        try {
            customProgress.showProgress(getActivity(), "در حال ارسال سفارش", true);
            JsonObject jsonObject = new JsonObject();
            jsonObject.Invoice = invoice;
            jsonObject.InvoiceDetail = invoiceDetail;
            jsonObject.PaymentRecieptDetail = clsPaymentRecieptDetail;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObject>() {
            }.getType();
            compositeDisposable.add(
                    api.sendFeedBack(company.USER, company.PASS, gson.toJson(jsonObject, typeJsonObject))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                customProgress.hideProgress();

                                Gson gson1 = new Gson();
                                Type typeIDs = new TypeToken<ModelLog>() {
                                }.getType();
                                ModelLog iDs = gson1.fromJson(jsonElement, typeIDs);
                                int message = 0;

                                if (iDs != null) {
                                    message = iDs.getLogs().get(0).getMessage();

                                }
                                if (message == 1) {

                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                            .setMessage("نظر شما با موفقیت ارسال شد.")
                                            .setPositiveButton("بستن", (dialog, which) -> {
                                                dialog.dismiss();
                                            })
                                            .show();

                                    TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                                    textView.setTypeface(face);
                                    textView.setTextColor(getResources().getColor(R.color.green_table));
                                    textView.setTextSize(13);

                                    orderListAdapter.notifyDataSetChanged();

                                } else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                            .setMessage("خطا در ارسال نظر")
                                            .setPositiveButton("بستن", (dialog, which) -> {
                                                dialog.dismiss();
                                            })
                                            .show();

                                    TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                                    textView.setTypeface(face);
                                    textView.setTextColor(getResources().getColor(R.color.red_table));
                                    textView.setTextSize(13);

                                }


                            }, throwable -> {

                                customProgress.hideProgress();

                                if (customProgress.isShow)
                                    customProgress.hideProgress();
                                Toast.makeText(getActivity(), "خطا در ارسال توضیحات", Toast.LENGTH_SHORT).show();


                            })
            );
        } catch (Exception e) {
            customProgress.hideProgress();

            if (customProgress.isShow)
                customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در ارسال توضیحات", Toast.LENGTH_SHORT).show();
        }

    }


}
