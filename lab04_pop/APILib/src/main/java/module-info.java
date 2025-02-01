module APILib {
    opens minow.pwr to com.fasterxml.jackson.databind;
    exports minow.pwr to com.fasterxml.jackson.databind, App;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
}