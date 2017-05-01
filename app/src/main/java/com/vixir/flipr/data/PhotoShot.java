package com.vixir.flipr.data;

public class PhotoShot {
    public final long id;
    public String title;
    public String description;
    public String size;
    public String image;


    public PhotoShot(long id, String title, String description, String size, String url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.size = size;
        this.image = url;
    }
}
