package ir.kitgroup.saleinhamsafar.ui.launcher.moreItem;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import ir.kitgroup.saleinhamsafar.Activities.LauncherActivity;
import ir.kitgroup.saleinhamsafar.DataBase.Company;
import ir.kitgroup.saleinhamsafar.R;
import ir.kitgroup.saleinhamsafar.databinding.AboutUsFragmentBinding;


public class AboutUsFragment extends Fragment {


    private AboutUsFragmentBinding binding;

    private Company company;
    private String AppVersion="";
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

        company= Select.from(Company.class).first();
        binding.txtDescription.setText(company!=null && company.ABUS !=null?company.ABUS :"");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.txtDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        binding.ivBackFragment.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());


        try {
            AppVersion = appVersion();
            binding.txtVersion.setText( " نسخه "+AppVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Picasso.get()
                .load("http://api.kitgroup.ir/GetCompanyImage?id=" +
                        company.I+"&width=300&height=300")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.ivLogo);

    }


    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }

}