package minow.pwr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiData {

    @JsonProperty("id-daty")
    private int idDaty;

    @JsonProperty("wartosc")
    private double wartosc;


    @JsonProperty("id-pozycja-1")
    private Integer idPozycja1;


    @JsonProperty("id-pozycja-2")
    private Integer idPozycja2;



    public int getIdPozycja1() {
        return idPozycja1;
    }

    public void setIdPozycja1(int idPozycja1) {
        this.idPozycja1 = idPozycja1;
    }


    public int getIdPozycja2() {
        return idPozycja2;
    }

    public void setIdPozycja2(int idPozycja2) {
        this.idPozycja2 = idPozycja2;
    }

    public int getIdDaty() {
        return idDaty;
    }

    public void setIdDaty(int idDaty) {
        this.idDaty = idDaty;
    }

    public double getWartosc() {
        return wartosc;
    }

    public void setWartosc(double wartosc) {
        this.wartosc = wartosc;
    }


}