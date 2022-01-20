package ir.kitgroup.saleinjam.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.net.Uri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.orm.query.Select;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinjam.Activities.LauncherActivity;

import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.databinding.ContactUsFragmentBinding;
import ir.kitgroup.saleinjam.models.Config;

@AndroidEntryPoint
public class ContactUsFragment extends Fragment {

    @Inject
    Config config;

    private ContactUsFragmentBinding binding;
    private Company company;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding =ContactUsFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

        company = Select.from(Company.class).first();
        binding.imageView.setImageResource(config.imageLogo);

        binding.title.setText(company.N);
        binding.description.setText(company.DESC);
        binding.textView4.setText(company.T1);

        binding.ivBack.setOnClickListener(v -> getFragmentManager().popBackStack());

        binding.Call1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + company.T1));
            startActivity(intent);
        });


      if (!config.watsApp.equals("") || !config.Instagram.equals(""))
          binding.layoutSocial.setVisibility(View.VISIBLE);




      binding.watsapp.setOnClickListener(v -> {

          if (!config.watsApp.equals("")){
              PackageManager pm = getActivity().getPackageManager();

              try
              {
                  PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                  Intent waIntent = new Intent(Intent.ACTION_SEND);
                  waIntent.setType("text/plain");
                  waIntent.setPackage("com.whatsapp");
                  waIntent.putExtra(Intent.EXTRA_TEXT, config.watsApp);
                  startActivity(waIntent);
              }
              catch (PackageManager.NameNotFoundException e)
              {
                  Toast.makeText(getActivity(), "لطفا اپلیکیشن واتس آپ نصب کنید.", Toast.LENGTH_SHORT)
                          .show();
              }
          }else {
              Toast.makeText(getActivity(), "در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
          }
      });



      binding.instagram.setOnClickListener(v -> {
          if (!config.Instagram.equals("")){
              Uri uri = Uri.parse(config.Instagram);
              Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

              likeIng.setPackage("com.instagram.android");

              try {
                  startActivity(likeIng);
              } catch (ActivityNotFoundException e) {
                  startActivity(new Intent(Intent.ACTION_VIEW,
                          Uri.parse(config.Instagram)));
              }
          }else {
              Toast.makeText(getActivity(), "در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
          }
      });


    }
}


