package ir.kitgroup.saleinkonj.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


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

import java.lang.reflect.Type;

import java.util.ArrayList;

import java.util.List;


import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinkonj.Adapters.TableAdapter;
import ir.kitgroup.saleinkonj.Adapters.TypeOrderAdapter;
import ir.kitgroup.saleinkonj.Connect.API;
import ir.kitgroup.saleinkonj.DataBase.Product;

import ir.kitgroup.saleinkonj.DataBase.Tables;
import ir.kitgroup.saleinkonj.DataBase.Account;
import ir.kitgroup.saleinkonj.DataBase.InvoiceDetail;
import ir.kitgroup.saleinkonj.DataBase.Company;
import ir.kitgroup.saleinkonj.classes.ConfigRetrofit;

import ir.kitgroup.saleinkonj.classes.ServerConfig;
import ir.kitgroup.saleinkonj.models.OrderType;


import ir.kitgroup.saleinkonj.models.ModelTable;
import ir.kitgroup.saleinkonj.models.ModelTypeOrder;
import ir.kitgroup.saleinkonj.R;
import ir.kitgroup.saleinkonj.databinding.FragmentLauncherOrganizationBinding;

import static java.util.Objects.*;

@AndroidEntryPoint
public class LauncherOrganizationFragment extends Fragment {


    //region Parameter

    private API api;

    ServerConfig serverConfig ;

    private Company company;

    private FragmentLauncherOrganizationBinding binding;

    private TableAdapter tableAdapter;
    private ArrayList<Tables> tablesList;
    private ArrayList<Tables> AllTable;
    private TypeOrderAdapter getOutOrderAdapter;


    private final ArrayList<OrderType> orderTypes = new ArrayList<>();
    private final ArrayList<OrderType> getOutOrderList = new ArrayList<>();

    private String error = "";
    private String TypeClickButtonDialog = "";


    //region Dialog
    private Dialog dialog;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    private String TableGUID;
    private int position1;
    //endregion Dialog




    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    //endregion Parameter


    @SuppressLint("SourceLockedOrientationActivity")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding = FragmentLauncherOrganizationBinding.inflate(getLayoutInflater());


        company = Select.from(Company.class).first();





        tablesList = new ArrayList<>();
        AllTable = new ArrayList<>();


        //region Cast Variable Dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textMessageDialog = dialog.findViewById(R.id.tv_message);
        ImageView ivIconDialog = dialog.findViewById(R.id.iv_icon);
        ivIconDialog.setImageResource(R.drawable.saleinorder_png);

