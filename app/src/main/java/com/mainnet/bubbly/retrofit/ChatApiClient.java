package com.mainnet.bubbly.retrofit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.mainnet.bubbly.LL_Login;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.model.AccessAndRefreshToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ChatApiClient {
    private static Retrofit retrofit;
    private static Retrofit retrofit2;

    /*public static Retrofit getApiClient()
    {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        // 모든 요청에 헤더 붙이는 인터셉터 적용!!
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws
                    IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + AccessAndRefreshToken.accessToken)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient okHttpClient = clientBuilder.build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.chat_server_addr)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }*/

    public static Retrofit getApiClient(Context context)
    {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        // 모든 요청에 헤더 붙이는 인터셉터 적용!!
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws
                    IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + AccessAndRefreshToken.accessToken)
                        .method(original.method(), original.body())
                        .build();

                Log.d("222", "" + "22");

                // 요청 결과
                Response response = chain.proceed(request);

                Log.d("response,code()", "" + response.code());

                // 액세스 토큰 만료
                if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    ApiInterface api = ApiClient.getApiClient(context).create(ApiInterface.class);
                    Call<String> call = api.reIssueAccessToken("Bearer " + AccessAndRefreshToken.refreshToken);

                    // 액세스토큰 재발급
                    SyncReIssueAccessToken t = new SyncReIssueAccessToken(call,context);

                    // 스레드 시작
                    t.start();

                    try {
                        // 쓰레드에서 데이터를 조회할 때 main 스레드는 중지를 시켜야 하므로 join()사용
                        t.join();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int statusCode = t.getStatusCode();

                    // 리프레시 토큰 만료
                    if(statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(context, "리프레시 토큰이 만료됐습니다.. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                        Intent intent = new Intent(context, LL_Login.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        // 토큰이 없음
                    } else if(statusCode == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                        // 리프레시 토큰 없음(db에) => 로그인화면으로 이동
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(context, "리프레시 토큰이 없습니다.. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                        Intent intent = new Intent(context, LL_Login.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }

                    // 새로운 리퀘스트 생성!
                    Request newRequest = original.newBuilder()
                            .header("Authorization", "Bearer " + AccessAndRefreshToken.accessToken)
                            .method(original.method(), original.body())
                            .build();

                    // 새로운 요청 서버에게 전송
                    return chain.proceed(newRequest);
                } else if(response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                    // 액세스 토큰 없음 => 로그인화면으로 이동
                    // 잘못된 액세스 토큰 => 로그인화면으로 이동
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(context, "액세스 토큰이 없거나 잘못됐습니다.. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                    Intent intent = new Intent(context, LL_Login.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }

                // 올바른 토큰을 포함한 요청이라면 요청 처리 결과를 반환한다.
                return response;

            }
        });

        OkHttpClient okHttpClient = clientBuilder.connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit2 == null)
        {
            retrofit2 = new Retrofit.Builder()
                    .baseUrl(Config.chat_server_addr)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit2;
    }
}
