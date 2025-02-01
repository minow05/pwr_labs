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

public class RetensionBasin extends UnicastRemoteObject implements IRetensionBasin {

    private final String name;
    private IRiverSection outputRiverSection;
    private final int maxVolume;
    int currentInflow;
    int currentAmount;
    int currentDischarge;

    static String nameCheck;

    public String getName() {
        return this.name;
    }
    protected RetensionBasin(String name, int volume) throws RemoteException {
        this.name = name;
        this.maxVolume = volume;
    }

    @Override
    public int getWaterDischarge() throws RemoteException {
        return currentDischarge;
    }

    @Override
    public long getFillingPercentage() throws RemoteException {
        double percentage = ((double) currentAmount / maxVolume) * 100;
        System.out.println(percentage);
        return Math.round(percentage);
    }

    @Override
    public void setWaterDischarge(int waterDischarge) throws RemoteException {
        currentDischarge = waterDischarge;
    }

    @Override
    public void setWaterInflow(int waterInflow, String name) throws RemoteException {
        currentInflow += waterInflow;
        System.out.println("current inflow: " + currentInflow);
    }

    @Override
    public void assignRiverSection(IRiverSection irs, String name) throws RemoteException {
//        if (name.equals(nameCheck))
            outputRiverSection = irs;
    }

    private void initializeUserInterface(RetensionBasin retensionBasin) {
        JFrame frame = new JFrame("Retention Basin - " + retensionBasin.getName());
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));

        JLabel currentOutput = new JLabel();

        JProgressBar progressBar = new JProgressBar(0, maxVolume);

        Timer updateTimer = new Timer(1000, e -> {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(retensionBasin.currentAmount);
                if (retensionBasin.outputRiverSection != null)
                    currentOutput.setText(retensionBasin.outputRiverSection.toString());
            });
        });
        updateTimer.start();

        mainPanel.add(currentOutput);
        mainPanel.add(progressBar);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void simulate() throws RemoteException {
        while (true) {
            int newDischarge = currentDischarge;
            currentAmount += currentInflow;
            if (currentAmount > maxVolume) {
                newDischarge += currentAmount - maxVolume;
                currentAmount = maxVolume;
            }
            currentInflow = 0;
            currentAmount -= newDischarge;
            if (outputRiverSection != null) {
                outputRiverSection.setRealDischarge(newDischarge);
                System.out.println("sending water to: " + outputRiverSection);
            }
            System.out.println("current amount: " + currentAmount);
            System.out.println("current filling percentage: " + String.valueOf(getFillingPercentage()));
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public static void main(String[] args) throws RemoteException, NotBoundException {
        String inputName = JOptionPane.showInputDialog("Enter the name of the Retention Basin: ");
        int inputVolume = Integer.parseInt(JOptionPane.showInputDialog("Enter the max volume of the Retention Basin: "));
        RetensionBasin retensionBasin = new RetensionBasin(inputName, inputVolume);
        // add shotInputDialog for Tailor name and tailor host + port, modify nameCheck to given name
        retensionBasin.initializeUserInterface(retensionBasin);

        Registry registry = LocateRegistry.getRegistry("localhost",2000);
        iTailor iTailor = (pl.edu.pwr.tkubik.iTailor) registry.lookup("Tailor");
        nameCheck = "Tailor";
        iTailor.register(retensionBasin, retensionBasin.getName());

        new Thread(() -> {
            try {
                retensionBasin.simulate();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
