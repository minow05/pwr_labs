package pl.edu.pwr.tkubik;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRiverSection extends Remote {
    void setRealDischarge(int realDischarge) throws RemoteException; // ustawia rzeczywistą wielkość zrzutu wody ze zbiornika znajdującego się na początku odcinka rzecznego,
    void setRainfall(int rainfall) throws RemoteException; // ustawia wielkość opadów atmosferycznych,

    void assignRetensionBasin(IRetensionBasin irb, String name) throws RemoteException; // ustawia namiastkę zbiornika retencyjnego na wyjściu;
}

