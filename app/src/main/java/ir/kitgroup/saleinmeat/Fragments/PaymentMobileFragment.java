package ir.kitgroup.saleinmeat.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleinmeat.Activities.LauncherActivity;
import ir.kitgroup.saleinmeat.Adapters.DateAdapter;
import ir.kitgroup.saleinmeat.Adapters.OrderTypePaymentAdapter1;
import ir.kitgroup.saleinmeat.Adapters.TimeAdapter;
import ir.kitgroup.saleinmeat.Connect.MyViewModel;
import ir.kitgroup.saleinmeat.DataBase.Product;
import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.classes.Util;
import ir.kitgroup.saleinmeat.classes.Utilities;
import ir.kitgroup.saleinmeat.DataBase.Company;
import ir.kitgroup.saleinmeat.models.Config;
import ir.kitgroup.saleinmeat.models.ModelDate;
import ir.kitgroup.saleinmeat.models.Setting;
import ir.kitgroup.saleinmeat.models.PaymentRecieptDetail;
import ir.kitgroup.saleinmeat.classes.CustomProgress;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.models.Invoice;
import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.models.OrderType;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.databinding.FragmentPaymentMobileBinding;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

@AndroidEntryPoint
public class PaymentMobileFragment extends Fragment {
    @Inject
    Config config;

    @Inject
    SharedPreferences sharedPreferences;

    private MyViewModel myViewModel;
    private FragmentPaymentMobileBinding binding;

    private Company company;
    private String linkPayment = "";
    private final Boolean Seen = false;
    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync

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
    private Boolean setADR1 = false;
    private double calculateTransport = 0.0;
    //endregion Dialog Address

    private String typePayment = "-1";
    private CustomProgress customProgress;
    private String numberPos = "";

    private Dialog dialogSendOrder;
    private RelativeLayout rlButtons;
    private MaterialButton btnReturned;
    private TextView tvMessage;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    private String Sum_PURE_PRICE = "0";
    private List<InvoiceDetail> invDetails;
    private Integer Ord_TYPE = -1;
    private String Tbl_GUID;
    private String Tbl_NAME;
    private String new_Tbl_GUID;
    private String Inv_GUID;
    private String new_Inv_GUID;
    private Boolean edit = false;
    private Integer paymentType = 0;
    private Integer OrderTypeApp = 0;
    private Integer SERVICE_DAY = 0;
    private String timeChoose = "";
    private boolean disableAccount = false;
    private double sumTransport = 0.0;
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
    private String Transport_GUID;


    public PaymentMobileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "RestrictedApi", "NotifyDataSetChanged", "StaticFieldLeak"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            company = Select.from(Company.class).first();
            customProgress = CustomProgress.getInstance();
            Transport_GUID = sharedPreferences.getString("Transport_GUID", "");

            OrdTList = new ArrayList<>();
            timesList = new ArrayList<>();
            times = new ArrayList<>();
            allTime = new ArrayList<>();
            allDate = new ArrayList<>();
            dateList = new ArrayList<>();

            Date dateNow = Calendar.getInstance().getTime();
            dateChoose = dateNow;

            if (company.mode == 1) {
                numberPos = Select.from(Company.class).first().numberPos;
                if (numberPos != null && numberPos.equals(""))
                    numberPos = "0";
            }

            //region get Bundle
            Bundle bundle = getArguments();
            Inv_GUID = bundle.getString("Inv_GUID");
            Tbl_GUID = bundle.getString("Tbl_GUID");
            Tbl_NAME = bundle.getString("Tbl_NAME");
            Sum_PURE_PRICE = bundle.getString("Sum_PRICE");
            edit = bundle.getBoolean("EDIT");
            setADR1 = bundle.getBoolean("setADR");
            if (!setADR1)
                setADR1 = bundle.getBoolean("setADR1");
            String ord_type = bundle.getString("Ord_TYPE");
            if (ord_type != null && !ord_type.equals(""))
                Ord_TYPE = Integer.parseInt(ord_type);
            if (!edit && Tbl_GUID.equals(""))
                new_Tbl_GUID = UUID.randomUUID().toString();
            else
                new_Tbl_GUID = Tbl_GUID;


