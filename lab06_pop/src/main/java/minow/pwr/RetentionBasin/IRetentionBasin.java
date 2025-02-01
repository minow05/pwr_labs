package minow.pwr.RetentionBasin;

import java.io.IOException;

public interface IRetentionBasin {
    int getWaterDischarge();
    long getFillingPercentage();
    void setWaterDischarge(int waterInflow);
    void setWaterInflow(int waterInflow, int port);
    void assignRiverSection(String host, int port) throws IOException;
}
