package com.example.mobileproject;

public class CitiesSavedFavourite {

    public class SavedFavourite {
        private long id;
        private String latitude;
        private String longitude;
        private String country;
        private String region;
        private String city;
        private String currency;



        SavedFavourite(String latitude, String longitude, String country, String region, String city, String currency, long id) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.country = country;
            this.region = region;
            this.city = city;
            this.currency = currency;
            this.id = id;
        }

        public long getId() { return id; }

        public String getLatitude() { return latitude ; }

        public String getLongitude() { return longitude; }

        public String getCountry() { return country ; }

        public String getRegion() { return region ; }

        public String getCity() { return city ; }

        public String getCurrency() { return currency ; }



    }

}
