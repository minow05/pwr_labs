package pl.edu.pwr.tkubik;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IControlCenter extends Remote {
    void assignRetensionBasin(IRetensionBasin irb, String name) throws RemoteException; // ustawia namiastkę zbiornika retencyjnego
}
