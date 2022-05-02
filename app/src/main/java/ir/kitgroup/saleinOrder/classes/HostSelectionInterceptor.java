package ir.kitgroup.saleinOrder.classes;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

@Singleton
public class HostSelectionInterceptor implements Interceptor {
    private volatile HttpUrl host = HttpUrl.parse(Util.PRODUCTION_BASE_URL);
    SharedPreferences preferenceHelper;

    @Inject
    public HostSelectionInterceptor(SharedPreferences preferenceHelper){
        this.preferenceHelper = preferenceHelper;
        setHostBaseUrl();
    }

    public void setHostBaseUrl() {
        if (preferenceHelper.getBoolean("status",false)) {
            this.host = HttpUrl.parse(Util.PRODUCTION_BASE_URL);
        } else {
            this.host = HttpUrl.parse(Util.DEVELOPMENT_BASE_URL);
        }
    }

    @NotNull
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (host != null) {
            HttpUrl newUrl = null;
            try {
                if (preferenceHelper.getBoolean("status",false)){
                    newUrl = request.url().newBuilder()
                            .scheme(host.scheme())
                            .host(host.url().toURI().getHost())
                            .port(host.url().toURI().getPort())
                            .build();
                }else {
                    newUrl = request.url().newBuilder()
                            .scheme(host.scheme())
                            .host(host.url().toURI().getHost())
                            .build();
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            assert newUrl != null;
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);

    }
}