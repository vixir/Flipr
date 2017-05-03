package com.vixir.flipr.data.api.rest;

import com.vixir.flipr.data.api.model.PhotoPage;
import com.vixir.flipr.data.api.model.PhotoSizes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    // TODO search for decent photographs group.
    @GET("rest/?method=flickr.photos.search&format=json&nojsoncallback=1&group_id=2297498%40N20&per_page=20")
    Call<PhotoPage> getPhotosFeed(@Query("api_key") String apiKey, @Query("tags") String tags, @Query("page") String count);

    @GET("/services/rest/?method=flickr.photos.getSizes&format=json&nojsoncallback=1")
    Call<PhotoSizes> getPhotoUrl(@Query("api_key") String apiKey, @Query("photo_id") String photoId);
}
