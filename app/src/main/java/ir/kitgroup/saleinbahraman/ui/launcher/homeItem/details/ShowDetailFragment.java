package ir.kitgroup.saleinbahraman.ui.launcher.homeItem.details;

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
import ir.kitgroup.saleinbahraman.Activities.LauncherActivity;
import ir.kitgroup.saleinbahraman.Connect.MyViewModel;
import ir.kitgroup.saleinbahraman.R;
import ir.kitgroup.saleinbahraman.classes.Util;
import ir.kitgroup.saleinbahraman.databinding.ActivityDetailBinding;
import ir.kitgroup.saleinbahraman.DataBase.Company;


@AndroidEntryPoint
public class ShowDetailFragment extends Fragment {


    private ActivityDetailBinding binding;
    private Company company;
    private  String Id;

    private MyViewModel myViewModel;

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
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

        company = Select.from(Company.class).first();
        Bundle bundle = getArguments();
        Id = bundle.getString("Id");


        String ip = company.IP1;

        Picasso.get()
                .load("http://" + ip + "/GetImage?productId=" + Id + "&width=" + (int) Util.width + "&height=" + (int) Util.height / 2)
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.ivProduct);


    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getProduct(company.USER,company.PASS,Id);

        myViewModel.getResultProduct().observe(getViewLifecycleOwner(), result -> {


            if (result == null) {
             binding.progressBar.setVisibility(View.GONE);
                return;
            }

            myViewModel.getResultProduct().setValue(null);

            if (result.size() > 0) {
                binding.tvDescriptionProduct.setText(result.get(0).getDes());
            } else {
                Toast.makeText(getActivity(), "لیست دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
            }
            binding.progressBar.setVisibility(View.GONE);

        });
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            binding.progressBar.setVisibility(View.GONE);

            if (result == null)
                return;
         //   myViewModel.getResultMessage().setValue(null);
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
        binding = null;
    }

}