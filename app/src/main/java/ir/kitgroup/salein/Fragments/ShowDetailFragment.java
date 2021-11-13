package ir.kitgroup.salein.Fragments;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.ConfigRetrofit;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.ActivityDetailBinding;
import ir.kitgroup.salein.models.Company;
import ir.kitgroup.salein.models.ModelProduct;

@AndroidEntryPoint
public class ShowDetailFragment  extends Fragment {
    @Inject
    Company company;


    @Inject
    API api;


    @Inject
    SharedPreferences sharedPreferences;



    private ActivityDetailBinding binding;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (!Util.RetrofitValue) {
            ConfigRetrofit configRetrofit = new ConfigRetrofit();
            String name = sharedPreferences.getString("CN", "");
            company = configRetrofit.getCompany(name);
            api = configRetrofit.getRetrofit(company.baseUrl).create(API.class);

        }

        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        Bundle bundle = getArguments();
        String Id = bundle.getString("Id");



        String ip = company.ipLocal;

        getProduct(Id);
        Picasso.get()
                .load("http://" + ip + "/GetImage?productId=" + Id
                        )
                .error(company.imageLogo)
                .placeholder(R.drawable.loading)
                .into(binding.ivProduct);

        return binding.getRoot();

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

}
