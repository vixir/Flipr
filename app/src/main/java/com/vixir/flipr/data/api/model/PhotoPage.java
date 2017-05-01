package com.vixir.flipr.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoPage implements Parcelable
{

    @SerializedName("photos")
    @Expose
    private Photos photos;
    @SerializedName("stat")
    @Expose
    private String stat;
    public final static Parcelable.Creator<PhotoPage> CREATOR = new Creator<PhotoPage>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PhotoPage createFromParcel(Parcel in) {
            PhotoPage instance = new PhotoPage();
            instance.photos = ((Photos) in.readValue((Photos.class.getClassLoader())));
            instance.stat = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PhotoPage[] newArray(int size) {
            return (new PhotoPage[size]);
        }

    }
            ;

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(photos);
        dest.writeValue(stat);
    }

    public int describeContents() {
        return 0;
    }

}