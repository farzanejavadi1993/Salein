package ir.kitgroup.saleindemo.ui.launcher.invoiceItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

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

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleindemo.ui.launcher.homeItem.DescriptionAdapter;
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.classes.CustomProgress;
import ir.kitgroup.saleindemo.classes.Utilities;

import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;


import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.models.Description;
import ir.kitgroup.saleindemo.R;

import ir.kitgroup.saleindemo.databinding.FragmentInvoiceDetailMobileBinding;
import ir.kitgroup.saleindemo.models.Product;

@AndroidEntryPoint

public class InVoiceDetailFragment extends Fragment {

    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;
    private FragmentInvoiceDetailMobileBinding binding;
    private MyViewModel myViewModel;
    private Company company;
    private CustomProgress customProgress;


    private String Prd_UID;
    private String sWord;


    private String type = "";//This Variable Show We Come To This Fragment From Which Fragment If Amount Equal "2" Mean We Come To This Fragment From Home Fragment And We Arent Going To Edit Order
    private String Inv_GUID = "";
    private boolean EDIT = false;

    private double sumPrice;
    private double sumPurePrice;
    private double sumDiscounts;

    private List<InvoiceDetail> invDetails;
    private   List<InvoiceDetail> invoiceDetailList;
    private InvoiceDetailAdapter invoiceDetailAdapter;

    private final DecimalFormat format = new DecimalFormat("#,###,###,###");


    //region Dialog Sync
    private Dialog dialogSync;
    //endregion Dialog Sync


    //region Variable Dialog
    private Dialog dialogDelete;
    //endregion Variable Dialog


    //region Variable Dialog Description
    private Dialog dialogDescription;
    private EditText edtDescriptionItem;
    private DescriptionAdapter descriptionAdapter;
    private ArrayList<Description> descriptionList;
    private String GuidInv;
    //endregion Variable Dialog Description


    private Integer status;
    private double sumTransport = 0.0;
    private String maxSales = "0";
    private String Transport_GUID = "";


    private int counter = 0;
    private  ArrayList<String> closeDayList;
    private String valueOfDay;


    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentInvoiceDetailMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        try {

            ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);

            Calendar calendar1 = Calendar.getInstance();
            switch (calendar1.getTime().getDay()) {

                case 0:
                     valueOfDay = "1";
                    break;
                case 1:
                    valueOfDay = "2";
                    break;
                case 2:
                    valueOfDay = "3";
                    break;
                case 3:
                    valueOfDay = "4";
                    break;
                case 4:
                    valueOfDay = "5";
                    break;
                case 5:
                    valueOfDay = "6";
                    break;
                case 6:
                    valueOfDay = "0";
                    break;
            }
             Inv_GUID = sharedPreferences.getString("Inv_GUID", "");
            String CloseDay = sharedPreferences.getString("close_day", "");

            closeDayList = new ArrayList<>(); //This Variable Is For Get holidays From Server
            if (!CloseDay.equals("")) {
                closeDayList = new ArrayList<>(Arrays.asList(CloseDay.split(",")));
            }


            sharedPreferences.edit().putString("FNM", "invoice").apply();
            customProgress = CustomProgress.getInstance();
            company = Select.from(Company.class).first();


            invDetails = new ArrayList<>();
            invoiceDetailList = new ArrayList<>();
            descriptionList = new ArrayList<>();
            maxSales = sharedPreferences.getString("maxSale", "0");
            Transport_GUID = sharedPreferences.getString("Transport_GUID", "");


            //region Cast Dialog Delete
            dialogDelete = new Dialog(getActivity());
            dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogDelete.setContentView(R.layout.custom_dialog);
            dialogDelete.setCancelable(false);

            TextView textExit = dialogDelete.findViewById(R.id.tv_message);
            textExit.setText("آیا مایل به حذف کامل سبد خرید هستید؟");


            MaterialButton btnOk = dialogDelete.findViewById(R.id.btn_ok);
            MaterialButton btnNo = dialogDelete.findViewById(R.id.btn_cancel);
            btnNo.setOnClickListener(v -> dialogDelete.dismiss());
            btnOk.setOnClickListener(v -> {

                binding.progressBar.setVisibility(View.VISIBLE);
                List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

                for (int i = 0; i < invoiceDetails.size(); i++) {
                    InvoiceDetail.delete(invoiceDetails.get(i));
                }
                invoiceDetailList.clear();
                invoiceDetailAdapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
                dialogDelete.dismiss();

            });
            //endregion Cast Dialog Delete


