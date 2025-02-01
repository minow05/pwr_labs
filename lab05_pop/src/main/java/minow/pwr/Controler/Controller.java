package minow.pwr.Controler;

import minow.pwr.Car.Car;
import minow.pwr.CarWash.CarWash;
import minow.pwr.CarWash.Station;

import java.util.ArrayList;

public class Controller extends Thread {
    /*
    build carWash
    build queues
    assign cars to carwash stations
    */
    private CarWash carWash;
    private ArrayList<Car> cars;
    private ArrayList<Queue> queues;

//    public Controller(int carWashSize, int amountOfCars, ArrayList<Queue> queues) {
//        carWash.buildCarWashSeries(carWashSize);
//        this.queues = queues;
//
//        for (int i = 0; i < amountOfCars; i++) {
//            cars.add(new Car((char) ('A' + i), queues));
//        }
//    }
    public Controller (CarWash carWash, ArrayList<Queue> queues, ArrayList<Car> cars) {
        this.carWash = carWash;
        this.queues = queues;
        this.cars = cars;
    }

    public void run(){
        while (true){
            for (Queue queue : queues) {
                try {
                    if (!queue.getOrder().isEmpty()){
                         assignCarToStation(queue.getOrder().getLast(), freeStation(carWash.getStations()), getCarCurrentQueue(queue.getOrder().getLast()));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                System.out.println(queue.getOrder());
            }
        }
    }

    private void assignCarToStation (Car car, Station station, Queue carCurrentQueue) throws InterruptedException {
//        System.out.println(station.getAvailablePressureWash().toString());
        car.getInCarWash(station, carCurrentQueue);
    }

    private Queue getCarCurrentQueue(Car car){
        for (Queue queue : queues) {
            if (queue.getOrder().contains(car)) return queue;
        }
        return null;
    }

    private Station freeStation (ArrayList<Station> stations) {
        while (true) {
            for (Station station : stations) {
                if (station.isEmpty()) {
                    return station;
                }
            }
        }
    }
}

