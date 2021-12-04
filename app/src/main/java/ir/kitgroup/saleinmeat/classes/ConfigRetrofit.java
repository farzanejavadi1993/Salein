package ir.kitgroup.saleinmeat.classes;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import ir.kitgroup.saleinmeat.Connect.API;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigRetrofit {
    public static Retrofit retrofit;
    public static GsonBuilder gsonBuilder;
    public static OkHttpClient.Builder client;


    public static Retrofit getRetrofit(String baseUrl, Boolean Nullable) {


        if (Nullable) {
            gsonBuilder = null;
            retrofit=null;
            retrofit=null;
            client=null;
        }
        try {

            if (gsonBuilder == null) {
                gsonBuilder = new GsonBuilder();
                gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);


                client = new OkHttpClient.Builder();
                client.readTimeout(30, TimeUnit.SECONDS);
                client.connectTimeout(30, TimeUnit.SECONDS);


                retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(baseUrl)
                        .client(client.build())
                        .build();
            }


        } catch (Exception e) {

        }
        return retrofit;
    }


}
