package ir.kitgroup.saleinOrder.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import java.util.Locale;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinOrder.Adapters.DateAdapter;
import ir.kitgroup.saleinOrder.Adapters.OrderTypePaymentAdapter1;
import ir.kitgroup.saleinOrder.Adapters.TimeAdapter;

import ir.kitgroup.saleinOrder.DataBase.Product;
import ir.kitgroup.saleinOrder.Util.Utilities;
import ir.kitgroup.saleinOrder.models.ModelDate;
import ir.kitgroup.saleinOrder.models.ModelTable;
import ir.kitgroup.saleinOrder.models.Setting;
import ir.kitgroup.saleinOrder.models.ModelAccount;
import ir.kitgroup.saleinOrder.models.ModelSetting;
import ir.kitgroup.saleinOrder.models.ModelTypeOrder;
import ir.kitgroup.saleinOrder.models.PaymentRecieptDetail;
import ir.kitgroup.saleinOrder.classes.App;
import ir.kitgroup.saleinOrder.classes.CustomProgress;
import ir.kitgroup.saleinOrder.DataBase.Account;
import ir.kitgroup.saleinOrder.models.Invoice;
import ir.kitgroup.saleinOrder.DataBase.InvoiceDetail;
import ir.kitgroup.saleinOrder.models.OrderType;

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
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    //region Dialog Address
    private Account acc;

    private Dialog dialogAddress;
    private RadioButton radioAddress1;
    private RadioButton radioAddress2;
    private int typeAddress = 0;
    private String ValidAddress = "ناموجود";


    private Double latitude1 = 0.0;
    private Double longitude1 = 0.0;

    private Double latitude2 = 0.0;
    private Double longitude2 = 0.0;

    public static Boolean setADR1 = false;
    private int imageIconDialog = R.drawable.saleinorder_png;


    private double lat = 0.0;
    private double lng = 0.0;

    private double calculateTransport = 0.0;
    //endregion Dialog Address


    private String typePayment = "-1";


    private SharedPreferences sharedPreferences;


    private CustomProgress customProgress;

    private String userName;
    private String passWord;

    private String numberPos = "";

    private String error = "";


    private Dialog dialogSendOrder;
    private RelativeLayout rlButtons;
    private MaterialButton btnReturned;
    private TextView tvMessage;


    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private String Sum_PURE_PRICE = "0";


    private List<InvoiceDetail> invDetails;

    private Integer Ord_TYPE = -1;
    private String Tbl_GUID;
    private String new_Tbl_GUID;


    private String Inv_GUID;
    private String new_Inv_GUID;


    private Boolean edit = false;
    private Integer OrderTypeApp = 0;
    private Integer SERVICE_DAY = 0;
    private String timeChoose = "";


    private double sumTransport = 0.0;
    private String link = "";


    private Dialog dialogTime;
    private TimeAdapter timeAdapter;
    private DateAdapter dateAdapter;
    private ArrayList<String> timesList;
    private ArrayList<String> times;
    private ArrayList<String> allTime;
    private ArrayList<ModelDate> allDate;
    private ArrayList<ModelDate> dateList;
    private Date dateChoose;


    private OrderTypePaymentAdapter1 orderTypePaymentAdapter;
    private List<OrderType> OrdTList;


    private Boolean OnceSee = false;
    private LinearLayout bottomRow;


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


                    break;

                case "ir.kitgroup.saleintop":

                    binding.btnPaymentPlace.setVisibility(View.GONE);
                    imageIconDialog = R.drawable.top_png;
                    link = "http://185.201.49.204:4008/";

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
        } catch (Exception e) {

        }


        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OrdTList = new ArrayList<>();

        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;
        lat = Select.from(User.class).first().lat;
        lng = Select.from(User.class).first().lng;
        numberPos = Select.from(User.class).first().numberPos;
        if (numberPos != null && numberPos.equals(""))
            numberPos = "0";


        timesList = new ArrayList<>();
        times = new ArrayList<>();
        allTime = new ArrayList<>();
        allDate = new ArrayList<>();
        dateList = new ArrayList<>();


        Date dateNow = Calendar.getInstance().getTime();
        dateChoose = dateNow;


        //region get Bundle
        Bundle bundle = getArguments();
        Inv_GUID = bundle.getString("Inv_GUID");
        Tbl_GUID = bundle.getString("Tbl_GUID");
        Sum_PURE_PRICE = bundle.getString("Sum_PURE_PRICE");
        edit = bundle.getBoolean("EDIT");
        setADR1 = bundle.getBoolean("setADR1");


        String ord_type = bundle.getString("Ord_TYPE");
        if (ord_type != null && !ord_type.equals(""))
            Ord_TYPE = Integer.parseInt(ord_type);


        if (Tbl_GUID.equals(""))
            new_Tbl_GUID = UUID.randomUUID().toString();
        else
            new_Tbl_GUID = Tbl_GUID;

        if (App.mode == 2 || (App.mode == 1 && edit) || (App.mode == 1 && Tbl_GUID.equals("")))
            new_Inv_GUID = Inv_GUID;

        else if (App.mode == 1 && !Tbl_GUID.equals(""))
            new_Inv_GUID = UUID.randomUUID().toString();


        //endregion get Bundle



        if (App.mode==1) {
            binding.layoutTypeOrder.setVisibility(View.GONE);
            binding.layoutAddress.setVisibility(View.GONE);
            binding.layoutTime.setVisibility(View.GONE);
            binding.layoutPayment.setVisibility(View.VISIBLE);
        }
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


        binding.tvAddress.setTextSize(fontBigSize);
        binding.tvPayment.setTextSize(fontBigSize);
        binding.tvTime.setTextSize(fontBigSize);
        binding.tvTypeOrder.setTextSize(fontBigSize);


        binding.edtAddress.setTextSize(fontSize);
        binding.tvTitlePaymentPlace.setTextSize(fontSize);
        binding.tvTitlePaymentOnline.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.edtTime.setTextSize(fontSize);


        binding.orderListPurePriceTv.setTextSize(fontBigSize);
        binding.orderListTransportTv.setTextSize(fontBigSize);

        binding.tvSumPurePrice.setTextSize(fontBigSize);
        binding.tvTransport.setTextSize(fontBigSize);


        binding.btnRegisterOrder.setTextSize(fontSize);

        //endregion Configuration Size


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
                    latitude1 = 0.0;
                    longitude1 = 0.0;
                }
                if (latitude1 == 0.0 && longitude1 == 0.0) {
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
                    calculateTransport = price;
                }


                typeAddress = 1;
                ValidAddress = radioAddress1.getText().toString();
                binding.edtAddress.setText(ValidAddress);

                dialogAddress.dismiss();

            }
        });
        radioAddress2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                try {
                    latitude2 = Double.parseDouble(acc.ADR2.split("latitude")[1]);
                    longitude2 = Double.parseDouble(acc.ADR2.split("longitude")[0]);
                } catch (Exception e) {
                    latitude2 = 0.0;
                    longitude2 = 0.0;
                }
                if (latitude2 == 0.0 || longitude2 == 0.0) {
                    Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();

                    return;
                }
                double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(lat, lng));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price == -1.0) {
                    Toast.makeText(getActivity(), "سفارش خارج از محدوده است.", Toast.LENGTH_SHORT).show();
                    dialogAddress.dismiss();
                    if (App.mode == 2)
                        binding.tvTypeOrder.setVisibility(View.GONE);
                    return;
                } else {
                    calculateTransport = price;
                }


                typeAddress = 2;
                ValidAddress = radioAddress2.getText().toString();
                binding.edtAddress.setText(ValidAddress);


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


        //region SetAddress
        acc = Select.from(Account.class).first();


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


        if (acc != null && acc.ADR2 != null && !acc.ADR2.equals("")) {
            String address = "";
            try {
                latitude2 = Double.parseDouble(acc.ADR2.split("latitude")[1]);
                longitude2 = Double.parseDouble(acc.ADR2.split("longitude")[0]);
                address = acc.ADR2.replace(acc.ADR2.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR2.split("longitude")[0], "").replace("longitude", "");

            } catch (Exception e) {
                address = acc.ADR2 + "( نامعتبر )";
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
                calculateTransport = price;
            }
            String address = "";
            try {
                address = acc.ADR.replace(acc.ADR.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR.split("longitude")[0], "").replace("longitude", "");
            } catch (Exception e) {
                address = acc.ADR;
            }


            binding.edtAddress.setText(address);
            typeAddress = 1;
            ValidAddress = address;

        } else if (acc != null && acc.ADR2 != null && !acc.ADR2.equals("") && latitude2 != 0.0 && longitude2 != 0.0) {

            latitude2 = Double.parseDouble(acc.ADR2.split("latitude")[1]);
            longitude2 = Double.parseDouble(acc.ADR2.split("longitude")[0]);
            double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(lat, lng));
            double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
            if (price != -1.0) {
                binding.tvError.setText("");
                binding.tvError.setVisibility(View.GONE);
                calculateTransport = price;
            }

            String address = "";
            try {
                address = acc.ADR2.replace(acc.ADR2.split("latitude")[1], "").replace("latitude", "").replace(acc.ADR2.split("longitude")[0], "").replace("longitude", "");
            } catch (Exception e) {
                address = acc.ADR2;

            }



            binding.edtAddress.setText(address);
            typeAddress = 2;
            ValidAddress = address;


        } else {

            typeAddress = 0;
            ValidAddress = "ناموجود";
        }


        binding.layoutAddress.setOnClickListener(v -> {


            if (acc != null && acc.ADR != null && !acc.ADR.equals("") && acc.ADR2 != null && !acc.ADR2.equals("")) {
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

        //endregion SetAddress


        //region Configuration Time
        times.addAll(timesList);
        dialogTime = new Dialog(getActivity());
        dialogTime.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTime.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTime.setContentView(R.layout.dialog_time);
        timeAdapter = new TimeAdapter(getActivity(), times);
        dateAdapter = new DateAdapter(getActivity(), dateList);


        RecyclerView recycleTime = dialogTime.findViewById(R.id.recyclerTime);
        recycleTime.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleTime.setAdapter(timeAdapter);

        RecyclerView recycleDate = dialogTime.findViewById(R.id.recyclerDate);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);
        recycleDate.setLayoutManager(manager);
        recycleDate.setAdapter(dateAdapter);


        dateAdapter.setOnClickItemListener((date, position) -> {
            dateChoose = date;
            timesList.clear();
            if (position == 0) {
                Date date1 = Calendar.getInstance().getTime();
                for (int i = 0; i < allTime.size(); i++) {
                    int hour;
                    try {
                        hour = Integer.parseInt(allTime.get(i).split("-")[0]);


                        if (hour > date1.getHours())
                            if ((hour - date1.getHours() == 1) && date1.getMinutes() > 45) {
                            } else
                                timesList.add(allTime.get(i));


                    } catch (Exception ignore) {
                    }

                }

            } else {
                timesList.addAll(allTime);
            }
            times.clear();
            times.addAll(timesList);
            timeAdapter.notifyDataSetChanged();


        });
        timeAdapter.setOnClickItemListener(time -> {

            Date date = Calendar.getInstance().getTime();

            try {
                int hour;//server time
                hour = Integer.parseInt(time.split("-")[0]);

                if (hour < date.getHours() &&
                        date.getDate() == dateChoose.getDate() &&
                        date.getDay() == dateChoose.getDay() &&
                        date.getMonth() == dateChoose.getMonth() &&
                        date.getYear() == dateChoose.getYear()
                ) {
                    Toast.makeText(getActivity(), "در این بازه زمانی سفارش ارسال نمی شود.", Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception ignore) {
            }

            dialogTime.dismiss();
            timeChoose = time;


            Utilities util = new Utilities();
            Locale loc = new Locale("en_US");
            Utilities.SolarCalendar sc;
            sc = util.new SolarCalendar(dateChoose);
            String datePersian = sc.strWeekDay + String.format(loc, "%02d", sc.date) + "\t" + sc.strMonth + "\t" + (sc.year);


            binding.edtTime.setText(time + "   " + datePersian);
        });


        binding.layoutTime.setOnClickListener(v -> {

            dateChoose = dateNow;
            dateList.clear();
            ArrayList<ModelDate> arrayList = new ArrayList<>(allDate);
            CollectionUtils.filter(arrayList, a -> a.Click);
            if (arrayList.size() > 0)
                allDate.get(allDate.indexOf(arrayList.get(0))).Click = false;

            if (allDate.size() > 0)
                allDate.get(0).Click = true;

            dateList.addAll(allDate);
            dateAdapter.notifyDataSetChanged();
            timesList.clear();

            if (allTime.size() == 0) {
                Toast.makeText(getActivity(), "زمان ارسال سفارش از سرور تعیین نشده است.", Toast.LENGTH_SHORT).show();
            } else {
                Date date = Calendar.getInstance().getTime();
                for (int i = 0; i < allTime.size(); i++) {
                    int hour;
                    try {
                        hour = Integer.parseInt(allTime.get(i).split("-")[0]);


                        if (hour > date.getHours())
                            if ((hour - date.getHours() == 1) && date.getMinutes() > 45) {
                            } else
                                timesList.add(allTime.get(i));


                    } catch (Exception ignore) {
                    }

                }
                times.clear();
                times.addAll(timesList);
                dialogTime.show();
            }
        });
        //endregion Configuration Time


        //region Configuration TypeOrder

        if (App.mode == 1) {
            binding.layoutTypeOrder.setVisibility(View.GONE);
        }



        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setReverseLayout(true);
        binding.recyclerViewOrderType.setLayoutManager(layoutManager);
        binding.recyclerViewOrderType.setScrollingTouchSlop(View.FOCUS_LEFT);
        orderTypePaymentAdapter = new OrderTypePaymentAdapter1(requireActivity(), OrdTList);
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
            } else {
                sumTransport = calculateTransport;
                binding.tvTransport.setText(format.format(sumTransport) + " ریال ");
                binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
            }




        });


        //endregion Configuration TypeOrder


        //region Configuration Payment
        if (App.mode == 1)
            binding.tvTitlePaymentPlace.setText("پرداخت در صندوق");


        binding.btnPaymentPlace.setOnClickListener(v -> {

            typePayment = "";
            binding.tvSuccessFullPayOnline.setText("");

            if (acc != null && acc.CRDT != null) {
                binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
            }

            binding.ivOkPaymentOnline.setVisibility(View.GONE);
            binding.ivOkPaymentPlace.setVisibility(View.VISIBLE);

        });


        binding.layoutPaymentOnline.setOnClickListener(v -> {

            binding.ivOkPaymentPlace.setVisibility(View.GONE);
            typePayment = "-1";

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


        });
        //endregion Configuration Payment


        //region Edit View
        if (edit) {
            binding.tvError.setVisibility(View.VISIBLE);
        }

        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

        //endregion Edit View

        binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");


        //region Configuration Send Order
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


        binding.btnRegisterOrder.setOnClickListener(v -> {

            if (typePayment.equals("-1")) {
                Toast.makeText(getActivity(), "نوع پرداخت را مشخص کنید.", Toast.LENGTH_SHORT).show();
                return;
            } else if (((ValidAddress.equals("ناموجود") || typeAddress == 0) && (App.mode == 2 || (App.mode == 1 && Tbl_GUID.equals("") && !Ord_TYPE.equals(OrderTypeApp))))) {
                Toast.makeText(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            } else if (Ord_TYPE == null || Ord_TYPE == -1) {
                Toast.makeText(getActivity(), "نوع سفارش را انتخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            } else if (timeChoose.equals("") && (App.mode == 2 || (App.mode == 1 && Tbl_GUID.equals("") && !Ord_TYPE.equals(OrderTypeApp)))) {
                Toast.makeText(getActivity(), "زمان ارسال سفارش را تعیین کنید", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tvMessage.setText("از ارسال سفارش اطمینان دارید ؟");
            }
            rlButtons.setVisibility(View.VISIBLE);
            btnReturned.setVisibility(View.GONE);

            if (App.mode == 2)
                dialogSendOrder.show();
            else
                getTable1();

        });


        btnNo.setOnClickListener(v -> dialogSendOrder.dismiss());


        btnOk.setOnClickListener(v -> {

            dialogSendOrder.dismiss();
            Date date = Calendar.getInstance().getTime();

            double sumPrice = 0;
            double sumDiscount = 0;
            double sumDiscountPercent = 0;
            double sumPurePrice = 0;


            InvoiceDetail invoiceDetailTransport = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "' AND INVDETDESCRIBTION ='" + "توزیع" + "'").first();


            if (invoiceDetailTransport == null) {
                invoiceDetailTransport = new InvoiceDetail();
                invoiceDetailTransport.INV_DET_UID = UUID.randomUUID().toString();
                invoiceDetailTransport.ROW_NUMBER = invDetails.size() + 1;
                invoiceDetailTransport.INV_UID = new_Inv_GUID;
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


                if (sumTransport!=0)
                InvoiceDetail.save(invoiceDetailTransport);
            } else {
                invoiceDetailTransport.INV_UID = new_Inv_GUID;
                invoiceDetailTransport.INV_DET_QUANTITY = 1.0;
                invoiceDetailTransport.INV_DET_PRICE_PER_UNIT = String.valueOf(sumTransport);
                invoiceDetailTransport.INV_DET_TOTAL_AMOUNT = String.valueOf(sumTransport);
                invoiceDetailTransport.save();
            }


            CollectionUtils.filter(invDetails, i -> (i.INV_DET_DESCRIBTION != null && !i.INV_DET_DESCRIBTION.equals("توزیع") || i.INV_DET_DESCRIBTION == null));
            for (int i = 0; i < invDetails.size(); i++) {

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
                    invoiceDtl.INV_UID = new_Inv_GUID;
                    invoiceDtl.INV_DET_PERCENT_DISCOUNT = product.getPercDis();
                    invoiceDtl.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                    invoiceDtl.INV_DET_PRICE_PER_UNIT = String.valueOf(product.getPrice());
                    sumDiscount = sumDiscount + discountPrice;
                    sumDiscountPercent = sumDiscountPercent + (product.getPercDis() / 100);


                    invoiceDtl.update();

                }

            }

            Invoice invoice = new Invoice();
            invoice.INV_UID = new_Inv_GUID;
            invoice.INV_TOTAL_AMOUNT = sumPrice + sumTransport;//جمع فاکنور
            invoice.INV_TOTAL_DISCOUNT = 0.0;
            invoice.INV_PERCENT_DISCOUNT = sumDiscountPercent * 100;
            invoice.INV_DET_TOTAL_DISCOUNT = sumDiscount;
            invoice.INV_DESCRIBTION = binding.edtDescription.getText().toString();
            invoice.INV_TOTAL_TAX = 0.0;
            invoice.INV_TOTAL_COST = 0.0;
            invoice.INV_EXTENDED_AMOUNT = sumPurePrice + sumTransport;
            invoice.INV_DATE = date;

            int hour = date.getHours();

            try {
                hour = Integer.parseInt(timeChoose.split("-")[0]);

            } catch (Exception e) {
            }


            invoice.INV_DUE_DATE = dateChoose;
            invoice.INV_DUE_TIME = hour + ":" + "00";
            invoice.INV_STATUS = true;

            invoice.ACC_CLB_UID = Select.from(Account.class).first().I;
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


            List<Invoice> listInvoice = new ArrayList<>();
            listInvoice.add(invoice);


            List<InvoiceDetail> invoiceDetailList = Select.from(InvoiceDetail.class).where("INVUID ='" + new_Inv_GUID + "'").list();


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
                    if (LauncherActivity.namePackage.equals("ir.kitgroup.salein")) {
                        replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF");
                    } else {
                        replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment");
                    }


                    replaceFragment.commit();
                }

            } else {

                if (Tbl_GUID.equals("")) {

                    Tables tables = new Tables();

                    tables.N = "بیرون بر";
                    tables.C = Ord_TYPE;
                    tables.ACT = false;
                    Account account = Select.from(Account.class).first();
                    tables.GO = account != null ? account.N : "";
                    tables.RSV = false;
                    tables.I = new_Inv_GUID;
                    tables.INVID = new_Inv_GUID;
                    Date date = Calendar.getInstance().getTime();
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    tables.DATE = dateFormats.format(date);
                    tables.save();


                }
                Account.deleteAll(Account.class);

                for (int i = 0; i < 4; i++) {
                    getFragmentManager().popBackStack();
                }
                LauncherOrganizationFragment launcherFragment = new LauncherOrganizationFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, launcherFragment, "LauncherFragment").addToBackStack("LauncherF").commit();

            }


        });


        //endregion  Configuration Send Order


        binding.ivBack.setOnClickListener(v -> getFragmentManager().popBackStack());


         getSetting1();

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
                    } catch (Exception ignore) {

                    }


                    if (message == 1) {

                        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" +
                                new_Inv_GUID + "'").list();
                        for (int i = 0; i < invoiceDetails.size(); i++) {
                            InvoiceDetail.delete(invoiceDetails.get(i));
                        }


                        Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                        if (tb == null) {
                            tb = new Tables();
                            tb.I = new_Tbl_GUID;
                        } else {
                            tb.I = Tbl_GUID;
                        }


                        tb.INVID = new_Inv_GUID;
                        tb.save();


                        tvMessage.setText("سفارش با موفقیت ارسال شد");

                        dialogSendOrder.show();
                    } else {
                        if (App.mode == 1) {
                            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + new_Inv_GUID + "'").list();
                            for (int i = 0; i < invDetails.size(); i++) {
                                InvoiceDetail invoiceDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invDetails.get(i).INV_DET_UID + "'").first();
                                invoiceDtl.INV_UID = Inv_GUID;
                                invoiceDtl.update();
                            }
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


                }
            });
        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در ارتباط" + ex.toString(), Toast.LENGTH_SHORT).show();

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


        try {
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
        } catch (Exception ignore) {

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

        }


        return priceTransport;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        OnceSee = false;
        reloadFragment(setADR1);
        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("PaymentFragment");
        FragmentManager ft = getActivity().getSupportFragmentManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            ft.beginTransaction().detach(frg).commitNow();
            ft.beginTransaction().attach(frg).commitNow();

        } else {

            ft.beginTransaction().detach(frg).attach(frg).commit();
        }
    }


    public Bundle reloadFragment(Boolean setAddress) {

        Bundle bundle = getArguments();
        bundle.putString("Inv_GUID", Inv_GUID);
        bundle.putString("Tbl_GUID", Tbl_GUID);

        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putString("Ord_TYPE", String.valueOf(Ord_TYPE));
        bundle.putString("Sum_PURE_PRICE", Sum_PURE_PRICE);
        bundle.putBoolean("edit", edit);
        bundle.putBoolean("setADR1", setAddress);
        return bundle;


    }


    @SuppressLint("SetTextI18n")
    private void getInquiryAccount1(String userName, String passWord, String mobile) {
        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    App.api.getInquiryAccount1(userName, passWord, mobile, "", "", 1, 1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelAccount>() {
                                }.getType();
                                ModelAccount iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است.", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                    return;
                                }


                                if (iDs != null)
                                    if (iDs.getAccountList() == null) {
                                        Type typeIDs0 = new TypeToken<ModelLog>() {
                                        }.getType();
                                        ModelLog iDs0 = gson.fromJson(jsonElement, typeIDs0);

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


                                    }
                                binding.progressBar.setVisibility(View.GONE);


                            }, throwable -> {

                                Toast.makeText(getActivity(), "خطا در بروز رسانی موجودی باشگاه", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getActivity(), "خطا در ارتباط با سرور در بروز رسانی موجودی باشگاه", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }

    }


    private void getSetting1() {
        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    App.api.getSetting1(userName, passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelSetting>() {
                                }.getType();

                                ModelSetting iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    error = error + "\n" + "مدل دریافت شده از تنظیمات نا معتبر است";
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                    return;
                                }

                                if (iDs == null) {
                                    error = error + "\n" + "لیست دریافت شده از تنظیمات نا معتبر می باشد";
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                    return;
                                } else {

                                    timesList.clear();

                                    List<Setting> settingsList = new ArrayList<>(iDs.getSettings());

                                    if (!settingsList.get(0).ORDER_TYPE_APP.equals("")){
                                        sharedPreferences.edit().putString("OrderTypeApp",settingsList.get(0).ORDER_TYPE_APP).apply();
                                        OrderTypeApp = Integer.parseInt(settingsList.get(0).ORDER_TYPE_APP);
                                    }



                                    if (!settingsList.get(0).SERVICE_DAY.equals(""))
                                        SERVICE_DAY = Integer.parseInt(settingsList.get(0).SERVICE_DAY);


                                    allDate.clear();
                                    Date dateNow = Calendar.getInstance().getTime();

                                    if (SERVICE_DAY == 0) {
                                        ModelDate modelDate = new ModelDate();
                                        modelDate.date = dateNow;
                                        modelDate.Click = true;
                                        allDate.add(modelDate);

                                    }
                                    for (int i = 0; i < SERVICE_DAY; i++) {
                                        Date date = Calendar.getInstance().getTime();
                                        date.setDate(date.getDate() + i);
                                        ModelDate modelDate = new ModelDate();
                                        modelDate.date = date;
                                        if (i == 0)
                                            modelDate.Click = true;
                                        else
                                            modelDate.Click = false;
                                        allDate.add(modelDate);

                                    }


                                    if (Tbl_GUID.equals("") && !Ord_TYPE.equals(OrderTypeApp) && App.mode == 1) {
                                        binding.layoutAddress.setVisibility(View.VISIBLE);
                                        binding.layoutTime.setVisibility(View.VISIBLE);

                                    } else if (App.mode == 1) {
                                        binding.layoutTypeOrder.setVisibility(View.GONE);
                                        binding.layoutAddress.setVisibility(View.GONE);
                                        binding.layoutTime.setVisibility(View.GONE);
                                        binding.layoutPayment.setVisibility(View.VISIBLE);
                                    }


                                    sharedPreferences.edit().putString("transportId", settingsList.get(0).PEYK).apply();
                                    try {
                                        allTime = new ArrayList<>(Arrays.asList(settingsList.get(0).SERVICE_TIME.split(",")));

                                    } catch (Exception ignore) {
                                    }


                                    getTypeOrder1();


                                }


                            }, throwable -> {
                                error = error + "\n" + "خطا در اتصال به سرور برای دریافت تنطیمات";
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            error = error + "\n" + "خطا در اتصال به سرور برای دریافت تنطیمات";
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }

    }


    @SuppressLint("SetTextI18n")
    private void getTypeOrder1() {


        binding.progressBar.setVisibility(View.VISIBLE);

        try {
            compositeDisposable.add(
                    App.api.getOrderType1(userName, passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                        Gson gson = new Gson();
                                        Type typeIDs = new TypeToken<ModelTypeOrder>() {
                                        }.getType();

                                        ModelTypeOrder iDs;
                                        try {
                                            iDs = gson.fromJson(jsonElement, typeIDs);
                                        } catch (Exception e) {


                                            error = error + "\n" + "مدل دریافت شده از نوع سفارش نا معتبر است";
                                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                            binding.progressBar.setVisibility(View.GONE);

                                            return;
                                        }

                                        if (iDs == null) {
                                            error = error + "\n" + "لیست دریافت شده از نوع سفارش نا معتبر می باشد";
                                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                            binding.progressBar.setVisibility(View.GONE);
                                            return;
                                        } else {
                                            CollectionUtils.filter(iDs.getOrderTypes(), i -> i.getTy() == 2);

                                                OrdTList.addAll(iDs.getOrderTypes());

                                             if (OrdTList.size() == 1) {
                                                OrdTList.get(0).Click = true;
                                                Ord_TYPE = OrdTList.get(0).getC();
                                                binding.tvTypeOrder.setVisibility(View.GONE);
                                                binding.recyclerViewOrderType.setVisibility(View.GONE);

                                             }
                                            orderTypePaymentAdapter.notifyDataSetChanged();







                                            if (Ord_TYPE.equals(OrderTypeApp)) {
                                                sumTransport = 0;
                                                binding.tvTransport.setText("0 ریال");
                                                binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");
                                            } else {
                                                if ((OrdTList.size() == 1 && App.mode == 2) || (App.mode == 1)) {
                                                    sumTransport = calculateTransport;
                                                    binding.tvTransport.setText(format.format(sumTransport) + " ریال ");
                                                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
                                                }

                                            }

                                        }


                                        binding.progressBar.setVisibility(View.GONE);

                                        //region Get Credit Club
                                        String name;
                                        try {
                                            if (!OnceSee && !LauncherActivity.name.equals("ir.kitgroup.saleinmeat"))
                                                getInquiryAccount1(userName, passWord, acc.M);
                                            else if (OnceSee && !LauncherActivity.name.equals("ir.kitgroup.saleinmeat"))
                                                binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
                                        } catch (Exception ignore) {
                                            getInquiryAccount1(userName, passWord, acc.M);
                                        }


                                        //endregion Get Credit Club


                                    }
                                    , throwable -> {
                                        error = error + "\n" + "خطای تایم اوت در دریافت نوع سفارش";
                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                        binding.progressBar.setVisibility(View.GONE);

                                    })
            );

        } catch (Exception e) {
            error = error + "\n" + "خطا در اتصال به سرور برای دریافت نوع سفارش";
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }


    }

    private void getTable1() {

        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    App.api.getTable1(userName, passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelTable>() {
                                }.getType();
                                ModelTable iDs = null;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception ignore) {
                                }
                                if (iDs != null) {
                                    ArrayList<Tables> Tables = new ArrayList<>(iDs.getTables());
                                    CollectionUtils.filter(Tables, t -> t.I.equals(Tbl_GUID));
                                    if (Tables.size() > 0 && Tables.get(0).RSV) {
                                        Toast.makeText(getActivity(), "این میز رزرو شده", Toast.LENGTH_SHORT).show();
                                        binding.progressBar.setVisibility(View.GONE);
                                        return;
                                    }

                                }

                                binding.progressBar.setVisibility(View.GONE);
                                dialogSendOrder.show();

                            }, throwable -> {
                                binding.progressBar.setVisibility(View.GONE);
                                dialogSendOrder.show();

                            })
            );
        } catch (Exception e) {
            binding.progressBar.setVisibility(View.GONE);
            dialogSendOrder.show();
        }

    }

}
