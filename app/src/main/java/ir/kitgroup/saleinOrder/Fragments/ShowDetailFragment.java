package ir.kitgroup.saleinOrder.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.classes.ConfigRetrofit;
import ir.kitgroup.saleinOrder.classes.ServerConfig;
import ir.kitgroup.saleinOrder.classes.Util;
import ir.kitgroup.saleinOrder.databinding.ActivityDetailBinding;
import ir.kitgroup.saleinOrder.DataBase.Company;
import ir.kitgroup.saleinOrder.models.Config;
import ir.kitgroup.saleinOrder.models.ModelProduct;

@AndroidEntryPoint
public class ShowDetailFragment extends Fragment {
    @Inject
    Config config;
    @Inject
    SharedPreferences sharedPreferences;

    private ActivityDetailBinding binding;
    private CompositeDisposable compositeDisposable;
    private API api;
    private Company company;
    private ServerConfig serverConfig;

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
        compositeDisposable = new CompositeDisposable();
        company = Select.from(Company.class).first();
        if (company.mode==2)
        api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/", false,30).create(API.class);

        Bundle bundle = getArguments();
        String Id = bundle.getString("Id");

        if (company.mode==2){
            String ip = company.IP1;
            getProduct(Id);
            Picasso.get()
                    .load("http://" + ip + "/GetImage?productId=" + Id + "&width=" + (int) Util.width + "&height=" + (int) Util.height / 2)
                    .error(config.imageLogo)
                    .placeholder(config.INSKU_ID.equals("ir.kitgroup.saleinmeat") ?R.drawable.donyavilaoding:R.drawable.loading)
                    .into(binding.ivProduct);
        }
        else {
            AsyncTask asyncTask= new AsyncTask() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    api = ConfigRetrofit.getRetrofit("http://" + serverConfig.URL1 + "/api/REST/", true,30).create(API.class);
                    String ip = serverConfig.URL1 ;
                    getProduct(Id);
                    Picasso.get()
                            .load("http://" + ip + "/GetImage?productId=" + Id + "&width=" + (int) Util.width + "&height=" + (int) Util.height / 2)
                            .error(config.imageLogo)
                            .placeholder(R.drawable.loading)
                            .into(binding.ivProduct);
                }
                @SuppressLint("StaticFieldLeak")
                @Override
                protected Object doInBackground(Object[] params) {
                    serverConfig =new ServerConfig(company.IP1,company.IP2);
                    return 0;
                }
            };
     asyncTask.execute(0);
        }
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
                    api.getProduct(company.USER, company.PASS, Guid)
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
                                } catch (Exception ignore) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                                if (iDs != null && iDs.getProductList().size() > 0) {
                                    binding.tvDescriptionProduct.setText(iDs.getProductList().get(0).getDes());
                                } else {
                                    Toast.makeText(getActivity(), "لیست دریافت شده از کالا ها نامعتبر است", Toast.LENGTH_SHORT).show();
                                }
                                binding.progressBar.setVisibility(View.GONE);

                            }, throwable -> {
                                Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
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
        super.onDestroyView();
        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
        compositeDisposable.dispose();
        binding = null;
    }
    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear(); }
}