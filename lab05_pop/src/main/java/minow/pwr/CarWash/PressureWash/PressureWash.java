package minow.pwr.CarWash.PressureWash;

import minow.pwr.Car.Car;

public abstract class PressureWash {
    int id;
    boolean isOccupied = false;
    Car currentTarget;
    WashType washType;

    public PressureWash (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WashType getWashType() {
        return washType;
    }

    public void setWashType(WashType washType) {
        this.washType = washType;
    }

    public Car getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Car currentTarget) {
        this.currentTarget = currentTarget;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    @Override
    public String toString() {
        return "PressureWash{" +
                "id=" + id +
                ", isOccupied=" + isOccupied +
                ", washType=" + washType +
                '}';
    }
}
