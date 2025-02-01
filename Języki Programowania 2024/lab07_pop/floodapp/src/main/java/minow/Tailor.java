package minow;

import pl.edu.pwr.tkubik.*;

import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Tailor implements iTailor {
    private final Map<String, Remote> allInstances = new HashMap<>();
    private Runnable update;

    @Override
    public boolean register(Remote r, String name) throws RemoteException {
        if (allInstances.containsKey(name)) {
            return false; // Prevent duplicate registration by name
        }
        allInstances.put(name, r);
        update.run();
        return true;
    }

    @Override
    public boolean unregister(Remote r) throws RemoteException {
        String toRemove = null;
        for (Map.Entry<String, Remote> entry : allInstances.entrySet()) {
            if (entry.getValue().equals(r)) {
                toRemove = entry.getKey();
                break;
            }
        }
        if (toRemove != null) {
            allInstances.remove(toRemove);
            update.run();
            return true;
        }
        return false;
    }

    public boolean assignOutputRetensionBasin(String riverSectionName, String basinName) throws RemoteException {
        Remote riverSection = allInstances.get(riverSectionName);
        Remote basin = allInstances.get(basinName);
        if (riverSection instanceof IRiverSection && basin instanceof IRetensionBasin) {
            ((IRiverSection) riverSection).assignRetensionBasin((IRetensionBasin) basin, basinName);
            return true;
        }
        return false;
    }

    public boolean assignOutputRiverSection(String basinName, String riverSectionName) throws RemoteException {
        Remote basin = allInstances.get(basinName);
        Remote riverSection = allInstances.get(riverSectionName);
        if (basin instanceof IRetensionBasin && riverSection instanceof IRiverSection) {
            ((IRetensionBasin) basin).assignRiverSection((IRiverSection) riverSection, riverSectionName);
            return true;
        }
        return false;
    }

    public boolean assignRiverSectionToEnvironment(String environmentName, String riverSectionName) throws RemoteException {
        Remote environment = allInstances.get(environmentName);
        Remote riverSection = allInstances.get(riverSectionName);
        if (environment instanceof IEnvironment && riverSection instanceof IRiverSection) {
            ((IEnvironment) environment).assignRiverSection((IRiverSection) riverSection, riverSectionName);
            return true;
        }
        return false;
    }

    public boolean assignRetensionBasinToControlCenter(String controlCenterName, String basinName) throws RemoteException {
        Remote controlCenter = allInstances.get(controlCenterName);
        Remote basin = allInstances.get(basinName);
        if (controlCenter instanceof IControlCenter && basin instanceof IRetensionBasin) {
            ((IControlCenter) controlCenter).assignRetensionBasin((IRetensionBasin) basin, basinName);
            return true;
        }
        return false;
    }

    private void updateDropdown (JComboBox<String> dropdown){
        dropdown.removeAllItems();
        for (Map.Entry<String, Remote> entry : allInstances.entrySet()) {
            dropdown.addItem(entry.getKey());
        }
    }

    private void initializeUserInterface(Tailor tailor){
        JFrame frame = new JFrame("Tailor UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));



        JLabel assignLabel = new JLabel("Assign Objects:");
        JComboBox<String> sourceBox = new JComboBox<>();
        tailor.updateDropdown(sourceBox);
        JComboBox<String> targetBox = new JComboBox<>();
        tailor.updateDropdown(targetBox);
        String[] assignMethods = {"Assign output Retention Basin for River Section", "Assign output River Section for Retention Basin", "Assign River Section to Environment", "Assign Retention Basin to Control Center"};
        JComboBox<String> methodBox = new JComboBox<>(assignMethods);
        JButton assignButton = new JButton("Assign");

        update = () -> {
            tailor.updateDropdown(sourceBox);
            tailor.updateDropdown(targetBox);
        };

        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);


        assignButton.addActionListener(e -> {
            String source = (String) sourceBox.getSelectedItem();
            String target = (String) targetBox.getSelectedItem();
            String method = (String) methodBox.getSelectedItem();

            if (source != null && target != null && method != null) {
                try {
                    boolean success = false;
                    switch (method) {
                        case "Assign output Retention Basin for River Section":
                            success = tailor.assignOutputRetensionBasin(source, target);
                            break;
                        case "Assign output River Section for Retention Basin":
                            success = tailor.assignOutputRiverSection(source, target);
                            break;
                        case "Assign River Section to Environment":
                            success = tailor.assignRiverSectionToEnvironment(source, target);
                            break;
                        case "Assign Retention Basin to Control Center":
                            success = tailor.assignRetensionBasinToControlCenter(source, target);
                            break;
                    }
                    if (success) {
                        outputArea.append("Assigned: " + source + " to " + target + " using " + method + "\n");
                    } else {
                        outputArea.append("Failed to assign: " + source + " to " + target + " using " + method + "\n");
                    }
                } catch (RemoteException ex) {
                    outputArea.append("Error assigning: " + ex.getMessage() + "\n");
                }

            }
        });
        panel.add(assignLabel);
        panel.add(sourceBox);
        panel.add(targetBox);
        panel.add(methodBox);
        panel.add(assignButton);
        panel.add(new JScrollPane(outputArea));

        frame.add(panel);
        frame.setVisible(true);
    }


    public static void main(String[] args) throws RemoteException {
        try {
            Tailor tailor = new Tailor();

            iTailor iTailorStub = (iTailor) UnicastRemoteObject.exportObject(tailor, 0);

            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(2000);
                registry.rebind("Tailor", iTailorStub);
                System.out.println("Tailor service is bound to an existing registry.");
            } catch (RemoteException e) {
                System.out.println("No registry found. Creating a new one...");
                registry = LocateRegistry.createRegistry(2000);
                registry.rebind("Tailor", iTailorStub);
                System.out.println("New registry created and Tailor service registered.");
            }

            tailor.initializeUserInterface(tailor);

        } catch (Exception e) {
            System.err.println("Exception encountered in Tailor: " + e.getMessage());
        }
    }
}
