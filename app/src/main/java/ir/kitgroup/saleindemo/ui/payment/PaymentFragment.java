package ir.kitgroup.saleindemo.ui.payment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
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
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.DataBase.Product;
import ir.kitgroup.saleindemo.classes.Utilities;
import ir.kitgroup.saleindemo.DataBase.Company;

import ir.kitgroup.saleindemo.databinding.FragmentPaymentBinding;
import ir.kitgroup.saleindemo.models.ModelDate;
import ir.kitgroup.saleindemo.models.Setting;
import ir.kitgroup.saleindemo.models.PaymentRecieptDetail;
import ir.kitgroup.saleindemo.classes.CustomProgress;
import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.models.Invoice;
import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;
import ir.kitgroup.saleindemo.models.OrderType;
import ir.kitgroup.saleindemo.R;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

@AndroidEntryPoint
public class PaymentFragment extends Fragment {


    //region Parameter

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentPaymentBinding binding;
    private MyViewModel myViewModel;
    private Company company;
    private String userName;
    private String passWord;

    private String linkPayment = "";//It Is For Payment That Get From Server
    private CustomProgress customProgress;
    private String typePayment = "-1";// Default Amount For Payment Is -1 Because If User Do'nt Choose Type Of Payment We Can Show He/She Error Toast
    private Integer Ord_TYPE = -1;// Default Amount For OrderType Is -1 Because If User Do'nt Choose Type Of Order We Can Show He/She Error Toast

    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync

    //region Dialog Address
    private Users user;
    private Dialog dialogAddress;
    private RadioButton radioAddress1;
    private RadioButton radioAddress2;
    private int typeAddress = 0;//If This Variable Equal 1 We Send Address1 As Address Order Else If This Variable Equal 2  We Send Address2
    private String ValidAddress = "";
    private Double latitude1 = 0.0;
    private Double longitude1 = 0.0;
    private Double latitude2 = 0.0;
    private Double longitude2 = 0.0;
    public static boolean ChooseAddress2 = false;
    private double calculateTransport = 0.0;
    //endregion Dialog Address

    //region Dialog Send  Order
    private Dialog dialogSendOrder;
    private RelativeLayout rlButtons;
    private MaterialButton btnReturned;
    private TextView tvMessage;
    //endregion Dialog Send  Order


    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    private double sumPurePrice =0;
    private List<InvoiceDetail> invDetails;
    private String Inv_GUID;
    private Integer ViewOfPayment = 0;//If This Variable Equal 1 We Only Show On_Site Payment Card If This Variable Equal 2 We Only Show Club Pay Card If This Variable Equal 3 We Can Show Both Pay Cards
    private Integer OrderTypeApp = 0;//This Variable Get From Server And Show What Type Of Order Is Free Or Without Transport Cost
    private Integer SERVICE_DAY = 0;//This Variable Show Service days Of Company And Get From Server
    private String timeChoose = "";//This Variable Show Service Times Per Day Of Company And Get From Server
    private boolean disableAccount = false;
    private double sumTransport = 0;
    //region Time Dialog
    private Dialog dialogTime;
    private TimeAdapter timeAdapter;
    private DateAdapter dateAdapter;
    private ArrayList<String> timesList;
    private ArrayList<String> times;
    private ArrayList<String> allTime;
    private ArrayList<ModelDate> allDate;
    private ArrayList<ModelDate> dateList;
    private Date dateChoose;
    //endregion Time Dialog


    private OrderTypeOrderAdapter orderTypeOrderAdapter;
    private List<OrderType> OrdTList;
    private String Transport_GUID;
    //endregion Parameter


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "RestrictedApi", "NotifyDataSetChanged", "StaticFieldLeak"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            //region Config
            user = Select.from(Users.class).first();
            company = Select.from(Company.class).first();
            
