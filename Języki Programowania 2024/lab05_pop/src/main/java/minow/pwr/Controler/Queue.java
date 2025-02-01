package minow.pwr.Controler;

import minow.pwr.Car.Car;

import java.util.ArrayList;

public class Queue {
    private final int id;
    public ArrayList<Car> order;

    public Queue(int id) {
        this.id = id;
        this.order = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Car> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<Car> order) {
        this.order = order;
    }
}
