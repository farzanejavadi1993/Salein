package ir.kitgroup.saleinhamsafar.ui.organization;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

import java.util.List;


import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleinhamsafar.Connect.MyViewModel;
import ir.kitgroup.saleinhamsafar.DataBase.Product;

import ir.kitgroup.saleinhamsafar.DataBase.Tables;
import ir.kitgroup.saleinhamsafar.DataBase.Account;
import ir.kitgroup.saleinhamsafar.DataBase.InvoiceDetail;
import ir.kitgroup.saleinhamsafar.DataBase.Company;
import ir.kitgroup.saleinhamsafar.models.OrderType;
import ir.kitgroup.saleinhamsafar.R;
import ir.kitgroup.saleinhamsafar.databinding.FragmentLauncherOrganizationBinding;
import ir.kitgroup.saleinhamsafar.ui.launcher.homeItem.MainOrderFragment;
import ir.kitgroup.saleinhamsafar.ui.launcher.invoiceItem.InVoiceDetailFragment;

import static java.util.Objects.*;

@AndroidEntryPoint
public class LauncherOrganizationFragment extends Fragment {


    //region Parameter


    public static boolean refresh = false;
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


    private MyViewModel myViewModel;
    //endregion Parameter


    @SuppressLint({"SourceLockedOrientationActivity", "NotifyDataSetChanged"})
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


        btnOkDialog = dialog.findViewById(R.id.btn_ok);
        btnNoDialog = dialog.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> dialog.dismiss());


        binding.refreshLayout.setOnRefreshListener(() -> {
                    binding.progressbar.setVisibility(View.VISIBLE);
                    myViewModel.getTable(company.USER, company.PASS);
                    myViewModel.getTypeOrder(company.USER, company.PASS);
                }
        );
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
                    binding.progressbar.setVisibility(View.VISIBLE);
                    myViewModel.getTable(company.USER, company.PASS);
                    myViewModel.getTypeOrder(company.USER, company.PASS);


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

            List<Tables> tb = Select.from(Tables.class).list();
            CollectionUtils.filter(tb, t -> t.N != null);
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


        return binding.getRoot();


    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        binding.progressbar.setVisibility(View.VISIBLE);


        myViewModel.getTable(company.USER, company.PASS);
        myViewModel.getTypeOrder(company.USER, company.PASS);


        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null) {
                binding.progressbar.setVisibility(View.GONE);
                binding.refreshLayout.setRefreshing(false);
                return;
            }

            myViewModel.getResultMessage().setValue(null);
            binding.progressbar.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);

            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();

        });

        myViewModel.getResultTable().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;


            if (result != null) {
                tablesList.clear();
                myViewModel.getResultTable().setValue(null);
                List<Tables> tb = Select.from(Tables.class).list();
                CollectionUtils.filter(tb, t -> t.N != null);
                tablesList.addAll(result.getTables());
                tablesList.addAll(tb);
                AllTable.clear();
                AllTable.addAll(result.getTables());

                if (tablesList.size() == 0)
                    binding.txtError.setText("هیچ میزی موجود نمی باشد.");


                tableAdapter.notifyDataSetChanged();


            } else {
                binding.progressbar.setVisibility(View.GONE);
                binding.refreshLayout.setRefreshing(false);
                error = "لیست دریافت شده از میزها نا معتبر می باشد";
                showError(error);
            }


        });
        myViewModel.getResultTypeOrder().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;


            if (result != null) {
                myViewModel.getResultTypeOrder().setValue(null);
                getOutOrderList.clear();
                orderTypes.clear();
                orderTypes.addAll(result.getOrderTypes());
                CollectionUtils.filter(result.getOrderTypes(), i -> i.getTy() == 2);
                getOutOrderList.addAll(result.getOrderTypes());
                getOutOrderAdapter.notifyDataSetChanged();

            }
            binding.progressbar.setVisibility(View.GONE);
            binding.refreshLayout.setRefreshing(false);

        });
    }

    @Override
    public void onDestroy() {


        super.onDestroy();

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

    public void refreshAdapter() {
        filter("whole");
        refresh = true;
        binding.progressbar.setVisibility(View.VISIBLE);
        myViewModel.getTable(company.USER, company.PASS);
        myViewModel.getTypeOrder(company.USER, company.PASS);

    }
}
