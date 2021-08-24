package ir.kitgroup.salein1.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.UUID;

import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.Classes.CustomProgress;
import ir.kitgroup.salein1.DataBase.Invoice;
import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.OrderType;
import ir.kitgroup.salein1.DataBase.Tables;
import ir.kitgroup.salein1.DataBase.User;


import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.salein1.Models.ModelLog;
import ir.kitgroup.salein1.R;

import ir.kitgroup.salein1.databinding.FragmentPaymentMobileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMobileFragment extends Fragment {


    //region Parameter
    private FragmentPaymentMobileBinding binding;

    private CustomProgress customProgress;

    private String userName;
    private String passWord;
    private String numberPos = "";
    private Dialog dialog;
    private TextView tvMessage;
    private String Inv_GUID;

    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");
    //end region Parameter


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customProgress = CustomProgress.getInstance();

        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;
        numberPos = Select.from(User.class).first().numberPos;
        if (numberPos != null && numberPos.equals(""))
            numberPos = "0";


        Bundle bundle = getArguments();
        Inv_GUID = bundle.getString("Inv_GUID");
        String Tbl_GUID = bundle.getString("Tbl_GUID");
        String Acc_NAME = bundle.getString("");
        String Acc_GUID = bundle.getString("Acc_GUID");
        String DESC = bundle.getString("DESC");
        String Ord_TYPE = bundle.getString("Ord_TYPE");
        String Sum_PRICE = bundle.getString("Sum_PRICE");


        //region Cast DialogSendOrder
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        tvMessage = dialog.findViewById(R.id.tv_message);
        RelativeLayout rlButtons = dialog.findViewById(R.id.layoutButtons);
        MaterialButton btnReturned = dialog.findViewById(R.id.btn_returned);
        rlButtons.setVisibility(View.GONE);
        btnReturned.setVisibility(View.VISIBLE);


        String finalOrd_TYPE1 = Ord_TYPE;
        btnReturned.setOnClickListener(v -> {


            for (int i = 0; i < InVoiceDetailMobileFragment.invoiceDetailList.size(); i++) {
                InVoiceDetailMobileFragment.invoiceDetailList.get(i).ROW_NUMBER = String.valueOf(i + 1);
            }


            if (Tbl_GUID.equals("")) {
                Tables tables = new Tables();
                tables.N = "بیرون بر";
                tables.ACT = true;


                if (!finalOrd_TYPE1.equals(""))
                    tables.C = Integer.parseInt(finalOrd_TYPE1);


                tables.RSV = false;

                tables.I = UUID.randomUUID().toString();

                Invoice inv = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
                if (inv != null) {
                    inv.TBL_UID = tables.I;
                    inv.save();
                }

                tables.save();


            } else {
                Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                if (tb != null) {
                    tb.ACT = true;
                    tb.update();
                }
            }

            // LauncherFragment.tableAdapter.notifyDataSetChanged();

            dialog.dismiss();
            if (App.mode == 2) {

                for (int i = 0; i < 2; i++) {
                    getFragmentManager().popBackStack();
                }
                MainOrderMobileFragment.invoiceDetailList.clear();
                InVoiceDetailMobileFragment.invoiceDetailList.clear();
                MainOrderMobileFragment.Inv_GUID="";

                assert getFragmentManager() != null;

                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
               if (frg!=null) {
                   ft.detach(frg);
                   ft.attach(frg);
                   ft.commit();
               }

            } else {
                Invoice invoice2 = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                invoice2.SendStatus = true;
                invoice2.save();
                for (int i = 0; i < 4; i++) {
                    getFragmentManager().popBackStack();
                }
                LauncherOrganizationFragment launcherFragment = new LauncherOrganizationFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, launcherFragment, "LauncherFragment").addToBackStack("LauncherF").commit();

            }


        });

        //endregion Cast DialogSendOrder


        binding.tvPrice.setText("مبلغ قابل پرداخت : " + format.format(Double.parseDouble(Sum_PRICE)));


        if (App.mode == 1) {

            binding.rlTypeOrder.setVisibility(View.GONE);
        }


        if (App.mode == 2) {
            OrderType OrdT = Select.from(OrderType.class).where("TY =" + 2).first();
            if (OrdT != null) {
                binding.tvTitleTypeOrder.setText(OrdT.getN());
                int c = OrdT.getC();
                Ord_TYPE = String.valueOf(c);
            }
        }


        String finalOrd_TYPE = Ord_TYPE;
        binding.orderListBtnRegister.setOnClickListener(v -> {


            Date date = Calendar.getInstance().getTime();

            float sumPrice = 0;
            float sumDiscount = 0;
            float sumPurePrice = 0;

            for (Invoicedetail invoicedetail : Select.from(Invoicedetail.class).where("INVUID = '" + Inv_GUID + "'").list()) {
                Invoicedetail.deleteInTx(invoicedetail);
            }
            for (int i = 0; i < InVoiceDetailMobileFragment.invoiceDetailList.size(); i++) {
                sumPurePrice = sumPurePrice + Float.parseFloat(InVoiceDetailMobileFragment.invoiceDetailList.get(i).INV_DET_TOTAL_AMOUNT);
                sumPrice = sumPrice + (Float.parseFloat(InVoiceDetailMobileFragment.invoiceDetailList.get(i).INV_DET_QUANTITY) * Float.parseFloat(InVoiceDetailMobileFragment.invoiceDetailList.get(i).INV_DET_PRICE_PER_UNIT));
                InVoiceDetailMobileFragment.invoiceDetailList.get(i).ROW_NUMBER = String.valueOf(i + 1);
                sumDiscount = sumDiscount + Float.parseFloat(InVoiceDetailMobileFragment.invoiceDetailList.get(i).INV_DET_DISCOUNT);
            }

            List<Invoicedetail> invoiceList = Select.from(Invoicedetail.class).where("INVUID = '" + Inv_GUID + "'").list();
            for (int i = 0; i < invoiceList.size(); i++) {
                Invoicedetail.delete(invoiceList.get(i));
            }

            Invoicedetail.saveInTx(InVoiceDetailMobileFragment.invoiceDetailList);
            Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
            invoice.INV_UID = Inv_GUID;
            invoice.INV_TOTAL_AMOUNT = String.valueOf(sumPrice);//جمع فاکنور
            invoice.INV_TOTAL_DISCOUNT = "0";
            invoice.INV_PERCENT_DISCOUNT = "0";
            invoice.INV_DET_TOTAL_DISCOUNT = String.valueOf(sumDiscount);
            invoice.INV_DESCRIBTION = DESC;
            invoice.INV_TOTAL_TAX = "0";
            invoice.INV_TOTAL_COST = "0";
            invoice.INV_EXTENDED_AMOUNT = String.valueOf(sumPurePrice);
            invoice.INV_DATE = date;
            invoice.INV_DUE_DATE = date;
            invoice.INV_STATUS = true;
            invoice.ACC_CLB_UID = Acc_GUID;
            invoice.TBL_UID = Tbl_GUID;
            invoice.INV_TYPE_ORDER = finalOrd_TYPE;
            invoice.Acc_name = Acc_NAME;
            invoice.save();
            // LauncherFragment.factorGuid = GID;


            List<Invoice> listInvoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").list();
            // List<Invoicedetail> invoiceDetailList = Select.from(Invoicedetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            SendOrder(listInvoice, InVoiceDetailMobileFragment.invoiceDetailList);


        });


        clickItemPay(binding.layoutPaymentPlace, binding.ivOkPaymentPlace);





    }


    private static class JsonObject {
        public List<Invoice> Invoice;
        public List<Invoicedetail> InvoiceDetail;
    }


    void SendOrder(List<Invoice> invoice, List<Invoicedetail> invoiceDetail) {

        try {

            customProgress.showProgress(getActivity(), "در حال ارسال سفارش", false);
            JsonObject jsonObject = new JsonObject();
            jsonObject.Invoice = invoice;
            jsonObject.InvoiceDetail = invoiceDetail;
            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObject>() {
            }.getType();


            Call<String> call = App.api.PostData(userName, passWord,
                    gson.toJson(jsonObject, typeJsonObject),
                    "",
                    numberPos);

            call.enqueue(new Callback<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<String> call, final Response<String> response) {


                    customProgress.hideProgress();

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    int message = iDs.getLogs().get(0).getMessage();
                    String description = iDs.getLogs().get(0).getDescription();
                    if (message == 1) {

                        tvMessage.setText("سفارش با موفقیت ارسال شد");
                        Invoice invoice2 = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                        invoice2.SendStatus = true;
                        invoice2.save();
                        dialog.show();
                    } else if (message == 2) {
                        Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                        invoice.SendStatus = false;
                        invoice.save();
                        tvMessage.setText("خطا در ارسال"+"\n"+description+"\n"+"این سفارش ذخیره می شود با مرجعه به پروفایل خود سفارش را مجددارسال کنید");
                        dialog.show();
                    } else if (message == 3) {
                        Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                        invoice.SendStatus = false;
                        invoice.save();
                        tvMessage.setText("خطا در ارسال"+"\n"+description+"\n"+"این سفارش ذخیره می شود با مرجعه به پروفایل خود سفارش را مجددارسال کنید");
                        dialog.show();

                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در ارتباط" + t.toString(), Toast.LENGTH_SHORT).show();
                    Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                    invoice.SendStatus = false;
                    invoice.save();

                }
            });
        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getActivity(), "خطا در ارتباط" + ex.toString(), Toast.LENGTH_SHORT).show();
            Invoice invoice1 = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
            invoice1.SendStatus = false;
            invoice1.save();
        }
    }


    private void clickItemPay(MaterialCardView view, ImageView imageView) {

        binding.layoutPaymentPlace.setStrokeColor(getActivity().getResources().getColor(R.color.colorLight));
        binding.ivOkPaymentPlace.setVisibility(View.GONE);


        if (view != null) {
            view.setStrokeColor(getActivity().getResources().getColor(R.color.purple_700));
            imageView.setVisibility(View.VISIBLE);
        }

    }

}
