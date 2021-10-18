package ir.kitgroup.saleinOrder.Fragments.MobileView;


import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Adapters.OrderListAdapter;

import ir.kitgroup.saleinOrder.DataBase.Account;
import ir.kitgroup.saleinOrder.models.Invoice;

import ir.kitgroup.saleinOrder.DataBase.InvoiceDetail;
import ir.kitgroup.saleinOrder.DataBase.User;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.Util.Util;
import ir.kitgroup.saleinOrder.classes.App;

import ir.kitgroup.saleinOrder.classes.DateConverter;
import ir.kitgroup.saleinOrder.databinding.FragmentOrderListBinding;
import ir.kitgroup.saleinOrder.models.ModelInvoice;



public class OrderListFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    //region Parameter
    private FragmentOrderListBinding binding;

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


        String userName = Select.from(User.class).list().get(0).userName;
        String passWord = Select.from(User.class).list().get(0).passWord;
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



        getAllInvoice1(userName, passWord, accGUID,datVip);
    }










    private void getAllInvoice1(String userName, String pass, String AccGuid, String date) {
        try {
            compositeDisposable.add(
                    App.api.getAllInvoice1(userName, pass,AccGuid,date )
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



}