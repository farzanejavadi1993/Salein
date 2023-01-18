package ir.kitgroup.saleinkhavari.ui.payment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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
import ir.kitgroup.saleinkhavari.Connect.CompanyViewModel;
import ir.kitgroup.saleinkhavari.DataBase.Account;

import ir.kitgroup.saleinkhavari.DataBase.Product;
import ir.kitgroup.saleinkhavari.DataBase.SaleinShop;
import ir.kitgroup.saleinkhavari.classes.Utilities;
import ir.kitgroup.saleinkhavari.DataBase.Company;

import ir.kitgroup.saleinkhavari.classes.dialog.DialogInstance;
import ir.kitgroup.saleinkhavari.databinding.FragmentPaymentBinding;
import ir.kitgroup.saleinkhavari.models.ModelDate;
import ir.kitgroup.saleinkhavari.models.Setting;
import ir.kitgroup.saleinkhavari.models.PaymentRecieptDetail;
import ir.kitgroup.saleinkhavari.classes.CustomProgress;

import ir.kitgroup.saleinkhavari.models.Invoice;
import ir.kitgroup.saleinkhavari.DataBase.InvoiceDetail;
import ir.kitgroup.saleinkhavari.models.OrderType;
import ir.kitgroup.saleinkhavari.R;

@AndroidEntryPoint
public class PaymentFragment extends Fragment {
    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;

    private FragmentPaymentBinding binding;
    private CompanyViewModel myViewModel;

    private Company company;
    private String userName;
    private String passWord;

    private String linkPayment = "";
    private CustomProgress customProgress;
    private String deliveryType = "-1";
    private Integer Ord_TYPE = -1;

    //region Dialog Sync
    private Dialog dialogRequestAgain;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync

    //region Dialog Address
    private Account account;
    private Dialog dialogAddress;
    private RadioButton radioAddress1;
    private RadioButton radioAddress2;
    private int typeAddress = 0;
    //If This Variable Equal 1 We Send Address1 As Address Order Else If This Variable Equal 2  We Send Address2
    private String ValidAddress = "";
    private double companyLat = 0.0;
    private double companyLong = 0.0;

    private double latitude1 = 0.0;
    private double longitude1 = 0.0;
    private double latitude2 = 0.0;
    private double longitude2 = 0.0;
    private boolean ChooseAddress2 = false;
    private int transportCost = 0;
    private int tempTransportCost = 0;
    //endregion Dialog Address

    //region Dialog Send  Order
    private Dialog dialogSendOrder;
    private RelativeLayout rlButtons;
    private MaterialButton btnReturned;
    private TextView tvMessage;
    //endregion Dialog Send  Order


    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    private double sumPurePrice = 0;
    private List<InvoiceDetail> invDetails;
    private String Inv_GUID;
    private Integer ViewOfPayment = 0;//If This Variable Equal 1 We Only Show On_Site Payment Card If This Variable Equal 2 We Only Show Club Pay Card If This Variable Equal 3 We Can Show Both Pay Cards
    private Integer SERVICE_DAY = 0;//This Variable Show Service days Of Company And Get From Server
    private String chooseTimeDelivery = "";//This Variable Show Service Times Per Day Of Company And Get From Server
    private boolean disableAccount = false;

    //region Time Dialog
    private Dialog dialogTime;
    private TimeAdapter timeDeliveryAdapter;
    private DateAdapter dateDeliveryAdapter;

    private ArrayList<String> filterTimes;
    private ArrayList<String> availableTimeDelivery;
    private ArrayList<String> AllTimeDelivery;

    private ArrayList<ModelDate> allDateDelivery;
    private ArrayList<ModelDate> availableDateDelivery;

    private Date chooseDayDelivery;
    //endregion Time Dialog


    private OrderTypeOrderAdapter deliveryOrderAdapter;
    private List<OrderType> deliveryList;
    private String Transport_GUID;

    private DialogInstance dialogInstance;
    private Utilities util;
    private Locale loc;
    private boolean isCalculateTransport = false;

    private int OrderTypeApp;

    //endregion Parameter

    //region Override Method

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
        init();
        castDialogRequestAgain();
        castDialogAddress();
        setAddress();
        castDialogTime();
        castDeliveryOrder();
        paymentType();
        calculateSumPriceOrder();
        sendOrder();

    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        myViewModel.getSetting(userName, passWord);

