package ir.kitgroup.saleinmeat.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;

import ir.kitgroup.saleinmeat.Adapters.DescriptionAdapter;
import ir.kitgroup.saleinmeat.Adapters.InvoiceDetailMobileAdapter;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.classes.CustomProgress;
import ir.kitgroup.saleinmeat.Util.Utilities;

import ir.kitgroup.saleinmeat.DataBase.Invoice;
import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.DataBase.OrderType;
import ir.kitgroup.saleinmeat.DataBase.Product;
import ir.kitgroup.saleinmeat.DataBase.Setting;
import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.DataBase.User;


import ir.kitgroup.saleinmeat.models.Description;
import ir.kitgroup.saleinmeat.models.ModelDesc;

import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.Util.Util;
import ir.kitgroup.saleinmeat.databinding.FragmentInvoiceDetailMobileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InVoiceDetailMobileFragment extends Fragment {

    //region Parameter
    private FragmentInvoiceDetailMobileBinding binding;
    private String maxSales;
    private String userName;
    private String passWord;
    private String Inv_GUID;

    private double sumPrice;
    private double sumPurePrice;
    private double sumDiscounts;


    private List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetailMobileAdapter invoiceDetailAdapter;

    private CustomProgress customProgress;

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    private Dialog dialog;
    private TextView textExit;
    private ImageView ivIcon;
    private int imageIconDialog;

    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;

    private DescriptionAdapter descriptionAdapter;
    private ArrayList<Description> descriptionList;
    private String GuidInv;

    private List<InvoiceDetail> invDetails;

    private String status;
    private double sumTransport = 0.0;
    //endregion Variable Dialog Description


    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentInvoiceDetailMobileBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        customProgress = CustomProgress.getInstance();
        descriptionList = new ArrayList<>();
        invoiceDetailList = new ArrayList<>();


        List<Setting> setting = Select.from(Setting.class).list();
        if (setting.size() > 0)
            //maxSales = setting.get(0).MAX_SALE;
            maxSales ="1";

        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;


        //region Configuration Text Size
        int fontSize;
        int fontLargeSize;
        if (LauncherActivity.screenInches >= 7) {
            fontSize = 13;
            fontLargeSize = 14;
        } else {
            fontSize = 11;
            fontLargeSize = 12;
        }


        binding.tvNameCustomer.setTextSize(fontSize);
        binding.txtDate.setTextSize(fontSize);
        binding.txtTypeOrder.setTextSize(fontSize);
        binding.txtTableNumber.setTextSize(fontSize);


        binding.orderListPurePriceTv.setTextSize(fontSize);
        binding.tvSumPurePrice.setTextSize(fontLargeSize);
        binding.orderListSumPriceTv.setTextSize(fontLargeSize);
        binding.tvSumPrice.setTextSize(fontLargeSize);
        binding.orderListSumDiscountTv.setTextSize(fontLargeSize);
        binding.tvSumDiscount.setTextSize(fontLargeSize);
        binding.tvSumTransport.setTextSize(fontLargeSize);
        binding.txtSumTransport.setTextSize(fontLargeSize);


        binding.btnContinue.setTextSize(fontSize);
        binding.btnDelete.setTextSize(fontSize);
        binding.btnEdit.setTextSize(fontSize);
        binding.btnResend.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Cast Dialog Delete


            switch (LauncherActivity.name) {
                case "ir.kitgroup.salein":

                    imageIconDialog = R.drawable.saleinicon128;

                    break;

                case "ir.kitgroup.saleintop":

                    imageIconDialog = R.drawable.top_png;


                    break;


                case "ir.kitgroup.saleinmeat":

                    imageIconDialog = R.drawable.meat_png;


                    break;

                case "ir.kitgroup.saleinnoon":

                    imageIconDialog = R.drawable.noon;


                    break;
            }

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textExit = dialog.findViewById(R.id.tv_message);
        textExit.setText("آیا مایل به حذف کامل سبد خرید هستید؟");
        ivIcon = dialog.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imageIconDialog);

        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> {
            dialog.dismiss();

        });
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            List<InvoiceDetail> invoiceDetails=Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID+ "'").list();

            for (int i=0;i<invoiceDetails.size();i++){

                ArrayList<Product> resultPrd_ = new ArrayList<>(Util.AllProduct);

                int finalI = i;
                CollectionUtils.filter(resultPrd_, r -> r.getPRDUID().equals(invoiceDetails.get(finalI).PRD_UID));
                if (resultPrd_.size() > 0) {
                    Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd_.get(0))).setAmount(0.0);
                }
                InvoiceDetail.delete(invoiceDetails.get(i));

            }
            invoiceDetailList.clear();
            invoiceDetailAdapter.notifyDataSetChanged();



        });
        //endregion Cast Dialog Delete


        //region Get Bundle
        Bundle bundle = getArguments();
        String type = bundle.getString("type");  //1 seen   //2 Edit
        Inv_GUID = bundle.getString("Inv_GUID");
        String Tbl_GUID = bundle.getString("Tbl_GUID");
        String Ord_TYPE = bundle.getString("Ord_TYPE");
        String Acc_NAME = bundle.getString("Acc_NAME");
        String Acc_GUID = bundle.getString("Acc_GUID");
        boolean edit = bundle.getBoolean("EDIT");
        //endregion Get Bundle


        //region Configuration Client Application
        if (App.mode == 2) {
            binding.tvNameCustomer.setVisibility(View.GONE);
            binding.txtTableNumber.setVisibility(View.GONE);
            binding.txtTypeOrder.setVisibility(View.GONE);
        }
        //endregion Configuration Client Application




        //region Configuration Organization Application And Edit Mode For Save Order
        if (App.mode == 1 && type.equals("2")) {
            binding.layoutSave.setVisibility(View.VISIBLE);

            binding.layoutSave.setOnClickListener(v -> {
                Date date = Calendar.getInstance().getTime();

                double sumDiscount = 0.0;
                double sumDiscountPercent = 0.0;
                List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();


                for (int i = 0; i < invDetails.size(); i++) {
                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invDetails.get(finalI).PRD_UID));
                    if (prdResult.size() > 0) {
                        double sumTotalPrice = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());//جمع کل ردیف
                        double discountPrice = sumTotalPrice * (prdResult.get(0).PERC_DIS / 100);//جمع تخفیف ردیف
                        double totalPurePrice = sumTotalPrice - discountPrice;//جمع خالص ردیف


                        sumPurePrice = sumPurePrice + totalPurePrice; //جمع خالص فاکتور
                        sumPrice = sumPrice + sumTotalPrice; // جمع کل فاکتور


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
                invoice.INV_TOTAL_AMOUNT = sumPurePrice;//جمع فاکنور
                invoice.INV_TOTAL_DISCOUNT = 0.0;
                invoice.INV_PERCENT_DISCOUNT = sumDiscountPercent;
                invoice.INV_DET_TOTAL_DISCOUNT = sumDiscount;
                invoice.INV_DESCRIBTION = "";
                invoice.INV_TOTAL_TAX = 0.0;
                invoice.INV_TOTAL_COST = 0.0;
                invoice.INV_EXTENDED_AMOUNT = sumPrice;
                invoice.INV_DATE = date;
                invoice.INV_DUE_DATE = date;
                invoice.INV_STATUS = true;
                invoice.ACC_CLB_UID = Acc_GUID;
                invoice.TBL_UID = Tbl_GUID;
                invoice.INV_TYPE_ORDER = Integer.parseInt(Ord_TYPE);
                invoice.Acc_name = Acc_NAME;
                invoice.save();

                Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
                if (tb != null) {
                    tb.SV = true;
                    tb.save();
                }
                assert getFragmentManager() != null;

                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("LauncherFragment");
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                if (frg != null) {
                    final int size1 = getActivity().getSupportFragmentManager().getBackStackEntryCount();

                    for (int i = 1; i <= size1; i++) {
                        getFragmentManager().popBackStack();
                    }

                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }


            });
        }
        //endregion Configuration Organization Application And Edit Mode For Save Order

        //region Action BtnResend
        binding.btnResend.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("Inv_GUID", Inv_GUID);
            bundle1.putString("Tbl_GUID", "");
            bundle1.putString("Acc_NAME", Acc_NAME);
            bundle1.putString("Acc_GUID", Acc_GUID);
            bundle1.putString("Ord_TYPE", "");
            if (edit)
                bundle1.putBoolean("EDIT", true);

            bundle1.putString("Sum_PURE_PRICE", String.valueOf(sumPurePrice));
            bundle1.putString("Sum_PRICE", String.valueOf(sumPrice));

            PaymentMobileFragment paymentFragment = new PaymentMobileFragment();
            paymentFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, paymentFragment, "PaymentFragment").addToBackStack("PaymentF").commit();
        });
        //endregion Action BtnResend





        sumPrice = 0;
        sumPurePrice = 0;
        sumDiscounts = 0;
        invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();






        //region Cast DialogDescription
        dialogDescription = new Dialog(getActivity());
        dialogDescription.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDescription.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDescription.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialogDescription.setContentView(R.layout.dialog_description);
        dialogDescription.setCancelable(false);
        edtDescriptionItem = dialogDescription.findViewById(R.id.edt_description);
        MaterialButton btnRegisterDescription = dialogDescription.findViewById(R.id.btn_register_description);
        RecyclerView recyclerDescription = dialogDescription.findViewById(R.id.recyclerView_description);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 3) {
            @Override
            protected boolean isLayoutRTL() {
                return true;
            }
        };
        recyclerDescription.setLayoutManager(gridLayoutManager1);
        descriptionAdapter = new DescriptionAdapter(getActivity(), descriptionList);
        recyclerDescription.setAdapter(descriptionAdapter);


        descriptionAdapter.setOnClickItemListener((desc, click, position) -> {
            if (click) {
                descriptionList.get(position).Click = true;
                edtDescriptionItem.setText(edtDescriptionItem.getText().toString() + "   " + "'" + desc + "'");
            } else {
                descriptionList.get(position).Click = false;
                if (edtDescriptionItem.getText().toString().contains("'" + desc + "'"))

                    edtDescriptionItem.setText(edtDescriptionItem.getText().toString().replace("   " + "'" + desc + "'", ""));
            }
        });


        btnRegisterDescription.setOnClickListener(v -> {
            ArrayList<Product> resultPrd = new ArrayList<>();
            InvoiceDetail invDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + GuidInv + "'").first();

            if (invDetail != null) {
                invDetail.INV_DET_DESCRIBTION = edtDescriptionItem.getText().toString();
                invDetail.update();
                /*    if (LauncherActivity.screenInches < 7) {*/
                resultPrd.addAll(Util.AllProduct);
                CollectionUtils.filter(resultPrd, r -> r.I.equals(invDetail.PRD_UID));
                // }

            }

            if (resultPrd.size() > 0) {
                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).descItem = edtDescriptionItem.getText().toString();
            }


            invoiceDetailAdapter.notifyDataSetChanged();
            dialogDescription.dismiss();
        });
        //endregion Cast DialogDescription



        //region Set Parameter Toolbar
        Utilities util = new Utilities();
        Locale loc = new Locale("en_US");

        //region Seen Order After Send It
        if (type.equals("1")) {

            binding.layoutContinue.setVisibility(View.GONE);
            binding.layoutEditDelete.setVisibility(View.VISIBLE);

            Invoice invoice = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
            if (invoice != null) {

                status = invoice.INV_SYNC;

                if (App.mode == 2) {
                    if (status != null && status.equals("*")) {
                        binding.btnResend.setVisibility(View.GONE);
                        binding.btnDelete.setVisibility(View.GONE);
                        binding.btnEdit.setVisibility(View.GONE);
                    } else if (status != null && status.equals("-")) {
                        binding.btnResend.setVisibility(View.GONE);
                        binding.btnDelete.setVisibility(View.VISIBLE);
                        binding.btnEdit.setVisibility(View.VISIBLE);
                    } else {
                        binding.btnResend.setVisibility(View.VISIBLE);
                        binding.btnDelete.setVisibility(View.VISIBLE);
                        binding.btnEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.btnResend.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.VISIBLE);
                    binding.btnEdit.setVisibility(View.VISIBLE);
                }


                Tables tbl1 = Select.from(Tables.class).where("I ='" + invoice.TBL_UID + "'").first();

                if (tbl1 != null)
                    binding.txtTableNumber.setText("شماره میز : " + tbl1.N);


                OrderType orderType = Select.from(OrderType.class).where("c ='" + invoice.INV_TYPE_ORDER + "'").first();
                if (orderType != null) {
                    binding.txtTypeOrder.setText(orderType.getN());
                }

                if (App.mode == 1) {
                    Account ac = Select.from(Account.class).first();
                    if (ac != null)
                        binding.tvNameCustomer.setText(ac.N);
                }

                if (invoice.INV_DUE_DATE != null) {
                    Utilities.SolarCalendar sc = util.new SolarCalendar(invoice.INV_DUE_DATE);
                    binding.txtDate.setText(invoice.INV_DUE_DATE.getHours() + ":" + invoice.INV_DUE_DATE.getMinutes() + " " + (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year);

                } else {
                    if (invoice.INV_DUE_DATE1 != null)
                        binding.txtDate.setText(invoice.INV_DUE_DATE1);
                }

            }

        }
        //endregion Seen Order After Send It



        //region See Order During Work
        else {
            Tables tbl1 = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
            if (tbl1 != null)
                binding.txtTableNumber.setText("شماره میز : " + tbl1.N);


            Calendar calendar = Calendar.getInstance();
            Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
            String text2 = (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year;
            binding.txtDate.setText(text2);


            OrderType ordTY = Select.from(OrderType.class).where("c ='" + Ord_TYPE + "'").first();

            if (ordTY != null)
                binding.txtTypeOrder.setText(ordTY.getN());


            if (App.mode == 1)
                binding.tvNameCustomer.setText(Acc_NAME);

        }
        //endregion See Order During Work

        //endregion Set Parameter Toolbar




        //region CONFIGURATION DATA INVOICE_DETAIL

        invoiceDetailList.clear();


        if (type.equals("1") || edit) {
            for (int i = 0; i < invDetails.size(); i++) {
                if (invDetails.get(i).INV_DET_DESCRIBTION != null && invDetails.get(i).INV_DET_DESCRIBTION.equals("توزیع")) {
                    sumTransport = Double.parseDouble(invDetails.get(i).INV_DET_TOTAL_AMOUNT);
                    invDetails.remove(invDetails.get(i));
                    binding.tvSumTransport.setText(format.format(sumTransport) + " ریال ");
                    binding.layoutTransport.setVisibility(View.VISIBLE);

                }
            }
        }


        if (type.equals("2")){
           binding.layoutTransport.setVisibility(View.GONE);
        }


        invoiceDetailList.addAll(invDetails);


        for (int i = 0; i < invDetails.size(); i++) {

            ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
            int finalI = i;
            CollectionUtils.filter(prdResult, p -> p.I.equals(invDetails.get(finalI).PRD_UID));

            if (prdResult.size() > 0) {
                double sumpriceE = (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                double discountPrice = sumpriceE * (prdResult.get(0).PERC_DIS / 100);
                double totalPrice = sumpriceE - discountPrice;

                sumPrice = sumPrice + (invDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                sumPurePrice = sumPurePrice + totalPrice;
                sumDiscounts = sumDiscounts + discountPrice;

            }

        }


        if (type.equals("1"))
        binding.tvSumPurePrice.setText(format.format(sumPurePrice+sumTransport) + " ریال ");
        else
            binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");

        binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
        binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");


        invoiceDetailAdapter = new InvoiceDetailMobileAdapter(getActivity(), invoiceDetailList, type);
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDetailInvoice.setHasFixedSize(false);
        binding.recyclerDetailInvoice.setAdapter(invoiceDetailAdapter);
        binding.recyclerDetailInvoice.setNestedScrollingEnabled(false);



        invoiceDetailAdapter.editAmountItemListener((Prd_GUID, s, Price, discountPercent) -> {

            if (maxSales.equals("1")) {
                getMaxSales(userName, passWord, Prd_GUID, s);
            } else {
                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));
                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    double amount = 0.0;
                    if (!s.equals(""))
                        amount = Double.parseDouble(s);

                    if (invoiceDetail != null) {
                        invoiceDetail.INV_DET_QUANTITY = amount;
                        invoiceDetail.update();
                    }


                    ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                    CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(Prd_GUID));
                    if (resultPrd.size() > 0) {
                        Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);

                    }
                    sumPrice = 0;
                    sumPurePrice = 0;
                    sumDiscounts = 0;


                    List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                    for (int i = 0; i < invoiceDetails.size(); i++) {

                        ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                        int finalI = i;
                        CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetails.get(finalI).PRD_UID));

                        if (prdResult.size() > 0) {
                            double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                            double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                            double totalPrice = sumprice - discountPrice;

                            sumPrice = sumPrice + (invoiceDetails.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                            sumPurePrice = sumPurePrice + totalPrice;
                            sumDiscounts = sumDiscounts + discountPrice;

                        }

                    }

                    binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");
                    binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
                    binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");
                    invoiceDetailAdapter.notifyDataSetChanged();

                }
            }


        });



        invoiceDetailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                sumPrice = 0;
                sumPurePrice = 0;
                sumDiscounts = 0;


                List<InvoiceDetail> invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();


                for (int i = 0; i < invoiceDetail.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetail.get(finalI).PRD_UID));

                    if (prdResult.size() > 0) {
                        double sumprice = (invoiceDetail.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                        double totalPrice = sumprice - discountPrice;

                        sumPrice = sumPrice + (invoiceDetail.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        sumPurePrice = sumPurePrice + totalPrice;
                        sumDiscounts = sumDiscounts + discountPrice;

                    }

                }


                binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");
                binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
                binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                sumPrice = 0;
                sumPurePrice = 0;
                sumDiscounts = 0;


                ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                CollectionUtils.filter(result, r -> r.PRD_UID.equals(invoiceDetailList.get(positionStart).PRD_UID));
                if (result.size() > 0) {
                    InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                    if (invoiceDetail != null)
                        invoiceDetail.delete();
                }

                invoiceDetailList.remove(invoiceDetailList.get(positionStart));


                for (int i = 0; i < invoiceDetailList.size(); i++) {

                    ArrayList<Product> prdResult = new ArrayList<>(Util.AllProduct);
                    int finalI = i;
                    CollectionUtils.filter(prdResult, p -> p.I.equals(invoiceDetailList.get(finalI).PRD_UID));

                    if (prdResult.size() > 0) {
                        double sumprice = (invoiceDetailList.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        double discountPrice = sumprice * (prdResult.get(0).PERC_DIS / 100);
                        double totalPrice = sumprice - discountPrice;

                        sumPrice = sumPrice + (invoiceDetailList.get(i).INV_DET_QUANTITY * prdResult.get(0).getPRDPRICEPERUNIT1());
                        sumPurePrice = sumPurePrice + totalPrice;
                        sumDiscounts = sumDiscounts + discountPrice;

                    }

                }


                binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");
                binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
                binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");


            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);


            }
        });



        invoiceDetailAdapter.onDescriptionItem((GUIDPrd, GUIDInv, description) -> {

            edtDescriptionItem.setText(description);
            descriptionList.clear();
            GuidInv = GUIDInv;
            getDescription(userName, passWord, GUIDPrd);
        });




        edtDescriptionItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    for (int i = 0; i < descriptionList.size(); i++) {
                        descriptionList.get(i).Click = false;
                    }
                    descriptionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //endregion CONFIGURATION DATA INVOICE_DETAIL


        //region Action BtnDelete
        binding.btnDelete.setOnClickListener(v -> {

            Tables tb = Select.from(Tables.class).where("I ='" + Tbl_GUID + "'").first();
            if (tb != null) {
                tb.SV = false;
                tb.save();
            }
            if (status.equals("-"))
                getDeleteInvoice(userName, passWord, Inv_GUID);


        });
        //endregion Action BtnDelete



        //region Action BtnEdit
        binding.btnEdit.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            bundle.putString("Ord_TYPE", Ord_TYPE);
            bundle.putString("Tbl_GUID", Tbl_GUID);
            bundle.putString("Inv_GUID", Inv_GUID);
            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
            mainOrderMobileFragment.setArguments(bundle);
            FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment")).addToBackStack("MainOrderMobileFX");
            replaceFragment.commit();
        });
        //endregion Action BtnEdit



        //region Action BtnContinue
        binding.btnContinue.setOnClickListener(v -> {
            if (invoiceDetailList.size() == 0) {
                Toast.makeText(getContext(), "سفارشی وجود ندارد", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("Inv_GUID", Inv_GUID);
            bundle1.putString("Tbl_GUID", Tbl_GUID);
            bundle1.putString("Acc_NAME", Acc_NAME);
            bundle1.putString("Acc_GUID", Acc_GUID);


            if (App.mode == 2)
                bundle1.putString("Ord_TYPE", "");
            else
                bundle1.putString("Ord_TYPE", Ord_TYPE);

            if (edit)
                bundle1.putBoolean("EDIT", true);

            bundle1.putString("Sum_PURE_PRICE", String.valueOf(sumPurePrice));
            bundle1.putString("Sum_PRICE", String.valueOf(sumPrice));
            PaymentMobileFragment paymentFragment = new PaymentMobileFragment();
            paymentFragment.setArguments(bundle1);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, paymentFragment, "PaymentFragment").addToBackStack("PaymentF").commit();
        });
        //endregion Action BtnContinue



        binding.txtDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invoiceDetailList.size()>0)
                dialog.show();
            }
        });


    }


    private void getDescription(String userName, String pass, String id) {

        customProgress.showProgress(getActivity(), "در حال دریافت توضیحات...", false);

        try {

            Call<String> call = App.api.getDescription(userName, pass, id);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelDesc>() {
                    }.getType();
                    ModelDesc iDs = null;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception ignored) {
                    }

                    descriptionList.clear();
                    if (iDs != null)
                        descriptionList.addAll(iDs.getDescriptions());


                    for (int i = 0; i < descriptionList.size(); i++) {
                        if (edtDescriptionItem.getText().toString().contains("'" + descriptionList.get(i).DSC + "'")) {
                            descriptionList.get(i).Click = true;
                        }

                    }

                    descriptionAdapter.notifyDataSetChanged();
                    customProgress.hideProgress();

                    dialogDescription.show();


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }

    private void getMaxSales(String userName, String pass, String Prd_GUID, String s) {


        try {

            Call<String> call = App.api.getMaxSales(userName, pass, Prd_GUID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    customProgress.hideProgress();

                    int remain = -1000000000;
                    try {
                        remain = Integer.parseInt(response.body());
                    } catch (Exception e) {
                        Gson gson = new Gson();
                        Type typeIDs = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                        int message = iDs.getLogs().get(0).getMessage();
                        String description = iDs.getLogs().get(0).getDescription();
                        if (message != 1) {
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (remain != -1000000000) {


                        ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
                        ArrayList<Product> resultPrd1 = new ArrayList<>(Util.AllProduct);

                        CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_GUID));
                        CollectionUtils.filter(resultPrd1, r -> r.I.equals(Prd_GUID));
                        if (result.size() > 0) {
                            InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                            double amount = 0.0;
                            if (!s.equals("")) {
                                amount = Double.parseDouble(s);
                                if (remain - amount < 0) {
                                    Toast.makeText(getActivity(), "مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + response.body(), Toast.LENGTH_SHORT).show();

                                    if (invoiceDetail != null) {
                                        invoiceDetail.INV_DET_QUANTITY = 0.0;
                                        invoiceDetail.update();
                                        if (resultPrd1.size() > 0) {
                                            resultPrd1.get(0).AMOUNT = 0.0;
                                        }
                                    }

                                    invoiceDetailAdapter.notifyDataSetChanged();
                                    customProgress.hideProgress();
                                    return;
                                }
                            }

                            if (invoiceDetail != null) {
                                invoiceDetail.INV_DET_QUANTITY = amount;
                                invoiceDetail.update();

                            }

                            ArrayList<Product> resultPrd = new ArrayList<>(Util.AllProduct);
                            CollectionUtils.filter(resultPrd, r -> r.getPRDUID().equals(Prd_GUID));
                            if (resultPrd.size() > 0) {
                                Util.AllProduct.get(Util.AllProduct.indexOf(resultPrd.get(0))).setAmount(amount);

                            }


                            invoiceDetailAdapter.notifyDataSetChanged();

                        }

                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();


                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }

   /* private void getInVoice(String userName, String pass, String Inv_GUID) {

        customProgress.showProgress(getActivity(), "در حال دریافت تغییرات فاکتور...", false);

        try {


            Call<String> call = App.api.getInvoice(userName, pass, Inv_GUID);

            call.enqueue(new Callback<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<String> call, Response<String> response) {


                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelInvoice>() {
                    }.getType();
                    ModelInvoice iDs = null;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception ignored) {
                    }

                    if (iDs != null) {
                        Invoice inv = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
                        if (inv != null) {
                            inv.delete();
                            Invoice.saveInTx(iDs.getInvoice());


                            status = iDs.getInvoice().get(0).INV_SYNC;

                            if (App.mode == 2) {
                                if (status != null && status.equals("*")) {
                                    binding.btnResend.setVisibility(View.GONE);
                                    binding.btnDelete.setVisibility(View.GONE);
                                    binding.btnEdit.setVisibility(View.GONE);
                                } else if (status != null && status.equals("-")) {
                                    binding.btnResend.setVisibility(View.GONE);
                                    binding.btnDelete.setVisibility(View.VISIBLE);
                                    binding.btnEdit.setVisibility(View.VISIBLE);
                                } else {
                                    binding.btnResend.setVisibility(View.VISIBLE);
                                    binding.btnDelete.setVisibility(View.VISIBLE);
                                    binding.btnEdit.setVisibility(View.VISIBLE);
                                }
                            } else {
                                binding.btnDelete.setVisibility(View.VISIBLE);
                                binding.btnEdit.setVisibility(View.VISIBLE);
                                binding.btnResend.setVisibility(View.GONE);
                            }


                            Tables tbl1 = Select.from(Tables.class).where("I ='" + iDs.getInvoice().get(0).TBL_UID + "'").first();

                            if (tbl1 != null)
                                binding.txtTableNumber.setText("شماره میز : " + tbl1.N);


                            OrderType orderType = Select.from(OrderType.class).where("c ='" + iDs.getInvoice().get(0).INV_TYPE_ORDER + "'").first();
                            if (orderType != null) {
                                binding.txtTypeOrder.setText(orderType.getN());
                            }

                            if (App.mode == 1) {
                                Account ac = Select.from(Account.class).first();
                                if (ac != null)
                                    binding.tvNameCustomer.setText(ac.N);
                            }


                            if (iDs.getInvoice().get(0).INV_DUE_DATE1 != null)
                                binding.txtDate.setText(iDs.getInvoice().get(0).INV_DUE_DATE1);


                        }
                        List<InvoiceDetail> invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                        for (int i = 0; i < invoiceDetail.size(); i++) {
                            InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                        }


                        InvoiceDetail.saveInTx(iDs.getInvoiceDetail());


                        invoiceDetailList.clear();
                        invoiceDetailList.addAll(iDs.getInvoiceDetail());
                        invoiceDetailAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), "دریافت آخرین اطلاعات ناموفق", Toast.LENGTH_SHORT).show();
                    }
                    customProgress.hideProgress();

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }*/

    private void getDeleteInvoice(String userName, String pass, String Inv_GUID) {

        customProgress.showProgress(getActivity(), "در حال دریافت تغییرات فاکتور...", false);

        try {


            Call<String> call = App.api.getDeleteInvoice(userName, pass, Inv_GUID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    if (iDs != null) {
                        int message = iDs.getLogs().get(0).getMessage();
                        String description = iDs.getLogs().get(0).getDescription();
                        if (message == 4) {


                            Invoice inv = Select.from(Invoice.class).where("INVUID ='" + Inv_GUID + "'").first();
                            if (inv != null)
                                inv.delete();
                            List<InvoiceDetail> invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                            for (int i = 0; i < invoiceDetail.size(); i++) {
                                InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                            }

                            assert getFragmentManager() != null;
                            getFragmentManager().popBackStack();
                            Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("OrderListFragment");
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            assert frg != null;
                            ft.detach(frg);
                            ft.attach(frg);
                            ft.commit();
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "خطایی در سرور رخ داد", Toast.LENGTH_SHORT).show();
                    }


                    customProgress.hideProgress();

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    customProgress.hideProgress();
                    Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + t.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            customProgress.hideProgress();
            Toast.makeText(getActivity(), "خطا در دریافت اطلاعات فاکتور..." + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }
}
