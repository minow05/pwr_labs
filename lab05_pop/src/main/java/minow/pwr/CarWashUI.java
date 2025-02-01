//package minow.pwr;
//
//import minow.pwr.Car.Status;
//import minow.pwr.CarWash.CarWash;
//import minow.pwr.Controler.Controller;
//import minow.pwr.Controler.Queue;
//import minow.pwr.Car.Car;
//import minow.pwr.CarWash.Station;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CarWashUI extends JFrame {
//    public static void main(String[] args) {
//        ArrayList<Queue> queues = new ArrayList<>();
//        queues.add(new Queue(0));
//        queues.add(new Queue(1));
//        ArrayList<Car> cars = new ArrayList<>();
//        cars.add(new Car('A', queues));
//        cars.add(new Car('B', queues));
//        cars.add(new Car('C', queues));
//        CarWash carWash = new CarWash();
//        carWash.buildCarWashSeries(3);
//        ArrayList<Controller> controllers = new ArrayList<>();
//        controllers.add(new Controller(carWash, queues, cars));
//        controllers.add(new Controller(carWash, queues, cars));
//        for (Car car : cars) {
//            car.start();
//        }
//        for (Controller controller : controllers) {
//            controller.start();
//        }
//    }
//}
package minow.pwr;

import minow.pwr.Car.Status;
import minow.pwr.CarWash.CarWash;
import minow.pwr.Controler.Controller;
import minow.pwr.Controler.Queue;
import minow.pwr.Car.Car;
import minow.pwr.CarWash.Station;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CarWashUI extends JFrame {
    private JTextArea leftPanel;
    private JTextArea middlePanel;
    private JTextArea rightPanel;
    private JTextArea bottomPanel;

    public CarWashUI(ArrayList<Car> cars, ArrayList<Controller> controllers, ArrayList<Queue> queues, CarWash carWash) {
        setTitle("Car Wash System");
        setSize(1000, 600);
        setLayout(new GridLayout(2, 2));

        // Left Panel: Free Vehicles
        leftPanel = new JTextArea();
        leftPanel.setEditable(false);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Free Vehicles"));

        // Middle Panel: Controllers
        middlePanel = new JTextArea();
        middlePanel.setEditable(false);
        middlePanel.setBorder(BorderFactory.createTitledBorder("Controllers"));

        // Right Panel: Car Wash Stations
        rightPanel = new JTextArea();
        rightPanel.setEditable(false);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Car Wash Stations"));

        // Bottom Panel: Queues
        bottomPanel = new JTextArea();
        bottomPanel.setEditable(false);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Queues"));

        add(leftPanel);
        add(middlePanel);
        add(rightPanel);
        add(bottomPanel);

        // Create a thread to refresh UI
        Thread uiUpdater = new Thread(() -> {
            while (true) {
                updateUI(cars, controllers, queues, carWash);
                try {
                    Thread.sleep(100); // Update every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        uiUpdater.start();
    }

    private void updateUI(ArrayList<Car> cars, ArrayList<Controller> controllers, ArrayList<Queue> queues, CarWash carWash) {
        // Update free vehicles
        StringBuilder freeVehicles = new StringBuilder();
        for (Car car : cars) {
            if (car.getStatus() == Status.DIRTY && !car.isAssigned()) {
                freeVehicles.append(car.getCarId()).append("\n");
            }
        }
        leftPanel.setText(freeVehicles.toString());

        // Update controllers
        StringBuilder controllerStatus = new StringBuilder();
        for (int i = 0; i < controllers.size(); i++) {
            controllerStatus.append("Controller ").append(i).append("\n");
        }
        middlePanel.setText(controllerStatus.toString());

        // Update car wash stations
        StringBuilder stationStatus = new StringBuilder();
        for (Station station : carWash.getStations()) {
            stationStatus.append("Station ").append(station.getId()).append(": ");
            if (station.isEmpty()) {
                stationStatus.append("Empty\n");
            } else {
                stationStatus.append("Car ").append(station.getCarId()).append("\n");
                for (var wash : station.getAvailablePressureWash()) {
                    stationStatus.append("  - ").append(wash.getWashType()).append(": ")
                            .append(wash.isOccupied() ? "Occupied (" + wash.getCurrentTarget().getCarId() + ")" : "Free").append("\n");
                }
            }
        }
        rightPanel.setText(stationStatus.toString());

        // Update queues
        StringBuilder queueStatus = new StringBuilder();
        for (Queue queue : queues) {
            queueStatus.append("Queue ").append(queue.getId()).append(": ");
            if (queue.getOrder().isEmpty()) {
                queueStatus.append("Empty\n");
            } else {
                for (Car car : queue.getOrder()) {
                    queueStatus.append(car.getCarId()).append(" ");
                }
                queueStatus.append("\n");
            }
        }
        bottomPanel.setText(queueStatus.toString());
    }

    public static void main(String[] args) {
        ArrayList<Queue> queues = new ArrayList<>();
        queues.add(new Queue(0));
        queues.add(new Queue(1));

        ArrayList<Car> cars = new ArrayList<>();
        cars.add(new Car('A', queues));
        cars.add(new Car('B', queues));
        cars.add(new Car('C', queues));
        cars.add(new Car('D', queues));
        cars.add(new Car('E', queues));
        cars.add(new Car('F', queues));
        cars.add(new Car('G', queues));
        cars.add(new Car('H', queues));
        cars.add(new Car('I', queues));

        CarWash carWash = new CarWash();
        carWash.buildCarWashSeries(3);

        ArrayList<Controller> controllers = new ArrayList<>();
        controllers.add(new Controller(carWash, queues, cars));

        // Start car threads
        for (Car car : cars) {
            car.start();
        }

        // Start controller threads
        for (Controller controller : controllers) {
            controller.start();
        }

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            CarWashUI ui = new CarWashUI(cars, controllers, queues, carWash);
            ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ui.setVisible(true);
        });
    }
}
