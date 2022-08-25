package com.example.bubbly.retrofit;

import com.example.bubbly.kim_util_test.Kim_JoinedCom_Response;
import com.example.bubbly.model.NFTSell_Item;
import com.example.bubbly.model.NFT_Item;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterfaceTransactionHistory {


    // todo 블록체인에서 거래기록 가져오기
//    @Headers("x-api-key:4LS0jVPkU61EBPpW2Ml3A2iaEcEfXK92aCDSzXXr")
    @GET("v2/accounts/{address}/transactions")
    Call<String> transactionHistory(@Header("x-api-key") String token,
            @Path(value = "address", encoded = true) String address,
            @Query("limit") int limit, @Query("next") String nextToken);
}