            userName=company.getUser();
            passWord=company.getPass();
            customProgress = CustomProgress.getInstance();
            Transport_GUID = sharedPreferences.getString("Transport_GUID", "");
            Inv_GUID = sharedPreferences.getString("Inv_GUID", "");
            if (!linkPayment.equals(""))
                linkPayment = linkPayment + "/ChargeClub?c=" + user.getC();
            OrdTList = new ArrayList<>();
            timesList = new ArrayList<>();
            times = new ArrayList<>();
            allTime = new ArrayList<>();
            allDate = new ArrayList<>();
            dateList = new ArrayList<>();
            Date dateNow = Calendar.getInstance().getTime();
            dateChoose = dateNow;
            sumPurePrice=0;
            //endregion Config

            //region get Bundle
            boolean edit = PaymentFragmentArgs.fromBundle(getArguments()).getEdit();
            //endregion get Bundle

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
                if (disableAccount) {
                    getActivity().finish();
                }
            });

            btnOkDialog.setOnClickListener(v -> {
                dialogSync.dismiss();
                binding.progressBar.setVisibility(View.VISIBLE);
                myViewModel.getSetting(userName, passWord);

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

            MaterialButton btnNewAddress = dialogAddress.findViewById(R.id.btn_edit);


            radioAddress1.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {
                    latitude1 = Double.parseDouble(user.LAT != null && !user.LAT.equals("") && !user.LAT.equals("-") ? user.LAT : "0.0");
                    longitude1 = Double.parseDouble(user.LNG != null && !user.LNG.equals("") && !user.LNG.equals("-") ? user.LNG : "0.0");

                    if (latitude1 == 0 && longitude1 == 0)
                        binding.tvError.setText("طول و عرض جغرافیایی شما ثبت نشده لطفا برای محاسبه دقیق هزینه توزیع موقعیت خود را در نقشه ثبت کنید.");
                    else
                        binding.tvError.setText("");

                    double distance = getDistanceMeters(new LatLng(latitude1, longitude1), new LatLng(Double.parseDouble(company.getLat()), Double.parseDouble(company.getLong())));

                    double price = PriceTransport(distance / 1000, sumPurePrice);

                    if (price == -1.0) {
                        binding.tvError.setText("سفارش خارج از محدوده است.");
                        dialogAddress.dismiss();
                        return;
                    } else {
                        calculateTransport = price;
                        if (Ord_TYPE.equals(OrderTypeApp)) {
                            sumTransport = 0;
                            binding.layoutPeyk.setVisibility(View.GONE);
                            binding.tvTransport.setText("0 ریال");
                            binding.tvSumPurePrice.setText(format.format(sumPurePrice) + "ریال");
                        } else {
                            sumTransport = calculateTransport;
                            binding.layoutPeyk.setVisibility(View.VISIBLE);
                            binding.tvSumPurePrice.setText(format.format(sumPurePrice + calculateTransport) + "ریال");
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
                    latitude2 = Double.parseDouble(user.LAT1 != null && !user.LAT1.equals("") && !user.LAT1.equals("-") ? user.LAT1 : "0.0");
                    longitude2 = Double.parseDouble(user.LNG1 != null && !user.LNG1.equals("") && !user.LNG1.equals("-") ? user.LNG1 : "0.0");
                    if (latitude2 == 0.0 || longitude2 == 0.0) {
                        binding.tvError.setText("طول و عرض جغرافیایی شما ثبت نشده لطفا برای محاسبه دقیق هزینه توزیع موقعیت خود را در نقشه ثبت کنید.");
                    }else
                        binding.tvError.setText("");

                    double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(Double.parseDouble(company.getLat()), Double.parseDouble(company.getLong())));
                    double price = PriceTransport(distance / 1000, sumPurePrice);
                    if (price == -1.0) {
                        binding.tvError.setText("سفارش خارج از محدوده است.");
                        dialogAddress.dismiss();
                        return;
                    } else {
                        calculateTransport=price;
                        if (Ord_TYPE.equals(OrderTypeApp)) {
                            sumTransport = 0;
                            binding.layoutPeyk.setVisibility(View.GONE);
                            binding.tvTransport.setText("0 ریال");
                            binding.tvSumPurePrice.setText(format.format(sumPurePrice) + "ریال");
                        } else {
                            sumTransport = calculateTransport;
                            binding.layoutPeyk.setVisibility(View.VISIBLE);
                            binding.tvSumPurePrice.setText(format.format(sumPurePrice + calculateTransport) + "ریال");
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
                if (user == null) {
                    Toast.makeText(getActivity(), "مشتری نامعتبر است", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialogAddress.dismiss();
                NavDirections action = PaymentFragmentDirections.actionGoToRegisterFragment("PaymentFragment", user.getM(),-1);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            });
            //endregion Cast DialogAddress

            //region SetAddress
            if (user != null && user.ADR != null && !user.ADR.equals("")) {
                radioAddress1.setText(user.ADR);
                radioAddress1.setVisibility(View.VISIBLE);
                //region If The Address Selected By The User Is Not Address 2 We Calculate Cost OfTransport
                if (!ChooseAddress2){
                    latitude1 = Double.parseDouble(user.LAT != null && !user.LAT.equals("") && !user.LAT.equals("-") ? user.LAT: "0.0");
                    longitude1 = Double.parseDouble(user.LNG != null && !user.LNG.equals("") && !user.LNG.equals("-") ? user.LNG : "0.0");
                    if (latitude1== 0.0 || longitude1 == 0.0) {
                        binding.tvError.setText("طول و عرض جغرافیایی شما ثبت نشده لطفا برای محاسبه دقیق هزینه توزیع موقعیت خود را در نقشه ثبت کنید.");
                    }
                    double distance = getDistanceMeters(new LatLng(latitude1, longitude1), new LatLng(Double.parseDouble(company.getLat()), Double.parseDouble(company.getLong())));
                    double price = PriceTransport(distance / 1000,sumPurePrice);
                    if (price == -1.0) {
                        binding.tvError.setText("سفارش خارج از محدوده است.");
                        dialogAddress.dismiss();

                        return;
                    } else {
                        calculateTransport=price;
                        if (Ord_TYPE.equals(OrderTypeApp)) {
                            sumTransport = 0;
                            binding.layoutPeyk.setVisibility(View.GONE);

                        } else {
                            sumTransport = calculateTransport;
                            binding.layoutPeyk.setVisibility(View.VISIBLE);

                        }
                    }
                }
                //endregion If The Address Selected By The User Is Not Address 2 We Calculate Cost OfTransport
            }
            else
                radioAddress1.setVisibility(View.GONE);

            if (user != null && user.ADR2 != null && !user.ADR2.equals("")) {
                radioAddress2.setVisibility(View.VISIBLE);
                radioAddress2.setText(user.ADR2);

                //region If The Address Selected By The User Is  Address 2 We Calculate Cost OfTransport
                if (ChooseAddress2) {
                    latitude2 = Double.parseDouble(user.LAT1 != null && !user.LAT1.equals("") && !user.LAT1.equals("-") ? user.LAT1 : "0.0");
                    longitude2 = Double.parseDouble(user.LNG1 != null && !user.LNG1.equals("") && !user.LNG1.equals("-") ? user.LNG1 : "0.0");
                    if ((latitude2 == 0.0 || longitude2 == 0.0)) {
                        binding.tvError.setText("طول و عرض جغرافیایی شما ثبت نشده لطفا برای محاسبه دقیق هزینه توزیع موقعیت خود را در نقشه ثبت کنید.");
                    }
                    double distance = getDistanceMeters(new LatLng(latitude2, longitude2), new LatLng(Double.parseDouble(company.getLat()), Double.parseDouble(company.getLong())));
                    double price = PriceTransport(distance / 1000, sumPurePrice);
                    if (price == -1.0 ) {
                        binding.tvError.setText("سفارش خارج از محدوده است.");
                        dialogAddress.dismiss();

                        return;
                    } else {
                        calculateTransport = price;
                        if (Ord_TYPE.equals(OrderTypeApp)) {
                            sumTransport = 0;
                            binding.layoutPeyk.setVisibility(View.GONE);
                            binding.tvTransport.setText("0 ریال");
                            binding.tvSumPurePrice.setText(format.format(sumPurePrice) + "ریال");
                        } else {
                            sumTransport = calculateTransport;
                            binding.layoutPeyk.setVisibility(View.VISIBLE);
                            binding.tvSumPurePrice.setText(format.format(sumPurePrice + calculateTransport) + "ریال");
                            binding.tvTransport.setText(format.format(calculateTransport) + " ریال ");
                        }
                    }
                }
                //endregion If The Address Selected By The User Is Address 2 We Calculate Cost OfTransport
            }
            else
                radioAddress2.setVisibility(View.GONE);


            if (user != null && user.ADR != null && !user.ADR.equals("") && !ChooseAddress2) {
                binding.edtAddress.setText(user.ADR);
                typeAddress = 1;
                ValidAddress = user.ADR;

            }
            else if (user != null && user.ADR2 != null && !user.ADR2.equals("")) {
                binding.edtAddress.setText(user.ADR2);
                typeAddress = 2;
                ValidAddress = user.ADR2;
            }
            else {
                typeAddress = 0;
                ValidAddress = "";
            }


            binding.layoutAddress.setOnClickListener(v -> {
                if (typeAddress != 0) {
                    dialogAddress.show();
                    return;
                } else if (user == null) {
                    Toast.makeText(getActivity(), "مشتری نامعتبر است", Toast.LENGTH_SHORT).show();
                    return;
                }
                NavDirections action = PaymentFragmentDirections.actionGoToRegisterFragment("PaymentFragment", user.getM(),-1);
                Navigation.findNavController(binding.getRoot()).navigate(action);
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
                    //Need To Change By Farzane
                    if (times.size() == 0)
                        dateList.remove(0);

                    timeAdapter.notifyDataSetChanged();
                    dialogTime.show();
                }
            });

            //endregion Configuration Time

            //region Configuration TypeOrder
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            layoutManager.setReverseLayout(true);
            binding.recyclerViewOrderType.setLayoutManager(layoutManager);
            binding.recyclerViewOrderType.setScrollingTouchSlop(View.FOCUS_LEFT);
            orderTypeOrderAdapter = new OrderTypeOrderAdapter(requireActivity(), OrdTList);
            binding.recyclerViewOrderType.setAdapter(orderTypeOrderAdapter);

            orderTypeOrderAdapter.setOnClickListener((GUID, code) -> {
                //region Reset Payment Type Because It is Possible That The Cost Of The Transport Will Change By Changing The Type Of Order
                typePayment = "-1";
                binding.tvSuccessFullPayOnline.setText("");
                binding.ivOkClubPayment.setVisibility(View.GONE);
                binding.ivOkOnSitePayment.setVisibility(View.GONE);
                //endregion Reset Payment Type Because It is Possible That The Cost Of The Transport Will Change By Changing The Type Of Order


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


                orderTypeOrderAdapter.notifyDataSetChanged();


                if (Ord_TYPE.equals(OrderTypeApp)) {
                    sumTransport = 0;
                    binding.tvError.setVisibility(View.GONE);
                    binding.layoutPeyk.setVisibility(View.GONE);
                    binding.tvTransport.setText("0 ریال");
                    binding.tvSumPurePrice.setText(format.format(sumPurePrice) + "ریال");
                } else {
                    binding.tvError.setVisibility(View.VISIBLE);
                    sumTransport = calculateTransport;
                    binding.layoutPeyk.setVisibility(View.VISIBLE);
                    binding.tvTransport.setText(format.format(sumTransport) + " ریال ");
                    binding.tvSumPurePrice.setText(format.format(sumPurePrice+ sumTransport) + "ریال");
                }
            });

            //endregion Configuration TypeOrder

            //region Configuration Payment
            binding.btnOnSitePayment.setOnClickListener(v -> {
                typePayment = "";

                binding.tvSuccessFullPayOnline.setText("");
                binding.ivOkClubPayment.setVisibility(View.GONE);

                if (user != null && user.CRDT != null) {
                    binding.tvCredit.setText("موجودی : " + format.format(user.CRDT) + " ریال ");
                }
                binding.ivOkOnSitePayment.setVisibility(View.VISIBLE);
            });


            binding.btnClubPayment.setOnClickListener(v -> {
                typePayment = "-1";

                binding.ivOkOnSitePayment.setVisibility(View.GONE);

                if (user != null && user.CRDT != null && user.CRDT >= (sumPurePrice + sumTransport)) {
                    binding.tvSuccessFullPayOnline.setText("پرداخت موفقیت آمیز");
                    binding.tvCredit.setText(format.format(user.CRDT - (sumPurePrice + sumTransport)));
                    typePayment = "4";
                    binding.ivOkClubPayment.setVisibility(View.VISIBLE);
                }
                else if (!linkPayment.equals("")) {
                    Uri uri = Uri.parse(linkPayment);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    startActivityForResult(intent, 44);
                }
                else {
                    Toast.makeText(getActivity(), "انتقال به باشگاه مشتریان ، در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();

                }
            });
            //endregion Configuration Payment

            //region Edit View
            if (edit)
                binding.tvError.setText("اصلاح سفارش");
            //endregion Edit View

            //region Calculate SumPrice Order
            invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            double sumPriceItem;
            double sumDiscountItem;
            double sumTotalPriceItem;
            for (int i = 0; i < invDetails.size(); i++) {
                ir.kitgroup.saleindemo.DataBase.Product product = Select.from(ir.kitgroup.saleindemo.DataBase.Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();

                InvoiceDetail invoiceDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invDetails.get(i).INV_DET_UID + "'").first();

                sumPriceItem = product.getPrice(sharedPreferences) * invoiceDtl.getQuantity();
                sumDiscountItem = (product.getPercDis() / 100) * sumPriceItem;
                sumTotalPriceItem = sumPriceItem - sumDiscountItem;
                sumPurePrice = sumPurePrice + sumTotalPriceItem;

            }

            binding.tvSumPurePrice.setText(format.format(sumPurePrice + calculateTransport) + "ریال");
            binding.tvTransport.setText(format.format(calculateTransport) + "ریال");

            //endregion Calculate SumPrice Order

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

                myViewModel.getInquiryAccount(userName, passWord, user.getM());
                if (typePayment.equals("-1")) {
                    Toast.makeText(getActivity(), "نوع پرداخت را مشخص کنید.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (typeAddress == 0) {
                    Toast.makeText(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Ord_TYPE == null || Ord_TYPE == -1) {
                    Toast.makeText(getActivity(), "نوع سفارش را انتخاب کنید", Toast.LENGTH_SHORT).show();
                    return;
                } else if (allTime.size() > 0 && !allTime.get(0).equals("") && timeChoose.equals("") && (!Ord_TYPE.equals(OrderTypeApp))) {
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

                double totalPrice = 0;
                double sumDiscount = 0;
                double sumDiscountPercent = 0;
                double sumPurePrice = 0;

                InvoiceDetail invoiceDetailTransport = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID.toLowerCase() + "' AND PRDUID ='" + Transport_GUID.toLowerCase() + "'").first();

                if (invoiceDetailTransport == null) {
                    invoiceDetailTransport = new InvoiceDetail();
                    invoiceDetailTransport.INV_DET_UID = UUID.randomUUID().toString();
                    invoiceDetailTransport.ROW_NUMBER = invDetails.size() + 1;
                    invoiceDetailTransport.INV_UID = Inv_GUID;
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

                    if (sumTransport!=0)
                    InvoiceDetail.save(invoiceDetailTransport);
                }
                else {
                    invoiceDetailTransport.INV_UID = Inv_GUID;
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
                    ir.kitgroup.saleindemo.DataBase.Product product = Select.from(ir.kitgroup.saleindemo.DataBase.Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();

                    InvoiceDetail invoiceDtl = Select.from(InvoiceDetail.class).where("INVDETUID ='" + invDetails.get(i).INV_DET_UID + "'").first();

                    if (product != null) {
                        double sumPrice = (invoiceDtl.getQuantity() * product.getPrice(sharedPreferences));//جمع کل ردیف
                        double discountPrice = sumPrice * (product.getPercDis() / 100);//جمع تخفیف ردیف
                        double purePrice = sumPrice - discountPrice;//جمع خالص ردیف


                        totalPrice = totalPrice + sumPrice;//جمع کل فاکتور
                        sumPurePrice = sumPurePrice + purePrice;//جمع خالص فاکتور
                        sumDiscount = sumDiscount + discountPrice;//جمع تخفیفات ردیف های فاکتور
                        sumDiscountPercent = sumDiscountPercent + (product.getPercDis() / 100);//جمع درصد تخفیفات ردیف های فاکتور

                        invoiceDtl.INV_DET_TOTAL_AMOUNT = String.valueOf(purePrice);
                        invoiceDtl.ROW_NUMBER = i + 1;
                        invoiceDtl.INV_DET_PERCENT_DISCOUNT = product.getPercDis();
                        invoiceDtl.INV_DET_DISCOUNT = String.valueOf(discountPrice);
                        invoiceDtl.INV_DET_PRICE_PER_UNIT = String.valueOf(product.getPrice(sharedPreferences));
                        invoiceDtl.update();
                    }
                }
                Invoice invoice = new Invoice();
                invoice.INV_TOTAL_AMOUNT = totalPrice + sumTransport;//جمع فاکنور
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
                if (Select.from(Users.class).first() == null) {
                    Toast.makeText(getActivity(), "مشتری معتبر نمی باشد", Toast.LENGTH_SHORT).show();
                    return;
                }
                invoice.ACC_CLB_UID = Select.from(Users.class).first().I;
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
                List<InvoiceDetail> invoiceDetailList = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                List<PaymentRecieptDetail> clsPaymentRecieptDetails = new ArrayList<>();
                if (!typePayment.equals("-1") && !typePayment.equals("")) {
                    PaymentRecieptDetail cl = new PaymentRecieptDetail();
                    cl.PAY_RCIPT_DET_DESCRIBTION = listInvoice.get(0).INV_DESCRIBTION;
                    cl.PAY_RCIPT_DET_TOTAL_AMOUNT = listInvoice.get(0).INV_EXTENDED_AMOUNT;
                    cl.PAY_RCIPT_DET_TYPE = typePayment;
                    clsPaymentRecieptDetails.add(cl);
                }

                customProgress.showProgress(getActivity(), "در حال ارسال سفارش...", false);
                myViewModel.sendOrder(userName, passWord, listInvoice, invoiceDetailList, clsPaymentRecieptDetails, "");


            });

            btnReturned.setOnClickListener(v -> {
                dialogSendOrder.dismiss();

                for (int i = 0; i < 2; i++) {
                    Navigation.findNavController(binding.getRoot()).popBackStack();
                }
            });
            //endregion Configuration Send Order


            binding.ivBack.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());

        } catch (Exception ignore) {
        }
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getSetting(userName, passWord);

        myViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            myViewModel.getResultSetting().setValue(null);

            timesList.clear();
            List<Setting> settingsList = new ArrayList<>(result);

            //region Get Type Order
            if (!settingsList.get(0).ORDER_TYPE_APP.equals(""))
                sharedPreferences.edit().putString("OrderTypeApp", settingsList.get(0).ORDER_TYPE_APP).apply();

            OrderTypeApp = !settingsList.get(0).ORDER_TYPE_APP.equals("") ? Integer.parseInt(settingsList.get(0).ORDER_TYPE_APP) : 0;
            //endregion Get Type Order


            //region Get Type Of Payment
            ViewOfPayment = !settingsList.get(0).PAYMENT_TYPE.equals("") ?
                    Integer.parseInt(settingsList.get(0).PAYMENT_TYPE) : 3;
            if (ViewOfPayment == 1)
                binding.btnOnSitePayment.setVisibility(View.VISIBLE);
            else if (ViewOfPayment == 2)
                binding.btnClubPayment.setVisibility(View.VISIBLE);
            else {
                binding.btnClubPayment.setVisibility(View.VISIBLE);
                binding.btnOnSitePayment.setVisibility(View.VISIBLE);
            }
            //endregion Get Type Of Payment


            //region Get Day Services
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
            //endregion Get Day Services


            //region Get GUID Of Row Transport
            Transport_GUID = settingsList.get(0).PEYK;
            sharedPreferences.edit().putString("Transport_GUID", settingsList.get(0).PEYK).apply();
            //endregion Get GUID Of Row Transport


            //region Get All Time Services
            try {
                allTime = new ArrayList<>(Arrays.asList(settingsList.get(0).SERVICE_TIME.split(",")));

            } catch (Exception ignore) {
            }
            if (allTime.size() > 0 && !allTime.get(0).equals(""))
                binding.layoutTime.setVisibility(View.VISIBLE);
            //region Get All Time Services


            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getTypeOrder(userName, passWord);


        });

        myViewModel.getResultTypeOrder().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;


            myViewModel.getResultTypeOrder().setValue(null);

            CollectionUtils.filter(result.getOrderTypes(), i -> i.getTy() == 2);
            OrdTList.addAll(result.getOrderTypes());

            if (OrdTList.size() == 1) {
                OrdTList.get(0).Click = true;
                Ord_TYPE = OrdTList.get(0).getC();
                binding.layoutTypeOrder.setVisibility(View.GONE);
            }


            orderTypeOrderAdapter.notifyDataSetChanged();
            if (Ord_TYPE.equals(OrderTypeApp)) {
                sumTransport = 0;
                binding.layoutPeyk.setVisibility(View.GONE);
                binding.tvTransport.setText("0 ریال");
                binding.tvSumPurePrice.setText(format.format(sumPurePrice) + "ریال");
            } else {
                if ((OrdTList.size() == 1)) {
                    sumTransport = calculateTransport;
                    binding.layoutPeyk.setVisibility(View.VISIBLE);
                    binding.tvTransport.setText(format.format(sumTransport) + " ریال ");
                    binding.tvSumPurePrice.setText(format.format(sumPurePrice+ sumTransport) + "ریال");
                }
            }


            //region Get Credit Club
            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getInquiryAccount(userName, passWord, user.M);
            //endregion Get Credit Club

        });

        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;


            myViewModel.getResultInquiryAccount().setValue(null);
            disableAccount = false;
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();
            //user is register
            if (result.size() > 0) {
                Users.deleteAll(Users.class);
                Users.saveInTx(result);
                user = Select.from(Users.class).first();
                if (user != null && user.CRDT != null)
                    binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
                binding.tvCredit.setText("موجودی : " + format.format(user.CRDT) + " ریال ");
            } else {
                binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.red_table));
                binding.tvCredit.setText("خطا در بروز رسانی موجودی ");
            }

            binding.progressBar.setVisibility(View.GONE);

        });


        myViewModel.getResultSendOrder().observe(getViewLifecycleOwner(), result -> {

            if (result == null) {
                binding.progressBar.setVisibility(View.GONE);
                customProgress.hideProgress();

                return;
            }

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

                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" +
                        Inv_GUID + "'").list();
                InvoiceDetail.deleteInTx(invoiceDetails);
                sharedPreferences.edit().putString("Inv_GUID", "").apply();
                tvMessage.setText("سفارش با موفقیت ارسال شد");

            } else {

                Product.deleteAll(Product.class);
                tvMessage.setText("خطا در ارسال ،" + "\n" + description);

            }
            customProgress.hideProgress();
            dialogSendOrder.show();

        });

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            binding.progressBar.setVisibility(View.GONE);
            customProgress.hideProgress();
            if (result == null)
                return;

            //  myViewModel.getResultMessage().setValue(null);
            Toasty.error(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
            disableAccount = sharedPreferences.getBoolean("disableAccount", false);
            if (disableAccount)
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
        double priceTransport = -1;
        try {
            if ("ir.kitgroup.saleinmeat".equals(company.getInskId())) {
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
    }

    private void showError(String error) {
        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        dialogSync.dismiss();
        btnOkDialog.setVisibility(View.GONE);
        dialogSync.setCancelable(false);
        dialogSync.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        ChooseAddress2 = false;
    }

}
