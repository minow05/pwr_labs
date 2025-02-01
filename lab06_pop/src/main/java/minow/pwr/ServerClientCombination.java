package minow.pwr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public abstract class ServerClientCombination {
    public void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0, 0, InetAddress.getLocalHost());
        System.out.println(this.getClass().getName() + " Server started: "+ serverSocket.getInetAddress().toString() + " port: " + serverSocket.getLocalPort());
        serverSocket.setReuseAddress(true);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = in.readLine();
            System.out.println("Received: " + request);
            String response = handleRequest(request);
            out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected abstract String handleRequest(String request) throws IOException;
    protected String sendRequest(String request, Socket targetSocket) throws SocketException {
        targetSocket.setReuseAddress(true);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(targetSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(targetSocket.getOutputStream(), true)) {

            out.println(request);
            String response = in.readLine();
            return response;

            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }
}
