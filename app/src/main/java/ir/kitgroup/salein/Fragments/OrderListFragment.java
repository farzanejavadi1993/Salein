package ir.kitgroup.salein.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Adapters.OrderListAdapter;

import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.models.Company;
import ir.kitgroup.salein.models.Invoice;

import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;

import ir.kitgroup.salein.classes.DateConverter;
import ir.kitgroup.salein.databinding.FragmentOrderListBinding;
import ir.kitgroup.salein.models.ModelInvoice;


@AndroidEntryPoint
public class OrderListFragment extends Fragment {

    @Inject
    API api;

    @Inject
    Company company;
     private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    //region Parameter
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



        String accGUID = Select.from(Account.class).list().get(0).I;




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
        btnNoDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
        });


        btnOkDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            getAllInvoice1( accGUID,datVip);

        });

        //endregion Cast Variable Dialog Sync





        list.clear();


        orderListAdapter = new OrderListAdapter(getContext(), list,2);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(orderListAdapter);
        binding.recycler.setHasFixedSize(false);
        orderListAdapter.setOnClickItemListener((type, Inv_GUID) -> {


            Bundle bundle = new Bundle();

            bundle.putString("type", "1");
            bundle.putString("Inv_GUID", Inv_GUID);
            bundle.putString("Tbl_GUID", "");
            bundle.putString("Ord_TYPE","");

            InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
            inVoiceDetailFragmentMobile.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobileX").commit();
        });



        getAllInvoice1( accGUID,datVip);
    }










    private void getAllInvoice1(String AccGuid, String date) {
        if (!isNetworkAvailable(getActivity())){
            ShowErrorConnection("خطا در اتصال به اینترنت");
            return;
        }

        try {
            compositeDisposable.add(
                   api.getAllInvoice1(company.userName, company.passWord,AccGuid,date )
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
                                    if (list.size()==0){
                                        binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
                                        binding.txtError.setVisibility(View.VISIBLE);
                                        binding.txtError.setText("هیچ سفارشی وجود ندارد");
                                    }
                                    orderListAdapter .notifyDataSetChanged();







                                    InvoiceDetail.saveInTx(iDs.getInvoiceDetail());

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


    }

}
