package ir.kitgroup.saleinBahraman.Fragments;


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

import ir.kitgroup.saleinBahraman.R;

import ir.kitgroup.saleinBahraman.databinding.AboutUsFragmentBinding;
import ir.kitgroup.saleinBahraman.models.Company;

@AndroidEntryPoint
public class AboutUsFragment extends Fragment {


    @Inject
    Company company;




    //region Parameter
    private AboutUsFragmentBinding binding;
    private int imageLogo;
    private String title;
    private String description;
    private String phoneNumber;
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
        switch (company.nameCompany) {


            case "ir.kitgroup.saleintop":
                imageLogo = R.drawable.top_png;
                title="تاپ کباب";
                description="عرضه کننده بهترین غذاها";
                phoneNumber="05137638311";

                break;


            case "ir.kitgroup.saleinmeat":
                imageLogo = R.drawable.meat_png;
                title="گوشت دنیوی";
                description="عرضه کننده انواع گوشت";
                phoneNumber="05137335985";
                break;

            case "ir.kitgroup.saleinnoon":
                imageLogo = R.drawable.noon;
                title="کافه نون";
                description="";
                break;
        }


            binding.imageView.setImageResource(imageLogo);
        binding.title.setText(title);
        binding.description.setText(description);
        binding.textView4.setText(phoneNumber);


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
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(intent);
            }
        });


    }
}


