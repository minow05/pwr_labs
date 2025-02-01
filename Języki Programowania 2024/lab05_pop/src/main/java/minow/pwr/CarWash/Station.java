package minow.pwr.CarWash;

import minow.pwr.CarWash.PressureWash.PressureWash;

import java.util.ArrayList;

public class Station {
    private final int id;
    private boolean isEmpty;
    private char carId;
    private final ArrayList<PressureWash> availablePressureWash = new ArrayList<>();

    public Station(int id) {
        this.id = id;
        this.isEmpty = true;
        this.carId = '-';
    }

    public int getId() {
        return id;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public char getCarId() {
        return carId;
    }

    public void setCarId(char carId) {
        this.carId = carId;
    }

    public ArrayList<PressureWash> getAvailablePressureWash() {
        return availablePressureWash;
    }
}