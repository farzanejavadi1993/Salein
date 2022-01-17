package ir.kitgroup.saleinjam.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ir.kitgroup.saleinjam.Activities.LauncherActivity;
import ir.kitgroup.saleinjam.databinding.FilterFragmentBinding;

public class FilterFragment extends Fragment {

    private FilterFragmentBinding binding;
    private  boolean filter=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =FilterFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle=getArguments();
        if (bundle!=null)
         filter=bundle.getBoolean("filter");



        if (filter){
            binding.btnFilter.setVisibility(View.VISIBLE);
            binding.switchDiscount.setChecked(true);
        }

        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);


        binding.btnFilter.setOnClickListener(v -> {

            if (filter){
                filter=false;
                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");
                if (frg instanceof MainOrderFragment) {
                    MainOrderFragment fgf = (MainOrderFragment) frg;
                    fgf.getDiscountProduct();
                }
            } });


        binding.tvDeleteFilter.setOnClickListener(v -> {

            if (filter){
                binding.switchDiscount.setChecked(false);
                Toast.makeText(getActivity(), "با موفقیت حذف شد.", Toast.LENGTH_SHORT).show();
                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");
                if (frg instanceof MainOrderFragment) {
                    MainOrderFragment fgf = (MainOrderFragment) frg;
                    fgf.getProductLevel1();

                }
            }else
                Toast.makeText(getActivity(), "هیچ فیلتری وجود ندارد", Toast.LENGTH_SHORT).show();
        });



        binding.switchDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                 filter=true;
                    binding.btnFilter.setVisibility(View.VISIBLE);
                }
                else{
            filter=false;
                    binding.btnFilter.setVisibility(View.GONE);
            }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
    }

}
