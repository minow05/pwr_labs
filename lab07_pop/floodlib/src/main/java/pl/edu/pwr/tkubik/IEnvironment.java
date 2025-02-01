package pl.edu.pwr.tkubik;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IEnvironment extends Remote {
    void assignRiverSection(IRiverSection irs, String name) throws RemoteException;
}
