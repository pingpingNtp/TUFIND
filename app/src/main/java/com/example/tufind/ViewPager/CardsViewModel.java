package com.example.tufind.ViewPager;

public class CardsViewModel {
    String image;
    String title;
    String detail;
    double latitud, longitud;
    String distance;

    public CardsViewModel(){

    }
    public String getImage(){
        return this.image;
    }
    public String getTitle(){
        return this.title;
    }
    public String getDetail(){return this.detail;}
    public void setDetail(String detail){
        this.detail = detail;
    }
    public void setImage(String image){
        this.image = image;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public double getLatitud(){
        return this.latitud;
    }
    public double getLongitud(){
        return this.longitud;
    }
    public void setLatitud(double image){
        this.latitud = latitud;
    }
    public void setLongitud(double title){
        this.longitud = longitud;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }
}
