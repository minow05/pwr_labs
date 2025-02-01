package minow.pwr.ControlCenter;

import minow.pwr.RetentionBasin.RetentionBasin;
import minow.pwr.ServerClientCombination;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ControlCenter extends ServerClientCombination implements IControlCenter{
//    Socket currentRetentionBasinSocket = new Socket();
    ArrayList<Socket> retentionBasinSockets = new ArrayList<>();

    @Override
    public void assignRetentionBasin(int port, String host) throws IOException {
//        currentRetentionBasinSocket.bind(new InetSocketAddress(host, port));
        retentionBasinSockets.add(new Socket(host, port));
    }

    protected String handleRequest(String request) {
        try {
            if (request.startsWith("arb:")) {
                String[] parts = request.substring(4).split(",");
                int port = Integer.parseInt(parts[0]);
                String host = parts[1];
                assignRetentionBasin(port, host);
                return "1";
            } else {
                System.out.println("GOT INVOKED");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
        return "-1";
    }

    /*
    requests:
    gwd
    swd:
    gfp
    */


    public static void main(String[] args) throws IOException {
        ControlCenter controlCenter = new ControlCenter();
        // UI Setup
        JFrame frame = new JFrame("Control Center " + InetAddress.getLocalHost());
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        // Information about available requests
        JLabel requestsInfo = new JLabel("Available Requests: gwd, swd:<value>, gfp, ars:<host>,<port>");

        // Input for request
        JTextField parameterField = new JTextField("Enter Request (e.g., swd:50)");

        // Dropdown for connected retention basins
        JComboBox<String> basinDropdown = new JComboBox<>();

        // Add retention basin inputs
        JTextField hostField = new JTextField("Enter Host");
        JTextField portField = new JTextField("Enter Port");
        JButton addBasinButton = new JButton("Add Retention Basin");

        addBasinButton.addActionListener(e -> {
            try {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                controlCenter.assignRetentionBasin(port, host);
                basinDropdown.addItem(host + ":" + port);
                JOptionPane.showMessageDialog(frame, "Retention Basin Added Successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error Adding Retention Basin: " + ex.getMessage());
            }
        });

        // Send request button
        JButton sendRequestButton = new JButton("Send Request");
        JTextArea responseArea = new JTextArea();

        sendRequestButton.addActionListener(e -> {
            String requestToSend = parameterField.getText();
            String selectedBasin = (String) basinDropdown.getSelectedItem();

            if (selectedBasin != null && requestToSend != null) {
                try {
                    String[] basinParts = selectedBasin.split(":");
                    String host = basinParts[0];
                    int port = Integer.parseInt(basinParts[1]);
                    Socket targetSocket = new Socket(host, port);

                    String response = controlCenter.sendRequest(requestToSend, targetSocket);
                    responseArea.append("Request sent: " + requestToSend + " to " + selectedBasin + "\n");
                    responseArea.append("Response: " + response + "\n");
                } catch (Exception ex) {
                    responseArea.append("Error sending request: " + ex.getMessage() + "\n");
                }
            } else {
                responseArea.append("Please enter a valid request and select a retention basin.\n");
            }
        });

        // Add components to panel
        panel.add(requestsInfo);
        panel.add(parameterField);
        panel.add(hostField);
        panel.add(portField);
        panel.add(basinDropdown);
        panel.add(addBasinButton);
        panel.add(sendRequestButton);
        panel.add(new JScrollPane(responseArea));

        frame.add(panel);
        frame.setVisible(true);

        controlCenter.startServer();
    }
}
