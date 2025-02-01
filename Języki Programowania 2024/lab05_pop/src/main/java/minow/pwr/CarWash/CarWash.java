package minow.pwr.CarWash;

import minow.pwr.CarWash.PressureWash.FoamWash;
import minow.pwr.CarWash.PressureWash.WaterWash;
import org.javatuples.Pair;

import java.util.ArrayList;

public class CarWash {
    public ArrayList<WaterWash> waterWashes = new ArrayList<>();
    public ArrayList<FoamWash> foamWashes = new ArrayList<>();
    public ArrayList<Station> stations = new ArrayList<>();

    public void buildCarWashSeries(int size) {
        Pair<Integer, Segment>[] carWashSeries = new Pair[3 * size];
        int idCounter = 0;

        for (int i = 0; i < 3 * (size - 1); i += 3) {
            carWashSeries[i] = new Pair<>(idCounter, Segment.STATION);
            addStation(idCounter);

            carWashSeries[i + 1] = new Pair<>(idCounter, Segment.FOAM);
            addFoamWash(idCounter);

            carWashSeries[i + 2] = new Pair<>(idCounter, Segment.WATER);
            addWaterWash(idCounter);

            linkPressureWashesToStation(idCounter, idCounter);
            if (idCounter >= 1){
                linkPressureWashesToStation(idCounter, idCounter - 1);
            }
            idCounter++;
        }

        carWashSeries[3 * (size - 1)] = new Pair<>(idCounter, Segment.STATION);
        addStation(idCounter);
        linkPressureWashesToStation(idCounter, idCounter - 1);
        for (int i = 0; i < 3 * (size); i++) {
            System.out.println(carWashSeries[i]);
        }
        for (Station station : stations){
            System.out.println(station.getAvailablePressureWash().toString());
        }
    }

    private void linkPressureWashesToStation(int stationId, int pressureWashId) {
        Station station = getStationById(stationId);
        if (station != null) {
            station.getAvailablePressureWash().add(getFoamWashById(pressureWashId));
            station.getAvailablePressureWash().add(getWaterWashById(pressureWashId));
        }
    }

    private void addStation(int id) {
        stations.add(new Station(id));
    }

    private void addFoamWash(int id) {
        foamWashes.add(new FoamWash(id));
    }

    private void addWaterWash(int id) {
        waterWashes.add(new WaterWash(id));
    }

    private Station getStationById(int id) {
        for (Station station : stations){
            if (station.getId() == id){
                return station;
            }
        }
        return null;
    }

    private FoamWash getFoamWashById(int id) {
        for (FoamWash foamWash : foamWashes){
            if (foamWash.getId() == id){
                return foamWash;
            }
        }
        return null;
    }

    private WaterWash getWaterWashById(int id) {
        for (WaterWash waterWash : waterWashes){
            if (waterWash.getId() == id){
                return waterWash;
            }
        }
        return null;
    }

    public ArrayList<Station> getStations(){
        return this.stations;
    }
}
