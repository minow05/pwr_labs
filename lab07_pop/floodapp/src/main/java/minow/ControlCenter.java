package minow;

import pl.edu.pwr.tkubik.IControlCenter;
import pl.edu.pwr.tkubik.IRetensionBasin;
import pl.edu.pwr.tkubik.iTailor;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ControlCenter extends UnicastRemoteObject implements IControlCenter {
    private final String name;
    private ArrayList<IRetensionBasin> retensionBasins = new ArrayList<>();
    private Runnable update;
    static String nameCheck;

    public String getName() {
        return this.name;
    }
    protected ControlCenter(String name) throws RemoteException {
        this.name = name;
        this.retensionBasins = new ArrayList<>();
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) {
//        if (name.equals(nameCheck))
            retensionBasins.add(irb);
        update.run();
        System.out.println(retensionBasins);
    }

    private void updateDropdown(JComboBox<IRetensionBasin> basinDropdown) {
        basinDropdown.removeAllItems();
        if (retensionBasins != null){
            for (IRetensionBasin basin : retensionBasins) {
                if (basin != null) basinDropdown.addItem(basin);
            }
        }
    }

    private void initializeUserInterface(ControlCenter controlCenter, iTailor iTailor) throws RemoteException {
        JFrame frame = new JFrame("Control Center - " + controlCenter.getName());
        frame.setSize (600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3,2));

        JTextField parameterField = new JTextField("Enter additional parameters");

        JComboBox<IRetensionBasin> basinDropdown = new JComboBox<>();
        update = () -> {
            updateDropdown(basinDropdown);
            System.out.println("updating");
        };

        JComboBox<String> requestsDropdown = new JComboBox<>();
        requestsDropdown.addItem("gwd");
        requestsDropdown.addItem("gfp");
        requestsDropdown.addItem("swd:");

        JButton sendRequestButton = new JButton("Send Request");

        JTextArea responseArea = new JTextArea();

        sendRequestButton.addActionListener(e -> {
            String parameters = parameterField.getText();
            IRetensionBasin selectedBasin = (IRetensionBasin) basinDropdown.getSelectedItem();
            String request = (String) requestsDropdown.getSelectedItem();
            if (selectedBasin != null && request != null) {
                try {
                    switch (request) {
                        case "gwd":
                            responseArea.append("Water Discharge: " + selectedBasin.getWaterDischarge() + "\n");
                            System.out.println("getting discharge");
                            break;
                        case "gfp":
                            responseArea.append("Filling percentage: " + selectedBasin.getFillingPercentage() + "\n");
                            break;
                        case "swd:":
                            if (Integer.parseInt(parameters) >= 0) selectedBasin.setWaterDischarge(Integer.parseInt(parameters));
                            responseArea.append("Setting Water Discharge to: " + parameters + "\n");
                            break;
                    }
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        mainPanel.add(requestsDropdown);
        mainPanel.add(parameterField);
        mainPanel.add(basinDropdown);
        mainPanel.add(sendRequestButton);
        mainPanel.add(new JScrollPane(responseArea));

        frame.add(mainPanel);
        frame.setVisible(true);
        while (!frame.isActive()){
            controlCenter.closeConnection(iTailor, controlCenter);
        }
    }

    private void closeConnection(iTailor iTailor, Remote remote) throws RemoteException {
        iTailor.unregister(remote);
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        String inputName = JOptionPane.showInputDialog("Enter the name of the Control Center:");
        ControlCenter controlCenter = new ControlCenter(inputName);
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        nameCheck = "Tailor";
        iTailor iTailor = (pl.edu.pwr.tkubik.iTailor) registry.lookup("Tailor");

        controlCenter.initializeUserInterface(controlCenter, iTailor);

        boolean success;
        do {
            success = iTailor.register(controlCenter, controlCenter.getName());
        } while (!success);

    }
}
