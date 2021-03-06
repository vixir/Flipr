package com.vixir.flipr.data;

public interface DataLoadingSubject {

    void registerCallback(DataLoadingCallbacks callbacks);

    void unregisterCallback(DataLoadingCallbacks callbacks);

    interface DataLoadingCallbacks {
        void dataStartedLoading();

        void dataFinishedLoading();
    }
}
