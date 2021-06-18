package com.example.tufind;

public class defineDatabase {
    String ImageUri;
    String location;

    public defineDatabase(String ImageUrl, String location){
        this.ImageUri = ImageUrl;
        this.location = location;
    }

    public void setImageUri(String imageUri) {
        this.ImageUri = imageUri;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public String getLocation() {
        return location;
    }
}
