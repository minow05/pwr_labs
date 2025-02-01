package minow.pwr;

public class CenyTowarow {
        private int year; // id-daty
        private double value; // wartosc
        private String position1Name; // nazwa-pozycja for id-pozycja-1
        private String position2Name; // nazwa-pozycja for id-pozycja-2


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


    public String getPosition1Name() {
        return position1Name;
    }

    public void setPosition1Name(String position1Name) {
        this.position1Name = position1Name;
    }


    public String getPosition2Name() {
        return position2Name;
    }

    public void setPosition2Name(String position2Name) {
        this.position2Name = position2Name;
    }
    @Override
    public String toString() {
        return String.format(
                "Year: '%s', Value: %.2f , Position1: '%s', Position2: '%s'",
                year, value, position1Name, position2Name
        );
    }
//    idVariable; // id-zmienna
//    private int idSection; // id-przekroj
//    private int year; // id-daty
//    private double value; // wartosc
//    private String dimension1Name; // nazwa-wymiar for id-wymiar-1
//    private String position1Name; // nazwa-pozycja for id-pozycja-1
//    private String dimension2Name; // nazwa-wymiar for id-wymiar-2
//    private String position2Name;
}
