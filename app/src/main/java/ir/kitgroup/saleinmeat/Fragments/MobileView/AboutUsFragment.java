package ir.kitgroup.saleinmeat.Fragments.MobileView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.Invoice;
import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.DataBase.OrderType;
import ir.kitgroup.saleinmeat.DataBase.Product;
import ir.kitgroup.saleinmeat.DataBase.ProductGroupLevel1;
import ir.kitgroup.saleinmeat.DataBase.ProductGroupLevel2;
import ir.kitgroup.saleinmeat.DataBase.Setting;
import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.DataBase.User;

import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.databinding.AboutUsFragmentBinding;
import ir.kitgroup.saleinmeat.databinding.FragmentSettingBinding;

public class AboutUsFragment extends Fragment {

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
        switch (LauncherActivity.name) {
            case "ir.kitgroup.salein":
                imageLogo = R.drawable.logo1;
                title="سالین دمو";
                description="سالین دمو ، راهنمای استفاده از اپلیکیشن";
                phoneNumber="05137638311";
                break;

            case "ir.kitgroup.saleintop":
                imageLogo = R.drawable.top_png;
                title="تاپ کباب";
                description="عرضه کننده بهترین غذاها";
                phoneNumber="05137335985";
                break;


            case "ir.kitgroup.saleinmeat":
                imageLogo = R.drawable.meat_png;
                title="گوشت دنیوی";
                description="عرضه کننده انواع گوشت";
                phoneNumber="05137638311";
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


