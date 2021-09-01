package ir.kitgroup.order.Fragments.MobileView;

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

import java.util.UUID;

import ir.kitgroup.order.Fragments.Client.Register.RegisterFragment;
import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.classes.CustomProgress;
import ir.kitgroup.order.DataBase.Account;
import ir.kitgroup.order.DataBase.Invoice;
import ir.kitgroup.order.DataBase.InvoiceDetail;
import ir.kitgroup.order.DataBase.OrderType;
import ir.kitgroup.order.DataBase.Product;
import ir.kitgroup.order.DataBase.Tables;
import ir.kitgroup.order.DataBase.User;


import ir.kitgroup.order.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.order.Util.Util;
import ir.kitgroup.order.models.ModelLog;
import ir.kitgroup.order.R;

import ir.kitgroup.order.databinding.FragmentPaymentMobileBinding;
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
    private RelativeLayout rlButtons;
    private MaterialButton btnReturned;
    private MaterialButton btnOk;
    private MaterialButton btnNo;


    private Dialog dialogAddress;
    private MaterialCardView lAdr1;
    private MaterialCardView lAdr2;
    private MaterialButton btnEdit;
    private MaterialButton btnChoose;

    private TextView txtAddress1;
    private TextView txtAddress2;
    private int typeAddress = 0;
    private String address = "ناموجود";


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
        //Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);
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
        boolean edit = bundle.getBoolean("EDIT");

        Invoice inv1 = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
        if (edit && inv1 != null && inv1.INV_DESCRIBTION != null)
            binding.edtDescription.setText(inv1.INV_DESCRIBTION);


        if (edit)
            binding.tvError.setVisibility(View.VISIBLE);


        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();


        Account acc = Select.from(Account.class).first();

        //region Cast DialogSendOrder
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        tvMessage = dialog.findViewById(R.id.tv_message);
        rlButtons = dialog.findViewById(R.id.layoutButtons);
        btnReturned = dialog.findViewById(R.id.btn_returned);
        btnOk = dialog.findViewById(R.id.btn_ok);
        btnNo = dialog.findViewById(R.id.btn_cancel);


        String finalOrd_TYPE1 = Ord_TYPE;
        btnReturned.setOnClickListener(v -> {


            dialog.dismiss();
            if (App.mode == 2) {

                if (edit) {
                    for (int i = 0; i < 5; i++) {
                        getFragmentManager().popBackStack();
                    }
                } else {
                    for (int i = 0; i < 2; i++) {
                        getFragmentManager().popBackStack();
                    }
                }


                assert getFragmentManager() != null;

                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                if (frg != null) {
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }

            } else {
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

        //region Cast DialogAddress
        dialogAddress = new Dialog(getActivity());
        dialogAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddress.setContentView(R.layout.dialog_address);
        dialogAddress.setCancelable(false);

        lAdr1 = dialogAddress.findViewById(R.id.layout_address1);
        lAdr2 = dialogAddress.findViewById(R.id.layout_address2);
        btnEdit = dialogAddress.findViewById(R.id.btn_edit);
        btnChoose = dialogAddress.findViewById(R.id.btn_choose);

        txtAddress1 = dialogAddress.findViewById(R.id.tvTAddress1);
        txtAddress2 = dialogAddress.findViewById(R.id.tvTAddress2);


        lAdr1.setOnClickListener(v -> {
            lAdr2.setStrokeColor(getResources().getColor(R.color.very_light_pink));
            lAdr1.setStrokeColor(getResources().getColor(R.color.purple_700));
            typeAddress = 1;
            address=txtAddress1.getText().toString();

        });


        lAdr2.setOnClickListener(v -> {
            lAdr1.setStrokeColor(getResources().getColor(R.color.very_light_pink));
            lAdr2.setStrokeColor(getResources().getColor(R.color.purple_700));
            typeAddress = 2;
            address=txtAddress2.getText().toString();
        });


        btnChoose.setOnClickListener(v -> {

            if (typeAddress==0){
                Toast.makeText(getActivity(), "آدرس را انتخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            }
            dialogAddress.dismiss();
            binding.tvTAddress.setText(address);
        });

        btnEdit.setOnClickListener(v -> {

                    dialogAddress.dismiss();
                    typeAddress=0;
                    address="ناموجود";
                    Bundle bundle1=new Bundle();
                    bundle1.putString("edit","1");
                    ProfileFragment profileFragment=new ProfileFragment();
                    profileFragment.setArguments(bundle1);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, profileFragment,"ProfileFragment").addToBackStack("ProfileF").commit();

                });

        if (acc!=null && acc.ADR!=null && !acc.ADR.equals(""))
        txtAddress1.setText(acc.ADR);
        else
            txtAddress1.setText("ناموجود");


        if (acc!=null && acc.ADR1!=null && !acc.ADR1.equals(""))
            txtAddress2.setText(acc.ADR1);
        else
            txtAddress2.setText("ناموجود");



        //endregion Cast DialogAddress



        if (acc != null && acc.ADR != null && !acc.ADR.equals("") &&  acc.ADR1 != null && !acc.ADR1.equals("")){
            dialogAddress.show();
        }
        else if  (acc != null && acc.ADR != null && !acc.ADR.equals("")) {
            binding.tvTAddress.setText(acc.ADR);
            typeAddress=1;
            address=acc.ADR;
        }
        else if  (acc != null && acc.ADR1 != null && !acc.ADR1.equals("")) {
            binding.tvTAddress.setText(acc.ADR1);
            typeAddress=2;
            address=acc.ADR1;
        }
        else {
            binding.tvTAddress.setText("ناموجود");
            typeAddress=0;
            address="ناموجود";
        }


        binding.tvAddAddress.setOnClickListener(v -> {
            typeAddress=0;
            address="ناموجود";

            if (acc != null && acc.ADR != null && !acc.ADR.equals("") &&  acc.ADR1 != null && !acc.ADR1.equals("")){
                dialogAddress.show();
                return;
            }
            Bundle bundle1=new Bundle();
            bundle1.putString("edit_address","1");
            bundle1.putString("mobile","");
            RegisterFragment registerFragment=new RegisterFragment();
            registerFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, registerFragment).addToBackStack("RegisterF").commit();
        });



        binding.sumPriceTxt.setText(format.format(Double.parseDouble(Sum_PRICE)) + "ریال");
        binding.purePriceTxt.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");

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

            if (address.equals("ناموجود") || typeAddress==0){
                Toast.makeText(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            }
            rlButtons.setVisibility(View.VISIBLE);
            btnReturned.setVisibility(View.GONE);
            tvMessage.setText(" از ارسال سفارش اطمینان دارید ؟");
            dialog.show();

        });


        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            Date date = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            double sumPrice = 0;
            double sumDiscount = 0;
            double sumDiscountPercent = 0;
            double sumPurePrice = 0;

            for (InvoiceDetail invoicedetail : Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list()) {
                InvoiceDetail.deleteInTx(invoicedetail);
            }
            for (int i = 0; i < invDetails.size(); i++) {
                ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                int finalI = i;
                CollectionUtils.filter(prdResult, p -> p.I.equals(invDetails.get(finalI).PRD_UID));
                if (prdResult.size() > 0) {
                    double sumpurePrice = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());//جمع کل ردیف
                    double discountPrice = sumpurePrice * (prdResult.get(0).PERC_DIS / 100);//جمع تخفیف ردیف
                    double totalPrice = sumpurePrice - discountPrice;//جمع خالص ردیف


                    sumPurePrice = sumPurePrice + sumpurePrice;//جمع کل فاکتور
                    sumPrice = sumPrice + totalPrice;//جمع خالص فاکتور
                    invDetails.get(i).INV_DET_TOTAL_AMOUNT = String.valueOf(sumpurePrice);
                    invDetails.get(i).ROW_NUMBER = i + 1;
                    invDetails.get(i).INV_DET_PRICE_PER_UNIT = String.valueOf(prdResult.get(0).getPRDPRICEPERUNIT1());
                    sumDiscount = sumDiscount + discountPrice;
                    sumDiscountPercent = sumDiscountPercent + (prdResult.get(0).PERC_DIS / 100);


                }

            }

            List<InvoiceDetail> invoiceList = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list();
            for (int i = 0; i < invoiceList.size(); i++) {
                InvoiceDetail.delete(invoiceList.get(i));
            }

            InvoiceDetail.saveInTx(invDetails);
            Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
            invoice.INV_UID = Inv_GUID;
            invoice.INV_TOTAL_AMOUNT = sumPurePrice;//جمع فاکنور
            invoice.INV_TOTAL_DISCOUNT = 0.0;
            invoice.INV_PERCENT_DISCOUNT = sumDiscountPercent;
            invoice.INV_DET_TOTAL_DISCOUNT = sumDiscount;
            invoice.INV_DESCRIBTION = binding.edtDescription.getText().toString();
            invoice.INV_TOTAL_TAX = 0.0;
            invoice.INV_TOTAL_COST = 0.0;
            invoice.INV_EXTENDED_AMOUNT = sumPrice;
            invoice.INV_DATE = date;
            invoice.INV_DUE_DATE = date;
            invoice.INV_STATUS = true;
            invoice.ACC_CLB_UID = Acc_GUID;
            invoice.TBL_UID = Tbl_GUID;
            if (!finalOrd_TYPE.equals(""))
                invoice.INV_TYPE_ORDER = Integer.parseInt(finalOrd_TYPE);


            if (typeAddress==1) {
                invoice.ACC_CLB_ADDRESS = "1";
                invoice.ACC_CLB_DEFAULT_ADDRESS="1";
            }
            else if (typeAddress==2) {
                invoice.ACC_CLB_ADDRESS2 = "2";
                invoice.ACC_CLB_DEFAULT_ADDRESS="2";
            }

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
        public List<ir.kitgroup.order.DataBase.InvoiceDetail> InvoiceDetail;
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
                    rlButtons.setVisibility(View.GONE);
                    btnReturned.setVisibility(View.VISIBLE);
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
