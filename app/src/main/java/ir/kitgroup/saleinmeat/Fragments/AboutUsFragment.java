package ir.kitgroup.saleinmeat.Fragments;


import android.content.Intent;
import android.net.Uri;
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

import ir.kitgroup.saleinmeat.databinding.AboutUsFragmentBinding;
import ir.kitgroup.saleinmeat.models.Company;

@AndroidEntryPoint
public class AboutUsFragment extends Fragment {


    @Inject
    Company company;




    //region Parameter
    private AboutUsFragmentBinding binding;

    //endregion Parameter





    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        binding = AboutUsFragmentBinding.inflate(getLayoutInflater());

        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //region Set Icon And Title



            binding.imageView.setImageResource(company.imageDialog);
        binding.title.setText(company.title);
        binding.description.setText(company.Description);
        binding.textView4.setText(company.numberPhone);


        //endregion Set Icon And Title

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        binding.Call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+company.numberPhone));
                startActivity(intent);
            }
        });


    }
}


