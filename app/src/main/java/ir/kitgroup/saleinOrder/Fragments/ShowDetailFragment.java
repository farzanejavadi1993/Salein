package ir.kitgroup.saleinOrder.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Activities.LauncherActivity;
import ir.kitgroup.saleinOrder.Connect.API;
import ir.kitgroup.saleinOrder.DataBase.User;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.classes.ConfigRetrofit;
import ir.kitgroup.saleinOrder.classes.Util;
import ir.kitgroup.saleinOrder.databinding.ActivityDetailBinding;
import ir.kitgroup.saleinOrder.DataBase.Company;
import ir.kitgroup.saleinOrder.models.ModelProduct;

@AndroidEntryPoint
public class ShowDetailFragment  extends Fragment {
    @Inject
    Company company;


    @Inject
    API api;


    @Inject
    SharedPreferences sharedPreferences;



    private ActivityDetailBinding binding;

    private  CompositeDisposable compositeDisposable;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);
        compositeDisposable=new CompositeDisposable();
        if (!Util.RetrofitValue) {
            ConfigRetrofit configRetrofit = new ConfigRetrofit();
            company=null;
            api=null;
            company = Select.from(Company.class).first();
            api = configRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/").create(API.class);

        }


        Bundle bundle = getArguments();
        String Id = bundle.getString("Id");



        String ip="";
        if (company.namePackage!=null && company.namePackage.equals("ir.kitgroup.saleinOrder"))
            ip= Select.from(User.class).first().ipLocal;
        else
            ip = company.IP1;

        getProduct(Id);
        Picasso.get()
                .load("http://" + ip + "/GetImage?productId=" + Id+"&width="+(int)Util.width+"&height="+(int)Util.height/2)
                .error(company.imageLogo)
                .placeholder(R.drawable.loading)
                .into(binding.ivProduct);




    }

    private Boolean networkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("SetTextI18n")
    private void getProduct(String Guid) {

        if (!networkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "خطا در اتصال به اینترنت", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        try {

            compositeDisposable.add(
                    api.getProduct(company.userName, company.passWord, Guid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {


                                Gson gson = new Gson();
                                Type typeModelProduct = new TypeToken<ModelProduct>() {
                                }.getType();
                                ModelProduct iDs = null;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeModelProduct);
                                }
                                catch (Exception ignore) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }


                                if (iDs != null && iDs.getProductList().size()>0) {
                                    binding.tvDescriptionProduct.setText(iDs.getProductList().get(0).getDes());
                                }

                                else {
                                    Toast.makeText(getActivity(), "لیست دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();

                                }

                                binding.progressBar.setVisibility(View.GONE);

                            }, throwable -> {

                                Toast.makeText(getActivity(),"خطا در ارتباط با سرور",Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);
                            })
            );
        } catch (Exception e) {
            Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        }

    }




    @Override
    public void onDestroyView() {
        //setADR1 = false;
        super.onDestroyView();
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
        compositeDisposable.dispose();
        binding = null;


    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }


}
