package ir.kitgroup.saleinOrder.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;


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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.UUID;

import ir.kitgroup.saleinOrder.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinOrder.Adapters.OrderTypePaymentAdapter;
import ir.kitgroup.saleinOrder.Adapters.TimeAdapter;

import ir.kitgroup.saleinOrder.DataBase.Product;
import ir.kitgroup.saleinOrder.DataBase.Setting;
import ir.kitgroup.saleinOrder.models.ModelAccount;
import ir.kitgroup.saleinOrder.models.ModelSetting;
import ir.kitgroup.saleinOrder.models.ModelTypeOrder;
import ir.kitgroup.saleinOrder.models.PaymentRecieptDetail;
import ir.kitgroup.saleinOrder.classes.App;
import ir.kitgroup.saleinOrder.classes.CustomProgress;
import ir.kitgroup.saleinOrder.DataBase.Account;
import ir.kitgroup.saleinOrder.DataBase.Invoice;
import ir.kitgroup.saleinOrder.DataBase.InvoiceDetail;
import ir.kitgroup.saleinOrder.DataBase.OrderType;

import ir.kitgroup.saleinOrder.DataBase.Tables;
import ir.kitgroup.saleinOrder.DataBase.User;


import ir.kitgroup.saleinOrder.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleinOrder.models.ModelLog;
import ir.kitgroup.saleinOrder.R;

