package ir.kitgroup.salein.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.geometry.LatLng;
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

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class PaymentMobileFragment extends Fragment {


    //region Parameter
    private SharedPreferences sharedPreferences;


    private FragmentPaymentMobileBinding binding;

    private CustomProgress customProgress;

    private String userName;
    private String passWord;
    private double lat = 0.0;
    private double lng = 0.0;
    private String numberPos = "";
    private Dialog dialog;
    private TextView tvMessage;
    private ImageView ivIcon;
    private String Inv_GUID;
    private RelativeLayout rlButtons;
    private MaterialButton btnReturned;
    private MaterialButton btnOk;
    private MaterialButton btnNo;


    private Dialog dialogAddress;
    private RadioButton radioAddress1;
    private RadioButton radioAddress2;

    private MaterialButton btnNewAddress;


    private int typeAddress = 0;
    private String ValidAddress = "ناموجود";


    private int fontSize = 0;
    private int imageIconDialog;

    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private Integer Ord_TYPE = -1;
    private String Sum_PURE_PRICE  =  "0";


    private List<InvoiceDetail> invDetails;


    public static Boolean setADR1 = false;
    public static Boolean onceSee = false;


    private String typePayment = "-1";
    private String Tbl_GUID;
    private double credit = 0.0;

    private double sumTransport = 0.0;
    private String link = "";




    private Double latitude1 =0.0;
    private Double longitude1 =0.0;

    private Double latitude2=0.0;
    private Double longitude2=0.0;
    //end region Parameter


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentMobileBinding.inflate(getLayoutInflater());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);


        try {
          //  PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            switch (LauncherActivity.name) {
                case "ir.kitgroup.salein":

                    imageIconDialog = R.drawable.saleinicon128;
                    link = "http://185.201.49.204:4008/";

                    break;

                case "ir.kitgroup.saleintop":

                    imageIconDialog = R.drawable.top_png;

                    break;


                case "ir.kitgroup.saleinmeat":

                    imageIconDialog = R.drawable.meat_png;

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = getArguments();
        Inv_GUID = bundle.getString("Inv_GUID");
        Tbl_GUID = bundle.getString("Tbl_GUID");
        String Acc_NAME = bundle.getString("");
        String Acc_GUID = bundle.getString("Acc_GUID");

        String ord_type = bundle.getString("Ord_TYPE");
        if (ord_type != null && !ord_type.equals(""))
            Ord_TYPE = Integer.parseInt(ord_type);
         Sum_PURE_PRICE = bundle.getString("Sum_PURE_PRICE");
        boolean edit = bundle.getBoolean("EDIT");

        int fontBigSize = 0;
        int fontSize = 0;
        if (LauncherActivity.screenInches >= 7) {
            fontBigSize = 14;
            fontSize = 13;
        } else {
            fontBigSize = 12;
            fontSize = 11;
        }



        Account acc = Select.from(Account.class).first();

        //region Cast DialogAddress
        dialogAddress = new Dialog(getActivity());
        dialogAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddress.setContentView(R.layout.dialog_address);
        dialogAddress.setCancelable(true);

        radioAddress1 = dialogAddress.findViewById(R.id.radioAddress1);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        btnNewAddress = dialogAddress.findViewById(R.id.btn_edit);


        radioAddress1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    latitude1=Double.parseDouble(acc.ADR.split("latitude")[1]);
                    longitude1=Double.parseDouble(acc.ADR.split("longitude")[0]);
                }catch (Exception e){

                }
                if (latitude1==0.0 && longitude1==0.0) {
                    Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();
                    return;
                }
                double distance = getDistanceMeters(new LatLng(latitude1, longitude1), new LatLng(lat, lng));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price == -1.0) {
                    Toast.makeText(getActivity(), "سفارش خارج از محدوده است.", Toast.LENGTH_SHORT).show();
                    dialogAddress.dismiss();

                    return;
                } else {
                    sumTransport = price;
                    binding.tvTransport.setText(format.format(price) + "ریال");
                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");

                }
                typeAddress = 1;
                ValidAddress = radioAddress1.getText().toString();
                binding.tvTAddress.setText(ValidAddress);
                dialogAddress.dismiss();
            }
        });

        radioAddress2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                try {
                    latitude2=Double.parseDouble(acc.ADR1.split("latitude")[1]);
                    longitude2=Double.parseDouble(acc.ADR1.split("longitude")[0]);
                }catch (Exception e){

                }
                if (latitude2 == 0.0 || longitude2 == 0.0) {
                    Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();
                    return;
                }
                double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(lat, lng));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price == -1.0) {
                    Toast.makeText(getActivity(), "سفارش خارج از محدوده است.", Toast.LENGTH_SHORT).show();

                    return;
                } else {
                    sumTransport = price;
                    binding.tvTransport.setText(format.format(price) + "ریال");
                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");

                }
                typeAddress = 2;
                ValidAddress = radioAddress2.getText().toString();
                binding.tvTAddress.setText(ValidAddress);
                dialogAddress.dismiss();
            }
        });

        btnNewAddress.setOnClickListener(v -> {

            dialogAddress.dismiss();
            Bundle bundle1 = new Bundle();
            bundle1.putString("edit_address", "1");
            bundle1.putString("type", String.valueOf(typeAddress));
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment).addToBackStack("MapF").commit();

        });





        //endregion Cast DialogAddress

        customProgress = CustomProgress.getInstance();
        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;
        lat = Select.from(User.class).first().lat;
        lng = Select.from(User.class).first().lng;
        numberPos = Select.from(User.class).first().numberPos;
        if (numberPos != null && numberPos.equals(""))
            numberPos = "0";


        binding.tvTitle.setTextSize(fontSize);
        binding.tvAddAddress.setTextSize(fontBigSize);
        binding.edtAddress.setTextSize(fontBigSize);
        binding.tvTAddress.setTextSize(fontSize);


        binding.tvPayment.setTextSize(fontBigSize);
        binding.tvTitlePaymentPlace.setTextSize(fontSize);
        binding.tvDescriptionPaymentPlace.setTextSize(fontSize);
        binding.tvTypePaymentPlace.setTextSize(fontSize);


        binding.tvTitlePaymentOnline.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.tvTypeOrder.setTextSize(fontBigSize);

        binding.btnRegisterOrder.setTextSize(fontSize);

        binding.tvSumPurePrice.setTextSize(fontBigSize);
        binding.orderListPurePriceTv.setTextSize(fontBigSize);
        binding.orderListTransportTv.setTextSize(fontBigSize);
        binding.tvTransport.setTextSize(fontBigSize);
        binding.tvTransport.setText(format.format(sumTransport) + " ریال ");


        Invoice inv1 = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
        if (edit && inv1 != null && inv1.INV_DESCRIBTION != null) {
            binding.edtDescription.setText(inv1.INV_DESCRIBTION);
        }


        if (edit) {
            binding.tvError.setVisibility(View.VISIBLE);
        }


        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
        for (int i = 0; i < invDetails.size(); i++) {
            if (invDetails.get(i).INV_DET_DESCRIBTION != null && invDetails.get(i).INV_DET_DESCRIBTION.equals("توزیع")) {

                invDetails.remove(invDetails.get(i));

            }
        }

        //region Cast DialogSendOrder
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        tvMessage = dialog.findViewById(R.id.tv_message);
        ivIcon = dialog.findViewById(R.id.iv_icon);

        ivIcon.setImageResource(imageIconDialog);
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
                    LauncherActivity mainActivity = (LauncherActivity) view.getContext();
                    Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
                    if (fragment instanceof MainOrderMobileFragment) {
                        MainOrderMobileFragment fgf = (MainOrderMobileFragment) fragment;
                        fgf.setHomeBottomBarAndClearBadge();
                    }
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }

            } else {
                Account.deleteAll(Account.class);
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




        if (acc != null && acc.ADR != null && !acc.ADR.equals("")){
            String address="";
            try {
               address= acc.ADR.replace(acc.ADR.split("latitude")[1],"").replace("latitude","").replace(acc.ADR.split("longitude")[0],"").replace("longitude","");
            }catch (Exception e){
                address=acc.ADR;
            }

            radioAddress1.setText(address);

        }

        else
            radioAddress1.setText("ناموجود");


        if (acc != null && acc.ADR1 != null && !acc.ADR1.equals("")){
            String address="";
            try {
                address= acc.ADR1.replace(acc.ADR1.split("latitude")[1],"").replace("latitude","").replace(acc.ADR1.split("longitude")[0],"").replace("longitude","");
            }catch (Exception e){
                address=acc.ADR1;
            }

            radioAddress2.setText(address);
        }

        else
            radioAddress2.setText("ناموجود");






        if ( acc != null && acc.ADR != null && !acc.ADR.equals("") && !setADR1 ) {
            if (onceSee) {
                try {
                    latitude1=Double.parseDouble(acc.ADR.split("latitude")[1]);
                    longitude1=Double.parseDouble(acc.ADR.split("longitude")[0]);
                }catch (Exception e){

                }

                if (latitude1==0.0 && longitude1==0.0)
                {
                    Toast.makeText(getActivity(), "دریافت طول و عرض جغرافیای با خطا مواجه شد", Toast.LENGTH_SHORT).show();
                }

                double distance = getDistanceMeters(new LatLng(latitude1,longitude1 ), new LatLng(lat, lng));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price == -1.0) {
                    binding.tvError.setText("سفارش خارج از محدوده است.");
                    binding.tvError.setVisibility(View.VISIBLE);
                } else {
                    binding.tvError.setText("");
                    binding.tvError.setVisibility(View.GONE);
                    sumTransport = price;
                    binding.tvTransport.setText(format.format(price) + "ریال");
                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
                }
                String address="";
                try {
                    address= acc.ADR.replace(acc.ADR.split("latitude")[1],"").replace("latitude","").replace(acc.ADR.split("longitude")[0],"").replace("longitude","");
                }catch (Exception e){
                    address=acc.ADR;
                }

                binding.tvTAddress.setText(address);
                typeAddress = 1;
                ValidAddress = acc.ADR;

            }
        }


        else if (acc != null && acc.ADR1 != null && !acc.ADR1.equals("")) {
            if (onceSee) {


                try {
                    latitude2=Double.parseDouble(acc.ADR1.split("latitude")[1]);
                    longitude2=Double.parseDouble(acc.ADR1.split("longitude")[0]);
                }catch (Exception e){

                }

                if (latitude2==0.0 && longitude2==0.0)
                {
                    Toast.makeText(getActivity(), "دریافت طول و عرض جغرافیای با خطا مواجه شد", Toast.LENGTH_SHORT).show();
                }
                double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(lat, lng));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price == -1.0) {
                    binding.tvError.setText("سفارش خارج از محدوده است.");
                    binding.tvError.setVisibility(View.VISIBLE);
                } else {
                    binding.tvError.setText("");
                    binding.tvError.setVisibility(View.GONE);
                    sumTransport = price;
                    binding.tvTransport.setText(format.format(price) + "ریال");
                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
                }

                String address="";
                try {
                    address= acc.ADR1.replace(acc.ADR1.split("latitude")[1],"").replace("latitude","").replace(acc.ADR1.split("longitude")[0],"").replace("longitude","");
                }catch (Exception e){
                    address=acc.ADR1;
                }
                binding.tvTAddress.setText(address);
                typeAddress = 2;
                ValidAddress = acc.ADR1;
            }

        } else {
            if (onceSee)
                binding.tvTAddress.setText("ناموجود");
            typeAddress = 0;
            ValidAddress = "ناموجود";
        }




        if (acc != null && acc.CRDT != null)
            binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");



        /*if (acc!=null && acc.M!=null)
        getInquiryAccount(userName,passWord,acc.M);*/






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


        binding.layoutPaymentPlace.setOnClickListener(v -> {

            binding.tvSuccessFullPayOnline.setText("");

            if (acc != null && acc.CRDT != null && acc.CRDT >= (Double.parseDouble(Sum_PURE_PRICE) + sumTransport)) {
                binding.tvCredit.setText(format.format(acc.CRDT));
            }

            binding.ivOkPaymentOnline.setVisibility(View.GONE);
            binding.ivOkPaymentPlace.setVisibility(View.VISIBLE);

//            View view1 = getLayoutInflater().inflate(R.layout.popup_place_payment, null);
//            PopupWindow popupWindow = new PopupWindow(view1, LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                popupWindow.setElevation(10.0F);
//            }
//
//            popupWindow.setOutsideTouchable(true);
//            popupWindow.showAsDropDown(v);
//
//
//            ConstraintLayout btnPos = view1.findViewById(R.id.layout_pos);
//            ConstraintLayout btnMoney = view1.findViewById(R.id.layout_money);
//
//
//            btnPos.setOnClickListener(v1 -> {
//                typePayment = "2";
//                binding.tvTypePaymentPlace.setText("پرداخت با کارت");
//                popupWindow.dismiss();
//            });
//
//
//            btnMoney.setOnClickListener(v12 -> {
//
//                typePayment = "1";
//                binding.tvTypePaymentPlace.setText("پرداخت نقدی");
//                popupWindow.dismiss();
//            });

        });


        binding.layoutPaymentOnline.setOnClickListener(v -> {
            binding.ivOkPaymentPlace.setVisibility(View.GONE);
            if (acc != null && acc.CRDT != null && acc.CRDT >= (Double.parseDouble(Sum_PURE_PRICE) + sumTransport)) {
                binding.tvSuccessFullPayOnline.setText("پرداخت موفقیت آمیز");
                binding.tvCredit.setText(String.valueOf(acc.CRDT - (Double.parseDouble(Sum_PURE_PRICE) + sumTransport)));
                typePayment = "4";
                binding.ivOkPaymentOnline.setVisibility(View.VISIBLE);
            } else if (!link.equals("")) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                startActivityForResult(intent,44);





            } else {
                Toast.makeText(getActivity(), "انتقال به باشگاه مشتریان ، در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
                typePayment = "-1";
            }


         /*   binding.tvTypePaymentPlace.setText("");
            binding.ivOkPaymentOnline.setVisibility(View.VISIBLE);
            typePayment="4";*/
        });


        binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");

        if (App.mode == 1) {
            binding.rlTypeOrder.setVisibility(View.GONE);
            binding.layoutAddress.setVisibility(View.GONE);
        }


        List<OrderType> OrdT = Select.from(OrderType.class).where("TY =" + 2).list();
        if (OrdT.size() > 1) {
            OrderTypePaymentAdapter orderTypePaymentAdapter = new OrderTypePaymentAdapter(getActivity(), OrdT);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            manager.setReverseLayout(true);
            binding.recyclerViewOrderType.setLayoutManager(manager);
            binding.recyclerViewOrderType.setScrollingTouchSlop(View.FOCUS_LEFT);
            binding.recyclerViewOrderType.setAdapter(orderTypePaymentAdapter);


            if (inv1 != null && inv1.INV_TYPE_ORDER != null) {
                ArrayList<OrderType> list2 = new ArrayList<>(OrdT);
                CollectionUtils.filter(list2, r -> r.getC().equals(inv1.INV_TYPE_ORDER));
                if (list2.size() > 0) {
                    OrdT.get(OrdT.indexOf(list2.get(0))).Click = true;
                    Ord_TYPE = inv1.INV_TYPE_ORDER;
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
        } else if (OrdT.size() == 1) {
            Ord_TYPE = OrdT.get(0).getC();
            binding.rlTypeOrder.setVisibility(View.GONE);

        }


        binding.btnRegisterOrder.setOnClickListener(v -> {

            if ((ValidAddress.equals("ناموجود") || typeAddress == 0) && App.mode == 2) {
                Toast.makeText(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            } else if (Ord_TYPE == null || Ord_TYPE == -1) {
                Toast.makeText(getActivity(), "نوع سفارش را انخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            } else if (typePayment.equals("-1")) {
                tvMessage.setText("از ارسال سفارش اطمینان دارید ؟");
            } else {
                tvMessage.setText("از ارسال سفارش اطمینان دارید ؟");
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

            InvoiceDetail invoiceDetailTransport = new InvoiceDetail();
            invoiceDetailTransport.INV_DET_UID = UUID.randomUUID().toString();
            invoiceDetailTransport.INV_UID = Inv_GUID;
            invoiceDetailTransport.ROW_NUMBER = invDetails.size() + 1;
            invoiceDetailTransport.INV_DET_QUANTITY = 1.0;
            invoiceDetailTransport.INV_DET_PRICE_PER_UNIT = String.valueOf(sumTransport);
            invoiceDetailTransport.INV_DET_PERCENT_DISCOUNT = 0.0;
            invoiceDetailTransport.INV_DET_DISCOUNT = "0.0";
            invoiceDetailTransport.INV_DET_TOTAL_AMOUNT = String.valueOf(sumTransport);

            ArrayList<Product> result = new ArrayList<>(Util.AllProduct);
            CollectionUtils.filter(result, r -> r.N.contains("توزیع"));
            if (result.size() > 0) {
                invoiceDetailTransport.PRD_UID = result.get(0).I;
            } else {
                Toast.makeText(getActivity(), "خطا در ارسال مبلغ توزیع", Toast.LENGTH_SHORT).show();
                return;
            }

            invoiceDetailTransport.INV_DET_DESCRIBTION = "توزیع";
            invDetails.add(invoiceDetailTransport);

            for (int i = 0; i < invDetails.size() - 1; i++) {
                ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                int finalI = i;
                CollectionUtils.filter(prdResult, p -> p.I.equals(invDetails.get(finalI).PRD_UID));
                if (prdResult.size() > 0) {
                    double sumTotalPrice = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());//جمع کل ردیف
                    double discountPrice = sumTotalPrice * (prdResult.get(0).PERC_DIS / 100);//جمع تخفیف ردیف
                    double totalPurePrice = sumTotalPrice - discountPrice;//جمع خالص ردیف


                    sumPrice = sumPrice + sumTotalPrice;//جمع کل فاکتور
                    sumPurePrice = sumPurePrice + totalPurePrice;//جمع خالص فاکتور
                    invDetails.get(i).INV_DET_TOTAL_AMOUNT = String.valueOf(totalPurePrice);
                    invDetails.get(i).ROW_NUMBER = i + 1;
                    invDetails.get(i).INV_DET_PERCENT_DISCOUNT = prdResult.get(0).PERC_DIS;
                    invDetails.get(i).INV_DET_DISCOUNT = String.valueOf(discountPrice);
                    invDetails.get(i).INV_DET_PRICE_PER_UNIT = String.valueOf(prdResult.get(0).getPRDPRICEPERUNIT1());
                    sumDiscount = sumDiscount + discountPrice;
                    sumDiscountPercent = sumDiscountPercent + (prdResult.get(0).PERC_DIS / 100);


                }

            }

            for (InvoiceDetail invoicedetail : Select.from(InvoiceDetail.class).where("INVUID = '" + Inv_GUID + "'").list()) {
                InvoiceDetail.deleteInTx(invoicedetail);
            }

            InvoiceDetail.saveInTx(invDetails);

            Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();

            invoice.INV_UID = Inv_GUID;
            invoice.INV_TOTAL_AMOUNT = sumPrice + sumTransport;//جمع فاکنور
            invoice.INV_TOTAL_DISCOUNT = 0.0;
            invoice.INV_PERCENT_DISCOUNT = sumDiscountPercent * 100;
            invoice.INV_DET_TOTAL_DISCOUNT = sumDiscount;
            invoice.INV_DESCRIBTION = binding.edtDescription.getText().toString();
            invoice.INV_TOTAL_TAX = 0.0;
            invoice.INV_TOTAL_COST = 0.0;
            invoice.INV_EXTENDED_AMOUNT = sumPurePrice + sumTransport;
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
            } else {
                invoice.ACC_CLB_ADDRESS = "1";
                invoice.ACC_CLB_DEFAULT_ADDRESS = "1";
            }

            invoice.Acc_name = Acc_NAME;
            invoice.save();
            // LauncherFragment.factorGuid = GID;


            List<Invoice> listInvoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").list();
            // List<InvoiceDetail> invoiceDetailList = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

            List<InvoiceDetail> invoiceDetailList = invDetails;


            List<PaymentRecieptDetail> clsPaymentRecieptDetails = new ArrayList<>();
            if (!typePayment.equals("-1")) {
                PaymentRecieptDetail cl = new PaymentRecieptDetail();
                cl.PAY_RCIPT_DET_DESCRIBTION = listInvoice.get(0).INV_DESCRIBTION;
                cl.PAY_RCIPT_DET_TOTAL_AMOUNT = listInvoice.get(0).INV_EXTENDED_AMOUNT;
                cl.PAY_RCIPT_DET_TYPE = typePayment;
                clsPaymentRecieptDetails.add(cl);
            }


            SendOrder(listInvoice, invoiceDetailList, clsPaymentRecieptDetails);


        });


        binding.ivBack.setOnClickListener(v -> getFragmentManager().popBackStack());


        /*        clickItemPay(binding.layoutPaymentPlace, binding.ivOkPaymentPlace);*/


    }


    private static class JsonObject {
        public List<Invoice> Invoice;
        public List<InvoiceDetail> InvoiceDetail;
        public List<PaymentRecieptDetail> PaymentRecieptDetail;
    }


    void SendOrder(List<Invoice> invoice, List<InvoiceDetail> invoiceDetail, List<PaymentRecieptDetail> clsPaymentRecieptDetail) {
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

                        Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                        if (tb != null) {
                            tb.SV = false;
                            tb.save();
                        }
                        dialog.show();
                    } else {
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

        setADR1 = false;
        onceSee = false;
    }


    private long getDistanceMeters(LatLng StartP, LatLng EndP) {

        double l1 = toRadians(StartP.getLatitude());
        double l2 = toRadians(EndP.getLatitude());
        double g1 = toRadians(StartP.getLongitude());
        double g2 = toRadians(EndP.getLongitude());

        double dist = acos(sin(l1) * sin(l2) + cos(l1) * cos(l2) * cos(g1 - g2));
        if (dist < 0) {
            dist = dist + Math.PI;
        }

        return Math.round(dist * 6378100);
    }

    private double PriceTransport(double distance, double SumPurePrice) {
        double priceTransport = -1.0;

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            switch (pInfo.packageName) {
                case "ir.kitgroup.saleinmeat":

                    imageIconDialog = R.drawable.meat_png;
                    if (SumPurePrice > 2000000) {
                        priceTransport = 0.0;
                    } else {
                        priceTransport = 100000;
                    }


                    break;

                default:
                    if (0 < distance && distance <= 1) {
                        priceTransport = 50000;
                    } else if (1 < distance && distance <= 1.5) {
                        priceTransport = 70000;
                    } else if (1.5 < distance && distance <= 2) {
                        priceTransport = 100000;
                    } else if (2 < distance && distance <= 2.5) {
                        priceTransport = 120000;
                    } else if (2.5 < distance && distance <= 3) {
                        priceTransport = 150000;
                    } else if (3 < distance && distance <= 3.5) {
                        priceTransport = 170000;
                    } else if (3.5 < distance && distance <= 4) {
                        priceTransport = 200000;
                    } else if (4 < distance && distance <= 5) {
                        priceTransport = 220000;
                    } else if (5 < distance && distance <= 6) {
                        priceTransport = 240000;
                    } else if (6 < distance && distance <= 7) {
                        priceTransport = 270000;
                    } else if (7 < distance && distance <= 8) {
                        priceTransport = 300000;
                    } else if (8 < distance && distance <= 9) {
                        priceTransport = 320000;
                    } else if (9 < distance && distance <= 11) {
                        priceTransport = 350000;
                    } else if (distance > 11) {
                        priceTransport = 400000;
                    }
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return priceTransport;

    }


    private static class JsonObjectAccount {

        public List<Account> Account;

    }



//    private void getInquiryAccount(String userName, String passWord, String mobile) {
//
//        customProgress.showProgress(getActivity(),"در حال دریافت موجودی باشگاه",false);
//        try {
//            Call<String> call = App.api.getInquiryAccount(userName, passWord, mobile, "", "", 1);
//
//            call.enqueue(new Callback<String>() {
//                @Override
//                public void onResponse(Call<String> call, Response<String> response) {
//                    Gson gson = new Gson();
//                    Type typeIDs = new TypeToken<ModelAccount>() {
//                    }.getType();
//                    ModelAccount iDs;
//                    try {
//                        iDs = gson.fromJson(response.body(), typeIDs);
//                    } catch (Exception e) {
//                        Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است.", Toast.LENGTH_SHORT).show();
//
//
//                        customProgress.hideProgress();
//                        return;
//                    }
//
//
//                 if (iDs!=null)
//                    if (iDs.getAccountList() == null) {
//                        Type typeIDs0 = new TypeToken<ModelLog>() {
//                        }.getType();
//                        ModelLog iDs0 = gson.fromJson(response.body(), typeIDs0);
//
//                        if (iDs0.getLogs() != null) {
//                            String description = iDs0.getLogs().get(0).getDescription();
//                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                 else {
//
//                        //user is register
//                        if (iDs.getAccountList().size() > 0) {
//                            Account.deleteAll(Account.class);
//                            Account.saveInTx(iDs.getAccountList());
//
//
//                            acc=Select.from(Account.class).first();
//
//
//                            if (acc != null && acc.ADR != null && !acc.ADR.equals(""))
//                                radioAddress1.setText(acc.ADR);
//                            else
//                                radioAddress1.setText("ناموجود");
//
//
//                            if (acc != null && acc.ADR1 != null && !acc.ADR1.equals(""))
//                                radioAddress2.setText(acc.ADR1);
//                            else
//                                radioAddress2.setText("ناموجود");
//
//
//
//
//
//
//                            if (acc != null && acc.ADR != null && !acc.ADR.equals("") && !setADR1) {
//                                if (onceSee) {
//
//                                    double distance = getDistanceMeters(new LatLng(acc.LAT, acc.LNG), new LatLng(lat, lng));
//                                    double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
//                                    if (price == -1.0) {
//                                        binding.tvError.setText("سفارش خارج از محدوده است.");
//                                        binding.tvError.setVisibility(View.VISIBLE);
//                                    } else {
//                                        binding.tvError.setText("");
//                                        binding.tvError.setVisibility(View.GONE);
//                                        sumTransport = price;
//                                        binding.tvTransport.setText(format.format(price) + "ریال");
//                                        binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
//                                    }
//
//                                    binding.tvTAddress.setText(acc.ADR);
//                                    typeAddress = 1;
//                                    address = acc.ADR;
//
//                                }
//                            } else if (acc != null && acc.ADR1 != null && !acc.ADR1.equals("")) {
//                                if (onceSee) {
//
//                                    double distance = getDistanceMeters(new LatLng(acc.LAT1, acc.LNG1), new LatLng(lat, lng));
//                                    double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
//                                    if (price == -1.0) {
//                                        binding.tvError.setText("سفارش خارج از محدوده است.");
//                                        binding.tvError.setVisibility(View.VISIBLE);
//                                    } else {
//                                        binding.tvError.setText("");
//                                        binding.tvError.setVisibility(View.GONE);
//                                        sumTransport = price;
//                                        binding.tvTransport.setText(format.format(price) + "ریال");
//                                        binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
//                                    }
//
//
//                                    binding.tvTAddress.setText(acc.ADR1);
//                                    typeAddress = 2;
//                                    address = acc.ADR1;
//                                }
//
//                            } else {
//                                if (onceSee)
//                                    binding.tvTAddress.setText("ناموجود");
//                                typeAddress = 0;
//                                address = "ناموجود";
//                            }
//
//
//
//
//                            if (acc != null && acc.CRDT != null)
//                                binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
//
//
//                        } else {
//
//                            Toast.makeText(getActivity(), "خطا در بروز رسانی موجودی باشگاه", Toast.LENGTH_SHORT).show();
//                        }
//
//                        customProgress.hideProgress();
//
//
//
//                    }
//
//                 customProgress.hideProgress();
//
//                }
//
//                @Override
//                public void onFailure(Call<String> call, Throwable t) {
//
//                    Toast.makeText(getActivity(), "خطا در بروز رسانی موجودی باشگاه", Toast.LENGTH_SHORT).show();
//                customProgress.hideProgress();
//
//                }
//            });
//
//
//        } catch (NetworkOnMainThreadException ex) {
//            Toast.makeText(getActivity(), "خطا در بروز رسانی موجودی باشگاه", Toast.LENGTH_SHORT).show();
//            customProgress.hideProgress();
//
//        }
//
//
//    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        getFragmentManager().popBackStack();
        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("PaymentFragment");
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (frg != null) {
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }
}
