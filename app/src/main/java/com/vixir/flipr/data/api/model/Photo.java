package com.vixir.flipr.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("farm")
    @Expose
    private Integer farm;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ispublic")
    @Expose
    private Integer ispublic;
    @SerializedName("isfriend")
    @Expose
    private Integer isfriend;
    @SerializedName("isfamily")
    @Expose
    private Integer isfamily;
    public final static Parcelable.Creator<Photo> CREATOR = new Creator<Photo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Photo createFromParcel(Parcel in) {
            Photo instance = new Photo();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.owner = ((String) in.readValue((String.class.getClassLoader())));
            instance.secret = ((String) in.readValue((String.class.getClassLoader())));
            instance.server = ((String) in.readValue((String.class.getClassLoader())));
            instance.farm = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.ispublic = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.isfriend = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.isfamily = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Photo[] newArray(int size) {
            return (new Photo[size]);
        }

    }
            ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getFarm() {
        return farm;
    }

    public void setFarm(Integer farm) {
        this.farm = farm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIspublic() {
        return ispublic;
    }

    public void setIspublic(Integer ispublic) {
        this.ispublic = ispublic;
    }

    public Integer getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(Integer isfriend) {
        this.isfriend = isfriend;
    }

    public Integer getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(Integer isfamily) {
        this.isfamily = isfamily;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(owner);
        dest.writeValue(secret);
        dest.writeValue(server);
        dest.writeValue(farm);
        dest.writeValue(title);
        dest.writeValue(ispublic);
        dest.writeValue(isfriend);
        dest.writeValue(isfamily);
    }

    public int describeContents() {
        return 0;
    }

}