package com.vixir.flipr.data.api.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sizes implements Parcelable
{

    @SerializedName("canblog")
    @Expose
    private Integer canblog;
    @SerializedName("canprint")
    @Expose
    private Integer canprint;
    @SerializedName("candownload")
    @Expose
    private Integer candownload;
    @SerializedName("size")
    @Expose
    private List<Size> size = null;
    public final static Parcelable.Creator<Sizes> CREATOR = new Creator<Sizes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Sizes createFromParcel(Parcel in) {
            Sizes instance = new Sizes();
            instance.canblog = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.canprint = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.candownload = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.size, (com.vixir.flipr.data.api.model.Size.class.getClassLoader()));
            return instance;
        }

        public Sizes[] newArray(int size) {
            return (new Sizes[size]);
        }

    }
            ;

    public Integer getCanblog() {
        return canblog;
    }

    public void setCanblog(Integer canblog) {
        this.canblog = canblog;
    }

    public Integer getCanprint() {
        return canprint;
    }

    public void setCanprint(Integer canprint) {
        this.canprint = canprint;
    }

    public Integer getCandownload() {
        return candownload;
    }

    public void setCandownload(Integer candownload) {
        this.candownload = candownload;
    }

    public List<Size> getSize() {
        return size;
    }

    public void setSize(List<Size> size) {
        this.size = size;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(canblog);
        dest.writeValue(canprint);
        dest.writeValue(candownload);
        dest.writeList(size);
    }

    public int describeContents() {
        return 0;
    }

}