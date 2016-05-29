package pro.luxun.luxunanimation.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by wufeiyang on 16/5/7.
 */
public class RetrofitClient {

    public static final String URL_BASE = "http://0.luxun.pro:163/";
    public static final String URL_MAIN_JSON = "http://0.luxun.pro:12580/luxun.json";
    public static final String URL_TOPIC_JSON = "http://0.luxun.pro:163/?topics";
    public static final String URL_AUTH_JSON = "http://0.luxun.pro:163/?auth/";
    public static final String URL_COMMENT = "http://0.luxun.pro:163/?review/";
    public static final String URL_WEB_AUTH = "http://luxun.pro/#/auth/";
    public static final String URL_BANGUMI = "http://0.luxun.pro:163/?bangumi";
    public static final String URL_DM = "http://0.luxun.pro:163/?dm/";
    public static final String URL_LIKE = "http://0.luxun.pro:163/?like";
    public static final String URL_UPDATE = "";

    public static final String URL_SOURCE = "http://0.luxun.pro:12580/";
    public static final String URL_REFERER = "http://luxun.pro/";

    private static OkHttpClient sOkHttpClient;

    static {
        sOkHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor())
                .cookieJar(new CookieManager())
                .connectTimeout(30, TimeUnit.SECONDS).build();
    }

    public static ApiService getApiService(){
        return SingleApiService.INSTANCE;
    }

    public static OkHttpClient getOkHttpClient() {
        return sOkHttpClient;
    }

    private static class SingleApiService{
        private static final ApiService INSTANCE = new Retrofit.Builder().baseUrl(URL_BASE).addConverterFactory(FastJsonConverterFactory.create())
                .client(sOkHttpClient).addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(ApiService.class);
    }
}
