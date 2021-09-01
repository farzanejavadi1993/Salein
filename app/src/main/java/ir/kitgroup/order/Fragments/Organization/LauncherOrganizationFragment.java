package ir.kitgroup.order.Fragments.Organization;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import ir.kitgroup.order.Activities.Classes.LauncherActivity;
import ir.kitgroup.order.Adapters.TableAdapter;
import ir.kitgroup.order.Adapters.TypeOrderAdapter;
import ir.kitgroup.order.Fragments.MobileView.InVoiceDetailMobileFragment;
import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.DataBase.Account;
import ir.kitgroup.order.DataBase.Invoice;
import ir.kitgroup.order.DataBase.InvoiceDetail;
import ir.kitgroup.order.DataBase.OrderType;
import ir.kitgroup.order.DataBase.Product;
import ir.kitgroup.order.DataBase.ProductGroupLevel1;
import ir.kitgroup.order.DataBase.ProductGroupLevel2;
import ir.kitgroup.order.DataBase.Setting;
import ir.kitgroup.order.DataBase.Tables;
import ir.kitgroup.order.DataBase.User;
import ir.kitgroup.order.Fragments.MobileView.MainOrderMobileFragment;
import ir.kitgroup.order.Fragments.TabletView.OrderFragment;


import ir.kitgroup.order.models.ModelProduct;
import ir.kitgroup.order.models.ModelSetting;
import ir.kitgroup.order.models.ModelTable;
import ir.kitgroup.order.models.ModelTypeOrder;
import ir.kitgroup.order.R;
import ir.kitgroup.order.Util.Util;
import ir.kitgroup.order.databinding.FragmentLauncherOrganizationBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LauncherOrganizationFragment extends Fragment {


    //region Parameter







    private FragmentLauncherOrganizationBinding binding;


    private SharedPreferences.Editor editor;
    private Boolean firstSync = false;

    private String userName;
    private String passWord;


    private TableAdapter tableAdapter;
    private final ArrayList<Tables> tablesList = new ArrayList<>();

    private TypeOrderAdapter getOutOrderAdapter;
    private final ArrayList<OrderType> getOutOrderList = new ArrayList<>();


    private String error = "";
    private String TypeClickButton = "";
    //region Dialog
    private Dialog dialog;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog


    //endregion Parameter


    @SuppressLint("SourceLockedOrientationActivity")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        if (LauncherActivity.screenInches >= 7) {

            Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {

            Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        binding = FragmentLauncherOrganizationBinding.inflate(getLayoutInflater());



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        firstSync = sharedPreferences.getBoolean("firstSync", false);



        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;


        //region Cast Variable Dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textMessageDialog = dialog.findViewById(R.id.tv_message);
        btnOkDialog = dialog.findViewById(R.id.btn_ok);
        btnNoDialog = dialog.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> dialog.dismiss());


        btnOkDialog.setOnClickListener(v -> {
            dialog.dismiss();
            if (TypeClickButton.equals("logOut")) {
                if (Account.count(Account.class) > 0)
                    Account.deleteAll(Account.class);

                if (Invoice.count(Invoice.class) > 0)
                    Invoice.deleteAll(Invoice.class);

                if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                    InvoiceDetail.deleteAll(InvoiceDetail.class);

                if (OrderType.count(OrderType.class) > 0)
                    OrderType.deleteAll(OrderType.class);

                if (Product.count(Product.class) > 0)
                    Product.deleteAll(Product.class);


                if (Setting.count(Setting.class) > 0)
                    Setting.deleteAll(Setting.class);

                if (ProductGroupLevel1.count(ProductGroupLevel1.class) > 0)
                    ProductGroupLevel1.deleteAll(ProductGroupLevel1.class);

                if (ProductGroupLevel2.count(ProductGroupLevel2.class) > 0)
                    ProductGroupLevel2.deleteAll(ProductGroupLevel2.class);


                if (Tables.count(Tables.class) > 0)
                    Tables.deleteAll(Tables.class);


                if (User.count(User.class) > 0)
                    User.deleteAll(User.class);


                editor.putBoolean("firstSync", false).apply();


                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();

                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main,  new LoginOrganizationFragment()).addToBackStack("UserF").commit();
                dialog.dismiss();
            }
            else if (TypeClickButton.equals("error")){
                getProduct();
            }


        });

        //endregion Cast Variable Dialog


        for (Invoice invoice : Select.from(Invoice.class).where("TBLUID is null ").list()) {
            Invoice.deleteInTx(invoice);
        }


        binding.busyTable.setOnClickListener(v -> {

            filter("busy");
            tablesList.clear();
            List<Tables> tables = Select.from(Tables.class).list();
            CollectionUtils.filter(tables, t -> !t.N.equals("بیرون بر") && t.C == null && t.ACT);
            tablesList.addAll(tables);
            tableAdapter.notifyDataSetChanged();
        });


        binding.reserveTable.setOnClickListener(v -> {
            filter("reserve");
            tablesList.clear();
            List<Tables> tables = Select.from(Tables.class).list();
            CollectionUtils.filter(tables, t -> !t.N.equals("بیرون بر") && t.C == null && t.RSV);
            tablesList.addAll(tables);
            tableAdapter.notifyDataSetChanged();
        });

        binding.vacantTable.setOnClickListener(v -> {
            filter("vacant");
            tablesList.clear();
            List<Tables> tables = Select.from(Tables.class).list();
            CollectionUtils.filter(tables, t -> !t.N.equals("بیرون بر") && t.C == null && !t.ACT && !t.RSV);
            LauncherOrganizationFragment.this.tablesList.addAll(tables);
            tableAdapter.notifyDataSetChanged();
        });

        binding.wholeTable.setOnClickListener(v -> {
            filter("whole");
            tablesList.clear();
            List<Tables> tables = Select.from(Tables.class).list();
            CollectionUtils.filter(tables, t -> !t.N.equals("بیرون بر") && t.C == null);
            LauncherOrganizationFragment.this.tablesList.addAll(tables);
            tableAdapter.notifyDataSetChanged();
        });


        binding.getOutOrder.setOnClickListener(v -> {
            filter("getOrder");
            tablesList.clear();
            List<Tables> tables = Select.from(Tables.class).list();
            CollectionUtils.filter(tables, t -> t.N.equals("بیرون بر") && t.C != null);
            LauncherOrganizationFragment.this.tablesList.addAll(tables);
            tableAdapter.notifyDataSetChanged();
        });


        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.BASELINE);



        tableAdapter = new TableAdapter(getContext(), tablesList);
        binding.recyclerTable.setLayoutManager(flexboxLayoutManager);
        binding.recyclerTable.setLayoutManager(flexboxLayoutManager);
        binding.recyclerTable.setAdapter(tableAdapter);


        tableAdapter.setOnClickItemListener((Name, Reserve, T_GUID) -> {

            if (Reserve) {
                Invoice invoice =Select.from(Invoice.class).where("TBLUID ='" + T_GUID + "'").first();

                if (invoice != null) {

                    Bundle bundle = new Bundle();
                    bundle.putString("type", "1");//go to InVoiceDetailMobileFragment for register order first time
                    bundle.putString("Inv_GUID", invoice.INV_UID);
                    bundle.putString("Ord_TYPE", "");
                    bundle.putString("Acc_Name", "");
                    bundle.putString("Acc_GUID", "");


                    InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
                    inVoiceDetailFragmentMobile.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobile").commit();

                } else {
                    Toast.makeText(getActivity(), "فاکتور این سفارش در تبلت موجود نمی باشد.", Toast.LENGTH_SHORT).show();
                }

            } else {
                List<OrderType> orderType = Select.from(OrderType.class).list();
                CollectionUtils.filter(orderType, o -> o.getTy() == 1);

                Bundle bundle = new Bundle();


                bundle.putString("Tbl_GUID", T_GUID);
                bundle.putString("Ord_TYPE", String.valueOf(orderType.get(0).getC()));
                bundle.putString("Inv_GUID", "");


                if (LauncherActivity.screenInches >= 7) {
                    OrderFragment orderFragment = new OrderFragment();
                    orderFragment.setArguments(bundle);
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, orderFragment, "OrderFragment").addToBackStack("OrderF").commit();

                } else {

                    MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                    mainOrderMobileFragment.setArguments(bundle);
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF").commit();
                }

            }
        });


        FlexboxLayoutManager flexboxLayoutManager1 = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager1.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager1.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager1.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager1.setAlignItems(AlignItems.BASELINE);

        List<OrderType> list = Select.from(OrderType.class).list();
        CollectionUtils.filter(list, l -> l.getTy() == 2);
        getOutOrderList.clear();
        getOutOrderList.addAll(list);

        getOutOrderAdapter = new TypeOrderAdapter(getContext(), getOutOrderList);
        binding.recycleGetOutOrder.setLayoutManager(flexboxLayoutManager1);
        binding.recycleGetOutOrder.setAdapter(getOutOrderAdapter);
        binding.recycleGetOutOrder.setHasFixedSize(false);
        getOutOrderAdapter.setOnClickItemListener((code, ty) -> {

            if (code == 0 && ty == 0) {
                List<Tables> tables = Select.from(Tables.class).list();
                CollectionUtils.filter(tables, t -> t.C == null && !t.N.equals("بیرون بر"));
                tablesList.clear();
                tablesList.addAll(tables);
                tableAdapter.notifyDataSetChanged();
                return;
            } else if (ty == 100) {
                tablesList.clear();
                List<Tables> tbls = Select.from(Tables.class).list();
                CollectionUtils.filter(tbls, t -> t.C != null && t.C.equals(code));
                tablesList.addAll(tbls);
                tableAdapter.notifyDataSetChanged();
                return;
            }


            Bundle bundle = new Bundle();
            bundle.putString("Tbl_GUID", "");
            bundle.putString("Ord_TYPE", String.valueOf(code));
            bundle.putString("Inv_GUID", "");

            if (LauncherActivity.screenInches >= 7) {
                OrderFragment orderFragment = new OrderFragment();
                orderFragment.setArguments(bundle);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, orderFragment, "OrderFragment").addToBackStack("OrderF").commit();
            } else {
                MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                mainOrderMobileFragment.setArguments(bundle);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF").commit();
            }


        });


        binding.btnLogout.setOnClickListener(v -> {
            TypeClickButton = "logOut";
            btnNoDialog.setText("خیر");
            btnOkDialog.setText("بله");
            textMessageDialog.setText("آیا مایل به خروج از برنامه هستید؟");
            dialog.show();

        });


        isAllPermissionGranted();

        return binding.getRoot();


    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroy() {
        Util.AllProduct.clear();


        super.onDestroy();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 88) {

            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED
            )
            ) {

                getProduct();
            } else {

                Toast.makeText(getActivity(), "لطفا دسترسی ها را کامل بدهید", Toast.LENGTH_LONG).show();
                Objects.requireNonNull(getActivity()).finish();
            }
        } else {

            Objects.requireNonNull(getActivity()).finish();
        }


    }


    static public void SaveImageToStorage(Bitmap bitmapImage, String ID, Context context) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        File destination = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SaleIn");
        File file = new File(destination, ID.toUpperCase() + ".jpg");

        FileOutputStream fo = null;
        try {
            if (!destination.exists()) {
                destination.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
                fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null)
                    fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static public Bitmap StringToImage(String image) {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static void deleteDirectory(File file) {
        if (file.isDirectory()) {


            if (Objects.requireNonNull(file.list()).length == 0) {

                   file.delete();


            } else {
                String[] files = file.list();

                assert files != null;
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    deleteDirectory(fileDelete);
                }

                if (Objects.requireNonNull(file.list()).length == 0) {
                    file.delete();
                }
            }


        } else {
            file.delete();
        }
    }

    public static String toEnglishNumber(String input) {

        String[] persian = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
        String[] arabic = new String[]{"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩"};

        for (int j = 0; j < persian.length; j++) {
            if (input.contains(persian[j]))
                input = input.replace(persian[j], String.valueOf(j));
        }

        for (int j = 0; j < arabic.length; j++) {
            if (input.contains(arabic[j]))
                input = input.replace(arabic[j], String.valueOf(j));
        }

        return input;
    }

    private void isAllPermissionGranted() {

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
        ) {
            getProduct();


        } else {

            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    }, 88);
        }
    }



    private void getProduct() {

        error = "";

        binding.progressbar.setVisibility(View.VISIBLE);
        String yourFilePath = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + "SaleIn";
        File file = new File(yourFilePath);
        deleteDirectory(file);
        try {

            Call<String> call = App.api.getProduct("saleinkit_api", userName, passWord);


            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelProduct>() {
                    }.getType();


                    ModelProduct iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception ignore) {

                        error = error + "\n" + "مدل دریافت شده از کالاها نا معتبر است";
                        showError(error);
                        return;
                    }


                    if (iDs == null) {
                        error = error + "\n" + "لیست دریافت شده از کالاها نا معتبر می باشد";
                        showError(error);
                    }
                    else {

                        List<ir.kitgroup.order.DataBase.Product> products = iDs.getProductList();

                       Util.AllProduct.clear();
                        for (int i = 0; i < products.size(); i++) {
                            Product product = new Product();
                            product.I = products.get(i).I;
                            product.STS = products.get(i).STS;
                            product.PID2 = products.get(i).PID2;
                            product.PID1 = products.get(i).PID1;
                            product.N = products.get(i).N;
                            product.DES = products.get(i).DES;
                            product.NIP = products.get(i).NIP;
                            product.PU1 = products.get(i).PU1;
                            product.PU2 = products.get(i).PU2;
                            product.PU3 = products.get(i).PU3;
                            product.PU4 = products.get(i).PU4;
                            product.PU5 = products.get(i).PU5;
                            product.PERC_DIS = products.get(i).PERC_DIS;
                            product.PID1 = products.get(i).PID1;
                            Util.AllProduct.add(product);
                            if (!firstSync)
                                product.save();
                            else
                                product.update();

                            if (!products.get(i).IMG.equals("0"))
                                SaveImageToStorage(StringToImage(products.get(i).IMG), products.get(i).I, Objects.requireNonNull(getActivity()));

                        }


                        List<ProductGroupLevel1> productGroupLevel1s = iDs.getProductLevel1List();

                        if (!firstSync)
                            ProductGroupLevel1.saveInTx(productGroupLevel1s);
                        else
                            for (ProductGroupLevel1 PrdGroupLevel1 : productGroupLevel1s) {
                                PrdGroupLevel1.update();
                            }


                        List<ProductGroupLevel2> productGroupLevel2s = iDs.getProductLevel2List();

                        if (!firstSync)
                            ProductGroupLevel2.saveInTx(productGroupLevel2s);
                        else
                            for (ProductGroupLevel2 prdGroupLevel2 : productGroupLevel2s) {
                                prdGroupLevel2.update();
                            }


                        editor.putBoolean("firstSync", true);
                        editor.apply();

                        getSetting();


                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    error = error + "\n" + "خطای تایم اوت در دریافت کالاها";
                    showError(error);

                }
            });


        } catch (NetworkOnMainThreadException ex) {

            error = error + "\n" + "خطا در اتصال به سرور برای دریافت کالاها";
            showError(error);
        }


    }

    private void getSetting() {


        try {

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
                        showError(error);
                        return;
                    }

                    if (iDs == null) {
                        error = error + "\n" + "لیست دریافت شده از تنظیمات نا معتبر می باشد";
                        showError(error);
                    } else {

                        Setting.deleteAll(Setting.class);
                        List<Setting> settingsList = new ArrayList<>(iDs.getSettings());
                        Setting.saveInTx(settingsList);
                        editor.putString("priceProduct", iDs.getSettings().get(0).DEFAULT_PRICE_INVOICE);
                        editor.apply();

                        getTypeOrder();


                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    error = error + "\n" + "خطای تایم اوت در دریافت تنظیمات";
                    showError(error);


                }
            });


        } catch (NetworkOnMainThreadException ex) {


            error = error + "\n" + "خطا در اتصال به سرور برای دریافت تنطیمات";
            showError(error);
        }


    }

    private void getTypeOrder() {


        try {

            Call<String> call = App.api.getOrderType(userName, passWord);
            call.enqueue(new Callback<String>() {
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
                        showError(error);
                        return;
                    }

                    if (iDs == null) {

                        error = error + "\n" + "لیست دریافت شده از نوع سفارش نا معتبر می باشد";
                        showError(error);
                    } else {

                        List<OrderType> orderTypeList1 = iDs.getOrderTypes();
                        OrderType.deleteAll(OrderType.class);
                        OrderType.saveInTx(orderTypeList1);
                        getOutOrderList.clear();
                        CollectionUtils.filter(iDs.getOrderTypes(), i -> i.getTy() == 2);
                        getOutOrderList.addAll(iDs.getOrderTypes());
                        getOutOrderAdapter.notifyDataSetChanged();

                        getTable();


                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    error = error + "\n" + "خطای تایم اوت در دریافت نوع سفارش";
                    showError(error);
                }
            });


        } catch (NetworkOnMainThreadException ex) {

            error = error + "\n" + "خطا در اتصال به سرور برای دریافت نوع سفارش";
            showError(error);
        }


    }


    private void getTable() {


        try {

            Call<String> call = App.api.getTable(userName, passWord);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelTable>() {
                    }.getType();


                    ModelTable iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {

                        error = error + "\n" + "مدل دریافت شده از میزها نا معتبر است";
                        showError(error);
                        return;
                    }
                    if (iDs != null) {

                        List<Tables> tbl = Select.from(Tables.class).list();
                        CollectionUtils.filter(tbl, t -> !t.N.equals("بیرون بر") && t.C == null);
                        for (int i = 0; i < tbl.size(); i++) {
                            Tables t = Select.from(Tables.class).where("I ='" + tbl.get(i).I + "'").first();
                            if (t != null)
                                t.delete();
                        }

                        CollectionUtils.filter(iDs.getTables(), t -> t.ST);
                        Tables.saveInTx(iDs.getTables());
                        CollectionUtils.filter(iDs.getTables(), t -> !t.N.equals("بیرون بر") && t.C == null);
                        tablesList.addAll(iDs.getTables());
                        tableAdapter.notifyDataSetChanged();
                        binding.progressbar.setVisibility(View.GONE);




                    } else {

                        error = error + "\n" + "لیست دریافت شده از میزها نا معتبر می باشد";
                        showError(error);
                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {


                    error = error + "\n" + "خطای تایم اوت در دریافت میزها";
                    showError(error);

                }
            });


        } catch (NetworkOnMainThreadException ex) {

            error = error + "\n" + "خطا در اتصال به سرور برای دریافت میزها";
            showError(error);
        }


    }






    private void filter(String viewName) {

        binding.reserveTable.setImageBitmap(null);
        binding.getOutOrder.setImageBitmap(null);
        binding.wholeTable.setImageBitmap(null);
        binding.vacantTable.setImageBitmap(null);
        binding.busyTable.setImageBitmap(null);
        switch (viewName) {
            case "busy":
                binding.busyTable.setImageResource(R.drawable.ic_tik_black);
                break;
            case "reserve":
                binding.reserveTable.setImageResource(R.drawable.ic_tik_black);
                break;
            case "vacant":
                binding.vacantTable.setImageResource(R.drawable.ic_tik_black);
                break;
            case "getout":
                binding.getOutOrder.setImageResource(R.drawable.ic_tik_black);
                break;
            case "whole":
                binding.wholeTable.setImageResource(R.drawable.ic_tik_black);
                break;
        }


    }

    private void showError(String error) {
        TypeClickButton = "error";
        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialog.dismiss();
        dialog.show();
        binding.progressbar.setVisibility(View.GONE);
    }


}
