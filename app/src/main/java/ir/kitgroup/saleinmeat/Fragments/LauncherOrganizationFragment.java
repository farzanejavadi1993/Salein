package ir.kitgroup.saleinmeat.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

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


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinmeat.Adapters.TableAdapter;
import ir.kitgroup.saleinmeat.Adapters.TypeOrderAdapter;
import ir.kitgroup.saleinmeat.DataBase.Product;
import ir.kitgroup.saleinmeat.Fragments.MobileView.InVoiceDetailMobileFragment;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.models.OrderType;

import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.DataBase.User;
import ir.kitgroup.saleinmeat.Fragments.MobileView.MainOrderMobileFragment;


import ir.kitgroup.saleinmeat.models.ModelTable;
import ir.kitgroup.saleinmeat.models.ModelTypeOrder;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.databinding.FragmentLauncherOrganizationBinding;


public class LauncherOrganizationFragment extends Fragment {


    //region Parameter
    private FragmentLauncherOrganizationBinding binding;

    private String userName = "";
    private String passWord = "";

    private TableAdapter tableAdapter;
    private ArrayList<Tables> tablesList;
    private ArrayList<Tables> AllTable;

    private TypeOrderAdapter getOutOrderAdapter;
    private final ArrayList<OrderType> orderTypes = new ArrayList<>();
    private final ArrayList<OrderType> getOutOrderList = new ArrayList<>();


    private String error = "";
    private String TypeClickButton = "";

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


