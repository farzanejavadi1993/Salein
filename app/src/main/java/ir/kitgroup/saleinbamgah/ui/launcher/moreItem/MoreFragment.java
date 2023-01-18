package ir.kitgroup.saleinbamgah.ui.launcher.moreItem;

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
import ir.kitgroup.saleinbamgah.Connect.CompanyViewModel;


import ir.kitgroup.saleinbamgah.DataBase.Account;
import ir.kitgroup.saleinbamgah.DataBase.InvoiceDetail;
import ir.kitgroup.saleinbamgah.DataBase.Product;

import ir.kitgroup.saleinbamgah.DataBase.Unit;
import ir.kitgroup.saleinbamgah.databinding.MoreFragmentBinding;
import ir.kitgroup.saleinbamgah.R;
import ir.kitgroup.saleinbamgah.DataBase.Company;


@AndroidEntryPoint
public class MoreFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;


    private MoreFragmentBinding binding;
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

    private Account account;

    private Boolean disableAccount = false;

    private CompanyViewModel myViewModel;

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


       Company company = Select.from(Company.class).first();

        userName= company.getUser();
        passWord= company.getPass();
        linkPayment = sharedPreferences.getString("payment_link", "");

        account = Select.from(Account.class).first();
        mobile = account.getM();
        if (!linkPayment.equals(""))
            linkPayment = linkPayment + "/ChargeClub?c=" + account.getC();


        if (company != null && company.getAbus() != null && !company.getAbus().equals(""))
            binding.cardAboutUs.setVisibility(View.VISIBLE);



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
            sharedPreferences.edit().clear().apply();

            if (Account.count(Account.class) > 0)
                Account.deleteAll(Account.class);

            if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                InvoiceDetail.deleteAll(InvoiceDetail.class);

            if (Product.count(Product.class) > 0)
                Product.deleteAll(Product.class);



            if (Unit.count(Unit.class) > 0)
                Unit.deleteAll(Unit.class);

            if (Company.count(Company.class) > 0)
                Company.deleteAll(Company.class);

            getActivity().finish();
            startActivity(getActivity().getIntent());

        });
        //endregion Cast Variable Dialog Sync



        binding.cardShare.setOnClickListener(v -> {
            NavDirections action = MoreFragmentDirections.actionGoToContactUsFragment("Share");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });

        binding.cardCredit.setOnClickListener(v -> {
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

        binding.ivPower.setOnClickListener(v -> showError("آیا مایل به خروج از حساب خود هستید؟", 1));




        binding.cardProfile.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToProfileFragment)
        );


        binding.cardContactUs.setOnClickListener(v -> {
            NavDirections action = MoreFragmentDirections.actionGoToContactUsFragment("Contact");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });


        binding.cardAboutUs.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToAboutUsFragment));


        binding.cardOrder.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToOrderFragment));


        if (account != null ) {
            binding.tvCredits.setText("موجودی : " + format.format(account.getCRDT()) + " ریال ");
        } else {
            binding.tvCredits.setText("موجودی : " + "0" + " ریال ");
        }



        binding.ivBackFragment.setOnClickListener(view1 -> Navigation.findNavController(binding.getRoot()).popBackStack());

    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        myViewModel.getInquiryAccount(userName,passWord, account.getM());

        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultInquiryAccount().setValue(null);

            disableAccount = false;
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();
            //user is register
            try {
                if (result.size() > 0) {
                    Account.deleteAll(Account.class);
                    Account.saveInTx(result);

                    Account acc = Select.from(Account.class).first();
                    if (acc != null )
                        binding.tvCredits.setTextColor(getActivity().getResources().getColor(R.color.medium_color));

                    binding.tvCredits.setText("موجودی : " + format.format(acc.getCRDT()) + " ریال ");
                } else {
                    binding.tvCredits.setTextColor(getActivity().getResources().getColor(R.color.red));
                    binding.tvCredits.setText("خطا در بروز رسانی موجودی ");
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