package minow.pwr.Car;

import minow.pwr.CarWash.PressureWash.PressureWash;
import minow.pwr.CarWash.PressureWash.WashType;
import minow.pwr.CarWash.Station;
import minow.pwr.Controler.Queue;

import java.util.ArrayList;

public class Car extends Thread {
    private final char id;
    private Status status;
    private boolean inQueue = false;
    private ArrayList<Queue> queues;
    private boolean isAssigned = false;
    private Station currentStation = null;

    public Car(char id, ArrayList<Queue> queues) {
        this.id = id;
        this.status = Status.DIRTY;
        this.queues = queues;
    }

    public synchronized void getInQueue(ArrayList<Queue> queues) {
        int smallestQueueId = getSmallestQueueId(queues);
        for (Queue queue : queues) {
            if (queue.getId() == smallestQueueId) {
                    queue.getOrder().addLast(this);
                    inQueue = true;
                    System.out.println("Car: " + this.getCarId() + " has entered the queue: " + queue.getId());
            }
        }
    }

    public void getInCarWash(Station station, Queue currentQueue) {
        if (!isAssigned) {
            currentStation = station;
            this.isAssigned = true;
//            System.out.println(currentStation);
            currentStation.setCarId(this.id);
            currentStation.setEmpty(false);
            this.inQueue = false;
            currentQueue.getOrder().remove(this);
            System.out.println("Car: " + this.getCarId() + " was assigned to: " + currentStation.getId());
        }
    }
    public void getOutOfCarWash(Station currentStation) throws InterruptedException {
        currentStation.setEmpty(true);
        currentStation.setCarId('-');
        this.isAssigned = false;
        System.out.println("Car: " + this.getCarId() + " finished cleaning in station: " + currentStation.getId());
        currentStation = null;
//        System.out.println("Car: " + this.getCarId() + " current status " + this.getStatus() + " is in queue? " + this.inQueue + " is assigned? " + this.isAssigned);
    }

    public void startCleaning(Station currentStation) throws InterruptedException {
        while (this.status != Status.WASHED) {
            cleanWithType(currentStation, WashType.Water, Status.PREWASHED);
            cleanWithType(currentStation, WashType.Foam, Status.FOAMED);
            cleanWithType(currentStation, WashType.Water, Status.WASHED);
        }
    }

    private synchronized void cleanWithType(Station station, WashType type, Status nextStatus) throws InterruptedException {
        while (this.status != nextStatus) {
            for (PressureWash wash : station.getAvailablePressureWash()) {
                if (wash.getWashType() == type && !wash.isOccupied() && wash.getWashType() != null) {
                    wash.setOccupied(true);
                    wash.setCurrentTarget(this);
                    Thread.sleep(2000);
                    wash.setOccupied(false);
                    this.status = nextStatus;
                }
            }
        }
    }

    private int getSmallestQueueId(ArrayList<Queue> queues) {
        int smallestQueueId = -1;
        int currentSize = Integer.MAX_VALUE;
        for (Queue q : queues) {
            if (q.order.size() < currentSize) {
                currentSize = q.order.size();
                smallestQueueId = q.getId();
            }
        }
        return smallestQueueId;
    }

    @Override
    public void run() {
            do {
                if (status == Status.WASHED && !inQueue && !isAssigned) {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.setStatus(Status.DIRTY);
//                    System.out.println("getting dirty");
                }
                if (status == Status.DIRTY && !inQueue && !isAssigned) {
//                    System.out.println("getting in queue");
                    getInQueue(queues);
                }
                if (status == Status.DIRTY && !inQueue && isAssigned){
                    try {
                        startCleaning(currentStation);
                        getOutOfCarWash(currentStation);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
    }

    public char getCarId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isAssigned() {
        return this.isAssigned;
    }
}
