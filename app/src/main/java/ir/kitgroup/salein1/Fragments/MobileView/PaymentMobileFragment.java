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

import android.view.WindowManager;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Objects;
import java.util.UUID;

import ir.kitgroup.salein1.classes.App;
import ir.kitgroup.salein1.classes.CustomProgress;
import ir.kitgroup.salein1.DataBase.Account;
import ir.kitgroup.salein1.DataBase.Invoice;
import ir.kitgroup.salein1.DataBase.InvoiceDetail;
import ir.kitgroup.salein1.DataBase.OrderType;
import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.DataBase.Tables;
import ir.kitgroup.salein1.DataBase.User;


import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.salein1.Util.Util;
import ir.kitgroup.salein1.models.ModelLog;
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

    private ArrayList<Integer> codeList = new ArrayList<>();
    private String Ord_TYPE;


    List<InvoiceDetail> invDetails;
    //end region Parameter


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentMobileBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);
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

        Ord_TYPE = bundle.getString("Ord_TYPE");
        String Sum_PURE_PRICE = bundle.getString("Sum_PURE_PRICE");
        String Sum_PRICE = bundle.getString("Sum_PRICE");


        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();


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




            dialog.dismiss();
            if (App.mode == 2) {

                for (int i = 0; i < 2; i++) {
                    getFragmentManager().popBackStack();
                }



                assert getFragmentManager() != null;

                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                if (frg != null) {
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }

            }
            else {
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


                }
                else {
                    Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                    if (tb != null) {
                        tb.ACT = true;
                        tb.update();
                    }
                }
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


        Account acc = Select.from(Account.class).first();
        if (acc!=null &&  acc.ADR!=null && !acc.ADR.equals(""))
            binding.tvTAddress.setText(acc.ADR);
        else
            binding.tvTAddress.setText("ناموجود");


        binding.sumPriceTxt.setText(format.format(Double.parseDouble(Sum_PRICE)) + "ریال");
        binding.purePriceTxt.setText(format.format(Double.parseDouble(Sum_PURE_PRICE))+"ریال");

        if (App.mode == 1) {

            binding.rlTypeOrder.setVisibility(View.GONE);
        }



        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        if (App.mode == 2) {
            List<OrderType> OrdT = Select.from(OrderType.class).where("TY =" + 2).list();
            if (OrdT.size() > 0) {


                codeList.clear();
                for (int i = 0; i < OrdT.size(); i++) {
                    binding.spinner.setItems(OrdT.get(i).getN());
                    codeList.add(OrdT.get(i).getC());
                    Ord_TYPE = String.valueOf(OrdT.get(0).getC());
                }

            }
        }
        binding.spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view1, position, id, item) -> {

            Ord_TYPE = String.valueOf(codeList.get(position));

        });


        String finalOrd_TYPE = Ord_TYPE;
        binding.orderListBtnRegister.setOnClickListener(v -> {



            Date date = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            double sumPrice = 0;
            double sumDiscount = 0;
            double sumPurePrice = 0;

            for (InvoiceDetail invoicedetail : Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list()) {
                InvoiceDetail.deleteInTx(invoicedetail);
            }
            for (int i = 0; i <invDetails.size(); i++) {
                ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                int finalI = i;
                CollectionUtils.filter(prdResult, p->p.I.equals(invDetails.get(finalI).PRD_UID));
                if (prdResult.size()>0){
                    Double sumprice = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                    Double discountPrice = sumprice * prdResult.get(0).PERC_DIS;
                    Double totalPrice = sumprice - discountPrice;



                    sumPurePrice =sumPurePrice + (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                    sumPrice =sumPrice +totalPrice;
                    invDetails.get(i).ROW_NUMBER = i + 1;
                    sumDiscount = sumDiscount + prdResult.get(0).PERC_DIS;
                }

            }

            List<InvoiceDetail> invoiceList = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list();
            for (int i = 0; i < invoiceList.size(); i++) {
                InvoiceDetail.delete(invoiceList.get(i));
            }

            InvoiceDetail.saveInTx(invDetails);
            Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
            invoice.INV_UID = Inv_GUID;
            invoice.INV_TOTAL_AMOUNT = (double) sumPrice;//جمع فاکنور
            invoice.INV_TOTAL_DISCOUNT = 0.0;
            invoice.INV_PERCENT_DISCOUNT = 0.0;
            invoice.INV_DET_TOTAL_DISCOUNT = (double)sumDiscount;
            invoice.INV_DESCRIBTION = binding.edtDescription.getText().toString();
            invoice.INV_TOTAL_TAX = 0.0;
            invoice.INV_TOTAL_COST = 0.0;
            invoice.INV_EXTENDED_AMOUNT = (double)sumPurePrice;
            invoice.INV_DATE = date;
            invoice.INV_DUE_DATE = date;
            invoice.INV_STATUS = true;
            invoice.ACC_CLB_UID = Acc_GUID;
            invoice.TBL_UID = Tbl_GUID;
            invoice.INV_TYPE_ORDER =Integer.parseInt( finalOrd_TYPE);
            invoice.Acc_name = Acc_NAME;
            invoice.save();
            // LauncherFragment.factorGuid = GID;


            List<Invoice> listInvoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").list();
            List<InvoiceDetail> invoiceDetailList = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            SendOrder(listInvoice, invoiceDetailList);


        });


        clickItemPay(binding.layoutPaymentPlace, binding.ivOkPaymentPlace);


    }


    private static class JsonObject {
        public List<Invoice> Invoice;
        public List<ir.kitgroup.salein1.DataBase.InvoiceDetail> InvoiceDetail;
    }


    void SendOrder(List<Invoice> invoice, List<InvoiceDetail> invoiceDetail) {

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
                        tvMessage.setText("خطا در ارسال" + "\n" + description + "\n" + "این سفارش ذخیره می شود با مرجعه به پروفایل خود سفارش را مجددارسال کنید");
                        dialog.show();
                    } else if (message == 3) {
                        Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                        invoice.SendStatus = false;
                        invoice.save();
                        tvMessage.setText("خطا در ارسال" + "\n" + description + "\n" + "این سفارش ذخیره می شود با مرجعه به پروفایل خود سفارش را مجددارسال کنید");
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
