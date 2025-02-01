package minow.pwr.ControlCenter;

import java.io.IOException;

public interface IControlCenter {
    void assignRetentionBasin(int port, String host) throws IOException;
}