            //region Get Bundle

            type =  InVoiceDetailFragmentArgs.fromBundle(getArguments()).getType();//1 Edit  //2 create
            EDIT =  InVoiceDetailFragmentArgs.fromBundle(getArguments()).getEdit();//when order need EDIT
            //endregion Get Bundle



            sumPrice = 0;
            sumPurePrice = 0;
            sumDiscounts = 0;


            //region Cast Variable Dialog Sync
            dialogSync = new Dialog(getActivity());
            dialogSync.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogSync.setContentView(R.layout.custom_dialog);
            dialogSync.setCancelable(false);



            MaterialButton btnOkDialog = dialogSync.findViewById(R.id.btn_ok);
            MaterialButton btnNoDialog = dialogSync.findViewById(R.id.btn_cancel);
            btnNoDialog.setOnClickListener(v -> dialogSync.dismiss());


            btnOkDialog.setOnClickListener(v -> {
                dialogSync.dismiss();
                binding.progressBar.setVisibility(View.VISIBLE);
                if (type.equals("1"))
                    myViewModel.getInvoice(company.getUser(),company.getPass(),Inv_GUID);
                else {
                    invDetails.clear();
                    invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                    if (invDetails.size() == 0) {
                        binding.progressBar.setVisibility(View.GONE);
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setMessage("سفارشی وجود ندارد")
                                .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                                .show();

                        TextView textView =alertDialog.findViewById(android.R.id.message);
                        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                        textView.setTypeface(face);
                        textView.setTextColor(getResources().getColor(R.color.red_table));
                        textView.setTextSize(13);

                    } else {
                        counter = 0;
                        ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);
                        for (int i = 0; i < invDetails.size(); i++) {
                            myViewModel.getProduct(company.getUser(),company.getPass(),invDetails.get(i).PRD_UID);
                        }
                    }
                }

            });

            //endregion Cast Variable Dialog Sync


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


            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
            flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
            flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
            flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
            flexboxLayoutManager.setAlignItems(AlignItems.BASELINE);
            recyclerDescription.setLayoutManager(flexboxLayoutManager);
            recyclerDescription.setLayoutManager(flexboxLayoutManager);
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

                InvoiceDetail invDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + GuidInv + "'").first();

                if (invDetail != null) {
                    invDetail.INV_DET_DESCRIBTION = edtDescriptionItem.getText().toString();
                    invDetail.update();


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
                binding.txtDeleteAll.setVisibility(View.GONE);
                binding.layoutContinue.setVisibility(View.GONE);
            }
            //endregion Seen Order After Send It


            //region See Order During Work
            else {

                Calendar calendar = Calendar.getInstance();
                Utilities.SolarCalendar sc = util.new SolarCalendar(calendar.getTime());
                String text2 = (sc.strWeekDay) + "\t" + String.format(loc, "%02d", sc.date) + "\t" + (sc.strMonth) + "\t" + sc.year;
                binding.txtDate.setText(text2);

            }
            //endregion See Order During Work


            //endregion Set Parameter Toolbar


            //region CONFIGURATION DATA INVOICE_DETAIL

            invoiceDetailList.clear();


            if (type.equals("1") && !EDIT)
                binding.tvSumPurePrice.setText(format.format(sumPurePrice + sumTransport) + " ریال ");
            else
                binding.tvSumPurePrice.setText(format.format(sumPurePrice) + " ریال ");


            binding.tvSumPrice.setText(format.format(sumPrice) + " ریال ");
            binding.tvSumDiscount.setText(format.format(sumDiscounts) + " ریال ");


            invoiceDetailAdapter = new InvoiceDetailAdapter(getActivity(), invoiceDetailList, type, sharedPreferences);
            binding.recyclerDetailInvoice.setAdapter(invoiceDetailAdapter);
            binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerDetailInvoice.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerDetailInvoice.setHasFixedSize(false);
            binding.recyclerDetailInvoice.setNestedScrollingEnabled(false);


            invoiceDetailAdapter.editAmountItemListener((Prd_GUID, s, Price, discountPercent) -> {

                if (maxSales.equals("1")) {
                    Prd_UID = Prd_GUID;
                    sWord=s;
                    myViewModel.getMaxSale(company.getUser(), company.getPass(), Prd_GUID);
                } else {

                    List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                    ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetails);
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

                        invoiceDetailAdapter.notifyDataSetChanged();

                    } }
            });


            invoiceDetailAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    sumPrice = 0;
                    sumPurePrice = 0;
                    sumDiscounts = 0;

                    List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();

                    if (invoiceDetails.size() > 0) {
                        CollectionUtils.filter(invoiceDetails, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
                    }


                    for (int i = 0; i < invoiceDetails.size(); i++) {
                        ir.kitgroup.saleindemo.DataBase.Product product = Select.from(ir.kitgroup.saleindemo.DataBase.Product.class).where("I ='" + invoiceDetails.get(i).PRD_UID + "'").first();
                        if (product != null) {
                            double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));

                            double discountPrice;
                            if (type.equals("1"))
                                discountPrice = sumprice * (invoiceDetails.get(i).INV_DET_PERCENT_DISCOUNT / 100);
                            else
                                discountPrice = sumprice * (product.getPercDis() / 100);
                            double totalPrice = sumprice - discountPrice;

                            sumPrice = sumPrice + (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));
                            sumPurePrice = sumPurePrice + totalPrice;
                            sumDiscounts = sumDiscounts + discountPrice;

                        }

                    }


                    binding.tvSumPurePrice.setText(format.format(sumPurePrice + sumTransport) + " ریال ");
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

                    List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                    for (int i = 0; i < invoiceDetails.size(); i++) {


                        ir.kitgroup.saleindemo.DataBase.Product product = Select.from(ir.kitgroup.saleindemo.DataBase.Product.class).where("I ='" + invoiceDetails.get(i).PRD_UID + "'").first();
                        if (product != null) {
                            double sumprice = (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));
                            double discountPrice = sumprice * (product.getPercDis() / 100);
                            double totalPrice = sumprice - discountPrice;

                            sumPrice = sumPrice + (invoiceDetails.get(i).INV_DET_QUANTITY * product.getPrice(sharedPreferences));
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
                customProgress.showProgress(getActivity(),"در حال دریافت تضیحات",true);
                myViewModel.getDescription(company.getUser(),company.getPass(),GUIDPrd);
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
                if (status == 1) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    myViewModel.getDeleteInvoice(company.getUser(), company.getPass(), Inv_GUID);
                }
            });

            //endregion Action BtnDelete


            //region Action BtnEdit
            binding.btnEdit.setOnClickListener(v -> {
                 if (closeDayList.contains(valueOfDay)) {
                    Toasty.warning(getActivity(), "فروشگاه تعطیل می باشد.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (invoiceDetailList.size() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage("سفارشی وجود ندارد")
                            .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                            .show();

                    TextView textView =  alertDialog.findViewById(android.R.id.message);
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                    textView.setTypeface(face);
                    textView.setTextColor(getResources().getColor(R.color.red_table));
                    textView.setTextSize(13);
                    return;
                }
                NavDirections action = InVoiceDetailFragmentDirections.actionGoToMainFragment(Inv_GUID);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            });
            //endregion Action BtnEdit


            //region Action BtnContinue
            binding.btnContinue.setOnClickListener(v -> {

                if (closeDayList.contains(valueOfDay)) {
                    Toasty.warning(getActivity(), "فروشگاه تعطیل می باشد.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                ArrayList<InvoiceDetail> invoiceDetails = new ArrayList<>(Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list());
                CollectionUtils.filter(invoiceDetails, i -> i.INV_DET_QUANTITY == 0.0);


                if (invoiceDetailList.size() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage("سفارشی وجود ندارد")
                            .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                            .show();

                    TextView textView =  alertDialog.findViewById(android.R.id.message);
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                    textView.setTypeface(face);
                    textView.setTextColor(getResources().getColor(R.color.red_table));
                    textView.setTextSize(13);
                    return;
                } else if (invoiceDetails.size() > 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage("برای ردیف های سفارش مقدار وارد کنید و یا ردیف با مقدار صفر را حذف کنید .")
                            .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                            .show();
                    TextView textView =  alertDialog.findViewById(android.R.id.message);
                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                    textView.setTypeface(face);
                    textView.setTextColor(getResources().getColor(R.color.red_table));
                    textView.setTextSize(13);

                    return;
                }
                binding.btnContinue.setEnabled(false);
                NavDirections action = InVoiceDetailFragmentDirections.actionGoToPaymentFragment(EDIT);
                Navigation.findNavController(binding.getRoot()).navigate(action);

            });
            //endregion Action BtnContinue


            binding.txtDeleteAll.setOnClickListener(v -> {
                if (invoiceDetailList.size() > 0)
                    dialogDelete.show();
            });




        } catch (Exception ignore) {

        }
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        if (type.equals("1")) {
            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getInvoice(company.getUser(),company.getPass(),Inv_GUID);
        }
        else {
            invDetails.clear();
            invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() == 0) {
                binding.progressBar.setVisibility(View.GONE);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("هیچ سفارشی وجود ندارد.")
                        .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                        .show();

                TextView textView =  alertDialog.findViewById(android.R.id.message);
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                textView.setTypeface(face);
                textView.setTextColor(getResources().getColor(R.color.red_table));
                textView.setTextSize(13);

            } else {
                counter = 0;
                ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);
                for (int i = 0; i < invDetails.size(); i++) {
                    myViewModel.getProduct(company.getUser(),company.getPass(),invDetails.get(i).PRD_UID);
                }
            }
        }

        myViewModel.getResultInvoice().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            myViewModel.getResultInvoice().setValue(null);

            if (result.getInvoice().size() == 0) {
                invDetails.clear();
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("این سفارش در سرور وجود ندارد.")
                        .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                        .show();

                TextView textView = alertDialog.findViewById(android.R.id.message);
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                textView.setTypeface(face);
                textView.setTextColor(getResources().getColor(R.color.red_table));
                textView.setTextSize(13);
            } else {

                String name = company.getInskId().split("ir.kitgroup.")[1];
                String invGuid = sharedPreferences.getString(name, "");

                List<InvoiceDetail> list = Select.from(InvoiceDetail.class).list();
                CollectionUtils.filter(list, l -> l.INV_UID != null && !l.INV_UID.equals(invGuid));
                for (int i = 0; i < list.size(); i++) {
                    InvoiceDetail.deleteInTx(list.get(i));
                }


                InvoiceDetail.saveInTx(result.getInvoiceDetail());

                status = result.getInvoice().get(0).INV_STEP;



                if (result.getInvoice().get(0).INV_DESCRIBTION != null && !result.getInvoice().get(0).INV_DESCRIBTION.equals("")) {
                    binding.edtDescription.setHint("توضیحات : " + result.getInvoice().get(0).INV_DESCRIBTION);
                    binding.edtDescription.setVisibility(View.VISIBLE);

                } else {
                    binding.edtDescription.setVisibility(View.GONE);
                }






                if (status != null && status == 1) {
                    binding.layoutEditDelete.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutEditDelete.setVisibility(View.GONE);
                }


                if (result.getInvoice().get(0).INV_DUE_DATE_PERSIAN != null)
                    binding.txtDate.setText(result.getInvoice().get(0).INV_DUE_DATE_PERSIAN);


                CollectionUtils.filter(result.getInvoiceDetail(), i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
                invDetails.clear();
                invDetails.addAll(result.getInvoiceDetail());
                invoiceDetailList.clear();
                invoiceDetailList.addAll(invDetails);

            }


            if (invDetails.size() == 0) {
                binding.progressBar.setVisibility(View.GONE);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("این سفارش در سرور وجود ندارد.")
                        .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                        .show();

                TextView textView = alertDialog.findViewById(android.R.id.message);
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                textView.setTypeface(face);
                textView.setTextColor(getResources().getColor(R.color.red_table));
                textView.setTextSize(13);
            }
            else {
                ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);
                for (int i = 0; i < invDetails.size(); i++) {
                    myViewModel.getProduct(company.getUser(), company.getPass(), invDetails.get(i).PRD_UID);
                }
            }
            binding.progressBar.setVisibility(View.GONE);
        });
        myViewModel.getResultProduct().observe(getViewLifecycleOwner(), result -> {

            binding.progressBar.setVisibility(View.GONE);
            if (result == null)
                return;
            myViewModel.getResultListProduct().setValue(null);

            counter = counter + 1;


            ArrayList<Product> list1 = new ArrayList<>(result);
            if (list1.size() > 0) {
                ir.kitgroup.saleindemo.DataBase.Product.saveInTx(list1.get(0));

            }

            if (counter == invDetails.size()) {
                invoiceDetailList.clear();
                if (invDetails.size() > 0) {
                    ArrayList<InvoiceDetail> invDtls = new ArrayList<>(invDetails);
                    CollectionUtils.filter(invDtls, i -> i.PRD_UID.equalsIgnoreCase(Transport_GUID));
                    if (invDtls.size() > 0) {
                        if (type.equals("1")) {
                            sumTransport = Double.parseDouble(invDtls.get(0).INV_DET_TOTAL_AMOUNT);
                            binding.tvSumTransport.setText(format.format(sumTransport) + " ریال ");
                            binding.layoutTransport.setVisibility(View.VISIBLE);
                        }
                    }

                    CollectionUtils.filter(invDetails, i -> !i.PRD_UID.equalsIgnoreCase(Transport_GUID));
                }

                invoiceDetailList.clear();
                invoiceDetailList.addAll(invDetails);
                invDetails.clear();
                invoiceDetailAdapter.notifyDataSetChanged();

                binding.progressBar.setVisibility(View.GONE);
                counter = 0;
            }

        });
        myViewModel.getResultDescription().observe(getViewLifecycleOwner(), result -> {

            customProgress.hideProgress();
            if (result == null)
                return;
            myViewModel.getResultDescription().setValue(null);


            descriptionList.clear();
            descriptionList.addAll(result);


            for (int i = 0; i < descriptionList.size(); i++) {
                if (edtDescriptionItem.getText().toString().contains("'" + descriptionList.get(i).DSC + "'")) {
                    descriptionList.get(i).Click = true;
                }

            }

            descriptionAdapter.notifyDataSetChanged();
            customProgress.hideProgress();

            dialogDescription.show();


        });
        myViewModel.getResultLog().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            myViewModel.getResultLog().setValue(null);

            int message = result.getLogs().get(0).getMessage();
            if (message == 4) {
                List<InvoiceDetail> invoiceDetail = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
                ir.kitgroup.saleindemo.DataBase.Product.deleteAll(ir.kitgroup.saleindemo.DataBase.Product.class);
                for (int i = 0; i < invoiceDetail.size(); i++) {
                    InvoiceDetail.deleteInTx(invoiceDetail.get(i));
                }

                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
            customProgress.hideProgress();

        });
        myViewModel.getResultMaxSale().observe(getViewLifecycleOwner(), res -> {

            if (res == null)
                return;

            myViewModel.getResultMaxSale().setValue(null);

            int remain = res;
            ArrayList<InvoiceDetail> result = new ArrayList<>(invoiceDetailList);
            CollectionUtils.filter(result, r -> r.PRD_UID.equals(Prd_UID));


            if (result.size() > 0) {
                InvoiceDetail invoiceDetail = Select.from(InvoiceDetail.class).where("INVDETUID ='" + result.get(0).INV_DET_UID + "'").first();
                double amount = 0.0;
                if (!sWord.equals("")) {
                    amount = Double.parseDouble(sWord);
                    if (remain - amount < 0) {


                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setMessage("مقدار انتخاب شده بیشتر از موجودی کالا می باشد ، موجودی : " + remain)
                                .setPositiveButton("بستن", (dialog, which) -> dialog.dismiss())
                                .show();

                        TextView textView =  alertDialog.findViewById(android.R.id.message);
                        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
                        textView.setTypeface(face);
                        textView.setTextColor(getResources().getColor(R.color.medium_color));
                        textView.setTextSize(13);

                        if (invoiceDetail != null) {
                            invoiceDetail.INV_DET_QUANTITY = 0.0;
                            invoiceDetail.update();

                        }

                        invoiceDetailAdapter.notifyDataSetChanged();

                        return;
                    }
                }

                if (invoiceDetail != null) {
                    invoiceDetail.INV_DET_QUANTITY = amount;
                    ArrayList<InvoiceDetail> invDtls = new ArrayList<>(invoiceDetailList);
                    CollectionUtils.filter(invDtls, i -> i.INV_DET_UID.equals(invoiceDetail.INV_DET_UID));
                    if (invDtls.size() > 0)
                        invoiceDetailList.get(invoiceDetailList.indexOf(invDtls.get(0))).INV_DET_QUANTITY = amount;

                    invoiceDetail.update();


                }


                invoiceDetailAdapter.notifyDataSetChanged();


            }

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            customProgress.hideProgress();
            if (result == null)
                return;
           // myViewModel.getResultMessage().setValue(null);
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

    }







    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (type.equals("1")) {
            List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" +
                    Inv_GUID + "'").list();
            for (int i = 0; i < invoiceDetails.size(); i++) {
                InvoiceDetail.delete(invoiceDetails.get(i));
            }
        }
        binding = null;
    }


}

