package minow.pwr.RiverSection;

import minow.pwr.ServerClientCombination;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;

public class RiverSection extends ServerClientCombination implements IRiverSection{
    int currentDischarge;
    int currentRainfall;
    Socket outputRetentionBasinSocket = new Socket();
    final int delay; //in hours/1000 (ex. 10 hours == 10000)

    public RiverSection(int delay) {
        this.delay = delay;
    }

    @Override
    protected String handleRequest(String request) throws IOException {
        try {
            if (request.startsWith("srd:")) {
                int discharge = Integer.parseInt(request.substring(4));
                setRealDischarge(discharge);
//                System.out.println("INVOKED");
                return "1";
            } else if (request.startsWith("srf:")) {
                int rain = Integer.parseInt(request.substring(4));
                setRainfall(rain);
                return "1";
            } else if (request.startsWith("arb:")) {
                String[] parts = request.substring(4).split(",");
                int port = Integer.parseInt(parts[0]);
                String host = parts[1];
                assignRetentionBasin(host, port);
                return "1";
            }
        } catch (Exception e) {
            return "Error:" + e.getMessage();
        }
        return "-1";
    }


    @Override
    public void setRealDischarge(int realDischarge) {
        this.currentDischarge = realDischarge;
    }

    @Override
    public void setRainfall(int rainfall) {
        this.currentRainfall = rainfall;
    }

    @Override
    public void assignRetentionBasin(String host, int port) throws IOException {
        this.outputRetentionBasinSocket = new Socket(host, port);
    }

    private void simulate() throws SocketException {
            int currentOutput = currentDischarge + currentRainfall;
            currentDischarge = 0;
            sendRequest("swi:" + currentOutput, outputRetentionBasinSocket);
//            System.out.println("SENDING INFO");
    }
    /*
    requests:
    swi:
    ars:

    */
    public static void main(String[] args) throws IOException {
        String input = JOptionPane.showInputDialog("Enter the delay for River Section (hours/1000):");
        int delay = Integer.parseInt(input);

        RiverSection riverSection = new RiverSection(delay);

        // UI Setup
        JFrame frame = new JFrame("River Section " + InetAddress.getLocalHost());
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JLabel currentRetentionBasin = new JLabel("Current Retention Basin");

        // Progress bar
        JLabel currentDischargeLabel = new JLabel();
        Timer updateTimer = new Timer(1000, e -> {
            currentDischargeLabel.setText(String.valueOf(riverSection.currentRainfall));
            if (riverSection.outputRetentionBasinSocket != null) currentRetentionBasin.setText(riverSection.outputRetentionBasinSocket.toString());
        });
        updateTimer.start();

        JTextField hostField = new JTextField("Enter Host for Retention Basin");
        JTextField portField = new JTextField("Enter Port for Retention Basin");
        JButton assignRiverButton = new JButton("Assign Retention Basin");

        assignRiverButton.addActionListener(e -> {
            try {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                riverSection.assignRetentionBasin(host, port);
                JOptionPane.showMessageDialog(frame, "Retention Basin Assigned Successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error Assigning Retention Basin: " + ex.getMessage());
            }
        });


        panel.add(new JLabel("River Section current rainfall: "));
        panel.add(currentDischargeLabel);
        panel.add(hostField);
        panel.add(portField);
        panel.add(assignRiverButton);
        panel.add(currentRetentionBasin);

        frame.add(panel);
        frame.setVisible(true);

        new Thread(() -> {
            try {
                while (true) {
                    if (riverSection.outputRetentionBasinSocket.isConnected()) {
                        riverSection.simulate();
                        riverSection.assignRetentionBasin(riverSection.outputRetentionBasinSocket.getInetAddress().getHostAddress(), riverSection.outputRetentionBasinSocket.getPort());
                        Thread.sleep(delay + 1000);
                    }

                }
            } catch (Exception e) {
                System.err.println("Error:" + e.getMessage());
            }
        }).start();
        riverSection.startServer();

    }
}
