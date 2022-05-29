package ir.kitgroup.saleindemo.ui.launcher.moreItem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.DataBase.Salein;
import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;
import ir.kitgroup.saleindemo.DataBase.Product;
import ir.kitgroup.saleindemo.DataBase.Tables;
import ir.kitgroup.saleindemo.DataBase.Unit;
import ir.kitgroup.saleindemo.databinding.MoreFragmentBinding;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.DataBase.Company;


@AndroidEntryPoint
public class MoreFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;


    private MoreFragmentBinding binding;
    private Company company;
    private String userName;
    private String passWord;

    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync
    private boolean seenActivity = true;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    private String mobile;
    private String linkPayment = "";

    private Users user;

    private Boolean disableAccount = false;

    private MyViewModel myViewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = MoreFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        company = Select.from(Company.class).first();

        userName=company.getUser();
        passWord=company.getPass();
        linkPayment = sharedPreferences.getString("payment_link", "");

        user = Select.from(Users.class).first();
        mobile = Select.from(Users.class).first().M;
        if (!linkPayment.equals(""))
            linkPayment = linkPayment + "/ChargeClub?c=" + user.getC();


        if (company != null && company.getAbus() != null && !company.getAbus().equals("")) {
            binding.view.setVisibility(View.VISIBLE);
            binding.btnAboutUs.setVisibility(View.VISIBLE);
        }


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
            sharedPreferences.edit().clear();

            if (Users.count(Users.class) > 0)
                Users.deleteAll(Users.class);

            if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                InvoiceDetail.deleteAll(InvoiceDetail.class);

            if (Product.count(Product.class) > 0)
                Product.deleteAll(Product.class);

            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);

            if (Unit.count(Unit.class) > 0)
                Unit.deleteAll(Unit.class);

            if (Company.count(Company.class) > 0)
                Company.deleteAll(Company.class);


            if (Salein.count(Salein.class) > 0)
                Salein.deleteAll(Salein.class);

//            final int size = getActivity().getSupportFragmentManager().getBackStackEntryCount();
//            for (int i = 0; i < size; i++) {
//                getActivity().getSupportFragmentManager().popBackStack();
//            }

            getActivity().finish();
            startActivity(getActivity().getIntent());

        });
        //endregion Cast Variable Dialog Sync



        binding.btnShare.setOnClickListener(v -> {
            NavDirections action = MoreFragmentDirections.actionGoToContactUsFragment("Share");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });

        binding.btnCredit.setOnClickListener(v -> {
            if (!linkPayment.equals("")) {
                if (seenActivity) {
                    seenActivity = false;
                    Uri uri = Uri.parse(linkPayment);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    startActivityForResult(intent, 44);
                }
            } else {
                Toast.makeText(getActivity(), "دسترسی به باشگاه امکان پذیر نمی باشد", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ivPower.setOnClickListener(v -> showError("آیا مایل به خروج از برنامه هستید؟", 1));
        binding.ivSupport.setColorFilter(getResources().getColor(R.color.color_svg), PorterDuff.Mode.SRC_IN);



        binding.lProfile.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToProfileFragment)
        );


        binding.btnSupport.setOnClickListener(v -> {
            NavDirections action = MoreFragmentDirections.actionGoToContactUsFragment("Contact");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });


        binding.btnAboutUs.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToAboutUsFragment));


        binding.btnOrderList.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToOrderFragment));


        if (user != null && user.CRDT != null) {
            binding.txtCredit.setText("موجودی : " + format.format(user.CRDT) + " ریال ");
        } else {
            binding.txtCredit.setText("موجودی : " + "0" + " ریال ");
        }


    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getInquiryAccount(userName,passWord, user.M);

        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultInquiryAccount().setValue(null);

            disableAccount = false;
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();
            //user is register
            try {
                if (result.size() > 0) {
                    Users.deleteAll(Users.class);
                    Users.saveInTx(result);

                    Users acc = Select.from(Users.class).first();
                    if (acc != null && acc.CRDT != null)
                        binding.txtCredit.setTextColor(getActivity().getResources().getColor(R.color.medium_color));

                    binding.txtCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
                } else {
                    binding.txtCredit.setTextColor(getActivity().getResources().getColor(R.color.red_table));
                    binding.txtCredit.setText("خطا در بروز رسانی موجودی ");
                    disableAccount = sharedPreferences.getBoolean("disableAccount", false);
                    if (disableAccount)
                        showError("حساب شما غیر فعال است بعداز بررسی و تایید کارشناس  فعال میگردد", 2);
                }
            } catch (Exception ignored) {
            }


        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            if (disableAccount)
                showError("حساب شما غیر فعال است بعداز بررسی و تایید کارشناس  فعال میگردد", 2);

            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();


        });

    }


    private void showError(String error, int type) {
        textMessageDialog.setText(error);

        dialogSync.dismiss();
        if (type == 2) {
            btnOkDialog.setVisibility(View.GONE);
            btnNoDialog.setText("بستن");

        } else {
            btnOkDialog.setVisibility(View.VISIBLE);
            btnNoDialog.setText("خیر");
            btnOkDialog.setText("بله");
        }
        dialogSync.setCancelable(false);
        dialogSync.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        seenActivity = true;
        myViewModel.getInquiryAccount(userName,passWord, mobile);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        seenActivity = false;
        binding = null;
    }
}