            if (company.mode == 2 || (company.mode == 1 && edit) || (company.mode == 1 && Tbl_GUID.equals("")))
                new_Inv_GUID = Inv_GUID;

            else if (company.mode == 1 && !Tbl_GUID.equals(""))
                new_Inv_GUID = UUID.randomUUID().toString();
            //endregion get Bundle
            if (company.mode == 1) {
                binding.layoutTypeOrder.setVisibility(View.GONE);
                binding.layoutAddress.setVisibility(View.GONE);
                binding.layoutTime.setVisibility(View.GONE);
                binding.layoutPayment.setVisibility(View.VISIBLE);
            }

            //region Configuration Size
            int fontBigSize;
            int fontSize;
            if (Util.screenSize >= 7) {
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


            //region Cast Variable Dialog Sync
            dialogSync = new Dialog(getActivity());
            dialogSync.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogSync.setContentView(R.layout.custom_dialog);
            dialogSync.setCancelable(false);

            textMessageDialog = dialogSync.findViewById(R.id.tv_message);
            btnOkDialog = dialogSync.findViewById(R.id.btn_ok);
            btnNoDialog = dialogSync.findViewById(R.id.btn_cancel);
            btnNoDialog.setOnClickListener(v -> {
                dialogSync.dismiss();
                if (disableAccount && company.mode==2) {
                    getActivity().finish();
                }
            });

            btnOkDialog.setOnClickListener(v -> {
                dialogSync.dismiss();
                binding.progressBar.setVisibility(View.VISIBLE);
                myViewModel.getSetting(company.USER, company.PASS);

            });

            //endregion Cast Variable Dialog Sync


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
                    latitude1 = Double.parseDouble(acc.LAT != null && !acc.LAT.equals("") && !acc.LAT.equals("-") ? acc.LAT : "0.0");
                    longitude1 = Double.parseDouble(acc.LNG != null && !acc.LNG.equals("") && !acc.LNG.equals("-") ? acc.LNG : "0.0");
                    if (latitude1 == 0.0 && longitude1 == 0.0) {
                        Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    double distance = getDistanceMeters(new LatLng(latitude1, longitude1), new LatLng(Double.parseDouble(company.LAT), Double.parseDouble(company.LONG)));
                    double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                    if (price == -1.0) {
                        Toast.makeText(getActivity(), "سفارش خارج از محدوده است.", Toast.LENGTH_SHORT).show();
                        dialogAddress.dismiss();
                        return;
                    } else {
                        calculateTransport = price;
                        if (Ord_TYPE.equals(OrderTypeApp)) {
                            sumTransport = 0;
                            binding.layoutPeyk.setVisibility(View.GONE);
                            binding.tvTransport.setText("0 ریال");
                            binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");
                        } else {
                            sumTransport = calculateTransport;
                            binding.layoutPeyk.setVisibility(View.VISIBLE);
                            binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + calculateTransport) + "ریال");
                            binding.tvTransport.setText(format.format(calculateTransport) + " ریال ");
                        }
                    }
                    typeAddress = 1;
                    ValidAddress = radioAddress1.getText().toString();
                    binding.edtAddress.setText(ValidAddress);
                    dialogAddress.dismiss();
                }
            });

            radioAddress2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    latitude2 = Double.parseDouble(acc.LAT1 != null && !acc.LAT1.equals("") && !acc.LAT1.equals("-") ? acc.LAT1 : "0.0");
                    longitude2 = Double.parseDouble(acc.LNG1 != null && !acc.LNG1.equals("") && !acc.LNG1.equals("-") ? acc.LNG1 : "0.0");
                    if (latitude2 == 0.0 || longitude2 == 0.0) {
                        Toast.makeText(getActivity(), "آدرس خود را مجدد ثبت کنید ، طول و عرض جغرافیایی ثبت نشده است.", Toast.LENGTH_LONG).show();

                        return;
                    }
                    double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(Double.parseDouble(company.LAT), Double.parseDouble(company.LONG)));
                    double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                    if (price == -1.0) {
                        Toast.makeText(getActivity(), "سفارش خارج از محدوده است.", Toast.LENGTH_SHORT).show();
                        dialogAddress.dismiss();
                        if (company.mode == 2)
                            binding.tvTypeOrder.setVisibility(View.GONE);
                        return;
                    } else {
                        if (Ord_TYPE.equals(OrderTypeApp)) {
                            sumTransport = 0;
                            binding.layoutPeyk.setVisibility(View.GONE);
                            binding.tvTransport.setText("0 ریال");
                            binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");
                        } else {
                            sumTransport = calculateTransport;
                            binding.layoutPeyk.setVisibility(View.VISIBLE);
                            binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + calculateTransport) + "ریال");
                            binding.tvTransport.setText(format.format(calculateTransport) + " ریال ");
                        }
                    }
                    typeAddress = 2;
                    ValidAddress = radioAddress2.getText().toString();
                    binding.edtAddress.setText(ValidAddress);
                    dialogAddress.dismiss();
                }
            });

            btnNewAddress.setOnClickListener(v -> {
                if (acc == null) {
                    Toast.makeText(getActivity(), "مشتری نامعتبر است", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogAddress.dismiss();
                Bundle bundleMap = new Bundle();
                bundleMap.putString("mobileNumber", "");
                bundleMap.putString("edit_address", "1");
                bundleMap.putString("type", String.valueOf(typeAddress));
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundleMap);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").addToBackStack("MapF").commit();
            });
            //endregion Cast DialogAddress


            //region SetAddress
            acc = Select.from(Account.class).first();
            if (!linkPayment.equals(""))
                linkPayment = linkPayment + "/ChargeClub?c=" + acc.getC();

            if (acc != null && acc.ADR != null && !acc.ADR.equals("")) {
                String address = "";
                latitude1 = Double.parseDouble(acc.LAT != null && !acc.LAT.equals("") && !acc.LAT.equals("-") ? acc.LAT : "0.0");
                longitude1 = Double.parseDouble(acc.LNG != null && !acc.LNG.equals("") && !acc.LNG.equals("-") ? acc.LNG : "0.0");

                if (latitude1 != 0.0)
                    address = acc.ADR;
                radioAddress1.setText(address);
            }

            if (acc != null && acc.ADR2 != null && !acc.ADR2.equals("")) {
                String address = "";
                latitude2 = Double.parseDouble(acc.LAT1 != null && !acc.LAT1.equals("") && !acc.LAT1.equals("-") ? acc.LAT1 : "0.0");
                longitude2 = Double.parseDouble(acc.LNG1 != null && !acc.LNG1.equals("") && !acc.LNG1.equals("-") ? acc.LNG1 : "0.0");
                if (latitude2 != 0.0)
                    address = acc.ADR2;

                radioAddress2.setText(address);
            }

            if (acc != null && acc.ADR != null && !acc.ADR.equals("") && latitude1 != 0.0 && longitude1 != 0.0 && !setADR1) {
                latitude1 = Double.parseDouble(acc.LAT != null && !acc.LAT.equals("") && !acc.LAT.equals("-") ? acc.LAT : "0.0");
                longitude1 = Double.parseDouble(acc.LNG != null && !acc.LNG.equals("") && !acc.LNG.equals("-") ? acc.LNG : "0.0");

                double distance = getDistanceMeters(new LatLng(latitude1, longitude1), new LatLng(Double.parseDouble(company.LAT), Double.parseDouble(company.LONG)));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price != -1.0) {
                    binding.tvError.setText("");
                    binding.tvError.setVisibility(View.GONE);
                    calculateTransport = price;
                }
                String address = "";

                if (latitude1 != 0.0)
                    address = acc.ADR;

                binding.edtAddress.setText(address);
                typeAddress = 1;
                ValidAddress = address;

            } else if (acc != null && acc.ADR2 != null && !acc.ADR2.equals("") && latitude2 != 0.0 && longitude2 != 0.0) {
                latitude2 = Double.parseDouble(acc.LAT1 != null && !acc.LAT1.equals("") && !acc.LAT1.equals("-") ? acc.LAT1 : "0.0");
                longitude2 = Double.parseDouble(acc.LNG1 != null && !acc.LNG1.equals("") && !acc.LNG1.equals("-") ? acc.LNG1 : "0.0");
                double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(Double.parseDouble(company.LAT), Double.parseDouble(company.LONG)));
                double price = PriceTransport(distance / 1000, Double.parseDouble(Sum_PURE_PRICE));
                if (price != -1.0) {
                    binding.tvError.setText("");
                    binding.tvError.setVisibility(View.GONE);
                    calculateTransport = price;
                }
                String address = "";
                if (latitude2 != 0.0)
                    address = acc.ADR2;

                binding.edtAddress.setText(address);
                typeAddress = 2;
                ValidAddress = address;


            } else {
                typeAddress = 0;
                ValidAddress = "ناموجود";
            }

            binding.layoutAddress.setOnClickListener(v -> {

                if (latitude1 != 0.0 && latitude2 != 0.0) {
                    dialogAddress.show();
                    return;
                } else if (acc == null) {
                    Toast.makeText(getActivity(), "مشتری نامعتبر است", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundleMap = new Bundle();
                bundleMap.putString("mobileNumber", "");
                bundleMap.putString("edit_address", "1");
                bundleMap.putString("type", String.valueOf(typeAddress));
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundleMap);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").addToBackStack("MapF").commit();
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
                                if ((hour - date1.getHours() != 1) || date1.getMinutes() <= 45) {
                                    timesList.add(allTime.get(i));
                                }
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

                if (allTime.size() == 0 || allTime.get(0).equals("")) {
                    Toast.makeText(getActivity(), "زمان ارسال سفارش از سرور تعیین نشده است.", Toast.LENGTH_SHORT).show();
                } else {
                    Date date = Calendar.getInstance().getTime();
                    for (int i = 0; i < allTime.size(); i++) {
                        int hour;
                        try {
                            hour = Integer.parseInt(allTime.get(i).split("-")[0]);
                            if (hour > date.getHours())
                                if ((hour - date.getHours() != 1) || date.getMinutes() <= 45) {
                                    timesList.add(allTime.get(i));
                                }
                        } catch (Exception ignore) {
                        }

                    }
                    times.clear();
                    times.addAll(timesList);
                    timeAdapter.notifyDataSetChanged();
                    dialogTime.show();
                }
            });
            //endregion Configuration Time


            //region Configuration TypeOrder
            if (company.mode == 1) {
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
                typePayment = "";
                binding.tvSuccessFullPayOnline.setText("");
                binding.ivOkPaymentOnline.setVisibility(View.GONE);
                binding.ivPaymentPlace.setVisibility(View.GONE);
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
                    binding.layoutPeyk.setVisibility(View.GONE);
                    binding.tvTransport.setText("0 ریال");
                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");
                } else {
                    sumTransport = calculateTransport;
                    binding.layoutPeyk.setVisibility(View.VISIBLE);
                    binding.tvTransport.setText(format.format(sumTransport) + " ریال ");
                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
                }
            });

            //endregion Configuration TypeOrder
            //region Configuration Payment
            if (company.mode == 1)
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
                } else if (!linkPayment.equals("")) {
                    Uri uri = Uri.parse(linkPayment);
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
            if (edit)
                binding.tvError.setVisibility(View.VISIBLE);

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
            rlButtons = dialogSendOrder.findViewById(R.id.layoutButtons);
            btnReturned = dialogSendOrder.findViewById(R.id.btn_returned);
            MaterialButton btnOk = dialogSendOrder.findViewById(R.id.btn_ok);
            MaterialButton btnNo = dialogSendOrder.findViewById(R.id.btn_cancel);

            binding.btnRegisterOrder.setOnClickListener(v -> {
                if (Select.from(Account.class).first().getM() != null)
                    myViewModel.getInquiryAccount(company.USER, company.PASS, acc.getM());
                if (typePayment.equals("-1")) {
                    Toast.makeText(getActivity(), "نوع پرداخت را مشخص کنید.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (((ValidAddress.equals("ناموجود") || typeAddress == 0) && (company.mode == 2 || (company.mode == 1 && Tbl_GUID.equals("") && !Ord_TYPE.equals(OrderTypeApp))))) {
                    Toast.makeText(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Ord_TYPE == null || Ord_TYPE == -1) {
                    Toast.makeText(getActivity(), "نوع سفارش را انتخاب کنید", Toast.LENGTH_SHORT).show();
                    return;
                } else if (allTime.size() > 0 && !allTime.get(0).equals("") && timeChoose.equals("") && (company.mode == 2 || (company.mode == 1 && Tbl_GUID.equals("") && !Ord_TYPE.equals(OrderTypeApp)))) {
                    Toast.makeText(getActivity(), "زمان ارسال سفارش را تعیین کنید", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    tvMessage.setText("از ارسال سفارش اطمینان دارید ؟");
                }

                rlButtons.setVisibility(View.VISIBLE);
                btnReturned.setVisibility(View.GONE);

                if (company.mode == 2)
                    dialogSendOrder.show();
                else {

                    myViewModel.getTable(company.USER, company.PASS);
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            });

            btnNo.setOnClickListener(v -> dialogSendOrder.dismiss());

            btnOk.setOnClickListener(v -> {
                dialogSendOrder.dismiss();
                Date date = Calendar.getInstance().getTime();

                double sumPrice = 0;
                double sumDiscount = 0;
                double sumDiscountPercent = 0;
                double sumPurePrice = 0;

                InvoiceDetail invoiceDetailTransport = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID.toLowerCase() + "' AND PRDUID ='" + Transport_GUID.toLowerCase() + "'").first();

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

                    if (!Transport_GUID.equals("")) {
                        invoiceDetailTransport.PRD_UID = Transport_GUID;
                    } else {
                        Toast.makeText(getActivity(), "خطا در ارسال مبلغ توزیع", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (sumTransport != 0)
                        InvoiceDetail.save(invoiceDetailTransport);
                } else {
                    invoiceDetailTransport.INV_UID = new_Inv_GUID;
                    invoiceDetailTransport.INV_DET_QUANTITY = 1.0;
                    invoiceDetailTransport.INV_DET_PRICE_PER_UNIT = String.valueOf(sumTransport);
                    invoiceDetailTransport.INV_DET_TOTAL_AMOUNT = String.valueOf(sumTransport);

                    if (sumTransport != 0)
                        invoiceDetailTransport.save();
                    else
                        InvoiceDetail.delete(invoiceDetailTransport);
                }
                CollectionUtils.filter(invDetails, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
                for (int i = 0; i < invDetails.size(); i++) {
                    ir.kitgroup.saleinmeat.DataBase.Product product = Select.from(ir.kitgroup.saleinmeat.DataBase.Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();
                    InvoiceDetail invoiceDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invDetails.get(i).INV_DET_UID + "'").first();
                    if (product != null) {
                        double sumTotalPrice = (invoiceDtl.INV_DET_QUANTITY * product.getPrice(sharedPreferences));//جمع کل ردیف
                        double discountPrice;//جمع تخفیف ردیف
                        if (Seen)
                            discountPrice = sumTotalPrice * (invoiceDtl.INV_DET_PERCENT_DISCOUNT / 100);
                        else
                            discountPrice = sumTotalPrice * (product.getPercDis() / 100);
                        double totalPurePrice = sumTotalPrice - discountPrice;//جمع خالص ردیف
                        sumPrice = sumPrice + sumTotalPrice;//جمع کل فاکتور
                        sumPurePrice = sumPurePrice + totalPurePrice;//جمع خالص فاکتور
                        invoiceDtl.INV_DET_TOTAL_AMOUNT = String.valueOf(totalPurePrice);
                        invoiceDtl.ROW_NUMBER = i + 1;
                        invoiceDtl.INV_UID = new_Inv_GUID;
                        if (!Seen)
                            invoiceDtl.INV_DET_PERCENT_DISCOUNT = product.getPercDis();

                        invoiceDtl.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                        invoiceDtl.INV_DET_PRICE_PER_UNIT = String.valueOf(product.getPrice(sharedPreferences));
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

                } catch (Exception ignored) {
                }
                invoice.INV_DUE_DATE = dateChoose;
                invoice.INV_DUE_TIME = hour + ":" + "00";
                invoice.INV_STATUS = true;
                if (Select.from(Account.class).first() == null) {
                    Toast.makeText(getActivity(), "مشتری معتبر نمی باشد", Toast.LENGTH_SHORT).show();
                    return;
                }
                invoice.ACC_CLB_UID = Select.from(Account.class).first().I;
                invoice.TBL_UID = Tbl_GUID;
                invoice.INV_TYPE_ORDER = Ord_TYPE;
                if (typeAddress == 1) {
                    invoice.ACC_CLB_ADDRESS = "";
                    invoice.ACC_CLB_DEFAULT_ADDRESS = "1";
                } else if (typeAddress == 2) {
                    invoice.ACC_CLB_ADDRESS2 = "";
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

                customProgress.showProgress(getActivity(), "در حال ارسال سفارش...", false);
                myViewModel.sendOrder(company.USER, company.PASS, listInvoice, invoiceDetailList, clsPaymentRecieptDetails, numberPos);


            });

            btnReturned.setOnClickListener(v -> {
                dialogSendOrder.dismiss();
                int size = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < size; i++) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                ((LauncherActivity) getActivity()).setFistItem();
                ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);
                if (company.mode == 2) {
                    Bundle bundleMainOrder = new Bundle();
                    bundleMainOrder.putString("Inv_GUID", "");
                    bundleMainOrder.putString("Tbl_GUID", "");
                    bundleMainOrder.putString("Ord_TYPE", "");
                    MainOrderFragment mainOrderFragment = new MainOrderFragment();
                    mainOrderFragment.setArguments(bundleMainOrder);
                    FragmentTransaction addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment");
                    if (config.INSKU_ID.equals("ir.kitgroup.salein"))
                        addFragment.addToBackStack("MainOrderF").commit();
                    else
                        addFragment.commit();
                } else {
                    if (Tbl_GUID.equals("")) {
                        Tables tables = new Tables();
                        tables.N = Tbl_NAME;
                        tables.C = Ord_TYPE;
                        tables.ACT = false;
                        Account account = Select.from(Account.class).first();
                        tables.GO = account.N != null ? account.N : "فروش روزانه";
                        tables.RSV = false;
                        tables.I = new_Inv_GUID;
                        tables.INVID = new_Inv_GUID;
                        Date date = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        tables.DATE = dateFormats.format(date);
                        tables.save();
                    }

                    LauncherOrganizationFragment.refresh = true;
                    Account.deleteAll(Account.class);
                    LauncherOrganizationFragment launcherFragment = new LauncherOrganizationFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, launcherFragment, "LauncherFragment").addToBackStack("LauncherF").commit();
                }
            });
            //endregion  Configuration Send Order
            binding.ivBack.setOnClickListener(v -> getFragmentManager().popBackStack());

        } catch (Exception ignore) {
        }
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getSetting(company.USER, company.PASS);

        myViewModel.getResultSendOrder().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            myViewModel.getResultSendOrder().setValue(null);
            int message = 0;
            String description = "";
            if (result != null) {
                message = result.getLogs().get(0).getMessage();
                description = result.getLogs().get(0).getDescription();
            }
            rlButtons.setVisibility(View.GONE);
            btnReturned.setVisibility(View.VISIBLE);
            if (message == 1) {
                String name;
                name = company.INSK_ID.split("ir.kitgroup.")[1];
                if (!edit || company.mode == 1) {
                    sharedPreferences.edit().putString(name, "").apply();
                    sharedPreferences.edit().putString(Inv_GUID, "").apply();
                }
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

            } else {
                if (company.mode == 1) {
                    List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + new_Inv_GUID + "'").list();
                    for (int i = 0; i < invDetails.size(); i++) {
                        InvoiceDetail invoiceDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invDetails.get(i).INV_DET_UID + "'").first();
                        invoiceDtl.INV_UID = Inv_GUID;
                        invoiceDtl.update();
                    }
                }

                Product.deleteAll(Product.class);
                tvMessage.setText("خطا در ارسال ،" + "\n" + description);

            }
            customProgress.hideProgress();
            dialogSendOrder.show();

        });
        myViewModel.getResultTable().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;


            if (result != null) {
                myViewModel.getResultTable().setValue(null);
                ArrayList<Tables> Tables = new ArrayList<>(result.getTables());
                CollectionUtils.filter(Tables, t -> t.I.equals(Tbl_GUID));
                if (Tables.size() > 0 && Tables.get(0).RSV) {
                    Toast.makeText(getActivity(), "این میز رزرو شده", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    return;
                }
            }

            binding.progressBar.setVisibility(View.GONE);
            dialogSendOrder.show();
        });
        myViewModel.getResultTypeOrder().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;


            myViewModel.getResultTypeOrder().setValue(null);


            CollectionUtils.filter(result.getOrderTypes(), i -> i.getTy() == 2);
            OrdTList.addAll(result.getOrderTypes());

            if (OrdTList.size() == 1 && company.mode == 2) {
                OrdTList.get(0).Click = true;
                Ord_TYPE = OrdTList.get(0).getC();
                binding.tvTypeOrder.setVisibility(View.GONE);
                binding.recyclerViewOrderType.setVisibility(View.GONE);

            }


            orderTypePaymentAdapter.notifyDataSetChanged();
            if (Ord_TYPE.equals(OrderTypeApp)) {
                sumTransport = 0;
                binding.layoutPeyk.setVisibility(View.GONE);
                binding.tvTransport.setText("0 ریال");
                binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE)) + "ریال");
            } else {
                if ((OrdTList.size() == 1 && (company.mode == 2) || (Tbl_GUID.equals("") && !Ord_TYPE.equals(OrderTypeApp) && company.mode == 1))) {
                    sumTransport = calculateTransport;
                    binding.layoutPeyk.setVisibility(View.VISIBLE);
                    binding.tvTransport.setText(format.format(sumTransport) + " ریال ");
                    binding.tvSumPurePrice.setText(format.format(Double.parseDouble(Sum_PURE_PRICE) + sumTransport) + "ریال");
                }
            }


            //region Get Credit Club
            try {
                if (!OnceSee && Select.from(Account.class).first().getM() != null)
                    myViewModel.getInquiryAccount(company.USER, company.PASS, acc.M);
                else if (OnceSee) {
                    binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
                    binding.progressBar.setVisibility(View.GONE);
                } else
                    binding.progressBar.setVisibility(View.GONE);
            } catch (Exception ignore) {
            }
            //endregion Get Credit Club

        });
        myViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            myViewModel.getResultSetting().setValue(null);

            timesList.clear();
            List<Setting> settingsList = new ArrayList<>(result);
            if (!settingsList.get(0).ORDER_TYPE_APP.equals("")) {
                sharedPreferences.edit().putString("OrderTypeApp", settingsList.get(0).ORDER_TYPE_APP).apply();
                OrderTypeApp = Integer.parseInt(settingsList.get(0).ORDER_TYPE_APP);
                try {
                    paymentType = Integer.parseInt(settingsList.get(0).PAYMENT_TYPE);
                } catch (Exception ignored) {
                    paymentType = 3;
                }
                if (((company.mode == 2)
                        ||
                        (!Ord_TYPE.equals(OrderTypeApp) &&
                                Tbl_GUID.equals("") &&
                                company.mode == 1))
                        &&
                        paymentType == 1) {

                    binding.btnPaymentPlace.setVisibility(View.VISIBLE);
                    binding.layoutPaymentOnline.setVisibility(View.GONE);

                } else if (((company.mode == 2)
                        ||
                        (!Ord_TYPE.equals(OrderTypeApp) &&
                                Tbl_GUID.equals("") &&
                                company.mode == 1))
                        &&
                        paymentType == 2) {
                    binding.btnPaymentPlace.setVisibility(View.GONE);
                    binding.layoutPaymentOnline.setVisibility(View.VISIBLE);
                } else {
                    binding.btnPaymentPlace.setVisibility(View.VISIBLE);
                    binding.layoutPaymentOnline.setVisibility(View.VISIBLE);
                }
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
                modelDate.Click = i == 0;
                allDate.add(modelDate);
            }
            if (Tbl_GUID.equals("") && !Ord_TYPE.equals(OrderTypeApp) && company.mode == 1) {
                binding.layoutAddress.setVisibility(View.VISIBLE);

            } else if (company.mode == 1) {
                binding.layoutTypeOrder.setVisibility(View.GONE);
                binding.layoutAddress.setVisibility(View.GONE);
                binding.layoutTime.setVisibility(View.GONE);
                binding.layoutPayment.setVisibility(View.VISIBLE);
            }


            Transport_GUID = settingsList.get(0).PEYK;
            sharedPreferences.edit().putString("Transport_GUID", settingsList.get(0).PEYK).apply();
            try {
                allTime = new ArrayList<>(Arrays.asList(settingsList.get(0).SERVICE_TIME.split(",")));

            } catch (Exception ignore) {
            }
            if (allTime.size() > 0 && !allTime.get(0).equals("") && (company.mode == 2 || (!Ord_TYPE.equals(OrderTypeApp) &&
                    Tbl_GUID.equals("") &&
                    company.mode == 1)))
                binding.layoutTime.setVisibility(View.VISIBLE);


            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getTypeOrder(company.USER, company.PASS);


        });
        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;


            myViewModel.getResultInquiryAccount().setValue(null);
            disableAccount = false;
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();
            //user is register
            if (result.size() > 0) {
                Account.deleteAll(Account.class);
                Account.saveInTx(result);
                acc = Select.from(Account.class).first();
                if (acc != null && acc.CRDT != null)
                    binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
                binding.tvCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
            } else {
                binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.red_table));
                binding.tvCredit.setText("خطا در بروز رسانی موجودی ");
            }

            OnceSee = true;
            binding.progressBar.setVisibility(View.GONE);

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            binding.progressBar.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;

            myViewModel.getResultMessage().setValue(null);
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
            disableAccount = sharedPreferences.getBoolean("disableAccount", false);
            if (disableAccount && company.mode==2)
                showError(result.getName());


        });

    }

    public static class JsonObject {
        public List<Invoice> Invoice;
        public List<InvoiceDetail> InvoiceDetail;
        public List<PaymentRecieptDetail> PaymentRecieptDetail;
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
            if ("ir.kitgroup.saleinmeat".equals(company.INSK_ID)) {
                if (SumPurePrice > 2000000) {
                    priceTransport = 0.0;
                } else {
                    priceTransport = 100000;
                }
            } else {
                if ((distance == 0 || 0 < distance) && distance <= 1) {
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
        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("PaymentMobileFragment");
        FragmentManager ft = getActivity().getSupportFragmentManager();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ft.beginTransaction().detach(frg).commitNow();
                ft.beginTransaction().attach(frg).commitNow();

            } else {
                ft.beginTransaction().detach(frg).attach(frg).commit();
            }

        } catch (Exception ignored) {
            ft.beginTransaction().detach(frg).attach(frg).commit();
        }
    }

    private void reloadFragment(Boolean setAddress) {
        Bundle bundle = getArguments();
        bundle.putString("Inv_GUID", Inv_GUID);
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putBoolean("Seen", Seen);
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putString("Ord_TYPE", String.valueOf(Ord_TYPE));
        bundle.putString("Sum_PURE_PRICE", Sum_PURE_PRICE);
        bundle.putBoolean("edit", edit);
        bundle.putBoolean("setADR1", setAddress);
    }


    private void showError(String error) {
        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        dialogSync.dismiss();
        btnOkDialog.setVisibility(View.GONE);
        dialogSync.setCancelable(false);
        dialogSync.show();
    }

    public Bundle getBundle(boolean SetARD1) {
        Bundle bundle = new Bundle();
        bundle.putString("Inv_GUID", Inv_GUID);
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putString("Tbl_NAME", Tbl_NAME);
        bundle.putString("Ord_TYPE", String.valueOf(Ord_TYPE));
        bundle.putString("Sum_PRICE", Sum_PURE_PRICE);
        bundle.putBoolean("EDIT", edit);
        if (!SetARD1)
            bundle.putBoolean("setADR", setADR1);
        else
            bundle.putBoolean("setADR", SetARD1);
        return bundle;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

}