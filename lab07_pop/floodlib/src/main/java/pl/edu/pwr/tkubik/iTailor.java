package pl.edu.pwr.tkubik;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iTailor extends Remote {
    boolean register(Remote r, String name) throws RemoteException; // służy do rejestrowania namiastek razem z nazwą
    boolean unregister(Remote r) throws RemoteException; // służy do wyrejestrowywania namiastek
}
