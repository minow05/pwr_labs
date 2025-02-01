package minow.pwr.RetentionBasin;

import minow.pwr.ServerClientCombination;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;


public class RetentionBasin extends ServerClientCombination implements IRetentionBasin{
    int currentDischarge;
    int currentAmount;
    final int maxVolume;
    Socket outputRiverSectionSocket = new Socket();
    int currentInflow;

    public RetentionBasin(int maxVolume){
        this.maxVolume = maxVolume;
    }

    @Override
    public int getWaterDischarge() {
        return currentDischarge;
    }

    @Override
    public long getFillingPercentage() {
        return currentAmount/maxVolume*100L;
    }

    @Override
    public void setWaterDischarge(int waterInflow) {
        currentDischarge = waterInflow;
    }

    @Override
    public void setWaterInflow(int waterInflow, int port) {
        currentInflow += waterInflow;
    }

    @Override
    public void assignRiverSection(String host, int port) throws IOException {
        this.outputRiverSectionSocket = new Socket(host, port);
//        outputRiverSectionSocket.getLocalPort() = outputRiverSectionSocket.getLocalPort();
    }

    private void simulate() throws Exception {
            currentAmount += currentInflow;
            currentInflow = 0;
            if (currentAmount > maxVolume){
                currentDischarge += currentAmount - maxVolume;
            }
//        System.out.println("current Amount: " + currentAmount);
            sendRequest("srd:" + currentDischarge, outputRiverSectionSocket);
//            System.out.println("SENDING INFO");
    }

    @Override
    protected String handleRequest(String request) {
        if (request.startsWith("gwd")) {
//            System.out.println("GOT INVOKED");
            return String.valueOf(getWaterDischarge());
        } else if (request.startsWith("gfp")) {
            return String.valueOf(getFillingPercentage());
        } else if (request.startsWith("swd:")) {
            setWaterDischarge(Integer.parseInt(request.split(":")[1]));
            return "1";
        } else if (request.startsWith("swi:")) {
            String[] parts = request.split(",");
            String[] inflowParts = parts[0].split(":");
            int amount = Integer.parseInt(inflowParts[1].trim());
//            System.out.println(amount);
            setWaterInflow(amount, 0);
//            System.out.println("INVOKED");
            return "1";
        } else if (request.startsWith("ars:")) {
            String[] parts = request.split(",");
            try {
                assignRiverSection((parts[0].split(":")[1]), Integer.parseInt(parts[1]));
                return "1";
            } catch (IOException e) {
                return "Error:" + e.getMessage();
            }
        } else {
            return "-1";
        }
    }
    /*
    requests:
    arb:
    srd:
    */


    public static void main(String[] args) throws IOException, InterruptedException {
        String input = JOptionPane.showInputDialog("Enter the maximum volume of the retention basin:");
        int maxVolume = Integer.parseInt(input);

        RetentionBasin retentionBasin = new RetentionBasin(maxVolume);

        // UI Setup
        JFrame frame = new JFrame("Retention Basin");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        JLabel currentOutRiver = new JLabel("Current Output River Section");

        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, maxVolume);
        progressBar.setStringPainted(true);
        Timer updateTimer = new Timer(1000, e -> {
            progressBar.setValue(retentionBasin.currentAmount);
            if (retentionBasin.outputRiverSectionSocket != null) currentOutRiver.setText(retentionBasin.outputRiverSectionSocket.toString());
        });
        updateTimer.start();

        JTextField hostField = new JTextField("Enter Host for River Section");
        JTextField portField = new JTextField("Enter Port for River Section");
        JButton assignRiverButton = new JButton("Assign River Section");

        assignRiverButton.addActionListener(e -> {
            try {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                retentionBasin.assignRiverSection(host,port);
                JOptionPane.showMessageDialog(frame, "River Section Assigned Successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error Assigning River Section: " + ex.getMessage());
            }
        });

        panel.add(new JLabel("Retention Basin Visualization"));
        panel.add(progressBar);
        panel.add(hostField);
        panel.add(portField);
        panel.add(assignRiverButton);
        panel.add(currentOutRiver);

        frame.add(panel);
        frame.setVisible(true);

        //server
        new Thread(() -> {
            try {
                while (true){
                    if (retentionBasin.outputRiverSectionSocket.isConnected()){
                            retentionBasin.simulate();
                            retentionBasin.assignRiverSection(retentionBasin.outputRiverSectionSocket.getInetAddress().getHostAddress(), retentionBasin.outputRiverSectionSocket.getPort());
                            Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error:" + e.getMessage());
            }
        }).start();
        retentionBasin.startServer();
    }
}
