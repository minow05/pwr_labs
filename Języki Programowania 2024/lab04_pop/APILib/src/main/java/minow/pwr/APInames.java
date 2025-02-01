package minow.pwr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiNames {

    @JsonProperty("id-wymiar")
    private int idWymiar;

    @JsonProperty("nazwa-wymiar")
    private String nazwaWymiar;

    @JsonProperty("id-pozycja")
    private int idPozycja;

    @JsonProperty("nazwa-pozycja")
    private String nazwaPozycja;

    public int getIdWymiar() {
        return idWymiar;
    }

    public void setIdWymiar(int idWymiar) {
        this.idWymiar = idWymiar;
    }

    public String getNazwaWymiar() {
        return nazwaWymiar;
    }

    public void setNazwaWymiar(String nazwaWymiar) {
        this.nazwaWymiar = nazwaWymiar;
    }

    public int getIdPozycja() {
        return idPozycja;
    }

    public void setIdPozycja(int idPozycja) {
        this.idPozycja = idPozycja;
    }

    public String getNazwaPozycja() {
        return nazwaPozycja;
    }

    public void setNazwaPozycja(String nazwaPozycja) {
        this.nazwaPozycja = nazwaPozycja;
    }
}