package ir.kitgroup.saleinjam.Fragments;

import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinjam.Activities.LauncherActivity;
import ir.kitgroup.saleinjam.databinding.AboutUsFragmentBinding;
import ir.kitgroup.saleinjam.models.Config;

@AndroidEntryPoint
public class AboutUsFragment extends Fragment {

    @Inject
    Config config;
    private AboutUsFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AboutUsFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

        binding.txtDescription.setText(config.Aboutus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            binding.txtDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);

        }

    }
}
