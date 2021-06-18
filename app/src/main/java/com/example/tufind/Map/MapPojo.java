package com.example.tufind.Map;

public class MapPojo {
    private double latitud;
    private double longitud;
    private String locationname;

    public MapPojo(){

    }
    public double getLatitud(){
        return  latitud;
    }
    public void setLatitud(double latitud){
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getLocationname(){
        return locationname;
    }
    public void setLocationname(String locationname){
        this.locationname = locationname;
    }
}
