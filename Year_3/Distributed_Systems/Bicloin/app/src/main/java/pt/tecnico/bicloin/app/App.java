package pt.tecnico.bicloin.app;

import pt.tecnico.bicloin.hub.grpc.Hub.*;
import pt.tecnico.bicloin.hub.HubFrontend;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class App{

    private HubFrontend hubFrontend;
    private String username;
    private String phone;
    private Coordinates coordinates;

    private HashMap<String, Coordinates> tags;

    public App(String zooHost, String zooPort, String hubPath, String username, String phone, String latitude, String longitude) throws ZKNamingException {
        ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
        ArrayList<ZKRecord> hubs = (ArrayList<ZKRecord>) zkNaming.listRecords(hubPath);
        if (hubs.size() == 0 ) {
            System.out.println("ERRO Could not connect to hub.");
            System.exit(1);
        }

        hubFrontend = new HubFrontend(hubs.get(0));
        this.username = username;
        this.phone = phone;
        this.coordinates = new Coordinates(latitude, longitude);
        tags = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }



    public Integer getBalance(){
        BalanceRequest request = BalanceRequest.newBuilder().setUsername(username).build();
        BalanceResponse response = hubFrontend.balance(request);
        return response.getBalance();
    }


    public void topUp(int amount) {
        TopUpRequest request = TopUpRequest.newBuilder().setUsername(username).setPhone(phone).setAmount(amount).build();
        hubFrontend.topUp(request);
    }

    public void bikeUp(String stationID) {
        BikeUpRequest request = BikeUpRequest.newBuilder().setUsername(username).setStationID(stationID).
                setLatitude(getLatitude()).setLongitude(getLongitude()).build();
        hubFrontend.bikeUp(request);
    }

    public void bikeDown(String stationID) {
        BikeDownRequest request = BikeDownRequest.newBuilder().setUsername(username).setStationID(stationID).
                setLatitude(getLatitude()).setLongitude(getLongitude()).build();
        hubFrontend.bikeDown(request);
    }

    public Coordinates getTagCoordinates(String tagName) {
        return tags.get(tagName);
    }

    public void setTag(String tagName, Coordinates coordinates) {
        tags.put(tagName, coordinates);
    }


    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Boolean tagExists(String tagName) {
        return tags.containsKey(tagName);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Double getLatitude() { return getCoordinates().getLatitude();}
    public Double getLongitude() { return getCoordinates().getLongitude();}

    public String infoStation(String stationID) {
        InfoStationRequest request = InfoStationRequest.newBuilder().setStationID(stationID).build();
        InfoStationResponse r = hubFrontend.infoStation(request);

        return String.format(Locale.ROOT, "%s, lat %.6f, %.6f long, %d docas, %d BIC prémio, %d bicicletas, %d levantamentos, %d devoluções, https://www.google.com/maps/place/%.6f,%.6f%n",
                r.getName(), r.getLatitude(), r.getLongitude(), r.getCapacity(),
                r.getPrize(), r.getNumOfAvailableBikes(), r.getWithdrawals(), r.getDeposits(), r.getLatitude(), r.getLongitude());
    }

    public List<String> locateStation(Double latitude, Double longitude, Integer k) {
        LocateStationRequest request = LocateStationRequest.newBuilder().setLatitude(latitude).setLongitude(longitude)
                .setNumOfStations(k).build();
        LocateStationResponse response = hubFrontend.locateStation(request);
        List<String> stationIDs = response.getStationIDsList();

        List<String> result = new ArrayList<>();
        for (String stationID: stationIDs) {
            InfoStationResponse info =
                    hubFrontend.infoStation(InfoStationRequest.newBuilder().setStationID(stationID).build());
            result.add(String.format("%s, lat %.4f, %.4f long, %d docas, %d BIC prémio, %d bicicletas, a %.0f metros",
                    stationID, info.getLatitude(), info.getLongitude(), info.getCapacity(), info.getPrize(),
                    info.getNumOfAvailableBikes(), distanceBetween(info.getLatitude(), info.getLongitude(),
                            getLatitude(), getLongitude())));
        }
        return result;

    }

    public String ping() {
        return hubFrontend.ping(PingRequest.newBuilder().build()).getOutputText();
    }


    public String sysStatus() {
        StringBuilder str = new StringBuilder();
        SysStatusResponse response = hubFrontend.sysStatus(SysStatusRequest.newBuilder().build());
        for (SysStatusResponse.Result result : response.getResultsList()){
            if (result.getIsUp())
                str.append(result.getPath()+" is up.");
            else
                str.append(result.getPath()+" is down.");

            str.append(System.lineSeparator());
        }
        return str.toString();

    }



    // https://github.com/jasonwinn/haversine/blob/76315fe5caedc8005b9366cbd5b619e33cf77331/Haversine.java
    // except this returns value in meters
    public static double distanceBetween(double startLat, double startLong, double endLat, double endLong){
        int EARTH_RADIUS = 6371; // Approx Earth radius in KM
        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin_aux(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin_aux(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 1000 * EARTH_RADIUS * c; // <-- d
    }

    private static double haversin_aux(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }


    public static double distanceBetween(Coordinates here, Coordinates there) {
        return distanceBetween(here.getLatitude(), here.getLongitude(), there.getLatitude(), there.getLongitude());
    }


}
