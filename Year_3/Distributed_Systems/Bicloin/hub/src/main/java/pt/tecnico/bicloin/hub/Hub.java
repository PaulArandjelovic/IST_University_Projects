package pt.tecnico.bicloin.hub;

import java.security.AccessControlException;
import java.util.*;

import com.google.protobuf.InvalidProtocolBufferException;
import pt.tecnico.rec.QuorumFrontend;

public class Hub {
    HashMap<String, User> users;
    HashMap<String, Station> stations;
    final QuorumFrontend recs;
    final String path;
    final boolean initRec;

    public Hub(String path, QuorumFrontend recs, boolean initRec){
        users = new HashMap<>();
        stations = new HashMap<>();
        this.recs = recs;
        this.path = path;
        this.initRec = initRec;
    }

    public String getPath(){
        return this.path;
    }

    public Map<String, Boolean> pingRecs(){
        return recs.ping();
    }

    public User getUser(String username) throws IllegalArgumentException {
        User user = users.get(username);
        if (user == null){
            throw new IllegalArgumentException("User does not exist");
        }
        return user;
    }

    public Station getStation(String stationID) throws IllegalArgumentException {
        Station station = stations.get(stationID);
        if (station == null){
            throw new IllegalArgumentException("Station does not exist");
        }
        return station;
    }

    public void newUser(String username, String name, String phone) {
        User newUser = new User(username, name, phone);
        users.put(username, newUser);
        if (initRec){
            newUser.setBalance(0, recs);
            newUser.setHasBike(false, recs);
        }
    }

    public void newStation(String[] line){
        Station newStation = new Station(line[0], line[1], Double.parseDouble(line[2]), Double.parseDouble(line[3]),
                Integer.parseInt(line[4]), Integer.parseInt(line[6]));
        stations.put(line[1], newStation);
        if (initRec) {
            newStation.setNumBikes(Integer.parseInt(line[5]), recs);
            newStation.setNumWithdrawals(0, recs);
            newStation.setNumDeposits(0, recs);

        }
    }

    public Integer balance(String username) throws IllegalArgumentException, InvalidProtocolBufferException {
        return getUser(username).getBalance(recs);
    }

    public Integer topUp(String username, Integer amount, String phone)
            throws IllegalArgumentException, InvalidProtocolBufferException {
        User user =  getUser(username);
        if (amount < 10 || amount > 200 || amount % 10 != 0){
            throw new IllegalArgumentException("Invalid top-up value");
        } else if (!user.getPhone().equals(phone)){
            throw new IllegalArgumentException("Phone number does not match username");
        }
        int newBalance;
        newBalance = user.topUp(amount, recs);
        return newBalance;
    }


    public void bikeUp(String username, String stationID, Double latitude, Double longitude)
            throws IllegalArgumentException, AccessControlException, InvalidProtocolBufferException {
        User user = getUser(username);
        Station station = getStation(stationID);

        synchronized (user) {
            synchronized (station) {
                Integer balance = user.getBalance(recs);
                Integer bikes = station.getNumBikes(recs);

                if (distanceTo(latitude, longitude, station) > 200) {
                    throw new IllegalArgumentException("Too far away");
                } else if (user.hasBike(recs)) {
                    throw new AccessControlException("Already using a bike");
                } else if (balance < 10) {
                    throw new AccessControlException("Not enough balance");
                } else if (bikes < 1) {
                    throw new AccessControlException("No bikes");
                }

                station.setNumBikes(bikes - 1, recs);
                station.incNumWithdrawals(recs);
                user.setBalance(balance - 10, recs);
                user.setHasBike(true, recs);
            }
        }
    }

    public void bikeDown(String username, String stationID, Double latitude, Double longitude)
            throws IllegalArgumentException, AccessControlException, InvalidProtocolBufferException {
        User user = getUser(username);
        Station station = getStation(stationID);

        synchronized (user) {
            synchronized (station) {
                Integer balance = user.getBalance(recs);
                Integer bikes = station.getNumBikes(recs);

                if (distanceTo(latitude, longitude, station) > 200) {
                    throw new IllegalArgumentException("Too far away");
                } else if (!user.hasBike(recs)) {
                    throw new AccessControlException("Not using a bike");
                } else if (bikes + 1 >= station.getNumDocks()) {
                    throw new AccessControlException("No docks");
                }

                station.setNumBikes(bikes + 1, recs);
                station.incNumDeposits(recs);
                user.setBalance(balance + station.getPrize(), recs);
                user.setHasBike(false, recs);
            }
        }
    }


    public double distanceTo(Double latitude, Double longitude, Station station) {
        return distanceBetween(latitude, longitude, station.getLatitude(), station.getLongitude());
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

    public Integer getNumBikes(String stationID) throws InvalidProtocolBufferException {
        return getStation(stationID).getNumBikes(recs);
    }

    public Integer getNumWithdrawals(String stationID) throws  InvalidProtocolBufferException {
        return getStation(stationID).getNumWithdrawals(recs);
    }

    public Integer getNumDeposits(String stationID) throws  InvalidProtocolBufferException {
        return getStation(stationID).getNumDeposits(recs);
    }

    public List<String> getKClosestStations(Double latitude, Double longitude, Integer k) {
        if (k > stations.size())
            throw new IllegalArgumentException("There are not enough stations");
        var stationIDs = new ArrayList<String>(stations.keySet());

        Collections.sort(stationIDs, Comparator.comparingDouble(s -> distanceBetween(getStation(s).getLatitude(), getStation(s).getLongitude(),
                latitude, longitude)));


        return stationIDs.subList(0, k);

    }

    @Override
    public String toString() {
        return "Hub{" +
                "users=" + users +
                ", stations=" + stations +
                '}';
    }
}