        btnOkDialog = dialog.findViewById(R.id.btn_ok);
        btnNoDialog = dialog.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> dialog.dismiss());



        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTable1(serverConfig.URL1);
            }
        });
        btnOkDialog.setOnClickListener(v -> {
            dialog.dismiss();
            switch (TypeClickButtonDialog) {
                case "logOut":

                    if (Account.count(Account.class) > 0)
                        Account.deleteAll(Account.class);


                    if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                        InvoiceDetail.deleteAll(InvoiceDetail.class);


                    if (Product.count(Product.class) > 0)
                        Product.deleteAll(Product.class);


                    if (Tables.count(Tables.class) > 0)
                        Tables.deleteAll(Tables.class);

                    if (Company.count(Company.class) > 0)
                        Company.deleteAll(Company.class);


                    dialog.dismiss();


                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginOrganizationFragment(), "LoginOrganizationFragment").commit();

                    break;


                case "error":
                    getTypeOrder(serverConfig.URL1);
                    getTable1(serverConfig.URL1);
                    break;


                case "deleteTable":

                    Tables tb = Select.from(Tables.class).where("I ='" + TableGUID + "'").first();
                    if (tb != null)
                        Tables.delete(tb);
                    try {
                        tablesList.remove(position1);
                    } catch (Exception ignore) {
                    }

                    tableAdapter.notifyDataSetChanged();

                    break;


                case "deleteInvoice":

                    List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + TableGUID + "'").list();

                    for (int i = 0; i < invoiceDetails.size(); i++) {
                        InvoiceDetail.delete(invoiceDetails.get(i));
                    }
                    tableAdapter.notifyDataSetChanged();


                    break;
            }


        });

        //endregion Cast Variable Dialog


        //region Action FilterButton

        binding.busyTable.setOnClickListener(v -> {
            requireNonNull(binding.txtError).setText("");
            filter("busy");
            tablesList.clear();
            ArrayList<Tables> arrayList = new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> t.C == null && t.ACT);
            tablesList.addAll(arrayList);
            if (tablesList.size() == 0)
                binding.txtError.setText("هیچ میز مشغولی موجود نمی باشد.");

            tableAdapter.notifyDataSetChanged();
        });
        binding.reserveTable.setOnClickListener(v -> {
            binding.txtError.setText("");
            filter("reserve");
            tablesList.clear();
            ArrayList<Tables> arrayList = new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> t.C == null && t.RSV);
            tablesList.addAll(arrayList);
            if (tablesList.size() == 0)
                binding.txtError.setText("هیچ میز رزرو شده ای موجود نمی باشد. ");
            tableAdapter.notifyDataSetChanged();
        });
        binding.vacantTable.setOnClickListener(v -> {
            binding.txtError.setText("");
            filter("vacant");
            tablesList.clear();
            ArrayList<Tables> arrayList = new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> t.C == null && !t.ACT && !t.RSV);
            LauncherOrganizationFragment.this.tablesList.addAll(arrayList);
            if (tablesList.size() == 0)
                binding.txtError.setText("هیچ سفارشی موجود نمی باشد.");
            tableAdapter.notifyDataSetChanged();
        });
        binding.wholeTable.setOnClickListener(v -> {
            binding.txtError.setText("");
            filter("whole");
            tablesList.clear();

           List<Tables> tb=Select.from(Tables.class).list();
           CollectionUtils.filter(tb,t->t.N!=null);
            LauncherOrganizationFragment.this.tablesList.addAll(tb);
            ArrayList<Tables> arrayList = new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> t.C == null);
            tablesList.addAll(arrayList);
            if (tablesList.size() == 0)
                binding.txtError.setText("هیچ میزی موجود نمی باشد.");
            tableAdapter.notifyDataSetChanged();
        });
        binding.getOutOrder.setOnClickListener(v -> {
            binding.txtError.setText("");
            tablesList.clear();
            filter("getOut");
            tablesList.clear();
            List<Tables> arrayList = Select.from(Tables.class).list();
            CollectionUtils.filter(arrayList, t -> t.C != null && t.GO != null);
            LauncherOrganizationFragment.this.tablesList.addAll(arrayList);
            if (tablesList.size() == 0)
                binding.txtError.setText("هیچ سفارشی موجود نمی باشد.");
            tableAdapter.notifyDataSetChanged();
        });

        //endregion Action FilterButton


        //region Configuration RecyclerView Table
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.BASELINE);

        tableAdapter = new TableAdapter(getActivity(), tablesList);
        binding.recyclerTable.setLayoutManager(flexboxLayoutManager);
        binding.recyclerTable.setAdapter(tableAdapter);


        tableAdapter.OnclickShowDialog((Tbl_GUID, position, type) -> {

            textMessageDialog.setText("آیا مایل به حذف سفارش می باشید؟");
            position1 = position;
            TableGUID = Tbl_GUID;
            if (type)
                TypeClickButtonDialog = "deleteTable";
            else
                TypeClickButtonDialog = "deleteInvoice";
            dialog.show();
        });


        tableAdapter.setOnClickItemListener((organization, Name, Reserve, T_GUID, Inv_GUID) -> {
            Account.deleteAll(Account.class);
            binding.txtError.setText("");
            if (Reserve) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "1");//go to InVoiceDetailMobileFragment for register order first time
                bundle.putString("Inv_GUID", Inv_GUID);
                bundle.putString("Ord_TYPE", "");
                bundle.putString("Tbl_GUID", T_GUID);
                if (organization)
                    bundle.putString("Tbl_NAME", "میز " + Name);
                else
                    bundle.putString("Tbl_NAME", Name);
                bundle.putBoolean("EDIT", true);


                InVoiceDetailFragment inVoiceDetailFragmentMobile = new InVoiceDetailFragment();
                inVoiceDetailFragmentMobile.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobileX").commit();

            } else {

                List<OrderType> orderType = new ArrayList<>(orderTypes);
                CollectionUtils.filter(orderType, o -> o.getTy() == 1);
                int OrderType = 0;
                if (orderType.size() > 0) {
                    OrderType = orderType.get(0).getC();
                }

                Bundle bundle = new Bundle();
                bundle.putString("Tbl_GUID", T_GUID);
                bundle.putString("Ord_TYPE", String.valueOf(OrderType));
                bundle.putString("Inv_GUID", Inv_GUID);
                bundle.putString("Acc_NAME", "");
                bundle.putString("Acc_GUID", "");
                if (organization)
                    bundle.putString("Tbl_NAME", "میز " + Name);
                else
                    bundle.putString("Tbl_NAME", Name);
                bundle.putBoolean("EDIT", false);


                MainOrderFragment mainFragment = new MainOrderFragment();
                mainFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();


            }
        });
        //endregion Configuration RecyclerView Table


        //region Configuration RecyclerView GetOutOrder
        FlexboxLayoutManager flexboxLayoutManager1 = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager1.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager1.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager1.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager1.setAlignItems(AlignItems.BASELINE);
        getOutOrderAdapter = new TypeOrderAdapter(getContext(), getOutOrderList);
        binding.recycleGetOutOrder.setLayoutManager(flexboxLayoutManager1);
        binding.recycleGetOutOrder.setAdapter(getOutOrderAdapter);
        binding.recycleGetOutOrder.setHasFixedSize(false);


        getOutOrderAdapter.setOnClickItemListener((name, code, ty) -> {

            Account.deleteAll(Account.class);
            if (code == 0 && ty == 0) {
                filter("whole");
                binding.txtError.setText("");
                tablesList.clear();
                tablesList.addAll(AllTable);
                if (tablesList.size() == 0)
                    binding.txtError.setText("هیچ میزی موجود نمی باشد.");
                tableAdapter.notifyDataSetChanged();
                return;
            } else if (ty == 100) {
                binding.txtError.setText("");
                tablesList.clear();
                List<Tables> tbls = Select.from(Tables.class).list();
                CollectionUtils.filter(tbls, t -> t.C != null && t.C.equals(code));
                tablesList.addAll(tbls);
                if (tablesList.size() == 0)
                    binding.txtError.setText("هیچ سفارشی موجود نمی باشد.");
                tableAdapter.notifyDataSetChanged();
                return;
            }


            Bundle bundle = new Bundle();
            bundle.putString("Tbl_GUID", "");
            bundle.putString("Ord_TYPE", String.valueOf(code));
            bundle.putString("Inv_GUID", "");
            bundle.putString("Acc_GUID", "");
            bundle.putString("Acc_NAME", "");
            bundle.putBoolean("EDIT", false);
            bundle.putString("Tbl_NAME", name);

            MainOrderFragment mainFragment = new MainOrderFragment();
            mainFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();


        });
        //endregion Configuration RecyclerView getOutOrder


        //region Action btnLogOut
        binding.btnLogout.setOnClickListener(v -> {
            TypeClickButtonDialog = "logOut";
            btnNoDialog.setText("خیر");
            btnOkDialog.setText("بله");
            textMessageDialog.setText("آیا مایل به خروج از برنامه هستید؟");
            dialog.show();

        });
        //endregion Action btnLogOut



        new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                int p=0;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                getTable1(serverConfig.URL1);

            }

            @Override
            protected Object doInBackground(Object[] params) {

                serverConfig =new ServerConfig(company.IP1,company.IP2);

                return 0;
            }
        }.execute(0);


        return binding.getRoot();


    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroy() {


        super.onDestroy();

    }


    private void getTypeOrder(String ip) {
        api = ConfigRetrofit.getRetrofit("http://" + ip + "/api/REST/", true,30).create(API.class);
        try {
            compositeDisposable.add(
                    api.getOrderType1(company.USER, company.PASS)
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

                                    error = "مدل دریافت شده از نوع سفارش نا معتبر است";
                                    showError(error);
                                    binding.progressbar.setVisibility(View.GONE);
                                    binding.refreshLayout.setRefreshing(false);

                                    return;
                                }

                                if (iDs == null) {
                                    error = "لیست دریافت شده از نوع سفارش نا معتبر می باشد";
                                    showError(error);

                                } else {
                                    getOutOrderList.clear();
                                    orderTypes.clear();
                                    orderTypes.addAll(iDs.getOrderTypes());
                                    CollectionUtils.filter(iDs.getOrderTypes(), i -> i.getTy() == 2);
                                    getOutOrderList.addAll(iDs.getOrderTypes());
                                    getOutOrderAdapter.notifyDataSetChanged();

                                }
                                binding.progressbar.setVisibility(View.GONE);
                                binding.refreshLayout.setRefreshing(false);


                            }, throwable -> {
                                error = "خطا در ارتباط با سرور";
                                showError(error);
                                binding.progressbar.setVisibility(View.GONE);
                                binding.refreshLayout.setRefreshing(false);


                            })
            );
        } catch (Exception e) {
            error = "خطا در اتصال به سرور";
            showError(error);
            binding.progressbar.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);

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
            case "getOut":
                binding.getOutOrder.setImageResource(R.drawable.ic_tik_black);
                break;
            case "whole":
                binding.wholeTable.setImageResource(R.drawable.ic_tik_black);
                break;
        }


    }

    private void showError(String error) {
        TypeClickButtonDialog = "error";
        textMessageDialog.setText(error);
        btnNoDialog.setText("بستن");
        btnOkDialog.setText("سینک مجدد");
        dialog.dismiss();
        dialog.show();
        binding.progressbar.setVisibility(View.GONE);
    }


    private void getTable1(String ip) {
        api = ConfigRetrofit.getRetrofit("http://" + ip + "/api/REST/", true,30).create(API.class);
        binding.progressbar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    api.getTable1(company.USER, company.PASS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelTable>() {
                                }.getType();
                                ModelTable iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    error = "مدل دریافت شده از میزها نا معتبر است";
                                    showError(error);
                                    binding.progressbar.setVisibility(View.GONE);
                                    binding.refreshLayout.setRefreshing(false);
                                    return;
                                }


                                if (iDs != null) {
                                    tablesList.clear();

                                    List<Tables> tb=Select.from(Tables.class).list();
                                    CollectionUtils.filter(tb,t->t.N!=null);
                                    tablesList.addAll(iDs.getTables());
                                    tablesList.addAll(tb);
                                    AllTable.clear();
                                    AllTable.addAll(iDs.getTables());

                                    if (tablesList.size() == 0)
                                        binding.txtError.setText("هیچ میزی موجود نمی باشد.");


                                    tableAdapter.notifyDataSetChanged();

                                    getTypeOrder(serverConfig.URL1);

                                } else {

                                    error = "لیست دریافت شده از میزها نا معتبر می باشد";
                                    showError(error);
                                }

                                binding.progressbar.setVisibility(View.GONE);
                                binding.refreshLayout.setRefreshing(false);


                            }, throwable -> {
                                error = "خطا در ارتباط با سرور";
                                showError(error);
                                binding.progressbar.setVisibility(View.GONE);
                                binding.refreshLayout.setRefreshing(false);

                            })
            );
        } catch (Exception e) {
            error = "خطا در اتصال به سرور";
            showError(error);
            binding.progressbar.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);
        }

    }


    public void refreshAdapter() {
        filter("whole");
        getTable1(serverConfig.URL1);
        getTypeOrder(serverConfig.URL1);
    }
}




























