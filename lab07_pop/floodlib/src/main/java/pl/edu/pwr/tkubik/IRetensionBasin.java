package pl.edu.pwr.tkubik;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRetensionBasin extends Remote {
    int getWaterDischarge() throws RemoteException;// zwraca informację o zrzucie wody,
    long getFillingPercentage() throws RemoteException; // zwraca informację o wypełnieniu zbiornika w procentach,
    void setWaterDischarge(int waterDischarge) throws RemoteException; // ustawiania wielkości zrzutu wody,
    void setWaterInflow(int waterInflow, String name) throws RemoteException; // ustawia wielkość napływu wody z odcinka rzecznego o nazwie name

    void assignRiverSection(IRiverSection irs, String name) throws RemoteException; // ustawia namiastę wychodzącego odcinka rzecznego;
}

