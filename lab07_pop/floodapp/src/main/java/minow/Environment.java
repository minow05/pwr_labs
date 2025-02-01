package minow;

import pl.edu.pwr.tkubik.IEnvironment;
import pl.edu.pwr.tkubik.IRiverSection;
import pl.edu.pwr.tkubik.iTailor;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Environment extends UnicastRemoteObject implements IEnvironment {

    private final String name;
    private ArrayList<IRiverSection> riverSections = new ArrayList<>();
    private Runnable update;
    static String nameCheck;

    public String getName() {
        return this.name;
    }
    protected Environment(String name) throws RemoteException {
        this.name = name;
        this.riverSections = new ArrayList<>();
    }

    @Override
    public void assignRiverSection(IRiverSection irs, String name) throws RemoteException {
//        if (name.equals(nameCheck))
            riverSections.add(irs);
        update.run();
    }

    private void updateDropdown(JComboBox<IRiverSection> basinDropdown) {
        basinDropdown.removeAllItems();
        for (IRiverSection riverSection : riverSections) {
            basinDropdown.addItem(riverSection);
        }
    }

    private void initializeUserInterface(Environment environment) {
        JFrame frame = new JFrame("Environment - " + environment.getName());
        frame.setSize (600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3,2));

        JTextField parameterField = new JTextField("Enter additional parameters");

        JComboBox<IRiverSection> riverDropdown = new JComboBox<>();
        update = () -> updateDropdown(riverDropdown);

        JComboBox<String> requestsDropdown = new JComboBox<>();
        requestsDropdown.addItem("srf:");

        JButton sendRequestButton = new JButton("Send Request");

        JTextArea responseArea = new JTextArea();

        sendRequestButton.addActionListener(e -> {
            String parameters = parameterField.getText();
            IRiverSection selectedRiver = (IRiverSection) riverDropdown.getSelectedItem();
            String request = (String) requestsDropdown.getSelectedItem();
            if (selectedRiver != null && request != null) {
                try {
                    if (request.equals("srf:")) {
                        selectedRiver.setRainfall(Integer.parseInt(parameters));
                        responseArea.append("Set Rainfall to: " + parameters + "\n");
                    }
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        mainPanel.add(requestsDropdown);
        mainPanel.add(parameterField);
        mainPanel.add(riverDropdown);
        mainPanel.add(sendRequestButton);
        mainPanel.add(new JScrollPane(responseArea));

        frame.add(mainPanel);
        frame.setVisible(true);
    }



    public static void main(String[] args) throws RemoteException, NotBoundException {
        String inputName = JOptionPane.showInputDialog("Enter the name of the Environment:");
        Environment environment = new Environment(inputName);

        environment.initializeUserInterface(environment);

//        IEnvironment iEnvironment = (IEnvironment) UnicastRemoteObject.exportObject(environment, 0);
        Registry registry = LocateRegistry.getRegistry("localhost",2000);
        iTailor iTailor = (pl.edu.pwr.tkubik.iTailor) registry.lookup("Tailor");
        nameCheck = "Tailor";
        boolean success;
        do {
            success = iTailor.register(environment, environment.getName());
        } while (!success);
    }
}
