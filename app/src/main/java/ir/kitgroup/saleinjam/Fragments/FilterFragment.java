package ir.kitgroup.saleinjam.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinjam.Activities.LauncherActivity;
import ir.kitgroup.saleinjam.databinding.FilterFragmentBinding;

@AndroidEntryPoint
public class FilterFragment extends Fragment {


    @Inject
    SharedPreferences sharedPreferences;
    private FilterFragmentBinding binding;
    private boolean filterDiscount = false;
    private boolean filterVip = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FilterFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);


        filterDiscount = sharedPreferences.getBoolean("discount", false);
        filterVip = sharedPreferences.getBoolean("vip", false);


        if (filterDiscount) {
            binding.switchDiscount.setChecked(true);
        } else if (filterVip) {
            binding.switchVip.setChecked(true);
        }


        binding.tvDeleteFilter.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("vip", false).apply();
            sharedPreferences.edit().putBoolean("discount", false).apply();
            if (filterDiscount || filterVip) {
                binding.switchDiscount.setChecked(false);
                binding.switchVip.setChecked(false);
                Toast.makeText(getActivity(), "با موفقیت حذف شد.", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getActivity(), "هیچ فیلتری وجود ندارد", Toast.LENGTH_SHORT).show();
        });


        binding.switchDiscount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                filterDiscount = true;
                filterVip = false;
                binding.switchVip.setChecked(false);
                reloadSpecialMethod();

            } else {
                binding.switchVip.setChecked(false);
                filterDiscount = false;
                filterVip = false;
                reloadMainMethod();
                sharedPreferences.edit().putBoolean("discount", false).apply();
            }
        });


        binding.switchVip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                filterDiscount = false;
                filterVip = true;
                binding.switchDiscount.setChecked(false);
                reloadSpecialMethod();
            } else {
                binding.switchDiscount.setChecked(false);
                filterDiscount = false;
                filterVip = false;
                reloadMainMethod();
                sharedPreferences.edit().putBoolean("vip", false).apply();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
    }

    private void reloadSpecialMethod() {
        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");
        if (frg instanceof MainOrderFragment) {
            MainOrderFragment fgf = (MainOrderFragment) frg;
            if (filterDiscount)
                fgf.getDiscountProduct();
            else if (filterVip)
                fgf.getProductVipSync();
        }
    }

    private void reloadMainMethod() {
        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");
        if (frg instanceof MainOrderFragment) {
            MainOrderFragment fgf = (MainOrderFragment) frg;
            fgf.getProductLevel1();
        }
    }
}
