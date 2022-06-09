package pt.tecnico.bicloin.app;

import java.util.Locale;

public class Coordinates {
    Double latitude;
    Double longitude;

    Coordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Coordinates(String latitude, String longitude) {
        this(Double.valueOf(latitude.replace(',', '.')), Double.valueOf(longitude.replace(',', '.')));
    }


    Coordinates(String coordinates) {
        this(coordinates.split(" ")[0], coordinates.split(" ")[1]);
    }

    public Double getLatitude() { return latitude;}
    public Double getLongitude() { return longitude;}

    public String getCoordinates() {
        return String.format(Locale.ROOT, "%.4f .%4f", getLatitude(), getLongitude());
    }

    public String getGoogleMapsRepr() {
        return String.format(Locale.ROOT, "https://www.google.com/maps/place/%.4f,%.4f",
                getLatitude(), getLongitude());
    }
}
