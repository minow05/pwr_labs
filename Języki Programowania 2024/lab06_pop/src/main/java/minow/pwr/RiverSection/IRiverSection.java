package minow.pwr.RiverSection;

import java.io.IOException;

public interface IRiverSection {
    void setRealDischarge(int realDischarge);
    void setRainfall (int rainfall);
    void assignRetentionBasin(String host, int port) throws IOException;
}
