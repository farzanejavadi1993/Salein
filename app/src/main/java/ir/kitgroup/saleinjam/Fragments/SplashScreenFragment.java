package ir.kitgroup.saleinjam.Fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinjam.Connect.API;
import ir.kitgroup.saleinjam.DataBase.Account;
import ir.kitgroup.saleinjam.R;

import ir.kitgroup.saleinjam.classes.ConfigRetrofit;

import ir.kitgroup.saleinjam.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.models.Config;
import ir.kitgroup.saleinjam.models.ModelCompany;

@AndroidEntryPoint
public class SplashScreenFragment extends Fragment {


    //region Parameter
    @Inject
    Config config;


    private  Company company;
    private API api;
    private CompositeDisposable compositeDisposable;
    private FragmentSplashScreenBinding binding;
    private PackageInfo appId;

    //endregion Parameter


    //region Override Method

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        company=Select.from(Company.class).first();
        api = ConfigRetrofit.getRetrofit("http://api.kitgroup.ir/api/REST/",true,5).create(API.class);
        compositeDisposable = new CompositeDisposable();

        if (config.INSKU_ID.equals("ir.kitgroup.saleinmeat")) {
            Glide.with(this).load(Uri.parse("file:///android_asset/donyavi.gif")).into(binding.animationView);
            binding.mainBackground.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

        }else
            Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);


        binding.tvTitle.setText(company!=null && company.N!=null && !config.INSKU_ID.equals("ir.kitgroup.salein") ? company.N:config.N);
        binding.tvDescription.setText(company!=null && company.DESC!=null && !config.INSKU_ID.equals("ir.kitgroup.salein") ?company.DESC:config.DESC);


        try {
          String  appVersion = appVersion();
            binding.tvversion.setText(" نسخه " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
             appId = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {

                        sleep(4000);
                    getCompany();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();


        binding.lnrError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.animationView.setVisibility(View.VISIBLE);
                binding.animationView1.setVisibility(View.GONE);
                binding.lnrError.setVisibility(View.GONE);
                getCompany();
            }
        });



    }
    //endregion Override Method


    //region Custom Method
    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }


    @SuppressLint("SetTextI18n")
    private void getCompany() {
        try {
            compositeDisposable.add(
                    api.getCompany("")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelCompany>() {
                                }.getType();
                                ModelCompany iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                }
                                catch (Exception e) {

                                    binding.txtErrorr.setText("مدل دریافت شده از شرکت ها نامعتبر است.");
                                    binding.animationView1.setImageResource(R.drawable.warning);
                                    binding.animationView1.setVisibility(View.VISIBLE);
                                    binding.animationView.setVisibility(View.GONE);
                                    binding.lnrError.setVisibility(View.VISIBLE);
                                    return;
                                }


                                if (iDs != null) {
                                    if (iDs.getCompany() != null) {
                                        CollectionUtils.filter(iDs.getCompany(), i -> i.INSK_ID!=null && i.INSK_ID.equals(appId.packageName.toString()));

                                        if (iDs.getCompany().size() == 1) {

                                            Company.deleteAll(Company.class);
                                            Company.saveInTx(iDs.getCompany().get(0));

                                            ConfigRetrofit.getRetrofit("http://"+iDs.getCompany().get(0).IP1+"/api/REST/",true,30);

                                            FragmentTransaction addFragment;

                                            //region Account Is Login & Register
                                            if (Select.from(Account.class).list().size() > 0) {

                                                if (config.INSKU_ID.equals("ir.kitgroup.salein"))
                                                    addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment");

                                                else {
                                                    Bundle bundleMainOrder = new Bundle();
                                                    bundleMainOrder.putString("Inv_GUID", "");
                                                    bundleMainOrder.putString("Tbl_GUID", "");
                                                    bundleMainOrder.putString("Ord_TYPE", "");
                                                    MainOrderFragment mainOrderFragment = new MainOrderFragment();
                                                    mainOrderFragment.setArguments(bundleMainOrder);
                                                    addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment");
                                                }


                                            }
                                            //endregion Account Is Login & Register

                                            else {
                                                addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginClientFragment(), "LoginClientFragment");
                                            }


                                            addFragment.commit();


                                        }
                                        else {
                                            binding.animationView1.setImageResource(R.drawable.warning);
                                            binding.animationView1.setVisibility(View.VISIBLE);
                                            binding.txtErrorr.setText("اطلاعات شرکت در سرور موجود نمی باشد.");

                                            binding.animationView.setVisibility(View.GONE);
                                            binding.lnrError.setVisibility(View.VISIBLE);

                                        }

                                    }


                                }
                                else {
                                    binding.animationView1.setImageResource(R.drawable.warning);
                                    binding.animationView1.setVisibility(View.VISIBLE);
                                    binding.txtErrorr.setText("خطا در ارتباط با سرور ، دوباره سعی کنید.");

                                    binding.animationView.setVisibility(View.GONE);
                                    binding.lnrError.setVisibility(View.VISIBLE);

                                }


                            }, throwable -> {

                                if (!config.INSKU_ID.equals("ir.kitgroup.salein")){
                                    if (Select.from(Company.class).list().size() == 0) {
                                        binding.animationView1.setImageResource(R.drawable.warning);
                                        binding.animationView1.setVisibility(View.VISIBLE);
                                        binding.txtErrorr.setText("اطلاعات شرکت به درستی از سرور دریافت نشده است ، دوباره سعی کنید.");
                                        binding.animationView.setVisibility(View.GONE);

                                        binding.lnrError.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        ConfigRetrofit.getRetrofit("http://"+Select.from(Company.class).first().IP1+"/api/REST/",true,30);
                                        FragmentTransaction addFragment;

                                        //region Account Is Login & Register
                                        if (Select.from(Account.class).list().size() > 0) {


                                            if (config.INSKU_ID.equals("ir.kitgroup.salein"))
                                                addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment");

                                            else {
                                                Bundle bundleMainOrder = new Bundle();
                                                bundleMainOrder.putString("Inv_GUID", "");
                                                bundleMainOrder.putString("Tbl_GUID", "");
                                                bundleMainOrder.putString("Ord_TYPE", "");
                                                MainOrderFragment mainOrderFragment = new MainOrderFragment();
                                                mainOrderFragment.setArguments(bundleMainOrder);
                                                addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment");
                                            }


                                        }
                                        //endregion Account Is Login & Register

                                        else {
                                            addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginClientFragment(), "LoginClientFragment");
                                        }

                                        addFragment.commit();
                                    }
                                }else {
                                    binding.animationView1.setImageResource(R.drawable.warning);
                                    binding.animationView1.setVisibility(View.VISIBLE);

                                    binding.txtErrorr.setText("خطا در ارتباط با سرور ، دوباره سعی کنید.");
                                    binding.animationView.setVisibility(View.GONE);
                                    binding.lnrError.setVisibility(View.VISIBLE);
                                }


                            })
            );
        } catch (Exception e) {
            binding.animationView1.setImageResource(R.drawable.warning);
            binding.animationView1.setVisibility(View.VISIBLE);

            binding.txtErrorr.setText("خطا در ارتباط با سرور ، دوباره سعی کنید.");
            binding.animationView.setVisibility(View.GONE);
            binding.lnrError.setVisibility(View.VISIBLE);

        }

    }

    //endregion Custom Method


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
        binding = null;



    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }


}