        tablesList = new ArrayList<>();
        AllTable = new ArrayList<>();


        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;


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



        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getTable1();
                getTypeOrder();
            }
        });


        btnOkDialog.setOnClickListener(v -> {
            dialog.dismiss();
            if (TypeClickButton.equals("logOut")) {

                if (Account.count(Account.class) > 0)
                    Account.deleteAll(Account.class);



                if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                    InvoiceDetail.deleteAll(InvoiceDetail.class);


                if (Product.count(Product.class) > 0)
                    Product.deleteAll(Product.class);


                if (Tables.count(Tables.class) > 0)
                    Tables.deleteAll(Tables.class);


                if (User.count(User.class) > 0)
                    User.deleteAll(User.class);



                getFragmentManager().popBackStack();

                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginOrganizationFragment()).addToBackStack("UserF").commit();
                dialog.dismiss();

            }


            else if (TypeClickButton.equals("error")) {
             getTypeOrder();
             getTable1();
            }
            else if (TypeClickButton.equals("deleteTable")) {

                Tables tb=  Select.from(Tables.class).where("I ='" +TableGUID+ "'").first();
                    if (tb!=null)
                        Tables.delete(tb);
                    try {
                        tablesList.remove(position1);
                    }catch (Exception ignore){}

                    tableAdapter.notifyDataSetChanged();

            }

            else if (TypeClickButton.equals("deleteInvoice")) {

                    List<InvoiceDetail> invoiceDetails=   Select.from(InvoiceDetail.class).where("INVUID ='"+TableGUID+"'").list();

                    for (int i=0;i<invoiceDetails.size();i++){
                        InvoiceDetail.delete(invoiceDetails.get(i));
                    }
                tableAdapter.notifyDataSetChanged();
            }


        });

        //endregion Cast Variable Dialog





        binding.busyTable.setOnClickListener(v -> {
            binding.txtError.setText("");
            filter("busy");
            tablesList.clear();
            ArrayList<Tables> arrayList=new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> !t.N.equals("بیرون بر") && t.C == null && t.ACT);
            tablesList.addAll(arrayList);
            if (tablesList.size()==0)
                binding.txtError.setText("هیچ میز مشغولی موجود نمی باشد. ");
            tableAdapter.notifyDataSetChanged();
        });


        binding.reserveTable.setOnClickListener(v -> {
            binding.txtError.setText("");
            filter("reserve");
            tablesList.clear();
            ArrayList<Tables> arrayList=new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> !t.N.equals("بیرون بر") && t.C == null && t.RSV);
            tablesList.addAll(arrayList);
            if (tablesList.size()==0)
                binding.txtError.setText("هیچ میز رزرو شده ای موجود نمی باشد. ");
            tableAdapter.notifyDataSetChanged();
        });

        binding.vacantTable.setOnClickListener(v -> {
            binding.txtError.setText("");
            filter("vacant");
            tablesList.clear();
            ArrayList<Tables> arrayList=new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> !t.N.equals("بیرون بر") && t.C == null && !t.ACT && !t.RSV);
            LauncherOrganizationFragment.this.tablesList.addAll(arrayList);
            if (tablesList.size()==0)
                binding.txtError.setText("هیچ سفارشی موجود نمی باشد.");
            tableAdapter.notifyDataSetChanged();
        });

        binding.wholeTable.setOnClickListener(v -> {
            binding.txtError.setText("");
            filter("whole");
            tablesList.clear();
            ArrayList<Tables> arrayList=new ArrayList<>(AllTable);
            CollectionUtils.filter(arrayList, t -> !t.N.equals("بیرون بر") && t.C == null);
            LauncherOrganizationFragment.this.tablesList.addAll(arrayList);
            if (tablesList.size()==0)
                binding.txtError.setText("هیچ میزی موجود نمی باشد.");
            tableAdapter.notifyDataSetChanged();
        });


        binding.getOutOrder.setOnClickListener(v -> {
            binding.txtError.setText("");
            tablesList.clear();
            filter("getOut");
            tablesList.clear();
            List<Tables> arrayList=Select.from(Tables.class).list();
            CollectionUtils.filter(arrayList, t -> t.N!=null && t.N.equals("بیرون بر") && t.GO != null);
            LauncherOrganizationFragment.this.tablesList.addAll(arrayList);
            if (tablesList.size()==0)
                binding.txtError.setText("هیچ سفارشی موجود نمی باشد.");
            tableAdapter.notifyDataSetChanged();
        });



        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.BASELINE);

        tableAdapter = new TableAdapter(getActivity(), tablesList);
        binding.recyclerTable.setLayoutManager(flexboxLayoutManager);
        binding.recyclerTable.setAdapter(tableAdapter);


        tableAdapter.OnclickShowDialog((Inv_GUID, position, type) -> {

            textMessageDialog.setText("آیا مایل به حذف سفارش می باشید؟");


            position1=position;
            TableGUID=Inv_GUID;
            if (type)
                TypeClickButton="deleteTable";
            else
                TypeClickButton="deleteInvoice";
            dialog.show();
        });

        tableAdapter.setOnClickItemListener((Name, Reserve, T_GUID,I_GUID) -> {
            binding.txtError.setText("");
            if (Reserve) {

                Bundle bundle = new Bundle();
                    bundle.putString("type", "1");//go to InVoiceDetailMobileFragment for register order first time
                    bundle.putString("Inv_GUID", I_GUID);
                    bundle.putString("Ord_TYPE","");
                    bundle.putString("Tbl_GUID",T_GUID);
                    bundle.putBoolean("EDIT",true);


                    InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
                    inVoiceDetailFragmentMobile.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobile").commit();


            }


            else {

                List<OrderType> orderType =new ArrayList<>(orderTypes);
                CollectionUtils.filter(orderType, o -> o.getTy() == 1);
                int OrderType=0;
                if (orderType.size()>0) {
                     OrderType = orderType.get(0).getC();
                }

                Bundle bundle = new Bundle();
                bundle.putString("Tbl_GUID", T_GUID);
                bundle.putString("Ord_TYPE", String.valueOf(OrderType));
                bundle.putString("Inv_GUID", T_GUID);
                bundle.putString("Acc_NAME", "");
                bundle.putString("Acc_GUID", "");
                bundle.putBoolean("EDIT",false);


                MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                mainOrderMobileFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF").commit();


            }
        });




        FlexboxLayoutManager flexboxLayoutManager1 = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager1.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager1.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager1.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager1.setAlignItems(AlignItems.BASELINE);
        getOutOrderAdapter = new TypeOrderAdapter(getContext(), getOutOrderList);
        binding.recycleGetOutOrder.setLayoutManager(flexboxLayoutManager1);
        binding.recycleGetOutOrder.setAdapter(getOutOrderAdapter);
        binding.recycleGetOutOrder.setHasFixedSize(false);




        binding.btnLogout.setOnClickListener(v -> {
            TypeClickButton = "logOut";
            btnNoDialog.setText("خیر");
            btnOkDialog.setText("بله");
            textMessageDialog.setText("آیا مایل به خروج از برنامه هستید؟");
            dialog.show();

        });


       getTypeOrder();
        getTable1();


        getOutOrderAdapter.setOnClickItemListener((code, ty) -> {

            if (code == 0 && ty == 0) {

                filter("whole");
                tablesList.clear();
                tablesList.addAll(AllTable);
                if (tablesList.size()==0)
                    binding.txtError.setText("هیچ میزی موجود نمی باشد.");
                tableAdapter.notifyDataSetChanged();
                return;
            } else if (ty == 100) {
                tablesList.clear();
                List<Tables> tbls = Select.from(Tables.class).list();
                CollectionUtils.filter(tbls, t -> t.C != null && t.C.equals(code));
                tablesList.addAll(tbls);
                if (tablesList.size()==0)
                    binding.txtError.setText("هیچ میزی موجود نمی باشد.");
                tableAdapter.notifyDataSetChanged();
                return;
            }


            Bundle bundle = new Bundle();
            bundle.putString("Tbl_GUID", "");
            bundle.putString("Ord_TYPE", String.valueOf(code));
            bundle.putString("Inv_GUID", "");
            bundle.putString("Acc_GUID", "");
            bundle.putString("Acc_NAME", "");


            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
            mainOrderMobileFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF").commit();


        });
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




    private void getTypeOrder() {

        try {
            compositeDisposable.add(
                    App.api.getOrderType1( userName, passWord)
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
                                    showError(error);

                                    return;
                                }

                                if (iDs == null) {
                                    error = error + "\n" + "لیست دریافت شده از نوع سفارش نا معتبر می باشد";
                                    showError(error);
                                } else {
                                    getOutOrderList.clear();
                                    orderTypes.clear();
                                    orderTypes.addAll(iDs.getOrderTypes());
                                    CollectionUtils.filter(iDs.getOrderTypes(), i -> i.getTy() == 2);
                                    getOutOrderList.addAll(iDs.getOrderTypes());
                                    getOutOrderAdapter.notifyDataSetChanged();

                                }




                            }, throwable -> {
                                error = error + "\n" + "خطا در ارتباط با سرور";
                                showError(error);


                            })
            );
        } catch (Exception e) {
            error = error + "\n" + "خطا در اتصال به سرور";
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
            case "getOut":
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


    private void getTable1() {
        binding.progressbar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    App.api.getTable1( userName, passWord)
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

                                    error = error + "\n" + "مدل دریافت شده از میزها نا معتبر است";
                                    showError(error);
                                    binding.progressbar.setVisibility(View.GONE);
                                    binding.refreshLayout.setRefreshing(false);
                                    return;
                                }
                                if (iDs != null) {
                                    tablesList.clear();
                                    tablesList.addAll(iDs.getTables());
                                    AllTable.clear();
                                    AllTable.addAll(iDs.getTables());
                                    tableAdapter.notifyDataSetChanged();
                                    if (tablesList.size()==0)
                                        binding.txtError.setText("هیچ میزی موجود نمی باشد.");
                                    binding.progressbar.setVisibility(View.GONE);
                                    binding.refreshLayout.setRefreshing(false);


                                } else {

                                    error = error + "\n" + "لیست دریافت شده از میزها نا معتبر می باشد";
                                    showError(error);
                                }

                                binding.progressbar.setVisibility(View.GONE);
                                binding.refreshLayout.setRefreshing(false);


                            }, throwable -> {
                                error = error + "\n" + "خطا در ارتباط با سرور";
                                showError(error);
                                binding.progressbar.setVisibility(View.GONE);
                                binding.refreshLayout.setRefreshing(false);

                            })
            );
        } catch (Exception e) {
            error = error + "\n" + "خطا در اتصال به سرور";
            showError(error);
            binding.progressbar.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);
        }

    }



    public void refreshAdapter(){
        filter("whole");
      getTable1();
        getTypeOrder();
    }
}
