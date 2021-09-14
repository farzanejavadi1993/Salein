package ir.kitgroup.salein.Fragments.MobileView;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
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

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ir.kitgroup.salein.Adapters.OrderListAdapter;

import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Invoice;

import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.classes.DateConverter;
import ir.kitgroup.salein.databinding.FragmentOrderListBinding;
import ir.kitgroup.salein.models.ModelInvoice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderListFragment extends Fragment {

    //region Parameter
    private FragmentOrderListBinding binding;
    private CustomProgress customProgress;
    //endregion Parameter

    public OrderListAdapter orderListAdapter;
    private final ArrayList<Invoice> list = new ArrayList<>();

    private String userName;
    private String passWord;
    private String AccGUID;

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


        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;
        AccGUID = Select.from(Account.class).list().get(0).I;
        customProgress = CustomProgress.getInstance();



           //region Calculate Date Always Product


        int dayAlways = 100;
        try {
            dayAlways = Integer.parseInt("700");
        } catch (Exception e) {
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
            Invoice invoice=Select.from(Invoice.class).where("INVUID ='"+Inv_GUID+"'").first();

            Bundle bundle = new Bundle();

            bundle.putString("type", "1");
            bundle.putString("Inv_GUID", Inv_GUID);
            bundle.putString("Tbl_GUID", "");
            bundle.putString("Ord_TYPE",String.valueOf(invoice.INV_TYPE_ORDER));

            InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
            inVoiceDetailFragmentMobile.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobileX").commit();
        });



        getAllInvoice(userName,passWord,AccGUID,datVip);
    }


    private void getAllInvoice(String userName, String pass, String AccGuid, String date) {

        customProgress.showProgress(getActivity(), "در حال دریافت سفارشات...", false);

        try {


            Call<String> call = App.api.getAllInvoice(userName, pass,AccGuid, date);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelInvoice>() {
                    }.getType();
                    ModelInvoice iDs = null;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception ignored) {
                    }

                    if (iDs != null) {

                        List<Invoice> invoicese=Select.from(Invoice.class).list();
                        CollectionUtils.filter(invoicese,i->i.INV_SYNC.equals("@") || i.INV_SYNC.equals("#"));
                        if (invoicese.size()>0){
                            List<InvoiceDetail> invoiceDetails= Select.from(InvoiceDetail.class).list();
                            CollectionUtils.filter(invoiceDetails,i->!i.INV_UID.equals(invoicese.get(0).INV_UID));
                            for (int i=0;i<invoiceDetails.size();i++){
                                InvoiceDetail.delete(invoiceDetails.get(i));
                            } }





                        List<Invoice> lists=Select.from(Invoice.class).list();
                        CollectionUtils.filter(lists,l->l.INV_SYNC ==null);
                        if (lists.size()>0){
                            for (int i=0;i<lists.size();i++){
                                Invoice.delete(lists.get(i));
                            }
                        }

                        List<Invoice> lists1=Select.from(Invoice.class).list();
                        CollectionUtils.filter(lists1,l-> !l.INV_SYNC.equals("#") && !l.INV_SYNC.equals("@"));
                        if (lists1.size()>0){
                            for (int i=0;i<lists1.size();i++){
                                Invoice.delete(lists1.get(i));
                            }
                        }


                            Invoice.saveInTx(iDs.getInvoice());
                            InvoiceDetail.saveInTx(iDs.getInvoiceDetail());







                        list.clear();
                        List<Invoice> invoices=Select.from(Invoice.class).list();
                        CollectionUtils.filter(invoices,i->!i.INV_SYNC.equals("@"));
                        list.addAll(invoices);
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


                    }
                    customProgress.hideProgress();

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();


                    binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
                    binding.txtError.setVisibility(View.VISIBLE);
                    binding.txtError.setText("خطا در دریافت اطلاعات فاکتور...");

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();

            binding.txtError.setTextColor(getResources().getColor(R.color.medium_color));
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText("خطا در دریافت اطلاعات فاکتور...");
        }


    }
}
