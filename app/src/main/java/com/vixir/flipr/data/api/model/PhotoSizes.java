package com.vixir.flipr.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoSizes implements Parcelable
{

    @SerializedName("sizes")
    @Expose
    private Sizes sizes;
    @SerializedName("stat")
    @Expose
    private String stat;
    public final static Parcelable.Creator<PhotoSizes> CREATOR = new Creator<PhotoSizes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PhotoSizes createFromParcel(Parcel in) {
            PhotoSizes instance = new PhotoSizes();
            instance.sizes = ((Sizes) in.readValue((Sizes.class.getClassLoader())));
            instance.stat = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PhotoSizes[] newArray(int size) {
            return (new PhotoSizes[size]);
        }

    }
            ;

    public Sizes getSizes() {
        return sizes;
    }

    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(sizes);
        dest.writeValue(stat);
    }

    public int describeContents() {
        return 0;
    }

}