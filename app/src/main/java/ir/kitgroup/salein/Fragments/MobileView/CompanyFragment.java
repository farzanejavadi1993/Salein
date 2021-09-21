package ir.kitgroup.salein.Fragments.MobileView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.databinding.FragmentCompanyBinding;

public class CompanyFragment  extends Fragment {

    private FragmentCompanyBinding binding;
    private Boolean top,meat,salein=false;
    private SharedPreferences sharedPreferences;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
     binding=FragmentCompanyBinding.inflate(getLayoutInflater());
     return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        top=sharedPreferences.getBoolean("top",false);
        salein=sharedPreferences.getBoolean("salein",false);
        meat=sharedPreferences.getBoolean("meat",false);


        binding.checkBoxSalein.setChecked(salein);
        binding.checkboxTop.setChecked(top);
        binding.checkboxMeat.setChecked(meat);

        binding.checkBoxSalein.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    salein=true;
                else
                    salein=false;



            }
        });

        binding.checkboxTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    top=true;
                else
                    top=false;


            }
        });

        binding.checkboxMeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    meat=true;
                else
                    meat=false;


            }
        });


        binding.btnRegisterCompany.setOnClickListener(v -> {

            sharedPreferences.edit().putBoolean("top",top).apply();
            sharedPreferences.edit().putBoolean("salein",salein).apply();
            sharedPreferences.edit().putBoolean("meat",meat).apply();
            LauncherActivity mainActivity = (LauncherActivity) v.getContext();

            Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag("StoriesFragment");
            if (fragment instanceof StoriesFragment) {
                StoriesFragment fgf = (StoriesFragment)fragment;
                fgf.setMyCompany();
            }
        });
    }


}
