package ir.kitgroup.salein.ui.companies;

import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import ir.kitgroup.salein.databinding.BannerFragmentBinding;


public class BannerFragment extends Fragment {
    private BannerFragmentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=BannerFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(this).asGif().load(Uri.parse("file:///android_asset/donyavi.gif")).into(binding.adv1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvDescript1.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);

        }

        binding.tvMore1.setOnClickListener(view1 -> {
            if (binding.tvDescript1.getVisibility()==View.VISIBLE) {
                binding.tvMore1.setText("بیشتر");
                binding.tvDescript1.setVisibility(View.GONE);
            }
            else {
                binding.tvMore1.setText("بستن");
                binding.tvDescript1.setVisibility(View.VISIBLE);
            }
        });


    }


}
