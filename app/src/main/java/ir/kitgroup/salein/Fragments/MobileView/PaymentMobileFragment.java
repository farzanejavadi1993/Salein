package ir.kitgroup.salein.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.Adapters.OrderTypePaymentAdapter;
import ir.kitgroup.salein.models.PaymentRecieptDetail;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Invoice;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.OrderType;
import ir.kitgroup.salein.DataBase.Product;
import ir.kitgroup.salein.DataBase.Tables;
import ir.kitgroup.salein.DataBase.User;


import ir.kitgroup.salein.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.models.ModelLog;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.databinding.FragmentPaymentMobileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMobileFragment extends Fragment {


    //region Parameter
    private SharedPreferences sharedPreferences;

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
    private RadioButton radioAddress1;
    private RadioButton radioAddress2;

    private MaterialButton btnEdit;
    private MaterialButton btnChoose;


    private int typeAddress = 0;
    private String address = "ناموجود";

    private int fontSize=0;

    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private Integer Ord_TYPE = -1;


    private List<InvoiceDetail> invDetails;


    public static Boolean setADR1 = false;



    private String typePayment="-1";
    //end region Parameter


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentMobileBinding.inflate(getLayoutInflater());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

        String ord_type = bundle.getString("Ord_TYPE");
        if (ord_type != null && !ord_type.equals(""))
            Ord_TYPE = Integer.parseInt(ord_type);
        String Sum_PURE_PRICE = bundle.getString("Sum_PURE_PRICE");
        String Sum_PRICE = bundle.getString("Sum_PRICE");
        boolean edit = bundle.getBoolean("EDIT");

        int fontBigSize=0;
        int fontSize=0;
        if (LauncherActivity.screenInches>=7){
            fontBigSize=14;
            fontSize=13;
        }else {
            fontBigSize=12;
            fontSize=11;
        }


        binding.tvTitle.setTextSize(fontSize);
        binding.tvAddAddress.setTextSize(fontBigSize);
        binding.edtAddress.setTextSize(fontBigSize);
        binding.tvTAddress.setTextSize(fontSize);


        binding.tvPayment.setTextSize(fontBigSize);
        binding.tvTitlePaymentPlace.setTextSize(fontSize);
        binding.tvDescriptionPaymentPlace.setTextSize(fontSize);
        binding.tvTypePaymentPlace.setTextSize(fontSize);


        binding.tvTitlePaymentOnline.setTextSize(fontSize);
        binding.tvDescriptionPaymentOnline.setTextSize(fontSize);
        binding.tvTypeOrder.setTextSize(fontBigSize);


        binding.btnRegisterOrder.setTextSize(fontSize);

        binding.sumPriceTxt.setTextSize(fontSize);
        binding.orderListSumPriceTv.setTextSize(fontSize);
        binding.purePriceTxt.setTextSize(fontSize);
        binding.orderListPurePriceTv.setTextSize(fontSize);



        Invoice inv1 = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
        if (edit && inv1 != null && inv1.INV_DESCRIBTION != null) {
            binding.edtDescription.setText(inv1.INV_DESCRIBTION);
          /*  if (inv1.ACC_CLB_DEFAULT_ADDRESS!=null && inv1.ACC_CLB_DEFAULT_ADDRESS.equals("1"))
                binding.tvTAddress.setText(Select.from(Account.class).first().ADR);
            else
                binding.tvTAddress.setText(Select.from(Account.class).first().ADR1);*/
        }


        if (edit) {
            binding.tvError.setVisibility(View.VISIBLE);

        }



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


                    tables.C = Ord_TYPE;


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

        radioAddress1 = dialogAddress.findViewById(R.id.radioAddress1);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        btnEdit = dialogAddress.findViewById(R.id.btn_edit);
        btnChoose = dialogAddress.findViewById(R.id.btn_choose);



        radioAddress1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                typeAddress = 1;
                address = radioAddress1.getText().toString();
            }
        });

        radioAddress2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                typeAddress = 2;
                address = radioAddress2.getText().toString();
            }
        });


        btnChoose.setOnClickListener(v -> {

            if (typeAddress == 0) {
                Toast.makeText(getActivity(), "آدرس را انتخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            }
            dialogAddress.dismiss();
            binding.tvTAddress.setText(address);
        });

        btnEdit.setOnClickListener(v -> {

            if (typeAddress == 0) {
                Toast.makeText(getActivity(), "آدرس مورد نظر خود را وارد کنید.", Toast.LENGTH_SHORT).show();
                return;
            }
            dialogAddress.dismiss();



            Bundle bundle1 = new Bundle();
            bundle1.putString("edit_address", "1");
            bundle1.putString("type", String.valueOf(typeAddress));
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment).addToBackStack("MapF").commit();

        });

        if (acc != null && acc.ADR != null && !acc.ADR.equals(""))
            radioAddress1.setText(acc.ADR);


        else
            radioAddress1.setText("ناموجود");


        if (acc != null && acc.ADR1 != null && !acc.ADR1.equals(""))
            radioAddress2.setText(acc.ADR1);
        else
            radioAddress2.setText("ناموجود");


        //endregion Cast DialogAddress


        binding.layoutPaymentPlace.setOnClickListener(v -> {
            binding.ivOkPaymentOnline.setVisibility(View.GONE);
            View view1 = getLayoutInflater().inflate(R.layout.popup_place_payment, null);
            PopupWindow popupWindow = new PopupWindow(view1, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.setElevation(10.0F);
            }

            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(v);


            ConstraintLayout btnPos = view1.findViewById(R.id.layout_pos);
            ConstraintLayout btnMoney = view1.findViewById(R.id.layout_money);


            btnPos.setOnClickListener(v1 -> {
                typePayment="2";
                binding.tvTypePaymentPlace.setText("پرداخت با کارت");
                popupWindow.dismiss();
            });


            btnMoney.setOnClickListener(v12 -> {

                typePayment="1";
                binding.tvTypePaymentPlace.setText("پرداخت نقدی");
                popupWindow.dismiss();
            });

        });


        binding.layoutPaymentOnline.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
            typePayment="-1";
         /*   binding.tvTypePaymentPlace.setText("");
            binding.ivOkPaymentOnline.setVisibility(View.VISIBLE);
            typePayment="4";*/
        });


        if (acc != null && acc.ADR != null && !acc.ADR.equals("") && !setADR1) {
            binding.tvTAddress.setText(acc.ADR);
            typeAddress = 1;
            address = acc.ADR;
        }
            else if (acc != null && acc.ADR1 != null && !acc.ADR1.equals("")) {
            binding.tvTAddress.setText(acc.ADR1);
            typeAddress = 2;
            address = acc.ADR1;
        } else {
            binding.tvTAddress.setText("ناموجود");
            typeAddress = 0;
            address = "ناموجود";
        }



        binding.layoutAddress.setOnClickListener(v -> {


            if (acc != null && acc.ADR != null && !acc.ADR.equals("") && acc.ADR1 != null && !acc.ADR1.equals("")) {
                dialogAddress.show();
                return;
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("edit_address", "1");
            bundle1.putString("type", String.valueOf(typeAddress));
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment).addToBackStack("MapF").commit();
        });


        binding.sumPriceTxt.setText(format.format(Double.parseDouble(Sum_PRICE)) + "ریال");
        binding.purePriceTxt.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");

        if (App.mode == 1) {
            binding.rlTypeOrder.setVisibility(View.GONE);
            binding.layoutAddress.setVisibility(View.GONE);
        }


        binding.ivBack.setOnClickListener(v -> getFragmentManager().popBackStack());



        List<OrderType> OrdT = Select.from(OrderType.class).where("TY =" + 2).list();


        if (OrdT.size() > 1) {
            OrderTypePaymentAdapter orderTypePaymentAdapter = new OrderTypePaymentAdapter(getActivity(), OrdT);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            manager.setReverseLayout(true);
            binding.recyclerViewOrderType.setLayoutManager(manager);
            binding.recyclerViewOrderType.setScrollingTouchSlop(View.FOCUS_LEFT);
            binding.recyclerViewOrderType.setAdapter(orderTypePaymentAdapter);


            if (inv1!=null && inv1.INV_TYPE_ORDER!=null) {
                ArrayList<OrderType> list2 = new ArrayList<>(OrdT);
                CollectionUtils.filter(list2, r -> r.getC().equals(inv1.INV_TYPE_ORDER));
                if (list2.size() > 0) {
                    OrdT.get(OrdT.indexOf(list2.get(0))).Click = true;
                    Ord_TYPE =inv1.INV_TYPE_ORDER;
                }
                orderTypePaymentAdapter.notifyDataSetChanged();

            }
            orderTypePaymentAdapter.setOnClickListener((GUID, code) -> {
                Ord_TYPE = code;

                //region UnClick Old Item
                ArrayList<OrderType> list = new ArrayList<>(OrdT);
                CollectionUtils.filter(list, r -> r.Click);
                if (list.size() > 0) {
                    OrdT.get(OrdT.indexOf(list.get(0))).Click = false;
                }
                //endregion UnClick Old Item


                //region Click New Item
                ArrayList<OrderType> list2 = new ArrayList<>(OrdT);
                CollectionUtils.filter(list2, r -> r.getI().equals(GUID));
                if (list2.size() > 0) {
                    OrdT.get(OrdT.indexOf(list2.get(0))).Click = true;
                }
                //endregion Click New Item


                orderTypePaymentAdapter.notifyDataSetChanged();
            });
        }
        else if (OrdT.size() == 1) {
            Ord_TYPE = OrdT.get(0).getC();
            binding.rlTypeOrder.setVisibility(View.GONE);

        }





        binding.btnRegisterOrder.setOnClickListener(v -> {

            if ((address.equals("ناموجود") || typeAddress == 0 ) && App.mode==2) {
                Toast.makeText(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (Ord_TYPE == null || Ord_TYPE == -1) {
                Toast.makeText(getActivity(), "نوع سفارش را انخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (typePayment.equals("-1")){
                tvMessage.setText("نوع پرداخت مشخص نشده است. سفارش به صورت موقت ارسال میشود . از ارسال سفارش اطمینان دارید ؟");
            }else {
                tvMessage.setText(" از ارسال سفارش اطمینان دارید ؟");
            }
            rlButtons.setVisibility(View.VISIBLE);
            btnReturned.setVisibility(View.GONE);

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


            for (int i = 0; i < invDetails.size(); i++) {
                ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                int finalI = i;
                CollectionUtils.filter(prdResult, p -> p.I.equals(invDetails.get(finalI).PRD_UID));
                if (prdResult.size() > 0) {
                    double sumTotalPrice = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());//جمع کل ردیف
                    double discountPrice = sumTotalPrice * (prdResult.get(0).PERC_DIS / 100);//جمع تخفیف ردیف
                    double totalPurePrice = sumTotalPrice - discountPrice;//جمع خالص ردیف


                    sumPurePrice = sumPurePrice + sumTotalPrice;//جمع کل فاکتور
                    sumPrice = sumPrice + totalPurePrice;//جمع خالص فاکتور
                    invDetails.get(i).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPurePrice);
                    invDetails.get(i).ROW_NUMBER = i + 1;
                    invDetails.get(i).INV_DET_PERCENT_DISCOUNT = prdResult.get(0).PERC_DIS;
                    invDetails.get(i).INV_DET_DISCOUNT =String.valueOf(discountPrice);
                    invDetails.get(i).INV_DET_PRICE_PER_UNIT = String.valueOf(prdResult.get(0).getPRDPRICEPERUNIT1());
                    sumDiscount = sumDiscount + discountPrice;
                    sumDiscountPercent = sumDiscountPercent + (prdResult.get(0).PERC_DIS / 100);


                }

            }

          /*  List<InvoiceDetail> invoiceList = Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list();
            for (int i = 0; i < invoiceList.size(); i++) {
                InvoiceDetail.delete(invoiceList.get(i));
            }*/

            for (InvoiceDetail invoicedetail : Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list()) {
                InvoiceDetail.deleteInTx(invoicedetail);
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
            invoice.INV_TYPE_ORDER = Ord_TYPE;


            if (typeAddress == 1) {
                invoice.ACC_CLB_ADDRESS = "1";
                invoice.ACC_CLB_DEFAULT_ADDRESS = "1";
            } else if (typeAddress == 2) {
                invoice.ACC_CLB_ADDRESS2 = "2";
                invoice.ACC_CLB_DEFAULT_ADDRESS = "2";
            }else {
                invoice.ACC_CLB_ADDRESS = "1";
                invoice.ACC_CLB_DEFAULT_ADDRESS = "1";
            }

            invoice.Acc_name = Acc_NAME;
            invoice.save();
            // LauncherFragment.factorGuid = GID;


            List<Invoice> listInvoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").list();
            // List<InvoiceDetail> invoiceDetailList = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            List<InvoiceDetail> invoiceDetailList =invDetails;


            List<PaymentRecieptDetail> clsPaymentRecieptDetails=new ArrayList<>();
            if (!typePayment.equals("-1")){
                PaymentRecieptDetail cl=new PaymentRecieptDetail();
                cl.PAY_RCIPT_DET_DESCRIBTION=listInvoice.get(0).INV_DESCRIBTION;
                cl.PAY_RCIPT_DET_TOTAL_AMOUNT=listInvoice.get(0).INV_EXTENDED_AMOUNT;
                cl.PAY_RCIPT_DET_TYPE=typePayment;
                clsPaymentRecieptDetails.add(cl);
            }



            SendOrder(listInvoice, invoiceDetailList,clsPaymentRecieptDetails);


        });





        /*        clickItemPay(binding.layoutPaymentPlace, binding.ivOkPaymentPlace);*/


    }


    private static class JsonObject {
        public List<Invoice> Invoice;
        public List<InvoiceDetail> InvoiceDetail;
        public List<PaymentRecieptDetail> PaymentRecieptDetail;
    }


    void SendOrder(List<Invoice> invoice, List<InvoiceDetail> invoiceDetail,List<PaymentRecieptDetail> clsPaymentRecieptDetail) {

        try {

            customProgress.showProgress(getActivity(), "در حال ارسال سفارش", false);
            JsonObject jsonObject = new JsonObject();
            jsonObject.Invoice = invoice;
            jsonObject.InvoiceDetail = invoiceDetail;
            jsonObject.PaymentRecieptDetail = clsPaymentRecieptDetail;


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
                    int message = 0;
                    String description = "";
                    if (iDs != null) {
                        message = iDs.getLogs().get(0).getMessage();
                        description = iDs.getLogs().get(0).getDescription();
                    }


                    rlButtons.setVisibility(View.GONE);
                    btnReturned.setVisibility(View.VISIBLE);
                    if (message == 1) {
                        tvMessage.setText("سفارش با موفقیت ارسال شد");
                        dialog.show();
                    }

                    else {
                        Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                        if (!invoice.INV_SYNC.equals("-")) {
                            invoice.INV_SYNC = "#";
                            invoice.save();
                        }
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




    @Override
    public void onDestroy() {
        super.onDestroy();

        setADR1=false;
    }
}
