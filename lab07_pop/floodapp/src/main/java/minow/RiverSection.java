package minow;

import pl.edu.pwr.tkubik.IEnvironment;
import pl.edu.pwr.tkubik.IRetensionBasin;
import pl.edu.pwr.tkubik.IRiverSection;
import pl.edu.pwr.tkubik.iTailor;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.Thread.sleep;

public class RiverSection extends UnicastRemoteObject implements IRiverSection {
    private final String name;
    private final int delay;  // in hours/1000, so it simulates delay time
    int currentDischarge;
    int currentRainfall;
    IRetensionBasin outputRetentionBasin;
    static String nameCheck;

    public String getName() {
        return this.name;
    }

    protected RiverSection(String name, int delay) throws RemoteException {
        this.name = name;
        this.delay = delay;
    }

    @Override
    public void setRealDischarge(int realDischarge) throws RemoteException {
        currentDischarge += realDischarge;
    }

    @Override
    public void setRainfall(int rainfall) throws RemoteException {
        currentRainfall = rainfall;
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) throws RemoteException {
        outputRetentionBasin = irb;
    }

    private void simulate() throws RemoteException {
        // Simulate the discharge and rainfall over time
        while (true) {
            int currentOutput = currentDischarge + currentRainfall;
            currentDischarge = 0;  // reset discharge after sending it
            if (outputRetentionBasin != null) {
                outputRetentionBasin.setWaterInflow(currentOutput, this.name);
                System.out.println("sending water to: " + outputRetentionBasin);
            }

            // Simulate delay based on the 'delay' value (in hours/1000)
            try {
                sleep(1000 + delay);  // Delay based on the input time (converted to ms)
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initializeUserInterface(RiverSection riverSection) {
        JFrame frame = new JFrame("River Section - " + riverSection.getName());
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));

        JLabel currentOutput = new JLabel();
        JLabel currentRainfall = new JLabel(String.valueOf(riverSection.currentRainfall));

        // Timer to update the UI every second
        Timer updateTimer = new Timer(1000, e -> {
            // Update the rainfall display
            currentRainfall.setText("Rainfall: " + riverSection.currentRainfall);
            if (riverSection.outputRetentionBasin != null)
                currentOutput.setText("Output Basin: " + riverSection.outputRetentionBasin.toString());
        });
        updateTimer.start();

        mainPanel.add(currentOutput);
        mainPanel.add(currentRainfall);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        String inputName = JOptionPane.showInputDialog("Enter the name of the River Section: ");
        int inputDelay = Integer.parseInt(JOptionPane.showInputDialog("Enter the delay (hours/1000): "));
        RiverSection riverSection = new RiverSection(inputName, inputDelay);

        riverSection.initializeUserInterface(riverSection);

        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        iTailor iTailor = (pl.edu.pwr.tkubik.iTailor) registry.lookup("Tailor");
        nameCheck = "Tailor";
        iTailor.register(riverSection, riverSection.getName());

        new Thread(() -> {
            try {
                riverSection.simulate();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
