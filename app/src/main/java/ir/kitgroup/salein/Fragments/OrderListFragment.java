package ir.kitgroup.salein.Fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.salein.Activities.LauncherActivity;
import ir.kitgroup.salein.Adapters.OrderListAdapter;
import ir.kitgroup.salein.Connect.MyViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.Invoice;


import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;

import ir.kitgroup.salein.classes.DateConverter;
import ir.kitgroup.salein.databinding.FragmentOrderListBinding;
import ir.kitgroup.salein.models.PaymentRecieptDetail;


@AndroidEntryPoint
public class OrderListFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Typeface typeface;

    private Company company;


    //region Parameter
    private CustomProgress customProgress;
    private FragmentOrderListBinding binding;


    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync

    //endregion Parameter

    public OrderListAdapter orderListAdapter;
    private final ArrayList<Invoice> list = new ArrayList<>();
    private String datVip = "";
    private String accGUID = "";
    private MyViewModel myViewModel;


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
        customProgress = CustomProgress.getInstance();

        accGUID = Select.from(Account.class).list().get(0).I;
        company = Select.from(Company.class).first();


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
        datVip = converter.gregorianToPersian(Integer.parseInt(da[2]), Integer.parseInt(da[1]), Integer.parseInt(da[0]));
        //endregion Calculate Date Always Product


        //region Cast Variable Dialog Sync
        dialogSync = new Dialog(getActivity());
        dialogSync.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSync.setContentView(R.layout.custom_dialog);
        dialogSync.setCancelable(false);

        textMessageDialog = dialogSync.findViewById(R.id.tv_message);

        btnOkDialog = dialogSync.findViewById(R.id.btn_ok);
        btnNoDialog = dialogSync.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> dialogSync.dismiss());


        btnOkDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getAllInvoice(company.USER, company.PASS, accGUID, datVip);

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
                customProgress.showProgress(getActivity(), "در حال ارسال پیام", false);
                myViewModel.sendFeedBack(company.USER, company.PASS, listInvoice, invoiceDetailList, clsPaymentRecieptDetails);
            }


        });


        binding.ivBack.setOnClickListener(v -> {
                    ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
        );


    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getAllInvoice(company.USER, company.PASS, accGUID, datVip);
        myViewModel.getResultAllInvoice().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                customProgress.hideProgress();
                binding.progressBar.setVisibility(View.GONE);
                return;

            }

            myViewModel.getResultAllInvoice().setValue(null);
            list.clear();
            list.addAll(result);
            if (list.size() == 0) {
                binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
                binding.txtError.setVisibility(View.VISIBLE);
                binding.txtError.setText("هیچ سفارشی وجود ندارد");
            }
            orderListAdapter.notifyDataSetChanged();

            binding.progressBar.setVisibility(View.GONE);
            customProgress.hideProgress();

        });
        myViewModel.getResultFeedBack().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                customProgress.hideProgress();
                binding.progressBar.setVisibility(View.GONE);
                return;

            }

            myViewModel.getResultFeedBack().setValue(null);
            customProgress.hideProgress();

            int message;


            message = result.getLogs().get(0).getMessage();
            if (message == 1) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("نظر شما با موفقیت ارسال شد.")
                        .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
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
                        .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                        .show();

                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                textView.setTypeface(face);
                textView.setTextColor(getResources().getColor(R.color.red_table));
                textView.setTextSize(13);

            }

            binding.progressBar.setVisibility(View.GONE);
            customProgress.hideProgress();

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText("خطا در دریافت اطلاعات فاکتور...");
            binding.progressBar.setVisibility(View.GONE);
            if (result == null)
                return;
            myViewModel.getResultMessage().setValue(null);
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();


        });

    }


    private void ShowErrorConnection() {
        binding.progressBar.setVisibility(View.GONE);
        textMessageDialog.setText("خطا در اتصال به اینترنت");

        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialogSync.dismiss();
        dialogSync.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public static class JsonObject {
        public List<Invoice> Invoice;
        public List<ir.kitgroup.salein.DataBase.InvoiceDetail> InvoiceDetail;
        public List<ir.kitgroup.salein.models.PaymentRecieptDetail> PaymentRecieptDetail;
    }


}
