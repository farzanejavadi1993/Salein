package ir.kitgroup.saleinfingilkabab.ui.launcher.homeItem.details;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleinfingilkabab.Connect.CompanyViewModel;
import ir.kitgroup.saleinfingilkabab.R;
import ir.kitgroup.saleinfingilkabab.databinding.ActivityDetailBinding;
import ir.kitgroup.saleinfingilkabab.DataBase.Company;


@AndroidEntryPoint
public class ShowDetailFragment extends Fragment {
    private ActivityDetailBinding binding;
    private Company company;
    private String Id;
    private CompanyViewModel myViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Id = ShowDetailFragmentArgs.fromBundle(getArguments()).getId();

        company = Select.from(Company.class).first();

        String ip = company.getIp1();

        Picasso.get()
                .load("http://" + ip + "/GetImage?productId=" + Id + "&width=" + 600 + "&height=" + 600)
                .error(R.drawable.nopic)
                .placeholder(R.drawable.loading)
                .into(binding.ivProduct);


    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        myViewModel.getProduct(company.getUser(), company.getPass(), Id);
        myViewModel.getResultProduct().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            myViewModel.getResultProduct().setValue(null);

            if (result.size() > 0) {
                binding.tvName.setText(result.get(0).getN().trim());
                if (!result.get(0).getDes().equals(""))
                binding.tvDesc.setVisibility(View.VISIBLE);


                binding.tvDescriptionProduct.setText(result.get(0).getDes());
            } else
                Toast.makeText(getActivity(), "لیست دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();

            binding.progressBar.setVisibility(View.GONE);

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            binding.progressBar.setVisibility(View.GONE);

            if (result == null)
                return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}