import ir.kitgroup.saleinOrder.databinding.FragmentPaymentMobileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class PaymentMobileFragment extends Fragment {


    //region Parameter
    private FragmentPaymentMobileBinding binding;

    private SharedPreferences sharedPreferences;

    private CustomProgress customProgress;


    private String userName;
    private String passWord;
    private double lat = 0.0;
    private double lng = 0.0;
    private String numberPos = "";

    private String error = "";


    private Dialog dialogSendOrder;
    private RelativeLayout rlButtons;
    private MaterialButton btnReturned;
    private TextView tvMessage;


    public static Boolean setADR1 = false;
    private Dialog dialogAddress;
    private RadioButton radioAddress1;
    private RadioButton radioAddress2;
    private int typeAddress = 0;
    private String ValidAddress = "ناموجود";

    private Double latitude1 = 0.0;
    private Double longitude1 = 0.0;

    private Double latitude2 = 0.0;
    private Double longitude2 = 0.0;

    private int imageIconDialog=R.drawable.saleinicon128;

    private static final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private String Sum_PURE_PRICE = "0";


    private List<InvoiceDetail> invDetails;
    private String typePayment = "-1";
    private Integer Ord_TYPE = -1;
    private String Tbl_GUID;
    private String Inv_GUID;
    private String Acc_NAME = "";
    private String Acc_GUID = "";
    private String ord_type = "";
    private Boolean edit;
    private Integer OrderTypeApp = 0;
    private String dateOrder = "";


    private double calculateTransport = 0.0;
    private double sumTransport = 0.0;
    private double sameSumTransport = 0.0;
    private String link = "";


    private Account acc;


    private Dialog dialogTime;
    private TimeAdapter timeAdapter;
    private final ArrayList<String> timesList = new ArrayList<>();
    private final ArrayList<String> times = new ArrayList<>();
    private ArrayList<String> arrayTime = new ArrayList<>();


    private OrderTypePaymentAdapter orderTypePaymentAdapter;
    private  List<OrderType> OrdTList ;

    private Invoice invEdit;


    private Boolean OnceSee = false;
    //end region Parameter


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentMobileBinding.inflate(getLayoutInflater());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());




        customProgress = CustomProgress.getInstance();
        try {
            switch (LauncherActivity.name) {
                case "ir.kitgroup.salein":
                    imageIconDialog = R.drawable.saleinicon128;
                    link = "http://185.201.49.204:4008/";

                    break;

                case "ir.kitgroup.saleintop":

                    binding.layoutPaymentPlace.setVisibility(View.GONE);
                    imageIconDialog = R.drawable.top_png;

                    break;


                case "ir.kitgroup.saleinmeat":
                    binding.layoutPaymentOnline.setVisibility(View.GONE);
                    imageIconDialog = R.drawable.meat_png;
                    typePayment = "";
                    binding.ivOkPaymentPlace.setVisibility(View.VISIBLE);

                    break;
                case "ir.kitgroup.saleinnoon":

                    imageIconDialog = R.drawable.noon;

                    break;
            }
        }catch (Exception e){

        }



        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //region get Bundle
        Bundle bundle = getArguments();
        Inv_GUID = bundle.getString("Inv_GUID");
        Tbl_GUID = bundle.getString("Tbl_GUID");
        Acc_NAME = bundle.getString("Acc_NAME");
        Acc_GUID = bundle.getString("Acc_GUID");
        ord_type = bundle.getString("Ord_TYPE");
        if (ord_type != null && !ord_type.equals(""))
            Ord_TYPE = Integer.parseInt(ord_type);
        Sum_PURE_PRICE = bundle.getString("Sum_PURE_PRICE");
        edit = bundle.getBoolean("EDIT");
        try {
            setADR1 = bundle.getBoolean("setADR1");
        } catch (Exception e) {
        }

        //endregion get Bundle


        //region Configuration Size
        int fontBigSize;
        int fontSize;
        if (SplashScreenFragment.screenInches >= 7) {
            fontBigSize = 14;
            fontSize = 13;
        } else {
            fontBigSize = 12;
            fontSize = 11;
        }

        binding.tvTitle.setTextSize(fontSize);
        binding.edtAddress.setTextSize(fontBigSize);
        binding.tvTAddress.setTextSize(fontSize);
        binding.tvPayment.setTextSize(fontBigSize);
        binding.tvTitlePaymentPlace.setTextSize(fontSize);
        binding.tvTypePaymentPlace.setTextSize(fontSize);
        binding.tvTitlePaymentOnline.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.tvTypeOrder.setTextSize(fontBigSize);
        binding.btnRegisterOrder.setTextSize(fontSize);
        binding.tvSumPurePrice.setTextSize(fontBigSize);
        binding.orderListPurePriceTv.setTextSize(fontBigSize);
        binding.orderListTransportTv.setTextSize(fontBigSize);
        binding.tvTransport.setTextSize(fontBigSize);
        //endregion Configuration Size







        customProgress = CustomProgress.getInstance();
        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;
        lat = Select.from(User.class).first().lat;
        lng = Select.from(User.class).first().lng;
        numberPos = Select.from(User.class).first().numberPos;
        if (numberPos != null && numberPos.equals(""))
            numberPos = "0";




        //region Cast DialogTime
        times.addAll(timesList);
        dialogTime = new Dialog(getActivity());
        dialogTime.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTime.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTime.setContentView(R.layout.dialog_time);
        timeAdapter = new TimeAdapter(getActivity(), times);
        RecyclerView recycleTime = dialogTime.findViewById(R.id.recyclerTime);
        recycleTime.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleTime.setAdapter(timeAdapter);

        timeAdapter.setOnClickItemListener(Name -> {
            Date date = Calendar.getInstance().getTime();

            try {
                int hour;
                hour = Integer.parseInt(Name.split("-")[0]);
                if (hour < date.getHours()) {
                    Toast.makeText(getActivity(), "در این بازه زمانی سفارش ارسال نمی شود.", Toast.LENGTH_SHORT).show();

                    return;
                }

            } catch (Exception e) {

            }

            dialogTime.dismiss();
            dateOrder = Name;

            binding.tvTime.setText(Name);
        });
        //endregion Cast DialogTime

        OrdTList = new ArrayList<>();
        acc = Select.from(Account.class).first();


        //region Cast DialogAddress
        dialogAddress = new Dialog(getActivity());
        dialogAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddress.setContentView(R.layout.dialog_address);
        dialogAddress.setCancelable(true);

        radioAddress1 = dialogAddress.findViewById(R.id.radioAddress1);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        MaterialButton btnNewAddress = dialogAddress.findViewById(R.id.btn_edit);


        radioAddress1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    latitude1 = Double.parseDouble(acc.ADR.split("latitude")[1]);
                    longitude1 = Double.parseDouble(acc.ADR.split("longitude")[0]);
                } catch (Exception e) {
                    latitude1=0.0;
                    longitude1=0.0;
                }
                if (latitude1 == 0.0 && longitude1 == 0.0) {
                    Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();
                    binding.recyclerViewOrderType.setVisibility(View.GONE);
                    return;
                }
                double distance = getDistanceMeters(new LatLng(latitude1, longitude1), new LatLng(lat, lng));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price == -1.0) {
                    Toast.makeText(getActivity(), "سفارش خارج از محدوده است.", Toast.LENGTH_SHORT).show();
                    dialogAddress.dismiss();
                    binding.recyclerViewOrderType.setVisibility(View.GONE);
                    return;
                } else {

                calculateTransport=price;

                }
                binding.recyclerViewOrderType.setVisibility(View.VISIBLE);
                typeAddress = 1;
                ValidAddress = radioAddress1.getText().toString();
                binding.tvTAddress.setText(ValidAddress);

                dialogAddress.dismiss();

            }
        });
        radioAddress2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                try {
                    latitude2 = Double.parseDouble(acc.ADR1.split("latitude")[1]);
                    longitude2 = Double.parseDouble(acc.ADR1.split("longitude")[0]);
                } catch (Exception e) {
                    latitude2=0.0;
                    longitude2=0.0;
                }
                if (latitude2 == 0.0 || longitude2 == 0.0) {
                    Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();
                    binding.recyclerViewOrderType.setVisibility(View.GONE);
                    return;
                }
                double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(lat, lng));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price == -1.0) {
                    Toast.makeText(getActivity(), "سفارش خارج از محدوده است.", Toast.LENGTH_SHORT).show();

                    return;
                } else {
                   calculateTransport=price;
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


        binding.tvTransport.setText(format.format(sumTransport) + " ریال ");


        //region Edit View
        invEdit = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
        if (edit && invEdit != null && invEdit.INV_DESCRIBTION != null) {
            binding.edtDescription.setText(invEdit.INV_DESCRIBTION);
        }


        if (edit) {
            binding.tvError.setVisibility(View.VISIBLE);
        }


       invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

        //endregion Edit View


        //region Cast DialogSendOrder
        dialogSendOrder = new Dialog(getActivity());
        dialogSendOrder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSendOrder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSendOrder.setContentView(R.layout.custom_dialog);
        dialogSendOrder.setCancelable(false);
        tvMessage = dialogSendOrder.findViewById(R.id.tv_message);
        ImageView ivIcon = dialogSendOrder.findViewById(R.id.iv_icon);

        ivIcon.setImageResource(imageIconDialog);
        rlButtons = dialogSendOrder.findViewById(R.id.layoutButtons);
        btnReturned = dialogSendOrder.findViewById(R.id.btn_returned);
        MaterialButton btnOk = dialogSendOrder.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialogSendOrder.findViewById(R.id.btn_cancel);


        btnReturned.setOnClickListener(v -> {

            dialogSendOrder.dismiss();
            if (App.mode == 2) {

                if (edit) {
                    for (int i = 0; i < 5; i++) {
                        getFragmentManager().popBackStack();
                    }
                } else {
                    for (int i = 0; i < 2; i++) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }


                assert getFragmentManager() != null;

                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");

                if (frg != null) {

                    if (frg instanceof MainOrderMobileFragment) {
                        MainOrderMobileFragment fgf = (MainOrderMobileFragment) frg;
                        fgf.setHomeBottomBarAndClearBadge();
                    }
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Ord_TYPE", "");
                    bundle1.putString("Tbl_GUID", "");
                    bundle1.putString("Inv_GUID", "");
                    MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                    mainOrderMobileFragment.setArguments(bundle1);
                    FragmentTransaction replaceFragment;
                    if(LauncherActivity.namePackage.equals("ir.kitgroup.salein")){
                        replaceFragment= requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF");
                    }else {
                        replaceFragment= requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment");
                    }


                    replaceFragment.commit();
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


        //region SetAddress
        if (acc != null && acc.ADR != null && !acc.ADR.equals("")) {
            String address = "";
            try {
                latitude1 = Double.parseDouble(acc.ADR.split("latitude")[1]);
                longitude1 = Double.parseDouble(acc.ADR.split("longitude")[0]);
                address = acc.ADR.replace(acc.ADR.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR.split("longitude")[0], "").replace("longitude", "");

            } catch (Exception e) {
                address = acc.ADR + "( نامعتبر )";
                latitude1 = 0.0;
                longitude1 = 0.0;
            }
            radioAddress1.setText(address);

        } else
            radioAddress1.setText("ناموجود");




        if (acc != null && acc.ADR1 != null && !acc.ADR1.equals("")) {
            String address = "";
            try {
                latitude2 = Double.parseDouble(acc.ADR1.split("latitude")[1]);
                longitude2 = Double.parseDouble(acc.ADR1.split("longitude")[0]);
                address = acc.ADR1.replace(acc.ADR1.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR1.split("longitude")[0], "").replace("longitude", "");

            } catch (Exception e) {
                address = acc.ADR1 + "( نامعتبر )";
                latitude2 = 0.0;
                longitude2 = 0.0;
            }
            radioAddress2.setText(address);

        } else
            radioAddress2.setText("ناموجود");










        if (acc != null && acc.ADR != null && !acc.ADR.equals("") && latitude1 != 0.0 && longitude1 != 0.0 && !setADR1) {


            latitude1 = Double.parseDouble(acc.ADR.split("latitude")[1]);
            longitude1 = Double.parseDouble(acc.ADR.split("longitude")[0]);

            double distance = getDistanceMeters(new LatLng(latitude1, longitude1), new LatLng(lat, lng));
            double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
            if (price != -1.0) {
                binding.tvError.setText("");
                binding.tvError.setVisibility(View.GONE);
                calculateTransport=price;
            }
            String address = "";
            try {
                address = acc.ADR.replace(acc.ADR.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR.split("longitude")[0], "").replace("longitude", "");
            } catch (Exception e) {
                address = acc.ADR;
            }
            binding.recyclerViewOrderType.setVisibility(View.VISIBLE);
            binding.tvTAddress.setText(address);
            typeAddress = 1;
            ValidAddress = address;

        }

       else if (acc != null && acc.ADR1 != null && !acc.ADR1.equals("") && latitude2 != 0.0 && longitude2 != 0.0 ) {

            latitude2 = Double.parseDouble(acc.ADR1.split("latitude")[1]);
            longitude2 = Double.parseDouble(acc.ADR1.split("longitude")[0]);
            double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(lat, lng));
            double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
            if (price != -1.0) {
                binding.tvError.setText("");
                binding.tvError.setVisibility(View.GONE);
                calculateTransport=price;
            }

            String address = "";
            try {
                address = acc.ADR1.replace(acc.ADR1.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR1.split("longitude")[0], "").replace("longitude", "");
            } catch (Exception e) {
                address = acc.ADR1;

            }
            binding.recyclerViewOrderType.setVisibility(View.VISIBLE);
            binding.tvTAddress.setText(address);
            typeAddress = 2;
            ValidAddress = address;


        }




        if (acc == null || (acc.ADR1 == null && acc.ADR == null)) {

            typeAddress = 0;
            ValidAddress = "ناموجود";
        }
        //endregion SetAddress


        binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");

        if (App.mode == 1) {
            binding.rlTypeOrder.setVisibility(View.GONE);
            binding.layoutAdd.setVisibility(View.GONE);
        }


        //region Configuration RecyclerViewOrderType
        orderTypePaymentAdapter = new OrderTypePaymentAdapter(getActivity(), OrdTList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);
        binding.recyclerViewOrderType.setLayoutManager(manager);
        binding.recyclerViewOrderType.setScrollingTouchSlop(View.FOCUS_LEFT);
        binding.recyclerViewOrderType.setAdapter(orderTypePaymentAdapter);


        orderTypePaymentAdapter.setOnClickListener((GUID, code) -> {
            Ord_TYPE = code;

            //region UnClick Old Item
            ArrayList<OrderType> list = new ArrayList<>(OrdTList);
            CollectionUtils.filter(list, r -> r.Click);
            if (list.size() > 0) {
                OrdTList.get(OrdTList.indexOf(list.get(0))).Click = false;
            }
            //endregion UnClick Old Item


            //region Click New Item
            ArrayList<OrderType> list2 = new ArrayList<>(OrdTList);
            CollectionUtils.filter(list2, r -> r.getI().equals(GUID));
            if (list2.size() > 0) {
                OrdTList.get(OrdTList.indexOf(list2.get(0))).Click = true;
            }


            //endregion Click New Item


            orderTypePaymentAdapter.notifyDataSetChanged();
            if (Ord_TYPE.equals(OrderTypeApp)) {
                sumTransport = 0;
                binding.tvTransport.setText("0 ریال");
                binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");
            }else {
                sumTransport =calculateTransport;
                binding.tvTransport.setText(format.format(sumTransport)+" ریال ");
                binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)+sumTransport) + "ریال");
            }


        });
        //endregion Configuration RecyclerViewOrderType


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

            typePayment = "";
            binding.tvSuccessFullPayOnline.setText("");

            if (acc != null && acc.CRDT != null && acc.CRDT >= (Double.parseDouble(Sum_PURE_PRICE) + sumTransport)) {
                binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
            }

            binding.ivOkPaymentOnline.setVisibility(View.GONE);
            binding.ivOkPaymentPlace.setVisibility(View.VISIBLE);

        });


        binding.layoutPaymentOnline.setOnClickListener(v -> {
            binding.ivOkPaymentPlace.setVisibility(View.GONE);
            if (acc != null && acc.CRDT != null && acc.CRDT >= (Double.parseDouble(Sum_PURE_PRICE) + sumTransport)) {
                binding.tvSuccessFullPayOnline.setText("پرداخت موفقیت آمیز");
                binding.tvCredit.setText(format.format(acc.CRDT - (Double.parseDouble(Sum_PURE_PRICE) + sumTransport)));
                typePayment = "4";
                binding.ivOkPaymentOnline.setVisibility(View.VISIBLE);
            } else if (!link.equals("")) {

                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                startActivityForResult(intent, 44);

            } else {
                Toast.makeText(getActivity(), "انتقال به باشگاه مشتریان ، در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
                typePayment = "-1";
            }


         /*   binding.tvTypePaymentPlace.setText("");
            binding.ivOkPaymentOnline.setVisibility(View.VISIBLE);
            typePayment="4";*/
        });


        binding.btnRegisterOrder.setOnClickListener(v -> {

            if ((ValidAddress.equals("ناموجود") || typeAddress == 0) && App.mode == 2) {
                Toast.makeText(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            } else if (Ord_TYPE == null || Ord_TYPE == -1) {
                Toast.makeText(getActivity(), "نوع سفارش را انخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            } else if (typePayment.equals("-1")) {
                Toast.makeText(getActivity(), "نوع پرداخت را مشخص کنید.", Toast.LENGTH_SHORT).show();
                return;
            } else if (dateOrder.equals("")) {
                Toast.makeText(getActivity(), "زمان ارسال سفارش را تعیین کنید", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tvMessage.setText("از ارسال سفارش اطمینان دارید ؟");
            }
            rlButtons.setVisibility(View.VISIBLE);
            btnReturned.setVisibility(View.GONE);

            dialogSendOrder.show();

        });


        btnNo.setOnClickListener(v -> dialogSendOrder.dismiss());


        btnOk.setOnClickListener(v -> {
            dialogSendOrder.dismiss();
            Date date = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            double sumPrice = 0;
            double sumDiscount = 0;
            double sumDiscountPercent = 0;
            double sumPurePrice = 0;


            InvoiceDetail invoiceDetailTransport = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "' AND INVDETDESCRIBTION ='" + "توزیع" + "'").first();



            if (invoiceDetailTransport==null) {
                invoiceDetailTransport = new InvoiceDetail();
                invoiceDetailTransport.INV_DET_UID = UUID.randomUUID().toString();
                invoiceDetailTransport.ROW_NUMBER = invDetails.size() + 1;
                invoiceDetailTransport.INV_UID = Inv_GUID;
                invoiceDetailTransport.INV_DET_QUANTITY = 1.0;
                invoiceDetailTransport.INV_DET_PRICE_PER_UNIT = String.valueOf(sumTransport);
                invoiceDetailTransport.INV_DET_TOTAL_AMOUNT = String.valueOf(sumTransport);
                invoiceDetailTransport.INV_DET_PERCENT_DISCOUNT = 0.0;
                invoiceDetailTransport.INV_DET_DISCOUNT = "0.0";
                String TransportId = sharedPreferences.getString("transportId", "");
            if (!TransportId.equals("")) {
                invoiceDetailTransport.PRD_UID = TransportId;
            } else {
                Toast.makeText(getActivity(), "خطا در ارسال مبلغ توزیع", Toast.LENGTH_SHORT).show();
                return;
            }

            invoiceDetailTransport.INV_DET_DESCRIBTION = "توزیع";


            InvoiceDetail.save(invoiceDetailTransport);
            }else {
                invoiceDetailTransport.INV_DET_QUANTITY = 1.0;
                invoiceDetailTransport.INV_DET_PRICE_PER_UNIT = String.valueOf(sumTransport);
                invoiceDetailTransport.INV_DET_TOTAL_AMOUNT = String.valueOf(sumTransport);
                invoiceDetailTransport.update();
            }








            for (int i = 0; i < invDetails.size() ; i++) {

                ir.kitgroup.saleinOrder.DataBase.Product product = Select.from(ir.kitgroup.saleinOrder.DataBase.Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();

                InvoiceDetail invoiceDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invDetails.get(i).INV_DET_UID + "'").first();

                if (product != null) {
                    double sumTotalPrice = (invoiceDtl.INV_DET_QUANTITY * product.getPrice());//جمع کل ردیف
                    double discountPrice = sumTotalPrice * (product.getPercDis() / 100);//جمع تخفیف ردیف
                    double totalPurePrice = sumTotalPrice - discountPrice;//جمع خالص ردیف


                    sumPrice = sumPrice + sumTotalPrice;//جمع کل فاکتور
                    sumPurePrice = sumPurePrice + totalPurePrice;//جمع خالص فاکتور
                    invoiceDtl.INV_DET_TOTAL_AMOUNT = String.valueOf(totalPurePrice);
                    invoiceDtl.ROW_NUMBER = i + 1;
                    invoiceDtl.INV_DET_PERCENT_DISCOUNT = product.getPercDis();
                    invoiceDtl.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                    invoiceDtl.INV_DET_PRICE_PER_UNIT = String.valueOf(product.getPrice());
                    sumDiscount = sumDiscount + discountPrice;
                    sumDiscountPercent = sumDiscountPercent + (product.getPercDis() / 100);


                    invoiceDtl.update();

                }

            }
          Invoice invoice=  Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
            if (invoice==null)
                invoice = new Invoice();

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
            Date date1 = date;

            int hour = date.getHours();

            try {
                hour = Integer.parseInt(dateOrder.split("-")[0]);

            } catch (Exception e) {
            }


            invoice.INV_DUE_DATE = date1;
            invoice.INV_DUE_TIME = hour + ":" + "00";
            invoice.INV_STATUS = true;
            invoice.ACC_CLB_UID = Acc_GUID;
            invoice.TBL_UID = Tbl_GUID;
            invoice.INV_TYPE_ORDER = Ord_TYPE;


            if (typeAddress == 1) {
                invoice.ACC_CLB_ADDRESS = ValidAddress;
                invoice.ACC_CLB_DEFAULT_ADDRESS = "1";
            } else if (typeAddress == 2) {
                invoice.ACC_CLB_ADDRESS2 = ValidAddress;
                invoice.ACC_CLB_DEFAULT_ADDRESS = "2";
            } else {
                invoice.ACC_CLB_ADDRESS = "1";
                invoice.ACC_CLB_DEFAULT_ADDRESS = "1";
            }

            invoice.Acc_name = Acc_NAME;
            invoice.save();


            List<Invoice> listInvoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").list();


            List<InvoiceDetail> invoiceDetailList = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();


            List<PaymentRecieptDetail> clsPaymentRecieptDetails = new ArrayList<>();
            if (!typePayment.equals("-1") && !typePayment.equals("")) {
                PaymentRecieptDetail cl = new PaymentRecieptDetail();
                cl.PAY_RCIPT_DET_DESCRIBTION = listInvoice.get(0).INV_DESCRIBTION;
                cl.PAY_RCIPT_DET_TOTAL_AMOUNT = listInvoice.get(0).INV_EXTENDED_AMOUNT;
                cl.PAY_RCIPT_DET_TYPE = typePayment;
                clsPaymentRecieptDetails.add(cl);
            }


            SendOrder(listInvoice, invoiceDetailList, clsPaymentRecieptDetails);


        });


        binding.layoutTime.setOnClickListener(v -> {
            timesList.clear();
            if (arrayTime.size() == 0) {
                Toast.makeText(getActivity(), "زمان ارسال سفارش از سرور تعیین نشده است.", Toast.LENGTH_SHORT).show();
            } else {

                Date date = Calendar.getInstance().getTime();

                for (int i = 0; i < arrayTime.size(); i++) {
                    int hour;

                    try {
                        hour = Integer.parseInt(arrayTime.get(i).split("-")[0]);

                        if ((hour - date.getHours() == 1) && date.getMinutes() < 50 || hour > date.getHours())

                            timesList.add(arrayTime.get(i));


                    } catch (Exception e) {
                    }

                }
                times.clear();
                times.addAll(timesList);
                dialogTime.show();
            }
        });


        binding.ivBack.setOnClickListener(v -> getFragmentManager().popBackStack());


        getSetting();
    }


    private class JsonObject {
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

                    if (iDs != null) {
                        message = iDs.getLogs().get(0).getMessage();

                    }


                    rlButtons.setVisibility(View.GONE);
                    btnReturned.setVisibility(View.VISIBLE);


                    String name;
                    try {
                        name = LauncherActivity.name.split("ir.kitgroup.")[1];
                        sharedPreferences.edit().putString(name, "").apply();
                    }catch (Exception ignore){

                    }



                    if (message == 1) {
                        List<Invoice> invoices = Select.from(Invoice.class).list();

                        CollectionUtils.filter(invoices, i -> i.INV_SYNC!=null && !i.INV_SYNC.equals("#"));



                        for (int i = 0; i < invoices.size(); i++) {
                            Invoice.delete(invoices.get(i));
                            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" +
                                    invoices.get(i).INV_UID + "'").list();
                            InvoiceDetail.delete(invoiceDetails);
                        }



                        tvMessage.setText("سفارش با موفقیت ارسال شد");

                        Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                        if (tb != null) {
                            tb.SV = false;
                            tb.save();
                        }
                        dialogSendOrder.show();
                    } else {
                        Invoice invoice = Select.from(Invoice.class).where("INVUID = '" + Inv_GUID + "'").first();
                        if (invoice != null) {
                            invoice.INV_SYNC = "#";
                            invoice.save();
                        }

                        Product.deleteAll(Product.class);
                        tvMessage.setText("خطا در ارسال ،" + "این سفارش ذخیره می شود با مراجعه به پروفایل خود سفارش را مجددارسال کنید");
                        dialogSendOrder.show();

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


        switch (LauncherActivity.name) {
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


        return priceTransport;

    }


    private void getInquiryAccount(String userName, String passWord, String mobile) {

        customProgress.showProgress(getActivity(), "در حال دریافت موجودی باشگاه", false);
        try {
            Call<String> call = App.api.getInquiryAccount(userName, passWord, mobile, "", "", 1, 1);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelAccount>() {
                    }.getType();
                    ModelAccount iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است.", Toast.LENGTH_SHORT).show();


                        customProgress.hideProgress();
                        return;
                    }


                    if (iDs != null)
                        if (iDs.getAccountList() == null) {
                            Type typeIDs0 = new TypeToken<ModelLog>() {
                            }.getType();
                            ModelLog iDs0 = gson.fromJson(response.body(), typeIDs0);

                            if (iDs0.getLogs() != null) {
                                String description = iDs0.getLogs().get(0).getDescription();
                                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            //user is register
                            if (iDs.getAccountList().size() > 0) {
                                Account.deleteAll(Account.class);
                                Account.saveInTx(iDs.getAccountList());


                                acc = Select.from(Account.class).first();
                                if (acc != null && acc.CRDT != null)
                                    binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.medium_color));

                                binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");


                            } else {

                                binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.red_table));
                                binding.tvCredit.setText("خطا در بروز رسانی موجودی ");

                            }
                            OnceSee = true;

                            customProgress.hideProgress();


                        }

                    customProgress.hideProgress();
                    return;

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Toast.makeText(getActivity(), "خطا در بروز رسانی موجودی باشگاه", Toast.LENGTH_SHORT).show();
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getActivity(), "خطا در بروز رسانی موجودی باشگاه", Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();

        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        OnceSee = false;
        reloadFragment(setADR1);
    }


    public Bundle reloadFragment(Boolean setAddress) {

        Bundle bundle = getArguments();
        bundle.putString("Inv_GUID", Inv_GUID);
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putString("Acc_NAME", Acc_NAME);
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putString("Ord_TYPE", ord_type);
        bundle.putString("Sum_PURE_PRICE", Sum_PURE_PRICE);
        bundle.putBoolean("edit", edit);
        bundle.putBoolean("setADR1", setAddress);
        return bundle;

 /*       getFragmentManager().popBackStack();

        PaymentMobileFragment paymentFragment = new PaymentMobileFragment();
        paymentFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, paymentFragment, "PaymentFragment").addToBackStack("PaymentF").commit();*/


    }


    private void getSetting() {
        try {
            customProgress.showProgress(getActivity(), "در حال دریافت اطلاعات", false);
            Call<String> call = App.api.getSetting(userName, passWord);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelSetting>() {
                    }.getType();

                    ModelSetting iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {
                        error = error + "\n" + "مدل دریافت شده از تنظیمات نا معتبر است";


                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        if (customProgress.isShow)
                            customProgress.hideProgress();
                        customProgress.hideProgress();
                        return;
                    }

                    if (iDs == null) {
                        error = error + "\n" + "لیست دریافت شده از تنظیمات نا معتبر می باشد";
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();

                        if (customProgress.isShow)
                            customProgress.hideProgress();
                        customProgress.hideProgress();
                        return;
                    } else {

                        timesList.clear();
                        List<Setting> settingsList = new ArrayList<>(iDs.getSettings());

                        if (!settingsList.get(0).ORDER_TYPE_APP.equals(""))
                            OrderTypeApp = Integer.parseInt(settingsList.get(0).ORDER_TYPE_APP);

                       sharedPreferences.edit().putString("transportId",settingsList.get(0).PEYK).apply();
                        try {
                            arrayTime = new ArrayList<>(Arrays.asList(settingsList.get(0).SERVICE_TIME.split(",")));


                        } catch (Exception e) {
                        }


                        timeAdapter.notifyDataSetChanged();
                        getTypeOrder();


                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    error = error + "\n" + "خطای تایم اوت در دریافت تنظیمات";
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                    if (customProgress.isShow)
                        customProgress.hideProgress();
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {


            error = error + "\n" + "خطا در اتصال به سرور برای دریافت تنطیمات";
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            if (customProgress.isShow)
                customProgress.hideProgress();
            customProgress.hideProgress();
        }
    }


    private void getTypeOrder() {


        try {

            Call<String> call = App.api.getOrderType(userName, passWord);
            call.enqueue(new Callback<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelTypeOrder>() {
                    }.getType();

                    ModelTypeOrder iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {


                        error = error + "\n" + "مدل دریافت شده از نوع سفارش نا معتبر است";
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        if (customProgress.isShow)
                            customProgress.hideProgress();
                        customProgress.hideProgress();

                        return;
                    }

                    if (iDs == null) {

                        error = error + "\n" + "لیست دریافت شده از نوع سفارش نا معتبر می باشد";
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        if (customProgress.isShow)
                            customProgress.hideProgress();
                        customProgress.hideProgress();
                        return;
                    } else {

                        CollectionUtils.filter(iDs.getOrderTypes(), i -> i.getTy() == 2);
                        if (iDs.getOrderTypes().size() > 0)
                            OrdTList.addAll(iDs.getOrderTypes());


                        if (OrdTList.size() > 1) {
                            orderTypePaymentAdapter.notifyDataSetChanged();
                        } else if (OrdTList.size() == 1) {
                            Ord_TYPE = OrdTList.get(0).getC();
                            binding.rlTypeOrder.setVisibility(View.GONE);
                        }
                        if (invEdit != null && invEdit.INV_TYPE_ORDER != null) {
                            ArrayList<OrderType> list2 = new ArrayList<>(OrdTList);
                            CollectionUtils.filter(list2, r -> r.getC().equals(invEdit.INV_TYPE_ORDER));
                            if (list2.size() > 0) {
                                OrdTList.get(OrdTList.indexOf(list2.get(0))).Click = true;
                                Ord_TYPE = invEdit.INV_TYPE_ORDER;



                            }
                            orderTypePaymentAdapter.notifyDataSetChanged();
                        }
                        orderTypePaymentAdapter.notifyDataSetChanged();

                        if (Ord_TYPE.equals(OrderTypeApp)) {
                            sumTransport = 0;
                            binding.tvTransport.setText("0 ریال");
                            binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");
                        } else {
                           if (invEdit!=null || OrdTList.size() == 1 ){
                               sumTransport = calculateTransport;
                               binding.tvTransport.setText(format.format(sumTransport) + " ریال ");
                               binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
                           }


                        }

                    }
                    if (customProgress.isShow)
                        customProgress.hideProgress();
                    customProgress.hideProgress();




                 //region Get Credit Club
                    String name;
                    try {
                        if (!OnceSee && !LauncherActivity.name.equals("ir.kitgroup.saleinmeat"))
                            getInquiryAccount(userName, passWord, acc.M);
                        else if (OnceSee && !LauncherActivity.name.equals("ir.kitgroup.saleinmeat"))
                            binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
                    }catch (Exception ignore){

                    }

                    //endregion Get Credit Club


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    error = error + "\n" + "خطای تایم اوت در دریافت نوع سفارش";

                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    if (customProgress.isShow)
                        customProgress.hideProgress();
                    customProgress.hideProgress();
                    return;
                }
            });


        } catch (NetworkOnMainThreadException ex) {

            error = error + "\n" + "خطا در اتصال به سرور برای دریافت نوع سفارش";
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            if (customProgress.isShow)
                customProgress.hideProgress();
            customProgress.hideProgress();
            return;
        }


    }


}
