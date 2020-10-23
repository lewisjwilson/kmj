package com.lewiswilson.kiminojisho.JSON;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JishoAPI {

    String BASE_URL = "https://jisho.org/api/v1/search/";

    /**
     * The return type is important here
     * The class structure that you've defined in Call<T>
     * should exactly match with your json response
     * If you are not using another api, and using the same as mine
     * then no need to worry, but if you have your own API, make sure
     * you change the return type appropriately
     **/

    @GET("words?keyword=\"japan\"")
    Call<JishoData> getData();

}
