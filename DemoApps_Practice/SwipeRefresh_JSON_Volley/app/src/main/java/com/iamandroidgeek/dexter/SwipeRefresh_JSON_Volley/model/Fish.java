package com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.model;

/**
 * Created by dexter on 3/10/2018.
 */

public class Fish {

    private String name;
    private String image_url;
    public  Fish(String name, String image_url){
        this.name=name;
        this.image_url=image_url;
    }

    public  String getImageUrl(){
        return image_url;
    }
    public  String getName(){
        return name;
    }

}
