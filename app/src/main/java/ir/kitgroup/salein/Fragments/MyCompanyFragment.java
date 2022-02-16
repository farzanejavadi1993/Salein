package ir.kitgroup.salein.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.databinding.FragmentMyCompanyBinding;

@AndroidEntryPoint
public class MyCompanyFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentMyCompanyBinding binding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentMyCompanyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.txtError.setText("لیست  علاقه مندی های شما خالی می باشد با مراجعه به تب فروشگاه ها ،فروشگاه  مورد نظر خود را به لیست علاقه مندی ها اضافه کنید کنید");


    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
