package minow.pwr.Enviroment;

import minow.pwr.ServerClientCombination;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Environment extends ServerClientCombination implements IEnviroment {

    ArrayList<Socket> riverSectionSockets = new ArrayList<>();

    @Override
    public void assignRiverSection(int port, String host) throws IOException {
        riverSectionSockets.add(new Socket(host, port));
    }

    @Override
    protected String handleRequest(String request) throws IOException {
        try {
            if (request.startsWith("ars:")) {
                String[] parts = request.substring(4).split(",");
                int port = Integer.parseInt(parts[0]);
                String host = parts[1];
                assignRiverSection(port, host);
                return "1";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "-1";
    }

    /*
    requests:
    srf:
    */

    public static void main(String[] args) throws IOException {
        Environment environment = new Environment();

        // UI Setup
        JFrame frame = new JFrame("Environment " + InetAddress.getLocalHost());
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        // Information about available requests
        JLabel requestsInfo = new JLabel("Available Requests: srf:<value>, arb:<host,port>");

        // Input for request
        JTextField parameterField = new JTextField("Enter Request (e.g., arb:123.456.7.8,12345)");

        // Dropdown for connected river sections
        JComboBox<String> sectionDropdown = new JComboBox<>();

        // Add river section inputs
        JTextField hostField = new JTextField("Enter Host");
        JTextField portField = new JTextField("Enter Port");
        JButton addSectionButton = new JButton("Add River Section");

        addSectionButton.addActionListener(e -> {
            try {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                environment.assignRiverSection(port, host);
                sectionDropdown.addItem(host + ":" + port);
                JOptionPane.showMessageDialog(frame, "River Section Added Successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error Adding River Section: " + ex.getMessage());
            }
        });

        // Send request button
        JButton sendRequestButton = new JButton("Send Request");
        JTextArea responseArea = new JTextArea();

        sendRequestButton.addActionListener(e -> {
            String requestToSend = parameterField.getText();
            String selectedSection = (String) sectionDropdown.getSelectedItem();

            if (selectedSection != null && requestToSend != null) {
                try {
                    String[] sectionParts = selectedSection.split(":");
                    String host = sectionParts[0];
                    int port = Integer.parseInt(sectionParts[1]);
                    Socket targetSocket = new Socket(host, port);

                    String response = environment.sendRequest(requestToSend, targetSocket);
                    responseArea.append("Request sent: " + requestToSend + " to " + selectedSection + "\n");
                    responseArea.append("Response: " + response + "\n");
                } catch (Exception ex) {
                    responseArea.append("Error sending request: " + ex.getMessage() + "\n");
                }
            } else {
                responseArea.append("Please enter a valid request and select a river section.\n");
            }
        });





        // Add components to panel
        panel.add(requestsInfo);
        panel.add(parameterField);
        panel.add(hostField);
        panel.add(portField);
        panel.add(sectionDropdown);
        panel.add(addSectionButton);
        panel.add(sendRequestButton);
        panel.add(new JScrollPane(responseArea));

        frame.add(panel);
        frame.setVisible(true);

        environment.startServer();
        //generate UI
    }
}
