package ir.kitgroup.saleinkonj.Fragments;

import android.content.Intent;
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
import com.orm.query.Select;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinkonj.Activities.LauncherActivity;
import ir.kitgroup.saleinkonj.databinding.AboutUsFragmentBinding;
import ir.kitgroup.saleinkonj.DataBase.Company;
import ir.kitgroup.saleinkonj.models.Config;

@AndroidEntryPoint
public class AboutUsFragment extends Fragment {

    @Inject
    Config config;

    private AboutUsFragmentBinding binding;
    private Company company;


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
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

        company = Select.from(Company.class).first();


        binding.title.setText(company.N);
        String Title=company.DESC.split("&&")[0];
        String description=company.DESC.split("&&")[1];
        binding.description.setText(Title);
        binding.textView4.setText(company.T1);

        binding.ivBack.setOnClickListener(v -> getFragmentManager().popBackStack());

        binding.Call1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + company.T1));
            startActivity(intent);
        });


        binding.description.setText(description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            binding.description.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);

        }
    }
}


