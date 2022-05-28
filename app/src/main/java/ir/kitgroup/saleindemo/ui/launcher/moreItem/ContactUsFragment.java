package ir.kitgroup.saleindemo.ui.launcher.moreItem;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.databinding.ContactUsFragmentBinding;
import ir.kitgroup.saleindemo.ui.logins.RegisterFragmentArgs;


@AndroidEntryPoint
public class ContactUsFragment extends Fragment {


    @Inject
    SharedPreferences sharedPreferences;

    private String updateLink="";

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



        Bundle bundle=getArguments();
        int type=bundle.getInt("type");

        String show = ContactUsFragmentArgs.fromBundle(getArguments()).getShow();


        updateLink = sharedPreferences.getString("update_link", "");
        company = Select.from(Company.class).first();



        Picasso.get()
                .load(Util.DEVELOPMENT_BASE_URL_Img +"/GetCompanyImage?id=" +
                        company.getI()+"&width=300&height=300")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.imageView);


        binding.title.setText(company.getN());


        if (show.equals("Contact")){
            binding.image.setImageResource(R.drawable.ic_support);
            binding.description.setText(company.getDesc());
            binding.textView4.setText(company.getT1());
        }else {
            binding.txtTitleToolbar.setText("دعوت از دوستان");
            binding.image.setImageResource(R.drawable.ic_share);
            Users account= Select.from(Users.class).first();
            binding.description.setText(company!=null && company.getTxt1()!=null ?company.getTxt1():"");
            binding.textView4.setText("کد معرف : "+account.getC());
        }


        binding.imageView.setOnClickListener(v -> getFragmentManager().popBackStack());



        binding.Call1.setOnClickListener(v -> {
            if (type==1){
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + company.T1));
                startActivity(intent);
            }else {
                if (!updateLink.equals(""))
                    shareApplication();
                else
                    Toast.makeText(getActivity(), "لینک دانلود از سرور تنظیم نشده است.", Toast.LENGTH_SHORT).show();
            }

        });


        if ((!config.watsApp.equals("") || !config.Instagram.equals("") ||  !config.website.equals("")) && type==1 )
            binding.layoutSocial.setVisibility(View.VISIBLE);




        binding.watsapp.setOnClickListener(v -> {

            if (!config.watsApp.equals("")){

                boolean install= appInstallOrNot("com.whatsapp");

                if (install){


                    Intent intentWhatsAppGroup = new Intent(Intent.ACTION_VIEW); Uri uri =
                            Uri.parse(config.watsApp);
                    intentWhatsAppGroup.setData(uri);
                    intentWhatsAppGroup.setPackage("com.whatsapp");
                    startActivity(intentWhatsAppGroup);
                }else {
                    Toast.makeText(getActivity(), "لطفا اپلیکیشن واتس آپ نصب کنید.", Toast.LENGTH_SHORT)
                            .show();
                }

            }else {
                Toast.makeText(getActivity(), "در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
            }
        });



        binding.ivBackFragment.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();;
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



        binding.website.setOnClickListener(v -> {
            if (!config.website.equals("")){
                Uri uri = Uri.parse(config.website);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }else {
                Toast.makeText(getActivity(), "در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private  boolean appInstallOrNot(String url){
        PackageManager packageManager= getActivity().getPackageManager();

        boolean app_install;
        try {
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            app_install=true;
        }catch (Exception ignored){
            app_install=false;
        }
        return app_install;
    }


    private void shareApplication() {
        Users account=   Select.from(Users.class).first();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String my_string = " سلام "+account.N +" شما را به "+company.N +" دعوت کرده است.از طریق لینک زیر برنامه را دانلود کنید."+"\n"+
                updateLink
                +"\n"+"کد معرف : " + account.getC();
        intent.putExtra(Intent.EXTRA_TEXT, my_string);
        startActivity(Intent.createChooser(intent, "اشتراک این برنامه با"));
    }
}


