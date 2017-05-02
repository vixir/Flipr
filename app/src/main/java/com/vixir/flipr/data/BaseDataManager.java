package com.vixir.flipr.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.vixir.flipr.data.api.client.ApiClient;
import com.vixir.flipr.data.api.rest.ApiInterface;


public abstract class BaseDataManager<T> implements DataLoadingSubject {

    private ApiInterface fliprApiInterface;
    private DataLoadingCallbacks mLoadingCallBack;

    public BaseDataManager(@NonNull Context context) {
    }

    public abstract void onDataLoaded(T data);

    @Override
    public void registerCallback(DataLoadingCallbacks callbacks) {
        mLoadingCallBack = callbacks;
    }

    protected void loadStarted() {
        dispatchLoadingStartedCallbacks();
    }

    protected void loadFinished() {
        dispatchLoadingFinishedCallbacks();
    }

    @Override
    public void unregisterCallback(DataLoadingCallbacks callbacks) {
        mLoadingCallBack = null;
    }

    protected void dispatchLoadingStartedCallbacks() {
        if (mLoadingCallBack == null) return;
        mLoadingCallBack.dataStartedLoading();
    }

    protected void dispatchLoadingFinishedCallbacks() {
        if (mLoadingCallBack == null) return;
        mLoadingCallBack.dataFinishedLoading();
    }

    public ApiInterface getFliprApi() {
        if (fliprApiInterface == null) createFliprAPI();
        return fliprApiInterface;
    }


    private void createFliprAPI() {
        final Gson gson = new Gson();
        fliprApiInterface = ApiClient.getClient().create(ApiInterface.class);
    }
}
