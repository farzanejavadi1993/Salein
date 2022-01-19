package ir.kitgroup.saleinkonj.classes;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigRetrofit {
    public static Retrofit retrofit;
    public static GsonBuilder gsonBuilder;
    public static OkHttpClient.Builder client;
    public  static   int time=30;


    public static Retrofit getRetrofit(String baseUrl, Boolean Nullable,int time1) {

        time=time1;

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
                client.readTimeout(time, TimeUnit.SECONDS);
                client.connectTimeout(time, TimeUnit.SECONDS);


                retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(baseUrl)
                        .client(client.build())
                        .build();
            }


        } catch (Exception ignored) {

        }
        return retrofit;
    }


}
