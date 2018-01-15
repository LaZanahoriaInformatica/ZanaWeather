package com.kalderius.agus.zanaweather;

/**
 * Created by Agus on 15/01/2018.
 */

public class Tiempo {
    private String temperatura;
    private String viento;
    private String cielo;

    public Tiempo(String temperatura, String viento, String cielo) {
        this.temperatura = temperatura;
        this.viento = viento;
        this.cielo = cielo;
    }

    public Tiempo() {
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getViento() {
        return viento;
    }

    public void setViento(String viento) {
        this.viento = viento;
    }

    public String getCielo() {
        return cielo;
    }

    public void setCielo(String cielo) {
        this.cielo = cielo;
    }
}
