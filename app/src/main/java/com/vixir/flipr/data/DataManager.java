package com.vixir.flipr.data;

import android.content.Context;
import android.util.Log;

import com.vixir.flipr.BuildConfig;
import com.vixir.flipr.data.api.model.Photo;
import com.vixir.flipr.data.api.model.PhotoPage;
import com.vixir.flipr.data.api.model.PhotoSizes;
import com.vixir.flipr.data.api.model.Photos;
import com.vixir.flipr.data.api.model.Size;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class DataManager extends BaseDataManager<List<? extends PhotoShot>> {


    public DataManager(Context context) {
        super(context);
    }

    public void loadDataSource(int page) {
        loadStarted();
    /*
            REMEMBER THIS IN CAPITAL LETTERS
        1. First, clear the array of data
            listOfItems.clear();
        2. Notify the adapter of the update
            recyclerAdapterOfItems.notifyDataSetChanged(); // or notifyItemRangeRemoved
        3. Reset endless scroll listener when performing a new search
            scrollListener.resetState();
     */
        final Call<PhotoPage> topPics = getFliprApi().getPhotosFeed(BuildConfig.FLICKR_API_KEY, BuildConfig.DEFAULT_PIC_TAG, page + "");
        topPics.enqueue(new Callback<PhotoPage>() {
            @Override
            public void onResponse(Call<PhotoPage> call, Response<PhotoPage> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStat().equals("fail")) {
                        loadFailed("response not successful");
                    } else {
                        loadPhotos(response.body());
                    }
                } else {
                    loadFailed("response not successful");
                }
            }

            @Override
            public void onFailure(Call<PhotoPage> call, Throwable t) {
                loadFailed(t.getMessage());
            }
        });
    }


    private void loadPhotos(PhotoPage body) {
        Photos photos = body.getPhotos();
        final List<Photo> photoList = photos.getPhoto();
        final List<PhotoShot> photoShotList = new ArrayList<>();
        for (final Photo photo : photoList) {
            final Call<PhotoSizes> photoSizesCall = getFliprApi().getPhotoUrl(BuildConfig.FLICKR_API_KEY, photo.getId());
            photoSizesCall.enqueue(new Callback<PhotoSizes>() {
                @Override
                public void onResponse(Call<PhotoSizes> call, Response<PhotoSizes> response) {
                    if (response.isSuccessful()) {
                        List<Size> imageUrlSizes = response.body().getSizes().getSize();
                        Size currentSize = new Size();
                        for (Size size : imageUrlSizes) {
                            if (size.getLabel().equals("Small")) {
                                currentSize = size;
                            }
                        }
                        if (currentSize == null) {
                            //in case medium size image is not there
                            currentSize = imageUrlSizes.get(0);
                        }
                        String sizeData = currentSize.getHeight() + "x" + currentSize.getHeight();
                        String description = photo.getTitle();
                        PhotoShot photoShot = new PhotoShot(System.currentTimeMillis(), currentSize.getLabel(), description, sizeData, currentSize.getSource());
                        photoShotList.add(photoShot);
                        //last piece of information loaded.
                        if (photo.equals(photoList.get(photoList.size() - 1))) {
                            sourceLoaded(photoShotList);
                        }
                    } else {
                        loadFailed("response not successful");
                    }
                }

                @Override
                public void onFailure(Call<PhotoSizes> call, Throwable t) {
                    loadFailed(t.getMessage());
                }
            });
        }
    }

    private void sourceLoaded(List<? extends PhotoShot> body) {
        loadFinished();
        onDataLoaded(body);
    }

    private void loadFailed(String key) {
        loadFinished();
    }


}