        getTransportCost();

        myViewModel.getResultTransportationCost().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            isCalculateTransport = true;
            myViewModel.getResultTransportationCost().setValue(null);

            tempTransportCost = result;

            setDataTransport(result);
        });

        myViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            myViewModel.getResultSetting().setValue(null);


            List<Setting> settingsList = new ArrayList<>(result);
            filterTimes.clear();

            //region getOrderTypePresent
            if (!settingsList.get(0).ORDER_TYPE_APP.equals(""))
                sharedPreferences.edit().putString("OrderTypeApp", settingsList.get(0).ORDER_TYPE_APP).apply();

            OrderTypeApp = !settingsList.get(0).ORDER_TYPE_APP.equals("") ? Integer.parseInt(settingsList.get(0).ORDER_TYPE_APP) : 0;
            //endregion getOrderTypePresent


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

            allDateDelivery.clear();
            Date dateNow = Calendar.getInstance().getTime();
            if (SERVICE_DAY == 0) {
                ModelDate modelDate = new ModelDate();
                modelDate.date = dateNow;
                modelDate.Click = true;
                allDateDelivery.add(modelDate);
            }
            for (int i = 0; i < SERVICE_DAY; i++) {
                Date date = Calendar.getInstance().getTime();
                date.setDate(date.getDate() + i);
                ModelDate modelDate = new ModelDate();
                modelDate.date = date;
                modelDate.Click = i == 0;
                allDateDelivery.add(modelDate);
            }
            //endregion Get Day Services


            //region Get GUID Of Row Transport
            Transport_GUID = settingsList.get(0).PEYK;
            sharedPreferences.edit().putString("Transport_GUID", settingsList.get(0).PEYK).apply();
            //endregion Get GUID Of Row Transport


            //region Get All Time Services
            try {
                AllTimeDelivery = new ArrayList<>(Arrays.asList(settingsList.get(0).SERVICE_TIME.split(",")));

            } catch (Exception ignore) {
            }
            if (AllTimeDelivery.size() > 0 && !AllTimeDelivery.get(0).equals(""))
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

            // CollectionUtils.filter(result.getOrderTypes(), i -> i.getTy() == 2);

            deliveryList.addAll(result.getOrderTypes());

            if (deliveryList.size() == 1) {
                deliveryList.get(0).Click = true;
                Ord_TYPE = deliveryList.get(0).getC();
                binding.layoutTypeOrder.setVisibility(View.GONE);
            }


            deliveryOrderAdapter.notifyDataSetChanged();

            if ((deliveryList.size() == 1))
                setDataTransport(tempTransportCost);


            //region Get Credit Club
            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getInquiryAccount(userName, passWord, account.getM());
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
                Account.deleteAll(Account.class);
                Account.saveInTx(result);
                account = Select.from(Account.class).first();
                if (account != null ) {
                    binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
                    binding.tvCredit.setText("موجودی : " + format.format(account.getCRDT()) + " ریال ");
                }
            } else {
                binding.tvCredit.setTextColor(getActivity().getResources().getColor(R.color.red));
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

                //region Delete Inv_GUID
                String name = company.getI();
                Inv_GUID = sharedPreferences.getString(name, "");
                sharedPreferences.edit().putString("Inv_GUID", "").apply();
                sharedPreferences.edit().putString(name, "").apply();
                //endregion Delete Inv_GUID

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
            if (result.getCode() == -11) {
                binding.tvError.setText(result.getName());
                isCalculateTransport = false;
                binding.tvSumPurePrice.setText(format.format(sumPurePrice + transportCost) + "ریال");
                binding.tvTransport.setText("خطا");
            } else
                Toasty.error(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();

            disableAccount = sharedPreferences.getBoolean("disableAccount", false);

            if (disableAccount)
                showError(result.getName());


        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!sharedPreferences.getBoolean("loginSuccess", false)) {
            sharedPreferences.edit().putBoolean("loginSuccess", true).apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.edit().putBoolean("loginSuccess", false).apply();
    }
    //endregion Override Method

    //region Custom Method
    public static class JsonObject {
        public List<Invoice> Invoice;
        public List<InvoiceDetail> InvoiceDetail;
        public List<PaymentRecieptDetail> PaymentRecieptDetail;
    }

    @SuppressLint("SetTextI18n")
    private void getTransportCost() {
        double mainCustomerLat;
        double mainCustomerLong;

        if (!ChooseAddress2) {
            mainCustomerLat = latitude1;
            mainCustomerLong = longitude1;
        } else {
            mainCustomerLat = latitude2;
            mainCustomerLong = longitude2;
        }

        if (mainCustomerLat != 0 && mainCustomerLong != 0)
            myViewModel.getTransportationCost(mainCustomerLat, mainCustomerLong, companyLat, companyLong, sumPurePrice);
        else
            binding.tvSumPurePrice.setText(format.format(sumPurePrice + transportCost) + "ریال");

    }

    @SuppressLint("SetTextI18n")
    private void setDataTransport(int price) {
        dialogAddress.dismiss();

        if (Ord_TYPE == OrderTypeApp)
            transportCost = 0;
        else
            transportCost = price;


        if (price > 0) {
            binding.layoutPeyk.setVisibility(View.VISIBLE);
            binding.tvTransport.setText(format.format(transportCost) + " ریال ");
        } else
            binding.layoutPeyk.setVisibility(View.GONE);

        if (account.getCRDT()<sumPurePrice + transportCost && deliveryType.equals("4"))
            resetDeliveryType();

        binding.tvSumPurePrice.setText(format.format(sumPurePrice + transportCost) + "ریال");
    }

    private void showError(String error) {
        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        dialogRequestAgain.dismiss();
        btnOkDialog.setVisibility(View.GONE);
        dialogRequestAgain.setCancelable(false);
        dialogRequestAgain.show();
    }

    private void init() {


        account = Select.from(Account.class).first();
        latitude1 = account.getLAT();
        longitude1 = account.getLNG();
        latitude2 = account.getLAT1();
        longitude2 = account.getLNG1();


        company = Select.from(Company.class).first();
        userName = company.getUser();
        passWord = company.getPass();
        companyLat = company.getLat();
        companyLong = company.getLong();

        customProgress = CustomProgress.getInstance();
        dialogInstance = DialogInstance.getInstance();

        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");

        Inv_GUID = sharedPreferences.getString("Inv_GUID", "");

        linkPayment = !linkPayment.equals("") ? linkPayment + "/ChargeClub?c=" + account.getC() : "";

        deliveryList = new ArrayList<>();

        AllTimeDelivery = new ArrayList<>();
        filterTimes = new ArrayList<>();
        availableTimeDelivery = new ArrayList<>();

        allDateDelivery = new ArrayList<>();
        availableDateDelivery = new ArrayList<>();

        sumPurePrice = 0;

        typeAddress = 0;
        ValidAddress = "";

        chooseDayDelivery = Calendar.getInstance().getTime();

        util = new Utilities();
        loc = new Locale("en_US");

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());


    }

    private void castDialogRequestAgain() {
        dialogRequestAgain = dialogInstance.dialog(getActivity(), false, R.layout.custom_dialog);

        textMessageDialog = dialogRequestAgain.findViewById(R.id.tv_message);
        btnOkDialog = dialogRequestAgain.findViewById(R.id.btn_ok);
        btnNoDialog = dialogRequestAgain.findViewById(R.id.btn_cancel);

        btnNoDialog.setOnClickListener(v -> {
            dialogRequestAgain.dismiss();
            if (disableAccount) {
                getActivity().finish();
            }
        });

        btnOkDialog.setOnClickListener(v -> {
            dialogRequestAgain.dismiss();
            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getSetting(userName, passWord);
            getTransportCost();
        });


    }

    @SuppressLint("SetTextI18n")
    private void castDialogAddress() {
        dialogAddress = dialogInstance.dialog(getActivity(), true, R.layout.dialog_address);

        radioAddress1 = dialogAddress.findViewById(R.id.radioAddress1);
        radioAddress2 = dialogAddress.findViewById(R.id.radioAddress2);
        MaterialButton btnNewAddress = dialogAddress.findViewById(R.id.btn_edit);

        radioAddress1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dialogAddress.dismiss();

                ChooseAddress2 = false;

                if (latitude1 == 0 && longitude1 == 0)
                    showAlert("هزینه توزیع محاسبه نشده است");
                else
                    getTransportCost();

                typeAddress = 1;
                ValidAddress = radioAddress1.getText().toString();
                binding.edtAddress.setText(ValidAddress);
            }
        });

        radioAddress2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dialogAddress.dismiss();

                ChooseAddress2 = true;
                if (latitude2 == 0.0 || longitude2 == 0.0)
                    showAlert("طول و عرض جغرافیایی شما ثبت نشده لطفا برای محاسبه دقیق هزینه توزیع موقعیت خود را در نقشه ثبت کنید.");
                else
                    getTransportCost();


                typeAddress = 2;
                ValidAddress = radioAddress2.getText().toString();
                binding.edtAddress.setText(ValidAddress);

            }
        });

        btnNewAddress.setOnClickListener(v -> {

            dialogAddress.dismiss();
            if (account == null) {
                Toast.makeText(getActivity(), "مشتری نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            }
            intentToMapActivity();
        });
    }

    private void setAddress() {

        if (account != null) {

            if (!account.getAdr().equals("")) {
                radioAddress1.setVisibility(View.VISIBLE);
                radioAddress1.setText(account.getAdr());
            } else
                radioAddress1.setVisibility(View.GONE);

            if (!account.getAdr2().equals("")) {
                radioAddress2.setVisibility(View.VISIBLE);
                radioAddress2.setText(account.getAdr2());
            } else
                radioAddress2.setVisibility(View.GONE);


            if (latitude2 == 0.0 || longitude2 == 0.0) {

                if (latitude1 != 0.0 && longitude1 != 0.0) {
                    binding.edtAddress.setText(account.getAdr());
                    typeAddress = 1;
                    ValidAddress = account.getAdr();

                }

            } else {
                binding.edtAddress.setText(account.getAdr2());
                typeAddress = 2;
                ValidAddress = account.getAdr2();
            }

        }

        binding.layoutAddress.setOnClickListener(v -> {
            if (typeAddress != 0) {
                dialogAddress.show();
                return;
            } else if (account == null) {
                Toast.makeText(getActivity(), "مشتری نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            }
            intentToMapActivity();
        });
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void castDialogTime() {

        availableTimeDelivery.addAll(filterTimes);
        dialogTime = dialogInstance.dialog(getActivity(), true, R.layout.dialog_time);

        timeDeliveryAdapter = new TimeAdapter(availableTimeDelivery);
        RecyclerView recycleTime = dialogTime.findViewById(R.id.recyclerTime);
        recycleTime.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleTime.setAdapter(timeDeliveryAdapter);

        timeDeliveryAdapter.setOnClickItemListener(time -> {
            try {

                Date todayDate = Calendar.getInstance().getTime();

                int hour;
                hour = Integer.parseInt(time.split("-")[0]);

                chooseTimeDelivery = time;

                if (hour < todayDate.getHours() &&
                        todayDate.getDate() == chooseDayDelivery.getDate() &&
                        todayDate.getDay() == chooseDayDelivery.getDay() &&
                        todayDate.getMonth() == chooseDayDelivery.getMonth() &&
                        todayDate.getYear() == chooseDayDelivery.getYear()
                ) {
                    showAlert("در این بازه زمانی سفارش ارسال نمی شود.");
                    return;
                }
            } catch (Exception ignore) {
            }


            dialogTime.dismiss();


            Utilities.SolarCalendar sc = util.new SolarCalendar(chooseDayDelivery);

            String datePersian = sc.strWeekDay + String.format(loc, "%02d", sc.date) + "\t" + sc.strMonth + "\t" + (sc.year);

            binding.edtTime.setText(time + "   " + datePersian);
        });


        dateDeliveryAdapter = new DateAdapter(getActivity(), availableDateDelivery);
        RecyclerView recycleDate = dialogTime.findViewById(R.id.recyclerDate);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);
        recycleDate.setLayoutManager(manager);
        recycleDate.setAdapter(dateDeliveryAdapter);

        dateDeliveryAdapter.setOnClickItemListener((date, position) -> {

            chooseDayDelivery = date;

            filterTimes.clear();

            if (position == 0) {
                Date date1 = Calendar.getInstance().getTime();
                for (int i = 0; i < AllTimeDelivery.size(); i++) {
                    int hour;
                    try {
                        hour = Integer.parseInt(AllTimeDelivery.get(i).split("-")[0]);
                        if (hour > date1.getHours())
                            if ((hour - date1.getHours() != 1) || date1.getMinutes() <= 45) {
                                filterTimes.add(AllTimeDelivery.get(i));
                            }
                    } catch (Exception ignore) {
                    }
                }

            } else
                filterTimes.addAll(AllTimeDelivery);

            availableTimeDelivery.clear();
            availableTimeDelivery.addAll(filterTimes);
            timeDeliveryAdapter.notifyDataSetChanged();
        });

        binding.layoutTime.setOnClickListener(v -> {
            binding.edtTime.setTextColor(getActivity().getResources().getColor(R.color.medium_color));

            availableDateDelivery.clear();

            ArrayList<ModelDate> arrayList = new ArrayList<>(allDateDelivery);
            CollectionUtils.filter(arrayList, a -> a.Click);
            if (arrayList.size() > 0)
                allDateDelivery.get(allDateDelivery.indexOf(arrayList.get(0))).Click = false;


            if (allDateDelivery.size() > 0)
                allDateDelivery.get(0).Click = true;


            availableDateDelivery.addAll(allDateDelivery);
            dateDeliveryAdapter.notifyDataSetChanged();

            filterTimes.clear();

            if (AllTimeDelivery.size() == 0 || AllTimeDelivery.get(0).equals(""))
                showAlert("زمان ارسال سفارش از سرور تعیین نشده است.");

            else {
                Date date = Calendar.getInstance().getTime();

                for (int i = 0; i < AllTimeDelivery.size(); i++) {
                    int hour;
                    try {
                        hour = Integer.parseInt(AllTimeDelivery.get(i).split("-")[0]);
                        if (hour > date.getHours())
                            if ((hour - date.getHours() != 1) || date.getMinutes() <= 45) {
                                filterTimes.add(AllTimeDelivery.get(i));
                            }
                    } catch (Exception ignore) {
                    }
                }

                availableTimeDelivery.clear();
                availableTimeDelivery.addAll(filterTimes);

                if (availableTimeDelivery.size() == 0)
                    availableDateDelivery.remove(0);

                timeDeliveryAdapter.notifyDataSetChanged();


                if (availableTimeDelivery.size()==0)
                {
                    binding.edtTime.setTextColor(getActivity().getResources().getColor(R.color.red));
                  binding.edtTime.setText("پایان زمان سفارش");
                }
                else
                dialogTime.show();
            }
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void castDeliveryOrder() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setReverseLayout(true);
        binding.recyclerViewDelivaryOrder.setLayoutManager(layoutManager);
        binding.recyclerViewDelivaryOrder.setScrollingTouchSlop(View.FOCUS_LEFT);
        deliveryOrderAdapter = new OrderTypeOrderAdapter(requireActivity(), deliveryList);
        binding.recyclerViewDelivaryOrder.setAdapter(deliveryOrderAdapter);

        deliveryOrderAdapter.setOnClickListener((GUID, code) -> {

            Ord_TYPE = code;

            //region UnClick Old Item
            ArrayList<OrderType> list = new ArrayList<>(deliveryList);
            CollectionUtils.filter(list, r -> r.Click);
            if (list.size() > 0) {
                deliveryList.get(deliveryList.indexOf(list.get(0))).Click = false;
            }
            //endregion UnClick Old Item


            //region Click New Item
            ArrayList<OrderType> list2 = new ArrayList<>(deliveryList);
            CollectionUtils.filter(list2, r -> r.getI().equals(GUID));
            if (list2.size() > 0) {
                deliveryList.get(deliveryList.indexOf(list2.get(0))).Click = true;
            }
            //endregion Click New Item


            deliveryOrderAdapter.notifyDataSetChanged();

            setDataTransport(tempTransportCost);

        });
    }

    @SuppressLint("SetTextI18n")
    private void paymentType() {
        binding.btnOnSitePayment.setOnClickListener(v -> {

            deliveryType = "";

            binding.tvSuccessFullPayOnline.setText("");
            binding.ivOkClubPayment.setVisibility(View.GONE);

            if (account != null) {
                binding.tvCredit.setText("موجودی : " + format.format(account.getCRDT()) + " ریال ");
            }
            binding.ivOkOnSitePayment.setVisibility(View.VISIBLE);
        });

        binding.btnClubPayment.setOnClickListener(v -> {
            deliveryType = "-1";

            binding.ivOkOnSitePayment.setVisibility(View.GONE);

            if (account != null && account.getCRDT() >= (sumPurePrice + transportCost)) {
                binding.tvSuccessFullPayOnline.setText("پرداخت موفقیت آمیز");
                binding.tvCredit.setText(format.format(account.getCRDT() - (sumPurePrice + transportCost)));
                deliveryType = "4";
                binding.ivOkClubPayment.setVisibility(View.VISIBLE);
            } else
                showAlert("انتقال به باشگاه مشتریان ، در حال حاضر در دسترس نمی باشد.");

        });
    }

    private void showAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("بستن", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

        setStyleTextAlert(alertDialog);
    }

    private void setStyleTextAlert(AlertDialog alert) {
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTypeface(face);
        textView.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
        textView.setTextSize(13);
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.color_accent));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.color_accent));

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(face);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(face);

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(12);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(12);
    }

    @SuppressLint("SetTextI18n")
    private void calculateSumPriceOrder() {

        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
        CollectionUtils.filter(invDetails, inv -> !inv.PRD_UID.equals(Transport_GUID));

        double sumPriceItem;
        double sumDiscountItem;
        double sumTotalPriceItem;
        for (int i = 0; i < invDetails.size(); i++) {
            Product product = Select.from(Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();


            sumPriceItem = product.getPrice(sharedPreferences) * invDetails.get(i).getQuantity();
            sumDiscountItem = (product.getPercDis() / 100) * sumPriceItem;
            sumTotalPriceItem = sumPriceItem - sumDiscountItem;
            sumPurePrice = sumPurePrice + sumTotalPriceItem;

        }
    }

    private void sendOrder() {
        dialogSendOrder = dialogInstance.dialog(getActivity(), false, R.layout.custom_dialog);

        tvMessage = dialogSendOrder.findViewById(R.id.tv_message);
        rlButtons = dialogSendOrder.findViewById(R.id.layoutButtons);
        btnReturned = dialogSendOrder.findViewById(R.id.btn_returned);
        MaterialButton btnOk = dialogSendOrder.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialogSendOrder.findViewById(R.id.btn_cancel);

        binding.btnRegisterOrder.setOnClickListener(v -> {

            myViewModel.getInquiryAccount(userName, passWord, account.getM());

            if (deliveryType.equals("-1")) {
                Toasty.warning(getActivity(), "نوع پرداخت را مشخص کنید.", Toast.LENGTH_SHORT).show();
                return;
            } else if (typeAddress == 0) {
                Toasty.warning(getActivity(), "آدرس وارد شده نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            } else if (Ord_TYPE == null || Ord_TYPE == -1) {
                Toasty.warning(getActivity(), "نحوه تحویل سفارش را انتخاب کنید", Toast.LENGTH_SHORT).show();
                return;
            } else if (AllTimeDelivery.size() > 0 && !AllTimeDelivery.get(0).equals("") && chooseTimeDelivery.equals("")) {
                Toasty.warning(getActivity(), "زمان ارسال سفارش را تعیین کنید", Toast.LENGTH_SHORT).show();
                return;
            } else if (!isCalculateTransport/* && Ord_TYPE!=OrderTypeApp*/) {
                Toasty.warning(getActivity(), "برای محاسبه هزینه ارسال آدرس دارای مشخصات جغرافیای را انتخاب کنید.", Toast.LENGTH_LONG).show();
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
                invoiceDetailTransport.INV_DET_PRICE_PER_UNIT = String.valueOf(transportCost);
                invoiceDetailTransport.INV_DET_TOTAL_AMOUNT = String.valueOf(transportCost);
                invoiceDetailTransport.INV_DET_PERCENT_DISCOUNT = 0.0;
                invoiceDetailTransport.INV_DET_DISCOUNT = "0.0";

                if (!Transport_GUID.equals(""))
                    invoiceDetailTransport.PRD_UID = Transport_GUID;

                else {
                    Toast.makeText(getActivity(), "خطا در ارسال مبلغ توزیع", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (transportCost != 0)
                    InvoiceDetail.save(invoiceDetailTransport);
            } else {
                invoiceDetailTransport.INV_UID = Inv_GUID;
                invoiceDetailTransport.INV_DET_QUANTITY = 1.0;
                invoiceDetailTransport.INV_DET_PRICE_PER_UNIT = String.valueOf(transportCost);
                invoiceDetailTransport.INV_DET_TOTAL_AMOUNT = String.valueOf(transportCost);

                if (transportCost != 0)
                    invoiceDetailTransport.save();
                else
                    InvoiceDetail.delete(invoiceDetailTransport);
            }


            for (int i = 0; i < invDetails.size(); i++) {
                Product product = Select.from(Product.class).where("I ='" + invDetails.get(i).PRD_UID + "'").first();
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
            invoice.INV_UID = Inv_GUID;
            invoice.INV_TOTAL_AMOUNT = totalPrice + transportCost;//جمع فاکنور
            invoice.INV_TOTAL_DISCOUNT = 0.0;
            invoice.INV_PERCENT_DISCOUNT = sumDiscountPercent * 100;
            invoice.INV_DET_TOTAL_DISCOUNT = sumDiscount;
            invoice.INV_DESCRIBTION = binding.edtDescription.getText().toString();
            invoice.INV_TOTAL_TAX = 0.0;
            invoice.INV_TOTAL_COST = 0.0;
            invoice.INV_EXTENDED_AMOUNT = sumPurePrice + transportCost;
            invoice.INV_DATE = date;

            int hour = date.getHours();
            try {
                hour = Integer.parseInt(chooseTimeDelivery.split("-")[0]);
            } catch (Exception ignored) {
            }

            invoice.INV_DUE_DATE = chooseDayDelivery;

            invoice.INV_DUE_TIME = hour + ":" + "00";

            invoice.INV_STATUS = true;

            if (Select.from(Account.class).first() == null) {
                Toast.makeText(getActivity(), "مشتری معتبر نمی باشد", Toast.LENGTH_SHORT).show();
                return;
            }

            invoice.ACC_CLB_UID = Select.from(Account.class).first().getI();

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
            if (!deliveryType.equals("-1") && !deliveryType.equals("")) {
                PaymentRecieptDetail cl = new PaymentRecieptDetail();
                cl.PAY_RCIPT_DET_DESCRIBTION = listInvoice.get(0).INV_DESCRIBTION;
                cl.PAY_RCIPT_DET_TOTAL_AMOUNT = listInvoice.get(0).INV_EXTENDED_AMOUNT;
                cl.PAY_RCIPT_DET_TYPE = deliveryType;
                clsPaymentRecieptDetails.add(cl);
            }


            customProgress.showProgress(getActivity(), "در حال ارسال سفارش...", false);
            myViewModel.sendOrder(userName, passWord, listInvoice, invoiceDetailList, clsPaymentRecieptDetails, "");
        });

        btnReturned.setOnClickListener(v -> {
            boolean saleinApp = Select.from(SaleinShop.class).first().isPublicApp();

            int size = Navigation.findNavController(binding.getRoot()).getBackQueue().size();
            int remain;

            if (saleinApp)
                remain = size - 3;
            else
                remain = size - 2;

            sharedPreferences.edit().putBoolean("vip", false).apply();
            sharedPreferences.edit().putBoolean("discount", false).apply();
            dialogSendOrder.dismiss();
            for (int i = 0; i < remain; i++) {
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });
    }

    private void resetDeliveryType() {
        deliveryType = "-1";
        binding.tvSuccessFullPayOnline.setText("");
        binding.ivOkClubPayment.setVisibility(View.GONE);
        binding.ivOkOnSitePayment.setVisibility(View.GONE);
    }

    private void intentToMapActivity() {
        NavDirections action = PaymentFragmentDirections.actionGoToMapActivity(0);
        Navigation.findNavController(binding.getRoot()).navigate(action);

    }


    //endregion Custom Method


}
