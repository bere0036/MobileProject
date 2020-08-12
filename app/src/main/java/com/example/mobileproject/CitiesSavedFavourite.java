package com.example.mobileproject;

class CitiesSavedFavourite {

    private long id;
    private String name;
    private String country;
    private String region;
    private String currency;
    private String latitude;
    private String longitude;

    public CitiesSavedFavourite(String name, String country, String region, String currency, String latitude, String longitude) {
        this.name = name;
        this.country = country;
        this.region = region;
        this.currency = currency;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CitiesSavedFavourite(long id, String name, String country, String region, String currency, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.region = region;
        this.currency = currency;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
